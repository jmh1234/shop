package com.example.shop.integration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.shop.ShopApplication;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
public class GoodsIntegrationTest {

    @Resource
    private Environment environment;

    @Test
    @SneakyThrows
    public void goodsOperateTest() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://localhost:" + environment.getProperty("local.server.port");
        String content = "{\n" +
                "    \"name\": \"肥皂\",\n" +
                "    \"description\": \"纯天然无污染肥皂\",\n" +
                "    \"details\": \"这是一块好肥皂\",\n" +
                "    \"imgUrl\": \"https://img.url\",\n" +
                "    \"price\": 500,\n" +
                "    \"stock\": 10,\n" +
                "    \"shopId\": 1234567\n" +
                "}";
        // 1. 没有登陆时返回401
        HttpResponse insertResponseWithoutLogin = getResponseByPost(url + "/api/v1/goods", JSONObject.parseObject(content), httpClient);
        Assertions.assertEquals(401, insertResponseWithoutLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("Unauthorized", getResponseObject(insertResponseWithoutLogin).getString("message"));

        // 2. 发送验证码
        JSONObject object = new JSONObject();
        object.put("tel", "11111111111");
        HttpResponse codeResponse = getResponseByPost(url + "/api/code", object, httpClient);
        Assertions.assertEquals(200, codeResponse.getStatusLine().getStatusCode());
        String code = getResponseObject(codeResponse).get("code").toString();
        Assertions.assertNotNull(code);

        // 3. 带着验证码进行登陆得到cookie
        object.put("code", code);
        HttpResponse loginResponse = getResponseByPost(url + "/api/login", object, httpClient);
        Assertions.assertEquals(200, loginResponse.getStatusLine().getStatusCode());
        Assertions.assertEquals("11111111111", getResponseObject(loginResponse).get("tel"));

        // 4. 登陆之后插入数据后返回插入的那条数据
        HttpResponse insertResponseWithLogin = getResponseByPost(url + "/api/v1/goods", JSONObject.parseObject(content), httpClient);
        Assertions.assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("1234567", getResponseObject(insertResponseWithLogin).getString("shopId"));

        // 5. 通过ID删除数据
        HttpResponse deleteResponse = getResponseByDelete(url + "/api/v1/goods/2", httpClient);
        Assertions.assertEquals(200, deleteResponse.getStatusLine().getStatusCode());
        Assertions.assertEquals("2", getResponseObject(deleteResponse).getString("id"));

        // 6. 更新数据
        HttpResponse updateResponse = getResponseByPatch(url + "/api/v1/goods/3", JSONObject.parseObject(content), httpClient);
        JSONObject updateObject = getResponseObject(updateResponse);
        Assertions.assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("3", updateObject.getString("id"));
        Assertions.assertEquals("1234567", updateObject.getString("shopId"));

        // 7. 获取指定id的商品
        HttpResponse goodsInfoByIdResponse = getResponseByGet(url + "/api/v1/goods/3", null, httpClient);
        JSONObject goodsInfoByIdObject = getResponseObject(goodsInfoByIdResponse);
        Assertions.assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("3", goodsInfoByIdObject.getString("id"));
        Assertions.assertEquals("1234567", goodsInfoByIdObject.getString("shopId"));

        // 8. 通过ShopId获取商品
        HttpResponse goodsInfoWithShopIdResponse = getResponseByGet(url + "/api/v1/goods?pageNum=1&pageSize=10&shopId=1234567", null, httpClient);
        JSONObject goodsInfoWithShopIdObject = getResponseObject(goodsInfoWithShopIdResponse);
        Assertions.assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("获取成功", goodsInfoWithShopIdObject.getString("msg"));
        Assertions.assertEquals("1", goodsInfoWithShopIdObject.getString("pageNum"));
        Assertions.assertEquals("10", goodsInfoWithShopIdObject.getString("pageSize"));

        // 9. 获取所有商品
        HttpResponse allGoodsInfoResponse = getResponseByGet(url + "/api/v1/goods?pageNum=1&pageSize=10", null, httpClient);
        JSONObject allGoodsInfoObject = getResponseObject(allGoodsInfoResponse);
        Assertions.assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("获取成功", allGoodsInfoObject.getString("msg"));
        Assertions.assertEquals("1", allGoodsInfoObject.getString("pageNum"));
        Assertions.assertEquals("10", allGoodsInfoObject.getString("pageSize"));
    }

    @SneakyThrows
    private static JSONObject getResponseObject(HttpResponse response) {
        return JSONObject.parseObject(new String(EntityUtils.toByteArray(response.getEntity())));
    }

    @SneakyThrows
    private static HttpResponse getResponseByPatch(String url, JSONObject object, CloseableHttpClient httpClient) {
        HttpPatch httpPatch = new HttpPatch(url);
        httpPatch.addHeader(HTTP.CONTENT_TYPE, "application/json");
        httpPatch.setEntity(getStringEntity(object));
        return httpClient.execute(httpPatch);
    }

    @SneakyThrows
    private static HttpResponse getResponseByPost(String url, JSONObject object, CloseableHttpClient httpClient) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        httpPost.setEntity(getStringEntity(object));
        return httpClient.execute(httpPost);
    }

    @SneakyThrows
    private static HttpResponse getResponseByGet(String url, String sessionId, CloseableHttpClient httpClient) {
        HttpGet httpGet = new HttpGet(url);
        if (sessionId != null) {
            httpGet.setHeader("Cookie", sessionId);
        }
        return httpClient.execute(httpGet);
    }

    @SneakyThrows
    private static HttpResponse getResponseByDelete(String url, CloseableHttpClient httpClient) {
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.addHeader(HTTP.CONTENT_TYPE, "application/json");
        return httpClient.execute(httpDelete);
    }

    @SneakyThrows
    private static StringEntity getStringEntity(JSONObject object) {
        StringEntity entity = new StringEntity(JSON.toJSONString(object), StandardCharsets.UTF_8);
        entity.setContentType("text/json");
        entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        return entity;
    }
}

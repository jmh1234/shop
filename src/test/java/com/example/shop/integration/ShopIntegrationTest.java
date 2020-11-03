package com.example.shop.integration;

import com.alibaba.fastjson.JSONObject;
import com.example.shop.ShopApplication;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class ShopIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void shopTest() {
        JSONObject content = JSONObject.parseObject("{\n" +
                "    \"name\": \"我的店铺\",\n" +
                "    \"description\": \"我的苹果专卖店\",\n" +
                "    \"imgUrl\": \"https://img.url\",\n" +
                "}");
        String sessionId = loginAndGetCookie();
        HttpResponse createResponse = getResponseByPost(getUrl("/api/v1/shop"), content, sessionId, httpClient);
        Assertions.assertEquals(200, createResponse.getStatusLine().getStatusCode());
        JSONObject createObject = getResponseObject(createResponse).getJSONObject("data");
        Assertions.assertEquals("我的店铺", createObject.getString("name"));
        Assertions.assertEquals("我的苹果专卖店", createObject.getString("description"));

        HttpResponse deleteResponse = getResponseByDelete(getUrl("/api/v1/shop/3"), sessionId, httpClient);
        Assertions.assertEquals(200, deleteResponse.getStatusLine().getStatusCode());
        JSONObject deleteObject = getResponseObject(deleteResponse).getJSONObject("data");
        Assertions.assertEquals("3", deleteObject.getString("id"));
        Assertions.assertEquals("我的店铺", deleteObject.getString("name"));
        Assertions.assertEquals("我的苹果专卖店", deleteObject.getString("description"));

        HttpResponse updateResponse = getResponseByPatch(getUrl("/api/v1/shop/1"), content, sessionId, httpClient);
        Assertions.assertEquals(200, updateResponse.getStatusLine().getStatusCode());
        JSONObject updateObject = getResponseObject(updateResponse).getJSONObject("data");
        Assertions.assertEquals("我的店铺", updateObject.getString("name"));
        Assertions.assertEquals("我的苹果专卖店", updateObject.getString("description"));

        HttpResponse getShopByIdResponse = getResponseByGet(getUrl("/api/v1/shop/1"), sessionId, httpClient);
        Assertions.assertEquals(200, getShopByIdResponse.getStatusLine().getStatusCode());
        JSONObject getShopByIdObject = getResponseObject(getShopByIdResponse).getJSONObject("data");
        Assertions.assertEquals("我的店铺", getShopByIdObject.getString("name"));
        Assertions.assertEquals("我的苹果专卖店", getShopByIdObject.getString("description"));

        HttpResponse getAllShopInfoInfoResponse = getResponseByGet(getUrl("/api/v1/shop?pageNum=1&pageSize=10"), sessionId, httpClient);
        JSONObject getAllShopInfoInfoObject = getResponseObject(getAllShopInfoInfoResponse);
        Assertions.assertEquals(200, getAllShopInfoInfoResponse.getStatusLine().getStatusCode());
        Assertions.assertEquals("获取成功", getAllShopInfoInfoObject.getString("msg"));
        Assertions.assertEquals("1", getAllShopInfoInfoObject.getString("pageNum"));
        Assertions.assertEquals("10", getAllShopInfoInfoObject.getString("pageSize"));
        assertEquals(2, getAllShopInfoInfoObject.getJSONArray("data").size());

    }
}

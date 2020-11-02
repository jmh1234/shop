package com.example.shop.integration;

import com.alibaba.fastjson.JSONObject;
import com.example.shop.ShopApplication;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class GoodsIntegrationTest extends AbstractIntegrationTest {

    @Test
    @SneakyThrows
    public void goodsOperateTest() {
        JSONObject content = JSONObject.parseObject("{\n" +
                "    \"name\": \"肥皂\",\n" +
                "    \"description\": \"纯天然无污染肥皂\",\n" +
                "    \"details\": \"这是一块好肥皂\",\n" +
                "    \"imgUrl\": \"https://img.url\",\n" +
                "    \"price\": 500,\n" +
                "    \"stock\": 10,\n" +
                "    \"shopId\": 1234567\n" +
                "}");
        String sessionId = loginAndGetCookie();

        // 1. 登陆之后插入数据后返回插入的那条数据
        HttpResponse insertResponseWithLogin = getResponseByPost(getUrl("/api/v1/goods"), content, sessionId, httpClient);
        Assertions.assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("1234567", getResponseObject(insertResponseWithLogin).getString("shopId"));

        // 2. 更新数据
        HttpResponse updateResponse = getResponseByPatch(getUrl("/api/v1/goods/3"), content, sessionId, httpClient);
        JSONObject updateObject = getResponseObject(updateResponse);
        Assertions.assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("3", updateObject.getString("id"));
        Assertions.assertEquals("1234567", updateObject.getString("shopId"));

        // 3. 获取指定id的商品
        HttpResponse goodsInfoByIdResponse = getResponseByGet(getUrl("/api/v1/goods/3"), sessionId, httpClient);
        JSONObject goodsInfoByIdObject = getResponseObject(goodsInfoByIdResponse);
        Assertions.assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("3", goodsInfoByIdObject.getString("id"));
        Assertions.assertEquals("1234567", goodsInfoByIdObject.getString("shopId"));

        // 4. 通过ShopId获取商品
        HttpResponse goodsInfoWithShopIdResponse = getResponseByGet(getUrl("/api/v1/goods?pageNum=1&pageSize=10&shopId=12345"), sessionId, httpClient);
        JSONObject goodsInfoWithShopIdObject = getResponseObject(goodsInfoWithShopIdResponse);
        Assertions.assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("获取成功", goodsInfoWithShopIdObject.getString("msg"));
        Assertions.assertEquals("1", goodsInfoWithShopIdObject.getString("pageNum"));
        Assertions.assertEquals("10", goodsInfoWithShopIdObject.getString("pageSize"));

        // 5. 获取所有商品
        HttpResponse allGoodsInfoResponse = getResponseByGet(getUrl("/api/v1/goods?pageNum=1&pageSize=10"), sessionId, httpClient);
        JSONObject allGoodsInfoObject = getResponseObject(allGoodsInfoResponse);
        Assertions.assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        Assertions.assertEquals("获取成功", allGoodsInfoObject.getString("msg"));
        Assertions.assertEquals("1", allGoodsInfoObject.getString("pageNum"));
        Assertions.assertEquals("10", allGoodsInfoObject.getString("pageSize"));

        // 6. 通过ID删除数据
        HttpResponse deleteResponse = getResponseByDelete(getUrl("/api/v1/goods/2"), sessionId, httpClient);
        Assertions.assertEquals(200, deleteResponse.getStatusLine().getStatusCode());
        Assertions.assertEquals("2", getResponseObject(deleteResponse).getString("id"));
    }
}

package com.example.shop.integration;

import com.alibaba.fastjson.JSONObject;
import com.example.shop.ShopApplication;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                "    \"shopId\": 2\n" +
                "}");
        String sessionId = loginAndGetCookie();

        // 1. 登陆之后插入数据后返回插入的那条数据
        HttpResponse insertResponseWithLogin = getResponseByPost(getUrl("/api/v1/goods"), content, sessionId, httpClient);
        JSONObject insertWithLoginObject = getResponseObject(insertResponseWithLogin).getJSONObject("data");
        assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        assertEquals("肥皂", insertWithLoginObject.getString("name"));
        assertEquals("纯天然无污染肥皂", insertWithLoginObject.getString("description"));
        assertEquals("这是一块好肥皂", insertWithLoginObject.getString("details"));
        assertEquals("500", insertWithLoginObject.getString("price"));
        assertEquals("10", insertWithLoginObject.getString("stock"));
        assertEquals("2", insertWithLoginObject.getString("shopId"));

        // 2. 更新数据
        HttpResponse updateResponse = getResponseByPatch(getUrl("/api/v1/goods/3"), content, sessionId, httpClient);
        JSONObject updateObject = getResponseObject(updateResponse).getJSONObject("data");
        assertEquals(200, updateResponse.getStatusLine().getStatusCode());
        assertEquals("肥皂", updateObject.getString("name"));
        assertEquals("纯天然无污染肥皂", updateObject.getString("description"));
        assertEquals("这是一块好肥皂", updateObject.getString("details"));
        assertEquals("500", updateObject.getString("price"));
        assertEquals("10", updateObject.getString("stock"));
        assertEquals("2", updateObject.getString("shopId"));

        // 3. 获取指定id的商品
        HttpResponse goodsInfoByIdResponse = getResponseByGet(getUrl("/api/v1/goods/3"), sessionId, httpClient);
        assertEquals(200, goodsInfoByIdResponse.getStatusLine().getStatusCode());
        JSONObject goodsInfoByIdObject = getResponseObject(goodsInfoByIdResponse).getJSONObject("data");
        assertEquals("3", goodsInfoByIdObject.getString("id"));
        assertEquals("肥皂", goodsInfoByIdObject.getString("name"));
        assertEquals("纯天然无污染肥皂", goodsInfoByIdObject.getString("description"));
        assertEquals("这是一块好肥皂", goodsInfoByIdObject.getString("details"));
        assertEquals("500", goodsInfoByIdObject.getString("price"));
        assertEquals("10", goodsInfoByIdObject.getString("stock"));
        assertEquals("2", goodsInfoByIdObject.getString("shopId"));

        // 4. 通过ShopId获取商品
        HttpResponse goodsInfoWithShopIdResponse = getResponseByGet(getUrl("/api/v1/goods?pageNum=1&pageSize=10&shopId=2"), sessionId, httpClient);
        JSONObject goodsInfoWithShopIdObject = getResponseObject(goodsInfoWithShopIdResponse);
        assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        assertEquals("获取成功", goodsInfoWithShopIdObject.getString("msg"));
        assertEquals("1", goodsInfoWithShopIdObject.getString("pageNum"));
        assertEquals("10", goodsInfoWithShopIdObject.getString("pageSize"));
        assertEquals(4, goodsInfoWithShopIdObject.getJSONArray("data").size());

        // 5. 获取所有商品
        HttpResponse allGoodsInfoResponse = getResponseByGet(getUrl("/api/v1/goods?pageNum=1&pageSize=10"), sessionId, httpClient);
        JSONObject allGoodsInfoObject = getResponseObject(allGoodsInfoResponse);
        assertEquals(200, insertResponseWithLogin.getStatusLine().getStatusCode());
        assertEquals("获取成功", allGoodsInfoObject.getString("msg"));
        assertEquals("1", allGoodsInfoObject.getString("pageNum"));
        assertEquals("10", allGoodsInfoObject.getString("pageSize"));
        assertEquals(6, allGoodsInfoObject.getJSONArray("data").size());

        // 6. 通过ID删除数据
        HttpResponse deleteResponse = getResponseByDelete(getUrl("/api/v1/goods/3"), sessionId, httpClient);
        assertEquals(200, deleteResponse.getStatusLine().getStatusCode());
        JSONObject deleteObject = getResponseObject(deleteResponse).getJSONObject("data");
        assertEquals("3", deleteObject.getString("id"));
        assertEquals("肥皂", deleteObject.getString("name"));
        assertEquals("纯天然无污染肥皂", deleteObject.getString("description"));
        assertEquals("这是一块好肥皂", deleteObject.getString("details"));
        assertEquals("500", deleteObject.getString("price"));
        assertEquals("10", deleteObject.getString("stock"));
        assertEquals("2", deleteObject.getString("shopId"));
    }
}

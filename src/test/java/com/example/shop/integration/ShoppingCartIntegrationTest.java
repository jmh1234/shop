package com.example.shop.integration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.shop.ShopApplication;
import com.example.shop.entity.AddToShoppingCartItem;
import com.example.shop.entity.AddToShoppingCartRequest;
import com.example.shop.entity.DataStatus;
import com.google.common.collect.Sets;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})

public class ShoppingCartIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void canQueryShoppingCartData() {
        String sessionId = loginAndGetCookie();
        HttpResponse responseByGet = getResponseByGet(getUrl("/api/v1/shoppingCart?pageNum=1&pageSize=10"), sessionId, httpClient);
        JSONObject responseObject = getResponseObject(responseByGet);
        assertEquals(1, responseObject.getInteger("pageNum"));
        assertEquals(10, responseObject.getInteger("pageSize"));
        assertEquals(1, responseObject.getInteger("totalPage"));
        assertEquals(2, responseObject.getJSONArray("data").size());
        JSONObject data = (JSONObject) responseObject.getJSONArray("data").get(1);

        assertEquals(2, data.getJSONObject("shop").getInteger("id"));
        assertEquals(Arrays.asList(4L, 5L),
                data.getJSONArray("goods").stream()
                        .map(good -> ((JSONObject) good).getLong("id")).collect(toList()));
        assertEquals(Arrays.asList(100L, 200L),
                data.getJSONArray("goods").stream()
                        .map(good -> ((JSONObject) good).getLong("price")).collect(toList()));
        assertEquals(Arrays.asList(200L, 300L),
                data.getJSONArray("goods").stream()
                        .map(good -> ((JSONObject) good).getLong("number")).collect(toList()));
    }

    @Test
    public void canAddShoppingCartData() {
        String sessionId = loginAndGetCookie();
        AddToShoppingCartRequest request = new AddToShoppingCartRequest();
        AddToShoppingCartItem item = new AddToShoppingCartItem();
        item.setId(2L);
        item.setNumber(2);

        request.setGoods(Collections.singletonList(item));
        HttpResponse responseByPost = getResponseByPost(getUrl("/api/v1/shoppingCart"), request, sessionId, httpClient);
        JSONObject responseObject = getResponseObject(responseByPost);
        JSONObject shop = responseObject.getJSONObject("shop");
        JSONArray goods = responseObject.getJSONArray("goods");

        assertEquals(1L, shop.getLong("id"));
        assertEquals(Arrays.asList(1L, 2L),
                goods.stream()
                        .map(good -> ((JSONObject) good).getLong("id"))
                        .collect(toList()));
        assertEquals(Sets.newHashSet(2, 100),
                goods.stream()
                        .map(good -> ((JSONObject) good).getInteger("number"))
                        .collect(toSet()));
        Assertions.assertTrue(goods.stream().allMatch(
                good -> ((JSONObject) good).getInteger("shopId") == 1L));

        // 手动注销一次，下面测试会用到该方法
        HttpResponse logoutResponse = getResponseByGet(getUrl("/api/logout"), sessionId, httpClient);
        Assertions.assertEquals(200, logoutResponse.getStatusLine().getStatusCode());
    }

    // 重复将同一个商品加入购物车，后面的商品会覆盖前面的
    @Test
    public void addingSameGoodsToShoppingCartOverwritesOldGoods() {
        // 第一次添加id为2的商品，2个
        canAddShoppingCartData();
        String sessionId = loginAndGetCookie();

        // 第二次添加id为2的商品，1个
        AddToShoppingCartRequest request = new AddToShoppingCartRequest();
        AddToShoppingCartItem item = new AddToShoppingCartItem();
        item.setId(2L);
        item.setNumber(1);

        request.setGoods(Collections.singletonList(item));

        getResponseByPost(getUrl("/api/v1/shoppingCart"), request, sessionId, httpClient);
        HttpResponse responseByGet = getResponseByGet(getUrl("/api/v1/shoppingCart?pageNum=1&pageSize=100"), sessionId, httpClient);
        JSONObject responseObject = getResponseObject(responseByGet);
        JSONArray data = responseObject.getJSONArray("data");

        JSONObject shop1Data = (JSONObject) data.stream()
                .filter(dataObject -> ((JSONObject) dataObject).getJSONObject("shop").getInteger("id") == 1)
                .findFirst().get();
        assertEquals(Arrays.asList(1L, 2L),
                shop1Data.getJSONArray("goods").stream().map(goods -> ((JSONObject) goods).getLong("id")).collect(toList()));
        assertEquals(Sets.newHashSet(1, 100),
                shop1Data.getJSONArray("goods").stream().map(goods -> ((JSONObject) goods).getInteger("number")).collect(toSet()));
    }

    @Test
    public void canDeleteAllShoppingCartData() {
        String sessionId = loginAndGetCookie();
        HttpResponse delete = getResponseByDelete(getUrl("/api/v1/deleteAllShoppingCart"), sessionId, httpClient);
        Assertions.assertEquals(200, delete.getStatusLine().getStatusCode());
        HttpResponse responseByGet = getResponseByGet(getUrl("/api/v1/shoppingCart?pageNum=1&pageSize=10"), sessionId, httpClient);
        Assertions.assertEquals(0, getResponseObject(responseByGet).getJSONArray("data").size());
    }

    @Test
    public void canDeleteShoppingCartData() {
        String sessionId = loginAndGetCookie();
        HttpResponse delete = getResponseByDelete(getUrl("/api/v1/shoppingCart/5"), sessionId, httpClient);
        JSONObject responseObject = getResponseObject(delete);
        JSONObject shop = responseObject.getJSONObject("shop");
        JSONArray goods = responseObject.getJSONArray("goods");
        Assertions.assertEquals(2L, shop.getLong("id"));
        Assertions.assertEquals(1, goods.size());

        JSONObject goodsObject = (JSONObject) goods.get(0);
        Assertions.assertEquals(4L, goodsObject.getLong("id"));
        Assertions.assertEquals(200, goodsObject.getInteger("number"));
        Assertions.assertEquals(DataStatus.OK.toString().toLowerCase(), goodsObject.getString("status"));

        HttpResponse responseByGet = getResponseByGet(getUrl("/api/v1/shoppingCart?pageNum=1&pageSize=10"), sessionId, httpClient);
        JSONObject responseObjectByGet = getResponseObject(responseByGet);
        JSONArray data = responseObjectByGet.getJSONArray("data");

        // 两家店铺各有一个
        Assertions.assertEquals(2, data.size());
        Assertions.assertEquals(1, responseObjectByGet.getInteger("totalPage"));
        Assertions.assertEquals(1, ((JSONObject) data.get(0)).getJSONArray("goods").size());
        Assertions.assertEquals(1, ((JSONObject) data.get(1)).getJSONArray("goods").size());
        Assertions.assertEquals(1L, ((JSONObject) ((JSONObject) data.get(0)).getJSONArray("goods").get(0)).getLong("id"));
        Assertions.assertEquals(4L, ((JSONObject) ((JSONObject) data.get(1)).getJSONArray("goods").get(0)).getLong("id"));
    }
}

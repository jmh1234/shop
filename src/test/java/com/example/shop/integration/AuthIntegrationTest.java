package com.example.shop.integration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.shop.ShopApplication;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
public class AuthIntegrationTest {

    @Resource
    private Environment environment;

    @Test
    @SneakyThrows
    public void loginLogoutTest() {
        JSONObject object = new JSONObject();
        object.put("tel", "11111111111");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://localhost:" + environment.getProperty("local.server.port");

        // 1. 最开始默认的情况下访问"/api/status" 时 处于未登录状态
        HttpResponse statusResponseWithoutCookie = getResponseByGet(url + "/api/status", null, httpClient);
        Assertions.assertFalse((Boolean) getResponseObject(statusResponseWithoutCookie).get("login"));

        // 2. 发送验证码
        HttpResponse codeSuccessResponse = getResponseByPost(url + "/api/code", object, httpClient);
        Assertions.assertEquals(200, codeSuccessResponse.getStatusLine().getStatusCode());
        String code = getResponseObject(codeSuccessResponse).get("code").toString();
        Assertions.assertNotNull(code);

        // 3. 带着验证码进行登陆得到cookie
        object.put("code", code);
        HttpResponse loginResponse = getResponseByPost(url + "/api/login", object, httpClient);
        Assertions.assertEquals(200, loginResponse.getStatusLine().getStatusCode());
        Assertions.assertEquals("11111111111", getResponseObject(loginResponse).get("tel"));

        // 4. 获得 cookie
        String setCookie = loginResponse.getFirstHeader("Set-Cookie").getValue();
        String sessionId = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));
        Assertions.assertNotNull(sessionId);

        // 5. 带着 cookie 访问 "api/status" 时 处于登陆状态
        HttpResponse statusResponseWithCookie = getResponseByGet(url + "/api/status", sessionId, httpClient);
        Assertions.assertTrue((Boolean) getResponseObject(statusResponseWithCookie).get("login"));

        // 5. 调用 "api/logout"
        HttpResponse logoutResponse = getResponseByGet(url + "/api/logout", sessionId, httpClient);
        Assertions.assertEquals(200, logoutResponse.getStatusLine().getStatusCode());

        // 6. 再次带着cookie访问 "api/status" 时 处于未登录状态
        HttpResponse httpGetStatusWithCookieAfterLogout = getResponseByGet(url + "/api/status", sessionId, httpClient);
        Assertions.assertFalse((Boolean) getResponseObject(httpGetStatusWithCookieAfterLogout).get("login"));
    }

    @Test
    @SneakyThrows
    public void loginFailureTest() {
        JSONObject object = new JSONObject();
        object.put("tel", "1111111111");
        object.put("code", "000");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://localhost:" + environment.getProperty("local.server.port");
        HttpResponse codeFailureResponse = getResponseByPost(url + "/api/login", object, httpClient);
        Assertions.assertEquals(400, codeFailureResponse.getStatusLine().getStatusCode());
    }

    @Test
    @SneakyThrows
    public void codeSendFailureTest() {
        JSONObject object = new JSONObject();
        object.put("tel", "1111111111");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://localhost:" + environment.getProperty("local.server.port");
        HttpResponse codeFailureResponse = getResponseByPost(url + "/api/code", object, httpClient);
        Assertions.assertEquals(400, codeFailureResponse.getStatusLine().getStatusCode());
    }

    @SneakyThrows
    private static JSONObject getResponseObject(HttpResponse response) {
        return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
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
    private static StringEntity getStringEntity(JSONObject object) {
        StringEntity entity = new StringEntity(JSON.toJSONString(object));
        entity.setContentType("text/json");
        entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        return entity;
    }
}

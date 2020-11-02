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
public class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    @SneakyThrows
    public void loginLogoutTest() {
        String sessionId = loginAndGetCookie();
        // 1. 带着 cookie 访问 "api/status" 时 处于登陆状态
        HttpResponse statusResponseWithCookie = getResponseByGet(getUrl("/api/status"), sessionId, httpClient);
        Assertions.assertTrue((Boolean) getResponseObject(statusResponseWithCookie).get("login"));

        // 2. 调用 "api/logout"
        HttpResponse logoutResponse = getResponseByGet(getUrl("/api/logout"), sessionId, httpClient);
        Assertions.assertEquals(200, logoutResponse.getStatusLine().getStatusCode());

        // 3. 再次带着cookie访问 "api/status" 时 处于未登录状态
        HttpResponse httpGetStatusWithCookieAfterLogout = getResponseByGet(getUrl("/api/status"), sessionId, httpClient);
        Assertions.assertFalse((Boolean) getResponseObject(httpGetStatusWithCookieAfterLogout).get("login"));

        // 4. 调用任意接口测试aspect内容
        HttpResponse getInfoWithoutSessionIdResponse = getResponseByGet(getUrl("/api/v1/goods/1"), null, httpClient);
        Assertions.assertEquals(401, getInfoWithoutSessionIdResponse.getStatusLine().getStatusCode());
        Assertions.assertEquals("Unauthorized", getResponseObject(getInfoWithoutSessionIdResponse).getString("message"));
    }

    @Test
    @SneakyThrows
    public void loginFailureTest() {
        JSONObject object = new JSONObject();
        object.put("tel", "1111111111");
        object.put("code", "000");
        HttpResponse codeFailureResponse = getResponseByPost(getUrl("/api/login"), object, null, httpClient);
        Assertions.assertEquals(400, codeFailureResponse.getStatusLine().getStatusCode());
    }

    @Test
    @SneakyThrows
    public void codeSendFailureTest() {
        JSONObject object = new JSONObject();
        object.put("tel", "1111111111");
        HttpResponse codeFailureResponse = getResponseByPost(getUrl("/api/code"), object, null, httpClient);
        Assertions.assertEquals(400, codeFailureResponse.getStatusLine().getStatusCode());
    }
}

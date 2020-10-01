package com.example.shop.integration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.shop.ShopApplication;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
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
public class AuthApplicationIntegrationTest {

    @Resource
    private Environment environment;

    @Test
    public void code1Test() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", "11111111111");
        jsonObject.put("code", "0000");
        executePostRequest(environment, jsonObject, "/api/code");
    }

    @Test
    public void codeTest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", "11111111111");
        jsonObject.put("code", "0000");
        executePostRequest(environment, jsonObject, "/api/code");
    }

    @Test
    public void loginTest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", "11111111111");
        jsonObject.put("code", "0000");
        executePostRequest(environment, jsonObject, "/api/code");
        executePostRequest(environment, jsonObject, "/api/login");
    }

    private static void executePostRequest(Environment environment, JSONObject jsonObject, String interfaceName) {
        String url = "http://localhost:" + environment.getProperty("local.server.port");
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost1 = new HttpPost(url + interfaceName);
            httpPost1.addHeader(HTTP.CONTENT_TYPE, "application/json");
            StringEntity se1 = new StringEntity(JSON.toJSONString(jsonObject));
            se1.setContentType("text/json");
            se1.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost1.setEntity(se1);
            httpclient.execute(httpPost1, (ResponseHandler<String>) httpResponse -> {
                Assertions.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

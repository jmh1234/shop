package com.example.shop.integration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

public class AbstractIntegrationTest {

    @Resource
    private Environment environment;

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    public CloseableHttpClient httpClient;

    @BeforeEach
    public void setup() {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();
        httpClient = HttpClients.createDefault();
    }

    public String loginAndGetCookie() {
        JSONObject object = new JSONObject();
        object.put("tel", "13800000000");

        // 1. 最开始默认的情况下访问"/api/status" 时 处于未登录状态
        HttpResponse statusResponseWithoutCookie = getResponseByGet(getUrl("/api/status"), null, httpClient);
        Assertions.assertFalse((Boolean) getResponseObject(statusResponseWithoutCookie).get("login"));

        // 2. 发送验证码
        HttpResponse codeSuccessResponse = getResponseByPost(getUrl("/api/code"), object, null, httpClient);
        Assertions.assertEquals(200, codeSuccessResponse.getStatusLine().getStatusCode());
        String code = getResponseObject(codeSuccessResponse).get("code").toString();
        Assertions.assertNotNull(code);

        // 3. 带着验证码进行登陆得到cookie
        object.put("code", code);
        HttpResponse loginResponse = getResponseByPost(getUrl("/api/login"), object, null, httpClient);
        Assertions.assertEquals(200, loginResponse.getStatusLine().getStatusCode());
        Assertions.assertTrue((Boolean) getResponseObject(loginResponse).get("login"));

        // 4. 获得 cookie
        String setCookie = loginResponse.getFirstHeader("Set-Cookie").getValue();
        Assertions.assertTrue(setCookie.contains("JSESSIONID=") && setCookie.contains(";"));
        String sessionId = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));
        Assertions.assertNotNull(sessionId);
        return sessionId;
    }

    public String getUrl(String apiName) {
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

    @SneakyThrows
    public static JSONObject getResponseObject(HttpResponse response) {
        return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
    }

    @SneakyThrows
    public static HttpResponse getResponseByPatch(String url, JSONObject object, String sessionId, CloseableHttpClient httpClient) {
        HttpPatch httpPatch = new HttpPatch(url);
        httpPatch.addHeader(HTTP.CONTENT_TYPE, "application/json");
        if (sessionId != null) {
            httpPatch.setHeader("Cookie", sessionId);
        }
        httpPatch.setEntity(getStringEntity(object));
        return httpClient.execute(httpPatch);
    }

    @SneakyThrows
    public static HttpResponse getResponseByDelete(String url, String sessionId, CloseableHttpClient httpClient) {
        HttpDelete httpDelete = new HttpDelete(url);
        if (sessionId != null) {
            httpDelete.setHeader("Cookie", sessionId);
        }
        httpDelete.addHeader(HTTP.CONTENT_TYPE, "application/json");
        return httpClient.execute(httpDelete);
    }

    @SneakyThrows
    public static HttpResponse getResponseByPost(String url, JSONObject object, String sessionId, CloseableHttpClient httpClient) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        if (sessionId != null) {
            httpPost.setHeader("Cookie", sessionId);
        }
        httpPost.setEntity(getStringEntity(object));
        return httpClient.execute(httpPost);
    }

    @SneakyThrows
    public static HttpResponse getResponseByGet(String url, String sessionId, CloseableHttpClient httpClient) {
        HttpGet httpGet = new HttpGet(url);
        if (sessionId != null) {
            httpGet.setHeader("Cookie", sessionId);
        }
        return httpClient.execute(httpGet);
    }

    @SneakyThrows
    public static StringEntity getStringEntity(JSONObject object) {
        StringEntity entity = new StringEntity(JSON.toJSONString(object), StandardCharsets.UTF_8);
        entity.setContentType("text/json");
        entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        return entity;
    }
}

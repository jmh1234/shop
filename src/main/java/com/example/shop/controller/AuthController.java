package com.example.shop.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.shop.service.Impl.AuthService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping("/code")
    public void code(@RequestBody JSONObject registerObj) {
        authService.sendVerificationCode(registerObj.getString("tel"));
    }

    @RequestMapping("/login")
    public void login(@RequestBody JSONObject loginObj) {
        UsernamePasswordToken token = new UsernamePasswordToken(
                loginObj.getString("tel"),
                loginObj.getString("code")
        );
        token.setRememberMe(true);
        SecurityUtils.getSubject().login(token);
    }
}

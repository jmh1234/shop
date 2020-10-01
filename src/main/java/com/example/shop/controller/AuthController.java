package com.example.shop.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.shop.service.authority.AuthService;
import com.example.shop.service.authority.TelVerificationService;
import com.example.shop.utils.LoggerUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final TelVerificationService telVerificationService;
    private final Logger logger = LoggerUtil.getInstance(AuthController.class);

    @Autowired
    public AuthController(AuthService authService, TelVerificationService telVerificationService) {
        this.authService = authService;
        this.telVerificationService = telVerificationService;
    }

    @PostMapping("/code")
    public void code(@RequestBody JSONObject registerObj, HttpServletResponse response) {
        if (telVerificationService.verifyTelParameter(registerObj)) {
            authService.sendVerificationCode(registerObj.getString("tel"));
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }

    @PostMapping("/login")
    public void login(@RequestBody JSONObject loginObj) {
        UsernamePasswordToken token = new UsernamePasswordToken(
                loginObj.getString("tel"),
                loginObj.getString("code")
        );
        token.setRememberMe(true);
        SecurityUtils.getSubject().login(token);
        logger.info("登陆成功！" + token.getUsername());
    }
}

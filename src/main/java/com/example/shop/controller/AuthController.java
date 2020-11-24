package com.example.shop.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.shop.entity.LoginResponse;
import com.example.shop.service.authority.AuthService;
import com.example.shop.service.authority.TelVerificationService;
import com.example.shop.utils.LoggerUtil;
import com.example.shop.utils.UserContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;
    private final TelVerificationService telVerificationService;
    private final Logger logger = LoggerUtil.getInstance(AuthController.class);

    @Inject
    public AuthController(AuthService authService, TelVerificationService telVerificationService) {
        this.authService = authService;
        this.telVerificationService = telVerificationService;
    }

    @PostMapping("/code")
    public LoginResponse code(@RequestBody JSONObject registerObj, HttpServletResponse response) {
        if (telVerificationService.verifyTelParameter(registerObj)) {
            String code = authService.sendVerificationCode(registerObj.getString("tel"));
            return LoginResponse.getCodeSuccess(code);
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return LoginResponse.failure();
        }
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody JSONObject loginObj, HttpServletResponse response) {
        UsernamePasswordToken token = new UsernamePasswordToken(
                loginObj.getString("tel"),
                loginObj.getString("code"));
        try {
            token.setRememberMe(true);
            SecurityUtils.getSubject().login(token);
            return LoginResponse.success();
        } catch (Exception e) {
            logger.error(LoggerUtil.formatException(e));
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return LoginResponse.failure();
        }
    }

    @GetMapping("/status")
    public Object loginStatus() {
        if (UserContext.getCurrentUser() == null) {
            return LoginResponse.notLogin();
        } else {
            return LoginResponse.login(UserContext.getCurrentUser());
        }
    }

    @GetMapping("/logout")
    public void logout() {
        SecurityUtils.getSubject().logout();
    }
}

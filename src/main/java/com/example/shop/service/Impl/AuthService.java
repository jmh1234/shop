package com.example.shop.service.Impl;

import com.example.shop.entity.User;
import com.example.shop.service.SmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final SmsCodeService smsCodeService;
    private final VerificationCodeCheckService verificationCodeCheckService;

    @Autowired
    public AuthService(UserService userService, SmsCodeService smsCodeService, VerificationCodeCheckService verificationCodeCheckService) {
        this.userService = userService;
        this.smsCodeService = smsCodeService;
        this.verificationCodeCheckService = verificationCodeCheckService;
    }

    public void sendVerificationCode(String tel) {
        User user = userService.createUserIfNotExist(tel);
        if (user != null){
            System.out.println(user.toString());
        }
        String correctCode = smsCodeService.senSmsCode(tel);
        verificationCodeCheckService.addCode(tel, correctCode);
    }
}

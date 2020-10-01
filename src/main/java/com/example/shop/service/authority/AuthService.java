package com.example.shop.service.authority;

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
        userService.createUserIfNotExist(tel);
        String correctCode = smsCodeService.senSmsCode(tel);
        verificationCodeCheckService.addCode(tel, correctCode);
    }
}

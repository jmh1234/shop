package com.example.shop.service.Impl;

import com.example.shop.service.SmsCodeService;
import org.springframework.stereotype.Service;

@Service
public class SmsCodeServiceImpl implements SmsCodeService {

    @Override
    public String senSmsCode(String tel) {
        return "0000";
    }
}

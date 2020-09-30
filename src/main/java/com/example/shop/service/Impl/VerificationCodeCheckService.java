package com.example.shop.service.Impl;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeCheckService {
    private ConcurrentHashMap<String, String> correctTelCodeMap = new ConcurrentHashMap<>();

    public void addCode(String tel, String correctCode) {
        correctTelCodeMap.put(tel, correctCode);
    }

    public String getCorrectTelCode(String tel) {
        return correctTelCodeMap.get(tel);
    }
}

package com.example.shop.service.authority;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeCheckService {
    private ConcurrentHashMap<String, String> correctTelCodeMap = new ConcurrentHashMap<>();

    void addCode(String tel, String correctCode) {
        correctTelCodeMap.put(tel, correctCode);
    }

    String getCorrectTelCode(String tel) {
        return correctTelCodeMap.get(tel);
    }
}

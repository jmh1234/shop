package com.example.shop.service.authority;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class TelVerificationService {
    private static Pattern TEL_PATTERN = Pattern.compile("1\\d{10}");

    public boolean verifyTelParameter(JSONObject jsonObject) {
        return jsonObject != null && jsonObject.getString("tel") != null
                && TEL_PATTERN.matcher(jsonObject.getString("tel")).find();
    }
}

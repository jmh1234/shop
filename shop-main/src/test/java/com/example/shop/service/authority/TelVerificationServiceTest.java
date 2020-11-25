package com.example.shop.service.authority;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TelVerificationServiceTest {

    @Test
    void returnTrueIfValid() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", "16852436393");
        Assertions.assertTrue(new TelVerificationService().verifyTelParameter(jsonObject));
    }

    @Test
    void returnTrueIfFalse() {
        JSONObject jsonObject = new JSONObject();
        Assertions.assertFalse(new TelVerificationService().verifyTelParameter(jsonObject));

        jsonObject.put("tel", "168524393");
        Assertions.assertFalse(new TelVerificationService().verifyTelParameter(jsonObject));
        Assertions.assertFalse(new TelVerificationService().verifyTelParameter(null));
    }
}

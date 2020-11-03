package com.example.shop.utils;

import java.util.HashMap;
import java.util.Map;

public class Util {

    private Util() {

    }

    public static Map<String, Integer> getPageNumAndPageSize(int pageSize, int page) {
        Map<String, Integer> resultMap = new HashMap<>();
        int offset = pageSize == 1 ? page * pageSize : (page - 1) * pageSize;
        resultMap.put("page", page);
        resultMap.put("offset", offset);
        resultMap.put("pageSize", pageSize);
        return resultMap;
    }
}

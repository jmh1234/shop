package com.example.shop.service;

import com.example.shop.api.rpc.OrderService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "${shop.service.version}")
public class OrderServiceImpl implements OrderService {
    @Override
    public String placeOrder(int goodsId, int number) {
        System.out.println("goodsId: " + goodsId + ", number: " + number);
        return "goodsId: " + goodsId + ", number: " + number;
    }
}

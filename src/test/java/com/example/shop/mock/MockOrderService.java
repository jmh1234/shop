package com.example.shop.mock;

import com.example.shop.api.OrderService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "${shop.service.version}")
public class MockOrderService implements OrderService {
    @Override
    public String placeOrder(int goodsId, int number) {
        return "I am Mock";
    }
}

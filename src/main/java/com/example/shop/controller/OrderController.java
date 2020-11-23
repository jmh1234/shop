package com.example.shop.controller;

import com.example.shop.api.OrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    @DubboReference(version = "${shop.service.version}")
    private OrderService orderService;

    @RequestMapping("/testRpc")
    public String testRpc() {
       return orderService.placeOrder(1, 2);
    }
}

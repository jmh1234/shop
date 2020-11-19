package com.example.shop.controller;

import com.example.shop.api.OrderService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    @Reference(version = "${shop.service.version}",check = false)
    private OrderService orderService;

    @RequestMapping("/testRpc")
    public void testRpc() {
        orderService.placeOrder(1, 2);
    }
}

package com.example.shop.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.shop.aspect.Authentication;
import com.example.shop.service.OrderService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/api/v1/")
public class OrderController {

    private final OrderService orderService;

    @Inject
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Authentication
    @RequestMapping("/order")
    public void createOrder(@RequestBody JSONObject orderObject) {
        orderService.createOrder();
    }
}

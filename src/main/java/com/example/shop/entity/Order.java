package com.example.shop.entity;

import com.example.shop.generate.Shop;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    private int id;
    // 快递公司
    private String expressCompany;
    // 订单号
    private int expressId;
    private String status;
    private String address;
    private Shop shop;
}

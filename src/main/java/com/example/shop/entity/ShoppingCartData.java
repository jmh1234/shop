package com.example.shop.entity;

import com.example.shop.generate.Shop;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShoppingCartData {
    private Shop shop;
    private List<GoodsWithNumber> goods;
}

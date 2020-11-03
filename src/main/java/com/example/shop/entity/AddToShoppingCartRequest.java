package com.example.shop.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddToShoppingCartRequest {
    List<AddToShoppingCartItem> goods;
}

package com.example.shop.entity;

import com.example.shop.generate.Goods;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GoodsWithNumber extends Goods {

    String number;

    public GoodsWithNumber() {

    }

    public GoodsWithNumber(Goods goods) {
        this.setId(goods.getId());
        this.setShopId(goods.getShopId());
        this.setName(goods.getName());
        this.setDescription(goods.getDescription());
        this.setImgUrl(goods.getImgUrl());
        this.setPrice(goods.getPrice());
        this.setStock(goods.getStock());
        this.setStatus(goods.getStatus());
        this.setCreatedAt(goods.getCreatedAt());
        this.setUpdatedAt(goods.getUpdatedAt());
        this.setDetails(goods.getDetails());
    }
}

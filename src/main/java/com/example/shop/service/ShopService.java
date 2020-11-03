package com.example.shop.service;

import com.example.shop.generate.Shop;
import com.example.shop.entity.Pagination;

public interface ShopService {

    Shop getShopInfoByID(long id);

    Shop updateShopInfoById(long id, Shop shop);

    Shop createShop(Shop shop);

    Shop deleteInfoById(long id);

    Pagination<Shop> getShopInfoList(int pageNum, int pageSize);
}

package com.example.shop.service;

import com.example.shop.generate.Shop;
import com.example.shop.utils.Pagination;

public interface ShopService {

    Shop getShopInfoByID(int id);

    Shop updateShopInfoById(int id, Shop shop);

    Shop createShop(Shop shop);

    Shop deleteInfoById(int id);

    Pagination<Shop> getShopInfoList(int pageNum, int pageSize);
}

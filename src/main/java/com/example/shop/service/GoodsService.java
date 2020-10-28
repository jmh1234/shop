package com.example.shop.service;

import com.example.shop.generate.Goods;
import com.example.shop.utils.Pagination;

public interface GoodsService {

    Goods insertGoods(Goods goods);

    Goods getGoodsInfoById(int id);

    Goods updateGoods(Goods goods, int id);

    Pagination<Goods> getGoodsInfoList(int pageNum, int pageSize, String shopId);

    Goods deleteGoodsById(int id);
}

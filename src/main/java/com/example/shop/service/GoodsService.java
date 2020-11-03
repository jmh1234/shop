package com.example.shop.service;

import com.example.shop.generate.Goods;
import com.example.shop.entity.Pagination;

import java.util.List;
import java.util.Map;

public interface GoodsService {

    Goods insertGoods(Goods goods);

    Goods getGoodsInfoById(long id);

    Goods updateGoods(Goods goods, long id);

    Pagination<Goods> getGoodsInfoList(int pageNum, int pageSize, String shopId);

    Goods deleteGoodsById(long id);

    Map<Long, Goods> getIdToGoodsMap(List<Long> goodsId);
}

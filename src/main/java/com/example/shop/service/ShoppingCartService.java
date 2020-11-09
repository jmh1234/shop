package com.example.shop.service;

import com.example.shop.entity.AddToShoppingCartRequest;
import com.example.shop.entity.Pagination;
import com.example.shop.entity.ShoppingCartData;

public interface ShoppingCartService {

    ShoppingCartData addToShoppingCart(AddToShoppingCartRequest request, long userId);

    Pagination<ShoppingCartData> getShoppingCartInfo(int pageNum, int pageSize);

    ShoppingCartData deleteGoodsInShoppingCart(Long goodsId, Long userId);

    void deleteAllShoppingCartByUserId(Long userId);
}

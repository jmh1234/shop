package com.example.shop.dao;

import com.example.shop.entity.ShoppingCartData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShoppingCartQueryMapper {

    int countShopsInUserShoppingCart(@Param("userId") long userId);

    List<ShoppingCartData> selectShoppingCartDataByUserId(@Param("userId") long userId);

    List<ShoppingCartData> selectShoppingCartDataByUserIdShopId(@Param("userId") long userId,
                                                                @Param("shopId") long shopId);

    void deleteShoppingCart(@Param("goodsId") long goodsId, @Param("userId") long userId);
}

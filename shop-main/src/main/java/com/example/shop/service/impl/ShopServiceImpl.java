package com.example.shop.service.impl;

import com.example.shop.entity.Pagination;
import com.example.shop.exception.HttpException;
import com.example.shop.generate.Shop;
import com.example.shop.generate.ShopExample;
import com.example.shop.generate.ShopMapper;
import com.example.shop.generate.User;
import com.example.shop.service.ShopService;
import com.example.shop.utils.UserContext;
import com.example.shop.utils.Util;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;


@Service
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;

    @Inject
    public ShopServiceImpl(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    @Override
    public Shop getShopInfoByID(long id) {
        ShopExample shopExample = new ShopExample();
        shopExample.createCriteria().andStatusEqualTo("ok");
        shopExample.createCriteria().andIdEqualTo(id);
        List<Shop> shops = shopMapper.selectByExample(shopExample);
        if (shops.isEmpty()) {
            throw HttpException.notFound("未找到!");
        }
        return shops.get(0);
    }

    @Override
    public Shop updateShopInfoById(long id, Shop shop) {
        User currentUser = UserContext.getCurrentUser();
        Shop shop1 = shopMapper.selectByPrimaryKey(id);
        if (shop1 == null) {
            throw HttpException.notFound("未找到店铺!");
        }
        if (ObjectUtils.notEqual(currentUser.getId(), shop1.getOwnerUserId())) {
            throw HttpException.forbidden("无法删除非自己管理店铺的商品!");
        }
        shop.setId(id);
        shop.setUpdatedAt(new Date());
        shopMapper.updateByPrimaryKeySelective(shop);
        return getShopInfoByID(id);
    }

    @Override
    public Shop createShop(Shop shop) {
        User currentUser = UserContext.getCurrentUser();
        shop.setStatus("ok");
        shop.setOwnerUserId(currentUser.getId());
        shop.setUpdatedAt(new Date());
        shop.setCreatedAt(new Date());
        shopMapper.insertSelective(shop);
        return shopMapper.selectByPrimaryKey(shop.getId());
    }

    @Override
    public Shop deleteInfoById(long id) {
        Shop shop = shopMapper.selectByPrimaryKey(id);
        User currentUser = UserContext.getCurrentUser();
        if (ObjectUtils.notEqual(currentUser.getId(), shop.getOwnerUserId())) {
            throw HttpException.forbidden("无法删除非自己管理店铺的商品!");
        }
        shop.setStatus("deleted");
        shop.setUpdatedAt(new Date());
        int i = shopMapper.updateByPrimaryKeySelective(shop);
        if (i == 0) {
            throw HttpException.notFound("未找到店铺!");
        }
        return shop;
    }

    @Override
    public Pagination<Shop> getShopInfoList(int pageNum, int pageSize) {
        int offset = Util.getPageNumAndPageSize(pageSize, pageNum).get("offset");
        ShopExample shopExample = new ShopExample();
        shopExample.createCriteria().andStatusEqualTo("ok");
        PageHelper.startPage(offset, pageSize);
        List<Shop> shops = shopMapper.selectByExample(shopExample);
        int total = (int) ((Page<Shop>) shops).getTotal();
        int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        return Pagination.pageOf(shops, pageSize, pageNum, totalPage, true);
    }
}

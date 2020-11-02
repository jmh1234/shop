package com.example.shop.service.impl;

import com.example.shop.generate.Shop;
import com.example.shop.generate.ShopExample;
import com.example.shop.generate.ShopMapper;
import com.example.shop.service.ShopService;
import com.example.shop.utils.Pagination;
import com.example.shop.utils.Util;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
        return shopMapper.selectByPrimaryKey(id);
    }

    @Override
    public Shop updateShopInfoById(long id, Shop shop) {
        shop.setId(id);
        shop.setUpdatedAt(new Date());
        shopMapper.updateByPrimaryKeySelective(shop);
        return getShopInfoByID(id);
    }

    @Override
    public Shop createShop(Shop shop) {
        shop.setUpdatedAt(new Date());
        shop.setCreatedAt(new Date());
        shopMapper.insertSelective(shop);
        return shopMapper.selectByPrimaryKey(shop.getId());
    }

    @Override
    public Shop deleteInfoById(long id) {
        Shop shop = shopMapper.selectByPrimaryKey(id);
        shopMapper.deleteByPrimaryKey(id);
        return shop;
    }

    @Override
    public Pagination<Shop> getShopInfoList(int pageNum, int pageSize) {
        int offset = Util.getPageNumAndPageSize(pageSize, pageNum).get("offset");
        ShopExample shopExample = new ShopExample();
        PageHelper.startPage(offset, pageSize);
        List<Shop> shops = shopMapper.selectByExample(shopExample);
        int total = (int) ((Page<Shop>) shops).getTotal();
        int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        return Pagination.pageOf(shops, pageSize, pageNum, totalPage, true);
    }
}

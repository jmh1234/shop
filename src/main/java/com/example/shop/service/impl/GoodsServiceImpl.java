package com.example.shop.service.impl;

import com.example.shop.entity.Pagination;
import com.example.shop.exception.HttpException;
import com.example.shop.generate.*;
import com.example.shop.service.GoodsService;
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
public class GoodsServiceImpl implements GoodsService {

    private final GoodsMapper goodsMapper;
    private final ShopMapper shopMapper;

    @Inject
    public GoodsServiceImpl(GoodsMapper goodsMapper, ShopMapper shopMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
    }

    @Override
    public Goods insertGoods(Goods goods) {
        User currentUser = UserContext.getCurrentUser();
        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());
        if (shop == null || ObjectUtils.notEqual(currentUser.getId(), shop.getOwnerUserId())) {
            throw HttpException.forbidden("无法添加非自己管理店铺的商品!");
        }
        goods.setStatus("ok");
        goods.setUpdatedAt(new Date());
        goods.setUpdatedAt(new Date());
        goodsMapper.insertSelective(goods);
        return goods;
    }

    @Override
    public Goods getGoodsInfoById(long id) {
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.createCriteria().andIdEqualTo(id);
        goodsExample.createCriteria().andStatusEqualTo("ok");
        Goods good = goodsMapper.selectByPrimaryKey(id);
        if (good == null || "deleted".equals(good.getStatus())) {
            throw HttpException.notFound("未找到!");
        }
        return good;
    }

    @Override
    public Goods updateGoods(Goods goods, long id) {
        User currentUser = UserContext.getCurrentUser();
        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());
        if (ObjectUtils.notEqual(currentUser.getId(), shop.getOwnerUserId())) {
            throw HttpException.forbidden("无法删除非自己管理店铺的商品!");
        }
        goods.setId(id);
        goods.setUpdatedAt(new Date());
        int i = goodsMapper.updateByPrimaryKeySelective(goods);
        if (i == 0) {
            throw HttpException.notFound("未找到商品!");
        }
        return goods;
    }

    @Override
    public Goods deleteGoodsById(long id) {
        Goods goods = getGoodsInfoById(id);
        User currentUser = UserContext.getCurrentUser();
        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());
        if (ObjectUtils.notEqual(currentUser.getId(), shop.getOwnerUserId())) {
            throw HttpException.forbidden("无法删除非自己管理店铺的商品!");
        }
        goods.setStatus("deleted");
        goods.setUpdatedAt(new Date());
        int i = goodsMapper.updateByPrimaryKeySelective(goods);
        if (i == 0) {
            throw HttpException.notFound("未找到商品!");
        }
        return goods;
    }

    @Override
    public Pagination<Goods> getGoodsInfoList(int pageNum, int pageSize, String shopId) {
        int offset = Util.getPageNumAndPageSize(pageSize, pageNum).get("offset");
        GoodsExample goodsExample = new GoodsExample();
        if (shopId != null) {
            goodsExample.createCriteria().andShopIdEqualTo(Long.valueOf(shopId));
        }
        goodsExample.createCriteria().andStatusEqualTo("ok");
        PageHelper.startPage(offset, pageSize);
        List<Goods> goods = goodsMapper.selectByExample(goodsExample);
        int total = (int) ((Page<Goods>) goods).getTotal();
        int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        return Pagination.pageOf(goods, pageSize, pageNum, totalPage, true);
    }
}

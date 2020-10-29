package com.example.shop.service.impl;

import com.example.shop.generate.Goods;
import com.example.shop.generate.GoodsExample;
import com.example.shop.generate.GoodsMapper;
import com.example.shop.service.GoodsService;
import com.example.shop.utils.Pagination;
import com.example.shop.utils.Util;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    private final GoodsMapper goodsMapper;

    @Inject
    public GoodsServiceImpl(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    @Override
    public Goods insertGoods(Goods goods) {
        goodsMapper.insertSelective(goods);
        return getGoodsInfoById(goods.getId());
    }

    @Override
    public Goods getGoodsInfoById(long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public Goods updateGoods(Goods goods, long id) {
        goods.setId(id);
        goods.setUpdatedAt(new Date());
        goodsMapper.updateByPrimaryKeySelective(goods);
        return getGoodsInfoById(goods.getId());
    }

    @Override
    public Pagination<Goods> getGoodsInfoList(int pageNum, int pageSize, String shopId) {
        int offset = Util.getPageNumAndPageSize(pageSize, pageNum).get("offset");
        GoodsExample goodsExample = new GoodsExample();
        if (!"-1".equals(shopId)) {
            goodsExample.createCriteria().andShopIdEqualTo(Long.valueOf(shopId));
        }
        PageHelper.startPage(offset, pageSize);
        List<Goods> goods = goodsMapper.selectByExample(goodsExample);
        int total = (int) ((Page<Goods>) goods).getTotal();
        int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        return Pagination.pageOf(goods, pageSize, pageNum, totalPage, true);
    }

    @Override
    public Goods deleteGoodsById(long id) {
        Goods goods = getGoodsInfoById(id);
        goodsMapper.deleteByPrimaryKey(id);
        return goods;
    }
}

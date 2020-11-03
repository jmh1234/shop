package com.example.shop.service.impl;

import com.example.shop.dao.ShoppingCartQueryMapper;
import com.example.shop.entity.*;
import com.example.shop.exception.HttpException;
import com.example.shop.generate.*;
import com.example.shop.service.GoodsService;
import com.example.shop.service.ShoppingCartService;
import com.example.shop.utils.LoggerUtil;
import com.example.shop.utils.UserContext;
import com.example.shop.utils.Util;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final GoodsService goodsService;
    private final GoodsMapper goodsMapper;
    private final SqlSessionFactory sqlSessionFactory;
    private final ShoppingCartQueryMapper shoppingCartQueryMapper;
    private final Logger logger = LoggerUtil.getInstance(ShoppingCartServiceImpl.class);

    @Inject
    public ShoppingCartServiceImpl(ShoppingCartQueryMapper shoppingCartQueryMapper, GoodsService goodsService, GoodsMapper goodsMapper, SqlSessionFactory sqlSessionFactory) {
        this.goodsMapper = goodsMapper;
        this.goodsService = goodsService;
        this.sqlSessionFactory = sqlSessionFactory;
        this.shoppingCartQueryMapper = shoppingCartQueryMapper;
    }

    @Override
    public ShoppingCartData addToShoppingCart(AddToShoppingCartRequest request, long userId) {
        List<Long> goodsId = request.getGoods().stream()
                .map(AddToShoppingCartItem::getId)
                .collect(toList());

        if (goodsId.isEmpty()) {
            throw HttpException.badRequest("商品ID为空！");
        }

        Map<Long, Goods> idToGoodsMap = goodsService.getIdToGoodsMap(goodsId);
        if (idToGoodsMap.values().stream().map(Goods::getShopId).collect(toSet()).size() != 1) {
            logger.debug("非法请求：{}, {}", goodsId, idToGoodsMap.values());
            throw HttpException.badRequest("商品ID非法！");
        }

        List<ShoppingCart> shoppingCartRows = request.getGoods().stream()
                .map(item -> toShoppingCartRow(item, idToGoodsMap))
                .collect(toList());

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            shoppingCartRows.forEach(row -> insertGoodsToShoppingCart(userId, row, mapper));
            sqlSession.commit();
        }
        return getLatestShoppingCartDataByUserIdShopId(new ArrayList<>(idToGoodsMap.values()).get(0).getShopId(), userId);
    }

    @Override
    public Pagination<ShoppingCartData> getShoppingCartInfo(int pageNum, int pageSize) {
        long userId = UserContext.getCurrentUser().getId();
        int offset = Util.getPageNumAndPageSize(pageSize, pageNum).get("offset");
        PageHelper.startPage(offset, pageSize);
        List<ShoppingCartData> shoppingCartData = shoppingCartQueryMapper.selectShoppingCartDataByUserId(userId);
        int total = shoppingCartQueryMapper.countShopsInUserShoppingCart(userId);
        int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        return Pagination.pageOf(shoppingCartData, pageSize, pageNum, totalPage, true);
    }

    @Override
    public ShoppingCartData deleteGoodsInShoppingCart(Long goodsId, Long userId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        if (goods == null) {
            throw HttpException.notFound("商品未找到：" + goodsId);
        }
        shoppingCartQueryMapper.deleteShoppingCart(goodsId, userId);
        return getLatestShoppingCartDataByUserIdShopId(goods.getShopId(), userId);
    }

    private void insertGoodsToShoppingCart(long userId, ShoppingCart shoppingCartRow, ShoppingCartMapper shoppingCartMapper) {
        ShoppingCartExample example = new ShoppingCartExample();
        example.createCriteria().andGoodsIdEqualTo(shoppingCartRow.getGoodsId()).andUserIdEqualTo(userId);
        shoppingCartMapper.deleteByExample(example);
        shoppingCartMapper.insert(shoppingCartRow);
    }

    private ShoppingCartData getLatestShoppingCartDataByUserIdShopId(long shopId, long userId) {
        List<ShoppingCartData> resultRows = shoppingCartQueryMapper.selectShoppingCartDataByUserIdShopId(userId, shopId);
        return merge(resultRows);
    }

    private ShoppingCartData merge(List<ShoppingCartData> goodsOfSameShop) {
        assert !goodsOfSameShop.isEmpty();
        ShoppingCartData result = new ShoppingCartData();
        result.setShop(goodsOfSameShop.get(0).getShop());
        List<GoodsWithNumber> goods = goodsOfSameShop.stream()
                .map(ShoppingCartData::getGoods)
                .flatMap(List::stream)
                .collect(toList());
        result.setGoods(goods);
        return result;
    }

    private ShoppingCart toShoppingCartRow(AddToShoppingCartItem item, Map<Long, Goods> idToGoodsMap) {
        Goods goods = idToGoodsMap.get(item.getId());
        assert goods != null;
        ShoppingCart result = new ShoppingCart();
        result.setGoodsId(item.getId());
        result.setNumber(item.getNumber());
        result.setUserId(UserContext.getCurrentUser().getId());
        result.setShopId(goods.getShopId());
        result.setStatus(DataStatus.OK.toString().toLowerCase());
        result.setCreatedAt(new Date());
        result.setUpdatedAt(new Date());
        return result;
    }
}

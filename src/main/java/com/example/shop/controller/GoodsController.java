package com.example.shop.controller;

import com.example.shop.aspect.Authentication;
import com.example.shop.generate.Goods;
import com.example.shop.service.GoodsService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/api/v1")
public class GoodsController {
    private final GoodsService goodsService;

    @Inject
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }


    @PostMapping("goods")
    @Authentication
    public Object createGoods(@RequestBody Goods goods) {
        return goodsService.insertGoods(clear(goods));
    }

    @Authentication
    @PatchMapping("/goods/{id}")
    public Object updateGoods(@PathVariable("id") long id, @RequestBody Goods goods) {
        return goodsService.updateGoods(clear(goods), id);
    }

    @Authentication
    @GetMapping("goods")
    public Object getGoodsInfoList(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize,
                                   @RequestParam(value = "shopId", required = false) String shopId) {
        return goodsService.getGoodsInfoList(pageNum, pageSize, shopId);
    }

    @Authentication
    @GetMapping("goods/{id}")
    public Object getGoodsById(@PathVariable("id") long id) {
        return goodsService.getGoodsInfoById(id);
    }

    @Authentication
    @DeleteMapping("goods/{id}")
    public Object deleteGoodsById(@PathVariable("id") long id) {
        return goodsService.deleteGoodsById(id);
    }

    private static Goods clear(Goods goods) {
        goods.setId(null);
        goods.setCreatedAt(null);
        goods.setUpdatedAt(null);
        return goods;
    }
}

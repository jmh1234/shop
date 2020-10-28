package com.example.shop.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.shop.generate.Goods;
import com.example.shop.service.GoodsService;
import com.example.shop.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("/api/v1")
public class GoodsController {
    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("goods")
    public Object createGoods(@RequestBody Goods goods, HttpServletResponse response) {
        if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // todo
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            return object;
        } else {
            return goodsService.insertGoods(clear(goods, "insert"));
        }
    }

    @PatchMapping("/goods/{id}")
    public Object updateGoods(@RequestBody Goods goods, @PathVariable("id") int id, HttpServletResponse response) {
        if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // todo
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            return object;
        } else {
            return goodsService.updateGoods(clear(goods, "update"), id);
        }
    }

    @GetMapping("goods")
    public Object getGoodsInfoList(@RequestParam("pageNum") int pageNum,
                                   @RequestParam("pageSize") int pageSize,
                                   @RequestParam(value = "shopId", defaultValue = "-1") String shopId,
                                   HttpServletResponse response) {
        if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // todo
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            return object;
        } else {
            return goodsService.getGoodsInfoList(pageNum, pageSize, shopId);
        }
    }

    @GetMapping("goods/{id}")
    public Object getGoodsById(@PathVariable("id") int id, HttpServletResponse response) {
        if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // todo
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            return object;
        } else {
            return goodsService.getGoodsInfoById(id);
        }
    }

    @DeleteMapping("goods/{id}")
    public Object deleteGoodsById(@PathVariable("id") int id, HttpServletResponse response) {
        if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // todo
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            return object;
        } else {
            return goodsService.deleteGoodsById(id);
        }
    }

    private static Goods clear(Goods goods, String type) {
        goods.setId(null);
        goods.setCreatedAt("insert".equals(type) ? new Date() : null);
        goods.setUpdatedAt(new Date());
        return goods;
    }
}

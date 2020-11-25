package com.example.shop.controller;

import com.example.shop.aspect.ResponseAnnotation;
import com.example.shop.generate.Shop;
import com.example.shop.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/api/v1")
public class ShopController {

    private final ShopService shopService;

    @Inject
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @ResponseAnnotation
    @PostMapping("/shop")
    public Object createShop(@RequestBody Shop shop) {
        return shopService.createShop(shop);
    }

    @ResponseAnnotation
    @PatchMapping("/shop/{id}")
    public Object updateShopInfo(@PathVariable("id") long id, @RequestBody Shop shop) {
        return shopService.updateShopInfoById(id, shop);
    }

    @ResponseAnnotation
    @DeleteMapping("/shop/{id}")
    public Object deleteShopInfo(@PathVariable("id") long id) {
        return shopService.deleteInfoById(id);
    }

    @ResponseAnnotation
    @GetMapping("shop")
    public Object getShopInfoList(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        return shopService.getShopInfoList(pageNum, pageSize);
    }

    @ResponseAnnotation
    @GetMapping("shop/{id}")
    public Object getShopById(@PathVariable("id") long id) {
        return shopService.getShopInfoByID(id);
    }
}

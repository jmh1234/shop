package com.example.shop.controller;

import com.example.shop.aspect.Authentication;
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

    @Authentication
    @PostMapping("/shop")
    public Object createShop(@RequestBody Shop shop) {
        return shopService.createShop(shop);
    }

    @Authentication
    @PatchMapping("/shop/{id}")
    public Object updateShopInfo(@PathVariable("id") long id, @RequestBody Shop shop) {
        return shopService.updateShopInfoById(id, shop);
    }

    @Authentication
    @DeleteMapping("/shop/{id}")
    public Object deleteShopInfo(@PathVariable("id") long id) {
        return shopService.deleteInfoById(id);
    }

    @Authentication
    @GetMapping("shop")
    public Object getShopInfoList(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        return shopService.getShopInfoList(pageNum, pageSize);
    }

    @Authentication
    @GetMapping("shop/{id}")
    public Object getShopById(@PathVariable("id") long id) {
        return shopService.getShopInfoByID(id);
    }
}

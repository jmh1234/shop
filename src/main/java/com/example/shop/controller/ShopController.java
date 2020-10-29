package com.example.shop.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.shop.generate.Shop;
import com.example.shop.service.ShopService;
import com.example.shop.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class ShopController {

    private final ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PatchMapping("/shop/{id}")
    public Object updateShopInfo(@PathVariable("id") int id, @RequestBody Shop shop, HttpServletResponse response) {
        if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // todo
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            return object;
        } else {
            return shopService.updateShopInfoById(id, shop);
        }
    }

    @PostMapping("/shop")
    public Object createShop(@RequestBody Shop shop, HttpServletResponse response) {
        if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // todo
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            return object;
        } else {
            return shopService.createShop(shop);
        }
    }

    @DeleteMapping("/shop/{id}")
    public Object deleteShopInfo(@PathVariable("id") int id, HttpServletResponse response) {
        if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // todo
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            return object;
        } else {
            return shopService.deleteInfoById(id);
        }
    }

    @GetMapping("shop")
    public Object getShopInfoList(@RequestParam("pageNum") int pageNum,
                                  @RequestParam("pageSize") int pageSize, HttpServletResponse response) {
        if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // todo
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            return object;
        } else {
//            String tel = UserContext.getCurrentUser().getTel();
            return shopService.getShopInfoList(pageNum, pageSize);
        }
    }

    @GetMapping("shop/{id}")
    public Object getShopById(@PathVariable("id") int id, HttpServletResponse response) {
        if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // todo
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            return object;
        } else {
            return shopService.getShopInfoByID(id);
        }
    }
}

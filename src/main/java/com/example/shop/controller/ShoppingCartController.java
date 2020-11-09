package com.example.shop.controller;

import com.example.shop.aspect.Authentication;
import com.example.shop.entity.AddToShoppingCartRequest;
import com.example.shop.service.ShoppingCartService;
import com.example.shop.utils.UserContext;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/api/v1/")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @Inject
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @Authentication
    @PostMapping("/shoppingCart")
    public Object addToShoppingCart(@RequestBody AddToShoppingCartRequest request) {
        return shoppingCartService.addToShoppingCart(request, UserContext.getCurrentUser().getId());
    }

    @Authentication
    @GetMapping("/shoppingCart")
    public Object getShoppingCartInfo(@PathParam("pageNum") int pageNum, @PathParam("pageSize") int pageSize) {
        return shoppingCartService.getShoppingCartInfo(pageNum, pageSize);
    }

    @Authentication
    @DeleteMapping("/shoppingCart/{id}")
    public Object deleteGoodsInShoppingCart(@PathVariable("id") Long goodsId) {
        System.out.println(goodsId);
        return shoppingCartService.deleteGoodsInShoppingCart(goodsId, UserContext.getCurrentUser().getId());
    }

    @Authentication
    @DeleteMapping("/deleteAllShoppingCart")
    public void deleteAllShoppingCartByUserId() {
        shoppingCartService.deleteAllShoppingCartByUserId(UserContext.getCurrentUser().getId());
    }


}

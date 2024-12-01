package com.ecom.service;

import com.ecom.entity.Cart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    Cart saveCart(Integer productId, Integer userId);
    List<Cart> getCartByUserId(Integer userId);
    Integer getCountCart(Integer userId);

    void     updateQuantity(String sy, Integer cid);
}

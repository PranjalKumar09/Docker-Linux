package com.ecom.service.impl;

import com.ecom.entity.Cart;
import com.ecom.entity.Product;
import com.ecom.entity.UserDtls;
import com.ecom.repository.CartReposistory;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserReposistory;
import com.ecom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartReposistory cartReposistory;
    @Autowired
    private UserReposistory userReposistory;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Cart> getCartByUserId(Integer userId) {
        List<Cart>   carts =  cartReposistory.findByUserId(userId);
        double totalOrderPrice = 0.0;
        List<Cart> cartList = new ArrayList<>();
        for (Cart cart : carts) {
            double totalPrice = cart.getProduct().getDiscount_price()*cart.getQuantity() ;
            cart.setTotalPrice(totalPrice);
            totalOrderPrice += totalPrice;
            cart.setTotalOrderAmount(totalOrderPrice);
            cartList.add(cart);
        }
        return cartList;


    }

    @Override
    public Integer getCountCart(Integer userId) {
        return cartReposistory.countByUser(userReposistory.findById(userId).get());
    }

    @Override
    public void updateQuantity(String sy, Integer cid) {
        Cart cart = cartReposistory.findById(cid).get();
        int quantity ;
        if ("de".equals(sy)) {
            quantity = cart.getQuantity() - 1;
            if (quantity <= 0) {
                cartReposistory.deleteById(cid);
                return;
            }
        }
        else  quantity = cart.getQuantity() + 1;

        cart.setQuantity(quantity);
        cartReposistory.save(cart);
    }

    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        UserDtls userDtls = userReposistory.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (userDtls == null || product == null) {
            throw new IllegalArgumentException("User or Product not found");
        }

        Cart cart = cartReposistory.findByUserAndProduct(userDtls, product);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(userDtls);
            cart.setProduct(product);
            cart.setQuantity(1);
            cart.setTotalPrice(product.getDiscount_price());
        } else {
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscount_price());
        }
        return cartReposistory.save(cart);
    }

}

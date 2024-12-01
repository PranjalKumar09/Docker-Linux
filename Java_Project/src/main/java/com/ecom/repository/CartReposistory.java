package com.ecom.repository;

import com.ecom.entity.Cart;
import com.ecom.entity.Product;
import com.ecom.entity.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartReposistory extends JpaRepository<Cart, Integer> {
     Cart findByUserAndProduct(UserDtls user, Product product);
     Integer countByUser(UserDtls user);
     List<Cart> findByUserId(Integer user);
}

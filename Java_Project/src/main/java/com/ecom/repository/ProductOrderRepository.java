package com.ecom.repository;

import com.ecom.entity.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface   ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByUserDtlsId(Integer userId);
    List<ProductOrder> findByOrderIdContainingIgnoreCase(String orderId);
    Page<ProductOrder> findByOrderIdContainingIgnoreCase(String orderId, Pageable pageable);
    Page<ProductOrder> findByUserDtlsId(Integer userId, Pageable pageable);

}
package com.ecom.service;

import com.ecom.entity.OrderRequest;
import com.ecom.entity.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    void saveOrder(Integer userId, OrderRequest orderRequest);
    List<ProductOrder> getOrderByUserId(Integer userId);
    ProductOrder updateOrderStatus(Integer orderId, String status);

    Page<ProductOrder> getOrders(Pageable pageable);
    List<ProductOrder> seachOrders(String keyword);
    Page<ProductOrder> seachOrders(String keyword, Integer pageNo, Integer pageSize);

    Page<ProductOrder> getOrderByUserId(Integer userId, Integer pageNo, Integer pageSize);

    }

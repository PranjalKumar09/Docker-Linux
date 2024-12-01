package com.ecom.service.impl;

import com.ecom.entity.Cart;
import com.ecom.entity.OrderAddress;
import com.ecom.entity.OrderRequest;
import com.ecom.entity.ProductOrder;
import com.ecom.repository.CartReposistory;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.OrderService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    public ProductOrderRepository productOrderRepository;
    @Autowired
    private CartReposistory cartReposistory;
    @Autowired
    private CommonUtil commonUtil;


    @Override
    public List<ProductOrder> seachOrders(String keyword) {
        return productOrderRepository.findByOrderIdContainingIgnoreCase(keyword);
    }

    @Override
    public Page<ProductOrder> seachOrders(String keyword, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productOrderRepository.findByOrderIdContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public Page<ProductOrder> getOrderByUserId(Integer userId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return  productOrderRepository.findByUserDtlsId(userId,pageable );
    }

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) {

        List<Cart> cartList = cartReposistory.findByUserId(userId);

        for (Cart cart : cartList) {
            ProductOrder productOrder = new ProductOrder();
            productOrder.setOrderId(UUID.randomUUID().toString());
            productOrder.setOrderDate(new Date());
            productOrder.setProduct(cart.getProduct());
            productOrder.setPrice(cart.getProduct().getDiscount_price());
            productOrder.setQuantity(cart.getQuantity());
            productOrder.setUserDtls(cart.getUser());
            productOrder.setStatus(OrderStatus.IN_PROGRESS.getName());
            productOrder.setPaymentType(orderRequest.getPaymentType());


            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setPincode(orderRequest.getPincode());

            productOrder.setOrderAddress(address);
            ProductOrder productOrder1 =    productOrderRepository.save(productOrder);

            commonUtil.sendMailForProductOrder(productOrder1);
        }

    }

    @Override
    public List<ProductOrder> getOrderByUserId(Integer userId) {
        return productOrderRepository.findByUserDtlsId(userId);
    }

    @Override
    public ProductOrder updateOrderStatus(Integer orderId, String status) {
        Optional<ProductOrder> productOrder = productOrderRepository.findById(orderId);
        if (productOrder.isPresent()) {
            ProductOrder productOrder1 = productOrder.get();

            // Resolve the status string to an OrderStatus enum
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                // Set the status (assuming ProductOrder.setStatus accepts a String)
                productOrder1.setStatus(orderStatus.getName());

                // Save the updated order

                return productOrderRepository.save(productOrder1);
            } catch (IllegalArgumentException e) {
                // Handle invalid status string
                throw new IllegalArgumentException("Invalid status value: " + status, e);
            }
        }
        return null; // Order not found
    }

    @Override
        public Page<ProductOrder> getOrders(Pageable pageable) {
        return productOrderRepository.findAll(pageable);
    }


}

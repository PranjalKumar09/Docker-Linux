package com.ecom.util;

import lombok.Setter;

public enum OrderStatus {
        IN_PROGRESS(1,"In Progress"),
    ORDER_RECIVED(2, "Order Received"),
    PRODUCT_PACKED(3, "Product Packed"),
    OUT_FOR_DELIVERY(4, "Order For Status"),
    DELIVERD(5, "Delivered"),
    CANCEL(6, "Cancelled"),
    SUCCESS(7, "Order Success");
    ;

        @Setter
        private Integer id;
        private String name;
        OrderStatus(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
        public Integer getId() {
            return id;
        }
        public String getName() {
            return name;
        }



}

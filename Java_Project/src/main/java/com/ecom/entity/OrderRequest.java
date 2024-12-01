package com.ecom.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String paymentType;
}

package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;

@Data
public class BuyerQueryDTO {
    private Long id;
    private String userName;
    private String name;
    private String idNumber;
    private String phone;
    private String sex;
    private String address;
    private String status;
}

package com.example.ecommerceplatform.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuyerLoginVO {
    private Long id;
    private String userName;
    private String name;
    private String phone;
    private String address;
    private String image;
    private String status;
    private String token;
}

package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;

@Data
public class OrderUpdateInfoDTO {

    /**
     * 买家电话
     */
    private String buyerPhone;

    /**
     * 买家地址
     */
    private String buyerAddress;
}
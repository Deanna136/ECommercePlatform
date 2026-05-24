package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class BuyerOrderCreateDTO {
    private String phone;
    private String address;
    private List<BuyerOrderItemDTO> orderItems;
}

package com.example.ecommerceplatform.pojo.dto;

import com.example.ecommerceplatform.common.enumeration.SexEnum;
import lombok.Data;

@Data
public class BuyerUpdateDTO {
    private String name;
    private String idNumber;
    private SexEnum sex;
    private String phone;
    private String address;
    private String image;
}

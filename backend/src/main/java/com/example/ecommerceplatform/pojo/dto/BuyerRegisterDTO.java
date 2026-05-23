package com.example.ecommerceplatform.pojo.dto;

import com.example.ecommerceplatform.common.enumeration.SexEnum;
import lombok.Data;

@Data
public class BuyerRegisterDTO {
    private String userName;
    private String password;
    private String name;
    private String idNumber;
    private SexEnum sex;
    private String phone;
    private String address;
    private String image;
}

package com.example.ecommerceplatform.pojo.entity;

import com.example.ecommerceplatform.common.enumeration.AcountStatusEnum;
import com.example.ecommerceplatform.common.enumeration.CategoryEnum;
import com.example.ecommerceplatform.common.enumeration.SexEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seller {
    private Long id;
    private String userName;
    private String password;
    private String name;
    private String idNumber;
    private SexEnum sex;
    private String phone;
    private String address;
    private String image;
    private AcountStatusEnum status;
    private String storeName;
    private CategoryEnum storeCategory;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

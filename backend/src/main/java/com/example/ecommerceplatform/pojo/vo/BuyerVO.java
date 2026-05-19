package com.example.ecommerceplatform.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerVO {
    private Long id;
    private String userName;
    private String name;
    private String idNumber;
    private String sex;
    private String phone;
    private String address;
    private String image;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

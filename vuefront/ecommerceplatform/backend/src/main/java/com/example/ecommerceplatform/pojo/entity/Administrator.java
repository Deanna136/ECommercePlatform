package com.example.ecommerceplatform.pojo.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrator {
    private Long id;
    private String userName;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

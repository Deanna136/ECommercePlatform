package com.example.ecommerceplatform.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdministratorLoginVO {
    private Long id;
    private String userName;
    private String token;
}

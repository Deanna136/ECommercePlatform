package com.example.ecommerceplatform.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(description = "卖家登录响应体")
public class SellerLoginVO {

    @ApiModelProperty(value = "卖家ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户名", example = "seller01")
    private String userName;

    @ApiModelProperty(value = "真实姓名", example = "甲商家")
    private String name;

    @ApiModelProperty(value = "店铺名称", example = "潮流服饰店")
    private String storeName;

    @ApiModelProperty(value = "店铺类型", example = "clothing")
    private String storeCategory;

    @ApiModelProperty(value = "账号状态", example = "active")
    private String status;

    @ApiModelProperty(value = "JWT Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}
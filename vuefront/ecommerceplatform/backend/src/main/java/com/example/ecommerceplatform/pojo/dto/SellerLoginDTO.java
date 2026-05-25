package com.example.ecommerceplatform.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "卖家登录请求体")
public class SellerLoginDTO {

    @ApiModelProperty(value = "用户名", required = true, example = "seller01")
    private String userName;

    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;
}
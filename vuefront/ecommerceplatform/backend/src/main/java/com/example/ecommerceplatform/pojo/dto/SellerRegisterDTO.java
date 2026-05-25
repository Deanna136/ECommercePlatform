package com.example.ecommerceplatform.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "卖家注册请求体")
public class SellerRegisterDTO {

    @ApiModelProperty(value = "用户名", required = true, example = "seller_new")
    private String userName;

    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;

    @ApiModelProperty(value = "真实姓名", example = "王大卖")
    private String name;

    @ApiModelProperty(value = "身份证号", example = "310101199001011234")
    private String idNumber;

    @ApiModelProperty(value = "性别：male/female", example = "male")
    private String sex;

    @ApiModelProperty(value = "手机号", example = "13900139099")
    private String phone;

    @ApiModelProperty(value = "地址", example = "上海市浦东新区")
    private String address;

    @ApiModelProperty(value = "头像URL", example = "https://img.com/avatar.jpg")
    private String image;

    @ApiModelProperty(value = "店铺名称", required = true, example = "王大卖的店铺")
    private String storeName;

    @ApiModelProperty(value = "店铺类型", required = true, example = "clothing")
    private String storeCategory;
}
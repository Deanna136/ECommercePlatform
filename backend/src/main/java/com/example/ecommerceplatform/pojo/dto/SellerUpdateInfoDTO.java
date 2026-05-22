package com.example.ecommerceplatform.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "卖家修改个人信息请求体")
public class SellerUpdateInfoDTO {

    @ApiModelProperty(value = "真实姓名", example = "甲商家（新）")
    private String name;

    @ApiModelProperty(value = "身份证号", example = "310101198501011111")
    private String idNumber;

    @ApiModelProperty(value = "性别：male/female", example = "male")
    private String sex;

    @ApiModelProperty(value = "手机号", example = "13900139099")
    private String phone;

    @ApiModelProperty(value = "地址", example = "上海市静安区")
    private String address;

    @ApiModelProperty(value = "头像URL", example = "https://img.com/new_avatar.jpg")
    private String image;

    @ApiModelProperty(value = "店铺名称", example = "新潮服饰旗舰店")
    private String storeName;

    @ApiModelProperty(value = "店铺类型", example = "clothing")
    private String storeCategory;
}
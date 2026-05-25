package com.example.ecommerceplatform.pojo.dto;

import com.example.ecommerceplatform.common.enumeration.CategoryEnum;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SellerProductAddDTO {
    @NotBlank(message = "商品编号不能为空")
    private String productNo;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "商品类型不能为空")
    private CategoryEnum category;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Long sku;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能小于0")
    private Float price;

    private String image;
    private String description;
}

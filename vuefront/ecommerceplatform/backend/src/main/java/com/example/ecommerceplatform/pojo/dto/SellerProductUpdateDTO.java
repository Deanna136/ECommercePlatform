package com.example.ecommerceplatform.pojo.dto;

import com.example.ecommerceplatform.common.enumeration.CategoryEnum;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class SellerProductUpdateDTO {
    private Long id;
    private String name;
    private CategoryEnum category;

    @Min(value = 0, message = "库存不能小于0")
    private Long sku;

    @Min(value = 0, message = "价格不能小于0")
    private Float price;

    private String image;
    private String description;
}

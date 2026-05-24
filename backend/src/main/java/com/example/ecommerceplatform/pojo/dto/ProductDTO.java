package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ProductDTO {

    /**
     * 商品ID（修改时必传）
     */
    private Long id;

    /**
     * 商品编号
     */
    @NotBlank(message = "商品编号不能为空")
    private String productNo;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品分类
     */
    @NotBlank(message = "商品分类不能为空")
    private String category;

    /**
     * 库存
     */
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Long sku;

    /**
     * 商品价格
     */
    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能小于0")
    private Float price;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品状态
     */
    private String status;
}
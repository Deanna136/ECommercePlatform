package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.ProductDTO;
import com.example.ecommerceplatform.pojo.vo.ProductVO;

import java.util.List;

public interface ProductService {

    /**
     * 新增商品
     */
    void add(ProductDTO dto);

    /**
     * 修改商品
     */
    void update(ProductDTO dto);

    /**
     * 商品详情
     */
    ProductVO detail(Long id);

    /**
     * 商品列表
     */
    List<ProductVO> list();

    /**
     * 商品上下架
     */
    void updateStatus(Long id, String status);

    /**
     * 删除商品
     */
    void delete(Long id);

    /**
     * 停售商品
     */
    void stopSale(Long id);

    /**
     * 商品条件筛选
     */
    List<ProductVO> search(
            String name,
            String category,
            String status
    );
}
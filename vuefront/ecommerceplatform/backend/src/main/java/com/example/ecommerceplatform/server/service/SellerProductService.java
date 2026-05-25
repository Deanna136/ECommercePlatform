package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.SellerProductAddDTO;
import com.example.ecommerceplatform.pojo.dto.SellerProductUpdateDTO;
import com.example.ecommerceplatform.pojo.entity.Product;

import java.util.List;

public interface SellerProductService {

    List<Product> getProductList(Long sellerId);

    Product getProductDetail(Long sellerId, Long productId);

    List<Product> queryProducts(Long sellerId, String productNo, String name, String category, String status, Float minPrice, Float maxPrice);

    Product addProduct(Long sellerId, SellerProductAddDTO dto);

    void updateProduct(Long sellerId, Long productId, SellerProductUpdateDTO dto);

    void stopSale(Long sellerId, Long productId);
}

package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.BuyerProductQueryDTO;
import com.example.ecommerceplatform.pojo.dto.ProductQueryDTO;
import com.example.ecommerceplatform.pojo.vo.ProductVO;

import java.util.List;

public interface ProductService {
    List<ProductVO> getAll();
    ProductVO getById(Long id);
    List<ProductVO> query(ProductQueryDTO dto);

    List<ProductVO> listOnsale();
    ProductVO getOnsaleById(Long id);
    List<ProductVO> queryOnsale(BuyerProductQueryDTO dto);

    void approve(Long id);
    void reject(Long id);
    void suspend(Long id);
}

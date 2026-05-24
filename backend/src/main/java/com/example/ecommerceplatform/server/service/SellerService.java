package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.SellerQueryDTO;
import com.example.ecommerceplatform.pojo.vo.SellerVO;

import java.util.List;

public interface SellerService {
    List<SellerVO> getAll();
    SellerVO getById(Long id);
    List<SellerVO> query(SellerQueryDTO dto);
    void ban(Long id);
    void unban(Long id);
}

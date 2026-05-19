package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.BuyerQueryDTO;
import com.example.ecommerceplatform.pojo.vo.BuyerVO;

import java.util.List;

public interface BuyerService {
    List<BuyerVO> getAll();
    BuyerVO getById(Long id);
    List<BuyerVO> query(BuyerQueryDTO dto);
    void ban(Long id);
    void unban(Long id);
}

package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.BuyerLoginDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerQueryDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerRegisterDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerUpdateDTO;
import com.example.ecommerceplatform.pojo.vo.BuyerLoginVO;
import com.example.ecommerceplatform.pojo.vo.BuyerVO;

import java.util.List;

public interface BuyerService {
    List<BuyerVO> getAll();
    BuyerVO getById(Long id);
    List<BuyerVO> query(BuyerQueryDTO dto);
    void ban(Long id);
    void unban(Long id);

    void register(BuyerRegisterDTO buyerRegisterDTO);

    BuyerLoginVO login(BuyerLoginDTO buyerLoginDTO);

    void update(BuyerUpdateDTO buyerUpdateDTO);
}

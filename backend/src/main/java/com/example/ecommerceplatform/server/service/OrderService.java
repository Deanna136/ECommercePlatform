package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.OrderQueryDTO;
import com.example.ecommerceplatform.pojo.vo.OrderDetailVO;
import com.example.ecommerceplatform.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {
    List<OrderVO> getAll();
    OrderDetailVO getById(Long id);
    List<OrderVO> query(OrderQueryDTO dto);
    void forceClose(Long id);
    void handleAbnormal(Long id, String handleResult);
}

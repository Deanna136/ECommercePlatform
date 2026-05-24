package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.OrderAbnormalDTO;
import com.example.ecommerceplatform.pojo.dto.OrderUpdateInfoDTO;
import com.example.ecommerceplatform.pojo.dto.OrderUpdateStatusDTO;
import com.example.ecommerceplatform.pojo.vo.OrderDetailVO;
import com.example.ecommerceplatform.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {

    /**
     * 订单列表
     */
    List<OrderVO> list();

    /**
     * 订单详情
     */
    OrderDetailVO detail(Long id);

    /**
     * 条件筛选
     */
    List<OrderVO> query(
            String orderNo,
            Long buyerId,
            String status,
            String startTime,
            String endTime
    );

    /**
     * 修改订单信息
     */
    void updateInfo(Long id, OrderUpdateInfoDTO dto);

    /**
     * 修改订单状态
     */
    void updateStatus(Long id, OrderUpdateStatusDTO dto);

    /**
     * 异常申报
     */
    void reportAbnormal(Long id, OrderAbnormalDTO dto);
}
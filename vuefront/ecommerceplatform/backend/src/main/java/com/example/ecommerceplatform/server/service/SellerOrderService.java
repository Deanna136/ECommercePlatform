package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.entity.Orders;
import com.example.ecommerceplatform.pojo.entity.OrderItems;

import java.util.List;
import java.util.Map;

public interface SellerOrderService {

    List<Map<String, Object>> getOrderList(Long sellerId);

    Map<String, Object> getOrderDetail(Long sellerId, Long orderId);

    List<Map<String, Object>> queryOrders(Long sellerId, String orderNo, String buyerName, String status, String startTime, String endTime);

    void updateOrderStatus(Long sellerId, Long orderId, String status);

    void reportAbnormal(Long sellerId, Long orderId, String abnormalReason);
}

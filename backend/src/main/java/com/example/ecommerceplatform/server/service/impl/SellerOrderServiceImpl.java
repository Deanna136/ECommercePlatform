package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.enumeration.OrdersStatusEnum;
import com.example.ecommerceplatform.pojo.entity.Orders;
import com.example.ecommerceplatform.pojo.entity.OrderItems;
import com.example.ecommerceplatform.pojo.entity.Product;
import com.example.ecommerceplatform.server.mapper.OrdersMapper;
import com.example.ecommerceplatform.server.mapper.OrderItemsMapper;
import com.example.ecommerceplatform.server.mapper.ProductMapper;
import com.example.ecommerceplatform.server.mapper.BuyerMapper;
import com.example.ecommerceplatform.server.service.SellerOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SellerOrderServiceImpl implements SellerOrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private BuyerMapper buyerMapper;

    @Override
    public List<Map<String, Object>> getOrderList(Long sellerId) {
        List<Orders> orders = ordersMapper.findBySellerId(sellerId);
        return orders.stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("buyerId", order.getBuyerId());
            map.put("buyerName", buyerMapper.findNameById(order.getBuyerId()));
            map.put("amount", order.getAmount());
            map.put("status", order.getStatus() != null ? order.getStatus().name().toLowerCase() : null);
            map.put("createTime", order.getCreateTime());
            map.put("updateTime", order.getUpdateTime());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getOrderDetail(Long sellerId, Long orderId) {
        Orders order = ordersMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (!sellerId.equals(order.getSellerId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("buyerId", order.getBuyerId());
        result.put("buyerName", buyerMapper.findNameById(order.getBuyerId()));
        result.put("amount", order.getAmount());
        result.put("status", order.getStatus() != null ? order.getStatus().name().toLowerCase() : null);
        result.put("createTime", order.getCreateTime());
        result.put("updateTime", order.getUpdateTime());

        List<OrderItems> items = orderItemsMapper.findByOrderId(orderId);
        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (OrderItems item : items) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("productId", item.getProductId());
            Product product = productMapper.findById(item.getProductId());
            if (product != null) {
                itemMap.put("productName", product.getName());
                itemMap.put("productNo", product.getProductNo());
            }
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("unitPrice", item.getUnitPrice());
            itemMap.put("totalPrice", item.getTotalPrice());
            orderItems.add(itemMap);
        }
        result.put("orderItems", orderItems);

        return result;
    }

    @Override
    public List<Map<String, Object>> queryOrders(Long sellerId, String orderNo, String buyerName, String status, String startTime, String endTime) {
        List<Map<String, Object>> orders = getOrderList(sellerId);
        return orders.stream()
                .filter(o -> orderNo == null || orderNo.isEmpty() || orderNo.equals(o.get("orderNo")))
                .filter(o -> buyerName == null || buyerName.isEmpty() || buyerName.equals(o.get("buyerName")))
                .filter(o -> status == null || status.isEmpty() || status.equals(o.get("status")))
                .filter(o -> startTime == null || startTime.isEmpty() || isAfterOrEqual(o, startTime))
                .filter(o -> endTime == null || endTime.isEmpty() || isBeforeOrEqual(o, endTime))
                .collect(Collectors.toList());
    }
    
    private boolean isAfterOrEqual(Map<String, Object> order, String startTimeStr) {
        Object createTimeObj = order.get("createTime");
        if (createTimeObj == null) return false;
        try {
            java.time.LocalDateTime createTime = (java.time.LocalDateTime) createTimeObj;
            java.time.LocalDate startDate = java.time.LocalDate.parse(startTimeStr);
            return !createTime.toLocalDate().isBefore(startDate);
        } catch (Exception e) {
            log.error("时间比较失败", e);
            return true;
        }
    }
    
    private boolean isBeforeOrEqual(Map<String, Object> order, String endTimeStr) {
        Object createTimeObj = order.get("createTime");
        if (createTimeObj == null) return false;
        try {
            java.time.LocalDateTime createTime = (java.time.LocalDateTime) createTimeObj;
            java.time.LocalDate endDate = java.time.LocalDate.parse(endTimeStr);
            return !createTime.toLocalDate().isAfter(endDate);
        } catch (Exception e) {
            log.error("时间比较失败", e);
            return true;
        }
    }

    @Override
    public void updateOrderStatus(Long sellerId, Long orderId, String status) {
        Orders order = ordersMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (!sellerId.equals(order.getSellerId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        OrdersStatusEnum currentStatus = order.getStatus();
        OrdersStatusEnum newStatus = toOrdersStatusEnum(status);

        if (currentStatus == OrdersStatusEnum.paid && newStatus == OrdersStatusEnum.processing) {
        } else if (currentStatus == OrdersStatusEnum.processing && newStatus == OrdersStatusEnum.shipped) {
        } else if (currentStatus == OrdersStatusEnum.shipped && newStatus == OrdersStatusEnum.delivered) {
        } else if (currentStatus == OrdersStatusEnum.delivered && newStatus == OrdersStatusEnum.completed) {
        } else {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }

        ordersMapper.updateStatus(orderId, status);
    }

    @Override
    public void reportAbnormal(Long sellerId, Long orderId, String abnormalReason) {
        Orders order = ordersMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (!sellerId.equals(order.getSellerId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        OrdersStatusEnum currentStatus = order.getStatus();
        if (currentStatus != OrdersStatusEnum.paid && currentStatus != OrdersStatusEnum.processing 
                && currentStatus != OrdersStatusEnum.shipped && currentStatus != OrdersStatusEnum.delivered) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }

        ordersMapper.updateStatus(orderId, "abnormal");
        log.info("订单异常申报：订单ID={}, 原因={}", orderId, abnormalReason);
    }

    private OrdersStatusEnum toOrdersStatusEnum(String status) {
        if (status == null) return null;
        switch (status.toLowerCase()) {
            case "pending": return OrdersStatusEnum.pending;
            case "paid": return OrdersStatusEnum.paid;
            case "processing": return OrdersStatusEnum.processing;
            case "shipped": return OrdersStatusEnum.shipped;
            case "delivered": return OrdersStatusEnum.delivered;
            case "completed": return OrdersStatusEnum.completed;
            case "cancelled": return OrdersStatusEnum.cancelled;
            case "refunded": return OrdersStatusEnum.refunded;
            case "failed": return OrdersStatusEnum.failed;
            case "abnormal": return OrdersStatusEnum.abnormal;
            default: return null;
        }
    }
}

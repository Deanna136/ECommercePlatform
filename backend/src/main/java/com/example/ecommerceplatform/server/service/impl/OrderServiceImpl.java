package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.pojo.dto.OrderAbnormalDTO;
import com.example.ecommerceplatform.pojo.dto.OrderUpdateInfoDTO;
import com.example.ecommerceplatform.pojo.dto.OrderUpdateStatusDTO;
import com.example.ecommerceplatform.pojo.entity.OrderItems;
import com.example.ecommerceplatform.pojo.entity.Orders;
import com.example.ecommerceplatform.pojo.vo.OrderDetailVO;
import com.example.ecommerceplatform.pojo.vo.OrderItemVO;
import com.example.ecommerceplatform.pojo.vo.OrderVO;
import com.example.ecommerceplatform.server.mapper.OrderItemsMapper;
import com.example.ecommerceplatform.server.mapper.OrdersMapper;
import com.example.ecommerceplatform.server.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrdersMapper ordersMapper;

    private final OrderItemsMapper orderItemsMapper;

    /**
     * 订单列表
     */
    @Override
    public List<OrderVO> list() {

        Long sellerId = BaseContext.getCurrentId();

        List<Orders> ordersList =
                ordersMapper.list(sellerId);

        List<OrderVO> voList = new ArrayList<>();

        for (Orders orders : ordersList) {

            OrderVO vo = OrderVO.builder()
                    .id(orders.getId())
                    .orderNo(orders.getOrderNo())
                    .buyerId(orders.getBuyerId())
                    .buyerName(orders.getBuyerName())
                    .amount(orders.getAmount())
                    .status(
                            orders.getStatus() == null
                                    ? null
                                    : orders.getStatus().name()
                    )
                    .createTime(orders.getCreateTime())
                    .updateTime(orders.getUpdateTime())
                    .build();

            voList.add(vo);
        }

        return voList;
    }

    /**
     * 订单详情
     */
    @Override
    public OrderDetailVO detail(Long id) {

        Long sellerId = BaseContext.getCurrentId();

        Orders orders = ordersMapper.findById(id);

        if (orders == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }

        if (!orders.getSellerId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        List<OrderItems> itemList =
                orderItemsMapper.listByOrderId(id);

        List<OrderItemVO> itemVOList =
                new ArrayList<>();

        for (OrderItems item : itemList) {

            OrderItemVO itemVO = OrderItemVO.builder()
                    .id(item.getId())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .productNo(item.getProductNo())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .totalPrice(item.getTotalPrice())
                    .build();

            itemVOList.add(itemVO);
        }

        return OrderDetailVO.builder()
                .id(orders.getId())
                .orderNo(orders.getOrderNo())
                .buyerId(orders.getBuyerId())
                .buyerName(orders.getBuyerName())
                .buyerPhone(orders.getBuyerPhone())
                .buyerAddress(orders.getBuyerAddress())
                .amount(orders.getAmount())
                .status(
                        orders.getStatus() == null
                                ? null
                                : orders.getStatus().name()
                )
                .createTime(orders.getCreateTime())
                .updateTime(orders.getUpdateTime())
                .orderItems(itemVOList)
                .build();
    }

    /**
     * 条件筛选
     */
    @Override
    public List<OrderVO> query(
            String orderNo,
            Long buyerId,
            String status,
            String startTime,
            String endTime) {

        Long sellerId = BaseContext.getCurrentId();

        List<Orders> ordersList =
                ordersMapper.query(
                        sellerId,
                        orderNo,
                        buyerId,
                        status,
                        startTime,
                        endTime
                );

        List<OrderVO> voList = new ArrayList<>();

        for (Orders orders : ordersList) {

            OrderVO vo = OrderVO.builder()
                    .id(orders.getId())
                    .orderNo(orders.getOrderNo())
                    .buyerId(orders.getBuyerId())
                    .buyerName(orders.getBuyerName())
                    .amount(orders.getAmount())
                    .status(
                            orders.getStatus() == null
                                    ? null
                                    : orders.getStatus().name()
                    )
                    .createTime(orders.getCreateTime())
                    .updateTime(orders.getUpdateTime())
                    .build();

            voList.add(vo);
        }

        return voList;
    }

    /**
     * 修改订单信息
     */
    @Override
    public void updateInfo(Long id,
                           OrderUpdateInfoDTO dto) {

        Long sellerId = BaseContext.getCurrentId();

        Orders orders = ordersMapper.findById(id);

        if (orders == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }

        if (!orders.getSellerId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        String status = orders.getStatus().name();

        if (!status.equals("paid")
                && !status.equals("processing")
                && !status.equals("shipped")
                && !status.equals("delivered")) {

            throw new BusinessException(
                    ErrorCode.ORDER_STATUS_ERROR
            );
        }

        orders.setId(id);
        orders.setBuyerPhone(dto.getBuyerPhone());
        orders.setBuyerAddress(dto.getBuyerAddress());

        ordersMapper.updateInfo(orders);
    }

    /**
     * 修改订单状态
     */
    @Override
    public void updateStatus(Long id,
                             OrderUpdateStatusDTO dto) {

        Long sellerId = BaseContext.getCurrentId();

        Orders orders = ordersMapper.findById(id);

        if (orders == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }

        if (!orders.getSellerId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        String current = orders.getStatus().name();

        String target = dto.getStatus();

        boolean valid = false;

        if (current.equals("paid")
                && target.equals("processing")) {
            valid = true;
        }

        if (current.equals("processing")
                && target.equals("shipped")) {
            valid = true;
        }

        if (current.equals("shipped")
                && target.equals("delivered")) {
            valid = true;
        }

        if (current.equals("delivered")
                && target.equals("completed")) {
            valid = true;
        }

        if (current.equals("abnormal")
                && target.equals("refunded")) {
            valid = true;
        }

        if (!valid) {
            throw new BusinessException(
                    ErrorCode.ORDER_STATUS_ERROR
            );
        }

        ordersMapper.updateStatus(id, target);
    }

    /**
     * 异常申报
     */
    @Override
    public void reportAbnormal(Long id,
                               OrderAbnormalDTO dto) {

        Long sellerId = BaseContext.getCurrentId();

        Orders orders = ordersMapper.findById(id);

        if (orders == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }

        if (!orders.getSellerId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        String status = orders.getStatus().name();

        if (!status.equals("paid")
                && !status.equals("processing")
                && !status.equals("shipped")
                && !status.equals("delivered")) {

            throw new BusinessException(
                    ErrorCode.ORDER_STATUS_ERROR
            );
        }

        if (dto.getReason() == null
                || dto.getReason().trim().isEmpty()) {

            throw new BusinessException(
                    ErrorCode.PARAM_ERROR
            );
        }

        ordersMapper.reportAbnormal(
                id,
                dto.getReason()
        );
    }
}
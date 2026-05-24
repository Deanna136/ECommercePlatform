package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.pojo.dto.OrderQueryDTO;
import com.example.ecommerceplatform.pojo.entity.*;
import com.example.ecommerceplatform.pojo.vo.OrderDetailVO;
import com.example.ecommerceplatform.pojo.vo.OrderItemVO;
import com.example.ecommerceplatform.pojo.vo.OrderVO;
import com.example.ecommerceplatform.server.mapper.*;
import com.example.ecommerceplatform.server.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private OrderItemsMapper orderItemsMapper;
    @Resource
    private BuyerMapper buyerMapper;
    @Resource
    private SellerMapper sellerMapper;
    @Resource
    private ProductMapper productMapper;

    private static final Set<String> FINAL_STATUSES = new HashSet<>(Arrays.asList(
            "completed", "cancelled", "refunded", "failed"
    ));

    @Override
    public List<OrderVO> getAll() {
        List<Orders> orders = ordersMapper.getAll();
        List<OrderVO> result = new ArrayList<>();
        for (Orders o : orders) {
            result.add(toOrderVO(o));
        }
        return result;
    }

    @Override
    public OrderDetailVO getById(Long id) {
        Orders order = ordersMapper.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        OrderDetailVO vo = toOrderDetailBase(order);

        List<OrderItems> items = orderItemsMapper.getByOrderId(id);
        List<OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItems item : items) {
            Product product = productMapper.getById(item.getProductId());
            itemVOs.add(OrderItemVO.builder()
                    .id(item.getId())
                    .productId(item.getProductId())
                    .productName(product != null ? product.getName() : null)
                    .productNo(product != null ? product.getProductNo() : null)
                    .quantity(item.getQuantity())
                    .unitPrice((double) item.getUnitPrice())
                    .totalPrice((double) item.getTotalPrice())
                    .build());
        }
        vo.setOrderItems(itemVOs);
        return vo;
    }

    @Override
    public List<OrderVO> query(OrderQueryDTO dto) {
        List<Orders> orders = ordersMapper.query(dto);
        List<OrderVO> result = new ArrayList<>();
        for (Orders o : orders) {
            result.add(toOrderVO(o));
        }
        return result;
    }

    @Override
    public void forceClose(Long id) {
        Orders order = ordersMapper.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        String status = order.getStatus() != null ? order.getStatus().name() : null;
        if (FINAL_STATUSES.contains(status)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        ordersMapper.updateStatus(id, "cancelled");
    }

    @Override
    public void handleAbnormal(Long id, String handleResult) {
        Orders order = ordersMapper.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        String status = order.getStatus() != null ? order.getStatus().name() : null;
        if (!"abnormal".equals(status)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        if (handleResult == null || !Arrays.asList("refunded", "completed", "cancelled").contains(handleResult)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        ordersMapper.updateStatus(id, handleResult);
    }

    private OrderVO toOrderVO(Orders o) {
        String buyerName = null;
        String sellerName = null;
        Buyer buyer = buyerMapper.getById(o.getBuyerId());
        if (buyer != null) buyerName = buyer.getName();
        Seller seller = sellerMapper.getById(o.getSellerId());
        if (seller != null) sellerName = seller.getName();

        return OrderVO.builder()
                .id(o.getId())
                .orderNo(o.getOrderNo())
                .buyerId(o.getBuyerId())
                .buyerName(buyerName)
                .sellerId(o.getSellerId())
                .sellerName(sellerName)
                .amount((double) o.getAmount())
                .status(o.getStatus() != null ? o.getStatus().name() : null)
                .createTime(o.getCreateTime())
                .updateTime(o.getUpdateTime())
                .build();
    }

    private OrderDetailVO toOrderDetailBase(Orders o) {
        String buyerName = null;
        String sellerName = null;
        Buyer buyer = buyerMapper.getById(o.getBuyerId());
        if (buyer != null) buyerName = buyer.getName();
        Seller seller = sellerMapper.getById(o.getSellerId());
        if (seller != null) sellerName = seller.getName();

        return OrderDetailVO.builder()
                .id(o.getId())
                .orderNo(o.getOrderNo())
                .buyerId(o.getBuyerId())
                .buyerName(buyerName)
                .sellerId(o.getSellerId())
                .sellerName(sellerName)
                .amount((double) o.getAmount())
                .status(o.getStatus() != null ? o.getStatus().name() : null)
                .createTime(o.getCreateTime())
                .updateTime(o.getUpdateTime())
                .build();
    }
}

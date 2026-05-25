package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderCreateDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderItemDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderQueryDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderRefundDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderReportDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderUpdateDTO;
import com.example.ecommerceplatform.pojo.entity.OrderItems;
import com.example.ecommerceplatform.pojo.entity.Orders;
import com.example.ecommerceplatform.pojo.entity.Product;
import com.example.ecommerceplatform.pojo.entity.Seller;
import com.example.ecommerceplatform.pojo.vo.BuyerOrderCreateVO;
import com.example.ecommerceplatform.pojo.vo.BuyerOrderDetailVO;
import com.example.ecommerceplatform.pojo.vo.BuyerOrderVO;
import com.example.ecommerceplatform.pojo.vo.OrderItemVO;
import com.example.ecommerceplatform.server.mapper.OrderItemsMapper;
import com.example.ecommerceplatform.server.mapper.OrdersMapper;
import com.example.ecommerceplatform.server.mapper.ProductMapper;
import com.example.ecommerceplatform.server.mapper.SellerMapper;
import com.example.ecommerceplatform.server.service.BuyerOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BuyerOrderServiceImpl implements BuyerOrderService {
    private static final Set<String> UPDATE_ALLOWED = new HashSet<>(Arrays.asList("paid", "processing"));
    private static final Set<String> REPORT_ALLOWED = new HashSet<>(Arrays.asList("paid", "processing", "shipped", "delivered"));
    private static final Set<String> FINAL_STATUSES = new HashSet<>(Arrays.asList("completed", "cancelled", "refunded", "failed"));

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private OrderItemsMapper orderItemsMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private SellerMapper sellerMapper;

    @Override
    public List<BuyerOrderVO> list() {
        Long buyerId = BaseContext.getCurrentId();
        List<Orders> orders = ordersMapper.getByBuyerId(buyerId);
        List<BuyerOrderVO> result = new ArrayList<>();
        for (Orders order : orders) {
            result.add(toOrderVO(order));
        }
        return result;
    }

    @Override
    public BuyerOrderDetailVO getById(Long id) {
        Orders order = ordersMapper.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        validateOwnership(order.getBuyerId());

        BuyerOrderDetailVO detail = toOrderDetailVO(order);
        List<OrderItems> items = orderItemsMapper.getByOrderId(order.getId());
        List<OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItems item : items) {
            Product product = productMapper.getById(item.getProductId());
            itemVOs.add(OrderItemVO.builder()
                    .id(item.getId())
                    .productId(item.getProductId())
                    .productName(product != null ? product.getName() : null)
                    .productNo(product != null ? product.getProductNo() : null)
                    .quantity(item.getQuantity())
                    .unitPrice(BigDecimal.valueOf(item.getUnitPrice()).setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .totalPrice(BigDecimal.valueOf(item.getTotalPrice()).setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .build());
        }
        detail.setOrderItems(itemVOs);
        return detail;
    }

    @Override
    public List<BuyerOrderVO> query(BuyerOrderQueryDTO dto) {
        BuyerOrderQueryDTO safe = dto == null ? new BuyerOrderQueryDTO() : dto;
        OrderQueryProxy proxy = new OrderQueryProxy();
        proxy.setOrderNo(safe.getOrderNo());
        proxy.setSellerId(safe.getSellerId());
        proxy.setStatus(safe.getStatus());
        proxy.setStartTime(safe.getStartTime());
        proxy.setEndTime(safe.getEndTime());
        proxy.setBuyerId(BaseContext.getCurrentId());

        List<Orders> orders = ordersMapper.query(proxy);
        List<BuyerOrderVO> result = new ArrayList<>();
        for (Orders order : orders) {
            result.add(toOrderVO(order));
        }
        return result;
    }

    @Override
    @Transactional
    public BuyerOrderCreateVO create(BuyerOrderCreateDTO dto) {
        if (dto == null || dto.getOrderItems() == null || dto.getOrderItems().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        if (dto.getPhone() == null || dto.getPhone().isEmpty() || dto.getAddress() == null || dto.getAddress().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        Long buyerId = BaseContext.getCurrentId();
        Long sellerId = null;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItems> itemsToInsert = new ArrayList<>();

        for (BuyerOrderItemDTO itemDTO : dto.getOrderItems()) {
            if (itemDTO.getProductId() == null || itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                throw new BusinessException(ErrorCode.CART_QUANTITY_ERROR);
            }

            Product product = productMapper.getById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
            }
            String status = product.getStatus() != null ? product.getStatus().name() : null;
            if (!"onsale".equals(status)) {
                if ("suspend".equals(status)) {
                    throw new BusinessException(ErrorCode.PRODUCT_SUSPEND);
                }
                throw new BusinessException(ErrorCode.PRODUCT_OFF_SALE);
            }

            if (sellerId == null) {
                sellerId = product.getSellerId();
            } else if (!sellerId.equals(product.getSellerId())) {
                throw new BusinessException(ErrorCode.PARAM_ERROR);
            }

            int quantity = itemDTO.getQuantity();
            int affected = productMapper.decreaseSku(product.getId(), quantity);
            if (affected == 0) {
                throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH);
            }

            BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemTotal);

            OrderItems orderItems = new OrderItems();
            orderItems.setProductId(product.getId());
            orderItems.setQuantity(quantity);
            orderItems.setUnitPrice(unitPrice.floatValue());
            orderItems.setTotalPrice(itemTotal.floatValue());
            itemsToInsert.add(orderItems);
        }

        Orders orders = new Orders();
        orders.setBuyerId(buyerId);
        orders.setSellerId(sellerId);
        orders.setAmount(totalAmount.setScale(2, RoundingMode.HALF_UP).floatValue());
        orders.setStatus(com.example.ecommerceplatform.common.enumeration.OrdersStatusEnum.paid);
        orders.setPhone(dto.getPhone());
        orders.setAddress(dto.getAddress());
        ordersMapper.insert(orders);

        String orderNo = buildOrderNo(orders.getId());
        ordersMapper.updateOrderNo(orders.getId(), orderNo);

        for (OrderItems item : itemsToInsert) {
            item.setOrderId(orders.getId());
            orderItemsMapper.insert(item);
        }

        return BuyerOrderCreateVO.builder()
                .id(orders.getId())
                .orderNo(orderNo)
                .amount(totalAmount.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .status("paid")
                .createTime(orders.getCreateTime())
                .build();
    }

    @Override
    public void refund(Long id, BuyerOrderRefundDTO dto) {
        Orders order = ordersMapper.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        validateOwnership(order.getBuyerId());

        if (dto == null || dto.getReason() == null || dto.getReason().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        String status = order.getStatus() != null ? order.getStatus().name() : null;
        if (!"paid".equals(status)) {
            if ("shipped".equals(status) || "delivered".equals(status) || "completed".equals(status)) {
                throw new BusinessException(ErrorCode.ORDER_CANCEL_ERROR);
            }
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        ordersMapper.updateStatus(id, "abnormal");
    }

    @Override
    public void updateInfo(Long id, BuyerOrderUpdateDTO dto) {
        Orders order = ordersMapper.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        validateOwnership(order.getBuyerId());

        String status = order.getStatus() != null ? order.getStatus().name() : null;
        if (!UPDATE_ALLOWED.contains(status)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }

        String phone = dto != null ? dto.getPhone() : null;
        String address = dto != null ? dto.getAddress() : null;
        if ((phone == null || phone.isEmpty()) && (address == null || address.isEmpty())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        if (phone == null || phone.isEmpty()) {
            phone = order.getPhone();
        }
        if (address == null || address.isEmpty()) {
            address = order.getAddress();
        }
        ordersMapper.updateContact(id, phone, address);
    }

    @Override
    public void reportAbnormal(Long id, BuyerOrderReportDTO dto) {
        Orders order = ordersMapper.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        validateOwnership(order.getBuyerId());

        if (dto == null || dto.getReason() == null || dto.getReason().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        String status = order.getStatus() != null ? order.getStatus().name() : null;
        if (!REPORT_ALLOWED.contains(status) || "abnormal".equals(status) || FINAL_STATUSES.contains(status)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        ordersMapper.updateStatus(id, "abnormal");
    }

    private void validateOwnership(Long buyerId) {
        Long currentBuyerId = BaseContext.getCurrentId();
        if (!currentBuyerId.equals(buyerId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
    }

    private String buildOrderNo(Long id) {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String suffix = String.format("%05d", id);
        return "ORD" + date + suffix;
    }

    private BuyerOrderVO toOrderVO(Orders order) {
        Seller seller = sellerMapper.getById(order.getSellerId());
        return BuyerOrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .sellerId(order.getSellerId())
                .sellerName(seller != null ? seller.getName() : null)
                .storeName(seller != null ? seller.getStoreName() : null)
                .amount(BigDecimal.valueOf(order.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .build();
    }

    private BuyerOrderDetailVO toOrderDetailVO(Orders order) {
        Seller seller = sellerMapper.getById(order.getSellerId());
        return BuyerOrderDetailVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .sellerId(order.getSellerId())
                .sellerName(seller != null ? seller.getName() : null)
                .storeName(seller != null ? seller.getStoreName() : null)
                .amount(BigDecimal.valueOf(order.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .phone(order.getPhone())
                .address(order.getAddress())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .build();
    }

    private static class OrderQueryProxy extends com.example.ecommerceplatform.pojo.dto.OrderQueryDTO {
        private Long buyerId;

        public void setBuyerId(Long buyerId) {
            this.buyerId = buyerId;
        }

        @Override
        public Long getBuyerId() {
            return buyerId;
        }
    }
}

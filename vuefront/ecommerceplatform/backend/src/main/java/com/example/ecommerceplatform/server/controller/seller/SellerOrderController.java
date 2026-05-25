package com.example.ecommerceplatform.server.controller.seller;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.server.service.SellerOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/seller/order")
@Api(tags = "卖家端 - 订单管理")
@Slf4j
public class SellerOrderController {

    @Autowired
    private SellerOrderService sellerOrderService;

    @GetMapping("/list")
    @ApiOperation("查看订单列表")
    public Result<List<Map<String, Object>>> getOrderList() {
        Long sellerId = BaseContext.getCurrentId();
        log.info("卖家查看订单列表：sellerId={}", sellerId);
        List<Map<String, Object>> orders = sellerOrderService.getOrderList(sellerId);
        return Result.success(orders);
    }

    @GetMapping("/{id}")
    @ApiOperation("查看订单详情")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        Long sellerId = BaseContext.getCurrentId();
        log.info("卖家查看订单详情：sellerId={}, orderId={}", sellerId, id);
        Map<String, Object> order = sellerOrderService.getOrderDetail(sellerId, id);
        return Result.success(order);
    }

    @GetMapping("/query")
    @ApiOperation("订单条件筛选")
    public Result<List<Map<String, Object>>> queryOrders(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String buyerName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Long sellerId = BaseContext.getCurrentId();
        log.info("卖家筛选订单：sellerId={}", sellerId);
        List<Map<String, Object>> orders = sellerOrderService.queryOrders(sellerId, orderNo, buyerName, status, startTime, endTime);
        return Result.success(orders);
    }

    @PutMapping("/{id}/updateStatus")
    @ApiOperation("修改订单状态")
    public Result<String> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long sellerId = BaseContext.getCurrentId();
        String status = body.get("status");
        log.info("卖家修改订单状态：sellerId={}, orderId={}, status={}", sellerId, id, status);
        sellerOrderService.updateOrderStatus(sellerId, id, status);
        return Result.success("订单状态更新成功");
    }

    @PutMapping("/{id}/reportAbnormal")
    @ApiOperation("订单异常申报")
    public Result<String> reportAbnormal(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long sellerId = BaseContext.getCurrentId();
        String abnormalReason = body.get("abnormalReason");
        log.info("卖家申报订单异常：sellerId={}, orderId={}, reason={}", sellerId, id, abnormalReason);
        sellerOrderService.reportAbnormal(sellerId, id, abnormalReason);
        return Result.success("异常申报成功");
    }
}

package com.example.ecommerceplatform.server.controller.admin;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.pojo.dto.AdminHandleDTO;
import com.example.ecommerceplatform.pojo.dto.OrderQueryDTO;
import com.example.ecommerceplatform.pojo.vo.OrderDetailVO;
import com.example.ecommerceplatform.pojo.vo.OrderVO;
import com.example.ecommerceplatform.server.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Api(tags = "管理员-订单管理")
public class AdminOrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/order/list")
    @ApiOperation("查看订单列表")
    public Result<List<OrderVO>> list() {
        return Result.success(orderService.getAll());
    }

    @GetMapping("/order/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderDetailVO> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @GetMapping("/order/query")
    @ApiOperation("订单条件筛选")
    public Result<List<OrderVO>> query(OrderQueryDTO dto) {
        return Result.success(orderService.query(dto));
    }

    @PutMapping("/order/{id}/forceClose")
    @ApiOperation("强制关闭订单")
    public Result<String> forceClose(@PathVariable Long id, @RequestBody(required = false) AdminHandleDTO dto) {
        orderService.forceClose(id);
        return Result.success("订单已强制关闭");
    }

    @PutMapping("/order/{id}/handleAbnormal")
    @ApiOperation("订单异常处理")
    public Result<String> handleAbnormal(@PathVariable Long id, @RequestBody AdminHandleDTO dto) {
        String handleResult = dto != null ? dto.getHandleResult() : null;
        orderService.handleAbnormal(id, handleResult);
        return Result.success("订单异常处理完成");
    }
}

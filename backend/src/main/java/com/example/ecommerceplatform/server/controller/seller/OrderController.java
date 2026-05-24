package com.example.ecommerceplatform.server.controller.seller;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.pojo.dto.OrderAbnormalDTO;
import com.example.ecommerceplatform.pojo.dto.OrderUpdateInfoDTO;
import com.example.ecommerceplatform.pojo.dto.OrderUpdateStatusDTO;
import com.example.ecommerceplatform.pojo.vo.OrderDetailVO;
import com.example.ecommerceplatform.pojo.vo.OrderVO;
import com.example.ecommerceplatform.server.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller/order")
@RequiredArgsConstructor
@Api(tags = "卖家订单接口")
public class OrderController {

    private final OrderService orderService;

    /**
     * 订单列表
     */
    @GetMapping("/list")
    @ApiOperation("订单列表")
    public Result<List<OrderVO>> list() {

        return Result.success(orderService.list());
    }

    /**
     * 订单详情
     */
    @GetMapping("/{id}")
    @ApiOperation("订单详情")
    public Result<OrderDetailVO> detail(
            @PathVariable Long id) {

        return Result.success(orderService.detail(id));
    }

    /**
     * 条件筛选
     */
    @GetMapping("/query")
    @ApiOperation("订单条件筛选")
    public Result<List<OrderVO>> query(
            String orderNo,
            Long buyerId,
            String status,
            String startTime,
            String endTime) {

        return Result.success(
                orderService.query(
                        orderNo,
                        buyerId,
                        status,
                        startTime,
                        endTime
                )
        );
    }

    /**
     * 修改订单信息
     */
    @PutMapping("/{id}/updateInfo")
    @ApiOperation("修改订单信息")
    public Result<String> updateInfo(
            @PathVariable Long id,
            @RequestBody OrderUpdateInfoDTO dto) {

        orderService.updateInfo(id, dto);

        return Result.success("订单信息更新成功");
    }

    /**
     * 修改订单状态
     */
    @PutMapping("/{id}/updateStatus")
    @ApiOperation("修改订单状态")
    public Result<String> updateStatus(
            @PathVariable Long id,
            @RequestBody OrderUpdateStatusDTO dto) {

        orderService.updateStatus(id, dto);

        return Result.success("订单状态更新成功");
    }

    /**
     * 异常申报
     */
    @PutMapping("/{id}/reportAbnormal")
    @ApiOperation("订单异常申报")
    public Result<String> reportAbnormal(
            @PathVariable Long id,
            @RequestBody OrderAbnormalDTO dto) {

        orderService.reportAbnormal(id, dto);

        return Result.success("订单异常已申报，请等待管理员处理");
    }
}
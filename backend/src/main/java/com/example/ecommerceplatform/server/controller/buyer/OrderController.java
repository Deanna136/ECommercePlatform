package com.example.ecommerceplatform.server.controller.buyer;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderCreateDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderQueryDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderRefundDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderReportDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderUpdateDTO;
import com.example.ecommerceplatform.pojo.vo.BuyerOrderCreateVO;
import com.example.ecommerceplatform.pojo.vo.BuyerOrderDetailVO;
import com.example.ecommerceplatform.pojo.vo.BuyerOrderVO;
import com.example.ecommerceplatform.server.service.BuyerOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/buyer/order")
@Slf4j
@Api(tags = "买家端订单相关接口")
public class OrderController {

    @Resource
    private BuyerOrderService buyerOrderService;

    @GetMapping("/list")
    @ApiOperation("查看订单列表")
    public Result<List<BuyerOrderVO>> list() {
        log.info("查看订单列表");
        return Result.success(buyerOrderService.list());
    }

    @GetMapping("/{id}")
    @ApiOperation("查看订单详情")
    public Result<BuyerOrderDetailVO> detail(@PathVariable Long id) {
        log.info("查看订单详情: {}", id);
        return Result.success(buyerOrderService.getById(id));
    }

    @GetMapping("/query")
    @ApiOperation("订单条件筛选")
    public Result<List<BuyerOrderVO>> query(BuyerOrderQueryDTO dto) {
        log.info("订单条件筛选: {}", dto);
        return Result.success(buyerOrderService.query(dto));
    }

    @PostMapping("/create")
    @ApiOperation("下单")
    public Result<BuyerOrderCreateVO> create(@RequestBody BuyerOrderCreateDTO dto) {
        log.info("下单: {}", dto);
        return Result.success(buyerOrderService.create(dto));
    }

    @PutMapping("/{id}/refund")
    @ApiOperation("退单")
    public Result<String> refund(@PathVariable Long id, @RequestBody BuyerOrderRefundDTO dto) {
        log.info("退单: {}, {}", id, dto);
        buyerOrderService.refund(id, dto);
        return Result.success("退单申请已提交，等待卖家审核");
    }

    @PutMapping("/{id}/updateInfo")
    @ApiOperation("修改订单信息")
    public Result<String> updateInfo(@PathVariable Long id, @RequestBody BuyerOrderUpdateDTO dto) {
        log.info("修改订单信息: {}, {}", id, dto);
        buyerOrderService.updateInfo(id, dto);
        return Result.success("订单信息更新成功");
    }

    @PutMapping("/{id}/reportAbnormal")
    @ApiOperation("订单异常申报")
    public Result<String> reportAbnormal(@PathVariable Long id, @RequestBody BuyerOrderReportDTO dto) {
        log.info("订单异常申报: {}, {}", id, dto);
        buyerOrderService.reportAbnormal(id, dto);
        return Result.success("订单异常已申报，请等待管理员处理");
    }
}

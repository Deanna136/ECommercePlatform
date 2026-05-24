package com.example.ecommerceplatform.server.controller.admin;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.pojo.dto.BuyerQueryDTO;
import com.example.ecommerceplatform.pojo.vo.BuyerVO;
import com.example.ecommerceplatform.server.service.BuyerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Api(tags = "管理员-买家管理")
public class AdminBuyerController {
    @Resource
    private BuyerService buyerService;

    @GetMapping("/buyer/list")
    @ApiOperation("查看买家列表")
    public Result<List<BuyerVO>> list() {
        return Result.success(buyerService.getAll());
    }

    @GetMapping("/buyer/{id}")
    @ApiOperation("查看买家详情")
    public Result<BuyerVO> getById(@PathVariable Long id) {
        return Result.success(buyerService.getById(id));
    }

    @GetMapping("/buyer/query")
    @ApiOperation("买家条件筛选")
    public Result<List<BuyerVO>> query(BuyerQueryDTO dto) {
        return Result.success(buyerService.query(dto));
    }

    @PutMapping("/buyer/{id}/ban")
    @ApiOperation("封禁买家账号")
    public Result<String> ban(@PathVariable Long id) {
        buyerService.ban(id);
        return Result.success("封禁成功");
    }

    @PutMapping("/buyer/{id}/unban")
    @ApiOperation("解封买家账号")
    public Result<String> unban(@PathVariable Long id) {
        buyerService.unban(id);
        return Result.success("解封成功");
    }
}

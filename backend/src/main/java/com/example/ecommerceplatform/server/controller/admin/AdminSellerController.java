package com.example.ecommerceplatform.server.controller.admin;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.pojo.dto.SellerQueryDTO;
import com.example.ecommerceplatform.pojo.vo.SellerVO;
import com.example.ecommerceplatform.server.service.SellerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Api(tags = "管理员-卖家管理")
public class AdminSellerController {
    @Resource
    private SellerService sellerService;

    @GetMapping("/seller/list")
    @ApiOperation("查看卖家列表")
    public Result<List<SellerVO>> list() {
        return Result.success(sellerService.getAll());
    }

    @GetMapping("/seller/{id}")
    @ApiOperation("查看卖家详情")
    public Result<SellerVO> getById(@PathVariable Long id) {
        return Result.success(sellerService.getById(id));
    }

    @GetMapping("/seller/query")
    @ApiOperation("卖家条件筛选")
    public Result<List<SellerVO>> query(SellerQueryDTO dto) {
        return Result.success(sellerService.query(dto));
    }

    @PutMapping("/seller/{id}/ban")
    @ApiOperation("封禁卖家账号")
    public Result<String> ban(@PathVariable Long id) {
        sellerService.ban(id);
        return Result.success("封禁成功");
    }

    @PutMapping("/seller/{id}/unban")
    @ApiOperation("解封卖家账号")
    public Result<String> unban(@PathVariable Long id) {
        sellerService.unban(id);
        return Result.success("解封成功");
    }
}

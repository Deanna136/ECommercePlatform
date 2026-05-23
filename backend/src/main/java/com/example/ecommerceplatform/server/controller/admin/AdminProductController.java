package com.example.ecommerceplatform.server.controller.admin;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.pojo.dto.AdminHandleDTO;
import com.example.ecommerceplatform.pojo.dto.ProductQueryDTO;
import com.example.ecommerceplatform.pojo.vo.ProductVO;
import com.example.ecommerceplatform.server.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Api(tags = "管理员-商品管理")
public class AdminProductController {
    @Resource
    private ProductService productService;

    @GetMapping("/product/list")
    @ApiOperation("查看商品列表")
    public Result<List<ProductVO>> list() {
        return Result.success(productService.getAll());
    }

    @GetMapping("/product/{id}")
    @ApiOperation("查看商品详情")
    public Result<ProductVO> getById(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @GetMapping("/product/query")
    @ApiOperation("商品条件筛选")
    public Result<List<ProductVO>> query(ProductQueryDTO dto) {
        return Result.success(productService.query(dto));
    }

    @PutMapping("/product/{id}/approve")
    @ApiOperation("审核通过-商品上架")
    public Result<String> approve(@PathVariable Long id) {
        productService.approve(id);
        return Result.success("审核通过，商品已上架");
    }

    @PutMapping("/product/{id}/reject")
    @ApiOperation("审核拒绝")
    public Result<String> reject(@PathVariable Long id, @RequestBody(required = false) AdminHandleDTO dto) {
        productService.reject(id);
        return Result.success("审核拒绝");
    }

    @PutMapping("/product/{id}/suspend")
    @ApiOperation("强制下架商品")
    public Result<String> suspend(@PathVariable Long id, @RequestBody(required = false) AdminHandleDTO dto) {
        productService.suspend(id);
        return Result.success("商品已强制下架");
    }
}

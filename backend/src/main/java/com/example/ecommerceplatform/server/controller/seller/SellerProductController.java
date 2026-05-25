package com.example.ecommerceplatform.server.controller.seller;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.pojo.dto.SellerProductAddDTO;
import com.example.ecommerceplatform.pojo.dto.SellerProductUpdateDTO;
import com.example.ecommerceplatform.pojo.entity.Product;
import com.example.ecommerceplatform.server.service.SellerProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/seller/product")
@Api(tags = "卖家端 - 商品管理")
@Slf4j
public class SellerProductController {

    @Autowired
    private SellerProductService sellerProductService;

    @GetMapping("/list")
    @ApiOperation("查看商品列表")
    public Result<List<Product>> getProductList() {
        Long sellerId = BaseContext.getCurrentId();
        log.info("卖家查看商品列表：sellerId={}", sellerId);
        List<Product> products = sellerProductService.getProductList(sellerId);
        return Result.success(products);
    }

    @GetMapping("/{id}")
    @ApiOperation("查看商品详情")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Long sellerId = BaseContext.getCurrentId();
        log.info("卖家查看商品详情：sellerId={}, productId={}", sellerId, id);
        Product product = sellerProductService.getProductDetail(sellerId, id);
        return Result.success(product);
    }

    @GetMapping("/query")
    @ApiOperation("商品条件筛选")
    public Result<List<Product>> queryProducts(
            @RequestParam(required = false) String productNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Float minPrice,
            @RequestParam(required = false) Float maxPrice) {
        Long sellerId = BaseContext.getCurrentId();
        log.info("卖家筛选商品：sellerId={}", sellerId);
        List<Product> products = sellerProductService.queryProducts(sellerId, productNo, name, category, status, minPrice, maxPrice);
        return Result.success(products);
    }

    @PostMapping("/add")
    @ApiOperation("新增商品")
    public Result<Map<String, Object>> addProduct(@RequestBody @Valid SellerProductAddDTO dto) {
        Long sellerId = BaseContext.getCurrentId();
        log.info("卖家新增商品：sellerId={}, productNo={}", sellerId, dto.getProductNo());
        Product product = sellerProductService.addProduct(sellerId, dto);
        Map<String, Object> result = new HashMap<>();
        result.put("id", product.getId());
        result.put("productNo", product.getProductNo());
        result.put("name", product.getName());
        result.put("status", product.getStatus() != null ? product.getStatus().name().toLowerCase() : null);
        result.put("createTime", product.getCreateTime());
        return Result.success(result);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改商品信息")
    public Result<String> updateProduct(@PathVariable Long id, @RequestBody @Valid SellerProductUpdateDTO dto) {
        Long sellerId = BaseContext.getCurrentId();
        log.info("卖家修改商品：sellerId={}, productId={}", sellerId, id);
        dto.setId(id);
        sellerProductService.updateProduct(sellerId, id, dto);
        return Result.success("商品信息更新成功");
    }

    @PutMapping("/{id}/stopSale")
    @ApiOperation("停售商品")
    public Result<String> stopSale(@PathVariable Long id) {
        Long sellerId = BaseContext.getCurrentId();
        log.info("卖家停售商品：sellerId={}, productId={}", sellerId, id);
        sellerProductService.stopSale(sellerId, id);
        return Result.success("商品已停售");
    }
}

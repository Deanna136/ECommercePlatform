package com.example.ecommerceplatform.server.controller.buyer;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.pojo.dto.BuyerProductQueryDTO;
import com.example.ecommerceplatform.pojo.vo.ProductVO;
import com.example.ecommerceplatform.server.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/buyer/product")
@Slf4j
@Api(tags = "买家端商品相关接口")
public class ProductController {

    @Resource
    private ProductService productService;

    @GetMapping("/list")
    @ApiOperation("查看商品列表")
    public Result<List<ProductVO>> list() {
        log.info("查看商品列表");
        List<ProductVO> list = productService.listOnsale();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @ApiOperation("查看商品详情")
    public Result<ProductVO> getById(@PathVariable Long id) {
        log.info("查看商品详情：{}", id);
        ProductVO productVO = productService.getOnsaleById(id);
        return Result.success(productVO);
    }

    @GetMapping("/query")
    @ApiOperation("商品条件筛选")
    public Result<List<ProductVO>> query(BuyerProductQueryDTO buyerProductQueryDTO) {
        log.info("商品条件筛选：{}", buyerProductQueryDTO);
        List<ProductVO> list = productService.queryOnsale(buyerProductQueryDTO);
        return Result.success(list);
    }
}

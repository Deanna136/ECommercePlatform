package com.example.ecommerceplatform.server.controller.seller;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.pojo.dto.ProductDTO;
import com.example.ecommerceplatform.pojo.vo.ProductVO;
import com.example.ecommerceplatform.server.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/seller/product")
@Api(tags = "卖家端 - 商品管理")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 新增商品
     */
    @PostMapping("/add")
    @ApiOperation("新增商品")
    public Result<String> add(@RequestBody @Valid ProductDTO dto) {

        log.info("新增商品：{}", dto.getName());

        productService.add(dto);

        return Result.success("新增商品成功");
    }

    /**
     * 修改商品
     */
    @PutMapping("/update")
    @ApiOperation("修改商品")
    public Result<String> update(@RequestBody @Valid ProductDTO dto) {

        log.info("修改商品：{}", dto.getId());

        productService.update(dto);

        return Result.success("修改商品成功");
    }

    /**
     * 商品详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation("商品详情")
    public Result<ProductVO> detail(@PathVariable Long id) {

        ProductVO vo = productService.detail(id);

        return Result.success(vo);
    }

    /**
     * 商品列表
     */
    @GetMapping("/list")
    @ApiOperation("商品列表")
    public Result<List<ProductVO>> list() {

        List<ProductVO> list = productService.list();

        return Result.success(list);
    }

    /**
     * 修改商品状态
     */
    @PutMapping("/status/{id}")
    @ApiOperation("修改商品状态")
    public Result<String> updateStatus(@PathVariable Long id,
                                       @RequestParam String status) {

        productService.updateStatus(id, status);

        return Result.success("状态修改成功");
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除商品")
    public Result<String> delete(@PathVariable Long id) {

        productService.delete(id);

        return Result.success("删除成功");
    }

    /**
     * 停售商品
     */
    @PutMapping("/{id}/stopSale")
    @ApiOperation("停售商品")
    public Result<String> stopSale(@PathVariable Long id) {

        productService.stopSale(id);

        return Result.success("商品已停售");
    }

    /**
     * 商品条件筛选
     */
    @GetMapping("/query")
    @ApiOperation("商品条件筛选")
    public Result<List<ProductVO>> search(
            String name,
            String category,
            String status) {

        List<ProductVO> list =
                productService.search(
                        name,
                        category,
                        status
                );

        return Result.success(list);
    }

}
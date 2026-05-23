package com.example.ecommerceplatform.server.controller.buyer;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.pojo.dto.CartAddDTO;
import com.example.ecommerceplatform.pojo.dto.CartUpdateDTO;
import com.example.ecommerceplatform.pojo.vo.CartVO;
import com.example.ecommerceplatform.server.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/buyer/cart")
@Slf4j
@Api(tags = "买家端购物车相关接口")
public class CartController {

    @Resource
    private CartService cartService;

    @GetMapping
    @ApiOperation("查询购物车商品")
    public Result<CartVO> getCart() {
        log.info("查询购物车商品");
        CartVO cartVO = cartService.getCart();
        return Result.success(cartVO);
    }

    @PostMapping("/add")
    @ApiOperation("加入购物车")
    public Result<String> add(@RequestBody CartAddDTO cartAddDTO) {
        log.info("加入购物车：{}", cartAddDTO);
        cartService.addItem(cartAddDTO);
        return Result.success("已加入购物车");
    }

    @PutMapping("/update")
    @ApiOperation("修改购物车中商品信息")
    public Result<String> update(@RequestBody CartUpdateDTO cartUpdateDTO) {
        log.info("修改购物车商品：{}", cartUpdateDTO);
        cartService.updateItem(cartUpdateDTO);
        return Result.success("购物车已更新");
    }

    @DeleteMapping("/remove/{cartItemId}")
    @ApiOperation("移出购物车")
    public Result<String> remove(@PathVariable Long cartItemId) {
        log.info("移出购物车：{}", cartItemId);
        cartService.removeItem(cartItemId);
        return Result.success("商品已从购物车移出");
    }
}

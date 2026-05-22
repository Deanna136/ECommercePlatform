package com.example.ecommerceplatform.server.controller.seller;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.pojo.dto.SellerLoginDTO;
import com.example.ecommerceplatform.pojo.dto.SellerRegisterDTO;
import com.example.ecommerceplatform.pojo.dto.SellerUpdateInfoDTO;
import com.example.ecommerceplatform.pojo.vo.SellerLoginVO;
import com.example.ecommerceplatform.server.service.SellerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/seller")
@Api(tags = "卖家端 - 用户管理")
@Slf4j
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @PostMapping("/register")
    @ApiOperation("卖家注册")
    public Result<String> register(@RequestBody @Valid SellerRegisterDTO dto) {
        log.info("卖家注册：{}", dto.getUserName());
        sellerService.register(dto);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    @ApiOperation("卖家登录")
    public Result<SellerLoginVO> login(@RequestBody @Valid SellerLoginDTO dto) {
        log.info("卖家登录：{}", dto.getUserName());
        SellerLoginVO vo = sellerService.login(dto);
        return Result.success(vo);
    }

    @PutMapping("/updateInfo")
    @ApiOperation("修改个人信息")
    public Result<String> updateInfo(@RequestBody @Valid SellerUpdateInfoDTO dto) {
        log.info("卖家修改个人信息");
        sellerService.updateInfo(dto);
        return Result.success("个人信息更新成功");
    }

}

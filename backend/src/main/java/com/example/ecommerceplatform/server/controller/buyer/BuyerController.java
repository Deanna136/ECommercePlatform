package com.example.ecommerceplatform.server.controller.buyer;

import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.common.properties.JwtProperties;
import com.example.ecommerceplatform.common.utils.JwtUtil;
import com.example.ecommerceplatform.common.utils.RedisUtil;
import com.example.ecommerceplatform.pojo.dto.BuyerLoginDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerRegisterDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerUpdateDTO;
import com.example.ecommerceplatform.pojo.vo.BuyerLoginVO;
import com.example.ecommerceplatform.server.service.BuyerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/buyer")
@Slf4j
@Api(tags = "买家相关接口")
public class BuyerController {

    @Resource
    private BuyerService buyerService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    RedisUtil redisUtil;

    private static final String TOKEN_KEY = "token:buyer:";

    @PostMapping("/register")
    @ApiOperation("买家注册")
    public Result<String> register(@RequestBody BuyerRegisterDTO buyerRegisterDTO) {
        log.info("买家注册：{}", buyerRegisterDTO);
        buyerService.register(buyerRegisterDTO);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    @ApiOperation("买家登录")
    public Result<BuyerLoginVO> login(@RequestBody BuyerLoginDTO buyerLoginDTO) {
        log.info("买家登录：{}", buyerLoginDTO);
        // 1. 验证用户名密码
        BuyerLoginVO buyerLoginVO = buyerService.login(buyerLoginDTO);

        // 2. 生成 JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", buyerLoginVO.getId());

        String token = JwtUtil.createJWT(
                jwtProperties.getBuyerSecretKey(),
                jwtProperties.getBuyerTtl(),
                claims);

        // 3. 将 Token 写入 Redis 白名单（过期时间与 JWT 一致）
        redisUtil.set(
                TOKEN_KEY + buyerLoginVO.getId(),
                token,
                jwtProperties.getBuyerTtl(),
                TimeUnit.MILLISECONDS
        );

        buyerLoginVO.setToken(token);

        return Result.success(buyerLoginVO);
    }

    @PutMapping("/updateInfo")
    @ApiOperation("买家修改个人信息")
    public Result<String> update(@RequestBody BuyerUpdateDTO buyerUpdateDTO) {
        log.info("修改个人信息：{}", buyerUpdateDTO);
        buyerService.update(buyerUpdateDTO);
        return Result.success("个人信息更新成功");
    }
}

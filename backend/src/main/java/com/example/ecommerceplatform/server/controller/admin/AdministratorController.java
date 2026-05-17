package com.example.ecommerceplatform.server.controller.admin;

import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.common.properties.JwtProperties;
import com.example.ecommerceplatform.common.utils.JwtUtil;
import com.example.ecommerceplatform.common.utils.RedisUtil;
import com.example.ecommerceplatform.pojo.dto.LoginDTO;
import com.example.ecommerceplatform.pojo.dto.UpdatePasswordDTO;
import com.example.ecommerceplatform.pojo.vo.AdministratorLoginVO;
import com.example.ecommerceplatform.pojo.vo.AdministratorVO;
import com.example.ecommerceplatform.server.service.AdministratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/admin")
@Api(tags = "管理员相关接口")
public class AdministratorController {
    @Resource
    AdministratorService administratorService;
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    RedisUtil redisUtil;

    private static final String TOKEN_KEY = "token:admin:";
    /**
     * 登录
     */
    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public Result<AdministratorLoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 1. 验证用户名密码
        AdministratorVO administratorVO = administratorService.login(loginDTO);

        // 2. 生成 JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", administratorVO.getId());
        claims.put("userName", administratorVO.getUserName());

        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        // 3. 将 Token 写入 Redis 白名单（过期时间与 JWT 一致）
        redisUtil.set(
                TOKEN_KEY + administratorVO.getId(),
                token,
                jwtProperties.getAdminTtl(),
                TimeUnit.MILLISECONDS
        );

        // 4. 构建并返回结果
        AdministratorLoginVO result = AdministratorLoginVO.builder()
                .id(administratorVO.getId())
                .userName(administratorVO.getUserName())
                .token(token)
                .build();

        return Result.success(result);
    }

    /**
     * 退出（从 Redis 删除 Token，真正使 Token 失效）
     */
    @GetMapping("/logout")
    @ApiOperation("管理员退出")
    public Result<String> logout() {
        // 从 ThreadLocal 获取当前管理员 ID（由拦截器解析 Token 后存入）
        Long adminId = BaseContext.getCurrentId();
        if (adminId != null) {
            redisUtil.delete(TOKEN_KEY + adminId);
            log.info("管理员 {} 已退出，Token 已从 Redis 删除", adminId);
        }
        return Result.success("退出成功");
    }

    /**
     * 修改密码
     *
     * @param updatePasswordDTO
     * @return
     */
    @PostMapping("/updatePwd")
    @ApiOperation("管理员修改密码")
    public Result<String> updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO){
        if (administratorService.updatePassword(updatePasswordDTO)){
            return Result.success("密码修改成功！");
        } else {
            return Result.error(ErrorCode.PASSWORD_ERROR);
        }
    }

    /**
     * 查询全部
     *
     * @return
     */
    @ApiOperation("查询全部")
    @GetMapping("/getAll")
    public Result<List<AdministratorVO>> getAll(){
        List<AdministratorVO> admins = administratorService.getAll();
        return Result.success(admins);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id查询")
    @GetMapping("/getById/{id}")
    public Result<AdministratorVO> getById(@PathVariable("id") Long id){
        AdministratorVO admin = administratorService.getById(id);
        return Result.success(admin);
    }

    /**
     * 根据用户名查询
     *
     * @param userName
     * @return
     */
    @ApiOperation("根据用户名查询")
    @GetMapping("/getByUserName")
    public Result<AdministratorVO> getByUserName(@RequestParam("userName") String userName){
        AdministratorVO admin = administratorService.getByUserName(userName);
        return Result.success(admin);
    }
}

package com.example.ecommerceplatform.server.controller.admin;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.common.properties.JwtProperties;
import com.example.ecommerceplatform.common.utils.JwtUtils;
import com.example.ecommerceplatform.pojo.dto.LoginDTO;
import com.example.ecommerceplatform.pojo.dto.UpdatePasswordDTO;
import com.example.ecommerceplatform.pojo.vo.AdministratorLoginVO;
import com.example.ecommerceplatform.pojo.vo.AdministratorVO;
import com.example.ecommerceplatform.server.service.AdministratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Api(tags = "管理员相关接口")
public class AdministratorController {
    @Resource
    AdministratorService administratorService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param loginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public Result<AdministratorLoginVO> login(@Valid @RequestBody LoginDTO loginDTO){
        AdministratorVO administratorVO = administratorService.login(loginDTO);

        //登录成功,生成令牌,下发令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", administratorVO.getId());
        claims.put("userName", administratorVO.getUserName());

        String token = JwtUtils.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        // 构建返回对象
        AdministratorLoginVO result = AdministratorLoginVO.builder()
                .id(administratorVO.getId())
                .userName(administratorVO.getUserName())
                .token(token)
                .build();

        return Result.success(result);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation("管理员退出")
    @GetMapping("/logout")
    public Result<String> logout() {
        return Result.success();
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

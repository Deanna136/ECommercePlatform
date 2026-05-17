package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.pojo.dto.LoginDTO;
import com.example.ecommerceplatform.pojo.dto.UpdatePasswordDTO;
import com.example.ecommerceplatform.pojo.entity.Administrator;
import com.example.ecommerceplatform.pojo.vo.AdministratorVO;
import com.example.ecommerceplatform.server.mapper.AdministratorMapper;
import com.example.ecommerceplatform.server.service.AdministratorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdministratorServiceImpl implements AdministratorService {
    @Resource
    AdministratorMapper administratorMapper;

    /**
     * 登录
     */
    @Override
    public AdministratorVO login(LoginDTO loginDTO) {
        String inputUserName = loginDTO.getUserName();
        String inputPassword = loginDTO.getPassword();

        // 1. 查找用户名
        Administrator admin = administratorMapper.getByUserName(inputUserName);
        if (admin == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 2. 校验密码
        if (!admin.getPassword().equals(inputPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        // 3. 构造 VO 返回
        return AdministratorVO.builder()
                .id(admin.getId())
                .userName(admin.getUserName())
                .createTime(admin.getCreateTime())
                .updateTime(admin.getUpdateTime())
                .build();
    }

    /**
     * 修改密码
     */
    @Override
    public Boolean updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        if (administratorMapper.getById(updatePasswordDTO.getId()) == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        if (updatePasswordDTO.getPassword().length() < 6) {
            throw new BusinessException(ErrorCode.PASSWORD_LENGTH_ERROR);
        }
        return administratorMapper.updatePassword(
                updatePasswordDTO.getPassword(), updatePasswordDTO.getId()) > 0;
    }

    /**
     * 查询全部
     */
    @Override
    public List<AdministratorVO> getAll() {
        List<Administrator> admins = administratorMapper.getAll();
        List<AdministratorVO> result = new ArrayList<>();
        for (Administrator admin : admins) {
            result.add(AdministratorVO.builder()
                    .id(admin.getId())
                    .userName(admin.getUserName())
                    .createTime(admin.getCreateTime())
                    .updateTime(admin.getUpdateTime())
                    .build());
        }
        return result;
    }

    /**
     * 根据id查询
     */
    @Override
    public AdministratorVO getById(Long id) {
        Administrator administrator = administratorMapper.getById(id);
        if (administrator == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        return AdministratorVO.builder()
                .id(administrator.getId())
                .userName(administrator.getUserName())
                .createTime(administrator.getCreateTime())
                .updateTime(administrator.getUpdateTime())
                .build();
    }

    /**
     * 根据用户名查询
     */
    @Override
    public AdministratorVO getByUserName(String userName) {
        Administrator administrator = administratorMapper.getByUserName(userName);
        if (administrator == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        return AdministratorVO.builder()
                .id(administrator.getId())
                .userName(administrator.getUserName())
                .createTime(administrator.getCreateTime())
                .updateTime(administrator.getUpdateTime())
                .build();
    }
}

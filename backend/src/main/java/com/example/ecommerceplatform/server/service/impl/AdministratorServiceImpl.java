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
     * 修改密码
     * @param updatePasswordDTO
     * @return
     */
    @Override
    public Boolean updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        Long inputId = updatePasswordDTO.getId();
        String inputPassword = updatePasswordDTO.getPassword();
        //1.查询用户是否存在
        if(administratorMapper.getById(inputId) == null){
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        //2.密码长度校验
        if(updatePasswordDTO.getPassword().length() < 6){
            throw new BusinessException(ErrorCode.PASSWORD_LENGTH_ERROR);
        }
        //3.修改密码，返回结果
        return administratorMapper.updatePassword(updatePasswordDTO.getPassword(),updatePasswordDTO.getId())>0;
    }

    /**
     * 登录
     * @param loginDTO
     * @return
     */
    @Override
    public AdministratorVO login(LoginDTO loginDTO) {
        String inputUserName = loginDTO.getUserName();
        String inputPassword = loginDTO.getPassword();
        //1.查找用户名
        Administrator admin = administratorMapper.getByUserName(inputUserName);
        if(admin==null){
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        //2.用户密码是否正确
        if(!admin.getPassword().equals(inputPassword)){//————密码不正确
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }else{//————密码正确
            //构造vo返回
            AdministratorVO administratorVO = AdministratorVO.builder()
                    .id(admin.getId())
                    .userName(admin.getUserName())
                    .createTime(admin.getCreateTime())
                    .updateTime(admin.getUpdateTime())
                    .build();
            return administratorVO;
        }
    }

    /**
     * 查询全部
     *
     * @return
     */
    @Override
    public List<AdministratorVO> getAll() {
        List<Administrator> admins = administratorMapper.getAll();
        List<AdministratorVO> newAdmins = new ArrayList<>();
        //封装为List<AdministratorVO>
        for(int i=0;i<admins.size();++i){
            AdministratorVO administratorVO = AdministratorVO.builder()
                    .id(admins.get(i).getId())
                    .userName(admins.get(i).getUserName())
                    .createTime(admins.get(i).getCreateTime())
                    .updateTime(admins.get(i).getUpdateTime())
                    .build();
            newAdmins.add(administratorVO);
        }
        return newAdmins;
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public AdministratorVO getById(Long id) {
        Administrator administrator = administratorMapper.getById(id);
        if(administrator==null){
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        } else {
            AdministratorVO administratorVO = AdministratorVO.builder()
                    .id(administrator.getId())
                    .userName(administrator.getUserName())
                    .createTime(administrator.getCreateTime())
                    .updateTime(administrator.getUpdateTime())
                    .build();
            return administratorVO;
        }
    }

    /**
     * 根据用户名查询
     * @param userName
     * @return
     */
    @Override
    public AdministratorVO getByUserName(String userName) {
        Administrator administrator = administratorMapper.getByUserName(userName);
        if(administrator==null){
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        } else {
            AdministratorVO administratorVO = AdministratorVO.builder()
                    .id(administrator.getId())
                    .userName(administrator.getUserName())
                    .createTime(administrator.getCreateTime())
                    .updateTime(administrator.getUpdateTime())
                    .build();
            return administratorVO;
        }
    }
}

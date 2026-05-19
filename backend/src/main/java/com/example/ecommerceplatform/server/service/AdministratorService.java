package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.LoginDTO;
import com.example.ecommerceplatform.pojo.dto.UpdatePasswordDTO;
import com.example.ecommerceplatform.pojo.entity.Administrator;
import com.example.ecommerceplatform.pojo.vo.AdministratorVO;

import java.util.List;


public interface AdministratorService {
    /**
     * 修改密码
     * @param updatePasswordDTO
     * @return
     */
    Boolean updatePassword(UpdatePasswordDTO updatePasswordDTO);

    /**
     * 登录
     * @param loginDTO
     * @return
     */
    AdministratorVO login(LoginDTO loginDTO);

    /**
     * 查询全部
     *
     * @return
     */
    List<AdministratorVO> getAll();

    /**
     * 根据id查询
     * @param id
     * @return
     */
    AdministratorVO getById(Long id);

    /**
     * 根据用户名查询
     * @param userName
     * @return
     */
    AdministratorVO getByUserName(String userName);
}

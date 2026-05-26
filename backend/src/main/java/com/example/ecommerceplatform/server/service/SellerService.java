package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.SellerQueryDTO;
import com.example.ecommerceplatform.pojo.vo.SellerVO;
import com.example.ecommerceplatform.pojo.dto.SellerLoginDTO;
import com.example.ecommerceplatform.pojo.dto.SellerRegisterDTO;
import com.example.ecommerceplatform.pojo.dto.SellerUpdateInfoDTO;
import com.example.ecommerceplatform.pojo.vo.SellerLoginVO;

import java.util.List;

public interface SellerService {
    List<SellerVO> getAll();

    SellerVO getById(Long id);

    List<SellerVO> query(SellerQueryDTO dto);

    void ban(Long id);

    void unban(Long id);

    /**
     * 卖家注册
     */
    void register(SellerRegisterDTO dto);

    /**
     * 卖家登录
     */
    SellerLoginVO login(SellerLoginDTO dto);

    /**
     * 修改个人信息（姓名、身份证、性别、手机、地址、头像、店铺名称、店铺类型）
     */
    void updateInfo(SellerUpdateInfoDTO dto);
}

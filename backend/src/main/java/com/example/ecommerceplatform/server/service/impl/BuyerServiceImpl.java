package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.enumeration.AcountStatusEnum;
import com.example.ecommerceplatform.pojo.dto.BuyerQueryDTO;
import com.example.ecommerceplatform.pojo.entity.Buyer;
import com.example.ecommerceplatform.pojo.entity.Cart;
import com.example.ecommerceplatform.pojo.vo.BuyerVO;
import com.example.ecommerceplatform.server.mapper.BuyerMapper;
import com.example.ecommerceplatform.server.mapper.CartMapper;
import com.example.ecommerceplatform.server.service.BuyerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.common.properties.JwtProperties;
import com.example.ecommerceplatform.common.utils.JwtUtil;
import com.example.ecommerceplatform.common.utils.RedisUtil;
import com.example.ecommerceplatform.pojo.dto.BuyerLoginDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerRegisterDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerUpdateDTO;
import com.example.ecommerceplatform.pojo.vo.BuyerLoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BuyerServiceImpl implements BuyerService {
    @Resource
    private BuyerMapper buyerMapper;
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CartMapper cartMapper;

    @Override
    public List<BuyerVO> getAll() {
        List<Buyer> buyers = buyerMapper.getAll();
        List<BuyerVO> result = new ArrayList<>();
        for (Buyer b : buyers) {
            result.add(toVO(b));
        }
        return result;
    }

    @Override
    public BuyerVO getById(Long id) {
        Buyer buyer = buyerMapper.getById(id);
        if (buyer == null) {
            throw new BusinessException(ErrorCode.BUYER_NOT_EXIST);
        }
        return toVO(buyer);
    }

    @Override
    public List<BuyerVO> query(BuyerQueryDTO dto) {
        List<Buyer> buyers = buyerMapper.query(dto);
        List<BuyerVO> result = new ArrayList<>();
        for (Buyer b : buyers) {
            result.add(toVO(b));
        }
        return result;
    }

    @Override
    public void ban(Long id) {
        Buyer buyer = buyerMapper.getById(id);
        if (buyer == null) {
            throw new BusinessException(ErrorCode.BUYER_NOT_EXIST);
        }
        String status = buyer.getStatus() != null ? buyer.getStatus().name() : null;
        if ("locked".equals(status)) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        buyerMapper.ban(id);
    }

    @Override
    public void unban(Long id) {
        Buyer buyer = buyerMapper.getById(id);
        if (buyer == null) {
            throw new BusinessException(ErrorCode.BUYER_NOT_EXIST);
        }
        String status = buyer.getStatus() != null ? buyer.getStatus().name() : null;
        if (!"locked".equals(status)) {
            throw new BusinessException(ErrorCode.PRODUCT_STATUS_ERROR);
        }
        buyerMapper.unban(id);
    }

    @Override
    public void register(BuyerRegisterDTO buyerRegisterDTO) {
        // 1. 校验用户名唯一性
        if (buyerMapper.getByUsername(buyerRegisterDTO.getUserName()) != null) {
            throw new BusinessException(ErrorCode.USER_EXIST);
        }

        // 2. 校验密码长度
        if (buyerRegisterDTO.getPassword().length() < 6) {
            throw new BusinessException(ErrorCode.PASSWORD_LENGTH_ERROR);
        }

        Buyer buyer = new Buyer();
        BeanUtils.copyProperties(buyerRegisterDTO, buyer);

        // 3. 密码加密
        buyer.setPassword(DigestUtils.md5DigestAsHex(buyer.getPassword().getBytes()));

        // 4. 设置默认值
        buyer.setStatus(AcountStatusEnum.active);
        buyer.setCreateTime(LocalDateTime.now());
        buyer.setUpdateTime(LocalDateTime.now());

        buyerMapper.insert(buyer);

        // 5. 创建购物车
        Cart cart = new Cart();
        cart.setBuyerId(buyer.getId());
        cartMapper.insert(cart);


    }

    @Override
    public BuyerLoginVO login(BuyerLoginDTO buyerLoginDTO) {
        String username = buyerLoginDTO.getUserName();
        String password = buyerLoginDTO.getPassword();

        // 1. 根据用户名查询用户
        Buyer buyer = buyerMapper.getByUsername(username);

        // 2. 处理各种异常情况
        if (buyer == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 3. 密码比对
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!encryptedPassword.equals(buyer.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        if (buyer.getStatus() == AcountStatusEnum.locked) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 4. 生成令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", buyer.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getBuyerSecretKey(),
                jwtProperties.getBuyerTtl(),
                claims);

        // 5. 令牌存入Redis
        redisUtil.set("buyer_token:" + token, buyer.getId(), jwtProperties.getBuyerTtl(), TimeUnit.MILLISECONDS);


        // 6. 封装VO返回
        return BuyerLoginVO.builder()
                .id(buyer.getId())
                .userName(buyer.getUserName())
                .name(buyer.getName())
                .phone(buyer.getPhone())
                .address(buyer.getAddress())
                .image(buyer.getImage())
                .status(buyer.getStatus().name())
                .token(token)
                .build();
    }

    @Override
    public void update(BuyerUpdateDTO buyerUpdateDTO) {
        Long buyerId = BaseContext.getCurrentId();
        Buyer buyer = new Buyer();
        BeanUtils.copyProperties(buyerUpdateDTO, buyer);
        buyer.setId(buyerId);
        buyer.setUpdateTime(LocalDateTime.now());
        log.info("{}",buyer);

        buyerMapper.update(buyer);
    }

    private BuyerVO toVO(Buyer b) {
        return BuyerVO.builder()
                .id(b.getId())
                .userName(b.getUserName())
                .name(b.getName())
                .idNumber(b.getIdNumber())
                .sex(b.getSex() != null ? b.getSex().name() : null)
                .phone(b.getPhone())
                .address(b.getAddress())
                .image(b.getImage())
                .status(b.getStatus() != null ? b.getStatus().name() : null)
                .createTime(b.getCreateTime())
                .updateTime(b.getUpdateTime())
                .build();
    }
}

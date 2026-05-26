package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.pojo.dto.SellerQueryDTO;
import com.example.ecommerceplatform.pojo.entity.Seller;
import com.example.ecommerceplatform.pojo.vo.SellerVO;
import com.example.ecommerceplatform.server.mapper.SellerMapper;
import com.example.ecommerceplatform.server.service.SellerService;
import org.springframework.stereotype.Service;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.common.enumeration.SexEnum;
import com.example.ecommerceplatform.common.enumeration.CategoryEnum;
import com.example.ecommerceplatform.common.enumeration.AcountStatusEnum;
import com.example.ecommerceplatform.common.utils.JwtUtil;
import com.example.ecommerceplatform.common.utils.RedisUtil;
import com.example.ecommerceplatform.pojo.dto.SellerLoginDTO;
import com.example.ecommerceplatform.pojo.dto.SellerRegisterDTO;
import com.example.ecommerceplatform.pojo.dto.SellerUpdateInfoDTO;
import com.example.ecommerceplatform.pojo.vo.SellerLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${ecommerceplatform.jwt.seller-secret-key}")
    private String secretKey;

    @Value("${ecommerceplatform.jwt.seller-ttl}")
    private Long ttl;

    private static final String REDIS_TOKEN_KEY = "seller_token:";

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    @Override
    public List<SellerVO> getAll() {
        List<Seller> sellers = sellerMapper.getAll();
        List<SellerVO> result = new ArrayList<>();
        for (Seller s : sellers) {
            result.add(toVO(s));
        }
        return result;
    }

    @Override
    public SellerVO getById(Long id) {
        Seller seller = sellerMapper.getById(id);
        if (seller == null) {
            throw new BusinessException(ErrorCode.SELLER_NOT_EXIST);
        }
        return toVO(seller);
    }

    @Override
    public List<SellerVO> query(SellerQueryDTO dto) {
        List<Seller> sellers = sellerMapper.query(dto);
        List<SellerVO> result = new ArrayList<>();
        for (Seller s : sellers) {
            result.add(toVO(s));
        }
        return result;
    }

    @Override
    public void ban(Long id) {
        Seller seller = sellerMapper.getById(id);
        if (seller == null) {
            throw new BusinessException(ErrorCode.SELLER_NOT_EXIST);
        }
        String status = seller.getStatus() != null ? seller.getStatus().name() : null;
        if ("locked".equals(status)) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        sellerMapper.ban(id);
    }

    @Override
    public void unban(Long id) {
        Seller seller = sellerMapper.getById(id);
        if (seller == null) {
            throw new BusinessException(ErrorCode.SELLER_NOT_EXIST);
        }
        String status = seller.getStatus() != null ? seller.getStatus().name() : null;
        if (!"locked".equals(status)) {
            throw new BusinessException(ErrorCode.PRODUCT_STATUS_ERROR);
        }
        sellerMapper.unban(id);
    }

    private SellerVO toVO(Seller s) {
        return SellerVO.builder()
                .id(s.getId())
                .userName(s.getUserName())
                .name(s.getName())
                .idNumber(s.getIdNumber())
                .sex(s.getSex() != null ? s.getSex().name() : null)
                .phone(s.getPhone())
                .address(s.getAddress())
                .image(s.getImage())
                .status(s.getStatus() != null ? s.getStatus().name() : null)
                .storeName(s.getStoreName())
                .storeCategory(s.getStoreCategory() != null ? s.getStoreCategory().name() : null)
                .createTime(s.getCreateTime())
                .updateTime(s.getUpdateTime())
                .build();
    }

    /**
     * 字符串转性别枚举
     */
    private SexEnum toSexEnum(String sex) {
        if (sex == null) return null;
        switch (sex.toLowerCase()) {
            case "male":
                return SexEnum.male;
            case "female":
                return SexEnum.female;
            default:
                return null;
        }
    }

    /**
     * 字符串转店铺分类枚举
     */
    private CategoryEnum toCategoryEnum(String category) {
        if (category == null) return null;
        switch (category.toLowerCase()) {
            case "clothing":
                return CategoryEnum.clothing;
            case "food":
                return CategoryEnum.food;
            case "electronics":
                return CategoryEnum.electronics;
            case "home_living":
                return CategoryEnum.home_living;
            case "mother_baby":
                return CategoryEnum.mother_baby;
            case "sports":
                return CategoryEnum.sports;
            case "books":
                return CategoryEnum.books;
            default:
                return CategoryEnum.others;
        }
    }

    /**
     * 枚举转字符串（用于VO）
     */
    private String sexToString(SexEnum sex) {
        if (sex == null) return null;
        return sex.name().toLowerCase();
    }

    private String categoryToString(CategoryEnum category) {
        if (category == null) return null;
        return category.name().toLowerCase();
    }

    @Override
    public void register(SellerRegisterDTO dto) {
        if (isBlank(dto.getUserName())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        if (isBlank(dto.getPassword()) || dto.getPassword().length() < 6) {
            throw new BusinessException(ErrorCode.PASSWORD_LENGTH_ERROR);
        }
        if (isBlank(dto.getStoreName())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        if (sellerMapper.countByUserName(dto.getUserName()) > 0) {
            throw new BusinessException(ErrorCode.USER_EXIST);
        }

        if (sellerMapper.countByStoreName(dto.getStoreName()) > 0) {
            throw new BusinessException(ErrorCode.STORE_NAME_EXIST);
        }

        Seller seller = new Seller();
        seller.setUserName(dto.getUserName());
        seller.setPassword(dto.getPassword());
        seller.setName(dto.getName());
        seller.setIdNumber(dto.getIdNumber());
        seller.setSex(toSexEnum(dto.getSex()));
        seller.setPhone(dto.getPhone());
        seller.setAddress(dto.getAddress());
        seller.setImage(dto.getImage());
        seller.setStoreName(dto.getStoreName());
        seller.setStoreCategory(toCategoryEnum(dto.getStoreCategory()));

        sellerMapper.insert(seller);
    }

    @Override
    public SellerLoginVO login(SellerLoginDTO dto) {
        if (isBlank(dto.getUserName()) || isBlank(dto.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        Seller seller = sellerMapper.findByUserName(dto.getUserName());
        if (seller == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        if (!dto.getPassword().equals(seller.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        if (AcountStatusEnum.locked.equals(seller.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        if (AcountStatusEnum.deleted.equals(seller.getStatus())) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 生成JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", seller.getId());
        claims.put("userName", seller.getUserName());
        claims.put("role", "seller");
        String token = JwtUtil.createJWT(secretKey, ttl, claims);

        String redisKey = REDIS_TOKEN_KEY + seller.getId();
        redisUtil.set(redisKey, token, ttl, TimeUnit.MILLISECONDS);

        return SellerLoginVO.builder()
                .id(seller.getId())
                .userName(seller.getUserName())
                .name(seller.getName())
                .storeName(seller.getStoreName())
                .storeCategory(categoryToString(seller.getStoreCategory()))
                .status(seller.getStatus() == null ? null : seller.getStatus().name().toLowerCase())
                .token(token)
                .build();
    }

    @Override
    public void updateInfo(SellerUpdateInfoDTO dto) {
        Long sellerId = BaseContext.getCurrentId();
        if (sellerId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Seller seller = sellerMapper.findById(sellerId);
        if (seller == null) {
            throw new BusinessException(ErrorCode.SELLER_NOT_EXIST);
        }

        if (isNotBlank(dto.getStoreName()) && !dto.getStoreName().equals(seller.getStoreName())) {
            if (sellerMapper.countByStoreName(dto.getStoreName()) > 0) {
                throw new BusinessException(ErrorCode.STORE_NAME_EXIST);
            }
        }

        if (isNotBlank(dto.getName())) {
            seller.setName(dto.getName());
        }
        if (isNotBlank(dto.getIdNumber())) {
            seller.setIdNumber(dto.getIdNumber());
        }
        if (isNotBlank(dto.getSex())) {
            seller.setSex(toSexEnum(dto.getSex()));
        }
        if (isNotBlank(dto.getPhone())) {
            seller.setPhone(dto.getPhone());
        }
        if (isNotBlank(dto.getAddress())) {
            seller.setAddress(dto.getAddress());
        }
        if (isNotBlank(dto.getImage())) {
            seller.setImage(dto.getImage());
        }
        if (isNotBlank(dto.getStoreName())) {
            seller.setStoreName(dto.getStoreName());
        }
        if (isNotBlank(dto.getStoreCategory())) {
            seller.setStoreCategory(toCategoryEnum(dto.getStoreCategory()));
        }

        sellerMapper.update(seller);
    }
}
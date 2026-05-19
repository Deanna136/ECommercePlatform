package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.pojo.dto.SellerQueryDTO;
import com.example.ecommerceplatform.pojo.entity.Seller;
import com.example.ecommerceplatform.pojo.vo.SellerVO;
import com.example.ecommerceplatform.server.mapper.SellerMapper;
import com.example.ecommerceplatform.server.service.SellerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {
    @Resource
    private SellerMapper sellerMapper;

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
}

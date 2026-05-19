package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.pojo.dto.BuyerQueryDTO;
import com.example.ecommerceplatform.pojo.entity.Buyer;
import com.example.ecommerceplatform.pojo.vo.BuyerVO;
import com.example.ecommerceplatform.server.mapper.BuyerMapper;
import com.example.ecommerceplatform.server.service.BuyerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class BuyerServiceImpl implements BuyerService {
    @Resource
    private BuyerMapper buyerMapper;

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

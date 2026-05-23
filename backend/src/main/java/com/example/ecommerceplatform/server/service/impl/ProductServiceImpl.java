package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.pojo.dto.BuyerProductQueryDTO;
import com.example.ecommerceplatform.pojo.dto.ProductQueryDTO;
import com.example.ecommerceplatform.pojo.entity.Product;
import com.example.ecommerceplatform.pojo.entity.Seller;
import com.example.ecommerceplatform.pojo.vo.ProductVO;
import com.example.ecommerceplatform.server.mapper.ProductMapper;
import com.example.ecommerceplatform.server.mapper.SellerMapper;
import com.example.ecommerceplatform.server.service.ProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private SellerMapper sellerMapper;

    @Override
    public List<ProductVO> getAll() {
        List<Product> products = productMapper.getAll();
        List<ProductVO> result = new ArrayList<>();
        for (Product p : products) {
            result.add(toVO(p));
        }
        return result;
    }

    @Override
    public List<ProductVO> listOnsale() {
        List<Product> products = productMapper.listOnsale();
        List<ProductVO> result = new ArrayList<>();
        for (Product p : products) {
            result.add(toVO(p));
        }
        return result;
    }

    @Override
    public ProductVO getById(Long id) {
        Product product = productMapper.getById(id);
        if (product == null || "deleted".equals(product.getStatus().name())) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return toVO(product);
    }

    @Override
    public ProductVO getOnsaleById(Long id) {
        Product product = productMapper.getOnsaleById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return toVO(product);
    }

    @Override
    public List<ProductVO> query(ProductQueryDTO dto) {
        List<Product> products = productMapper.query(dto);
        List<ProductVO> result = new ArrayList<>();
        for (Product p : products) {
            result.add(toVO(p));
        }
        return result;
    }

    @Override
    public List<ProductVO> queryOnsale(BuyerProductQueryDTO dto) {
        List<Product> products = productMapper.queryOnsale(dto);
        List<ProductVO> result = new ArrayList<>();
        for (Product p : products) {
            result.add(toVO(p));
        }
        return result;
    }

    @Override
    public void approve(Long id) {
        Product product = productMapper.getById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        String status = product.getStatus() != null ? product.getStatus().name() : null;
        if (!"pending_review".equals(status)) {
            throw new BusinessException(ErrorCode.PRODUCT_STATUS_ERROR);
        }
        productMapper.approve(id);
    }

    @Override
    public void reject(Long id) {
        Product product = productMapper.getById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        String status = product.getStatus() != null ? product.getStatus().name() : null;
        if (!"pending_review".equals(status)) {
            throw new BusinessException(ErrorCode.PRODUCT_STATUS_ERROR);
        }
        productMapper.reject(id);
    }

    @Override
    public void suspend(Long id) {
        Product product = productMapper.getById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        String status = product.getStatus() != null ? product.getStatus().name() : null;
        if ("deleted".equals(status)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        productMapper.suspend(id);
    }

    private ProductVO toVO(Product p) {
        String sellerName = null;
        String storeName = null;
        if (p.getSellerId() != null) {
            Seller seller = sellerMapper.getById(p.getSellerId());
            if (seller != null) {
                sellerName = seller.getName();
                storeName = seller.getStoreName();
            }
        }
        return ProductVO.builder()
                .id(p.getId())
                .productNo(p.getProductNo())
                .name(p.getName())
                .category(p.getCategory() != null ? p.getCategory().name() : null)
                .sku(p.getSku())
                .price((double) p.getPrice())
                .image(p.getImage())
                .description(p.getDescription())
                .sellerId(p.getSellerId())
                .sellerName(sellerName)
                .storeName(storeName)
                .status(p.getStatus() != null ? p.getStatus().name() : null)
                .salesCount(p.getSalesCount())
                .createTime(p.getCreateTime())
                .updateTime(p.getUpdateTime())
                .build();
    }
}

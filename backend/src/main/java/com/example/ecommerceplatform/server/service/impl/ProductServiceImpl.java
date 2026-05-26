package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.common.enumeration.CategoryEnum;
import com.example.ecommerceplatform.common.enumeration.ProductStatusEnum;
import com.example.ecommerceplatform.pojo.dto.ProductDTO;
import com.example.ecommerceplatform.pojo.entity.Product;
import com.example.ecommerceplatform.pojo.vo.ProductVO;
import com.example.ecommerceplatform.server.mapper.ProductMapper;
import com.example.ecommerceplatform.server.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 字符串转分类枚举
     */
    private CategoryEnum toCategoryEnum(String category) {
        if (category == null) {
            return CategoryEnum.others;
        }

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
     * 新增商品
     */
    @Override
    public void add(ProductDTO dto) {

        Long sellerId = BaseContext.getCurrentId();

        Product existProduct =
                productMapper.findByProductNo(dto.getProductNo(), sellerId);

        if (existProduct != null) {
            throw new BusinessException(ErrorCode.PRODUCT_NO_DUPLICATE);
        }

        Product product = new Product();

        product.setProductNo(dto.getProductNo());
        product.setName(dto.getName());
        product.setCategory(toCategoryEnum(dto.getCategory()));
        product.setSku(dto.getSku());
        product.setPrice(dto.getPrice());
        product.setImage(dto.getImage());
        product.setDescription(dto.getDescription());
        product.setSellerId(sellerId);

        if (dto.getStatus() != null) {
            product.setStatus(
                    ProductStatusEnum.valueOf(dto.getStatus())
            );
        } else {
            product.setStatus(ProductStatusEnum.pending_review);
        }


        productMapper.insert(product);
    }

    /**
     * 修改商品
     */
    @Override
    public void update(ProductDTO dto) {

        Long sellerId = BaseContext.getCurrentId();

        Product product = productMapper.findById(dto.getId());

        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        if (!product.getSellerId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        // suspend 和 deleted 不允许修改
        if (product.getStatus() == ProductStatusEnum.suspend
                || product.getStatus() == ProductStatusEnum.deleted) {

            throw new BusinessException(
                    ErrorCode.PRODUCT_STATUS_ERROR
            );
        }
        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getCategory() != null) {
            product.setCategory(
                    toCategoryEnum(dto.getCategory())
            );
        }
        if (dto.getSku() != null) {
            product.setSku(dto.getSku());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getImage() != null) {
            product.setImage(dto.getImage());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        product.setSellerId(sellerId);


        productMapper.update(product);
    }

    /**
     * 商品详情
     */
    @Override
    public ProductVO detail(Long id) {

        Long sellerId = BaseContext.getCurrentId();

        Product product = productMapper.findById(id);

        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        if (!product.getSellerId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        return ProductVO.builder()
                .id(product.getId())
                .productNo(product.getProductNo())
                .name(product.getName())
                .category(product.getCategory().name())
                .sku(product.getSku())
                .price(product.getPrice())
                .image(product.getImage())
                .description(product.getDescription())
                .status(product.getStatus() == null
                        ? "pending_review"
                        : product.getStatus().name())
                .salesCount(product.getSalesCount())
                .build();
    }

    /**
     * 商品列表
     */
    @Override
    public List<ProductVO> list() {

        Long sellerId = BaseContext.getCurrentId();

        List<Product> productList =
                productMapper.findBySellerId(sellerId);

        List<ProductVO> voList = new ArrayList<>();

        for (Product product : productList) {

            ProductVO vo = ProductVO.builder()
                    .id(product.getId())
                    .productNo(product.getProductNo())
                    .name(product.getName())
                    .category(product.getCategory().name())
                    .sku(product.getSku())
                    .price(product.getPrice())
                    .image(product.getImage())
                    .description(product.getDescription())
                    .status(product.getStatus() == null
                            ? "pending_review"
                            : product.getStatus().name())
                    .salesCount(product.getSalesCount())
                    .build();

            voList.add(vo);
        }

        return voList;
    }

    /**
     * 修改商品状态
     */
    @Override
    public void updateStatus(Long id, String status) {

        Long sellerId = BaseContext.getCurrentId();

        Product product = productMapper.findById(id);

        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        ProductStatusEnum statusEnum =
                ProductStatusEnum.valueOf(status);

        productMapper.updateStatus(id, sellerId, statusEnum.name());
    }

    /**
     * 删除商品
     */
    @Override
    public void delete(Long id) {

        Long sellerId = BaseContext.getCurrentId();

        Product product = productMapper.findById(id);

        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        productMapper.deleteProduct(id, sellerId);
    }

    /**
     * 停售商品
     */
    @Override
    public void stopSale(Long id) {

        Long sellerId = BaseContext.getCurrentId();

        Product product = productMapper.findById(id);

        // 商品不存在
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        // 无权限
        if (!product.getSellerId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        // 只有 onsale 状态允许停售
        if (product.getStatus() != ProductStatusEnum.onsale) {
            throw new BusinessException(ErrorCode.PRODUCT_STATUS_ERROR);
        }

        // 修改为 offsale
        productMapper.updateStatus(
                id,
                sellerId,
                "offsale"
        );
    }
    /**
     * 商品条件筛选
     */
    @Override
    public List<ProductVO> search(
            String name,
            String category,
            String status) {

        Long sellerId = BaseContext.getCurrentId();

        List<Product> productList =
                productMapper.search(
                        sellerId,
                        name,
                        category,
                        status
                );

        List<ProductVO> voList = new ArrayList<>();

        for (Product product : productList) {

            ProductVO vo = ProductVO.builder()
                    .id(product.getId())
                    .productNo(product.getProductNo())
                    .name(product.getName())
                    .category(product.getCategory() == null
                            ? null
                            : product.getCategory().name())
                    .sku(product.getSku())
                    .price(product.getPrice())
                    .image(product.getImage())
                    .description(product.getDescription())
                    .status(product.getStatus() == null
                            ? null
                            : product.getStatus().name())
                    .salesCount(product.getSalesCount())
                    .build();

            voList.add(vo);
        }

        return voList;
    }
}
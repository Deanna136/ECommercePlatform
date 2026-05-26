package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.enumeration.CategoryEnum;
import com.example.ecommerceplatform.common.enumeration.ProductStatusEnum;
import com.example.ecommerceplatform.pojo.dto.SellerProductAddDTO;
import com.example.ecommerceplatform.pojo.dto.SellerProductUpdateDTO;
import com.example.ecommerceplatform.pojo.entity.Product;
import com.example.ecommerceplatform.pojo.entity.Seller;
import com.example.ecommerceplatform.pojo.entity.SellerProducts;
import com.example.ecommerceplatform.server.mapper.ProductMapper;
import com.example.ecommerceplatform.server.mapper.SellerMapper;
import com.example.ecommerceplatform.server.mapper.SellerProductsMapper;
import com.example.ecommerceplatform.server.service.SellerProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SellerProductServiceImpl implements SellerProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private SellerProductsMapper sellerProductsMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public List<Product> getProductList(Long sellerId) {
        List<Long> productIds = sellerProductsMapper.findProductIdsBySellerId(sellerId);
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }
        return productMapper.findByIds(productIds);
    }

    @Override
    public Product getProductDetail(Long sellerId, Long productId) {
        int count = sellerProductsMapper.countBySellerIdAndProductId(sellerId, productId);
        if (count == 0) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return product;
    }

    @Override
    public List<Product> queryProducts(Long sellerId, String productNo, String name, String category, String status, Float minPrice, Float maxPrice) {
        List<Product> products = getProductList(sellerId);
        return products.stream()
                .filter(p -> productNo == null || productNo.isEmpty() || productNo.equals(p.getProductNo()))
                .filter(p -> name == null || name.isEmpty() || p.getName().contains(name))
                .filter(p -> category == null || category.isEmpty() || category.equals(p.getCategory() != null ? p.getCategory().name().toLowerCase() : null))
                .filter(p -> status == null || status.isEmpty() || status.equals(p.getStatus() != null ? p.getStatus().name().toLowerCase() : null))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    @Override
    public Product addProduct(Long sellerId, SellerProductAddDTO dto) {
        Seller seller = sellerMapper.findById(sellerId);
        if (seller == null) {
            throw new BusinessException(ErrorCode.SELLER_NOT_EXIST);
        }
        if ("locked".equals(seller.getStatus() != null ? seller.getStatus().name().toLowerCase() : null)) {
            throw new BusinessException(ErrorCode.SELLER_STOP_OPERATE);
        }

        Product existingProduct = productMapper.findByProductNo(dto.getProductNo());
        if (existingProduct != null) {
            int count = sellerProductsMapper.countBySellerIdAndProductId(sellerId, existingProduct.getId());
            if (count > 0) {
                throw new BusinessException(ErrorCode.PRODUCT_NO_DUPLICATE);
            }
        }

        Product product = new Product();
        product.setProductNo(dto.getProductNo());
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setSku(dto.getSku());
        product.setPrice(dto.getPrice());
        product.setImage(dto.getImage());
        product.setDescription(dto.getDescription());
        product.setStatus(ProductStatusEnum.pending_review);
        product.setSalesCount(0);
        product.setSellerId(sellerId);

        productMapper.insert(product);

        SellerProducts sellerProducts = new SellerProducts();
        sellerProducts.setSellerId(sellerId);
        sellerProducts.setProductId(product.getId());
        sellerProductsMapper.insert(sellerProducts);

        return product;
    }

    @Override
    public void updateProduct(Long sellerId, Long productId, SellerProductUpdateDTO dto) {
        int count = sellerProductsMapper.countBySellerIdAndProductId(sellerId, productId);
        if (count == 0) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        ProductStatusEnum status = product.getStatus();
        if (status == ProductStatusEnum.deleted || status == ProductStatusEnum.suspend) {
            throw new BusinessException(ErrorCode.PRODUCT_STATUS_ERROR);
        }

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getCategory() != null) {
            product.setCategory(dto.getCategory());
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

        productMapper.update(product);
    }

    @Override
    public void stopSale(Long sellerId, Long productId) {
        int count = sellerProductsMapper.countBySellerIdAndProductId(sellerId, productId);
        if (count == 0) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        if (product.getStatus() != ProductStatusEnum.onsale) {
            throw new BusinessException(ErrorCode.PRODUCT_STATUS_ERROR);
        }

            productMapper.updateStatus(productId, "offsale");
    }
}

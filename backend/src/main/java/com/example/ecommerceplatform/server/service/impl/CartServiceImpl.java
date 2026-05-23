package com.example.ecommerceplatform.server.service.impl;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.pojo.dto.CartAddDTO;
import com.example.ecommerceplatform.pojo.dto.CartUpdateDTO;
import com.example.ecommerceplatform.pojo.entity.Cart;
import com.example.ecommerceplatform.pojo.entity.CartItems;
import com.example.ecommerceplatform.pojo.entity.Product;
import com.example.ecommerceplatform.pojo.vo.CartItemVO;
import com.example.ecommerceplatform.pojo.vo.CartVO;
import com.example.ecommerceplatform.server.mapper.CartItemsMapper;
import com.example.ecommerceplatform.server.mapper.CartMapper;
import com.example.ecommerceplatform.server.mapper.ProductMapper;
import com.example.ecommerceplatform.server.service.CartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper;
    @Resource
    private CartItemsMapper cartItemsMapper;
    @Resource
    private ProductMapper productMapper;

    @Override
    public CartVO getCart() {
        Long buyerId = BaseContext.getCurrentId();

        Cart cart = cartMapper.getByBuyerId(buyerId);

        if(cart == null){
            cart = new Cart();
            cart.setBuyerId(buyerId);
            cartMapper.insert(cart);
        }

        List<CartItemVO> items = cartItemsMapper.listDetailByCartId(cart.getId());
        if (items == null) {
            items = new ArrayList<>();
        }

        int totalCount = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItemVO item : items) {
            int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
            double priceValue = item.getPrice() == null ? 0.0 : item.getPrice();
            BigDecimal price = BigDecimal.valueOf(priceValue);
            BigDecimal subTotal = price.multiply(BigDecimal.valueOf(quantity));
            item.setSubTotal(subTotal.doubleValue());
            totalCount += quantity;
            if ("onsale".equals(item.getProductStatus())) {
                totalPrice = totalPrice.add(subTotal);
            }
        }

        CartVO cartVO = new CartVO();
        cartVO.setCartId(cart.getId());
        cartVO.setTotalCount(totalCount);
        cartVO.setTotalPrice(totalPrice.doubleValue());
        cartVO.setCartItems(items);
        return cartVO;
    }

    @Override
    public void addItem(CartAddDTO cartAddDTO) {
        if (cartAddDTO.getQuantity() == null || cartAddDTO.getQuantity() <= 0) {
            throw new BusinessException(ErrorCode.CART_QUANTITY_ERROR);
        }

        Long buyerId = BaseContext.getCurrentId();
        Cart cart = cartMapper.getByBuyerId(buyerId);
        if (cart == null) {
            throw new BusinessException(ErrorCode.CART_NOT_EXIST);
        }

        Product product = productMapper.getById(cartAddDTO.getProductId());
        if (product == null || "deleted".equals(product.getStatus().name())) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        String status = product.getStatus() != null ? product.getStatus().name() : null;
        if (!"onsale".equals(status)) {
            if ("suspend".equals(status)) {
                throw new BusinessException(ErrorCode.PRODUCT_SUSPEND);
            }
            throw new BusinessException(ErrorCode.PRODUCT_OFF_SALE);
        }

        long sku = product.getSku() == null ? 0 : product.getSku();
        int quantity = cartAddDTO.getQuantity();
        if (sku < quantity) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH);
        }

        CartItems existing = cartItemsMapper.getByCartIdAndProductId(cart.getId(), product.getId());
        if (existing == null) {
            CartItems cartItems = new CartItems();
            cartItems.setCartId(cart.getId());
            cartItems.setProductId(product.getId());
            cartItems.setQuantity(quantity);
            cartItemsMapper.insert(cartItems);
            return;
        }

        int newQuantity = existing.getQuantity() + quantity;
        if (sku < newQuantity) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH);
        }
        cartItemsMapper.updateQuantity(existing.getId(), newQuantity);
    }

    @Override
    public void updateItem(CartUpdateDTO cartUpdateDTO) {
        if (cartUpdateDTO.getQuantity() == null || cartUpdateDTO.getQuantity() <= 0) {
            throw new BusinessException(ErrorCode.CART_QUANTITY_ERROR);
        }

        CartItems cartItems = cartItemsMapper.getById(cartUpdateDTO.getCartItemId());
        if (cartItems == null) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_EXIST);
        }

        validateOwnership(cartItems.getCartId());

        Product product = productMapper.getById(cartItems.getProductId());
        if (product == null || "deleted".equals(product.getStatus().name())) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        String status = product.getStatus() != null ? product.getStatus().name() : null;
        if (!"onsale".equals(status)) {
            if ("suspend".equals(status)) {
                throw new BusinessException(ErrorCode.PRODUCT_SUSPEND);
            }
            throw new BusinessException(ErrorCode.PRODUCT_OFF_SALE);
        }

        long sku = product.getSku() == null ? 0 : product.getSku();
        int quantity = cartUpdateDTO.getQuantity();
        if (sku < quantity) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH);
        }
        cartItemsMapper.updateQuantity(cartItems.getId(), quantity);
    }

    @Override
    public void removeItem(Long cartItemId) {
        CartItems cartItems = cartItemsMapper.getById(cartItemId);
        if (cartItems == null) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_EXIST);
        }
        validateOwnership(cartItems.getCartId());
        cartItemsMapper.deleteById(cartItemId);
    }

    private void validateOwnership(Long cartId) {
        Long buyerId = BaseContext.getCurrentId();
        Cart cart = cartMapper.getById(cartId);
        if (cart == null) {
            throw new BusinessException(ErrorCode.CART_NOT_EXIST);
        }
        if (!buyerId.equals(cart.getBuyerId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
    }
}

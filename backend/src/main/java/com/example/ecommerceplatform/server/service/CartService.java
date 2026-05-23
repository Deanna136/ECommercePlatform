package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.CartAddDTO;
import com.example.ecommerceplatform.pojo.dto.CartUpdateDTO;
import com.example.ecommerceplatform.pojo.vo.CartVO;

public interface CartService {
    CartVO getCart();

    void addItem(CartAddDTO cartAddDTO);

    void updateItem(CartUpdateDTO cartUpdateDTO);

    void removeItem(Long cartItemId);
}

package com.example.ecommerceplatform.server.service;

import com.example.ecommerceplatform.pojo.dto.BuyerOrderCreateDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderQueryDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderRefundDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderReportDTO;
import com.example.ecommerceplatform.pojo.dto.BuyerOrderUpdateDTO;
import com.example.ecommerceplatform.pojo.vo.BuyerOrderCreateVO;
import com.example.ecommerceplatform.pojo.vo.BuyerOrderDetailVO;
import com.example.ecommerceplatform.pojo.vo.BuyerOrderVO;

import java.util.List;

public interface BuyerOrderService {
    List<BuyerOrderVO> list();

    BuyerOrderDetailVO getById(Long id);

    List<BuyerOrderVO> query(BuyerOrderQueryDTO dto);

    BuyerOrderCreateVO create(BuyerOrderCreateDTO dto);

    void refund(Long id, BuyerOrderRefundDTO dto);

    void updateInfo(Long id, BuyerOrderUpdateDTO dto);

    void reportAbnormal(Long id, BuyerOrderReportDTO dto);
}

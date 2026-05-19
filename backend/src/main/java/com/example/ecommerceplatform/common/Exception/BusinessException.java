package com.example.ecommerceplatform.common.Exception;

import com.example.ecommerceplatform.common.Result.ErrorCode;
import lombok.Getter;

/**
 * 业务异常，用于主动抛出业务逻辑错误（如用户名已存在、库存不足、参数无效等）
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;   // 1000

    /**
     * 自定义错误码和错误消息
     * @param errorCode 错误码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public BusinessException(int code,String msg){
        super(msg);
        this.code = code;
    }

}
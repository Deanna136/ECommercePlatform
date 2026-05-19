package com.example.ecommerceplatform.common.Exception;

import com.example.ecommerceplatform.common.Result.ErrorCode;
import lombok.Getter;

@Getter
public class CosException extends BusinessException{
    private final int code; //3000

    /**
     * 自定义错误码和错误消息
     * @param errorCode 错误码
     */
    public CosException(ErrorCode errorCode) {
        super(errorCode);
        this.code = errorCode.getCode();
    }

    public CosException(int code, String msg){
        super(code,msg);
        this.code = code;
    }
}
package com.example.ecommerceplatform.common.Exception;

import com.example.ecommerceplatform.common.Result.ErrorCode;
import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {
    private int code; //1001

    /**
     * 自定义错误码和错误消息
     *
     * @param errorCode 错误码
     */
    public AccountNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public AccountNotFoundException(int code,String msg){
        super(msg);
        this.code = code;
    }

}

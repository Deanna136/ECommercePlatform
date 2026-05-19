package com.example.ecommerceplatform.common.Exception;

import com.example.ecommerceplatform.common.Result.ErrorCode;
import lombok.Getter;

@Getter
public class AccountLockedException extends RuntimeException{
    private final int code; //1002

    public AccountLockedException(ErrorCode errorCode) {
        super(errorCode.getMsg());

        this.code = errorCode.getCode();
    }

    public AccountLockedException(int code,String msg){
        super(msg);
        this.code = code;
    }
}

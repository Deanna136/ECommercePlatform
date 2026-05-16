package com.example.ecommerceplatform.common.Exception;

import com.example.ecommerceplatform.common.Result.ErrorCode;
import lombok.Getter;

/**
 * 未认证异常，用于用户未认证（如token过期、无效、用户未登录）
 * 继承 RuntimeException，无需在方法签名中显式声明 throws
 */
@Getter
public class UnauthorizedException extends RuntimeException {

    private final int code;   // 错误码，通常 401 表示未认证

    /**
     * 自定义错误码和错误消息
     * @param errorCode 错误码
     */
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public UnauthorizedException(int code,String msg){
        super(msg);
        this.code = code;
    }

}

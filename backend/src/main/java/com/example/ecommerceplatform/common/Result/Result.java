package com.example.ecommerceplatform.common.Result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(){return new Result<>(200,"操作成功",null);}

    public static <T> Result<T> success(T data){
        return new Result<>(200,"操作成功",data);
    }

    public static <T> Result<T> error(ErrorCode errorCode){
        return new Result<>(errorCode.getCode(), errorCode.getMsg(), null);
    }

    public static <T> Result<T> error(Integer code,String msg){
        return new Result<>(code,msg,null);
    }

    // 失败
    public static <T> Result<T> failure(String msg) {
        return new Result<>(400, msg, null);
    }
}

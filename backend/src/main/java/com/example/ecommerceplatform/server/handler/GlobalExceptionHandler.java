package com.example.ecommerceplatform.server.handler;

import com.example.ecommerceplatform.common.Exception.*;
import com.example.ecommerceplatform.common.Result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice // 全局异常处理 + 自动@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 处理未认证异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Result<String> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("用户未登录或token失效，code={},msg={}",e.getCode(),e.getMessage());
        return Result.error(e.getCode(),e.getMessage());
    }

    /**
     * 用户被锁定
     */
    @ExceptionHandler(AccountLockedException.class)
    public Result<String> handleAccountLockedException(AccountLockedException e) {
        log.warn("用户被锁定，code={},msg={}",e.getCode(),e.getMessage());
        return Result.error(e.getCode(),e.getMessage());
    }

    /**
     * 用户未找到
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public Result<String> handleAccountNotFoundException(AccountNotFoundException e) {
        log.warn("未找到用户，code={},msg={}",e.getCode(),e.getMessage());
        return Result.error(e.getCode(),e.getMessage());
    }

    /**
     * 处理cos异常
     */
    @ExceptionHandler(CosException.class)
    public Result<String> handleCosException(CosException e) {
        log.warn("腾讯云cos业务异常，code={},msg={}",e.getCode(),e.getMessage());
        return Result.error(e.getCode(),e.getMessage());
    }

    /**
     * 捕获 SQL 唯一键冲突异常
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        // 错误信息示例: Duplicate entry 'zhangsan' for key 'idx_username'
        String message = e.getMessage();
        if (message.contains("Duplicate entry")) {
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + " 已存在";
            log.error("唯一键冲突异常，code={},msg={}",2002,e.getMessage());
            return Result.error(2002,e.getMessage());
        } else {
            log.error("数据库异常，code={},msg={}",2001,e.getMessage());
            return Result.error(2001,e.getMessage());
        }
    }

    /**
     * 捕获 SQL 其他异常
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public Result<String> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("数据库异常，code={},msg={}",2001,e.getMessage());
        return Result.error(2001,e.getMessage());
    }

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.warn("业务异常，code={},msg={}",e.getCode(),e.getMessage());
        return Result.error(e.getCode(),e.getMessage());
    }

    /**
     * 处理参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        log.warn("参数校验失败,code={},msg={}",400,e.getMessage());
        return Result.error(400,e.getMessage());
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<String> handleNullPointerException(NullPointerException e) {
        log.warn("空指针异常，code={},msg={}",500,e.getMessage());
        return Result.error(500,e.getMessage());
    }

    /**
     * 处理其他通用异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleGenericException(Exception e) {
        log.warn("通用异常，code={},msg={}",500,e.getMessage());
        return Result.error(500,e.getMessage());
    }

}
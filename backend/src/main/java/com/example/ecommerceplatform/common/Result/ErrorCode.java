package com.example.ecommerceplatform.common.Result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ==================== 腾讯云COS ====================
    COS_ERROR(3000, "腾讯云存储操作异常"),
    FILE_UPLOAD_FAILED(3001,"文件上传失败,请重试"),
    FILE_DOWNLOAD_FAILED(3002,"文件不存在或下载失败"),
    URL_NULL(3003,"文件url不能为空"),
    FILE_DELETE_FAILED(3004,"文件删除失败"),


    // ==================== 通用 HTTP 标准码 ====================
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "用户未登录，请先登录"),
    NO_PERMISSION(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_ERROR(405, "请求方法错误"),
    SERVER_ERROR(500, "系统繁忙，请稍后再试"),
    SERVICE_BUSY(503, "系统繁忙，请稍后再试"),

    // ==================== Token / 登录 专用 ====================
    TOKEN_INVALID(401, "Token 无效"),
    TOKEN_EXPIRED(401, "Token 已过期，请重新登录"),
    TOKEN_MALFORMED(401, "Token格式错误"),
    TOKEN_SIGNATURE_ERROR(401, "Token签名验证失败"),
    TOKEN_ILLEGAL_ARGUMENT(401, "Token参数错误"),
    TOKEN_UNKNOWN_ERROR(401, "Token解析失败"),
    USER_NOT_LOGIN(401, "用户未登录或登录状态已失效"),
    
    // ==================== 系统 / 数据库异常 ====================
    DB_ERROR(2001, "数据库服务异常"),
    DATA_DUPLICATE(2002, "数据重复/唯一约束冲突"),
    DATA_NOT_EXIST(2003, "数据不存在"),
    FOREIGN_KEY_ERROR(2004, "外键关联数据不存在"),

    // ==================== 登录 / 账号通用 ====================
    USER_NOT_EXIST(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    USER_EXIST(1003, "用户名已存在"),
    USER_DISABLED(1004, "账号已被锁定/禁用"),
    USER_DELETED(1005, "账号已被删除"),
    PASSWORD_LENGTH_ERROR(1006, "密码格式错误，长度不能少于6位"),
    PASSWORD_UPDATE_ERROR(1007,"密码修改失败"),


    // ====================  买家（buyer）====================
    BUYER_NOT_EXIST(1101, "买家不存在"),
    BUYER_ID_NUMBER_ERROR(1102, "身份证格式错误"),
    BUYER_PHONE_ERROR(1103, "手机号格式错误"),

    // ==================== 卖家（seller）====================
    SELLER_NOT_EXIST(1201, "卖家不存在"),
    STORE_NAME_EXIST(1202, "店铺名称已存在"),
    STORE_CATEGORY_ERROR(1203, "店铺类型不合法"),
    SELLER_STOP_OPERATE(1204, "卖家账号已停用，无法操作商品"),

    // ==================== 管理员（administrator）====================
    ADMIN_NOT_EXIST(1301, "管理员不存在"),
    PERMISSION_ONLY_ADMIN(1302, "仅管理员可操作"),

    // ==================== 商品（product）====================
    PRODUCT_NOT_EXIST(4001, "商品不存在"),
    PRODUCT_NO_DUPLICATE(4002, "商品编号已存在"),
    PRODUCT_STATUS_ERROR(4003, "商品状态不允许操作"),
    PRODUCT_OFF_SALE(4004, "商品已下架"),
    PRODUCT_SUSPEND(4005, "商品已被封禁"),
    PRODUCT_DELETED(4006, "商品已删除"),
    STOCK_NOT_ENOUGH(4007, "商品库存不足"),
    STOCK_ERROR(4008, "库存不能小于0"),
    PRICE_ERROR(4009, "商品价格不能小于0"),

    // ==================== 购物车（cart）====================
    CART_NOT_EXIST(5001, "购物车不存在"),
    CART_ITEM_NOT_EXIST(5002, "购物车商品不存在"),
    CART_QUANTITY_ERROR(5003, "商品数量必须大于0"),

    // ==================== 订单（orders）====================
    ORDER_NOT_EXIST(6001, "订单不存在"),
    ORDER_NO_DUPLICATE(6002, "订单编号重复"),
    ORDER_STATUS_ERROR(6003, "订单状态不允许此操作"),
    ORDER_CANCEL_ERROR(6004, "订单已支付/发货，无法取消"),
    ORDER_AMOUNT_ERROR(6005, "订单金额异常"),

    // ==================== 权限三端隔离 ====================
    ONLY_BUYER(6101, "该功能仅买家可访问"),
    ONLY_SELLER(6102, "该功能仅卖家可访问"),
    ONLY_ADMIN(6103, "该功能仅管理员可访问"),

    // ==================== 业务操作失败 ====================
    BUSINESS_ERROR(1000, "业务操作失败"),

    // ==================== 空指针异常 ====================
    NULL_POINTER(500, "空指针异常");


    private final int code;
    private final String msg;

}

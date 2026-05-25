package com.example.ecommerceplatform.server.interceptor;

import com.example.ecommerceplatform.common.Exception.BusinessException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.common.utils.JwtUtil;
import com.example.ecommerceplatform.common.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenSellerInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;

    @Value("${ecommerceplatform.jwt.seller-secret-key}")
    private String secretKey;

    private static final String REDIS_TOKEN_KEY = "seller_token:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader("seller_token");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Claims claims;
        try {
            claims = JwtUtil.parseJWT(secretKey, token);
        } catch (Exception e) {
            log.error("Token解析失败：{}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        // 获取用户ID - 关键修复点
        Object sellerIdObj = claims.get("id");
        Long sellerId = null;
        if (sellerIdObj instanceof Integer) {
            sellerId = ((Integer) sellerIdObj).longValue();
        } else if (sellerIdObj instanceof Long) {
            sellerId = (Long) sellerIdObj;
        } else if (sellerIdObj instanceof String) {
            try {
                sellerId = Long.parseLong((String) sellerIdObj);
            } catch (NumberFormatException e) {
                log.error("ID格式错误");
            }
        }

        if (sellerId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String redisKey = REDIS_TOKEN_KEY + sellerId;
        String redisToken = (String) redisUtil.get(redisKey);
        if (redisToken == null || !redisToken.equals(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        BaseContext.setCurrentId(sellerId);
        log.info("卖家端拦截器校验通过，sellerId：{}", sellerId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeCurrentId();
    }
}
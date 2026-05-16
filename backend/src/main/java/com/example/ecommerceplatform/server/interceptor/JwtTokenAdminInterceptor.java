package com.example.ecommerceplatform.server.interceptor;

import com.example.ecommerceplatform.common.Exception.UnauthorizedException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.Result.Result;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.common.properties.JwtProperties;
import com.example.ecommerceplatform.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截到管理员端请求");
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 1. 从请求头中获取令牌 (authentication)
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        try {
            log.info("C端用户拦截器: jwt校验: {}", token);
            Claims claims = JwtUtils.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long id = Long.valueOf(claims.get("id").toString());

            // 存入 Context
            BaseContext.setCurrentId(id);

            log.info("当前管理员ID: {}", id);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            throw new UnauthorizedException(ErrorCode.PARAM_ERROR);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("管理员请求完成");
        BaseContext.removeCurrentId();
    }
}

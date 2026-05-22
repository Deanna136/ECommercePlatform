package com.example.ecommerceplatform.server.interceptor;

import com.example.ecommerceplatform.common.Exception.UnauthorizedException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import com.example.ecommerceplatform.common.context.BaseContext;
import com.example.ecommerceplatform.common.properties.JwtProperties;
import com.example.ecommerceplatform.common.utils.JwtUtil;
import com.example.ecommerceplatform.common.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenBuyerInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisUtil redisUtil;

    private static final String TOKEN_KEY = "token:buyer:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截到买家端请求");
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 1. 从请求头获取 Token
        String token = request.getHeader(jwtProperties.getBuyerTokenName());

        try {
            log.info("买家拦截器: jwt校验: {}", token);

            // 2. 验证 JWT 合法性（过期/篡改会抛异常）
            Claims claims = JwtUtil.parseJWT(jwtProperties.getBuyerSecretKey(), token);
            Long id = Long.valueOf(claims.get("id").toString());

            // 3. 验证 Redis 白名单（token 是否仍有效，登出后此处会失败）
            String redisToken = (String) redisUtil.get(TOKEN_KEY + id);
            if (redisToken == null || !redisToken.equals(token)) {
                log.info("买家 Token 不在白名单中，已登出或被踢下线");
                response.setStatus(401);
                throw new UnauthorizedException(ErrorCode.TOKEN_INVALID);
            }

            // 4. 存入 ThreadLocal
            BaseContext.setCurrentId(id);
            log.info("当前买家ID: {}", id);
            return true;

        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            response.setStatus(401);
            throw new UnauthorizedException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        log.info("买家请求完成");
        BaseContext.removeCurrentId();
    }
}

package com.example.ecommerceplatform.common.utils;

import com.example.ecommerceplatform.common.Exception.UnauthorizedException;
import com.example.ecommerceplatform.common.Result.ErrorCode;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)(设置为2小时)
     * @param claims    设置的信息
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(exp);

        return builder.compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey, String token) {
        try{
            // 得到DefaultJwtParser
            Claims claims = Jwts.parser()
                    // 设置签名的秘钥
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    // 设置需要解析的jwt
                    .parseClaimsJws(token).getBody();
            return claims;
        } catch (ExpiredJwtException e) {
            // 1. Token过期
            log.error("Token已过期: {}", e.getMessage());
            throw new UnauthorizedException(ErrorCode.TOKEN_EXPIRED);  // 自定义过期错误码
        } catch (UnsupportedJwtException e) {
            // 2. Token格式不支持
            log.error("Token格式错误: {}", e.getMessage());
            throw new UnauthorizedException(ErrorCode.TOKEN_INVALID);
        } catch (MalformedJwtException e) {
            // 3. Token结构错误
            log.error("Token结构错误: {}", e.getMessage());
            throw new UnauthorizedException(ErrorCode.TOKEN_MALFORMED);
        } catch (SignatureException e) {
            // 4. 签名验证失败
            log.error("Token签名错误: {}", e.getMessage());
            throw new UnauthorizedException(ErrorCode.TOKEN_SIGNATURE_ERROR);
        } catch (IllegalArgumentException e) {
            // 5. 参数错误（如token为null）
            log.error("Token参数错误: {}", e.getMessage());
            throw new UnauthorizedException(ErrorCode.TOKEN_ILLEGAL_ARGUMENT);
        } catch (Exception e) {
            // 6. 其他未知异常
            log.error("Token解析未知错误: {}", e.getMessage());
            throw new UnauthorizedException(ErrorCode.TOKEN_UNKNOWN_ERROR);
        }
    }
}

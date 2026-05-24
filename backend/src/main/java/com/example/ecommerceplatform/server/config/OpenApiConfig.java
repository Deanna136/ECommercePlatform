package com.example.ecommerceplatform.server.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "综合型电商平台API", version = "v1"),
        security = {
                @SecurityRequirement(name = "AdminAuth"),
                @SecurityRequirement(name = "BuyerAuth"),
                @SecurityRequirement(name = "SellerAuth")
        }
)
@SecuritySchemes({
        @SecurityScheme(name = "AdminAuth", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, paramName = "admin_token"),
        @SecurityScheme(name = "BuyerAuth", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, paramName = "buyer_token"),
        @SecurityScheme(name = "SellerAuth", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, paramName = "seller_token")
})
public class OpenApiConfig {
}
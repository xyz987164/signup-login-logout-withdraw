package com.solutionChallenge.demo.global.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        // Bearer Token 인증 방식 설정
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("회원관리 API 문서")
                        .version("1.0.0")
                        .description("회원가입, 로그인, 로그아웃, 정보조회, 회원탈퇴 기능을 담은 API 문서입니다.")
                        .contact(new Contact()
                                .name("UG HONGSEOHYEON")
                                .url("https://github.com/chaesiktak/chaesiktak-BE"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", bearerAuth))
                .addSecurityItem(securityRequirement);
    }
}

package com.project.demo.app.controller;

import com.project.demo.app.domain.User;
import com.project.demo.app.service.Oauth2LoginService;
import com.project.demo.global.dto.ApiResponseTemplete;
import com.project.demo.global.exception.SuccessCode;
import com.project.demo.global.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class Oauth2LoginController {

    private final Oauth2LoginService oauth2LoginService;
    private final TokenService tokenService;

    public Oauth2LoginController(Oauth2LoginService oauth2LoginService, TokenService tokenService) {
        this.oauth2LoginService = oauth2LoginService;
        this.tokenService = tokenService;
    }

    @GetMapping("/code/{registrationId}")
    public ResponseEntity<ApiResponseTemplete<Map<String, String>>> oauth2Login(
            @RequestParam String code,
            @PathVariable String registrationId
    ) {
        User user = oauth2LoginService.socialLogin(code, registrationId);

        // JWT 토큰 발급
        String accessToken = tokenService.createAccessToken(user.getEmail());

        // 토큰 정보를 Map으로 담기
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("email", user.getEmail());

        // 성공 코드 넣어서 ApiResponseTemplete로 반환
        return ApiResponseTemplete.success(SuccessCode.LOGIN_USER_SUCCESS, tokens);
    }
}

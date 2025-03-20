package com.project.demo.app.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private final String accessToken;
    private final String refreshToken;
    private final String email;

    @Builder
    public LoginResponseDto(String accessToken, String refreshToken, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
    }
}

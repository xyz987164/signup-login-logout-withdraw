package com.project.demo.app.controller;


import com.project.demo.app.dto.UserMypageDto;
import com.project.demo.global.dto.ApiResponseTemplete;
import com.project.demo.app.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/check")
@RequiredArgsConstructor
public class UserValidationController {

    private final UserRepository userRepository;

    // 이메일 형식 검증 정규식 (영문, 숫자, 특수문자(+_.-)@영문, 숫자, 특수문자(-).영문(2~6자리))
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    // 닉네임 정규식 (한글, 영어, 숫자만 허용)
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]+$");

    /**
     * 이메일 중복 확인
     */
    @Operation(summary = "이메일 중복확인 API (토큰 인증 불필요)", security = @SecurityRequirement(name = ""))
    @PostMapping("/email")
    public ResponseEntity<ApiResponseTemplete<Boolean>> checkEmail(@Valid @RequestBody UserMypageDto.UserEmailDto emailDto) {
        String email = emailDto.getEmail();

        // 예외처리1. 잘못된 이메일 형식
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ResponseEntity.badRequest().body(
                    ApiResponseTemplete.<Boolean>builder()
                            .status(400)
                            .success(false)
                            .message("올바른 이메일 주소를 입력해주세요.")
                            .data(null)
                            .build()
            );
        }

        boolean isDuplicate = userRepository.findByEmail(email).isPresent();

        return ResponseEntity.ok(ApiResponseTemplete.<Boolean>builder()
                .status(200)
                .success(!isDuplicate)
                .message(isDuplicate ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.")
                .data(isDuplicate)
                .build());
    }

    /**
     * 닉네임 중복 확인 API
     */
    @Operation(summary = "닉네임 중복확인 API (토큰 인증 불필요)", security = @SecurityRequirement(name = ""))
    @PostMapping("/nickname")
    public ResponseEntity<ApiResponseTemplete<Boolean>> checkNickname(@Valid @RequestBody UserMypageDto.UserNicknameDto nicknameDto) {
        String nickname = nicknameDto.getUserNickName();

        // 예외처리1. 잘못된 닉네임 형식
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            return ResponseEntity.badRequest().body(
                    ApiResponseTemplete.<Boolean>builder()
                            .status(400)
                            .success(false)
                            .message("닉네임은 한글, 영어, 숫자만 입력 가능합니다.")
                            .data(null)
                            .build()
            );
        }

        boolean isDuplicate = userRepository.findByUserNickName(nickname).isPresent();

        return ResponseEntity.ok(ApiResponseTemplete.<Boolean>builder()
                .status(200)
                .success(!isDuplicate)
                .message(isDuplicate ? "이미 사용 중인 닉네임입니다." : "사용 가능한 닉네임입니다.")
                .data(isDuplicate)
                .build());
    }
}

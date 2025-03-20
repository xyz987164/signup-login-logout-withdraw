package com.project.demo.app.controller;


import com.project.demo.app.dto.UserMypageDto;
import com.project.demo.app.dto.UserWithdrawalDto;
import com.project.demo.app.service.UserService;
import com.project.demo.global.dto.ApiResponseTemplete;
import com.project.demo.global.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify/mypage")
@RequiredArgsConstructor
public class UserMypageController {

    private final UserService userService;
    private final TokenService tokenService;

    /**
     * 마이페이지 유저 정보 조회
     */
    @Operation(summary = "마이페이지 유저 정보 조회 API (Access 토큰 필요)")
    @GetMapping
    public ResponseEntity<ApiResponseTemplete<UserMypageDto>> getUserMypage(HttpServletRequest request) {
        String email = tokenService.extractAccessToken(request)
                .flatMap(tokenService::extractEmail)
                .orElse(null);

        if (email == null) {
            return ResponseEntity.status(401).body(
                    ApiResponseTemplete.<UserMypageDto>builder()
                            .status(401)
                            .success(false)
                            .message("인증되지 않은 사용자입니다.")
                            .data(null)
                            .build()
            );
        }

        UserMypageDto userMypageDto = userService.getUserMypage(email);

        return ResponseEntity.ok(
                ApiResponseTemplete.<UserMypageDto>builder()
                        .status(200)
                        .success(true)
                        .message("유저 정보 조회 성공")
                        .data(userMypageDto)
                        .build()
        );
    }

    /**
     * 유저 이름 변경
     */
    @Operation(summary = "유저 이름 변경 API (Access 토큰 필요)")
    @PatchMapping("/name")
    public ResponseEntity<ApiResponseTemplete<String>> updateUserName(
            HttpServletRequest request,
            @RequestBody UserMypageDto.UserNameDto dto) {

        String email = tokenService.extractAccessToken(request)
                .flatMap(tokenService::extractEmail)
                .orElse(null);

        if (email == null) {
            return ResponseEntity.status(401).body(
                    ApiResponseTemplete.<String>builder()
                            .status(401)
                            .success(false)
                            .message("인증되지 않은 사용자입니다.")
                            .data(null)
                            .build()
            );
        }

        boolean isUpdated = userService.updateUserName(email, dto.getUserName());

        if (isUpdated) {
            return ResponseEntity.ok(
                    ApiResponseTemplete.<String>builder()
                            .status(200)
                            .success(true)
                            .message("이름이 변경되었습니다.")
                            .data(dto.getUserName())
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    ApiResponseTemplete.<String>builder()
                            .status(400)
                            .success(false)
                            .message("이름 변경 실패")
                            .data(null)
                            .build()
            );
        }
    }

    /**
     * 유저 닉네임 변경
     */
    @Operation(summary = "유저 닉네임 변경 API (Access 토큰 필요)")
    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponseTemplete<String>> updateUserNickname(
            HttpServletRequest request,
            @RequestBody UserMypageDto.UserNicknameDto dto) {

        String email = tokenService.extractAccessToken(request)
                .flatMap(tokenService::extractEmail)
                .orElse(null);

        if (email == null) {
            return ResponseEntity.status(401).body(
                    ApiResponseTemplete.<String>builder()
                            .status(401)
                            .success(false)
                            .message("인증되지 않은 사용자입니다.")
                            .data(null)
                            .build()
            );
        }

        boolean isUpdated = userService.updateUserNickname(email, dto.getUserNickName());

        if (isUpdated) {
            return ResponseEntity.ok(
                    ApiResponseTemplete.<String>builder()
                            .status(200)
                            .success(true)
                            .message("닉네임이 변경되었습니다.")
                            .data(dto.getUserNickName())
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    ApiResponseTemplete.<String>builder()
                            .status(400)
                            .success(false)
                            .message("닉네임 변경 실패")
                            .data(null)
                            .build()
            );
        }
    }

    // 회원 탈퇴
    @Operation(summary = "유저 회원 탈퇴 API (Access 토큰 필요)")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponseTemplete<String>> withdrawUser(
            HttpServletRequest request,
            @RequestBody UserWithdrawalDto dto){

        String email = tokenService.extractAccessToken(request)
                .flatMap(tokenService::extractEmail)
                .orElse(null);

        if (email == null) {
            return ResponseEntity.status(401).body(
                    ApiResponseTemplete.<String>builder()
                            .status(401)
                            .success(false)
                            .message("인증되지 않은 사용자입니다.")
                            .data(null)
                            .build()
            );
        }
        // 회원 탈퇴 로직 실행
        userService.withdrawUser(email, dto.getReason());

        return ResponseEntity.ok(
                ApiResponseTemplete.<String>builder()
                        .status(200)
                        .success(true)
                        .message("회원 탈퇴가 완료되었습니다.")
                        .data("탈퇴 사유: " + dto.getReason())
                        .build()
        );
    }

}

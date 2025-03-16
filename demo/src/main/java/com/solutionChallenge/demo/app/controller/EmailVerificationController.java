package com.solutionChallenge.demo.app.controller;


import com.solutionChallenge.demo.app.dto.LoginRequestDto;
import com.solutionChallenge.demo.app.service.EmailVerificationService;
import com.solutionChallenge.demo.global.dto.ApiResponseTemplete;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    /**
     * 이메일 인증 API : 사용자가 이메일 인증 링크 클릭시 해당 API 호출, 인증 처리.
     */
    @Operation(summary = "이메일 인증 내부 API (토큰 인증 불필요)", security = @SecurityRequirement(name = ""))
    @GetMapping("/email")
    public ResponseEntity<ApiResponseTemplete<String>> verifyEmail(@RequestParam String token) {
        boolean isVerified = emailVerificationService.verifyEmail(token).isSuccess();

        if (isVerified) {
            return ResponseEntity.ok(
                    ApiResponseTemplete.<String>builder()
                            .status(200)
                            .success(true)
                            .message("이메일 인증이 완료되었습니다.")
                            .data("이제, 로그인이 가능합니다!")
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    ApiResponseTemplete.<String>builder()
                            .status(400)
                            .success(false)
                            .message("이메일 인증에 실패하였습니다.")
                            .data("토큰이 만료되었거나 유효하지 않습니다! 다시 요청해주세요.")
                            .build()
            );
        }
    }
    /**
     * 이메일 인증 재전송 API : 사용자가 이메일 인증을 하지 않았을 경우, 이메일 재전송 요청 처리.
     */
    @Operation(summary = "이메일 재전송 내부 API (토큰 인증 불필요)", security = @SecurityRequirement(name = ""))
    @PostMapping("/resend")
    public ApiResponseTemplete<String> resendVerificationEmail(@RequestBody LoginRequestDto loginRequestDto) {
        return emailVerificationService.resendVerificationEmail(loginRequestDto.getEmail(), loginRequestDto.getPassword());
    }
}

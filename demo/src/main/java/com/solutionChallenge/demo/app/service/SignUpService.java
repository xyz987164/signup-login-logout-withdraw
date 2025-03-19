package com.solutionChallenge.demo.app.service;

import com.solutionChallenge.demo.app.domain.Provider;
import com.solutionChallenge.demo.app.domain.RoleType;
import com.solutionChallenge.demo.app.domain.User;
import com.solutionChallenge.demo.app.dto.UserSignUpDto;
import com.solutionChallenge.demo.app.repository.UserRepository;
import com.solutionChallenge.demo.global.dto.ApiResponseTemplete;
import com.solutionChallenge.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public ApiResponseTemplete<UserSignUpDto> signUp(UserSignUpDto userSignUpDto) {
        // 예외처리 1: 필수 필드 누락 시 예외 처리
        if (userSignUpDto.getEmail() == null || userSignUpDto.getEmail().isBlank() ||
                userSignUpDto.getPassword() == null || userSignUpDto.getPassword().isBlank() ||
                userSignUpDto.getUserName() == null || userSignUpDto.getUserName().isBlank() ||
                userSignUpDto.getUserNickName() == null || userSignUpDto.getUserNickName().isBlank()) {

            return failureResponse(400, "필수 입력 항목이 누락되었습니다. (이메일, 비밀번호, 이름, 닉네임)");
        }

        // 예외처리 2: 공백 포함 여부 체크 (이메일, 비밀번호, 닉네임, 사용자 이름)
        if (userSignUpDto.getEmail().matches(".*\\s.*") ||
                userSignUpDto.getPassword().matches(".*\\s.*") ||
                userSignUpDto.getUserName().matches(".*\\s.*") ||
                userSignUpDto.getUserNickName().matches(".*\\s.*")) {

            return failureResponse(400, "입력값에 공백이 포함될 수 없습니다. (이메일, 비밀번호, 이름, 닉네임)");
        }

        // 예외처리 3: 이메일 중복 검사
        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            return failureResponse(400, ErrorCode.ALREADY_EXIT_EMAIL_EXCEPTION.getMessage());
        }

        // 예외처리 4: 닉네임 중복 검사
        if (userRepository.findByUserNickName(userSignUpDto.getUserNickName()).isPresent()) {
            return failureResponse(400, ErrorCode.ALREADY_EXIT_NICKNAME_EXCEPTION.getMessage());
        }

        try {
            // 사용자 저장 (트랜잭션 내부)
            User user = saveUser(userSignUpDto);

            // 이메일 인증 발송 (트랜잭션 외부)
            emailVerificationService.sendVerificationEmail(user.getEmail());

            return successResponse(userSignUpDto);
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            return failureResponse(500, ErrorCode.EMAIL_CERTIFICATION_SEND_MISSING_EXCEPTION.getMessage());
        }
    }

    @Transactional
    private User saveUser(UserSignUpDto userSignUpDto) {
        User user = User.builder()
                .email(userSignUpDto.getEmail().trim())
                .password(passwordEncoder.encode(userSignUpDto.getPassword().trim()))
                .userName(userSignUpDto.getUserName().trim())
                .userNickName(userSignUpDto.getUserNickName().trim())
                .roleType(RoleType.USER)
                .emailVerified(false)
                .provider(Provider.NORMAL)
                .providerId(null)
                .build();
        return userRepository.save(user);
    }

    private ApiResponseTemplete<UserSignUpDto> successResponse(UserSignUpDto userSignUpDto) {
        return ApiResponseTemplete.<UserSignUpDto>builder()
                .status(200)
                .success(true)
                .message("회원가입 성공! 이메일 인증을 완료해주세요.")
                .data(userSignUpDto)
                .build();
    }

    private ApiResponseTemplete<UserSignUpDto> failureResponse(int status, String message) {
        return ApiResponseTemplete.<UserSignUpDto>builder()
                .status(status)
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}

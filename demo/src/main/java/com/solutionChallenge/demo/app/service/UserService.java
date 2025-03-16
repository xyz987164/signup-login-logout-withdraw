package com.solutionChallenge.demo.app.service;


import com.solutionChallenge.demo.app.domain.User;
import com.solutionChallenge.demo.app.dto.UserMypageDto;
import com.solutionChallenge.demo.app.entity.WithdrawalEntity;
import com.solutionChallenge.demo.app.repository.UserRepository;
import com.solutionChallenge.demo.app.repository.WithdrawalRepository;
import com.solutionChallenge.demo.global.exception.ErrorCode;
import com.solutionChallenge.demo.global.exception.model.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WithdrawalRepository withdrawalRepository;

    /**
     * 유저 정보 조회
     */
    public UserMypageDto getUserMypage(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));

        return new UserMypageDto(user.getEmail(), user.getUserName(), user.getUserNickName());
    }


    /**
     * 유저 이름 변경
     */
    @Transactional
    public boolean updateUserName(String email, String userName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NOT_FOUND_USER_EXCEPTION,
                        ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()
                ));
        user.setUserName(userName);
        userRepository.save(user);
        return true;
    }

    /**
     * 유저 닉네임 변경
     */
    @Transactional
    public boolean updateUserNickname(String email, String userNickName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NOT_FOUND_USER_EXCEPTION,
                        ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()
                ));
        user.setUserNickName(userNickName);
        userRepository.save(user);
        return true;
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void withdrawUser(String email, String reason) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NOT_FOUND_USER_EXCEPTION,
                        "유저를 찾을 수 없습니다."
                ));

        WithdrawalEntity withdrawal = WithdrawalEntity.builder()
                .email(user.getEmail())
                .reason(reason)
                .build();
        withdrawalRepository.save(withdrawal);

        userRepository.delete(user);
    }
}
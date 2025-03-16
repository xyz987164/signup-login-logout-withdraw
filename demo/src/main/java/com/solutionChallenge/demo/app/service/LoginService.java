package com.solutionChallenge.demo.app.service;

import com.solutionChallenge.demo.app.domain.User;
import com.solutionChallenge.demo.app.dto.CustomUserDetails;
import com.solutionChallenge.demo.app.repository.UserRepository;
import com.solutionChallenge.demo.global.exception.ErrorCode;
import com.solutionChallenge.demo.global.exception.model.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NOT_FOUND_USER_EXCEPTION,
                        ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()
                ));

        return new CustomUserDetails(user);
    }

    /**
     * 이메일 인증 여부 확인
     */
    public boolean isEmailVerified(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NOT_FOUND_USER_EXCEPTION,
                        ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()
                ));

        return user.getEmailVerified();
    }
}

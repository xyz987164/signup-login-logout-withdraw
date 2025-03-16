package com.solutionChallenge.demo.global.config;


import com.solutionChallenge.demo.app.dto.CustomUserDetails;
import com.solutionChallenge.demo.global.security.TokenService;
import com.solutionChallenge.demo.app.service.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig implements WebMvcConfigurer {

    private final LoginService loginService;
    private final TokenService tokenService;


    /**
     * 비밀번호 암호화 설정.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * AuthenticationManager 설정
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(loginService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(authProvider));
    }

    /**
     * SecurityFilterChain 설정 (JWT 토큰 필터 포함)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 비활성화
                .authorizeHttpRequests(auth -> auth
                        // Token 인증이 필요없는 API들을 추가하는 부분
                        .requestMatchers("/api/sign-up", "/api/check/email", "/api/check/nickname", "/api/login",
                                "/api/verify/email", "/api/verify/resend", "/api/verify/reset-password",
                                "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**")
                        .permitAll() // 특정 요청 허용
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\":\"Unauthorized: " + authException.getMessage() + "\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\":\"Forbidden: " + accessDeniedException.getMessage() + "\"}");
                        })
                )
                .addFilterBefore(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                            throws ServletException, IOException {

                        String token = getTokenFromHeader(request);

                        if (token != null) {
                            try {
                                tokenService.validateToken(token);
                                String email = tokenService.extractEmail(token)
                                        .orElseThrow(() -> new IllegalArgumentException("Token does not contain email"));

                                // CustomUserDetails를 사용하여 Spring Security 인증 설정
                                CustomUserDetails customUserDetails = (CustomUserDetails) loginService.loadUserByUsername(email);
                                Authentication authToken = new UsernamePasswordAuthenticationToken(
                                        customUserDetails, null, customUserDetails.getAuthorities()
                                );
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                            } catch (IllegalArgumentException e) {
                                log.error("토큰에서 이메일 추출 실패: {}", e.getMessage());
                                sendErrorResponse(response);
                                return;
                            } catch (Exception e) {
                                log.error("JWT 필터 처리 중 오류 발생: {}", e.getMessage(), e);
                                sendErrorResponse(response);
                                return;
                            }
                        }

                        filterChain.doFilter(request, response);
                    }
                }, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Authorization 헤더에서 토큰을 추출
     */
    private String getTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);  // "Bearer " 이후의 토큰 반환
        }
        return null;
    }

    /**
     * 예외 발생 시 JSON 형식으로 응답을 반환하는 메서드
     */
    private void sendErrorResponse(HttpServletResponse response) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"" + "Invalid or expired token" + "\"}");
        } catch (IOException e) {
            log.error("에러 응답 처리 중 IOException 발생: {}", e.getMessage(), e);
        }
    }



}

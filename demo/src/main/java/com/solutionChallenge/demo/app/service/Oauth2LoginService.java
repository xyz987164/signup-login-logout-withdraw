package com.solutionChallenge.demo.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.solutionChallenge.demo.app.domain.Provider;
import com.solutionChallenge.demo.app.domain.RoleType;
import com.solutionChallenge.demo.app.domain.User;
import com.solutionChallenge.demo.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Oauth2LoginService {

    private final Environment env;
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;

    /*
    public Oauth2LoginService(Environment env) {
        this.env = env;
    }*/
    public User socialLogin(String code, String registrationId) {
        JsonNode tokenResponse = getTokenResponse(code, registrationId);
        System.out.println(tokenResponse);// 전체 응답 가져옴
        String accessToken = tokenResponse.get("access_token").asText();
        //JsonNode userResourceNode = getUserResource(accessToken, registrationId);
        String refreshToken = tokenResponse.has("refresh_token") ? tokenResponse.get("refresh_token").asText() : null;
        // String refreshToken = getTokenResponse().has("refresh_token") ? getTokenResponse().get("refresh_token").asText() : null;
        // System.out.println("userResourceNode = " + userResourceNode);

        JsonNode userResourceNode = getUserResource(accessToken, registrationId);
        String id = userResourceNode.get("id").asText();
        String email = userResourceNode.get("email").asText();
        String rawNickname = userResourceNode.get("name").asText();
        String nickname = new String(rawNickname.getBytes(), StandardCharsets.UTF_8);

        return saveOrUpdateUser(id, email, nickname, registrationId, refreshToken);

    }

    private JsonNode getTokenResponse(String authorizationCode, String registrationId) {
        String clientId = env.getProperty("oauth2." + registrationId + ".client-id");
        String clientSecret = env.getProperty("oauth2." + registrationId + ".client-secret");
        String redirectUri = env.getProperty("oauth2." + registrationId + ".redirect-uri");
        String tokenUri = env.getProperty("oauth2." + registrationId + ".token-uri");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
/*
        HttpEntity entity = new HttpEntity(params, headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();
        return accessTokenNode.get("access_token").asText();*/
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        // 🔹 Google OAuth2 서버로 요청을 보내서 응답 받기
        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);

        // 🔹 전체 응답을 반환
        return responseNode.getBody();
    }

    private JsonNode getUserResource(String accessToken, String registrationId) {
        String resourceUri = env.getProperty("oauth2."+registrationId+".resource-uri");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
    }

    private User saveOrUpdateUser(String id, String email, String nickname, String provider, String refreshToken) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            return userRepository.save(user);
        }

        User newUser = User.builder()
                .email(email)
                .userName(nickname)
                .userNickName(nickname)
                .emailVerified(true)
                .roleType(RoleType.USER)
                .provider(Provider.GOOGLE)
                .providerId(id)
                .refreshToken(refreshToken)
                .build();

        return userRepository.save(newUser);
    }
}
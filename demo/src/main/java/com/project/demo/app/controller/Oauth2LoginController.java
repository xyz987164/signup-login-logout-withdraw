package com.project.demo.app.controller;

import com.project.demo.app.domain.User;
import com.project.demo.app.service.Oauth2LoginService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class Oauth2LoginController {

    Oauth2LoginService oauth2LoginService;

    public Oauth2LoginController(Oauth2LoginService oauth2LoginService) {
        this.oauth2LoginService = oauth2LoginService;
    }

    @GetMapping("/code/{registrationId}")
    public String googleLogin(@RequestParam String code, @PathVariable String registrationId) {
        User user = oauth2LoginService.socialLogin(code, registrationId);
        return "로그인 성공: " + user.getEmail();
    }
}
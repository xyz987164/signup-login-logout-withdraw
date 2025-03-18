package com.solutionChallenge.demo.app.controller;

import com.solutionChallenge.demo.app.service.Oauth2LoginService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class Oauth2LoginController {

    Oauth2LoginService oauth2LoginService;

    public Oauth2LoginController(Oauth2LoginService oauth2LoginService) {
        this.oauth2LoginService = oauth2LoginService;
    }

    @GetMapping("/code/{registrationId}")
    public void googleLogin(@RequestParam String code, @PathVariable String registrationId) {
        oauth2LoginService.socialLogin(code, registrationId);
    }
}
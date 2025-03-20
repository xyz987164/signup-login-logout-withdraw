package com.project.demo.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpDto {
    private String email;
    private String password;
    private String userName;
    private String userNickName;
}
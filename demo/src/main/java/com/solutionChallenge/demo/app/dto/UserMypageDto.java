package com.solutionChallenge.demo.app.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserMypageDto {
    private String email;
    private String userNickName;
    private String userName;

    //유저 이메일
    @Getter
    @Setter
    public static class UserEmailDto {
        private String email;
    }

    // 유저 이름
    @Getter
    @Setter
    public static class UserNameDto {
        private String userName;
    }

    // 유저 닉네임
    @Getter
    @Setter
    public static class UserNicknameDto {
        private String userNickName;
    }


}

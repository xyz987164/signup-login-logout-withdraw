package com.project.demo.app.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "USER_EMAIL", referencedColumnName = "USER_EMAIL")
    private User user;

    private String token;
    private Date expirationTime;

    @Builder
    public EmailVerification(User user, String token, Date expirationTime) {
        this.user = user;
        this.token = token;
        this.expirationTime = expirationTime;
    }
}

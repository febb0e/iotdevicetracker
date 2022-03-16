package de.thkoeln.syp.team17.backend.auth;

import lombok.Getter;

@Getter
public class LoginRequest {

    private String login;
    private String password;
    private String totp;

}

package de.thkoeln.syp.team17.backend.auth.totp;

import lombok.Getter;

@Getter
public class SetupTotpCredentials {

    private String secret;
    private String qrCode;

    public SetupTotpCredentials(String secret, String qrCode) {
        this.secret = secret;
        this.qrCode = qrCode;
    }

}

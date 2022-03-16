package de.thkoeln.syp.team17.backend.auth.totp;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class SetupTotpForm {

    @NotNull
    @NotEmpty
    private String password;

}

package de.thkoeln.syp.team17.backend.auth;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class DeleteAccountForm {

    @NotNull
    @NotEmpty
    private String password;

}

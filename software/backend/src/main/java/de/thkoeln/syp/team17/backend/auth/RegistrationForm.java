package de.thkoeln.syp.team17.backend.auth;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class RegistrationForm {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @Length(min=8)
    private String password;

    @NotNull
    @NotEmpty
    private String passwordConfirmation;

}

package de.thkoeln.syp.team17.backend.auth;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class UpdateAccountForm {

    @NotNull
    @NotEmpty
    private String currentPassword;

    @NotEmpty
    private String username;

    @NotEmpty
    @Email
    private String email;

    @Length(min=8)
    private String newPassword;

    private String newPasswordConfirmation;

}

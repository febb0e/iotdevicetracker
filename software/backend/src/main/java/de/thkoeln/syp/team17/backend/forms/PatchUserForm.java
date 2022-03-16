package de.thkoeln.syp.team17.backend.forms;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
public class PatchUserForm {

    @NotEmpty
    private String username;

    @Email
    private String email;

    private Boolean verified;

    private String role;

}

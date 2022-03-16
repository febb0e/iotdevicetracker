package de.thkoeln.syp.team17.backend.auth.totp;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
public class VerifyTotpForm {

    @NotNull
    @Length(min=6, max=6)
    private String totp;

}

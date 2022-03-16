package de.thkoeln.syp.team17.backend.dto;

import de.thkoeln.syp.team17.backend.entities.User;
import de.thkoeln.syp.team17.backend.entities.enums.UserRole;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;

@Getter
public class UserDTO implements Serializable {

    private Long id;
    private String username;
    private String email;
    private boolean verified;
    private UserRole role;
    private Instant updatedAt;
    private Instant createdAt;
    private boolean mfaEnabled;

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        verified = user.isVerified();
        role = user.getEffectiveRole();
        updatedAt = user.getUpdatedAt();
        createdAt = user.getCreatedAt();
        mfaEnabled = user.getTotpAuthenticator() != null && user.getTotpAuthenticator().isVerified();
    }

}

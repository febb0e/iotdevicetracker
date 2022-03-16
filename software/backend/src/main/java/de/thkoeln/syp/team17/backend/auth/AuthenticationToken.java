package de.thkoeln.syp.team17.backend.auth;

import de.thkoeln.syp.team17.backend.entities.User;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collections;

@EqualsAndHashCode
public class AuthenticationToken extends AbstractAuthenticationToken {

    private final PrincipalImpl principal;
    private LoginRequest loginRequest;

    public AuthenticationToken(LoginRequest loginRequest) {
        super(null);
        this.principal = null;
        this.loginRequest = loginRequest;
        setAuthenticated(false);
    }

    public AuthenticationToken(User user) {
        super(Collections.unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_" + user.getEffectiveRole(), "ROLE_" + (user.isVerified() ? "VERIFIED" : "UNVERIFIED"))));
        this.principal = new PrincipalImpl(user);
        this.loginRequest = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return loginRequest;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.loginRequest = null;
    }

}

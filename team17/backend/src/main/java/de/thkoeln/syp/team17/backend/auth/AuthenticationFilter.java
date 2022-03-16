package de.thkoeln.syp.team17.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CommonsLog
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public AuthenticationFilter() {
        super(new AntPathRequestMatcher("/auth/login", "POST"));
    }

    @Autowired
    public RememberMeServices rememberMeServices;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper mapper = new ObjectMapper();
        LoginRequest loginRequest;

        try {
            log.debug("Extracting credentials from request");
            loginRequest = mapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (Exception e) {
            rememberMeServices.loginFail(request, response);
            throw new AuthenticationCredentialsNotFoundException("Credentials could not be extracted from request");
        }

        log.debug("Creating AuthenticationToken from request");
        AuthenticationToken token = new AuthenticationToken(loginRequest);

        log.debug("Trying to authenticate using the created AuthenticationToken");
        try {
            Authentication authentication = this.getAuthenticationManager().authenticate(token);
            rememberMeServices.loginSuccess(request, response, authentication);
            return authentication;
        } catch (AuthenticationException e) {
            rememberMeServices.loginFail(request, response);
            throw e;
        }
    }

}

package de.thkoeln.syp.team17.backend.auth;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@CommonsLog
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.debug("Handling AuthenticationException", authException);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("You must be logged in to access this resource.");
    }

}

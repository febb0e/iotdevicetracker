package de.thkoeln.syp.team17.backend.auth;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@CommonsLog
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.debug("Handling AccessDeniedException", accessDeniedException);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        if (accessDeniedException instanceof MissingCsrfTokenException) {
            response.getWriter().write("Missing CSRF token.");
        } else if (accessDeniedException instanceof InvalidCsrfTokenException) {
            response.getWriter().write("Invalid CSRF token.");
        } else {
            response.getWriter().write("You don't have access to this resource.");
        }
    }

}

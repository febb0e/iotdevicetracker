package de.thkoeln.syp.team17.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.syp.team17.backend.dto.UserDTO;
import de.thkoeln.syp.team17.backend.entities.User;
import de.thkoeln.syp.team17.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        Principal principal = (Principal) authentication.getPrincipal();
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        UserDTO userDTO = new UserDTO(user);
        response.getWriter().write(objectMapper.writeValueAsString(userDTO));
    }
}

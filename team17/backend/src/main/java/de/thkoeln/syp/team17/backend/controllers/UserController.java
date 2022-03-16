package de.thkoeln.syp.team17.backend.controllers;

import de.thkoeln.syp.team17.backend.auth.AuthenticationToken;
import de.thkoeln.syp.team17.backend.dto.UserDTO;
import de.thkoeln.syp.team17.backend.dto.UserListDTO;
import de.thkoeln.syp.team17.backend.entities.User;
import de.thkoeln.syp.team17.backend.entities.enums.UserRole;
import de.thkoeln.syp.team17.backend.exceptions.ValidationException;
import de.thkoeln.syp.team17.backend.forms.PatchUserForm;
import de.thkoeln.syp.team17.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    /**
     * Get User
     * @param username Search-field for the Username
     * @param page Determines which part of pages you receive if the amount exceeds 25, when omitted a value of 1 is assumed
     * @return A list of Users split into pages of size of 25 per page
     */
    @GetMapping
    public ResponseEntity<UserListDTO> getUser(@RequestParam(required = false) String username, @RequestParam() int page) {
        Pageable pageable = PageRequest.of(page - 1, 25);

        Page<User> userPage;
        if (username == null || username.isEmpty()) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.findByUsernameLike("%"+username+"%", pageable);
        }

        return ResponseEntity.ok(new UserListDTO(userPage));
    }

    /**
     * Edit User
     * @param principal User-Details
     * @param form Changes for the User
     * @param userId Targeted User
     * @param bindingResult validationErrors
     * @return Updated User
     */
    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<UserDTO> updateUser(@AuthenticationPrincipal Principal principal, @RequestBody @Valid PatchUserForm form, @PathVariable("id") long userId, BindingResult bindingResult) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find User with id:" + userId));

        UserRole role = null;

        // Validation
        List<ObjectError> validationErrors = new ArrayList<>(bindingResult.getAllErrors());
        // Check if username is already in use
        if (form.getUsername() != null) {
            Optional<User> existingUser = userRepository.findByUsername(form.getUsername());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                validationErrors.add(new FieldError(form.getClass().getName(), "username", "already exists"));
            }
        }
        if (form.getEmail() != null) {
            // Check if email is already in use
            Optional<User> existingUser = userRepository.findByEmail(form.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                validationErrors.add(new FieldError(form.getClass().getName(), "email", "already exists"));
            }
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }
        // Validate and get role.
        if (form.getRole() != null && !bindingResult.hasFieldErrors("role")) {
            switch (form.getRole()) {
                case "USER":
                    role = UserRole.USER;
                    break;
                case "SYSADMIN":
                    role = UserRole.SYSADMIN;
                    break;
                default:
                    validationErrors.add(new FieldError(form.getClass().getName(), "role", "Unknown role"));
            }
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        // Update data
        if (form.getUsername() != null) {
            user.setUsername(form.getUsername());
        }
        if (form.getEmail() != null) {
            user.setEmail(form.getEmail());
        }
        if (form.getVerified() != null) {
            user.setVerified(form.getVerified());
        }
        if (role != null) {
            user.setRole(role);
        }

        userRepository.save(user);

        // When the user updated itself, update authentication, because the username and authorities (roles) might have changed.
        if (principal.getName().equals(user.getUsername())) {
            SecurityContextHolder.getContext().setAuthentication(new AuthenticationToken(user));
        }

        return ResponseEntity.ok(new UserDTO(user));
    }

    /**
     * Delete User
     * @param principal User-Details
     * @param userId Targeted User
     */
    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(OK)
    public void deleteUser(@AuthenticationPrincipal Principal principal, @PathVariable("id") long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find User with id:" + userId));
        userRepository.delete(user);

        // Delete all sessions of the user, excluding the current session.
        for (Session session : sessionRepository.findByPrincipalName(user.getUsername()).values()) {
            sessionRepository.deleteById(session.getId());
        }

        // When the user deleted itself, reset authentication.
        if (principal.getName().equals(user.getUsername())) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

}

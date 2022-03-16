package de.thkoeln.syp.team17.backend.auth;

import de.thkoeln.syp.team17.backend.dto.UserDTO;
import de.thkoeln.syp.team17.backend.entities.User;
import de.thkoeln.syp.team17.backend.exceptions.ValidationException;
import de.thkoeln.syp.team17.backend.repositories.UserRepository;
import de.thkoeln.syp.team17.backend.services.EmailService;
import de.thkoeln.syp.team17.backend.services.VerificationTokenGenerator;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@CommonsLog
public class AuthController {

    private static final String ALREADY_EXISTS = "already exists";

    @Autowired
    private VerificationTokenGenerator verificationTokenGenerator;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    /**
     * Returns a CSRF Token
     * @param token Generated CSRF Token
     * @return CSRF Token
     */
    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    /**
     * Get details about the logged-in Account
     * @param principal User-details
     * @return Account-details
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@AuthenticationPrincipal Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        return ResponseEntity.ok(new UserDTO(user));
    }

    /**
     * Change Account-details
     * @param principal User-details
     * @param form Changes for Account
     * @param bindingResult validationErrors
     * @return Updated Account-details
     */
    @PatchMapping("/me")
    @Transactional
    public ResponseEntity<UserDTO> updateAccount(@AuthenticationPrincipal Principal principal, @RequestBody @Valid UpdateAccountForm form, BindingResult bindingResult) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Validation
        List<ObjectError> validationErrors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            validationErrors.addAll(bindingResult.getAllErrors());
        }
        // Validate password
        if (!bindingResult.hasFieldErrors("currentPassword") && !passwordEncoder.matches(form.getCurrentPassword(), user.getPassword())) {
            validationErrors.add(new FieldError(form.getClass().getName(), "currentPassword", "Incorrect password"));
        }
        // Check if username is already in use
        if (form.getUsername() != null && !form.getUsername().equals(user.getUsername())) {
            Optional<User> existingUser = userRepository.findByUsername(form.getUsername());
            if (existingUser.isPresent()) {
                validationErrors.add(new FieldError(form.getClass().getName(), "username", ALREADY_EXISTS));
            }
        }
        // Check if email is already in use
        if (form.getEmail() != null && !form.getEmail().equals(user.getEmail())) {
            Optional<User>  existingUser = userRepository.findByEmail(form.getEmail());
            if (existingUser.isPresent()) {
                validationErrors.add(new FieldError(form.getClass().getName(), "email", ALREADY_EXISTS));
            }
        }
        // Check if passwordConfirmation matches password.
        if (form.getNewPassword() != null && !form.getNewPasswordConfirmation().equals(form.getNewPassword())) {
            validationErrors.add(new FieldError(form.getClass().getName(), "newPasswordConfirmation", "does not match newPassword"));
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        // Update changed user data
        if (form.getUsername() != null && !form.getUsername().equals(user.getUsername())) {
            user.setUsername(form.getUsername());
        }
        if (form.getEmail() != null && !form.getEmail().equals(user.getEmail())) {
            user.setEmail(form.getEmail());
            user.setVerified(false);
            user.setVerificationToken(verificationTokenGenerator.generate());

            // Send the verification mail.
            try {
                emailService.sendVerificationToken(user);
            } catch (MailException e) {
                log.error("Failed to send verification mail", e);
            }
        }
        if (form.getNewPassword() != null) {
            user.setPassword(passwordEncoder.encode(form.getNewPassword()));
            // Expire all sessions of the user, excluding the current session.
            for (Session session : sessionRepository.findByPrincipalName(principal.getName()).values()) {
                if (!session.getId().equals(RequestContextHolder.currentRequestAttributes().getSessionId())) {
                    sessionRepository.deleteById(session.getId());
                }
            }
        }
        userRepository.save(user);

        // Reset authentication to apply new authorities (roles) and username.
        SecurityContextHolder.getContext().setAuthentication(new AuthenticationToken(user));

        return ResponseEntity.ok(new UserDTO(user));
    }

    /**
     * Delete Account
     * @param principal User-details
     * @param form DeletionForm
     * @param bindingResult validationErrors
     */
    @DeleteMapping("/me")
    @Transactional
    @ResponseStatus(OK)
    public void deleteAccount(@AuthenticationPrincipal Principal principal, @RequestBody @Valid DeleteAccountForm form, BindingResult bindingResult) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Validate password
        List<ObjectError> validationErrors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            validationErrors.addAll(bindingResult.getAllErrors());
        }
        if (!bindingResult.hasFieldErrors("password") && !passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            validationErrors.add(new FieldError(form.getClass().getName(), "password", "Incorrect password"));
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        // Delete the user.
        userRepository.delete(user);

        // Delete all sessions of the user.
        for (Session session : sessionRepository.findByPrincipalName(principal.getName()).values()) {
            sessionRepository.deleteById(session.getId());
        }

        // Reset authentication.
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * Register Account
     * @param form RegisteringForm
     * @param bindingResult validationErrors
     * @return Created Account-Details
     */
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UserDTO> register(@RequestBody @Valid RegistrationForm form, BindingResult bindingResult) {
        List<ObjectError> validationErrors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            validationErrors.addAll(bindingResult.getAllErrors());
        }
        // Check if passwordConfirmation matches password.
        if (!form.getPasswordConfirmation().equals(form.getPassword())) {
            validationErrors.add(new FieldError(form.getClass().getName(), "passwordConfirmation", "does not match password"));
        }
        // Check if username is already in use
        Optional<User> existingUser = userRepository.findByUsername(form.getUsername());
        if (existingUser.isPresent()) {
            validationErrors.add(new FieldError(form.getClass().getName(), "username", ALREADY_EXISTS));
        }
        // Check if email is already in use
        existingUser = userRepository.findByEmail(form.getEmail());
        if (existingUser.isPresent()) {
            validationErrors.add(new FieldError(form.getClass().getName(), "email", ALREADY_EXISTS));
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        // Create the new user from form input.
        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setVerificationToken(verificationTokenGenerator.generate());
        userRepository.save(user);

        // Send the verification mail.
        try {
            emailService.sendVerificationToken(user);
        } catch (MailException e) {
            log.error("Failed to send verification mail", e);
        }

        // Log the user in.
        SecurityContextHolder.getContext().setAuthentication(new AuthenticationToken(user));

        return ResponseEntity.ok(new UserDTO(user));
    }

    /**
     * Verify an Account
     * @param principal User-details
     * @param token VerificationToken
     * @return Account-Details
     */
    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<UserDTO> verify(@AuthenticationPrincipal Principal principal, @RequestParam("token") String token) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        if (user.isVerified()) {
            return ResponseEntity.ok().build();
        }
        if (user.getVerificationToken() == null) {
            return ResponseEntity.internalServerError().build();
        }
        if (!user.getVerificationToken().equals(token)) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY, "Invalid token");
        }
        user.setVerificationToken(null);
        user.setVerified(true);
        userRepository.save(user);

        // We need to reset the authentication, because authorities (roles) have changed.
        SecurityContextHolder.getContext().setAuthentication(new AuthenticationToken(user));

        return ResponseEntity.ok(new UserDTO(user));
    }

    // TODO: Password reset flow

}

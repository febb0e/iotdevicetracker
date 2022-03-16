package de.thkoeln.syp.team17.backend.auth.totp;

import de.thkoeln.syp.team17.backend.auth.AuthenticationToken;
import de.thkoeln.syp.team17.backend.entities.RecoveryCode;
import de.thkoeln.syp.team17.backend.entities.TOTPAuthenticator;
import de.thkoeln.syp.team17.backend.entities.User;
import de.thkoeln.syp.team17.backend.exceptions.ValidationException;
import de.thkoeln.syp.team17.backend.repositories.RecoveryCodeRepository;
import de.thkoeln.syp.team17.backend.repositories.TotpAuthenticatorRepository;
import de.thkoeln.syp.team17.backend.repositories.UserRepository;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = "/totp", produces = MediaType.APPLICATION_JSON_VALUE)
public class TotpController {

    private static final String PASSWORD_FIELD = "password";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TotpAuthenticatorRepository totpAuthenticatorRepository;

    @Autowired
    private RecoveryCodeRepository recoveryCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecretGenerator secretGenerator;

    @Autowired
    private QrDataFactory qrDataFactory;

    @Autowired
    private QrGenerator qrGenerator;

    @Autowired
    private CodeVerifier codeVerifier;

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    /**
     * Add Totp
     * @param principal User-Details
     * @param form TotpForm
     * @param bindingResult validationErrors
     * @return Totp Details
     * @throws QrGenerationException Failed generation of a QR-Code
     */
    @SneakyThrows
    @PostMapping("/setup")
    @Transactional
    public ResponseEntity<SetupTotpCredentials> setupTotp(@AuthenticationPrincipal Principal principal, @RequestBody @Valid SetupTotpForm form, BindingResult bindingResult) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Validate password
        List<ObjectError> validationErrors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            validationErrors.addAll(bindingResult.getAllErrors());
        }
        if (!bindingResult.hasFieldErrors(PASSWORD_FIELD) && !passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            validationErrors.add(new FieldError(form.getClass().getName(), PASSWORD_FIELD, "Incorrect password"));
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        // Ensure TOTP is not yet setup.
        if (user.getTotpAuthenticator() != null) {
            if (user.getTotpAuthenticator().isVerified()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "TOTP Authenticator is already setup.");
            } else {
                // If not already verified, remove the current TOTP authenticator to initiate a new setup.
                totpAuthenticatorRepository.delete(user.getTotpAuthenticator());
            }
        }

        // Generate TOTP secret and QrCode that the user can scan.
        String secret = secretGenerator.generate();
        QrData data = qrDataFactory.newBuilder()
                .label(user.getEmail())
                .secret(secret)
                .issuer("IoT Device Tracker")
                .digits(6)
                .period(30)
                .build();
        String qrCode = getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType());

        // Persist authenticator in DB.
        TOTPAuthenticator totpAuthenticator = new TOTPAuthenticator();
        totpAuthenticator.setSecret(secret);
        totpAuthenticator.setUser(user);
        totpAuthenticatorRepository.save(totpAuthenticator);

        // Respond with credentials required for setup.
        return ResponseEntity.ok(new SetupTotpCredentials(secret, qrCode));
    }

    /**
     * Verify Totp
     * @param principal User-Details
     * @param form VerificationForm
     * @param bindingResult ValidationErrors
     * @return List of recovery codes
     */
    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<String[]> verifiyTotp(@AuthenticationPrincipal Principal principal, @RequestBody @Valid VerifyTotpForm form, BindingResult bindingResult) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Ensure the TOTP Authenticator exists and is not already veriefied
        TOTPAuthenticator totpAuthenticator = user.getTotpAuthenticator();
        if (totpAuthenticator == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TOTP Authenticator not found.");
        }
        if (totpAuthenticator.isVerified()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "TOTP Authenticator already verified.");
        }

        // Check the provided TOTP.
        List<ObjectError> validationErrors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            validationErrors.addAll(bindingResult.getAllErrors());
        }
        if (!bindingResult.hasFieldErrors("totp") && !codeVerifier.isValidCode(totpAuthenticator.getSecret(), form.getTotp())) {
            validationErrors.add(new FieldError(form.getClass().getName(), "totp", "Incorrect TOTP"));
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        // Mark the authenticator as verified.
        totpAuthenticator.setVerified(true);
        totpAuthenticatorRepository.save(totpAuthenticator);

        // Generate 16 random recovery codes
        RecoveryCodeGenerator recoveryCodes = new RecoveryCodeGenerator();
        String[] codes = recoveryCodes.generateCodes(16);
        recoveryCodeRepository.saveAll(Arrays.stream(codes).map(code -> {
            RecoveryCode recoveryCode = new RecoveryCode();
            recoveryCode.setUser(user);
            recoveryCode.setCode(code);
            return recoveryCode;
        }).collect(Collectors.toList()));

        // Expire all sessions of the user, excluding the current session.
        for (Session session : sessionRepository.findByPrincipalName(principal.getName()).values()) {
            if (!session.getId().equals(RequestContextHolder.currentRequestAttributes().getSessionId())) {
                sessionRepository.deleteById(session.getId());
            }
        }

        // Reset authentication to apply new authorities (roles).
        SecurityContextHolder.getContext().setAuthentication(new AuthenticationToken(user));

        return ResponseEntity.ok(codes);
    }

    /**
     * Remove Totp
     * @param principal User-Details
     * @param form DisablingForm
     * @param bindingResult validationErrors
     */
    @PostMapping("/disable")
    @Transactional
    @ResponseStatus(OK)
    public void disableTotp(@AuthenticationPrincipal Principal principal, @RequestBody @Valid DisableTotpForm form, BindingResult bindingResult) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Validate password
        List<ObjectError> validationErrors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            validationErrors.addAll(bindingResult.getAllErrors());
        }
        if (!bindingResult.hasFieldErrors(PASSWORD_FIELD) && !passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            validationErrors.add(new FieldError(form.getClass().getName(), PASSWORD_FIELD, "Incorrect password"));
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        // Ensure the TOTP Authenticator exists
        TOTPAuthenticator totpAuthenticator = user.getTotpAuthenticator();
        if (totpAuthenticator == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TOTP Authenticator not found.");
        }

        // Delete the TOTP Authenticator
        totpAuthenticatorRepository.delete(totpAuthenticator);

        // Delete all remaining Recovery codes
        recoveryCodeRepository.deleteByUserId(user.getId());

        // Expire all sessions of the user, excluding the current session.
        for (Session session : sessionRepository.findByPrincipalName(principal.getName()).values()) {
            if (!session.getId().equals(RequestContextHolder.currentRequestAttributes().getSessionId())) {
                sessionRepository.deleteById(session.getId());
            }
        }

        // Reset authentication to apply new authorities (roles).
        SecurityContextHolder.getContext().setAuthentication(new AuthenticationToken(user));
    }
}

package de.thkoeln.syp.team17.backend.auth;

import de.thkoeln.syp.team17.backend.entities.RecoveryCode;
import de.thkoeln.syp.team17.backend.entities.TOTPAuthenticator;
import de.thkoeln.syp.team17.backend.entities.User;
import de.thkoeln.syp.team17.backend.repositories.RecoveryCodeRepository;
import de.thkoeln.syp.team17.backend.repositories.UserRepository;
import dev.samstevens.totp.code.CodeVerifier;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

@CommonsLog
public class AuthenticationProviderImpl implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecoveryCodeRepository recoveryCodeRepository;

    @Autowired
    private CodeVerifier verifier;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(AuthenticationToken.class, authentication, "Only AuthenticationToken is supported");
        log.debug("Extracting credentials");
        LoginRequest loginRequest = (LoginRequest) authentication.getCredentials();

        if (loginRequest.getLogin() == null || loginRequest.getLogin().isEmpty()) {
            throw new BadCredentialsException("Invalid login");
        }

        log.debug("Trying to find a user by email");
        Optional<User> foundUser = userRepository.findByEmail(loginRequest.getLogin());
        if (foundUser.isEmpty()) {
            log.debug("Trying to find a user by username");
            foundUser = userRepository.findByUsername(loginRequest.getLogin());
        }
        if (foundUser.isEmpty()) {
            throw new BadCredentialsException("Unknown login");
        }

        log.debug("User found - checking password");
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new BadCredentialsException("Invalid password");
        }
        boolean validPassword = passwordEncoder.matches(loginRequest.getPassword(), foundUser.get().getPassword());
        if (!validPassword) {
            throw new BadCredentialsException("Invalid password");
        }
        if (passwordEncoder.upgradeEncoding(foundUser.get().getPassword())) {
            log.debug("Password needs upgrading - re-encoding password");
            foundUser.get().setPassword(passwordEncoder.encode(loginRequest.getPassword()));
            userRepository.save(foundUser.get());
        }

        TOTPAuthenticator authenticator = foundUser.get().getTotpAuthenticator();
        if (authenticator != null && authenticator.isVerified()) {
            log.debug("User requires 2 step verification");
            boolean validTotp = false;
            if (loginRequest.getTotp() != null) {
                if (loginRequest.getTotp().length() == 6) {
                    // Try TOTP
                    validTotp = verifier.isValidCode(authenticator.getSecret(), loginRequest.getTotp());
                } else if (loginRequest.getTotp().length() == 19) {
                    // Try recovery code
                    Optional<RecoveryCode> recoveryCode = recoveryCodeRepository.findByUserIdAndCode(foundUser.get().getId(), loginRequest.getTotp());
                    if (recoveryCode.isPresent()) {
                        validTotp = true;
                        // Delete recovery code on usage
                        recoveryCodeRepository.delete(recoveryCode.get());
                    }
                }
            }
            if (!validTotp) {
                throw new BadCredentialsException("Invalid TOTP");
            }
        }

        log.debug("User successfully authenticated");

        return new AuthenticationToken(foundUser.get());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (AuthenticationToken.class.isAssignableFrom(authentication));
    }

}

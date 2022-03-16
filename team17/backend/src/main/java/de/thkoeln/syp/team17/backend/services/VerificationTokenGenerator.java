package de.thkoeln.syp.team17.backend.services;


import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class VerificationTokenGenerator {

    private final Random random = new SecureRandom();

    /**
     * Generates a 6 digit key consisting of uppercase letters and numbers.
     *
     * @return The generated key
     */
    public String generate() {
        String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            token.append(possible.charAt(random.ints(0, possible.length()).findFirst().orElseThrow()));
        }
        return token.toString();
    }

}

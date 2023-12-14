package com.backend.cms.security.jwt;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtSecretGenerator {

    public static void main(String[] args) {
        // Generate a 256-bit (32-byte) random secret
        byte[] secretBytes = generateRandomBytes(32);

        // Convert the secret bytes to a base64-encoded string
        String jwtSecret = Base64.getEncoder().encodeToString(secretBytes);

        System.out.println("Generated JWT secret: " + jwtSecret);
    }

    private static byte[] generateRandomBytes(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }
}

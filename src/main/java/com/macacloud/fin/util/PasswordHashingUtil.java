package com.macacloud.fin.util;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password Hashing Utility
 * Generating by Claude.ai;
 * <br />
 * Total length calculation:
 * Salt (24 chars) + Separator (1 char '$') + Hash (44 chars)
 * 24 + 1 + 44 = 69 characters
 *
 * @author Emmett
 * @since 2025/01/16
 */
public final class PasswordHashingUtil {

    // Prevent instantiation
    private PasswordHashingUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Argon2id parameters
    private static final int MEMORY = 65536;      // 64MB
    private static final int ITERATIONS = 3;
    private static final int PARALLELISM = 4;
    private static final int HASH_LENGTH = 32;    // 256 bits
    private static final int SALT_LENGTH = 16;    // 128 bits

    // Thread-safe SecureRandom instance
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Reusable Base64 encoders/decoders
    private static final Base64.Encoder B64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder B64_DECODER = Base64.getDecoder();

    /**
     * Hashes a password using Argon2id with a random salt
     *
     * @param password The password to hash
     * @return A string containing the encoded salt and hash, separated by '$'
     */
    public static String hashPassword(String password) {
        byte[] salt = generateSalt();
        byte[] hash = hashPassword(password.toCharArray(), salt);

        return encodeSaltAndHash(salt, hash);
    }

    /**
     * Verifies a password against a stored hash
     *
     * @param password   The password to verify
     * @param storedHash The stored hash string (including salt)
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split("\\$");
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = B64_DECODER.decode(parts[0]);
            byte[] expectedHash = B64_DECODER.decode(parts[1]);
            byte[] actualHash = hashPassword(password.toCharArray(), salt);

            return constantTimeEquals(expectedHash, actualHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    private static byte[] hashPassword(char[] password, byte[] salt) {
        byte[] hash = new byte[HASH_LENGTH];

        Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withMemoryAsKB(MEMORY)
                .withIterations(ITERATIONS)
                .withParallelism(PARALLELISM)
                .build();

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);
        generator.generateBytes(password, hash);

        return hash;
    }

    private static String encodeSaltAndHash(byte[] salt, byte[] hash) {
        return B64_ENCODER.encodeToString(salt) + "$" +
                B64_ENCODER.encodeToString(hash);
    }

    /**
     * Constant-time comparison of two byte arrays to prevent timing attacks
     */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}

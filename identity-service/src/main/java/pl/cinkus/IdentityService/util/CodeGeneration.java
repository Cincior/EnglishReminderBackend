package pl.cinkus.IdentityService.util;

import java.security.SecureRandom;

public class CodeGeneration {
    public static String generateCode() {
        SecureRandom random = new SecureRandom();
        int randomCode = random.nextInt(1_000_000);
        return String.format("%06d", randomCode);
    }
}

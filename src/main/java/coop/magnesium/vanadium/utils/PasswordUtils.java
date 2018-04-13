package coop.magnesium.vanadium.utils;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * Created by rsperoni on 05/05/17.
 */
public class PasswordUtils {

    public static String digestPassword(String plainTextPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(plainTextPassword.getBytes("UTF-8"));
            byte[] passwordDigest = md.digest();
            return new String(Base64.getEncoder().encode(passwordDigest));
        } catch (Exception e) {
            throw new RuntimeException("Exception encoding password", e);
        }
    }

}

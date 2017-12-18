package coop.magnesium.sulfur.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by rsperoni on 15/12/17.
 */
@JsonAutoDetect
public class DataRecuperacionPassword implements Serializable {
    private String email;
    private String token;
    private LocalDateTime expirationDate;

    public DataRecuperacionPassword(String email, String token, LocalDateTime expirationDate) {
        this.email = email;
        this.token = token;
        this.expirationDate = expirationDate;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }
}

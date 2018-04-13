package coop.magnesium.vanadium.db.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by rsperoni on 15/12/17.
 */
@Entity

public class RecuperacionPassword implements Serializable {
    private String email;
    @Id
    private String token;
    private LocalDateTime expirationDate;

    public RecuperacionPassword() {
    }

    public RecuperacionPassword(String email, String token, LocalDateTime expirationDate) {
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

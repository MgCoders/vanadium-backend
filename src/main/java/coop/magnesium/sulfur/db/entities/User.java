package coop.magnesium.sulfur.db.entities;


import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by rsperoni on 05/05/17.
 */
@Entity
public class User implements Serializable {

    @Id
    private String id;
    private String email;
    private String password;
    private String role = Role.USER.name();


    public User() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

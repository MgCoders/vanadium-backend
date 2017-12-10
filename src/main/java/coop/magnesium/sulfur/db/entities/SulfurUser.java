package coop.magnesium.sulfur.db.entities;


import javax.security.auth.Subject;
import java.security.Principal;

/**
 * Created by rsperoni on 05/05/17.
 */
public class SulfurUser implements Principal {

    private Long colaboradorId;
    private String role;


    public SulfurUser() {
    }

    public SulfurUser(Long colaboradorId, String role) {
        this.colaboradorId = colaboradorId;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getColaboradorId() {
        return colaboradorId;
    }

    public void setColaboradorId(Long colaboradorId) {
        this.colaboradorId = colaboradorId;
    }

    @Override
    public String getName() {
        return String.valueOf(colaboradorId);
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}

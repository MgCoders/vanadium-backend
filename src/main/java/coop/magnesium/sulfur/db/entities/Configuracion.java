package coop.magnesium.sulfur.db.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by rsperoni on 16/11/17.
 */
@Entity
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoConfiguracion clave;
    @NotNull
    private String valor;


    public Configuracion() {
    }

    public Configuracion(TipoConfiguracion clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoConfiguracion getClave() {
        return clave;
    }

    public void setClave(TipoConfiguracion clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Configuracion{" +
                "id=" + id +
                ", clave='" + clave + '\'' +
                ", valor='" + valor + '\'' +
                '}';
    }
}

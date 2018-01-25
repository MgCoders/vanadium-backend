package coop.magnesium.sulfur.db.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsperoni on 12/12/17.
 */
@Entity
@JsonAutoDetect
@ApiModel
public class EstimacionCargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    private Cargo cargo;
    @NotNull
    private BigDecimal precioTotal;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "estimacion_cargo_id")
    private List<EstimacionTipoTarea> estimacionTipoTareas = new ArrayList<>();


    public EstimacionCargo() {
    }

    public EstimacionCargo(Cargo cargo, BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
        this.cargo = cargo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EstimacionTipoTarea> getEstimacionTipoTareas() {
        return estimacionTipoTareas;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    @Override
    public String toString() {
        return "EstimacionCargo{" +
                "id=" + id +
                ", cargo=" + cargo.getCodigo() +
                ", precioTotal=" + precioTotal +
                ", estimacionTipoTareas=" + estimacionTipoTareas +
                '}';
    }
}

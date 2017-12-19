package coop.magnesium.sulfur.db.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by rsperoni on 18/12/17.
 */
@Embeddable
@JsonAutoDetect
@ApiModel
public class EstimacionDetalle {

    @NotNull
    @ManyToOne
    private TipoTarea tipoTarea;
    @NotNull
    @ManyToOne
    private Cargo cargo;
    @NotNull
    private Integer cantidadHoras;
    @NotNull
    private BigDecimal precioTotal;

    public EstimacionDetalle() {
    }

    public EstimacionDetalle(TipoTarea tipoTarea, Cargo cargo, Integer cantidadHoras, BigDecimal precioTotal) {
        this.tipoTarea = tipoTarea;
        this.cargo = cargo;
        this.cantidadHoras = cantidadHoras;
        this.precioTotal = precioTotal;
    }

    public TipoTarea getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(TipoTarea tipoTarea) {
        this.tipoTarea = tipoTarea;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Integer getCantidadHoras() {
        return cantidadHoras;
    }

    public void setCantidadHoras(Integer cantidadHoras) {
        this.cantidadHoras = cantidadHoras;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    @Override
    public String toString() {
        return "EstimacionDetalle{" +
                "tipoTarea=" + tipoTarea +
                ", cargo=" + cargo +
                ", cantidadHoras=" + cantidadHoras +
                ", precioTotal=" + precioTotal +
                '}';
    }
}

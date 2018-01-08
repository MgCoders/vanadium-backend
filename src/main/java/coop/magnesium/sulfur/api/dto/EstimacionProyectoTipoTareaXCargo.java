package coop.magnesium.sulfur.api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Created by rsperoni on 03/01/18.
 */
@JsonAutoDetect
@ApiModel
public class EstimacionProyectoTipoTareaXCargo {

    public Proyecto proyecto;
    public TipoTarea tipoTarea;
    public Cargo cargo;
    public BigDecimal precioTotal;
    @JsonIgnore
    public Duration cantidadHoras;

    public EstimacionProyectoTipoTareaXCargo() {
    }

    public EstimacionProyectoTipoTareaXCargo(Proyecto proyecto, TipoTarea tipoTarea, Cargo cargo, BigDecimal precioTotal, Long cantidadHoras) {
        this.proyecto = proyecto;
        this.tipoTarea = tipoTarea;
        this.cargo = cargo;
        this.precioTotal = precioTotal;
        this.cantidadHoras = Duration.ofNanos(cantidadHoras);
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public TipoTarea getTipoTarea() {
        return tipoTarea;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public Duration getCantidadHoras() {
        return cantidadHoras;
    }

    @Override
    public String toString() {
        return "EstimacionProyectoTipoTareaXCargo{" +
                "proyecto=" + proyecto +
                ", tipoTarea=" + tipoTarea +
                ", cargo=" + cargo +
                ", precioTotal=" + precioTotal +
                ", cantidadHoras=" + cantidadHoras +
                '}';
    }
}

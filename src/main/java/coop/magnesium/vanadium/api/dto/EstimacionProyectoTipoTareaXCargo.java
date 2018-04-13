package coop.magnesium.vanadium.api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import coop.magnesium.vanadium.db.entities.Cargo;
import coop.magnesium.vanadium.db.entities.Proyecto;
import coop.magnesium.vanadium.db.entities.TipoTarea;
import coop.magnesium.vanadium.utils.TimeUtils;
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
    public BigDecimal cantidadHoras;

    public EstimacionProyectoTipoTareaXCargo() {
    }

    public EstimacionProyectoTipoTareaXCargo(Proyecto proyecto, TipoTarea tipoTarea, Cargo cargo, BigDecimal precioTotal, BigDecimal cantidadHoras) {
        this.proyecto = proyecto;
        this.tipoTarea = tipoTarea;
        this.cargo = cargo;
        this.precioTotal = precioTotal;
        this.cantidadHoras = cantidadHoras;
    }

    public EstimacionProyectoTipoTareaXCargo(Proyecto proyecto, TipoTarea tipoTarea, Cargo cargo, BigDecimal precioTotal, Long cantidadHoras) {
        this.proyecto = proyecto;
        this.tipoTarea = tipoTarea;
        this.cargo = cargo;
        this.precioTotal = precioTotal;
        this.cantidadHoras = TimeUtils.durationToBigDecimal(Duration.ofNanos(cantidadHoras));
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

    public BigDecimal getCantidadHoras() {
        return cantidadHoras;
    }

    @Override
    public String toString() {
        return "EstimacionProyectoTipoTareaXCargo{" +
                "proyecto=" + proyecto.getCodigo() +
                ", tipoTarea=" + tipoTarea.getCodigo() +
                ", cargo=" + cargo.getCodigo() +
                ", precioTotal=" + precioTotal +
                ", cantidadHoras=" + cantidadHoras +
                '}';
    }
}

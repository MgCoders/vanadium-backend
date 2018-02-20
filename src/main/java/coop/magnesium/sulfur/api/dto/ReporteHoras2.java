package coop.magnesium.sulfur.api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;

/**
 * Created by rsperoni on 19/12/17.
 * Para extraer horas por proyecto y tipoTarea agrupadas por Cargo.
 * Con fila total con cargo en null.
 */
@JsonAutoDetect
@ApiModel
public class ReporteHoras2 {

    public BigDecimal cantidadHoras;
    public BigDecimal precioTotal;
    public BigDecimal cantidadHorasEstimadas;
    public BigDecimal precioEstimado;
    public Proyecto proyecto;
    public TipoTarea tipoTarea;
    public Cargo cargo;

    public ReporteHoras2() {
    }

    public ReporteHoras2(BigDecimal cantidadHoras, BigDecimal cantidadHorasEstimadas, BigDecimal precioEstimado, BigDecimal precioTotal, Proyecto proyecto, TipoTarea tipoTarea, Cargo cargo) {
        this.cantidadHoras = cantidadHoras;
        this.precioTotal = precioTotal;
        this.cantidadHorasEstimadas = cantidadHorasEstimadas;
        this.precioEstimado = precioEstimado;
        this.proyecto = proyecto;
        this.tipoTarea = tipoTarea;
        this.cargo = cargo;
    }

    public BigDecimal getCantidadHoras() {
        return cantidadHoras;
    }

    public void setCantidadHoras(BigDecimal cantidadHoras) {
        this.cantidadHoras = cantidadHoras;
    }

    public BigDecimal getCantidadHorasEstimadas() {
        return cantidadHorasEstimadas;
    }

    public void setCantidadHorasEstimadas(BigDecimal cantidadHorasEstimadas) {
        this.cantidadHorasEstimadas = cantidadHorasEstimadas;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }


    public BigDecimal getPrecioEstimado() {
        return precioEstimado;
    }

    public void setPrecioEstimado(BigDecimal precioEstimado) {
        this.precioEstimado = precioEstimado;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
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

    @Override
    public String toString() {
        return "ReporteHoras1{" +
                "cantidadHoras=" + cantidadHoras +
                ", precioTotal=" + precioTotal +
                ", cantidadHorasEstimadas=" + cantidadHorasEstimadas +
                ", precioEstimado=" + precioEstimado +
                ", proyecto=" + proyecto.getCodigo() +
                ", tipoTarea=" + ((tipoTarea != null) ? tipoTarea.getCodigo() : "-") +
                ", cargo=" + ((cargo != null) ? cargo.getCodigo() : "-") +
                '}';
    }
}

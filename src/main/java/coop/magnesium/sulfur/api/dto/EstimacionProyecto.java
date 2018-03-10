package coop.magnesium.sulfur.api.dto;

import java.math.BigDecimal;

/**
 * Created by rsperoni on 03/01/18.
 */
public class EstimacionProyecto {

    public Long proyecto_id;
    public Long tipoTarea_id;
    public Long cargo_id;
    public BigDecimal precioTotal;
    public Long duracion;

    public EstimacionProyecto() {
    }

    public EstimacionProyecto(Long proyecto_id, Long tipoTarea_id, Long cargo_id, BigDecimal precioTotal, Long duracion) {
        this.proyecto_id = proyecto_id;
        this.tipoTarea_id = tipoTarea_id;
        this.cargo_id = cargo_id;
        this.precioTotal = precioTotal;
        this.duracion = duracion;
    }

    public EstimacionProyecto(String proyecto_id, String tipoTarea_id, String cargo_id, String precioTotal, String duracion) {
        this.proyecto_id = Long.valueOf(proyecto_id);
        this.tipoTarea_id = Long.valueOf(tipoTarea_id);
        this.cargo_id = Long.valueOf(cargo_id);
        this.precioTotal = BigDecimal.valueOf(Long.valueOf(precioTotal));
        this.duracion = Long.valueOf(duracion);
    }

    public Long getProyecto_id() {
        return proyecto_id;
    }

    public void setProyecto_id(Long proyecto_id) {
        this.proyecto_id = proyecto_id;
    }

    public Long getTipoTarea_id() {
        return tipoTarea_id;
    }

    public void setTipoTarea_id(Long tipoTarea_id) {
        this.tipoTarea_id = tipoTarea_id;
    }

    public Long getCargo_id() {
        return cargo_id;
    }

    public void setCargo_id(Long cargo_id) {
        this.cargo_id = cargo_id;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Long getDuracion() {
        return duracion;
    }

    public void setDuracion(Long duracion) {
        this.duracion = duracion;
    }
}

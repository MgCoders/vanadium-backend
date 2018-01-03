package coop.magnesium.sulfur.api.dto;

import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Created by rsperoni on 03/01/18.
 */
public class EstimacionProyectoTipoTareaXCargo {

    public Proyecto proyecto;
    public TipoTarea tipoTarea;
    public Cargo cargo;
    public BigDecimal precioTotal;
    public Duration cantidadHoras;

    public EstimacionProyectoTipoTareaXCargo(Proyecto proyecto, TipoTarea tipoTarea, Cargo cargo, BigDecimal precioTotal, Long cantidadHoras) {
        this.proyecto = proyecto;
        this.tipoTarea = tipoTarea;
        this.cargo = cargo;
        this.precioTotal = precioTotal;
        this.cantidadHoras = Duration.ofNanos(cantidadHoras);
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

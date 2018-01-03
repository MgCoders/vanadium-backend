package coop.magnesium.sulfur.api.dto;

import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;

import java.time.Duration;

/**
 * Created by rsperoni on 19/12/17.
 * Para extraer horas por proyecto y tipoTarea agrupadas por Cargo del Colaborador.
 */
public class HorasProyectoTipoTareaXCargo {

    public Duration cantidadHoras;
    public Proyecto proyecto;
    public TipoTarea tipoTarea;
    public Cargo cargo;

    public HorasProyectoTipoTareaXCargo(Long cantidadHoras, Proyecto proyecto, TipoTarea tipoTarea, Cargo cargo) {
        this.cantidadHoras = Duration.ofNanos(cantidadHoras);
        this.proyecto = proyecto;
        this.tipoTarea = tipoTarea;
        this.cargo = cargo;
    }

    @Override
    public String toString() {
        return "HorasProyectoTipoTareaXCargo{" +
                "cantidadHoras=" + cantidadHoras +
                ", proyecto=" + proyecto +
                ", tipoTarea=" + tipoTarea +
                ", cargo=" + cargo +
                '}';
    }
}

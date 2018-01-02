package coop.magnesium.sulfur.api.dto;

import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Created by rsperoni on 19/12/17.
 */
public class HorasDeProyectoPorCargo {

    public Duration cantidadHoras;
    public LocalDate dia;
    public Proyecto proyecto;
    public TipoTarea tipoTarea;
    public Colaborador colaborador;

    public HorasDeProyectoPorCargo(Long cantidadHoras, LocalDate dia, Proyecto proyecto, TipoTarea tipoTarea, Colaborador colaborador) {
        this.cantidadHoras = Duration.ofNanos(cantidadHoras);
        this.dia = dia;
        this.proyecto = proyecto;
        this.tipoTarea = tipoTarea;
        this.colaborador = colaborador;
    }

    @Override
    public String toString() {
        return "HorasDeProyectoPorCargo{" +
                "cantidadHoras=" + cantidadHoras +
                ", dia=" + dia +
                ", proyecto=" + proyecto +
                ", tipoTarea=" + tipoTarea +
                ", colaborador=" + colaborador +
                '}';
    }
}

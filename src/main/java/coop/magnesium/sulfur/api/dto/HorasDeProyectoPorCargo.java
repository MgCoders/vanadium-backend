package coop.magnesium.sulfur.api.dto;

import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;

import java.time.LocalDate;

/**
 * Created by rsperoni on 19/12/17.
 */
public class HorasDeProyectoPorCargo extends ReporteRow {

    public float cantidadHoras;
    public LocalDate dia;
    public Cargo cargo;
    public Proyecto proyecto;
    public TipoTarea tipoTarea;
    public Colaborador colaborador;

}

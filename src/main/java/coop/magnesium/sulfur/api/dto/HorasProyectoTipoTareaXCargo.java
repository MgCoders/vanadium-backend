package coop.magnesium.sulfur.api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;
import io.swagger.annotations.ApiModel;

import java.time.Duration;

/**
 * Created by rsperoni on 19/12/17.
 * Para extraer horas por proyecto y tipoTarea agrupadas por Cargo del Colaborador.
 */
@JsonAutoDetect
@ApiModel
public class HorasProyectoTipoTareaXCargo {

    @JsonIgnore
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

    @JsonProperty
    public Long getCantidadHoras() {
        return cantidadHoras.toHours();
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

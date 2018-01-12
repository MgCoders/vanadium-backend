package coop.magnesium.sulfur.api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Proyecto;
import io.swagger.annotations.ApiModel;

import java.time.Duration;

/**
 * Created by rsperoni on 19/12/17.
 * Para extraer horas por proyecto y tipoTarea agrupadas por Cargo del Colaborador.
 */
@JsonAutoDetect
@ApiModel
public class HorasProyectoXCargo {

    @JsonIgnore
    public Duration cantidadHoras;
    public Proyecto proyecto;
    public Cargo cargo;

    public HorasProyectoXCargo() {
    }

    public HorasProyectoXCargo(Long cantidadHoras, Proyecto proyecto, Cargo cargo) {
        this.cantidadHoras = Duration.ofNanos(cantidadHoras);
        this.proyecto = proyecto;
        this.cargo = cargo;
    }

    @JsonProperty
    public Long getCantidadHoras() {
        return cantidadHoras.toHours();
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public Cargo getCargo() {
        return cargo;
    }

    @Override
    public String toString() {
        return "HorasProyectoTipoTareaXCargoDia{" +
                "cantidadHoras=" + cantidadHoras +
                ", proyecto=" + proyecto +
                ", cargo=" + cargo +
                '}';
    }
}

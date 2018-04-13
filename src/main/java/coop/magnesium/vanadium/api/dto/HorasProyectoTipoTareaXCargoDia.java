package coop.magnesium.vanadium.api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import coop.magnesium.vanadium.db.entities.Cargo;
import coop.magnesium.vanadium.utils.TimeUtils;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

/**
 * Created by rsperoni on 19/12/17.
 * Para extraer horas por proyecto y tipoTarea agrupadas por Cargo del Colaborador.
 */
@JsonAutoDetect
@ApiModel
public class HorasProyectoTipoTareaXCargoDia {

    public BigDecimal cantidadHoras;
    public Cargo cargo;
    public LocalDate dia;

    public HorasProyectoTipoTareaXCargoDia() {
    }

    public HorasProyectoTipoTareaXCargoDia(Long cantidadHoras, Cargo cargo, LocalDate dia) {
        this.cantidadHoras = TimeUtils.durationToBigDecimal(Duration.ofNanos(cantidadHoras));
        this.cargo = cargo;
        this.dia = dia;
    }

    public BigDecimal getCantidadHoras() {
        return cantidadHoras;
    }

    public void setCantidadHoras(BigDecimal cantidadHoras) {
        this.cantidadHoras = cantidadHoras;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }


    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    @Override
    public String toString() {
        return "HorasProyectoTipoTareaXCargoDia{" +
                "cantidadHoras=" + cantidadHoras +
                ", cargo=" + cargo.getCodigo() +
                ", dia=" + dia +
                '}';
    }
}

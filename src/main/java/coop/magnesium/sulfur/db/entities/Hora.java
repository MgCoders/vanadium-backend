package coop.magnesium.sulfur.db.entities;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by rsperoni on 17/11/17.
 */
@Entity
@JsonAutoDetect
public class Hora {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private LocalDate dia;
    @NotNull
    private LocalTime horaIn;
    @NotNull
    private LocalTime horaOut;
    private LocalTime subtotal;

    @NotNull
    @Valid
    @ManyToOne
    private Proyecto proyecto;
    @NotNull
    @Valid
    @ManyToOne
    private TipoTarea tipoTarea;
    @NotNull
    @Valid
    @ManyToOne
    private Colaborador colaborador;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        Duration duration = Duration.between(horaIn, horaOut);
        this.subtotal = LocalTime.ofNanoOfDay(duration.toNanos());
    }


    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    public LocalTime getHoraIn() {
        return horaIn;
    }

    public void setHoraIn(LocalTime horaIn) {
        this.horaIn = horaIn;
    }

    public LocalTime getHoraOut() {
        return horaOut;
    }

    public void setHoraOut(LocalTime horaOut) {
        this.horaOut = horaOut;
    }

    @JsonProperty
    public LocalTime getSubtotal() {
        return subtotal;
    }

    @JsonIgnore
    public void setSubtotal(LocalTime subtotal) {
        this.subtotal = subtotal;
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

    @Override
    public String toString() {
        return "Hora{" +
                "id=" + id +
                ", dia=" + dia +
                ", horaIn=" + horaIn +
                ", horaOut=" + horaOut +
                ", subtotal=" + subtotal +
                ", proyecto=" + proyecto.getCodigo() +
                ", tipoTarea=" + tipoTarea.getCodigo() +
                ", colaborador=" + colaborador.getNombre() +
                '}';
    }
}
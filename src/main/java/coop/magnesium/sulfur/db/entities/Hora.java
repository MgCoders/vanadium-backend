package coop.magnesium.sulfur.db.entities;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by rsperoni on 17/11/17.
 */
@Entity
@JsonAutoDetect
@ApiModel
public class Hora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @ApiModelProperty(dataType = "date", example = "23/01/2017")
    private LocalDate dia;
    @NotNull
    @ApiModelProperty(dataType = "dateTime", example = "08:15")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime horaIn;
    @NotNull
    @ApiModelProperty(dataType = "dateTime", example = "17:34")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime horaOut;
    private LocalTime subtotal;

    @NotNull
    @ManyToOne
    private Proyecto proyecto;
    @NotNull
    @ManyToOne
    private TipoTarea tipoTarea;
    @NotNull
    @ManyToOne
    private Colaborador colaborador;

    public Hora() {
    }

    public Hora(LocalDate dia, LocalTime horaIn, LocalTime horaOut, Proyecto proyecto, TipoTarea tipoTarea, Colaborador colaborador) {
        this.dia = dia;
        this.horaIn = horaIn;
        this.horaOut = horaOut;
        this.proyecto = proyecto;
        this.tipoTarea = tipoTarea;
        this.colaborador = colaborador;
    }

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @ApiModelProperty(dataType = "dateTime", example = "08:00")
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

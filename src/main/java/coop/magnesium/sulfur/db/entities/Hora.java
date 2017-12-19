package coop.magnesium.sulfur.db.entities;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

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
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dia;
    @NotNull
    @ApiModelProperty(dataType = "dateTime", example = "08:15")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime horaIn;
    @NotNull
    @ApiModelProperty(dataType = "dateTime", example = "17:34")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime horaOut;
    private LocalTime subtotal;
    @NotNull
    @ManyToOne
    private Colaborador colaborador;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "horadetalle",
            joinColumns = @JoinColumn(name = "hora_id")
    )
    private Set<HoraDetalle> horaDetalleList = new HashSet<>();
    private boolean completa = false;
    private LocalTime subtotalDetalles;

    public Hora() {
    }

    public Hora(LocalDate dia, LocalTime horaIn, LocalTime horaOut, Colaborador colaborador) {
        this.dia = dia;
        this.horaIn = horaIn;
        this.horaOut = horaOut;
        this.colaborador = colaborador;
        this.completa = false;
    }

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        //Calculo el subtotal
        Duration duration = Duration.between(horaIn, horaOut);
        this.subtotal = LocalTime.ofNanoOfDay(duration.toNanos());
        this.subtotalDetalles = LocalTime.MIN;
        horaDetalleList.stream().map(HoraDetalle::getDuracion).reduce((h1, h2) -> {
            if (h1 != null && h2 != null)
                return h1.plusHours(h2.getHour()).plusMinutes(h2.getMinute());
            else return h1;
        }).ifPresent(localTime -> this.subtotalDetalles = localTime);
        //Veo si está completa
        this.completa = (this.subtotalDetalles != null) && (!subtotal.isBefore(subtotalDetalles) && !subtotal.isAfter(subtotalDetalles));
    }

    @JsonProperty
    public boolean isCompleta() {
        return completa;
    }

    @JsonIgnore
    public void setCompleta(boolean completa) {
        this.completa = completa;
    }

    public Set<HoraDetalle> getHoraDetalleList() {
        return horaDetalleList;
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
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    public LocalTime getSubtotal() {
        return subtotal;
    }

    @JsonIgnore
    public void setSubtotal(LocalTime subtotal) {
        this.subtotal = subtotal;
    }

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @ApiModelProperty(dataType = "dateTime", example = "08:00")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    public LocalTime getSubtotalDetalles() {
        return subtotalDetalles;
    }

    @JsonIgnore
    public void setSubtotalDetalles(LocalTime subtotalDetalles) {
        this.subtotalDetalles = subtotalDetalles;
    }

    @Override
    public String toString() {
        return "Hora{" +
                "id=" + id +
                ", dia=" + dia +
                ", horaIn=" + horaIn +
                ", horaOut=" + horaOut +
                ", subtotal=" + subtotal +
                ", colaborador=" + colaborador +
                ", horaDetalleList=" + horaDetalleList +
                ", completa=" + completa +
                ", subtotalDetalles=" + subtotalDetalles +
                '}';
    }
}

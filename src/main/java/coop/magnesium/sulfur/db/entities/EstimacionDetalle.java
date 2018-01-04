package coop.magnesium.sulfur.db.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Duration;

/**
 * Created by rsperoni on 18/12/17.
 */
@Embeddable
@JsonAutoDetect
@ApiModel
public class EstimacionDetalle {

    @NotNull
    @ManyToOne
    private TipoTarea tipoTarea;
    @NotNull
    @ManyToOne
    private Cargo cargo;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'PT'HH'H'MM'M'")
    @ApiModelProperty(example = "PT23H59M", dataType = "dateTime")
    @JsonDeserialize(using = DurationDeserializer.class)
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duracion;
    @NotNull
    private BigDecimal precioTotal;

    public EstimacionDetalle() {
    }

    public EstimacionDetalle(TipoTarea tipoTarea, Cargo cargo, Duration duracion, BigDecimal precioTotal) {
        this.tipoTarea = tipoTarea;
        this.cargo = cargo;
        this.duracion = duracion;
        this.precioTotal = precioTotal;
    }

    public TipoTarea getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(TipoTarea tipoTarea) {
        this.tipoTarea = tipoTarea;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Duration getDuracion() {
        return duracion;
    }

    public void setDuracion(Duration duracion) {
        this.duracion = duracion;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    @Override
    public String toString() {
        return "EstimacionDetalle{" +
                "tipoTarea=" + tipoTarea +
                ", cargo=" + cargo +
                ", duration=" + duracion +
                ", precioTotal=" + precioTotal +
                '}';
    }
}

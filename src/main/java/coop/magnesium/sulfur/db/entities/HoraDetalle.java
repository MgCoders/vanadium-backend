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
import java.time.Duration;

/**
 * Created by rsperoni on 17/12/17.
 */
@Embeddable
@JsonAutoDetect
@ApiModel
public class HoraDetalle {

    @NotNull
    @ManyToOne
    private Proyecto proyecto;
    @NotNull
    @ManyToOne
    private TipoTarea tipoTarea;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'PT'HH'H'MM'M'")
    @ApiModelProperty(example = "PT23H59M", dataType = "dateTime")
    @JsonDeserialize(using = DurationDeserializer.class)
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duracion;

    public HoraDetalle() {
    }

    public HoraDetalle(Proyecto proyecto, TipoTarea tipoTarea, Duration duracion) {
        this.proyecto = proyecto;
        this.tipoTarea = tipoTarea;
        this.duracion = duracion;
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

    public Duration getDuracion() {
        return duracion;
    }

    public void setDuracion(Duration duracion) {
        this.duracion = duracion;
    }

    @Override
    public String toString() {
        return "HoraDetalle{" +
                "proyecto=" + proyecto +
                ", tipoTarea=" + tipoTarea +
                ", duracion=" + duracion +
                '}';
    }
}

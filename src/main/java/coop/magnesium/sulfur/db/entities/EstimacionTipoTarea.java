package coop.magnesium.sulfur.db.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;

/**
 * Created by rsperoni on 18/12/17.
 */
@Entity
@JsonAutoDetect
@ApiModel
public class EstimacionTipoTarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    private TipoTarea tipoTarea;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'PT'HH'H'MM'M'")
    @ApiModelProperty(example = "PT23H59M", dataType = "dateTime")
    @JsonDeserialize(using = DurationDeserializer.class)
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duracion;


    public EstimacionTipoTarea() {
    }

    public EstimacionTipoTarea(TipoTarea tipoTarea, Duration duracion) {
        this.tipoTarea = tipoTarea;
        this.duracion = duracion;
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "EstimacionTipoTarea{" +
                "id=" + id +
                ", tipoTarea=" + tipoTarea.getCodigo() +
                ", duracion=" + duracion +
                '}';
    }
}

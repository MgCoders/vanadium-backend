package coop.magnesium.sulfur.db.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import coop.magnesium.sulfur.api.dto.EstimacionProyecto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsperoni on 12/12/17.
 */
@Entity
@JsonAutoDetect
@ApiModel
@SqlResultSetMapping(name = "EstimacionProyecto", classes = {
        @ConstructorResult(targetClass = EstimacionProyecto.class,
                columns = {@ColumnResult(name = "proyecto_id", type = Long.class),
                        @ColumnResult(name = "tipotarea_id", type = Long.class),
                        @ColumnResult(name = "cargo_id", type = Long.class),
                        @ColumnResult(name = "precioTotal", type = BigDecimal.class),
                        @ColumnResult(name = "duracion", type = Long.class)})
})
public class Estimacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "El proyecto no puede ser vacío")
    @ManyToOne
    private Proyecto proyecto;
    private String descripcion;

    @NotNull(message = "La fecha no puede ser vacía")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @ApiModelProperty(dataType = "date", example = "23-01-2017")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate fecha;

    @Valid
    //@NotNull
    //@Size(min = 1, message = "La lista de Estimaciones por cargo no puede ser vacia")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "estimacion_id")
    private List<EstimacionCargo> estimacionCargos = new ArrayList<>();


    public Estimacion() {
    }

    public Estimacion(Proyecto proyecto, String descripcion, LocalDate fecha) {
        this.proyecto = proyecto;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EstimacionCargo> getEstimacionCargos() {
        return estimacionCargos;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }


    @Override
    public String toString() {
        return "Estimacion{" +
                "id=" + id +
                ", proyecto=" + proyecto.getCodigo() +
                ", descripcion='" + descripcion + '\'' +
                ", fecha=" + fecha +
                ", estimacionCargos=" + estimacionCargos +
                '}';
    }
}

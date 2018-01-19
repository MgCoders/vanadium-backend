package coop.magnesium.sulfur.db.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
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
public class Estimacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    private Proyecto proyecto;
    @NotNull
    private BigDecimal precioTotal;
    private String descripcion;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @ApiModelProperty(dataType = "date", example = "23-01-2017")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate fecha;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "estimacion_id")
    private List<EstimacionDetalle> estimacionDetalleList = new ArrayList<>();


    public Estimacion() {
    }

    public Estimacion(Proyecto proyecto, String descripcion, LocalDate fecha, BigDecimal precioTotal) {
        this.proyecto = proyecto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.precioTotal = precioTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EstimacionDetalle> getEstimacionDetalleList() {
        return estimacionDetalleList;
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

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    @Override
    public String toString() {
        return "Estimacion{" +
                "id=" + id +
                ", proyecto=" + proyecto.getCodigo() +
                ", precioTotal=" + precioTotal +
                ", descripcion='" + descripcion + '\'' +
                ", fecha=" + fecha +
                ", estimacionDetalleList=" + estimacionDetalleList +
                '}';
    }
}

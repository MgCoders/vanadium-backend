package coop.magnesium.sulfur.db.entities;


import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by rsperoni on 17/11/17.
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Hora {

    @Id
    @GeneratedValue
    private Long id;
    private LocalDate dia;
    private LocalTime in;
    private LocalTime out;
    private LocalTime subtotal;

    @ManyToOne
    private Proyecto proyecto;
    @ManyToOne
    private TipoTarea tipoTarea;
    @ManyToOne
    private Colaborador colaborador;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        Duration duration = Duration.between(in, out);
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

    public LocalTime getIn() {
        return in;
    }

    public void setIn(LocalTime in) {
        this.in = in;
    }

    public LocalTime getOut() {
        return out;
    }

    public void setOut(LocalTime out) {
        this.out = out;
    }

    public LocalTime getSubtotal() {
        return subtotal;
    }

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
                ", in=" + in +
                ", out=" + out +
                ", subtotal=" + subtotal +
                ", proyecto=" + proyecto.getCodigo() +
                ", tipoTarea=" + tipoTarea.getCodigo() +
                ", colaborador=" + colaborador.getNombre() +
                '}';
    }
}

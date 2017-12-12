package coop.magnesium.sulfur.db.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.persistence.*;

/**
 * Created by rsperoni on 12/12/17.
 */
@Entity
@JsonAutoDetect
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"proyecto_id", "tipoTarea_id", "cargo_id"}))
public class Estimacion {

    @Id
    private Long id;
    @ManyToOne
    private Proyecto proyecto;
    @ManyToOne
    private TipoTarea tipoTarea;
    @ManyToOne
    private Cargo cargo;

    public Estimacion(Proyecto proyecto, TipoTarea tipoTarea, Cargo cargo) {
        this.proyecto = proyecto;
        this.tipoTarea = tipoTarea;
        this.cargo = cargo;
    }

    public Estimacion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    @Override
    public String toString() {
        return "Estimacion{" +
                "id=" + id +
                ", proyecto=" + proyecto +
                ", tipoTarea=" + tipoTarea +
                ", cargo=" + cargo +
                '}';
    }
}

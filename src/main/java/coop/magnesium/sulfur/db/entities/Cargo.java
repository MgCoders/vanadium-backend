package coop.magnesium.sulfur.db.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by rsperoni on 16/11/17.
 */
@Entity
@JsonAutoDetect
@ApiModel
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "El campo nombre no puede estar vacío")
    private String nombre;
    @NotNull(message = "El campo código no puede estar vacío")
    @Column(unique = true)
    private String codigo;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "precioHoraHistoria",
            joinColumns = @JoinColumn(name = "cargo_id")
    )
    private Set<PrecioHora> precioHoraHistoria = new HashSet<>();

    public Cargo() {
    }

    public Cargo(String codigo, String nombre, BigDecimal precioHora) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioHoraHistoria.add(new PrecioHora(precioHora, LocalDate.now()));
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<PrecioHora> getPrecioHoraHistoria() {
        return precioHoraHistoria;
    }

    /**
     * Ultimo precioHora.
     *
     * @return
     */
    public Optional<PrecioHora> getPrecioHora(LocalDate diaReferencia) {
        return precioHoraHistoria.stream().sorted(Comparator.comparing(PrecioHora::getVigenciaDesde).reversed()).filter(precioHora -> !precioHora.getVigenciaDesde().isAfter(diaReferencia)).findFirst();
    }

    @Override
    public String toString() {
        return "Cargo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", codigo='" + codigo + '\'' +
                ", precioHoraHistoria=" + precioHoraHistoria +
                '}';
    }
}

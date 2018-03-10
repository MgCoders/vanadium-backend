package coop.magnesium.sulfur.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by rsperoni on 17/12/17.
 */
public class HoraCompletaReporte1 {

    public Long proyecto_id;
    public Long tipoTarea_id;
    public Long colaborador_id;
    public Long cargo_id;
    public Long duracion;
    public LocalDate dia;
    public BigDecimal costo;


    public HoraCompletaReporte1() {
    }

    public HoraCompletaReporte1(Long proyecto_id, Long tipoTarea_id, Long colaborador_id, Long cargo_id, Long duracion, LocalDate dia) {
        this.proyecto_id = proyecto_id;
        this.tipoTarea_id = tipoTarea_id;
        this.colaborador_id = colaborador_id;
        this.cargo_id = cargo_id;
        this.duracion = duracion;
        this.dia = dia;
    }

    @Override
    public String toString() {
        return "HoraCompletaReporte1{" +
                "proyecto_id=" + proyecto_id +
                ", tipoTarea_id=" + tipoTarea_id +
                ", colaborador_id=" + colaborador_id +
                ", cargo_id=" + cargo_id +
                ", duracion=" + duracion +
                ", dia=" + dia +
                ", costo=" + costo +
                '}';
    }
}

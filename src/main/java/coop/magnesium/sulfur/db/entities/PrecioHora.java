package coop.magnesium.sulfur.db.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by rsperoni on 18/12/17.
 */
@Embeddable
@JsonAutoDetect
@ApiModel
public class PrecioHora {

    @NotNull
    private BigDecimal precioHora;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @ApiModelProperty(dataType = "date", example = "23/01/2017")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate vigenciaDesde;


    public PrecioHora() {
    }

    public PrecioHora(BigDecimal precioHora, LocalDate vigenciaDesde) {
        this.precioHora = precioHora;
        this.vigenciaDesde = vigenciaDesde;
    }

    public BigDecimal getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(BigDecimal precioHora) {
        this.precioHora = precioHora;
    }

    public LocalDate getVigenciaDesde() {
        return vigenciaDesde;
    }


    @Override
    public String toString() {
        return "PrecioHora{" +
                "precioHora=" + precioHora +
                ", vigenciaDesde=" + vigenciaDesde +
                '}';
    }
}

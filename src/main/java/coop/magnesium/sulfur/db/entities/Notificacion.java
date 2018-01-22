package coop.magnesium.sulfur.db.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by rsperoni on 19/01/18.
 */
@Entity
@JsonAutoDetect
@ApiModel
@Inheritance
@DiscriminatorColumn(name = "tipo_notificacion")
public class Notificacion {

    @OneToOne
    Hora hora;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;
    private String texto;
    private boolean email;
    private Colaborador notificado;
    private Colaborador protagonista;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm")
    @ApiModelProperty(dataType = "date", example = "23-01-2017T16:45")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime fechaHora;

    public Notificacion(TipoNotificacion tipo, Colaborador notificado, Colaborador protagonista, String texto) {
        this.tipo = tipo;
        this.email = notificado != null;
        this.notificado = notificado;
        this.protagonista = protagonista;
        this.fechaHora = LocalDateTime.now();
    }

    public Notificacion(TipoNotificacion tipo, Colaborador notificado, Colaborador protagonista, String texto, Hora hora) {
        this.hora = hora;
        if (getTexto() == null) {
            switch (tipo) {
                case LOGIN:
                    setTexto(getProtagonista().getNombre() + " inició sesión en el sistema.");
                    break;
                default:
                    setTexto("");

            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public Colaborador getNotificado() {
        return notificado;
    }

    public void setNotificado(Colaborador notificado) {
        this.notificado = notificado;
    }

    public Colaborador getProtagonista() {
        return protagonista;
    }

    public void setProtagonista(Colaborador protagonista) {
        this.protagonista = protagonista;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", texto='" + texto + '\'' +
                ", email=" + email +
                ", notificado=" + notificado.getEmail() +
                ", protagonista=" + protagonista.getEmail() +
                ", fechaHora=" + fechaHora +
                '}';
    }
}



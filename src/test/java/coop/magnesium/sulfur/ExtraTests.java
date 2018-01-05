package coop.magnesium.sulfur;

import coop.magnesium.sulfur.db.entities.Hora;
import coop.magnesium.sulfur.db.entities.HoraDetalle;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.wildfly.common.Assert.assertTrue;

/**
 * Created by rsperoni on 17/11/17.
 */
public class ExtraTests {

    @Test
    public void HoraSubtotalTest() {
        Hora hora = new Hora();
        hora.setHoraIn(LocalTime.now());
        hora.setHoraOut(hora.getHoraIn().plusHours(3L).plusMinutes(5L));
        hora.calcularSubtotal();
        assertEquals(Duration.ofHours(3).plus(Duration.ofMinutes(5)), hora.getSubtotal());
        assertEquals(Duration.ZERO, hora.getSubtotalDetalles());
        assertFalse(hora.isCompleta());
    }

    @Test
    public void HoraSubtotalDetalleTest() {
        Hora hora = new Hora();
        hora.setHoraIn(LocalTime.MIN);
        hora.setHoraOut(hora.getHoraIn().plusHours(3L).plusMinutes(5L));
        hora.getHoraDetalleList().add(new HoraDetalle(null, null, Duration.ofHours(22)));
        hora.calcularSubtotal();
        assertEquals(Duration.ofHours(3).plus(Duration.ofMinutes(5)), hora.getSubtotal());
        assertEquals(Duration.ofHours(22), hora.getSubtotalDetalles());
        assertFalse(hora.isCompleta());
    }

    @Test
    public void HoraSubtotalDetalle2Test() {
        Hora hora = new Hora();
        hora.setHoraIn(LocalTime.MIN);
        hora.setHoraOut(hora.getHoraIn().plusHours(23L).plusMinutes(5L));
        hora.getHoraDetalleList().add(new HoraDetalle(null, null, Duration.ofHours(22)));
        hora.getHoraDetalleList().add(new HoraDetalle(null, null, Duration.ofHours(1).plusMinutes(5)));
        hora.calcularSubtotal();
        assertEquals(Duration.ofHours(23).plus(Duration.ofMinutes(5)), hora.getSubtotal());
        assertEquals(Duration.ofHours(23).plus(Duration.ofMinutes(5)), hora.getSubtotalDetalles());
        assertTrue(hora.isCompleta());
    }
}

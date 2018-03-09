package coop.magnesium.sulfur;

import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Hora;
import coop.magnesium.sulfur.db.entities.HoraDetalle;
import coop.magnesium.sulfur.utils.TimeUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
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
        hora.getHoraDetalleList().add(new HoraDetalle(null, null, Duration.ofHours(22), null));
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
        hora.getHoraDetalleList().add(new HoraDetalle(null, null, Duration.ofHours(22), null));
        hora.getHoraDetalleList().add(new HoraDetalle(null, null, Duration.ofHours(1).plusMinutes(5), null));
        hora.calcularSubtotal();
        assertEquals(Duration.ofHours(23).plus(Duration.ofMinutes(5)), hora.getSubtotal());
        assertEquals(Duration.ofHours(23).plus(Duration.ofMinutes(5)), hora.getSubtotalDetalles());
        assertTrue(hora.isCompleta());
    }

    @Test
    public void durationToBigDecimalTest() {
        assertEquals(new BigDecimal(23.5).setScale(2, RoundingMode.HALF_DOWN), TimeUtils.durationToBigDecimal(Duration.ofHours(23).plusMinutes(30)));
        assertEquals(new BigDecimal(23).setScale(2, RoundingMode.HALF_DOWN), TimeUtils.durationToBigDecimal(Duration.ofHours(23)));
        assertEquals(new BigDecimal(1.25).setScale(2, RoundingMode.HALF_DOWN), TimeUtils.durationToBigDecimal(Duration.ofHours(1).plusMinutes(15)));
        assertEquals(new BigDecimal(3.75).setScale(2, RoundingMode.HALF_DOWN), TimeUtils.durationToBigDecimal(Duration.ofHours(3).plusMinutes(45)));
        assertEquals(new BigDecimal(10.25).setScale(2, RoundingMode.HALF_DOWN), TimeUtils.durationToBigDecimal(Duration.ofHours(10).plusMinutes(15)));
        assertEquals(new BigDecimal(0.25).setScale(2, RoundingMode.HALF_DOWN), TimeUtils.durationToBigDecimal(Duration.ofHours(0).plusMinutes(15)));
        assertEquals(new BigDecimal(0.17).setScale(2, RoundingMode.HALF_DOWN), TimeUtils.durationToBigDecimal(Duration.ofHours(0).plusMinutes(10)));



    }

    @Test
    public void cargoPrecioHoraTest() {
        BigDecimal precio = new BigDecimal(32.2).setScale(2, RoundingMode.HALF_DOWN);
        final Cargo cargo1 = new Cargo("C1", "C1", precio);
        assertTrue(cargo1.getPrecioHora(LocalDate.now()).isPresent());
        assertTrue(cargo1.getPrecioHora(LocalDate.now()).get().equals(precio));

    }
}

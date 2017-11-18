package coop.magnesium.sulfur;

import coop.magnesium.sulfur.db.entities.Hora;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 17/11/17.
 */
public class ExtraTests {

    @Test
    public void HoraSubtotalTest() {
        Hora hora = new Hora();
        hora.setIn(LocalTime.now());
        hora.setOut(hora.getIn().plusHours(3L).plusMinutes(5L));
        hora.calcularSubtotal();
        assertEquals(LocalTime.of(3, 05), hora.getSubtotal());
    }
}

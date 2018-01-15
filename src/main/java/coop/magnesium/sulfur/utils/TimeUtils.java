package coop.magnesium.sulfur.utils;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Created by rsperoni on 08/01/18.
 */
public class TimeUtils {

    public static BigDecimal durationToBigDecimal(Duration duration) {
        BigDecimal hour = new BigDecimal(Math.floorDiv(duration.getSeconds(), 3600));
        Long segundosRestantes = duration.getSeconds() % 3600;
        BigDecimal minutosRestantes = new BigDecimal(Math.floorDiv(segundosRestantes, 60));
        BigDecimal horaDecimal = minutosRestantes.divide(new BigDecimal(60));
        return hour.add(horaDecimal).setScale(2, BigDecimal.ROUND_CEILING);
    }
}

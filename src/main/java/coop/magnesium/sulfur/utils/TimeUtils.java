package coop.magnesium.sulfur.utils;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Created by rsperoni on 08/01/18.
 */
public class TimeUtils {

    public static BigDecimal durationToBigDecimal(Duration duration) {
        BigDecimal bdhours = BigDecimal.valueOf(duration.toHours());
        BigDecimal bdminutes = new BigDecimal((duration.toMinutes() % 60) / 60L);
        return bdhours.add(bdminutes).setScale(2, BigDecimal.ROUND_CEILING);
    }
}

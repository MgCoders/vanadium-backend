package coop.magnesium.sulfur.utils;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.logging.Logger;

/**
 * Created by rsperoni on 13/09/17.
 */
public class LoggerProvider {

    @Produces
    public Logger createLogger(InjectionPoint ip) {
        return Logger.getLogger(ip.getMember().getDeclaringClass().getName());
    }
}

package coop.magnesium.vanadium.utils;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rsperoni on 05/10/17.
 */
public class LoggingFilter implements ClientRequestFilter {

    private Logger logger;

    public LoggingFilter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        logger.info(requestContext.getUri().toString());
        logger.log(Level.INFO, requestContext.getEntity().toString());
    }
}
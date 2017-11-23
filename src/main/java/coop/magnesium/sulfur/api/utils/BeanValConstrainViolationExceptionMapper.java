package coop.magnesium.sulfur.api.utils;

import coop.magnesium.sulfur.utils.Logged;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Created by rsperoni on 18/11/17.
 */
@Provider
public class BeanValConstrainViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Inject
    Logger logger;

    @Override
    @Logged
    public Response toResponse(ConstraintViolationException e) {
        ConstraintViolation cv = (ConstraintViolation) e.getConstraintViolations().toArray()[0];
        //TODO: ver como pasar a espanol y mostrar bien el nombre del campo.
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(cv.getPropertyPath() + " " + cv.getMessage())
                .build();
    }

}
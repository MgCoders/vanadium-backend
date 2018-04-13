package coop.magnesium.vanadium.api;


import coop.magnesium.vanadium.api.utils.JWTTokenNeeded;
import coop.magnesium.vanadium.api.utils.RoleNeeded;
import coop.magnesium.vanadium.db.dao.ConfiguracionDao;
import coop.magnesium.vanadium.db.entities.Role;
import coop.magnesium.vanadium.utils.Logged;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by rsperoni on 05/05/17.
 */
@Path("/configuracion")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
@Api(description = "Configuracion service", tags = "configuracion")
public class ConfiguracionService {

    @Inject
    private Logger logger;
    @Inject
    private ConfiguracionDao configuracionDao;

    @POST
    @Path("mail")
    @Logged
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Set mail enabled", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error interno")})
    public Response setMailEnabled(boolean enabled) {
        try {
            configuracionDao.setMailOn(enabled);
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @GET
    @Path("mail")
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Get email enabled", response = Response.class)
    public Response getMailEnabled() {
        return Response.ok(configuracionDao.isEmailOn()).build();
    }

    @POST
    @Path("periodicidad")
    @Logged
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Set mail periodicidad", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error interno")})
    public Response setMailPeriodicidad(long horas) {
        try {
            configuracionDao.setPeriodicidadNotificaciones(horas);
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @GET
    @Path("periodicidad")
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Get email periodicidad", response = Response.class)
    public Response getMailPeriodicidad() {
        return Response.ok(configuracionDao.getPeriodicidadNotificaciones()).build();
    }


    @GET
    @Path("destinatarios")
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Get email destinatarios", response = String.class, responseContainer = "List")
    public Response getDestinatarios() {
        return Response.ok(configuracionDao.getDestinatariosNotificacionesAdmins()).build();
    }

    @POST
    @Path("destinatarios")
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Add email destinatario", response = String.class, responseContainer = "List")
    public Response addDestinatario(String email) {
        configuracionDao.addDestinatarioNotificacionesAdmins(email);
        return Response.ok(configuracionDao.getDestinatariosNotificacionesAdmins()).build();
    }

    @DELETE
    @Path("destinatarios")
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Delete email destinatario", response = String.class, responseContainer = "List")
    public Response deleteDestinatario(String email) {
        configuracionDao.deleteDestinatarioNotificacionesAdmins(email);
        return Response.ok(configuracionDao.getDestinatariosNotificacionesAdmins()).build();
    }


}

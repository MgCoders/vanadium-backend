package coop.magnesium.sulfur.api;


import coop.magnesium.sulfur.api.utils.JWTTokenNeeded;
import coop.magnesium.sulfur.api.utils.RoleNeeded;
import coop.magnesium.sulfur.db.dao.ColaboradorDao;
import coop.magnesium.sulfur.db.dao.NotificacionDao;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.db.entities.Notificacion;
import coop.magnesium.sulfur.db.entities.Role;
import coop.magnesium.sulfur.utils.ex.MagnesiumNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by rsperoni on 05/05/17.
 */
@Path("/notificaciones")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
@Api(description = "Notificaciones service", tags = "notificaciones")
public class NotificacionService {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    @Inject
    private Logger logger;
    @EJB
    private NotificacionDao notificacionDao;
    @EJB
    private ColaboradorDao colaboradorDao;


    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Get notificaciones", response = Notificacion.class, responseContainer = "List")
    public Response findAll() {
        List<Notificacion> notificacionList = notificacionDao.findAll();
        return Response.ok(notificacionList).build();
    }

    @GET
    @Path("colaborador/{colaborador_id}/{fecha_ini}/{fecha_fin}")
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN, Role.USER})
    @ApiOperation(value = "Get notificaciones por colaborador", response = Notificacion.class, responseContainer = "List")
    public Response findByColaborador(@PathParam("colaborador_id") Long colaborador_id,
                                      @PathParam("fecha_ini") String fechaIniString,
                                      @PathParam("fecha_fin") String fechaFinString) {
        try {
            Colaborador colaborador = colaboradorDao.findById(colaborador_id);
            if (colaborador == null)
                throw new MagnesiumNotFoundException("colaborador no encontrado");

            LocalDate fechaIni = LocalDate.parse(fechaIniString, formatter);
            LocalDate fechaFin = LocalDate.parse(fechaFinString, formatter);

            List<Notificacion> notificacionList = notificacionDao.findAllByColaborador(colaborador, fechaIni, fechaFin);
            return Response.ok(notificacionList).build();
        } catch (MagnesiumNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


}

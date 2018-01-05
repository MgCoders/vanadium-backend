package coop.magnesium.sulfur.api;


import coop.magnesium.sulfur.api.utils.JWTTokenNeeded;
import coop.magnesium.sulfur.api.utils.RoleNeeded;
import coop.magnesium.sulfur.db.dao.ColaboradorDao;
import coop.magnesium.sulfur.db.dao.HoraDao;
import coop.magnesium.sulfur.db.dao.ProyectoDao;
import coop.magnesium.sulfur.db.dao.TipoTareaDao;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.db.entities.Hora;
import coop.magnesium.sulfur.db.entities.Role;
import coop.magnesium.sulfur.db.entities.SulfurUser;
import coop.magnesium.sulfur.utils.Logged;
import coop.magnesium.sulfur.utils.ex.MagnesiumException;
import coop.magnesium.sulfur.utils.ex.MagnesiumNotFoundException;
import coop.magnesium.sulfur.utils.ex.MagnesiumSecurityException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by rsperoni on 05/05/17.
 */
@Path("/horas")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
@Api(description = "Horas service", tags = "horas")
public class HoraService {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @Inject
    private Logger logger;
    @EJB
    private HoraDao horaDao;
    @EJB
    private ProyectoDao proyectoDao;
    @EJB
    private ColaboradorDao colaboradorDao;
    @EJB
    private TipoTareaDao tipoTareaDao;

    @POST
    @Logged
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Create hora", response = Hora.class)
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "Código o Id ya existe"),
            @ApiResponse(code = 400, message = "Objeto inválido"),
            @ApiResponse(code = 500, message = "Error interno"),
            @ApiResponse(code = 401, message = "No Autorizado")})
    public Response create(@Valid Hora hora, @Context SecurityContext securityContext) {
        try {
            Colaborador colaborador = colaboradorDao.findById(hora.getColaborador().getId());
            if (colaborador == null)
                throw new MagnesiumNotFoundException("Colaborador no encontrado");

            hora.setColaborador(colaborador);

            if (!((SulfurUser) securityContext.getUserPrincipal()).getColaboradorId().equals(colaborador.getId()))
                throw new MagnesiumSecurityException("Colaborador no coincide");


            if (horaDao.existsByColaboradorIncompleta(hora.getColaborador()))
                throw new MagnesiumException("El colaborador tiene horas incompletas");

            //si es mismo colaborador, misma fecha, entonces asumo edicion de hora
            Hora horaExists = horaDao.findByColaboradorFecha(hora.getColaborador(), hora.getDia());
            if (horaExists != null) {
                hora.setId(horaExists.getId());
            }

            hora.cacularSubtotalDetalle();
            Hora horaCreada = horaDao.save(hora);
            if (horaExists == null) {
                return Response.status(Response.Status.CREATED).entity(horaCreada).build();
            } else {
                return Response.status(Response.Status.OK).entity(horaCreada).build();
            }
        } catch (MagnesiumNotFoundException e) {
            logger.warning(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (MagnesiumSecurityException e) {
            logger.warning(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Get horas", response = Hora.class, responseContainer = "List")
    public Response findAll() {
        List<Hora> horaList = horaDao.findAll();
        return Response.ok(horaList).build();
    }

    @GET
    @Path("user/{id}/{fecha_ini}/{fecha_fin}")
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Get horas por usuario y fechas", response = Hora.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Objeto inválido"),
            @ApiResponse(code = 401, message = "No Autorizado")})
    public Response findAllByColaborador(@PathParam("id") Long id,
                                         @Context SecurityContext securityContext,
                                         @PathParam("fecha_ini") String fechaIniString,
                                         @PathParam("fecha_fin") String fechaFinString) {
        try {
            Colaborador colaborador = colaboradorDao.findById(id);
            if (colaborador == null) throw new MagnesiumNotFoundException("Colaborador no encontrado");

            if (!((SulfurUser) securityContext.getUserPrincipal()).getColaboradorId().equals(colaborador.getId())) {
                throw new MagnesiumSecurityException("Colaborador no coincide");
            }

            LocalDate fechaIni = LocalDate.parse(fechaIniString, formatter);
            LocalDate fechaFin = LocalDate.parse(fechaFinString, formatter);

            List<Hora> horaList = horaDao.findAllByColaborador(colaborador, fechaIni, fechaFin);
            return Response.ok(horaList).build();
        } catch (MagnesiumSecurityException e) {
            logger.warning(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (MagnesiumNotFoundException e) {
            logger.warning(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("{id}")
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Get hora", response = Hora.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Id no encontrado")})
    public Response find(@PathParam("id") Long id) {
        Hora hora = horaDao.findById(id);
        if (hora == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(hora).build();
    }

    @Deprecated
    @PUT
    @Path("{id}")
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Edit hora", response = Hora.class)
    @ApiResponses(value = {
            @ApiResponse(code = 304, message = "Error: objeto no modificado")})
    public Response edit(@PathParam("id") Long id, @Valid Hora hora, @Context SecurityContext securityContext) {
        return create(hora, securityContext);
    }


}

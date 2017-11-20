package coop.magnesium.sulfur.api;


import coop.magnesium.sulfur.api.utils.JWTTokenNeeded;
import coop.magnesium.sulfur.api.utils.RoleNeeded;
import coop.magnesium.sulfur.db.dao.ColaboradorDao;
import coop.magnesium.sulfur.db.dao.HoraDao;
import coop.magnesium.sulfur.db.dao.ProyectoDao;
import coop.magnesium.sulfur.db.dao.TipoTareaDao;
import coop.magnesium.sulfur.db.entities.*;
import coop.magnesium.sulfur.utils.Logged;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdAlredyExistsException;
import coop.magnesium.sulfur.utils.ex.MagnesiumNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
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
            @ApiResponse(code = 500, message = "Error interno")})
    public Response create(@Valid Hora hora) {
        try {
            Hora horaExists = hora.getId() != null ? horaDao.findById(hora.getId()) : null;
            if (horaExists != null) throw new MagnesiumBdAlredyExistsException("Hora ya existe");

            Proyecto proyecto = proyectoDao.findById(hora.getProyecto().getId());
            if (proyecto == null) throw new MagnesiumNotFoundException("Proyecto no encontrado");
            hora.setProyecto(proyecto);

            TipoTarea tipoTarea = tipoTareaDao.findById(hora.getTipoTarea().getId());
            if (tipoTarea == null) throw new MagnesiumNotFoundException("Tipo tarea no encontrado");
            hora.setTipoTarea(tipoTarea);

            Colaborador colaborador = colaboradorDao.findById(hora.getColaborador().getId());
            if (colaborador == null) throw new MagnesiumNotFoundException("Colaborador no encontrado");
            hora.setColaborador(colaborador);

            hora = horaDao.save(hora);
            return Response.status(Response.Status.CREATED).entity(hora).build();
        } catch (MagnesiumBdAlredyExistsException exists) {
            logger.warning(exists.getMessage());
            return Response.status(Response.Status.CONFLICT).entity(exists.getMessage()).build();
        } catch (MagnesiumNotFoundException e) {
            logger.warning(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Get horas", response = Hora.class, responseContainer = "List")
    public Response findAll() {
        List<Hora> horaList = horaDao.findAll();
        return Response.ok(horaList).build();
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

    @PUT
    @Path("{id}")
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Edit hora", response = Hora.class)
    @ApiResponses(value = {
            @ApiResponse(code = 304, message = "Error: objeto no modificado")})
    public Response edit(@PathParam("id") Long id, @Valid Hora hora) {
        try {
            if (horaDao.findById(id) == null) throw new MagnesiumNotFoundException("Hora no encontrada");
            hora.setId(id);

            Proyecto proyecto = proyectoDao.findById(hora.getProyecto().getId());
            if (proyecto == null) throw new MagnesiumNotFoundException("Proyecto no encontrado");
            hora.setProyecto(proyecto);

            TipoTarea tipoTarea = tipoTareaDao.findById(hora.getTipoTarea().getId());
            if (tipoTarea == null) throw new MagnesiumNotFoundException("Tipo tarea no encontrado");
            hora.setTipoTarea(tipoTarea);

            Colaborador colaborador = colaboradorDao.findById(hora.getColaborador().getId());
            if (colaborador == null) throw new MagnesiumNotFoundException("Colaborador no encontrado");
            hora.setColaborador(colaborador);


            hora = horaDao.save(hora);
            return Response.ok(hora).build();
        } catch (Exception e) {
            return Response.notModified().entity(e.getMessage()).build();
        }
    }


}

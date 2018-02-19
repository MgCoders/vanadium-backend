package coop.magnesium.sulfur.api;


import coop.magnesium.sulfur.api.utils.JWTTokenNeeded;
import coop.magnesium.sulfur.api.utils.RoleNeeded;
import coop.magnesium.sulfur.db.dao.ProyectoDao;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.Role;
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
@Path("/proyectos")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
@Api(description = "Proyectos service", tags = "proyectos")
public class ProyectoService {

    @Inject
    private Logger logger;
    @EJB
    private ProyectoDao proyectoDao;

    @POST
    @Logged
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Create Proyecto", response = Proyecto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "Código o Id ya existe"),
            @ApiResponse(code = 500, message = "Error interno")})
    public Response create(@Valid Proyecto proyecto) {
        try {
            Proyecto proyectoExists = proyecto.getId() != null ? proyectoDao.findById(proyecto.getId()) : null;
            if (proyectoExists != null) throw new MagnesiumBdAlredyExistsException("Id ya existe");

            if (proyectoDao.findByField("codigo", proyecto.getCodigo()).size() > 0)
                throw new MagnesiumBdAlredyExistsException("Código ya existe");

            proyecto = proyectoDao.save(proyecto);
            return Response.status(Response.Status.CREATED).entity(proyecto).build();
        } catch (MagnesiumBdAlredyExistsException exists) {
            logger.warning(exists.getMessage());
            return Response.status(Response.Status.CONFLICT).entity(exists.getMessage()).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Get proyectos", response = Proyecto.class, responseContainer = "List")
    public Response findAll() {
        List<Proyecto> proyectoList = proyectoDao.findAllByPrioridad();
        return Response.ok(proyectoList).build();
    }

    @GET
    @Path("{id}")
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Get tipo Tarea", response = Proyecto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Id no encontrado")})
    public Response find(@PathParam("id") Long id) {
        Proyecto proyecto = proyectoDao.findById(id);
        if (proyecto == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(proyecto).build();
    }

    @PUT
    @Path("{id}")
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Edit proyecto", response = Proyecto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 304, message = "Error: objeto no modificado")})
    public Response edit(@PathParam("id") Long id, @Valid Proyecto proyecto) {
        try {
            if (proyectoDao.findById(id) == null) throw new MagnesiumNotFoundException("Proyecto no encontrado");
            proyecto.setId(id);
            proyecto = proyectoDao.save(proyecto);
            return Response.ok(proyecto).build();
        } catch (Exception e) {
            return Response.notModified().entity(e.getMessage()).build();
        }
    }


}

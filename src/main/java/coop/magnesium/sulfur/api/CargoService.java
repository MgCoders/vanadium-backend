package coop.magnesium.sulfur.api;


import coop.magnesium.sulfur.api.utils.JWTTokenNeeded;
import coop.magnesium.sulfur.api.utils.RoleNeeded;
import coop.magnesium.sulfur.db.dao.CargoDao;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.PrecioHora;
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
@Path("/cargos")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
@Api(description = "Cargos service", tags = "cargos")
public class CargoService {

    @Inject
    private Logger logger;
    @EJB
    private CargoDao cargoDao;

    @POST
    @Logged
    @ApiOperation(value = "Create Cargo", response = Cargo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "Código o Id ya existe"),
            @ApiResponse(code = 500, message = "Error interno")})
    public Response create(@Valid Cargo cargo) {
        try {
            Cargo cargoExists = cargo.getId() != null ? cargoDao.findById(cargo.getId()) : null;
            if (cargoExists != null) throw new MagnesiumBdAlredyExistsException("Id ya existe");

            if (cargoDao.findByField("codigo", cargo.getCodigo()).size() > 0)
                throw new MagnesiumBdAlredyExistsException("Código existe");

            cargo = cargoDao.save(cargo);
            return Response.status(Response.Status.CREATED).entity(cargo).build();
        } catch (MagnesiumBdAlredyExistsException exists) {
            logger.warning(exists.getMessage());
            return Response.status(Response.Status.CONFLICT).entity(exists.getMessage()).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("{id}")
    @Logged
    @ApiOperation(value = "Actualizar precioHora Cargo", response = Cargo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "Código o Id ya existe"),
            @ApiResponse(code = 304, message = "No modificado")})
    public Response actualizarPrecioHora(@PathParam("id") Long id, @Valid PrecioHora precioHora) {
        try {
            Cargo cargo = cargoDao.findById(id);
            if (cargo == null) throw new MagnesiumNotFoundException("Cargo no encontrado");

            cargo.getPrecioHoraHistoria().add(precioHora);
            return Response.ok(cargo).build();
        } catch (Exception e) {
            return Response.notModified().entity(e.getMessage()).build();
        }
    }


    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Get cargos", response = Cargo.class, responseContainer = "List")
    public Response findAll() {
        List<Cargo> cargoList = cargoDao.findAll();
        return Response.ok(cargoList).build();
    }

    @GET
    @Path("{id}")
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Get Cargo", response = Cargo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Id no encontrado")})
    public Response find(@PathParam("id") Long id) {
        Cargo cargo = cargoDao.findById(id);
        if (cargo == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(cargo).build();
    }

    @PUT
    @Path("{id}")
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Edit cargo", response = Cargo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 304, message = "Error: objeto no modificado")})
    public Response edit(@PathParam("id") Long id, @Valid Cargo cargo) {
        try {
            if (cargoDao.findById(id) == null) throw new MagnesiumNotFoundException("Cargo no encontrado");
            cargo.setId(id);
            cargo = cargoDao.save(cargo);
            return Response.ok(cargo).build();
        } catch (Exception e) {
            return Response.notModified().entity(e.getMessage()).build();
        }
    }


}

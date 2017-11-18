package coop.magnesium.sulfur.api;


import coop.magnesium.sulfur.db.dao.CargoDao;
import coop.magnesium.sulfur.db.dao.ColaboradorDao;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.utils.Logged;
import coop.magnesium.sulfur.utils.PasswordUtils;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdAlredyExistsException;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdMultipleResultsException;
import coop.magnesium.sulfur.utils.ex.MagnesiumNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
@Path("/colaboradores")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
@Api(description = "Colaboradores service", tags = "colaboradores")
public class ColaboradorService {

    @Inject
    private Logger logger;
    @EJB
    private ColaboradorDao colaboradorDao;
    @EJB
    private CargoDao cargoDao;


    @POST
    @Logged
    @ApiOperation(value = "Create colaborador", response = Colaborador.class)
    public Response create(@Valid Colaborador colaborador) {
        try {
            Colaborador colaboradorExists = colaboradorDao.findByEmail(colaborador.getEmail());
            if (colaboradorExists != null) throw new MagnesiumBdAlredyExistsException("Email ya existe");
            colaborador.setPassword(PasswordUtils.digestPassword(colaborador.getPassword()));
            Cargo cargo = cargoDao.findById(colaborador.getCargo().getId());
            if (cargo == null) throw new MagnesiumNotFoundException("Cargo no encontrado");
            colaborador.setCargo(cargo);
            colaborador = colaboradorDao.save(colaborador);
            return Response.status(Response.Status.CREATED).entity(colaborador).build();
        } catch (MagnesiumBdMultipleResultsException | MagnesiumBdAlredyExistsException exists) {
            logger.warning("Email ya existe");
            return Response.status(Response.Status.CONFLICT).entity("Email ya existe").build();
        } catch (MagnesiumNotFoundException e) {
            logger.warning("Rol no existe");
            return Response.status(Response.Status.BAD_REQUEST).entity("Rol no existe").build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @GET
    //@JWTTokenNeeded
    //@RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Get colaboradores", response = Colaborador.class, responseContainer = "List")
    public Response findAll() {
        List<Colaborador> allSulfurUsers = colaboradorDao.findAll();
        return Response.ok(allSulfurUsers).build();
    }

    @GET
    @Path("{id}")
    //@JWTTokenNeeded
    //@RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Get colaborador", response = Colaborador.class)
    public Response find(@PathParam("id") Long id) {
        Colaborador colaborador = colaboradorDao.findById(id);
        if (colaborador == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(colaborador).build();
    }

    @PUT
    @Path("{id}")
    //@JWTTokenNeeded
    //@RoleNeeded({Role.USER, Role.ADMIN})
    @ApiOperation(value = "Edit colaborador", response = Colaborador.class)
    public Response edit(@PathParam("id") Long id, Colaborador colaborador) {
        try {
            if (colaboradorDao.findById(id) == null) throw new MagnesiumNotFoundException("Colaborador no encontrado");
            colaborador.setId(id);
            Cargo cargo = cargoDao.findById(colaborador.getCargo().getId());
            if (cargo == null) throw new MagnesiumNotFoundException("Cargo no encontrado");
            colaborador.setCargo(cargo);
            colaborador = colaboradorDao.save(colaborador);
            return Response.ok(colaborador).build();
        } catch (Exception e) {
            return Response.notModified().entity(e.getMessage()).build();
        }
    }


}

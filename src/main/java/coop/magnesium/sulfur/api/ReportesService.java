package coop.magnesium.sulfur.api;


import coop.magnesium.sulfur.api.dto.EstimacionProyectoTipoTareaXCargo;
import coop.magnesium.sulfur.api.dto.HorasProyectoTipoTareaCargoXColaborador;
import coop.magnesium.sulfur.api.dto.HorasProyectoTipoTareaXCargo;
import coop.magnesium.sulfur.api.dto.HorasProyectoXCargo;
import coop.magnesium.sulfur.api.utils.JWTTokenNeeded;
import coop.magnesium.sulfur.api.utils.RoleNeeded;
import coop.magnesium.sulfur.db.dao.EstimacionDao;
import coop.magnesium.sulfur.db.dao.HoraDao;
import coop.magnesium.sulfur.db.entities.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by rsperoni on 05/05/17.
 */
@Path("/reportes")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
@Api(description = "Reportes service", tags = "reportes")
public class ReportesService {

    @Inject
    private Logger logger;
    @EJB
    private EstimacionDao estimacionDao;
    @EJB
    private HoraDao horaDao;


    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Get estimaciones", response = Estimacion.class, responseContainer = "List")
    public Response findAll() {
        List<Estimacion> estimacionList = estimacionDao.findAll();
        return Response.ok(estimacionList).build();
    }

    @POST
    @Path("horas/proyecto/tarea/cargo")
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Horas de Proyecto, TipoTarea y Cargo agrupadas por Colaborador", response = HorasProyectoTipoTareaCargoXColaborador.class, responseContainer = "List")
    public Response findHorasProyectoTipoTareaCargoXColaborador(Proyecto proyecto, TipoTarea tipoTarea, Cargo cargo) {
        try {
            return Response.ok(horaDao.findHorasProyectoTipoTareaCargoXColaborador(proyecto, tipoTarea, cargo)).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("horas/proyecto/tarea")
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Horas de Proyecto y TipoTarea agrupadas por Cargo", response = HorasProyectoTipoTareaXCargo.class, responseContainer = "List")
    public Response findHorasProyectoTipoTareaXCargo(Proyecto proyecto, TipoTarea tipoTarea) {
        try {
            return Response.ok(horaDao.findHorasProyectoTipoTareaXCargo(proyecto, tipoTarea)).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("estimaciones/proyecto/tarea")
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Estimaciones de Proyecto y TipoTarea agrupadas por Cargo", response = EstimacionProyectoTipoTareaXCargo.class, responseContainer = "List")
    public Response findEstimacionesProyectoTipoTareaXCargo(Proyecto proyecto, TipoTarea tipoTarea) {
        try {
            return Response.ok(estimacionDao.findEstimacionProyectoTipoTareaXCargo(proyecto, tipoTarea)).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("horas/proyecto")
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @ApiOperation(value = "Horas de Proyecto agrupadas por Cargo", response = HorasProyectoXCargo.class, responseContainer = "List")
    public Response findHorasProyectoXCargo(Proyecto proyecto) {
        try {
            return Response.ok(horaDao.findHorasProyectoXCargo(proyecto)).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


}

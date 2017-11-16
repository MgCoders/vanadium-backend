package coop.magnesium.sulfur.api;


import coop.magnesium.sulfur.db.dao.CargoDao;
import io.swagger.annotations.Api;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by rsperoni on 05/05/17.
 */
@Path("/tareas")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
@Api(description = "Tareas service", tags = "tareas")
public class TareaService {

    @Inject
    private Logger logger;
    @EJB
    private CargoDao cargoDao;

    @GET
    public void empty() {
    }


}

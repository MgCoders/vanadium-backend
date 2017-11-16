package coop.magnesium.sulfur.api;


import coop.magnesium.sulfur.db.dao.ProyectoDao;
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

    @GET
    public void empty() {
    }


}

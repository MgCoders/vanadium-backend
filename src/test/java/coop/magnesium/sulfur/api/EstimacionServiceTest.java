package coop.magnesium.sulfur.api;

import coop.magnesium.sulfur.db.dao.CargoDao;
import coop.magnesium.sulfur.db.dao.EstimacionDao;
import coop.magnesium.sulfur.db.dao.ProyectoDao;
import coop.magnesium.sulfur.db.dao.TipoTareaDao;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Estimacion;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;
import coop.magnesium.sulfur.utils.Logged;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 20/11/17.
 */
@RunWith(Arquillian.class)
public class EstimacionServiceTest {

    final Proyecto proyecto = new Proyecto("PP", "PP");
    final TipoTarea tipoTarea = new TipoTarea("TT", "TT");
    final Cargo cargo = new Cargo("CC", "CC", new BigDecimal(32.2));

    @Inject
    CargoDao cargoDao;
    @Inject
    ProyectoDao proyectoDao;
    @Inject
    TipoTareaDao tipoTareaDao;
    @Inject
    Logger logger;

    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, Filters.exclude(".*Test.*"),
                        Estimacion.class.getPackage(),
                        EstimacionDao.class.getPackage(),
                        Logged.class.getPackage())
                .addClass(JAXRSConfiguration.class)
                .addClass(EstimacionService.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("endpoints.properties")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Before
    public void init() {
        this.proyecto.setId(1L);
        this.tipoTarea.setId(1L);
        this.cargo.setId(1L);
    }

    @Test
    @InSequence(1)
    public void inicializarBd() {
        logger.info(proyectoDao.save(this.proyecto).toString());
        logger.info(tipoTareaDao.save(this.tipoTarea).toString());
        logger.info(cargoDao.save(this.cargo).toString());
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void createEstmacion(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/estimaciones")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new Estimacion(this.proyecto, this.tipoTarea, this.cargo)));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(1, response.readEntity(Estimacion.class).getId().longValue());
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void createEstmacionMal(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/estimaciones")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new Estimacion(this.proyecto, this.tipoTarea, this.cargo)));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }


}

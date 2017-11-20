package coop.magnesium.sulfur.api;

import coop.magnesium.sulfur.db.dao.TipoTareaDao;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 20/11/17.
 */
@RunWith(Arquillian.class)
public class TareaServiceTest {
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, Filters.exclude(".*Test.*"),
                        TipoTarea.class.getPackage(),
                        TipoTareaDao.class.getPackage(),
                        Logged.class.getPackage())
                .addClass(JAXRSConfiguration.class)
                .addClass(TareaService.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("endpoints.properties")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @InSequence(1)
    @RunAsClient
    public void createTarea(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/tareas")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new TipoTarea("TT1", "Tipo tarea 1")));
        assertEquals(1, response.readEntity(TipoTarea.class).getId().longValue());
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void createTareaCodigoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/tareas")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new TipoTarea("TT1", "Tipo tarea 2")));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void createTareaIdExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        TipoTarea tipoTarea = new TipoTarea("TT1", "Tipo tarea 2");
        tipoTarea.setId(1L);
        final Response response = webTarget
                .path("/tareas")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(tipoTarea));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }
}

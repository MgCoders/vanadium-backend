package coop.magnesium.sulfur.api;

import coop.magnesium.sulfur.api.dto.HorasProyectoXCargo;
import coop.magnesium.sulfur.db.dao.ProyectoDao;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Proyecto;
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
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 20/11/17.
 */
@RunWith(Arquillian.class)
public class ProyectoServiceTest {
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, Filters.exclude(".*Test.*"),
                        Proyecto.class.getPackage(),
                        ProyectoDao.class.getPackage(),
                        Logged.class.getPackage(),
                        HorasProyectoXCargo.class.getPackage())
                .addClass(JAXRSConfiguration.class)
                .addClass(ProyectoService.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("endpoints.properties")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }


    @Test
    @InSequence(1)
    @RunAsClient
    public void createProyecto(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/proyectos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new Proyecto("AA", "Proyecto 1")));
        assertEquals(1, response.readEntity(Proyecto.class).getId().longValue());
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void createProyectoCodigoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/proyectos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new Proyecto("AA", "Proyecto 1")));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void createProyectoIdExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        Proyecto proyecto = new Proyecto("AA", "Proyecto 1");
        proyecto.setId(1L);
        final Response response = webTarget
                .path("/proyectos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(proyecto));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(4)
    @RunAsClient
    public void getProyectos(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/proyectos")
                .request(MediaType.APPLICATION_JSON)
                .get();
        List<Proyecto> proyectoList = response.readEntity(new GenericType<List<Proyecto>>() {
        });
        assertEquals(1, proyectoList.size());
    }

    @Test
    @InSequence(5)
    @RunAsClient
    public void getProyecto(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/proyectos/1")
                .request(MediaType.APPLICATION_JSON)
                .get();
        Proyecto proyecto = response.readEntity(Proyecto.class);
        assertEquals(1L, proyecto.getId().longValue());
        assertEquals("AA", proyecto.getCodigo());
        assertEquals("Proyecto 1", proyecto.getNombre());
    }

    @Test
    @InSequence(5)
    @RunAsClient
    public void getProyectoNoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/proyectos/4")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(6)
    @RunAsClient
    public void editarProyectoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        Proyecto proyecto = new Proyecto("BB", "Proyecto 2");
        proyecto.setId(1L);
        final Response response = webTarget
                .path("/proyectos/1")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(proyecto));
        Cargo returned = response.readEntity(Cargo.class);
        assertEquals(proyecto.getCodigo(), returned.getCodigo());
        assertEquals(proyecto.getNombre(), returned.getNombre());
    }

    @Test
    @InSequence(6)
    @RunAsClient
    public void editarProyectoNoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        Proyecto proyecto = new Proyecto("BB", "Proyecto 2");
        proyecto.setId(2L);
        final Response response = webTarget
                .path("/proyectos/2")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(proyecto));
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
    }


}

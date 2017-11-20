package coop.magnesium.sulfur.api;

import coop.magnesium.sulfur.db.dao.CargoDao;
import coop.magnesium.sulfur.db.entities.Cargo;
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
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 20/11/17.
 */
@RunWith(Arquillian.class)
public class CargoServiceTest {
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, Filters.exclude(".*Test.*"),
                        Cargo.class.getPackage(),
                        CargoDao.class.getPackage(),
                        Logged.class.getPackage())
                .addClass(JAXRSConfiguration.class)
                .addClass(CargoService.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("endpoints.properties")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }


    @Test
    @InSequence(1)
    @RunAsClient
    public void createCargo(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/cargos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new Cargo("JNR", "JUNIOR", new BigDecimal(32.2))));
        assertEquals(1, response.readEntity(Cargo.class).getId().longValue());
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void createCargoCodigoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/cargos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new Cargo("JNR", "JUNIOR", new BigDecimal(32.2))));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void createCargoIdExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        Cargo cargo = new Cargo("JNR", "JUNIOR", new BigDecimal(32.2));
        cargo.setId(1L);
        final Response response = webTarget
                .path("/cargos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(cargo));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(4)
    @RunAsClient
    public void getCargos(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/cargos")
                .request(MediaType.APPLICATION_JSON)
                .get();
        List<Cargo> cargoList = response.readEntity(new GenericType<List<Cargo>>() {
        });
        assertEquals(1, cargoList.size());
    }

    @Test
    @InSequence(5)
    @RunAsClient
    public void getCargo(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/cargos/1")
                .request(MediaType.APPLICATION_JSON)
                .get();
        Cargo cargo = response.readEntity(Cargo.class);
        assertEquals(1L, cargo.getId().longValue());
        assertEquals("JNR", cargo.getCodigo());
        assertEquals("JUNIOR", cargo.getNombre());
    }

    @Test
    @InSequence(5)
    @RunAsClient
    public void getCargoNoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/cargos/4")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(6)
    @RunAsClient
    public void editarCargoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        Cargo cargo = new Cargo("JXR", "JUNIOR 2", new BigDecimal(33.2));
        cargo.setId(1L);
        final Response response = webTarget
                .path("/cargos/1")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(cargo));
        Cargo returned = response.readEntity(Cargo.class);
        assertEquals(cargo.getCodigo(), returned.getCodigo());
        assertEquals(cargo.getNombre(), returned.getNombre());
        assertEquals(cargo.getPrecioHora(), returned.getPrecioHora());
    }

    @Test
    @InSequence(6)
    @RunAsClient
    public void editarCargoNoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        Cargo cargo = new Cargo("JXR", "JUNIOR 2", new BigDecimal(33.2));
        cargo.setId(2L);
        final Response response = webTarget
                .path("/cargos/2")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(cargo));
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
    }


}

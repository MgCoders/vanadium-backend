package coop.magnesium.sulfur.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import coop.magnesium.sulfur.api.dto.HorasProyectoXCargo;
import coop.magnesium.sulfur.db.dao.CargoDao;
import coop.magnesium.sulfur.db.dao.ProyectoDao;
import coop.magnesium.sulfur.db.dao.TipoTareaDao;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.PrecioHora;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;
import coop.magnesium.sulfur.utils.Logged;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdMultipleResultsException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 20/11/17.
 */
@RunWith(Arquillian.class)
public class CargoServiceTest {
    /**
     * El mapper de jackson para LocalTime y LocalDate se registra via CDI.
     * Acá en arquillian hay que hacerlo a mano.
     */
    final ObjectMapper objectMapper = new ObjectMapper();
    final Proyecto proyecto = new Proyecto("PP", "PP");
    final TipoTarea tipoTarea = new TipoTarea("TT", "TT");
    final Cargo cargo = new Cargo("CC", "CC", new BigDecimal(32.2));
    final Cargo admin = new Cargo("ADMIN", "CC", new BigDecimal(32.2));
    final BigDecimal cuarentaYcinco = new BigDecimal(49.5).setScale(1, 1);

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
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").resolve("com.fasterxml.jackson.datatype:jackson-datatype-jsr310").withTransitivity().asFile();
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, Filters.exclude(".*Test.*"),
                        Cargo.class.getPackage(),
                        CargoDao.class.getPackage(),
                        Logged.class.getPackage(),
                        HorasProyectoXCargo.class.getPackage())
                .addClass(JAXRSConfiguration.class)
                .addClass(CargoService.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("endpoints.properties")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(libs);
    }

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.proyecto.setId(1L);
        this.tipoTarea.setId(1L);
        this.cargo.setId(1L);
    }

    @Test
    @InSequence(1)
    public void inicializarBd() throws MagnesiumBdMultipleResultsException {
        logger.info(proyectoDao.save(this.proyecto).toString());
        logger.info(tipoTareaDao.save(this.tipoTarea).toString());
        logger.info(cargoDao.save(this.cargo).toString());
        //logger.info(cargoDao.save(this.admin).toString());
        //logger.info(cargoDao.findByCodigo("ADMIN").toString());
    }


    @Test
    @InSequence(2)
    @RunAsClient
    public void createCargoCodigoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/cargos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(this.cargo));
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
        assertEquals("CC", cargo.getCodigo());
        assertEquals("CC", cargo.getNombre());
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
        Cargo cargo = new Cargo("JXR", "JUNIOR 2", new BigDecimal(33.2).setScale(2, RoundingMode.CEILING));
        cargo.setId(1L);
        final Response response = webTarget
                .path("/cargos/1")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(cargo));
        Cargo returned = response.readEntity(Cargo.class);
        assertEquals(cargo.getCodigo(), returned.getCodigo());
        assertEquals(cargo.getNombre(), returned.getNombre());
        assertEquals(cargo.getPrecioHora(LocalDate.now()).get().getPrecioHora(), returned.getPrecioHora(LocalDate.now()).get().getPrecioHora());
    }

    @Test
    @InSequence(6)
    @RunAsClient
    public void editarCargoNoExiste(@ArquillianResteasyResource final WebTarget webTarget) {
        Cargo cargo = new Cargo("JXR", "JUNIOR 2", new BigDecimal(33.2).setScale(2, RoundingMode.CEILING));
        cargo.setId(2L);
        final Response response = webTarget
                .path("/cargos/2")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(cargo));
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(7)
    @RunAsClient
    public void actualizarPrecioHora1(@ArquillianResteasyResource final WebTarget webTarget) throws JsonProcessingException {
        PrecioHora precioHora = new PrecioHora(new BigDecimal(50.2), LocalDate.of(2017, 12, 01));
        final Response response = webTarget
                .path("/cargos/1")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(precioHora));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Cargo returned = response.readEntity(Cargo.class);
        assertEquals(2, returned.getPrecioHoraHistoria().size());
        assertEquals(new BigDecimal(50.2), returned.getPrecioHora(LocalDate.of(2017, 12, 01)).get().getPrecioHora());
        assertEquals(new BigDecimal(33.2).setScale(2, RoundingMode.CEILING), returned.getPrecioHora(LocalDate.now()).get().getPrecioHora());

    }

    @Test
    @InSequence(7)
    @RunAsClient
    public void actualizarPrecioHora2(@ArquillianResteasyResource final WebTarget webTarget) throws JsonProcessingException {
        PrecioHora precioHora = new PrecioHora(cuarentaYcinco, LocalDate.of(2017, 12, 15));
        final Response response = webTarget
                .path("/cargos/1")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(precioHora));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Cargo returned = response.readEntity(Cargo.class);
        assertEquals(cuarentaYcinco, returned.getPrecioHora(LocalDate.of(2017, 12, 15)).get().getPrecioHora());
        assertEquals(3, returned.getPrecioHoraHistoria().size());
    }

    @Test
    @InSequence(7)
    @RunAsClient
    public void actualizarPrecioHora3(@ArquillianResteasyResource final WebTarget webTarget) throws JsonProcessingException {
        PrecioHora precioHora = new PrecioHora(new BigDecimal(60.5), LocalDate.of(2017, 12, 10));
        final Response response = webTarget
                .path("/cargos/1")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(precioHora));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Cargo returned = response.readEntity(Cargo.class);
        assertEquals(new BigDecimal(60.5), returned.getPrecioHora(LocalDate.of(2017, 12, 10)).get().getPrecioHora().setScale(1, 1));
        assertEquals(4, returned.getPrecioHoraHistoria().size());
    }


}

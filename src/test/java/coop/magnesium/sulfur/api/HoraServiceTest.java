package coop.magnesium.sulfur.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import coop.magnesium.sulfur.api.utils.ObjectMapperContextResolver;
import coop.magnesium.sulfur.db.dao.*;
import coop.magnesium.sulfur.db.entities.*;
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
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 22/11/17.
 */
@RunWith(Arquillian.class)
public class HoraServiceTest {

    /**
     * El mapper de jackson para LocalTime y LocalDate se registra via CDI.
     * Acá en arquillian hay que hacerlo a mano.
     */
    final ObjectMapper objectMapper = new ObjectMapper();

    final Proyecto proyecto = new Proyecto("PP", "PP");
    final TipoTarea tipoTarea = new TipoTarea("TT", "TT");
    final Cargo cargo = new Cargo("CC", "CC", new BigDecimal(32.2));
    final Colaborador colaborador = new Colaborador("em", "nom", cargo, "pwd", "ADMIN");
    @Inject
    CargoDao cargoDao;
    @Inject
    ProyectoDao proyectoDao;
    @Inject
    ColaboradorDao colaboradorDao;
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
                        Hora.class.getPackage(),
                        HoraDao.class.getPackage(),
                        Logged.class.getPackage())
                .addClass(JAXRSConfiguration.class)
                .addClass(ObjectMapperContextResolver.class)
                .addClass(HoraService.class)
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
        this.colaborador.setId(1L);
        this.cargo.setId(1L);
    }

    @Test
    @InSequence(1)
    public void inicializarBd() {
        logger.info(proyectoDao.save(this.proyecto).toString());
        logger.info(tipoTareaDao.save(this.tipoTarea).toString());
        Cargo cargo = cargoDao.save(this.cargo);
        logger.info(cargo.toString());
        this.colaborador.setCargo(cargo);
        logger.info(colaboradorDao.save(this.colaborador).toString());
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void createTarea(@ArquillianResteasyResource final WebTarget webTarget) throws IOException {
        Hora hora = new Hora(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, this.proyecto, this.tipoTarea, this.colaborador);

        System.out.println(objectMapper.writeValueAsString(hora));

        final Response response = webTarget
                .path("/horas")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(objectMapper.writeValueAsString(hora)));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        String horaCreadaString = response.readEntity(String.class);
        Hora horaCreada = objectMapper.readValue(horaCreadaString, Hora.class);
        assertEquals(1, horaCreada.getId().longValue());
        assertEquals(LocalTime.MAX.truncatedTo(ChronoUnit.MINUTES), horaCreada.getSubtotal());
    }
}

package coop.magnesium.vanadium.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import coop.magnesium.vanadium.api.dto.HorasProyectoXCargo;
import coop.magnesium.vanadium.db.dao.ColaboradorDao;
import coop.magnesium.vanadium.db.entities.Colaborador;
import coop.magnesium.vanadium.system.DataTimer;
import coop.magnesium.vanadium.system.MailEvent;
import coop.magnesium.vanadium.system.StartupBean;
import coop.magnesium.vanadium.system.TimerType;
import coop.magnesium.vanadium.utils.Logged;
import coop.magnesium.vanadium.utils.PasswordUtils;
import coop.magnesium.vanadium.utils.ex.MagnesiumSecurityException;
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
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 20/11/17.
 */
@RunWith(Arquillian.class)
public class LoginServiceTest {
    /**
     * El mapper de jackson para LocalTime y LocalDate se registra via CDI.
     * Acá en arquillian hay que hacerlo a mano.
     */
    final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    ColaboradorDao colaboradorDao;
    @Inject
    Logger logger;

    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").resolve("com.fasterxml.jackson.datatype:jackson-datatype-jsr310", "io.jsonwebtoken:jjwt").withTransitivity().asFile();
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, Filters.exclude(".*Test.*"),
                        Colaborador.class.getPackage(),
                        Logged.class.getPackage(),
                        MagnesiumSecurityException.class.getPackage(),
                        ColaboradorDao.class.getPackage(),
                        HorasProyectoXCargo.class.getPackage())
                .addClass(JAXRSConfiguration.class)
                .addClass(UserService.class)
                .addClass(StartupBean.class)
                .addClass(DataTimer.class)
                .addClass(TimerType.class)
                .addClass(MailEvent.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("endpoints.properties")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(libs);
    }

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @InSequence(1)
    public void inicializarBd() {
        logger.info(colaboradorDao.save(new Colaborador("bu", "bu", null, PasswordUtils.digestPassword("bu"), "ADMIN")).toString());
    }


    @Test
    @InSequence(2)
    @RunAsClient
    public void login(@ArquillianResteasyResource final WebTarget webTarget) {
        Form form = new Form();
        form.param("email", "bu");
        form.param("password", "bu");
        final Response response = webTarget
                .path("/users/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.form(form));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Colaborador returned = response.readEntity(Colaborador.class);
        assertEquals(null, returned.getPassword());
    }


}

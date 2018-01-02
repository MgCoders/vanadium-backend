package coop.magnesium.sulfur.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import coop.magnesium.sulfur.api.dto.HorasDeProyectoPorCargo;
import coop.magnesium.sulfur.api.utils.JWTTokenNeeded;
import coop.magnesium.sulfur.api.utils.RoleNeeded;
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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 22/11/17.
 */
@RunWith(Arquillian.class)
public class ReporteServiceTest {

    /**
     * El mapper de jackson para LocalTime y LocalDate se registra via CDI.
     * Acá en arquillian hay que hacerlo a mano.
     */
    final ObjectMapper objectMapper = new ObjectMapper();

    final Proyecto proyecto = new Proyecto("PP", "PP");
    final TipoTarea tipoTarea = new TipoTarea("TT", "TT");
    final Cargo cargo = new Cargo("CC", "CC", new BigDecimal(32.2));
    final Colaborador colaborador_admin = new Colaborador("em", "nom", cargo, "pwd", "ADMIN");
    final Colaborador colaborador_user = new Colaborador("em1", "nom", cargo, "pwd", "USER");

    @Inject
    CargoDao cargoDao;
    @Inject
    ProyectoDao proyectoDao;
    @Inject
    ColaboradorDao colaboradorDao;
    @Inject
    TipoTareaDao tipoTareaDao;
    @Inject
    HoraDao horaDao;
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
                .addClass(JWTTokenNeeded.class)
                .addClass(RoleNeeded.class)
                .addClass(JWTTokenNeededFilterMock.class)
                .addClass(RoleNeededFilterMock.class)
                .addClass(HoraService.class)
                .addClass(HorasDeProyectoPorCargo.class)
                .addClass(UserServiceMock.class)
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
        this.colaborador_admin.setId(1L);
        this.cargo.setId(1L);
        this.colaborador_user.setId(2L);
    }

    @Test
    @InSequence(1)
    public void inicializarBd() {
        logger.info(proyectoDao.save(this.proyecto).toString());
        logger.info(tipoTareaDao.save(this.tipoTarea).toString());
        Cargo cargo = cargoDao.save(this.cargo);
        logger.info(cargo.toString());
        this.colaborador_admin.setCargo(cargo);
        this.colaborador_user.setCargo(cargo);
        logger.info(colaboradorDao.save(this.colaborador_admin).toString());
        logger.info(colaboradorDao.save(this.colaborador_user).toString());

        Hora hora = new Hora(LocalDate.of(2017, 12, 24), LocalTime.MIN, LocalTime.MAX, this.colaborador_user);
        hora.getHoraDetalleList().add(new HoraDetalle(this.proyecto, this.tipoTarea, Duration.ofHours(23).plus(Duration.ofMinutes(59))));
        horaDao.save(hora);

        List<HorasDeProyectoPorCargo> horasDeProyectoPorCargos = horaDao.findHoras(null, null);
        horasDeProyectoPorCargos.forEach(horasDeProyectoPorCargo -> logger.info(horasDeProyectoPorCargo.toString()));
        horasDeProyectoPorCargos.size();
    }


    @Test
    @InSequence(9)
    @RunAsClient
    public void getHorasUserBien(@ArquillianResteasyResource final WebTarget webTarget) throws IOException {


        final Response response = webTarget
                .path("/horas/user/2/01-01-2011/01-01-2018")
                .request(MediaType.APPLICATION_JSON)
                .header("AUTHORIZATION", "USER:2")
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Hora> horaList = response.readEntity(new GenericType<List<Hora>>() {
        });
        assertEquals(1, horaList.size());
    }


}

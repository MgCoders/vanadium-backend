package coop.magnesium.sulfur.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import coop.magnesium.sulfur.api.dto.EstimacionProyectoTipoTareaXCargo;
import coop.magnesium.sulfur.api.dto.HorasProyectoTipoTareaCargoXColaborador;
import coop.magnesium.sulfur.api.dto.HorasProyectoXCargo;
import coop.magnesium.sulfur.api.dto.ReporteHoras1;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    final Proyecto proyecto1 = new Proyecto("P1", "P1");
    final Proyecto proyecto2 = new Proyecto("P2", "P2");
    final TipoTarea tipoTarea1 = new TipoTarea("T1", "T1");
    final TipoTarea tipoTarea2 = new TipoTarea("T2", "T2");
    final Cargo cargo1 = new Cargo("C1", "C1", new BigDecimal(32.2));
    final Cargo cargo2 = new Cargo("C2", "C2", new BigDecimal(33.2));
    final Colaborador colaborador_admin = new Colaborador("em", "nom", cargo1, "pwd", "ADMIN");
    final Colaborador colaborador_user = new Colaborador("em1", "nom", cargo2, "pwd", "USER");

    @Inject
    CargoDao cargoDao;
    @Inject
    ProyectoDao proyectoDao;
    @Inject
    ColaboradorDao colaboradorDao;
    @Inject
    TipoTareaDao tipoTareaDao;
    @Inject
    EstimacionDao estimacionDao;
    @Inject
    HoraDao horaDao;
    @Inject
    ReportesDao reporteDao;
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
                        Logged.class.getPackage(),
                        HorasProyectoXCargo.class.getPackage())
                .addClass(JAXRSConfiguration.class)
                .addClass(JWTTokenNeeded.class)
                .addClass(RoleNeeded.class)
                .addClass(JWTTokenNeededFilterMock.class)
                .addClass(RoleNeededFilterMock.class)
                .addClass(HoraService.class)
                .addClass(ReportesService.class)
                .addClass(EstimacionDetalle.class)
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
    }

    @Test
    @InSequence(1)
    public void inicializarBd() {
        Proyecto proyecto1 = proyectoDao.save(this.proyecto1);
        Proyecto proyecto2 = proyectoDao.save(this.proyecto2);
        TipoTarea tipoTarea1 = tipoTareaDao.save(this.tipoTarea1);
        TipoTarea tipoTarea2 = tipoTareaDao.save(this.tipoTarea2);
        Cargo cargo1 = cargoDao.save(this.cargo1);
        Cargo cargo2 = cargoDao.save(this.cargo2);
        this.colaborador_admin.setCargo(cargo1);
        this.colaborador_user.setCargo(cargo2);
        Colaborador colaborador1 = colaboradorDao.save(this.colaborador_admin);
        Colaborador colaborador2 = colaboradorDao.save(this.colaborador_user);

        Estimacion estimacion = new Estimacion(proyecto1, null, LocalDate.now());
        estimacion.getEstimacionDetalleList().add(new EstimacionDetalle(tipoTarea1, cargo1, Duration.ofHours(3), new BigDecimal(150.5)));
        estimacion.getEstimacionDetalleList().add(new EstimacionDetalle(tipoTarea1, cargo2, Duration.ofHours(6), new BigDecimal(170)));
        estimacionDao.save(estimacion);

        Estimacion estimacion2 = new Estimacion(proyecto1, null, LocalDate.now().plusDays(1));
        estimacion2.getEstimacionDetalleList().add(new EstimacionDetalle(tipoTarea1, cargo1, Duration.ofHours(3), new BigDecimal(150.5)));
        estimacionDao.save(estimacion2);

        Hora hora = new Hora(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, colaborador1);
        hora.getHoraDetalleList().add(new HoraDetalle(proyecto1, tipoTarea1, Duration.ofHours(20)));
        horaDao.save(hora);

        Hora hora2 = new Hora(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, colaborador2);
        hora2.getHoraDetalleList().add(new HoraDetalle(proyecto1, tipoTarea1, Duration.ofHours(15)));
        horaDao.save(hora2);

        Hora hora3 = new Hora(LocalDate.now().plusDays(1), LocalTime.MIN, LocalTime.MAX, colaborador2);
        hora3.getHoraDetalleList().add(new HoraDetalle(proyecto1, tipoTarea1, Duration.ofHours(10)));
        horaDao.save(hora3);

        Hora hora4 = new Hora(LocalDate.now().plusDays(1), LocalTime.MIN, LocalTime.MAX, colaborador1);
        hora4.getHoraDetalleList().add(new HoraDetalle(proyecto1, tipoTarea1, Duration.ofHours(5)));
        horaDao.save(hora4);

        Hora hora5 = new Hora(LocalDate.now().plusDays(1), LocalTime.MIN, LocalTime.MAX, colaborador1);
        hora5.getHoraDetalleList().add(new HoraDetalle(proyecto1, tipoTarea2, Duration.ofHours(20)));
        horaDao.save(hora5);

        Hora hora6 = new Hora(LocalDate.now().plusDays(1), LocalTime.MIN, LocalTime.MAX, colaborador2);
        hora6.getHoraDetalleList().add(new HoraDetalle(proyecto1, tipoTarea2, Duration.ofHours(10).plusMinutes(10)));
        horaDao.save(hora6);

        //Cargo 1, P1, T1, 40h
        //Cargo 2, P1, T1, 20h

        //Cargo 1, P1, T2, 20h
        //Cargo 2, P1, T2, 10h

        //Cargo 1 45h
        //Cargo 2 45:10h

        List<HoraDetalle> lt = horaDao.prueba(proyecto1, tipoTarea1);
    }


    @Test
    @InSequence(2)
    public void horasXCargo() {

        proyecto1.setId(1L);
        tipoTarea1.setId(1L);


        List<ReporteHoras1> reporteHoras1 = reporteDao.reporteHoras1(proyecto1, tipoTarea1);
        reporteHoras1.forEach(reporteHoras11 -> logger.info(reporteHoras11.toString()));

    }

    @Test
    @InSequence(3)
    public void estimacionesXCargo() {
        proyecto1.setId(1L);
        tipoTarea1.setId(1L);

        List<EstimacionProyectoTipoTareaXCargo> estimacionProyectoTipoTareaXCargos = estimacionDao.findEstimacionProyectoTipoTareaXCargo(proyecto1, tipoTarea1);
        estimacionProyectoTipoTareaXCargos.forEach(estimacionProyectoTipoTareaXCargo -> {
            logger.info(estimacionProyectoTipoTareaXCargo.toString());
            if (estimacionProyectoTipoTareaXCargo.cargo.getCodigo().equals("C1")) {
                assertEquals(BigDecimal.valueOf(6).setScale(2, RoundingMode.CEILING), estimacionProyectoTipoTareaXCargo.cantidadHoras);

            } else {
                assertEquals(BigDecimal.valueOf(6).setScale(2, RoundingMode.CEILING), estimacionProyectoTipoTareaXCargo.cantidadHoras);
            }
        });

        assertEquals(2, estimacionProyectoTipoTareaXCargos.size());
    }

    @Test
    @InSequence(4)
    public void totalHorasXCargo() {
        proyecto1.setId(1L);

        List<HorasProyectoXCargo> horasProyectoXCargo = horaDao.findHorasProyectoXCargo(proyecto1);
        horasProyectoXCargo.forEach(horasProyectoXCargo1 -> {
            logger.info(horasProyectoXCargo1.toString());
            if (horasProyectoXCargo1.cargo.getCodigo().equals("C1")) {
                assertEquals(45, horasProyectoXCargo1.cantidadHoras.toHours());

            } else {
                assertEquals(45, horasProyectoXCargo1.cantidadHoras.toHours());
            }
        });

        assertEquals(2, horasProyectoXCargo.size());
    }

    @Test
    @InSequence(5)
    public void totalHorasCargo1() {
        proyecto1.setId(1L);
        tipoTarea1.setId(1L);
        cargo1.setId(1L);

        List<HorasProyectoTipoTareaCargoXColaborador> horasProyectoTipoTareaCargoXColaborador = horaDao.findHorasProyectoTipoTareaCargoXColaborador(proyecto1, tipoTarea1, cargo1);
        logger.info(horasProyectoTipoTareaCargoXColaborador.get(0).toString());
        assertEquals(45, horasProyectoTipoTareaCargoXColaborador.get(0).cantidadHoras.toHours());
        assertEquals(cargo1.getCodigo(), horasProyectoTipoTareaCargoXColaborador.get(0).cargo.getCodigo());
        assertEquals(1, horasProyectoTipoTareaCargoXColaborador.size());
    }

    @Test
    @InSequence(6)
    public void totalHorasCargo2() {
        proyecto1.setId(1L);
        tipoTarea1.setId(1L);
        cargo2.setId(2L);

        List<HorasProyectoTipoTareaCargoXColaborador> horasProyectoTipoTareaCargoXColaborador = horaDao.findHorasProyectoTipoTareaCargoXColaborador(proyecto1, tipoTarea1, cargo2);
        logger.info(horasProyectoTipoTareaCargoXColaborador.get(0).toString());
        assertEquals(45, horasProyectoTipoTareaCargoXColaborador.get(0).cantidadHoras.toHours());
        assertEquals(cargo2.getCodigo(), horasProyectoTipoTareaCargoXColaborador.get(0).cargo.getCodigo());
        assertEquals(1, horasProyectoTipoTareaCargoXColaborador.size());
    }

    @Test
    @InSequence(7)
    @RunAsClient
    public void getHorasProyectoTareaXCargo(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/reportes/horas/proyecto/1/tarea/1")
                .request(MediaType.APPLICATION_JSON)
                .header("AUTHORIZATION", "ADMIN:2")
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<ReporteHoras1> horaList = response.readEntity(new GenericType<List<ReporteHoras1>>() {
        });
        assertEquals(3, horaList.size());
        assertEquals(25, horaList.get(0).cantidadHoras);
        assertEquals(25, horaList.get(1).cantidadHoras);
        assertEquals(50, horaList.get(2).cantidadHoras);

    }

    @Test
    @InSequence(8)
    @RunAsClient
    public void getHorasProyectoTareaXCargo2(@ArquillianResteasyResource final WebTarget webTarget) {
        final Response response = webTarget
                .path("/reportes/horas/proyecto/1/tarea/2")
                .request(MediaType.APPLICATION_JSON)
                .header("AUTHORIZATION", "ADMIN:2")
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<ReporteHoras1> horaList = response.readEntity(new GenericType<List<ReporteHoras1>>() {
        });
        assertEquals(3, horaList.size());
        assertEquals(20, horaList.get(0).cantidadHoras);
        assertEquals(20.10, horaList.get(1).cantidadHoras);
        assertEquals(40.10, horaList.get(2).cantidadHoras);

    }



}

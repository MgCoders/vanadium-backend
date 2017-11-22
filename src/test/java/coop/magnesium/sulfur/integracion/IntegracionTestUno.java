package coop.magnesium.sulfur.integracion;

import coop.magnesium.sulfur.api.HoraService;
import coop.magnesium.sulfur.api.JAXRSConfiguration;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 22/11/17.
 */
@RunWith(Arquillian.class)
public class IntegracionTestUno {

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

    @Deployment(testable = true)
    public static WebArchive createDeployment() {
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
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @InSequence(1)
    public void inicializarBd() {
        Proyecto proyecto = proyectoDao.save(this.proyecto);
        TipoTarea tipoTarea = tipoTareaDao.save(this.tipoTarea);
        Cargo cargo = cargoDao.save(this.cargo);
        this.colaborador.setCargo(cargo);
        colaboradorDao.save(this.colaborador);
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void createTarea(@ArquillianResteasyResource final WebTarget webTarget) {
        Hora hora = new Hora(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, this.proyecto, this.tipoTarea, this.colaborador);
        final Response response = webTarget
                .path("/horas")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(hora));
        assertEquals(200, response.getStatus());
        assertEquals(1, response.readEntity(Hora.class).getId().longValue());
    }
}

package coop.magnesium.sulfur.system;

import coop.magnesium.sulfur.db.dao.ColaboradorDao;
import coop.magnesium.sulfur.db.dao.ConfiguracionDao;
import coop.magnesium.sulfur.db.dao.HoraDao;
import coop.magnesium.sulfur.db.dao.RecuperacionPasswordDao;
import coop.magnesium.sulfur.db.entities.*;
import coop.magnesium.sulfur.utils.PasswordUtils;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdMultipleResultsException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static coop.magnesium.sulfur.system.TimerType.NOTIFICACION_ALERTA;
import static coop.magnesium.sulfur.system.TimerType.RECUPERACION_CONTRASENA;

/**
 * Created by rsperoni on 18/11/17.
 */
@Singleton
@Startup
public class StartupBean {


    @Inject
    Logger logger;
    @Resource
    TimerService timerService;
    @EJB
    HoraDao horaDao;
    @EJB
    ColaboradorDao colaboradorDao;
    @Inject
    Event<Notificacion> notificacionEvent;
    @Inject
    ConfiguracionDao configuracionDao;
    @Inject
    String jbossNodeName;
    @Inject
    RecuperacionPasswordDao recuperacionPasswordDao;

    @PostConstruct
    public void init() {
        try {
            if (colaboradorDao.findByEmail("info@magnesium.coop") == null) {
                colaboradorDao.save(new Colaborador("info@magnesium.coop", "root", null, PasswordUtils.digestPassword(UUID.randomUUID().toString()), "ADMIN"));
            }
        } catch (MagnesiumBdMultipleResultsException e) {
            logger.warning(e.getMessage());
        }
        configuraciones();
        setMyselfAsNodoMaster();
        setTimerNotificaciones();
    }

    public void setTimerNotificaciones() {
        if (configuracionDao.getPeriodicidadNotificaciones() != 0) {
            Instant instant = Instant.now().plus(configuracionDao.getPeriodicidadNotificaciones(), ChronoUnit.HOURS);
            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setInfo(new DataTimer(TimerType.NOTIFICACION_ALERTA, null));
            timerService.createSingleActionTimer(Date.from(instant), timerConfig);
        }
    }

    public void setMyselfAsNodoMaster() {
        configuracionDao.setNodoMaster(jbossNodeName);

    }

    public void configuraciones() {
        List<Configuracion> emailOn = configuracionDao.findAllByClave(TipoConfiguracion.NOTIFICACION_MAIL_ACTIVADO);
        if (emailOn.isEmpty()) {
            configuracionDao.save(new Configuracion(TipoConfiguracion.NOTIFICACION_MAIL_ACTIVADO, "1"));
        }
        List<Configuracion> periodicidad = configuracionDao.findAllByClave(TipoConfiguracion.NOTIFICACION_PERIODICIDAD);
        if (periodicidad.isEmpty()) {
            configuracionDao.save(new Configuracion(TipoConfiguracion.NOTIFICACION_PERIODICIDAD, "48"));
        }
    }

    @Timeout
    public void timeout(Timer timer) {
        if (timer.getInfo().getClass().getCanonicalName().equals(DataTimer.class.getCanonicalName())) {
            DataTimer dataTimer = ((DataTimer) timer.getInfo());
            switch (dataTimer.timerType) {
                case NOTIFICACION_ALERTA:
                    logger.info("Timeout: " + NOTIFICACION_ALERTA.name());
                    alertaHorasSinCargar();
                    setTimerNotificaciones();
                    break;
                case RECUPERACION_CONTRASENA:
                    logger.info("Timeout: " + RECUPERACION_CONTRASENA.name());
                    recuperacionPasswordDao.delete((String) dataTimer.obj);
                    break;
            }
        }
    }

    public void putRecuperacionPassword(RecuperacionPassword recuperacionPassword) {
        Instant instant = recuperacionPassword.getExpirationDate().toInstant(ZoneOffset.UTC);
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo(recuperacionPassword.getToken());
        timerService.createSingleActionTimer(Date.from(instant), timerConfig);
        recuperacionPasswordDao.save(recuperacionPassword);
    }

    public RecuperacionPassword getRecuperacionInfo(String token) {
        RecuperacionPassword recuperacionPassword = recuperacionPasswordDao.findById(token);
        if (recuperacionPassword != null && recuperacionPassword.getExpirationDate().isAfter(LocalDateTime.now())) {
            return recuperacionPassword;
        } else {
            recuperacionPasswordDao.delete(token);
            return null;
        }
    }

    public void alertaHorasSinCargar() {
        //Solo si soy master
        if (configuracionDao.getEntityManager().equals(jbossNodeName)) {
            LocalDate hoy = LocalDate.now();
            //Solo días de semana
            if (!hoy.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !hoy.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                colaboradorDao.findAll().stream().filter(colaborador -> colaborador.getCargo() != null)
                        .forEach(colaborador -> {
                            //Si hace más de 3 días que no hay horas
                            if (horaDao.findAllByColaborador(colaborador, LocalDate.now().minusDays(3), LocalDate.now()).isEmpty()) {
                                notificacionEvent.fire(new Notificacion(TipoNotificacion.FALTAN_HORAS, colaborador, "Más de dos días sin ingresar horas"));
                            }
                        });
            }
        }
    }


}

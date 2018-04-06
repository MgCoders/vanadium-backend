package coop.magnesium.sulfur.system;

import coop.magnesium.sulfur.db.dao.*;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.db.entities.Notificacion;
import coop.magnesium.sulfur.db.entities.RecuperacionPassword;
import coop.magnesium.sulfur.db.entities.TipoNotificacion;
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

import static coop.magnesium.sulfur.system.TimerType.*;

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
    ConfiguracionDao configuracionDao;
    @Inject
    String jbossNodeName;
    @Inject
    RecuperacionPasswordDao recuperacionPasswordDao;
    @Inject
    NotificacionDao notificacionDao;
    @Inject
    Event<MailEvent> mailEvent;
    @Inject
    Event<Notificacion> notificacionEvent;

    @PostConstruct
    public void init() {
        System.setProperty("user.timezone", "America/Montevideo");
        logger.warning("FECHA HORA DE JVM: " + LocalDateTime.now());

        try {
            if (colaboradorDao.findByEmail("info@magnesium.coop") == null) {
                colaboradorDao.save(new Colaborador("info@magnesium.coop", "root", null, PasswordUtils.digestPassword(UUID.randomUUID().toString()), "ADMIN"));
            }
        } catch (MagnesiumBdMultipleResultsException e) {
            logger.warning(e.getMessage());
        }
        configuraciones();
        setMyselfAsNodoMaster();
        //Solo si soy master
        if (configuracionDao.getEntityManager().equals(jbossNodeName)) {
            setTimerNotificaciones();
            setTimerCleanRecuperacionContrasena();
            setTimerEnvioMails();
        }
    }

    public void setTimerNotificaciones() {
        if (configuracionDao.getPeriodicidadNotificaciones() != 0) {
            Instant instant = Instant.now().plus(configuracionDao.getPeriodicidadNotificaciones(), ChronoUnit.HOURS);
            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setInfo(new DataTimer(TimerType.NOTIFICACION_ALERTA, null));
            timerService.createSingleActionTimer(Date.from(instant), timerConfig);
        }
    }

    public void setTimerCleanRecuperacionContrasena() {
        Instant instant = Instant.now().plus(1, ChronoUnit.HOURS);
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo(new DataTimer(TimerType.RECUPERACION_CONTRASENA, null));
        timerService.createSingleActionTimer(Date.from(instant), timerConfig);
    }

    public void setTimerEnvioMails() {
        Instant instant = Instant.from(LocalTime.of(9, 30));
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo(new DataTimer(TimerType.ENVIO_MAIL, null));
        timerService.createSingleActionTimer(Date.from(instant), timerConfig);
    }

    public void setMyselfAsNodoMaster() {
        configuracionDao.setNodoMaster(jbossNodeName);

    }

    public void configuraciones() {
        if (!configuracionDao.isEmailOn()) {
            configuracionDao.setMailOn(false);
        }
        if (configuracionDao.getPeriodicidadNotificaciones().equals(0L)) {
            configuracionDao.setPeriodicidadNotificaciones(48L);
        }
        if (configuracionDao.getDestinatariosNotificacionesAdmins().isEmpty()) {
            configuracionDao.addDestinatarioNotificacionesAdmins("info@magnesium.coop");
        }
        if (configuracionDao.getMailFrom() == null) {
            configuracionDao.setMailFrom("no-reply@mm.com");
        }
        if (configuracionDao.getMailHost() == null) {
            configuracionDao.setMailPort("1025");
        }
        if (configuracionDao.getMailPort() == null) {
            configuracionDao.setMailHost("ip-172-31-6-242");
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
                    cleanRecuperacionContrasena();
                    setTimerCleanRecuperacionContrasena();
                    break;
                case ENVIO_MAIL:
                    logger.info("Timeout: " + ENVIO_MAIL.name());
                    enviarMailsConNotificaciones();
                    setTimerEnvioMails();
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

    public void cleanRecuperacionContrasena() {
        //Solo si soy master

        logger.info("Master cleaning Recuperacion Contraseña");
        recuperacionPasswordDao.findAll().forEach(recuperacionPassword -> {
            if (recuperacionPassword.getExpirationDate().isBefore(LocalDateTime.now())) {
                recuperacionPasswordDao.delete(recuperacionPassword);
            }
        });

    }

    public void alertaHorasSinCargar() {

        logger.info("Master generando notificaciones");
        LocalDate hoy = LocalDate.now();
        //Solo días de semana
        if (!hoy.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !hoy.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            colaboradorDao.findAll().stream().filter(colaborador -> colaborador.getCargo() != null)
                    .forEach(colaborador -> {
                        //Si hace más de 3 días que no hay horas
                        if (horaDao.findAllByColaborador(colaborador, LocalDate.now().minusDays(3), LocalDate.now()).isEmpty()) {
                            notificacionEvent.fire(new Notificacion(TipoNotificacion.FALTAN_HORAS, colaborador, colaborador.getNombre() + " no cargó horas en más de 2 días."));
                        }
                    });
        }

    }

    public void enviarMailsConNotificaciones() {
        logger.info("Master enviando mails");
        StringBuilder stringBuilder = new StringBuilder();
        notificacionDao.findAllNoEnviadas().forEach(notificacion -> {
            stringBuilder.append("- ").append(notificacion.getTexto()).append("\n");
            notificacion.setEnviado(true);
        });

        List<String> mailsAdmins = configuracionDao.getDestinatariosNotificacionesAdmins();
        if (!stringBuilder.toString().isEmpty()) {
            mailEvent.fire(
                    new MailEvent(mailsAdmins,
                            stringBuilder.toString(),
                            "MARQ: Notificaciones"));
        }

    }

}

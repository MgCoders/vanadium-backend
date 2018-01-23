package coop.magnesium.sulfur.system;

import coop.magnesium.sulfur.db.dao.ColaboradorDao;
import coop.magnesium.sulfur.db.dao.HoraDao;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.db.entities.Notificacion;
import coop.magnesium.sulfur.db.entities.TipoNotificacion;
import coop.magnesium.sulfur.utils.DataRecuperacionPassword;
import coop.magnesium.sulfur.utils.PasswordUtils;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdMultipleResultsException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.time.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

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

    private ConcurrentHashMap recuperacionPassword = null;

    @PostConstruct
    public void init() {
        this.recuperacionPassword = new ConcurrentHashMap();
        try {
            if (colaboradorDao.findByEmail("root@magnesium.coop") == null) {
                colaboradorDao.save(new Colaborador("root@magnesium.coop", "root", null, PasswordUtils.digestPassword(System.getenv("ROOT_PASSWORD") != null ? System.getenv("ROOT_PASSWORD") : "bu"), "ADMIN"));
            }
        } catch (MagnesiumBdMultipleResultsException e) {
            logger.warning(e.getMessage());
        }
    }

    @Timeout
    public void timeout(Timer timer) {
        logger.info("Timeout: " + timer.toString());
        recuperacionPassword.remove(timer.getInfo());
    }

    public void putRecuperacionPassword(DataRecuperacionPassword dataRecuperacionPassword) {
        Instant instant = dataRecuperacionPassword.getExpirationDate().toInstant(ZoneOffset.UTC);
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo(dataRecuperacionPassword.getToken());
        timerService.createSingleActionTimer(Date.from(instant), timerConfig);
        recuperacionPassword.put(dataRecuperacionPassword.getToken(), dataRecuperacionPassword);
    }

    public DataRecuperacionPassword getRecuperacionInfo(String token) {
        DataRecuperacionPassword dataRecuperacionPassword = (DataRecuperacionPassword) recuperacionPassword.get(token);
        if (dataRecuperacionPassword != null && dataRecuperacionPassword.getExpirationDate().isAfter(LocalDateTime.now())) {
            return (DataRecuperacionPassword) recuperacionPassword.get(token);
        } else {
            recuperacionPassword.remove(token);
            return null;
        }
    }

    @Schedule(hour = "7", persistent = false)
    public void alertaHorasSinCargar() {
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

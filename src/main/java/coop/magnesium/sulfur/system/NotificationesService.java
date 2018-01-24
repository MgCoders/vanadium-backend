package coop.magnesium.sulfur.system;

import coop.magnesium.sulfur.db.dao.NotificacionDao;
import coop.magnesium.sulfur.db.entities.Notificacion;
import coop.magnesium.sulfur.utils.Logged;

import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by rsperoni on 22/01/18.
 */
@Stateless
public class NotificationesService {

    @Inject
    Logger logger;
    @EJB
    NotificacionDao notificacionDao;
    @Inject
    Event<MailEvent> mailEvent;

    @Logged
    @Asynchronous
    @Lock(LockType.READ)
    public void nuevaNotificacionHoras(@Observes(during = TransactionPhase.AFTER_SUCCESS) Notificacion notificacion) {
        try {
            Notificacion notificacionSaved = notificacionDao.save(notificacion);
            switch (notificacionSaved.getTipo()) {
                case NUEVA_HORA:
                    if (notificacion.getHora().getDia().isBefore(LocalDate.now().minusDays(2))) {
                        mailEvent.fire(
                                new MailEvent(Arrays.asList("rsperoni@magnesium.coop,cbauza@magnesium.coop"),
                                        MailService.generarEmailAviso(notificacion),
                                        "MARQ: Alerta, carga de hora antigua"));
                    }
                    break;
                case FALTAN_HORAS:
                    mailEvent.fire(
                            new MailEvent(Arrays.asList("rsperoni@magnesium.coop,cbauza@magnesium.coop"),
                                    MailService.generarEmailAviso(notificacion),
                                    "MARQ: Alerta, faltan horas"));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }
}

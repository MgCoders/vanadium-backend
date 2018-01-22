package coop.magnesium.sulfur.system;

import coop.magnesium.sulfur.db.dao.NotificacionDao;
import coop.magnesium.sulfur.db.entities.Notificacion;
import coop.magnesium.sulfur.utils.Logged;

import javax.ejb.*;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
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

    @Logged
    @Asynchronous
    @Lock(LockType.READ)
    public void nuevaNotificacionHoras(@Observes(during = TransactionPhase.AFTER_SUCCESS) Notificacion notificacionHoras) {
        try {
            notificacionDao.save(notificacionHoras);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }
}

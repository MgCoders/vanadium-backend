package coop.magnesium.vanadium.system;

import coop.magnesium.vanadium.db.dao.CargoDao;
import coop.magnesium.vanadium.db.dao.ConfiguracionDao;
import coop.magnesium.vanadium.db.dao.NotificacionDao;
import coop.magnesium.vanadium.db.entities.Notificacion;
import coop.magnesium.vanadium.utils.Logged;

import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
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
    public void nuevaNotificacionHoras(@Observes(during = TransactionPhase.AFTER_SUCCESS) Notificacion notificacion) {
        logger.info(notificacionDao.save(notificacion).toString());
    }
}

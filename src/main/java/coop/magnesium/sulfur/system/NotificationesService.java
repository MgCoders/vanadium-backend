package coop.magnesium.sulfur.system;

import coop.magnesium.sulfur.db.dao.CargoDao;
import coop.magnesium.sulfur.db.dao.ColaboradorDao;
import coop.magnesium.sulfur.db.dao.NotificacionDao;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.db.entities.Notificacion;
import coop.magnesium.sulfur.utils.Logged;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdMultipleResultsException;

import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    CargoDao cargoDao;
    @Inject
    ColaboradorDao colaboradorDao;
    @Inject
    Event<MailEvent> mailEvent;

    @Logged
    @Asynchronous
    @Lock(LockType.READ)
    public void nuevaNotificacionHoras(@Observes(during = TransactionPhase.AFTER_SUCCESS) Notificacion notificacion) throws MagnesiumBdMultipleResultsException {
        List<String> mailsAdmins = new ArrayList<>();
        Cargo admin = cargoDao.findByCodigo("ADMIN");
        if (admin != null) {
            logger.info(admin.toString());
            mailsAdmins = colaboradorDao.findAllByCargo(admin).stream().map(Colaborador::getEmail).collect(Collectors.toList());
            logger.info("MAILS ADMINS"+mailsAdmins);
        }
        try {
            Notificacion notificacionSaved = notificacionDao.save(notificacion);
            switch (notificacionSaved.getTipo()) {
                case NUEVA_HORA:
                    if (notificacion.getHora().getDia().isBefore(LocalDate.now().minusDays(2))) {
                        mailEvent.fire(
                                new MailEvent(mailsAdmins,
                                        MailService.generarEmailAviso(notificacion),
                                        "MARQ: Alerta, carga de hora antigua"));
                    }
                    break;
                case FALTAN_HORAS:
                    mailEvent.fire(
                            new MailEvent(mailsAdmins,
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

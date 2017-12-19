package coop.magnesium.sulfur.system;

import coop.magnesium.sulfur.db.dao.CargoDao;
import coop.magnesium.sulfur.db.dao.ColaboradorDao;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.utils.DataRecuperacionPassword;
import coop.magnesium.sulfur.utils.PasswordUtils;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdMultipleResultsException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by rsperoni on 18/11/17.
 */
@Singleton
@Startup
public class StartupBean {

    @EJB
    CargoDao cargoDao;
    @EJB
    ColaboradorDao colaboradorDao;
    @Inject
    Logger logger;
    @Resource
    TimerService timerService;

    private ConcurrentHashMap recuperacionPassword = null;

    @PostConstruct
    public void init() {
        this.recuperacionPassword = new ConcurrentHashMap();
        try {
            if (colaboradorDao.findByEmail("root@magnesium.coop") == null) {
                colaboradorDao.save(new Colaborador("root@magnesium.coop", "root", null, PasswordUtils.digestPassword(System.getenv("ROOT_PASSWORD")), "ADMIN"));
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


}

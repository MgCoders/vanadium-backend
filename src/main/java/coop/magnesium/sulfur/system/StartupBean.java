package coop.magnesium.sulfur.system;

import coop.magnesium.sulfur.db.dao.CargoDao;
import coop.magnesium.sulfur.db.dao.ColaboradorDao;
import coop.magnesium.sulfur.db.entities.Cargo;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.math.BigDecimal;
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

    @PostConstruct
    public void init() {

        cargoDao.save(new Cargo("JUNIOR", "JUNIOR", new BigDecimal(32.2)));
        /*try {
            if (colaboradorDao.findByEmail("root@magnesium.coop") == null) {
                Colaborador root = new Colaborador();
                root.setNombre("Root");
                root.setRole("ADMIN");
                root.setCargo(null);
                root.setPassword(PasswordUtils.digestPassword("root"));
                colaboradorDao.save(root);
            }
        } catch (MagnesiumBdMultipleResultsException e) {
            logger.severe(e.getMessage());
        }*/
    }
}

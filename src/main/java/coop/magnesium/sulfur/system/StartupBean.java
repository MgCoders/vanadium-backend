package coop.magnesium.sulfur.system;

import coop.magnesium.sulfur.db.dao.CargoDao;
import coop.magnesium.sulfur.db.entities.Cargo;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.math.BigDecimal;

/**
 * Created by rsperoni on 18/11/17.
 */
@Singleton
@Startup
public class StartupBean {

    @EJB
    CargoDao cargoDao;

    @PostConstruct
    public void init() {
        cargoDao.save(new Cargo("JUNIOR", new BigDecimal(32.2)));
    }
}

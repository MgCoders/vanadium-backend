package coop.magnesium.vanadium.db.dao;

import coop.magnesium.vanadium.db.entities.SulfurUser;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by rsperoni on 28/10/17.
 */
@Stateless
public class UserDao extends AbstractDao<SulfurUser, String> {

    @PersistenceContext
    EntityManager em;

    @Override
    public Class<SulfurUser> getEntityClass() {
        return SulfurUser.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}

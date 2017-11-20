package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.db.entities.Hora;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by rsperoni on 28/10/17.
 */
@Stateless
public class HoraDao extends AbstractDao<Hora, Long> {

    @PersistenceContext
    EntityManager em;

    @Override
    public Class<Hora> getEntityClass() {
        return Hora.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }


}

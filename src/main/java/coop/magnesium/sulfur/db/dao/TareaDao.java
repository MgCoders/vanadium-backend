package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.db.entities.Tarea;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by rsperoni on 28/10/17.
 */
@Stateless
public class TareaDao extends AbstractDao<Tarea, String> {

    @PersistenceContext
    EntityManager em;

    @Override
    public Class<Tarea> getEntityClass() {
        return Tarea.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}

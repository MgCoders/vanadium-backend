package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.db.entities.TipoTarea;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by rsperoni on 28/10/17.
 */
@Stateless
public class TipoTareaDao extends AbstractDao<TipoTarea, Long> {

    @PersistenceContext
    EntityManager em;

    @Override
    public Class<TipoTarea> getEntityClass() {
        return TipoTarea.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}

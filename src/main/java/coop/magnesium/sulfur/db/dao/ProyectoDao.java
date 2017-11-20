package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.db.entities.Proyecto;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by rsperoni on 28/10/17.
 */
@Stateless
public class ProyectoDao extends AbstractDao<Proyecto, Long> {

    @PersistenceContext
    EntityManager em;

    @Override
    public Class<Proyecto> getEntityClass() {
        return Proyecto.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}

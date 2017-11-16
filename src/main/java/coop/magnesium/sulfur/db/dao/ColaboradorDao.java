package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.db.entities.Colaborador;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by rsperoni on 28/10/17.
 */
@Stateless
public class ColaboradorDao extends AbstractDao<Colaborador, String> {

    @PersistenceContext
    EntityManager em;

    @Override
    public Class<Colaborador> getEntityClass() {
        return Colaborador.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}

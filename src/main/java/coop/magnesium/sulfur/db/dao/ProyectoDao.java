package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.db.entities.Proyecto;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

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

    public List<Proyecto> findAllByPrioridad() {
        CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root entity = criteriaQuery.from(Proyecto.class);
        criteriaQuery.select(entity);
        criteriaQuery.orderBy(criteriaBuilder.desc(entity.get("prioridad")));
        Query query = this.getEntityManager().createQuery(criteriaQuery);
        return (List<Proyecto>) query.getResultList();
    }
}

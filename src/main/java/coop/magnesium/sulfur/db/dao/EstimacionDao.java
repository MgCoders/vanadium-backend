package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.db.entities.Estimacion;
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
public class EstimacionDao extends AbstractDao<Estimacion, Long> {

    @PersistenceContext
    EntityManager em;

    @Override
    public Class<Estimacion> getEntityClass() {
        return Estimacion.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Estimacion> findAllByProyecto(Proyecto proyecto) {
        CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root entity = criteriaQuery.from(Estimacion.class);
        criteriaQuery.select(entity);
        criteriaQuery.where(criteriaBuilder.equal(entity.get("proyecto"), criteriaBuilder.parameter(Proyecto.class, "p")));
        Query query = this.getEntityManager().createQuery(criteriaQuery);
        query.setParameter("p", proyecto);
        return (List<Estimacion>) query.getResultList();
    }
}

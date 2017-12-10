package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.db.entities.Hora;

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

    public List<Hora> findAllByColaborador(Colaborador colaborador) {
        CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root entity = criteriaQuery.from(Hora.class);
        criteriaQuery.select(entity);
        criteriaQuery.where(criteriaBuilder.equal(entity.get("colaborador"), criteriaBuilder.parameter(Colaborador.class, "c")));
        Query query = this.getEntityManager().createQuery(criteriaQuery);
        query.setParameter("c", colaborador);
        return (List<Hora>) query.getResultList();
    }


}

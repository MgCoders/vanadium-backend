package coop.magnesium.vanadium.db.dao;

import coop.magnesium.vanadium.db.entities.Cargo;
import coop.magnesium.vanadium.db.entities.Colaborador;
import coop.magnesium.vanadium.db.entities.Hora;
import coop.magnesium.vanadium.utils.ex.MagnesiumBdMultipleResultsException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by rsperoni on 28/10/17.
 */
@Stateless
public class CargoDao extends AbstractDao<Cargo, Long> {

    @PersistenceContext
    EntityManager em;

    @Override
    public Class<Cargo> getEntityClass() {
        return Cargo.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Cargo findByCodigo(String codigo) throws MagnesiumBdMultipleResultsException {
        CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root entity = criteriaQuery.from(Cargo.class);
        criteriaQuery.select(entity);
        Predicate codigoIsOk = criteriaBuilder.equal(entity.get("codigo"), criteriaBuilder.parameter(String.class, "c"));
        criteriaQuery.where(codigoIsOk);
        Query query = this.getEntityManager().createQuery(criteriaQuery);
        query.setParameter("c", codigo);
        List<Cargo> result = query.getResultList();
        if (result.size() == 0) return null;
        if (result.size() > 1)
            throw new MagnesiumBdMultipleResultsException(Hora.class.getSimpleName() + "multiples resultados encontrados");
        return result.get(0);
    }
}

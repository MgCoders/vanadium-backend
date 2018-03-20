package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdMultipleResultsException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by rsperoni on 28/10/17.
 */
@Stateless
public class ColaboradorDao extends AbstractDao<Colaborador, Long> {

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

    public Colaborador findByEmail(String email) throws MagnesiumBdMultipleResultsException {
        List<Colaborador> result = findByField("email", email);
        if (result.size() == 0) return null;
        if (result.size() > 1)
            throw new MagnesiumBdMultipleResultsException(Colaborador.class.getSimpleName() + "multiples resultados encontrados");
        return result.get(0);
    }

    public List<Colaborador> findAllByCargo(Cargo cargo) {
        CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root entity = criteriaQuery.from(Colaborador.class);
        criteriaQuery.select(entity);
        Predicate cargoIsOk = criteriaBuilder.equal(entity.get("cargo"), criteriaBuilder.parameter(Cargo.class, "c"));
        criteriaQuery.where(cargoIsOk);
        Query query = this.getEntityManager().createQuery(criteriaQuery);
        query.setParameter("c", cargo);
        return (List<Colaborador>) query.getResultList();
    }

    public List<Colaborador> findAllAdmins() {
        Query query = getEntityManager().createQuery(
                "select e from Colaborador e where e.role like 'ADMIN'");
        return (List<Colaborador>) query.getResultList();
    }
}

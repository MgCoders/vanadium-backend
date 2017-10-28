package coop.magnesium.sulfur.db.dao;


import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class AbstractDao<E, ID extends Serializable> {


    public abstract Class<E> getEntityClass();

    public abstract EntityManager getEntityManager();

    public E findById(ID id) {
        return getEntityManager().find(getEntityClass(), id);
    }

    public List<E> findAll() {
        Query query = getEntityManager().createQuery(
                "select e from " + getEntityClass().getSimpleName() + " e");
        return (List<E>) query.getResultList();
    }

    public List<E> findByNamedQuery(final String jpql, Object... params) {
        Query query = getEntityManager().createQuery(jpql);
        int index = 1;
        for (Object param : params) {
            query.setParameter(index, param);
            index++;
        }
        return (List<E>) query.getResultList();
    }

    public List<E> findByNamedQuery(final String jpql, Map<String, ? extends Object> params) {
        Query query = getEntityManager().createQuery(jpql);
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return (List<E>) query.getResultList();
    }

    public int countAll() {
        Query query = getEntityManager().createQuery(
                "select count(e) from " + getEntityClass().getSimpleName() + " e");
        return (Integer) query.getSingleResult();
    }

    public E save(E entity) {
        final E savedEntity = getEntityManager().merge(entity);
        return savedEntity;
    }

    public void delete(E entity) {
        getEntityManager().remove(entity);
    }

    public void delete(ID id) {
        E entity = findById(id);
        if (entity != null) {
            getEntityManager().remove(entity);
        }
    }
}
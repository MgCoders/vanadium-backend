package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.api.dto.HorasProyectoTipoTareaXCargo;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.db.entities.Hora;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;

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

    /**
     * Devuelva horas de colaborador entre fechas ini y fin.
     *
     * @param colaborador
     * @param ini
     * @param fin
     * @return
     */
    public List<Hora> findAllByColaborador(Colaborador colaborador, LocalDate ini, LocalDate fin) {
        CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root entity = criteriaQuery.from(Hora.class);
        criteriaQuery.select(entity);
        Predicate colaboradorIsOk = criteriaBuilder.equal(entity.get("colaborador"), criteriaBuilder.parameter(Colaborador.class, "c"));
        Predicate diaEntreFechas = criteriaBuilder.between(entity.get("dia"), criteriaBuilder.parameter(LocalDate.class, "ini"), criteriaBuilder.parameter(LocalDate.class, "fin"));
        criteriaQuery.where(criteriaBuilder.and(colaboradorIsOk, diaEntreFechas));
        criteriaQuery.orderBy(criteriaBuilder.asc(entity.get("dia")));
        Query query = this.getEntityManager().createQuery(criteriaQuery);
        query.setParameter("c", colaborador);
        query.setParameter("ini", ini);
        query.setParameter("fin",fin);
        return (List<Hora>) query.getResultList();
    }

    public List<HorasProyectoTipoTareaXCargo> findHorasXCargo(Proyecto proyecto, TipoTarea tipoTarea) {
        Query query = em.createQuery("" +
                "select new coop.magnesium.sulfur.api.dto.HorasProyectoTipoTareaXCargo(sum(hd.duracion),hd.proyecto,hd.tipoTarea,h.colaborador.cargo) " +
                "from Hora h JOIN h.horaDetalleList hd " +
                "where hd.proyecto = :proyecto and hd.tipoTarea = :tipoTarea " +
                "group by hd.proyecto,hd.tipoTarea,h.colaborador.cargo");
        query.setParameter("proyecto", proyecto);
        query.setParameter("tipoTarea", tipoTarea);
        return query.getResultList();
    }


}

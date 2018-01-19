package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.api.dto.HoraCompleta;
import coop.magnesium.sulfur.api.dto.HorasProyectoTipoTareaCargoXColaborador;
import coop.magnesium.sulfur.api.dto.HorasProyectoXCargo;
import coop.magnesium.sulfur.db.entities.*;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdMultipleResultsException;

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

    public Hora findByColaboradorFecha(Colaborador colaborador, LocalDate dia) throws MagnesiumBdMultipleResultsException {
        CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root entity = criteriaQuery.from(Hora.class);
        criteriaQuery.select(entity);
        Predicate colaboradorIsOk = criteriaBuilder.equal(entity.get("colaborador"), criteriaBuilder.parameter(Colaborador.class, "c"));
        Predicate fechaIsOk = criteriaBuilder.equal(entity.get("dia"), criteriaBuilder.parameter(LocalDate.class, "dia"));
        criteriaQuery.where(criteriaBuilder.and(colaboradorIsOk, fechaIsOk));
        Query query = this.getEntityManager().createQuery(criteriaQuery);
        query.setParameter("c", colaborador);
        query.setParameter("dia", dia);
        List<Hora> result = query.getResultList();
        if (result.size() == 0) return null;
        if (result.size() > 1)
            throw new MagnesiumBdMultipleResultsException(Hora.class.getSimpleName() + "multiples resultados encontrados");
        return result.get(0);
    }

    public boolean existsByColaboradorIncompleta(Colaborador colaborador) {
        CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root entity = criteriaQuery.from(Hora.class);
        criteriaQuery.select(entity);
        Predicate colaboradorIsOk = criteriaBuilder.equal(entity.get("colaborador"), criteriaBuilder.parameter(Colaborador.class, "c"));
        Predicate completaOk = criteriaBuilder.equal(entity.get("completa"), false);
        criteriaQuery.where(criteriaBuilder.and(colaboradorIsOk, completaOk));
        Query query = this.getEntityManager().createQuery(criteriaQuery);
        query.setParameter("c", colaborador);
        List<Hora> result = query.getResultList();
        return result.size() > 0;
    }

    public List<HorasProyectoTipoTareaCargoXColaborador> findHorasProyectoTipoTareaCargoXColaborador(Proyecto proyecto, TipoTarea tipoTarea, Cargo cargo) {
        Query query = em.createQuery("" +
                "select new coop.magnesium.sulfur.api.dto.HorasProyectoTipoTareaCargoXColaborador(sum(hd.duracion),hd.proyecto,hd.tipoTarea,h.colaborador.cargo,h.colaborador) " +
                "from Hora h JOIN h.horaDetalleList hd " +
                "where hd.proyecto = :proyecto and hd.tipoTarea = :tipoTarea and h.colaborador.cargo = :cargo " +
                "group by hd.proyecto,hd.tipoTarea,h.colaborador.cargo,h.colaborador");
        query.setParameter("proyecto", proyecto);
        query.setParameter("tipoTarea", tipoTarea);
        query.setParameter("cargo", cargo);
        return query.getResultList();
    }

    public List<Hora> findHdProyectoTipoTarea(Proyecto proyecto, TipoTarea tipoTarea) {
        Query query = em.createQuery("" +
                "select h " +
                "from Hora h ");
        //query.setParameter("proyecto", proyecto);
        //query.setParameter("tipoTarea", tipoTarea);
        return query.getResultList();
    }

    public List<Hora> findHorasProyectoTipoTarea(Proyecto proyecto, TipoTarea tipoTarea) {
        Query query = em.createQuery("" +
                "select distinct h " +
                "from Hora h join h.horaDetalleList hd " +
                "where hd.proyecto = :proyecto and hd.tipoTarea = :tipoTarea ");
        query.setParameter("proyecto", proyecto);
        query.setParameter("tipoTarea", tipoTarea);
        return query.getResultList();
    }

    public List<HoraCompleta> findHorasProyectoTipoTareaXCargoDia(Proyecto proyecto, TipoTarea tipoTarea) {
        Query query = em.createNativeQuery("" +
                "select hd.proyecto,hd.tipoTarea_id,sum(hd.duracion) duracion,h.dia,h.colaborador_id,ca.cargo_id) " +
                "from hora as h, horaDetalle as hd, Cargo as ca, Colaborador as co " +
                "where hd.proyecto_id = :proyecto and hd.tipoTarea_id = :tipoTarea and hd.hora_id = h.id and ca.id = h.colaborador_id and co.id = ca.cargo_id" +
                "group by hd.proyecto,hd.tipoTarea,h.dia,h.colaborador,h.colaborador.cargo");
        query.setParameter("proyecto", proyecto.getId());
        query.setParameter("tipoTarea", tipoTarea.getId());
        return query.getResultList();
    }

    public List<HoraDetalle> prueba(Proyecto proyecto, TipoTarea tipoTarea) {
        Query query = em.createQuery("" +
                "select new coop.magnesium.sulfur.db.entities.HoraDetalle(hd.proyecto,hd.tipoTarea,hd.duracion) " +
                "from Hora h JOIN h.horaDetalleList hd " +
                "where hd.proyecto = :proyecto and hd.tipoTarea = :tipoTarea ");
        query.setParameter("proyecto", proyecto);
        query.setParameter("tipoTarea", tipoTarea);
        return query.getResultList();
    }

    public List<HorasProyectoXCargo> findHorasProyectoXCargo(Proyecto proyecto) {
        Query query = em.createQuery("" +
                "select new coop.magnesium.sulfur.api.dto.HorasProyectoXCargo(sum(hd.duracion),hd.proyecto,h.colaborador.cargo) " +
                "from Hora h JOIN h.horaDetalleList hd " +
                "where hd.proyecto = :proyecto " +
                "group by hd.proyecto,h.colaborador.cargo");
        query.setParameter("proyecto", proyecto);
        return query.getResultList();
    }


}

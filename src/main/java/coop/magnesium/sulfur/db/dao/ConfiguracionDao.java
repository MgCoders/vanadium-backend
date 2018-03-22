package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.db.entities.Configuracion;
import coop.magnesium.sulfur.db.entities.TipoConfiguracion;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by rsperoni on 28/10/17.
 */
@Stateless
public class ConfiguracionDao extends AbstractDao<Configuracion, Long> {

    @PersistenceContext
    EntityManager em;

    @Inject
    Logger logger;

    @Override
    public Class<Configuracion> getEntityClass() {
        return Configuracion.class;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }


    public List<Configuracion> findAllByClave(TipoConfiguracion clave) {
        CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root entity = criteriaQuery.from(Configuracion.class);
        criteriaQuery.select(entity);
        Predicate claveIsOk = criteriaBuilder.equal(entity.get("clave"), criteriaBuilder.parameter(TipoConfiguracion.class, "c"));
        criteriaQuery.where(claveIsOk);
        Query query = this.getEntityManager().createQuery(criteriaQuery);
        query.setParameter("c", clave);
        return (List<Configuracion>) query.getResultList();
    }

    public boolean isEmailOn() {
        List<Configuracion> configuracionList = findAllByClave(TipoConfiguracion.NOTIFICACION_MAIL_ACTIVADO);
        return !configuracionList.isEmpty() && (Boolean.getBoolean(configuracionList.get(0).getValor()));
    }

    public List<String> getDestinatariosNotificacionesAdmins() {
        return findAllByClave(TipoConfiguracion.NOTIFICACION_ADMIN_DESTINATARIO).stream().map(Configuracion::getValor).collect(Collectors.toList());
    }

    public Long getPeriodicidadNotificaciones() {
        List<Configuracion> configuracionList = findAllByClave(TipoConfiguracion.NOTIFICACION_PERIODICIDAD);
        if (!configuracionList.isEmpty()) {
            //logger.info(configuracionList.get(0).getValor());
            return new Long(configuracionList.get(0).getValor());
        } else {
            return 0L;
        }
    }

    public String getNodoMaster() {
        List<Configuracion> configuracionList = findAllByClave(TipoConfiguracion.NODO_MASTER);
        if (!configuracionList.isEmpty()) {
            return configuracionList.get(0).getValor();
        } else {
            return null;
        }
    }

    public void setNodoMaster(String nodoMaster) {
        Optional<Configuracion> configuracion = findAllByClave(TipoConfiguracion.NODO_MASTER).stream().findFirst();
        if (configuracion.isPresent()) {
            configuracion.get().setValor(nodoMaster);
        } else {
            save(new Configuracion(TipoConfiguracion.NODO_MASTER, nodoMaster));
        }
    }
}

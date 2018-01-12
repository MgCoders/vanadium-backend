package coop.magnesium.sulfur.db.dao;

import coop.magnesium.sulfur.api.dto.EstimacionProyectoTipoTareaXCargo;
import coop.magnesium.sulfur.api.dto.ReporteHoras1;
import coop.magnesium.sulfur.db.entities.Cargo;
import coop.magnesium.sulfur.db.entities.Proyecto;
import coop.magnesium.sulfur.db.entities.TipoTarea;
import coop.magnesium.sulfur.utils.TimeUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by rsperoni on 28/10/17.
 */
@Stateless
public class ReportesDao {

    @EJB
    private EstimacionDao estimacionDao;
    @EJB
    private HoraDao horaDao;
    @Inject
    private Logger logger;


    public List<ReporteHoras1> reporteHoras1(Proyecto proyecto, TipoTarea tipoTarea) {

        //Busco las estimaciones
        Map<Cargo, EstimacionProyectoTipoTareaXCargo> estimacionesXCargo = new HashMap<>();
        estimacionDao.findEstimacionProyectoTipoTareaXCargo(proyecto, tipoTarea)
                .forEach(ePTXC -> estimacionesXCargo.put(ePTXC.getCargo(), ePTXC));

        //Aca va el resultado
        Map<Cargo, Set<ReporteHoras1>> reporteXCargo = new HashMap<>();
        //Junto todas las horas Detalle separadas por cargo.
        horaDao.findAll().forEach(hora -> {
            Cargo cargo = hora.getColaborador().getCargo();
            reporteXCargo.computeIfAbsent(cargo, k -> new HashSet<>());
            hora.getHoraDetalleList().forEach(horaDetalle -> {
                if (horaDetalle.getProyecto().equals(proyecto) && horaDetalle.getTipoTarea().equals(tipoTarea)) {
                    BigDecimal costoXHora = cargo.getPrecioHora(hora.getDia()).get().getPrecioHora();
                    BigDecimal cantHoras = TimeUtils.durationToBigDecimal(horaDetalle.getDuracion());
                    logger.info(horaDetalle.getDuracion().toString() + " " + cantHoras.toString());
                    BigDecimal costoHoras = costoXHora.multiply(cantHoras);
                    reporteXCargo.get(cargo).add(new ReporteHoras1(cantHoras, estimacionesXCargo.get(cargo).cantidadHoras, estimacionesXCargo.get(cargo).precioTotal, costoHoras, horaDetalle.getProyecto(), horaDetalle.getTipoTarea(), cargo));
                }
            });
        });

        //Resultados
        List<ReporteHoras1> result = new ArrayList<>();
        reporteXCargo.keySet().forEach(cargo ->
                reporteXCargo.get(cargo).stream().reduce((r1, r2) -> {
                    logger.info(cargo.getId() + " " + r1.cantidadHoras.toString() + " + " + r2.cantidadHoras.toString());
                    return new ReporteHoras1(
                            r1.cantidadHoras.add(r2.cantidadHoras),
                            r1.cantidadHorasEstimadas.add(r2.cantidadHorasEstimadas),
                            r1.precioEstimado.add(r2.precioEstimado),
                            r1.precioTotal.add(r2.precioTotal),
                            r1.proyecto, r1.tipoTarea, r1.cargo);
                }).ifPresent(result::add));

        //Fila total
        result.stream().reduce((r1, r2) -> {
            return new ReporteHoras1(
                    r1.cantidadHoras.add(r2.cantidadHoras),
                    r1.cantidadHorasEstimadas.add(r2.cantidadHorasEstimadas),
                    r1.precioEstimado.add(r2.precioEstimado),
                    r1.precioTotal.add(r2.precioTotal),
                    r1.proyecto, r1.tipoTarea, null);
        }).ifPresent(result::add);
        return result;
    }


}

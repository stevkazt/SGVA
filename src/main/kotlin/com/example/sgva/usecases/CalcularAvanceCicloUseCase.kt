package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.AvanceCiclo
import com.example.sgva.domain.CicloNoEncontradoException
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.ConfiguracionSeguimiento
import com.example.sgva.domain.EstadoActividad
import com.example.sgva.domain.FaseCiclo
import com.example.sgva.domain.ParticipacionConSemaforo
import java.time.LocalDate

/**
 * Módulo 5 — seguimiento del avance de un ciclo (`DESIGN_SGVA.md` §0.1 y §2,
 * V6). Para un [ciclo] y una [fechaReferencia] —recibida siempre como
 * parámetro, nunca leída del reloj del sistema (`DESIGN_SGVA.md` §0.1)—
 * calcula el avance por fase, las actividades vencidas y próximas a vencer,
 * los factores con preevaluación pendiente y la participación de los
 * actores, aplicando exactamente las reglas de `DESIGN_SGVA.md` §0.1.
 */
class CalcularAvanceCicloUseCase(
    private val repositorio: AcademicDataRepository,
    private val configuracionPonderaciones: ConfiguracionPonderaciones,
    private val configuracionSeguimiento: ConfiguracionSeguimiento,
) {

    /** @throws CicloNoEncontradoException si no existe el [ciclo] referenciado. */
    fun ejecutar(ciclo: String, fechaReferencia: LocalDate): AvanceCiclo {
        repositorio.buscarCicloPorId(ciclo) ?: throw CicloNoEncontradoException(ciclo)

        val actividades = repositorio.listarActividadesPorCiclo(ciclo)
        val actividadesPorFase = actividades.groupBy { it.fase }

        val avancePorFase = FaseCiclo.RANGO_NUMERO.associateWith { numero ->
            avanceDeFase(actividadesPorFase[numero].orEmpty())
        }
        val avanceCiclo = avancePorFase.values.filterNotNull().let { presentes ->
            if (presentes.isEmpty()) null else presentes.average()
        }

        val factoresDelGrupo = repositorio.listarGruposEvaluadorPorCiclo(ciclo).flatMap { it.factores }.toSet()
        val factoresConPendiente = factoresDelGrupo.filter { factor ->
            caracteristicasConPreevaluacionPendiente(ciclo, factor)
        }.toSet()

        val participacionConSemaforo = repositorio.listarParticipacionPorCiclo(ciclo).map { participacion ->
            ParticipacionConSemaforo(
                participacion = participacion,
                semaforo = participacion.semaforo(configuracionSeguimiento),
            )
        }

        return AvanceCiclo(
            ciclo = ciclo,
            avancePorFase = avancePorFase,
            avanceCiclo = avanceCiclo,
            actividadesVencidas = actividades.filter { it.estaVencida(fechaReferencia) },
            actividadesProximasAVencer = actividades.filter {
                it.estaProximaAVencer(fechaReferencia, configuracionSeguimiento.diasProximaAVencer)
            },
            factoresConPreevaluacionPendiente = factoresConPendiente,
            participacionActores = participacionConSemaforo,
        )
    }

    /** Regla de §0.1: completadas / total; ausente si la fase no tiene actividades. */
    private fun avanceDeFase(actividadesDeFase: List<ActividadCiclo>): Double? {
        if (actividadesDeFase.isEmpty()) return null
        val completadas = actividadesDeFase.count { it.estado == EstadoActividad.COMPLETADA }
        return completadas.toDouble() / actividadesDeFase.size
    }

    /**
     * `true` si alguna característica del [factor] no tiene ninguna
     * preevaluación registrada en el [ciclo] (`DESIGN_SGVA.md` §0.1).
     */
    private fun caracteristicasConPreevaluacionPendiente(ciclo: String, factor: String): Boolean {
        val caracteristicasDelFactor = configuracionPonderaciones.caracteristicas
            .filter { it.factor == factor }
            .map { it.id }
            .toSet()
        val caracteristicasConPreevaluacion = repositorio.listarPreevaluacionesPorCiclo(ciclo)
            .map { it.caracteristica }
            .toSet()
        return (caracteristicasDelFactor - caracteristicasConPreevaluacion).isNotEmpty()
    }
}

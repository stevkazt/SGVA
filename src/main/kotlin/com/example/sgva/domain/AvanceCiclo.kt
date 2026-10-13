package com.example.sgva.domain

/**
 * Participación de un actor junto con su semáforo ya resuelto contra
 * [ConfiguracionSeguimiento] (Módulo 5, V6). Evita que los consumidores de
 * [AvanceCiclo] (controladores, reporte PDF) necesiten la configuración para
 * mostrar el semáforo.
 */
data class ParticipacionConSemaforo(
    val participacion: ParticipacionActor,
    val semaforo: TipoSemaforo?,
)

/**
 * Resultado de `CalcularAvanceCicloUseCase` para un [ciclo] y una fecha de
 * referencia (Módulo 5, `DESIGN_SGVA.md` §0.1 y §2, V6).
 *
 * @property ciclo identificador del [CicloAutoevaluacion] consultado.
 * @property avancePorFase avance de cada fase (actividades completadas /
 *   total), ausente si la fase no tiene actividades.
 * @property avanceCiclo promedio de los avances de fase presentes, ausente
 *   si ninguna fase tiene avance calculable.
 * @property actividadesVencidas actividades cuyo fin planificado ya pasó sin
 *   estar completadas.
 * @property actividadesProximasAVencer actividades no vencidas que vencen
 *   dentro de la ventana de días configurada.
 * @property factoresConPreevaluacionPendiente factores asignados a un grupo
 *   evaluador del ciclo cuyas características no tienen ninguna preevaluación.
 * @property participacionActores participación de cada actor con su semáforo ya resuelto.
 */
data class AvanceCiclo(
    val ciclo: String,
    val avancePorFase: Map<Int, Double?>,
    val avanceCiclo: Double?,
    val actividadesVencidas: List<ActividadCiclo>,
    val actividadesProximasAVencer: List<ActividadCiclo>,
    val factoresConPreevaluacionPendiente: Set<String>,
    val participacionActores: List<ParticipacionConSemaforo>,
)

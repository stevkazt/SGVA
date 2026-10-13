package com.example.sgva.domain

import java.time.LocalDate

/**
 * Actividad de una fase de un [CicloAutoevaluacion] (Módulo 5,
 * `DESIGN_SGVA.md` §0.1 y §1, V6). No depende de frameworks, bases de datos
 * ni sistemas de archivos.
 *
 * Las actividades se registran libremente en cada ciclo: no existe un
 * catálogo fijo, porque varían entre ediciones (`DESIGN_SGVA.md` §0.1).
 *
 * @property id identificador de la actividad (ej.: `"2.3"`: número de fase,
 *   punto y orden dentro de la fase; no se exige ese formato exacto).
 * @property ciclo identificador del [CicloAutoevaluacion] al que pertenece.
 * @property fase número de la fase a la que pertenece, de 1 a 4 inclusive.
 * @property descripcion descripción de la actividad.
 * @property responsable rol responsable de la actividad (ej.: "Comité de
 *   aseguramiento de la calidad"), nunca una persona.
 * @property fechaInicioPlanificada fecha de inicio planificada; no puede ser
 *   posterior a [fechaFinPlanificada].
 * @property fechaFinPlanificada fecha de fin planificada.
 * @property estado estado actual de la actividad.
 */
data class ActividadCiclo(
    val id: String,
    val ciclo: String,
    val fase: Int,
    val descripcion: String,
    val responsable: String,
    val fechaInicioPlanificada: LocalDate,
    val fechaFinPlanificada: LocalDate,
    val estado: EstadoActividad,
) {
    init {
        ReglasDeFormato.exigirNoVacio(id, "id")
        ReglasDeFormato.exigirNoVacio(ciclo, "ciclo")
        if (fase !in FaseCiclo.RANGO_NUMERO) {
            throw FaseInvalidaException(fase)
        }
        ReglasDeFormato.exigirNoVacio(descripcion, "descripcion")
        ReglasDeFormato.exigirNoVacio(responsable, "responsable")
        if (fechaInicioPlanificada.isAfter(fechaFinPlanificada)) {
            throw FechasActividadInvalidasException(fechaInicioPlanificada, fechaFinPlanificada)
        }
    }

    /**
     * `true` si la actividad está vencida frente a [fechaReferencia]: su fin
     * planificado ya pasó y no está completada (`DESIGN_SGVA.md` §0.1).
     */
    fun estaVencida(fechaReferencia: LocalDate): Boolean =
        estado != EstadoActividad.COMPLETADA && fechaFinPlanificada.isBefore(fechaReferencia)

    /**
     * `true` si la actividad está próxima a vencer frente a [fechaReferencia]
     * dentro de los próximos [dias] días: no está completada, no está
     * vencida y su fin planificado cae en `[fechaReferencia, fechaReferencia
     * + dias]` (`DESIGN_SGVA.md` §0.1).
     */
    fun estaProximaAVencer(fechaReferencia: LocalDate, dias: Int): Boolean =
        estado != EstadoActividad.COMPLETADA &&
            !estaVencida(fechaReferencia) &&
            !fechaFinPlanificada.isBefore(fechaReferencia) &&
            !fechaFinPlanificada.isAfter(fechaReferencia.plusDays(dias.toLong()))
}

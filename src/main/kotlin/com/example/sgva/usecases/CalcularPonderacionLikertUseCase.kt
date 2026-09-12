package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.domain.PonderacionLikert

/**
 * Módulo 3 — ponderación Likert de las encuestas de percepción
 * (`DESIGN_SGVA.md` §2).
 *
 * Promedia la `calificacion` de las respuestas registradas agrupándolas por
 * `factor` y `estamento`, opcionalmente restringidas a un periodo. Las
 * respuestas de encuesta no tienen `facultad` (Sección 0 de `DESIGN_SGVA.md`);
 * a diferencia de [CalculoDeIndicadorUseCase], este caso de uso no ofrece ese
 * filtro.
 */
class CalcularPonderacionLikertUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * @throws DatosInsuficientesException si no hay respuestas registradas para el filtro dado.
     */
    fun ejecutar(periodo: String? = null): List<PonderacionLikert> {
        val filtroPeriodo = periodo?.trim()?.takeIf { it.isNotEmpty() }
        val respuestas = repositorio.listarRespuestasEncuesta()
            .filter { filtroPeriodo == null || it.periodo == filtroPeriodo }

        if (respuestas.isEmpty()) {
            throw DatosInsuficientesException(
                "No hay respuestas de encuesta registradas para calcular la ponderación Likert" +
                    (filtroPeriodo?.let { " (periodo '$it')" } ?: "") + ".",
            )
        }

        val etiquetaPeriodo = filtroPeriodo ?: IndicadorCalidad.CONSOLIDADO

        return respuestas
            .groupBy { it.factor to it.estamento }
            .toSortedMap(compareBy({ it.first }, { it.second }))
            .map { (clave, grupo) ->
                PonderacionLikert(
                    factor = clave.first,
                    estamento = clave.second,
                    promedio = redondearACentesimas(grupo.map { it.calificacion }.average()),
                    periodo = etiquetaPeriodo,
                )
            }
    }

    private fun redondearACentesimas(valor: Double): Double = Math.round(valor * 100.0) / 100.0
}

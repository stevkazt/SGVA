package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.IndicadorCalidad

/**
 * Módulo 1 — consolidación de puntajes Saber Pro (`DESIGN_SGVA.md` §2).
 *
 * Calcula el promedio global y el promedio por cohorte de los puntajes Saber Pro
 * efectivamente registrados. Los estudiantes con `puntajeSaberPro == null` (aún
 * no han presentado la prueba) se excluyen del promedio, nunca se cuentan como 0
 * (`DESIGN_SGVA.md` §0.1).
 *
 * La comparación contra la referencia nacional queda **fuera del alcance de esta
 * versión** (`DESIGN_SGVA.md` §0): no hay fuente definida para ese dato.
 *
 * Devuelve el promedio global primero (periodo [IndicadorCalidad.CONSOLIDADO]) y
 * a continuación un indicador por cohorte con puntajes, en orden ascendente.
 */
class ConsolidarPuntajesSaberProUseCase(
    repositorio: AcademicDataRepository,
) : CalculoDeIndicadorUseCase(repositorio) {

    /**
     * @throws DatosInsuficientesException si no hay ningún puntaje Saber Pro registrado.
     */
    fun ejecutar(facultad: String? = null): List<IndicadorCalidad> {
        val puntajesPorCohorte = estudiantesRegistrados(facultad)
            .mapNotNull { estudiante -> estudiante.puntajeSaberPro?.let { estudiante.cohorte to it } }

        if (puntajesPorCohorte.isEmpty()) {
            throw DatosInsuficientesException(
                "No hay puntajes Saber Pro registrados" +
                    (facultad?.let { " para la facultad '${it.trim()}'" } ?: "") + ".",
            )
        }

        val etiqueta = etiquetaFacultad(facultad)
        val promedioGlobal = IndicadorCalidad(
            nombre = IndicadorCalidad.PROMEDIO_SABER_PRO,
            valor = redondearACentesimas(puntajesPorCohorte.map { it.second }.average()),
            periodo = IndicadorCalidad.CONSOLIDADO,
            facultad = etiqueta,
        )

        val promediosPorCohorte = puntajesPorCohorte
            .groupBy({ it.first }, { it.second })
            .toSortedMap()
            .map { (cohorte, puntajes) ->
                IndicadorCalidad(
                    nombre = IndicadorCalidad.PROMEDIO_SABER_PRO,
                    valor = redondearACentesimas(puntajes.average()),
                    periodo = cohorte,
                    facultad = etiqueta,
                )
            }

        return listOf(promedioGlobal) + promediosPorCohorte
    }
}

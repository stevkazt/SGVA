package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.domain.NivelFormacion

/**
 * Módulo 2 — distribución de la formación docente (`DESIGN_SGVA.md` §2).
 *
 * Agrupa los docentes por [NivelFormacion] y calcula el porcentaje que
 * representa cada nivel sobre el total, opcionalmente restringido a un periodo
 * y/o una facultad. Devuelve un [IndicadorCalidad] por cada nivel de formación
 * (incluidos los de porcentaje `0`), en el orden del enum.
 */
class CalcularDistribucionFormacionUseCase(
    repositorio: AcademicDataRepository,
) : CalculoDeIndicadorUseCase(repositorio) {

    /**
     * @throws DatosInsuficientesException si no hay docentes registrados para el filtro dado.
     */
    fun ejecutar(periodo: String? = null, facultad: String? = null): List<IndicadorCalidad> {
        val docentes = docentesRegistrados(facultad, periodo)
        if (docentes.isEmpty()) {
            throw DatosInsuficientesException(
                "No hay docentes registrados para calcular la distribución de formación" +
                    descripcionFiltro(periodo, facultad) + ".",
            )
        }

        val conteoPorNivel = docentes.groupBy { it.nivelFormacion }.mapValues { it.value.size }
        val total = docentes.size
        val etiquetaPeriodo = etiquetaPeriodo(periodo)
        val etiquetaFacultad = etiquetaFacultad(facultad)

        return NivelFormacion.entries.map { nivel ->
            IndicadorCalidad(
                nombre = IndicadorCalidad.distribucionFormacionPara(nivel.name),
                valor = redondearACentesimas(100.0 * (conteoPorNivel[nivel] ?: 0) / total),
                periodo = etiquetaPeriodo,
                facultad = etiquetaFacultad,
            )
        }
    }
}

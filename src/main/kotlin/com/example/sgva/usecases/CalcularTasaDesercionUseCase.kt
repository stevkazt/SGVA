package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.IndicadorCalidad

/**
 * Módulo 1 — tasa de deserción (`DESIGN_SGVA.md` §2).
 *
 * Calcula el porcentaje de estudiantes en estado [EstadoEstudiante.DESERTOR]
 * frente al total de la cohorte seleccionada (todos los estados: matriculados,
 * graduados y desertores), opcionalmente restringido a una facultad.
 */
class CalcularTasaDesercionUseCase(
    repositorio: AcademicDataRepository,
) : CalculoDeIndicadorUseCase(repositorio) {

    /**
     * @param cohorte cohorte o periodo `AAAAS` a analizar.
     * @throws DatosInsuficientesException si no hay estudiantes en esa cohorte (el porcentaje sería indefinido).
     */
    fun ejecutar(cohorte: String, facultad: String? = null): IndicadorCalidad {
        val cohorteBuscada = cohorte.trim()
        val poblacion = estudiantesRegistrados(facultad).filter { it.cohorte == cohorteBuscada }

        if (poblacion.isEmpty()) {
            throw DatosInsuficientesException(
                "No hay estudiantes registrados para la cohorte '$cohorteBuscada'" +
                    (facultad?.let { " en la facultad '${it.trim()}'" } ?: "") + ".",
            )
        }

        val desertores = poblacion.count { it.estado == EstadoEstudiante.DESERTOR }
        return IndicadorCalidad(
            nombre = IndicadorCalidad.TASA_DESERCION,
            valor = redondearACentesimas(100.0 * desertores / poblacion.size),
            periodo = cohorteBuscada,
            facultad = etiquetaFacultad(facultad),
        )
    }
}

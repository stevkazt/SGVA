package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.DetalleCiclo

/**
 * Consulta los ciclos de autoevaluación registrados, con sus fases y
 * actividades (Módulo 5, V6). No modifica el estado del repositorio.
 */
class ConsultarCicloAutoevaluacionUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** Devuelve todos los ciclos registrados, en orden de inserción. */
    fun listarTodos(): List<CicloAutoevaluacion> = repositorio.listarCiclos()

    /** Devuelve el detalle del ciclo con [id] (fases y actividades), o `null` si no existe. */
    fun buscarDetallePorId(id: String): DetalleCiclo? {
        val ciclo = repositorio.buscarCicloPorId(id) ?: return null
        return DetalleCiclo(
            ciclo = ciclo,
            fases = repositorio.listarFasesPorCiclo(id),
            actividades = repositorio.listarActividadesPorCiclo(id),
        )
    }

    /** Devuelve el ciclo cuya clave natural es ([programa], [periodo]), o `null` si no hay ninguno registrado. */
    fun buscarPorProgramaYPeriodo(programa: String, periodo: String): CicloAutoevaluacion? =
        repositorio.buscarCicloPorProgramaYPeriodo(programa, periodo)

    /**
     * Devuelve el ciclo inmediatamente anterior del [programa] al [periodo]
     * dado —el de mayor periodo estrictamente anterior—, o `null` si no hay
     * ninguno registrado. El periodo tiene formato `AAAAS`: al tener ancho
     * fijo, el orden lexicográfico de cadenas coincide con el orden cronológico.
     */
    fun buscarCicloAnterior(programa: String, periodo: String): CicloAutoevaluacion? =
        repositorio.listarCiclos()
            .filter { it.programa == programa && it.periodo < periodo }
            .maxByOrNull { it.periodo }
}

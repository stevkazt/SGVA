package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.CicloNoEncontradoException
import com.example.sgva.domain.EvaluacionDocumental

/**
 * Consolida las preevaluaciones de cada característica de un ciclo en una
 * [EvaluacionDocumental] (Módulo 5, `DESIGN_SGVA.md` §0.1 "Consolidación" y
 * §2, V6): la calificación consolidada es el promedio simple de las
 * calificaciones de sus preevaluaciones, sin redondear. Si no hay
 * preevaluaciones de una característica, no se genera evaluación documental
 * para ella (ausente, nunca `0`).
 *
 * Al guardar directamente mediante el repositorio —y no mediante
 * `RegistrarEvaluacionDocumentalUseCase`— una consolidación repetida
 * sobrescribe la evaluación documental anterior en lugar de rechazarse como
 * duplicado, para poder recalcularla cuando se agregan nuevas
 * preevaluaciones. No altera el comportamiento de
 * `CalcularResultadoAutoevaluacionUseCase`, que sigue leyendo
 * `EvaluacionDocumental` de la forma de siempre.
 */
class ConsolidarPreevaluacionesUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * @throws CicloNoEncontradoException si no existe el [ciclo] referenciado.
     */
    fun ejecutar(ciclo: String): List<EvaluacionDocumental> {
        val cicloRegistrado = repositorio.buscarCicloPorId(ciclo) ?: throw CicloNoEncontradoException(ciclo)

        return repositorio.listarPreevaluacionesPorCiclo(ciclo)
            .groupBy { it.caracteristica }
            .map { (caracteristica, preevaluaciones) ->
                val consolidada = EvaluacionDocumental(
                    programa = cicloRegistrado.programa,
                    caracteristica = caracteristica,
                    periodo = cicloRegistrado.periodo,
                    calificacion = preevaluaciones.map { it.calificacion }.average(),
                )
                repositorio.guardarEvaluacionDocumental(consolidada)
            }
    }
}

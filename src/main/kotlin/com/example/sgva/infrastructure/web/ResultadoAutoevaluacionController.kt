package com.example.sgva.infrastructure.web

import com.example.sgva.domain.ResultadoAutoevaluacion
import com.example.sgva.usecases.CalcularResultadoAutoevaluacionUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Adaptador de entrada REST (Primary Adapter) del Módulo 4 (`DESIGN_SGVA.md`
 * §4 y §234): expone el resultado de autoevaluación de un programa y un
 * periodo, con su desglose por factor y por característica. Delega todo el
 * cálculo en [CalcularResultadoAutoevaluacionUseCase]; no reimplementa
 * ninguna de las reglas de `DESIGN_SGVA.md` §0.1.
 *
 * A diferencia de [IndicadorController], `programa` y `periodo` son siempre
 * obligatorios: no existe un resultado de autoevaluación "global" sin un
 * programa y un periodo concretos.
 */
@RestController
@RequestMapping("/api/resultados-autoevaluacion")
class ResultadoAutoevaluacionController(
    private val calcularResultadoAutoevaluacion: CalcularResultadoAutoevaluacionUseCase,
) {

    @GetMapping
    fun obtener(
        @RequestParam programa: String,
        @RequestParam periodo: String,
    ): ResultadoAutoevaluacion = calcularResultadoAutoevaluacion.ejecutar(programa, periodo)
}

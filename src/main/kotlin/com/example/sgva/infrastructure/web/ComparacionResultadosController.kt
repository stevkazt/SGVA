package com.example.sgva.infrastructure.web

import com.example.sgva.domain.ComparacionResultados
import com.example.sgva.usecases.CompararResultadosEntreCiclosUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Adaptador de entrada REST (Primary Adapter) del Módulo 5 (`DESIGN_SGVA.md`
 * §4, V6): expone la comparación del resultado de autoevaluación de un
 * programa entre periodos. Delega toda la lógica a
 * [CompararResultadosEntreCiclosUseCase].
 */
@RestController
@RequestMapping("/api/comparaciones")
class ComparacionResultadosController(
    private val compararResultadosEntreCiclos: CompararResultadosEntreCiclosUseCase,
) {

    @GetMapping
    fun comparar(
        @RequestParam programa: String,
        @RequestParam periodos: List<String>,
    ): ComparacionResultados = compararResultadosEntreCiclos.ejecutar(programa, periodos)
}

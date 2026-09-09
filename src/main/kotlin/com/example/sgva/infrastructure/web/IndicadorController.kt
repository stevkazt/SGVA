package com.example.sgva.infrastructure.web

import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.usecases.CalcularCapacidadInstaladaUseCase
import com.example.sgva.usecases.CalcularDistribucionFormacionUseCase
import com.example.sgva.usecases.CalcularEvolucionMatriculaUseCase
import com.example.sgva.usecases.CalcularTasaDesercionUseCase
import com.example.sgva.usecases.ConsolidarPuntajesSaberProUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Adaptador de entrada REST (Primary Adapter) de los indicadores de calidad de
 * los Módulos 1 y 2 (`DESIGN_SGVA.md` §4), el output que consume el Dashboard.
 * Cada endpoint delega en el caso de uso correspondiente; los filtros de
 * `facultad`/`periodo`/`cohorte` se pasan tal cual llegan como parámetros de
 * consulta opcionales (salvo `cohorte`, obligatoria para la tasa de deserción).
 */
@RestController
@RequestMapping("/api/indicadores")
class IndicadorController(
    private val calcularEvolucionMatricula: CalcularEvolucionMatriculaUseCase,
    private val calcularTasaDesercion: CalcularTasaDesercionUseCase,
    private val consolidarSaberPro: ConsolidarPuntajesSaberProUseCase,
    private val calcularDistribucionFormacion: CalcularDistribucionFormacionUseCase,
    private val calcularCapacidadInstalada: CalcularCapacidadInstaladaUseCase,
) {

    @GetMapping("/evolucion-matricula")
    fun obtenerEvolucionMatricula(@RequestParam facultad: String?): List<IndicadorCalidad> =
        calcularEvolucionMatricula.ejecutar(facultad)

    @GetMapping("/tasa-desercion")
    fun obtenerTasaDesercion(
        @RequestParam cohorte: String,
        @RequestParam facultad: String?,
    ): IndicadorCalidad = calcularTasaDesercion.ejecutar(cohorte, facultad)

    @GetMapping("/saber-pro")
    fun obtenerSaberPro(@RequestParam facultad: String?): List<IndicadorCalidad> =
        consolidarSaberPro.ejecutar(facultad)

    @GetMapping("/distribucion-formacion")
    fun obtenerDistribucionFormacion(
        @RequestParam periodo: String?,
        @RequestParam facultad: String?,
    ): List<IndicadorCalidad> = calcularDistribucionFormacion.ejecutar(periodo, facultad)

    @GetMapping("/capacidad-instalada")
    fun obtenerCapacidadInstalada(
        @RequestParam periodo: String?,
        @RequestParam facultad: String?,
    ): IndicadorCalidad = calcularCapacidadInstalada.ejecutar(periodo, facultad)
}

package com.example.sgva.infrastructure.web

import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.PreevaluacionEvaluador
import com.example.sgva.domain.ResultadoIngesta
import com.example.sgva.usecases.ConsolidarPreevaluacionesUseCase
import com.example.sgva.usecases.ConsultarPreevaluacionUseCase
import com.example.sgva.usecases.ProcesarPreevaluacionesUseCase
import com.example.sgva.usecases.RegistrarPreevaluacionUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * Adaptador de entrada REST (Primary Adapter) del Módulo 5 (`DESIGN_SGVA.md`
 * §4, V6): expone el registro individual, la consulta, la carga masiva y la
 * consolidación de preevaluaciones por evaluador. Delega toda la lógica a
 * los casos de uso.
 */
@RestController
@RequestMapping("/api/preevaluaciones")
class PreevaluacionController(
    private val registrarPreevaluacion: RegistrarPreevaluacionUseCase,
    private val consultarPreevaluacion: ConsultarPreevaluacionUseCase,
    private val procesarPreevaluaciones: ProcesarPreevaluacionesUseCase,
    private val consolidarPreevaluaciones: ConsolidarPreevaluacionesUseCase,
) {

    @PostMapping
    fun registrar(@RequestBody preevaluacion: PreevaluacionEvaluador): ResponseEntity<PreevaluacionEvaluador> {
        val registrada = registrarPreevaluacion.ejecutar(preevaluacion)
        return ResponseEntity.status(201).body(registrada)
    }

    @GetMapping
    fun listar(@RequestParam ciclo: String): List<PreevaluacionEvaluador> =
        consultarPreevaluacion.listarPorCiclo(ciclo)

    @PostMapping("/archivos", consumes = ["multipart/form-data"])
    fun cargarArchivo(@RequestParam("archivo") archivo: MultipartFile): ResponseEntity<ResultadoIngesta> {
        val resultado = procesarPreevaluaciones.ejecutar(archivo.originalFilename ?: archivo.name, archivo.bytes)
        return ResponseEntity.status(201).body(resultado)
    }

    @PostMapping("/{ciclo}/consolidacion")
    fun consolidar(@PathVariable ciclo: String): List<EvaluacionDocumental> = consolidarPreevaluaciones.ejecutar(ciclo)
}

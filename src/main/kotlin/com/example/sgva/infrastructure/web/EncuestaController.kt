package com.example.sgva.infrastructure.web

import com.example.sgva.domain.MatrizRadar
import com.example.sgva.domain.PonderacionLikert
import com.example.sgva.domain.ResultadoIngesta
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.usecases.CalcularPonderacionLikertUseCase
import com.example.sgva.usecases.ConsultarRespuestasEncuestaUseCase
import com.example.sgva.usecases.GenerarMatrizRadarUseCase
import com.example.sgva.usecases.ProcesarRespuestasEncuestasUseCase
import com.example.sgva.usecases.RegistrarRespuestaEncuestaUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI

/**
 * Adaptador de entrada REST (Primary Adapter) del Módulo 3 (`DESIGN_SGVA.md`
 * §4): expone la carga masiva desde archivo, el registro individual y la
 * consulta de respuestas de encuesta, junto con los indicadores de percepción
 * calculados (ponderación Likert y matriz radar) que consume el Dashboard.
 * Delega toda la lógica a los casos de uso; no reimplementa reglas de negocio.
 */
@RestController
@RequestMapping("/api/encuestas")
class EncuestaController(
    private val registrarRespuestaEncuesta: RegistrarRespuestaEncuestaUseCase,
    private val consultarRespuestasEncuesta: ConsultarRespuestasEncuestaUseCase,
    private val procesarRespuestasEncuestas: ProcesarRespuestasEncuestasUseCase,
    private val calcularPonderacionLikert: CalcularPonderacionLikertUseCase,
    private val generarMatrizRadar: GenerarMatrizRadarUseCase,
) {

    @PostMapping
    fun registrar(@RequestBody respuesta: RespuestaEncuesta): ResponseEntity<RespuestaEncuesta> {
        val registrada = registrarRespuestaEncuesta.ejecutar(respuesta)
        return ResponseEntity.created(URI.create("/api/encuestas/${registrada.id}")).body(registrada)
    }

    @GetMapping
    fun listar(): List<RespuestaEncuesta> = consultarRespuestasEncuesta.listarTodas()

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: String): ResponseEntity<RespuestaEncuesta> =
        consultarRespuestasEncuesta.buscarPorId(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping("/archivos", consumes = ["multipart/form-data"])
    fun cargarArchivo(@RequestParam("archivo") archivo: MultipartFile): ResponseEntity<ResultadoIngesta> {
        val resultado = procesarRespuestasEncuestas.ejecutar(
            archivo.originalFilename ?: archivo.name,
            archivo.bytes,
        )
        return ResponseEntity.status(201).body(resultado)
    }

    @GetMapping("/ponderacion-likert")
    fun obtenerPonderacionLikert(@RequestParam periodo: String?): List<PonderacionLikert> =
        calcularPonderacionLikert.ejecutar(periodo)

    @GetMapping("/matriz-radar")
    fun obtenerMatrizRadar(@RequestParam periodo: String?): MatrizRadar =
        generarMatrizRadar.ejecutar(periodo)
}

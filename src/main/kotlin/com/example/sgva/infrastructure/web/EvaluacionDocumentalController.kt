package com.example.sgva.infrastructure.web

import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.ResultadoIngesta
import com.example.sgva.usecases.ConsultarEvaluacionDocumentalUseCase
import com.example.sgva.usecases.ProcesarEvaluacionesDocumentalesUseCase
import com.example.sgva.usecases.RegistrarEvaluacionDocumentalUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * Adaptador de entrada REST (Primary Adapter) del Módulo 4 (`DESIGN_SGVA.md`
 * §4): expone el registro individual, la consulta y la carga masiva desde
 * archivo de evaluaciones documentales. Delega toda la lógica a los casos de
 * uso; no reimplementa reglas de negocio.
 *
 * A diferencia de [DocenteController] o [EncuestaController], no expone un
 * `GET /{id}`: la entidad no tiene un `id` propio, su clave natural es la
 * combinación de `programa`, `periodo` y `caracteristica` (`DESIGN_SGVA.md` §1).
 */
@RestController
@RequestMapping("/api/evaluaciones-documentales")
class EvaluacionDocumentalController(
    private val registrarEvaluacionDocumental: RegistrarEvaluacionDocumentalUseCase,
    private val consultarEvaluacionDocumental: ConsultarEvaluacionDocumentalUseCase,
    private val procesarEvaluacionesDocumentales: ProcesarEvaluacionesDocumentalesUseCase,
) {

    @PostMapping
    fun registrar(@RequestBody evaluacion: EvaluacionDocumental): ResponseEntity<EvaluacionDocumental> {
        val registrada = registrarEvaluacionDocumental.ejecutar(evaluacion)
        return ResponseEntity.status(201).body(registrada)
    }

    @GetMapping
    fun listar(
        @RequestParam programa: String?,
        @RequestParam periodo: String?,
    ): List<EvaluacionDocumental> =
        consultarEvaluacionDocumental.listarTodas()
            .filter { programa == null || it.programa == programa }
            .filter { periodo == null || it.periodo == periodo }

    @PostMapping("/archivos", consumes = ["multipart/form-data"])
    fun cargarArchivo(@RequestParam("archivo") archivo: MultipartFile): ResponseEntity<ResultadoIngesta> {
        val resultado = procesarEvaluacionesDocumentales.ejecutar(
            archivo.originalFilename ?: archivo.name,
            archivo.bytes,
        )
        return ResponseEntity.status(201).body(resultado)
    }
}

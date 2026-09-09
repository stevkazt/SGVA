package com.example.sgva.infrastructure.web

import com.example.sgva.domain.Docente
import com.example.sgva.domain.ResultadoIngesta
import com.example.sgva.usecases.ConsultarDocentesUseCase
import com.example.sgva.usecases.ProcesarDatosDocentesUseCase
import com.example.sgva.usecases.RegistrarDocenteUseCase
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
 * Adaptador de entrada REST (Primary Adapter) del Factor 3 (`DESIGN_SGVA.md`
 * §4): expone la carga masiva desde archivo, el registro individual y la
 * consulta de docentes. Delega toda la lógica a los casos de uso; no
 * reimplementa reglas de negocio.
 */
@RestController
@RequestMapping("/api/docentes")
class DocenteController(
    private val registrarDocente: RegistrarDocenteUseCase,
    private val consultarDocentes: ConsultarDocentesUseCase,
    private val procesarArchivoDocentes: ProcesarDatosDocentesUseCase,
) {

    @PostMapping
    fun registrar(@RequestBody docente: Docente): ResponseEntity<Docente> {
        val registrado = registrarDocente.ejecutar(docente)
        return ResponseEntity.created(URI.create("/api/docentes/${registrado.id}")).body(registrado)
    }

    @GetMapping
    fun listar(): List<Docente> = consultarDocentes.listarTodos()

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: String): ResponseEntity<Docente> =
        consultarDocentes.buscarPorId(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping("/archivos", consumes = ["multipart/form-data"])
    fun cargarArchivo(@RequestParam("archivo") archivo: MultipartFile): ResponseEntity<ResultadoIngesta> {
        val resultado = procesarArchivoDocentes.ejecutar(
            archivo.originalFilename ?: archivo.name,
            archivo.bytes,
        )
        return ResponseEntity.status(201).body(resultado)
    }
}

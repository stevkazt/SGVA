package com.example.sgva.infrastructure.web

import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.ResultadoIngesta
import com.example.sgva.usecases.ConsultarEstudiantesUseCase
import com.example.sgva.usecases.ProcesarDatosEstudiantesUseCase
import com.example.sgva.usecases.RegistrarEstudianteUseCase
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
 * Adaptador de entrada REST (Primary Adapter) del Factor 2 (`DESIGN_SGVA.md`
 * §4): expone la carga masiva desde archivo, el registro individual y la
 * consulta de estudiantes. Delega toda la lógica a los casos de uso; no
 * reimplementa reglas de negocio.
 */
@RestController
@RequestMapping("/api/estudiantes")
class EstudianteController(
    private val registrarEstudiante: RegistrarEstudianteUseCase,
    private val consultarEstudiantes: ConsultarEstudiantesUseCase,
    private val procesarArchivoEstudiantes: ProcesarDatosEstudiantesUseCase,
) {

    @PostMapping
    fun registrar(@RequestBody estudiante: Estudiante): ResponseEntity<Estudiante> {
        val registrado = registrarEstudiante.ejecutar(estudiante)
        return ResponseEntity.created(URI.create("/api/estudiantes/${registrado.id}")).body(registrado)
    }

    @GetMapping
    fun listar(): List<Estudiante> = consultarEstudiantes.listarTodos()

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: String): ResponseEntity<Estudiante> =
        consultarEstudiantes.buscarPorId(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping("/archivos", consumes = ["multipart/form-data"])
    fun cargarArchivo(@RequestParam("archivo") archivo: MultipartFile): ResponseEntity<ResultadoIngesta> {
        val resultado = procesarArchivoEstudiantes.ejecutar(
            archivo.originalFilename ?: archivo.name,
            archivo.bytes,
        )
        return ResponseEntity.status(201).body(resultado)
    }
}

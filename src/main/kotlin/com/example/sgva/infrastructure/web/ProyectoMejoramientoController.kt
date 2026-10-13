package com.example.sgva.infrastructure.web

import com.example.sgva.domain.ProyectoMejoramiento
import com.example.sgva.usecases.ConsultarProyectoMejoramientoUseCase
import com.example.sgva.usecases.RegistrarProyectoMejoramientoUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * Adaptador de entrada REST (Primary Adapter) del Módulo 5 (`DESIGN_SGVA.md`
 * §4, V6): expone el registro y la consulta de los proyectos del plan de
 * mejoramiento. Delega toda la lógica a los casos de uso.
 */
@RestController
@RequestMapping("/api/proyectos-mejoramiento")
class ProyectoMejoramientoController(
    private val registrarProyecto: RegistrarProyectoMejoramientoUseCase,
    private val consultarProyecto: ConsultarProyectoMejoramientoUseCase,
) {

    @PostMapping
    fun registrar(@RequestBody proyecto: ProyectoMejoramiento): ResponseEntity<ProyectoMejoramiento> {
        val registrado = registrarProyecto.ejecutar(proyecto)
        return ResponseEntity.created(URI.create("/api/proyectos-mejoramiento/${registrado.id}")).body(registrado)
    }

    @GetMapping
    fun listar(@RequestParam programa: String?): List<ProyectoMejoramiento> =
        consultarProyecto.listarTodos().filter { programa == null || it.programa == programa }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: String): ResponseEntity<ProyectoMejoramiento> =
        consultarProyecto.buscarPorId(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}

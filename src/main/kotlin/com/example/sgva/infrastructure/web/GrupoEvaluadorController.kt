package com.example.sgva.infrastructure.web

import com.example.sgva.domain.GrupoEvaluador
import com.example.sgva.usecases.ConsultarGrupoEvaluadorUseCase
import com.example.sgva.usecases.RegistrarGrupoEvaluadorUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/** Grupo evaluador recibido en el cuerpo de la solicitud, sin el `ciclo` (lo aporta la ruta). */
data class SolicitudGrupoEvaluador(
    val factores: Set<String>,
    val taller: Int,
    val fechaTaller: LocalDate? = null,
) {
    fun aGrupoEvaluador(ciclo: String): GrupoEvaluador =
        GrupoEvaluador(ciclo = ciclo, factores = factores, taller = taller, fechaTaller = fechaTaller)
}

/**
 * Adaptador de entrada REST (Primary Adapter) del Módulo 5 (`DESIGN_SGVA.md`
 * §4, V6): expone el registro y la consulta de grupos evaluadores de un
 * ciclo. Delega toda la lógica a los casos de uso.
 */
@RestController
@RequestMapping("/api/ciclos/{ciclo}/grupos-evaluadores")
class GrupoEvaluadorController(
    private val registrarGrupoEvaluador: RegistrarGrupoEvaluadorUseCase,
    private val consultarGrupoEvaluador: ConsultarGrupoEvaluadorUseCase,
) {

    @PostMapping
    fun registrar(
        @PathVariable ciclo: String,
        @RequestBody solicitud: SolicitudGrupoEvaluador,
    ): ResponseEntity<GrupoEvaluador> {
        val registrado = registrarGrupoEvaluador.ejecutar(solicitud.aGrupoEvaluador(ciclo))
        return ResponseEntity.status(201).body(registrado)
    }

    @GetMapping
    fun listar(@PathVariable ciclo: String): List<GrupoEvaluador> = consultarGrupoEvaluador.listarPorCiclo(ciclo)
}

package com.example.sgva.infrastructure.web

import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.AvanceCiclo
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.DetalleCiclo
import com.example.sgva.domain.EstadoActividad
import com.example.sgva.usecases.CalcularAvanceCicloUseCase
import com.example.sgva.usecases.ConsultarCicloAutoevaluacionUseCase
import com.example.sgva.usecases.RegistrarActividadCicloUseCase
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/** Actividad recibida en el cuerpo de la solicitud, sin el `ciclo` (lo aporta la ruta). */
data class SolicitudActividadCiclo(
    val id: String,
    val fase: Int,
    val descripcion: String,
    val responsable: String,
    val fechaInicioPlanificada: LocalDate,
    val fechaFinPlanificada: LocalDate,
    val estado: EstadoActividad,
) {
    fun aActividadCiclo(ciclo: String): ActividadCiclo = ActividadCiclo(
        id = id,
        ciclo = ciclo,
        fase = fase,
        descripcion = descripcion,
        responsable = responsable,
        fechaInicioPlanificada = fechaInicioPlanificada,
        fechaFinPlanificada = fechaFinPlanificada,
        estado = estado,
    )
}

/** Solicitud de registro de un ciclo nuevo, con sus actividades iniciales (opcionales). */
data class SolicitudRegistroCiclo(
    val id: String,
    val programa: String,
    val periodo: String,
    val actividades: List<SolicitudActividadCiclo> = emptyList(),
)

/**
 * Adaptador de entrada REST (Primary Adapter) del Módulo 5 (`DESIGN_SGVA.md`
 * §4, V6): expone el registro de ciclos (con sus fases fijas y actividades
 * iniciales), el registro de actividades adicionales, la consulta y el
 * seguimiento del avance. Delega toda la lógica a los casos de uso.
 */
@RestController
@RequestMapping("/api/ciclos")
class CicloAutoevaluacionController(
    private val registrarCiclo: RegistrarCicloAutoevaluacionUseCase,
    private val registrarActividad: RegistrarActividadCicloUseCase,
    private val consultarCiclo: ConsultarCicloAutoevaluacionUseCase,
    private val calcularAvanceCiclo: CalcularAvanceCicloUseCase,
) {

    @PostMapping
    fun registrar(@RequestBody solicitud: SolicitudRegistroCiclo): ResponseEntity<DetalleCiclo> {
        val ciclo = CicloAutoevaluacion(id = solicitud.id, programa = solicitud.programa, periodo = solicitud.periodo)
        val actividades = solicitud.actividades.map { it.aActividadCiclo(ciclo.id) }
        val detalle = registrarCiclo.ejecutar(ciclo, actividades)
        return ResponseEntity.status(201).body(detalle)
    }

    @GetMapping
    fun listar(
        @RequestParam programa: String?,
        @RequestParam periodo: String?,
    ): List<CicloAutoevaluacion> =
        consultarCiclo.listarTodos()
            .filter { programa == null || it.programa == programa }
            .filter { periodo == null || it.periodo == periodo }

    @GetMapping("/{id}")
    fun buscarDetallePorId(@PathVariable id: String): ResponseEntity<DetalleCiclo> =
        consultarCiclo.buscarDetallePorId(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping("/{id}/actividades")
    fun registrarActividad(
        @PathVariable id: String,
        @RequestBody solicitud: SolicitudActividadCiclo,
    ): ResponseEntity<ActividadCiclo> {
        val registrada = registrarActividad.ejecutar(solicitud.aActividadCiclo(id))
        return ResponseEntity.status(201).body(registrada)
    }

    @GetMapping("/{id}/avance")
    fun avance(@PathVariable id: String, @RequestParam fechaReferencia: LocalDate): AvanceCiclo =
        calcularAvanceCiclo.ejecutar(id, fechaReferencia)
}

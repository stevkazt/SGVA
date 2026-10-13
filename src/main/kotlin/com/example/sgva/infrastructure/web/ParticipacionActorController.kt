package com.example.sgva.infrastructure.web

import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.domain.ResultadoIngesta
import com.example.sgva.usecases.ConsultarParticipacionActorUseCase
import com.example.sgva.usecases.ProcesarParticipacionActorUseCase
import com.example.sgva.usecases.RegistrarParticipacionActorUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * Adaptador de entrada REST (Primary Adapter) del Módulo 5 (`DESIGN_SGVA.md`
 * §4, V6): expone el registro individual, la consulta y la carga masiva de
 * la participación de actores en las encuestas de un ciclo. Delega toda la
 * lógica a los casos de uso.
 */
@RestController
@RequestMapping("/api/participacion-actores")
class ParticipacionActorController(
    private val registrarParticipacionActor: RegistrarParticipacionActorUseCase,
    private val consultarParticipacionActor: ConsultarParticipacionActorUseCase,
    private val procesarParticipacionActor: ProcesarParticipacionActorUseCase,
) {

    @PostMapping
    fun registrar(@RequestBody participacion: ParticipacionActor): ResponseEntity<ParticipacionActor> {
        val registrada = registrarParticipacionActor.ejecutar(participacion)
        return ResponseEntity.status(201).body(registrada)
    }

    @GetMapping
    fun listar(@RequestParam ciclo: String): List<ParticipacionActor> =
        consultarParticipacionActor.listarPorCiclo(ciclo)

    @PostMapping("/archivos", consumes = ["multipart/form-data"])
    fun cargarArchivo(@RequestParam("archivo") archivo: MultipartFile): ResponseEntity<ResultadoIngesta> {
        val resultado = procesarParticipacionActor.ejecutar(archivo.originalFilename ?: archivo.name, archivo.bytes)
        return ResponseEntity.status(201).body(resultado)
    }
}

package com.example.sgva.infrastructure.web

import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.ResultadoIngesta
import com.example.sgva.usecases.CargarConfiguracionPonderacionesUseCase
import com.example.sgva.usecases.ConsultarConfiguracionPonderacionesUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * Adaptador de entrada REST (Primary Adapter) de la configuración de
 * ponderaciones del Módulo 4 (`DESIGN_SGVA.md` §0.1 y §4): permite cargar el
 * archivo de ponderaciones del modelo y consultar la configuración vigente.
 * Delega todo en los casos de uso de carga y consulta.
 */
@RestController
@RequestMapping("/api/ponderaciones")
class ConfiguracionPonderacionesController(
    private val cargarConfiguracionPonderaciones: CargarConfiguracionPonderacionesUseCase,
    private val consultarConfiguracionPonderaciones: ConsultarConfiguracionPonderacionesUseCase,
) {

    @GetMapping
    fun obtener(): ConfiguracionPonderaciones = consultarConfiguracionPonderaciones.ejecutar()

    /**
     * Responde con el mismo [ResultadoIngesta] de las demás cargas: cada fila
     * del archivo (factor, característica o mezcla) cuenta como leída y como
     * registrada, porque el archivo se acepta o se rechaza completo.
     */
    @PostMapping("/archivos", consumes = ["multipart/form-data"])
    fun cargarArchivo(@RequestParam("archivo") archivo: MultipartFile): ResponseEntity<ResultadoIngesta> {
        val nombre = archivo.originalFilename ?: archivo.name
        val configuracion = cargarConfiguracionPonderaciones.ejecutar(nombre, archivo.bytes)
        val filas = configuracion.pesosFactores.size + configuracion.caracteristicas.size + FILAS_DE_MEZCLA
        return ResponseEntity.status(201).body(
            ResultadoIngesta(
                nombreArchivo = nombre,
                totalRegistrosLeidos = filas,
                registrados = filas,
                omitidos = emptyList(),
            ),
        )
    }

    private companion object {
        const val FILAS_DE_MEZCLA = 2
    }
}

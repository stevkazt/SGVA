package com.example.sgva.infrastructure.web

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.DominioException
import com.example.sgva.domain.EntidadDuplicadaException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/** Cuerpo de respuesta uniforme para los errores traducidos por [ManejadorGlobalDeErrores]. */
data class ErrorRespuesta(val mensaje: String)

/**
 * Traduce las excepciones de dominio (`DESIGN_SGVA.md` §0.1, §5) a respuestas
 * HTTP consistentes para todos los controladores REST. La validación de
 * negocio ya ocurrió en el dominio o en los casos de uso (constructores de
 * [com.example.sgva.domain.Estudiante]/[com.example.sgva.domain.Docente],
 * reglas de aplicación de los casos de uso); este adaptador solo traduce el
 * resultado a un código HTTP, no reimplementa reglas.
 */
@RestControllerAdvice
class ManejadorGlobalDeErrores {

    /** Identificador ya registrado: la solicitud es válida pero entra en conflicto con el estado actual. */
    @ExceptionHandler(EntidadDuplicadaException::class)
    fun manejarDuplicado(ex: EntidadDuplicadaException): ResponseEntity<ErrorRespuesta> =
        responder(HttpStatus.CONFLICT, ex.message)

    /** No hay datos suficientes para calcular el indicador solicitado. */
    @ExceptionHandler(DatosInsuficientesException::class)
    fun manejarDatosInsuficientes(ex: DatosInsuficientesException): ResponseEntity<ErrorRespuesta> =
        responder(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)

    /** Resto de violaciones de reglas de negocio (formato inválido, campo vacío, archivo no soportado, etc.). */
    @ExceptionHandler(DominioException::class)
    fun manejarDominio(ex: DominioException): ResponseEntity<ErrorRespuesta> =
        responder(HttpStatus.BAD_REQUEST, ex.message)

    /**
     * El cuerpo JSON no pudo deserializarse. Si la causa raíz es una
     * [DominioException] —p. ej. el `init` de [com.example.sgva.domain.Estudiante]
     * rechazó el `puntajeSaberPro`— se conserva su mensaje; si es un problema de
     * formato ajeno al dominio (JSON corrupto, campo faltante), se responde con
     * un mensaje genérico.
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun manejarCuerpoInvalido(ex: HttpMessageNotReadableException): ResponseEntity<ErrorRespuesta> {
        val causaDominio = generateSequence<Throwable>(ex) { it.cause }
            .filterIsInstance<DominioException>()
            .firstOrNull()
        return responder(HttpStatus.BAD_REQUEST, causaDominio?.message ?: "El cuerpo de la solicitud no es válido.")
    }

    private fun responder(estado: HttpStatus, mensaje: String?): ResponseEntity<ErrorRespuesta> =
        ResponseEntity.status(estado).body(ErrorRespuesta(mensaje ?: estado.reasonPhrase))
}

package com.example.sgva.domain

/**
 * Excepción base para toda violación de una regla de negocio detectada en la
 * capa de dominio. Extiende [IllegalArgumentException] para que los adaptadores
 * puedan capturar de forma uniforme cualquier rechazo de un registro corrupto.
 *
 * Las reglas de validación se aplican en el dominio y nunca se delegan
 * silenciosamente a la infraestructura (Sección 0.1 de `DESIGN_SGVA.md`).
 */
open class DominioException(mensaje: String) : IllegalArgumentException(mensaje)

/** Se lanza cuando un campo obligatorio de una entidad llega vacío o en blanco. */
class CampoRequeridoVacioException(campo: String) :
    DominioException("El campo '$campo' es obligatorio y no puede estar vacío.")

/**
 * Se lanza cuando un campo de periodo académico (`periodo` o `cohorte`) no
 * cumple el formato estricto `AAAAS`: 4 dígitos de año + 1 dígito de semestre
 * (`1` o `2`), sin separadores. Ej.: `"20261"` es válido; `"2026"`, `"20263"`
 * y `"2026-1"` no lo son.
 */
class FormatoPeriodoInvalidoException(campo: String, valor: String) :
    DominioException(
        "El campo '$campo' debe tener formato AAAAS " +
            "(año de 4 dígitos + semestre 1 o 2, sin separadores); valor recibido: '$valor'.",
    )

/**
 * Se lanza cuando el puntaje global de Saber Pro está fuera del rango oficial
 * `0..300` inclusive. Un estudiante que aún no ha presentado la prueba se
 * modela con `null`, nunca con `0` (Sección 0.1 de `DESIGN_SGVA.md`).
 */
class PuntajeSaberProFueraDeRangoException(valor: Int) :
    DominioException(
        "El puntaje Saber Pro debe estar entre ${Estudiante.RANGO_SABER_PRO.first} y " +
            "${Estudiante.RANGO_SABER_PRO.last} inclusive; valor recibido: $valor.",
    )

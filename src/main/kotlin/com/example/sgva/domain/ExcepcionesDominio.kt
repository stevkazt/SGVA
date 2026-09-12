package com.example.sgva.domain

/**
 * Excepción base para toda violación de una regla de negocio detectada en la
 * capa de dominio. Extiende [IllegalArgumentException] para que los adaptadores
 * puedan capturar de forma uniforme cualquier rechazo de un registro corrupto.
 *
 * Las reglas de validación se aplican en el dominio y nunca se delegan
 * silenciosamente a la infraestructura (Sección 0.1 de `DESIGN_SGVA.md`).
 */
open class DominioException(mensaje: String, causa: Throwable? = null) :
    IllegalArgumentException(mensaje, causa)

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

/**
 * Se lanza cuando la calificación de una [RespuestaEncuesta] está fuera del
 * rango válido de la escala Likert de 5 puntos (`1..5` inclusive).
 */
class CalificacionFueraDeRangoException(valor: Int) :
    DominioException(
        "La calificación debe estar entre ${RespuestaEncuesta.RANGO_CALIFICACION.first} y " +
            "${RespuestaEncuesta.RANGO_CALIFICACION.last} inclusive (escala Likert); valor recibido: $valor.",
    )

/**
 * Se lanza al intentar registrar una entidad cuyo identificador ya existe en el
 * repositorio. El registro es una operación de alta: modificar un registro
 * existente queda fuera del alcance de esta versión.
 *
 * @param tipoEntidad nombre legible de la entidad (ej.: `"estudiante"`, `"docente"`).
 * @param id identificador que ya estaba registrado.
 */
class EntidadDuplicadaException(tipoEntidad: String, id: String) :
    DominioException("Ya existe un registro de $tipoEntidad con id '$id'.")

/**
 * Se lanza cuando un archivo cargado desde el exterior no puede procesarse: le
 * faltan columnas obligatorias, está corrupto o contiene datos que —ni siquiera
 * tras la normalización en infraestructura— pueden convertirse en una entidad
 * válida (`DESIGN_SGVA.md` §5.2).
 *
 * Es una excepción controlada: los adaptadores de parseo la usan para envolver
 * cualquier fallo técnico de la librería subyacente y dar una alerta clara sin
 * romper la ejecución de la aplicación.
 *
 * @param mensaje descripción del problema, con el nombre del archivo y —si aplica— el número de fila.
 * @param causa excepción técnica original, si la hubo.
 */
class FormatoArchivoInvalidoException(mensaje: String, causa: Throwable? = null) :
    DominioException(mensaje, causa)

/**
 * Se lanza cuando el cálculo de un [IndicadorCalidad] produce un valor que no es
 * un número finito (`NaN` o infinito), señal de una división por cero o de datos
 * corruptos que no se filtraron antes del cálculo.
 */
class ValorIndicadorInvalidoException(nombre: String, valor: Double) :
    DominioException("El indicador '$nombre' produjo un valor no finito ($valor).")

/**
 * Se lanza cuando no hay datos suficientes para calcular un indicador: una
 * cohorte o periodo sin estudiantes registrados, o un conjunto sin ningún
 * puntaje Saber Pro presentado.
 */
class DatosInsuficientesException(mensaje: String) : DominioException(mensaje)

package com.example.sgva.domain

/**
 * Estado de vinculación de un [Estudiante] con la institución.
 *
 * `GRADUADO` representa también al egresado (ver Sección 0 de `DESIGN_SGVA.md`):
 * no existe una entidad `Egresado` independiente.
 */
enum class EstadoEstudiante {
    MATRICULADO,
    GRADUADO,
    DESERTOR,
}

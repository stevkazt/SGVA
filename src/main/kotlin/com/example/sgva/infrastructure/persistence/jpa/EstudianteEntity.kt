package com.example.sgva.infrastructure.persistence.jpa

import com.example.sgva.domain.EstadoEstudiante
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de persistencia para [com.example.sgva.domain.Estudiante]
 * (adaptador de salida sobre PostgreSQL, `DESIGN_SGVA.md` §3–§4). Distinta de
 * la entidad de dominio: el dominio no debe depender de JPA/Jakarta
 * (`CLAUDE.md`); el mapeo entre ambas vive en `MapeadorPersistencia.kt`.
 *
 * Anotaciones con destino `@field:` para forzar el acceso por campo de
 * Hibernate: permite poblar propiedades `val` inmutables por reflexión sin
 * exigir setters.
 *
 * @property creadoEn instante de inserción; solo existe en esta entidad (el
 *   dominio no lo conoce) para poder devolver los listados "en orden de
 *   inserción", tal como exige el contrato de `AcademicDataRepository`.
 */
@Entity
@Table(name = "estudiantes")
internal class EstudianteEntity(
    @field:Id
    val id: String,
    val programa: String,
    val facultad: String,
    val cohorte: String,
    @field:Enumerated(EnumType.STRING)
    val estado: EstadoEstudiante,
    val puntajeSaberPro: Int?,
    val creadoEn: Instant = Instant.now(),
)

internal interface EstudianteJpaRepository : JpaRepository<EstudianteEntity, String> {
    fun findAllByOrderByCreadoEnAsc(): List<EstudianteEntity>
}

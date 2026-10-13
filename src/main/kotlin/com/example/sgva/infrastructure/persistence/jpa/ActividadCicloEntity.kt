package com.example.sgva.infrastructure.persistence.jpa

import com.example.sgva.domain.EstadoActividad
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.time.LocalDate

/**
 * Entidad JPA de persistencia para [com.example.sgva.domain.ActividadCiclo]
 * (adaptador de salida sobre PostgreSQL, Módulo 5, V6). Su `idActividad` (ej.
 * `"2.3"`) no es único entre ciclos distintos: el `id` técnico de esta
 * entidad es la concatenación de `ciclo` e `idActividad` (ver
 * [EvaluacionDocumentalEntity] para el mismo criterio).
 */
@Entity
@Table(name = "actividades_ciclo")
internal class ActividadCicloEntity(
    @field:Id
    val id: String,
    val idActividad: String,
    val ciclo: String,
    val fase: Int,
    val descripcion: String,
    val responsable: String,
    val fechaInicioPlanificada: LocalDate,
    val fechaFinPlanificada: LocalDate,
    @field:Enumerated(EnumType.STRING)
    val estado: EstadoActividad,
    val creadoEn: Instant = Instant.now(),
)

internal interface ActividadCicloJpaRepository : JpaRepository<ActividadCicloEntity, String> {
    fun findByCicloOrderByCreadoEnAsc(ciclo: String): List<ActividadCicloEntity>
}

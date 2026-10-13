package com.example.sgva.infrastructure.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de persistencia para [com.example.sgva.domain.FaseCiclo]
 * (adaptador de salida sobre PostgreSQL, Módulo 5, V6). Su clave natural es
 * la combinación de `ciclo` y `numero`; el `id` técnico es su concatenación
 * (ver [EvaluacionDocumentalEntity] para el mismo criterio).
 */
@Entity
@Table(name = "fases_ciclo")
internal class FaseCicloEntity(
    @field:Id
    val id: String,
    val ciclo: String,
    val numero: Int,
    val nombre: String,
    val creadoEn: Instant = Instant.now(),
)

internal interface FaseCicloJpaRepository : JpaRepository<FaseCicloEntity, String> {
    fun findByCicloOrderByCreadoEnAsc(ciclo: String): List<FaseCicloEntity>
}

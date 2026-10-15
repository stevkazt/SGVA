package com.example.sgva.infrastructure.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de la configuración de ponderaciones vigente (adaptador de
 * salida sobre PostgreSQL, `DESIGN_SGVA.md` §0.1). Refleja las filas del
 * archivo de ponderaciones: `tipo` es `FACTOR`, `CARACTERISTICA` o `MEZCLA`;
 * `factor` y `nombre` solo se usan en las características; `orden` conserva
 * el orden de las filas del archivo. La clave es `tipo:clave`.
 */
@Entity
@Table(name = "ponderaciones_modelo")
internal class PonderacionEntity(
    @field:Id
    val id: String,
    val tipo: String,
    val clave: String,
    val factor: String?,
    val nombre: String?,
    val ponderacion: Double,
    val orden: Int,
    val cargadoEn: Instant = Instant.now(),
)

internal interface PonderacionJpaRepository : JpaRepository<PonderacionEntity, String> {
    fun findAllByOrderByOrdenAsc(): List<PonderacionEntity>
}

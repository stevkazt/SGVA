package com.example.sgva.infrastructure.persistence.jpa

import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.TipoDedicacion
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de persistencia para [com.example.sgva.domain.Docente]
 * (adaptador de salida sobre PostgreSQL). Ver [EstudianteEntity] para el
 * razonamiento de `@field:` y `creadoEn`.
 */
@Entity
@Table(name = "docentes")
internal class DocenteEntity(
    @field:Id
    val id: String,
    val facultad: String,
    @field:Enumerated(EnumType.STRING)
    val nivelFormacion: NivelFormacion,
    @field:Enumerated(EnumType.STRING)
    val dedicacion: TipoDedicacion,
    val periodo: String,
    val creadoEn: Instant = Instant.now(),
)

internal interface DocenteJpaRepository : JpaRepository<DocenteEntity, String> {
    fun findAllByOrderByCreadoEnAsc(): List<DocenteEntity>
}

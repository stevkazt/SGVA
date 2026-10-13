package com.example.sgva.infrastructure.persistence.jpa

import com.example.sgva.domain.EstadoProyecto
import com.example.sgva.domain.OrigenProyecto
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de persistencia para [com.example.sgva.domain.ProyectoMejoramiento]
 * (adaptador de salida sobre PostgreSQL, Módulo 5, V6). `aspectosMejora` y
 * `actividades` son texto libre, por lo que se serializan separados por el
 * carácter de control `\u001E` (nunca presente en texto normal) en lugar de
 * una coma, que sí podría aparecer dentro de ellos; `factores` reutiliza el
 * criterio de coma de [ResultadoEncuestaPreguntaEntity], ya que son
 * identificadores cortos.
 */
@Entity
@Table(name = "proyectos_mejoramiento")
internal class ProyectoMejoramientoEntity(
    @field:Id
    val id: String,
    val programa: String,
    val nombre: String,
    val aspectosMejora: String,
    @field:Enumerated(EnumType.STRING)
    val origen: OrigenProyecto,
    val objetivo: String,
    val actividades: String,
    val factores: String,
    @field:Enumerated(EnumType.STRING)
    val estado: EstadoProyecto,
    val creadoEn: Instant = Instant.now(),
)

internal interface ProyectoMejoramientoJpaRepository : JpaRepository<ProyectoMejoramientoEntity, String> {
    fun findAllByOrderByCreadoEnAsc(): List<ProyectoMejoramientoEntity>
}

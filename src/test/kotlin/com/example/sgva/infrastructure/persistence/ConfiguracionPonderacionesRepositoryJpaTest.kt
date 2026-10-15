package com.example.sgva.infrastructure.persistence

import com.example.sgva.domain.Caracteristica
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.infrastructure.persistence.jpa.PonderacionJpaRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Prueba de integración de [ConfiguracionPonderacionesRepositoryJpa] contra
 * PostgreSQL real, igual que [AcademicDataRepositoryJpaTest]: cada prueba corre
 * en una transacción que `@DataJpaTest` revierte al terminar.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ConfiguracionPonderacionesRepositoryJpa::class)
@DirtiesContext
class ConfiguracionPonderacionesRepositoryJpaTest {

    @Autowired
    private lateinit var repositorio: ConfiguracionPonderacionesRepositoryJpa

    @Autowired
    private lateinit var filas: PonderacionJpaRepository

    private fun configuracion(pesoF1: Double, pesoF2: Double) = ConfiguracionPonderaciones(
        caracteristicas = listOf(
            Caracteristica(id = "C1", nombre = "Uno", factor = "F1", ponderacion = 0.4),
            Caracteristica(id = "C2", nombre = "Dos", factor = "F1", ponderacion = 0.6),
            Caracteristica(id = "C10", nombre = "Diez", factor = "F2", ponderacion = 1.0),
        ),
        pesosFactores = mapOf("F1" to pesoF1, "F2" to pesoF2),
        pesoEncuestas = 0.4,
        pesoDocumental = 0.6,
    )

    @Test
    fun `reemplazar y obtener devuelven la misma configuracion con su orden`() {
        val original = configuracion(0.7, 0.3)
        repositorio.reemplazar(original)

        assertEquals(original, repositorio.obtener())
    }

    @Test
    fun `reemplazar sustituye por completo la configuracion anterior`() {
        repositorio.reemplazar(configuracion(0.7, 0.3))
        repositorio.reemplazar(configuracion(0.2, 0.8))

        assertEquals(mapOf("F1" to 0.2, "F2" to 0.8), repositorio.obtener()?.pesosFactores)
        assertEquals(3, repositorio.obtener()?.caracteristicas?.size)
    }

    @Test
    fun `sin filas guardadas obtener devuelve nulo`() {
        // Se vacía la tabla para no depender de lo que haya en la base de desarrollo.
        filas.deleteAllInBatch()

        assertNull(repositorio.obtener())
    }
}

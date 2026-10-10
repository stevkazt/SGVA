package com.example.sgva.infrastructure.configuracion

import com.example.sgva.domain.FormatoArchivoInvalidoException
import com.example.sgva.domain.SumaPonderacionesInvalidaException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Path
import kotlin.test.assertEquals

/**
 * Verifica la carga de [ConfiguracionPonderaciones][com.example.sgva.domain.ConfiguracionPonderaciones]
 * desde el archivo externo de ponderaciones (`DESIGN_SGVA.md` §0.1 y §4).
 */
class CargadorConfiguracionPonderacionesTest {

    private val cargador = CargadorConfiguracionPonderaciones()

    @Test
    fun `un archivo valido construye la configuracion con sus pesos de factores, caracteristicas y mezcla`() {
        val configuracion = cargador.parsear(
            contenido = """
                tipo,id,factor,nombre,ponderacion
                FACTOR,F1,,,0.6
                FACTOR,F2,,,0.4
                CARACTERISTICA,C1,F1,Característica 1,1.0
                CARACTERISTICA,C2,F2,Característica 2,1.0
                MEZCLA,ENCUESTAS,,,0.4
                MEZCLA,DOCUMENTAL,,,0.6
            """.trimIndent(),
            origen = "test",
        )

        assertEquals(mapOf("F1" to 0.6, "F2" to 0.4), configuracion.pesosFactores)
        assertEquals(setOf("C1", "C2"), configuracion.caracteristicas.map { it.id }.toSet())
        assertEquals(0.4, configuracion.pesoEncuestas)
        assertEquals(0.6, configuracion.pesoDocumental)
    }

    @Test
    fun `una suma de pesos de factores invalida es rechazada con la misma regla de dominio`() {
        assertThrows<SumaPonderacionesInvalidaException> {
            cargador.parsear(
                contenido = """
                    tipo,id,factor,nombre,ponderacion
                    FACTOR,F1,,,0.5
                    FACTOR,F2,,,0.4
                    CARACTERISTICA,C1,F1,Característica 1,1.0
                    CARACTERISTICA,C2,F2,Característica 2,1.0
                    MEZCLA,ENCUESTAS,,,0.4
                    MEZCLA,DOCUMENTAL,,,0.6
                """.trimIndent(),
                origen = "test",
            )
        }
    }

    @Test
    fun `un archivo con columnas faltantes en el encabezado es rechazado`() {
        assertThrows<FormatoArchivoInvalidoException> {
            cargador.parsear(
                contenido = """
                    tipo,id,factor,ponderacion
                    FACTOR,F1,,0.6
                """.trimIndent(),
                origen = "test",
            )
        }
    }

    @Test
    fun `una fila con un tipo desconocido es rechazada`() {
        assertThrows<FormatoArchivoInvalidoException> {
            cargador.parsear(
                contenido = """
                    tipo,id,factor,nombre,ponderacion
                    OTRO,F1,,,0.6
                    MEZCLA,ENCUESTAS,,,0.4
                    MEZCLA,DOCUMENTAL,,,0.6
                """.trimIndent(),
                origen = "test",
            )
        }
    }

    @Test
    fun `un archivo inexistente es rechazado con un mensaje claro, sin usar valores por defecto`() {
        assertThrows<FormatoArchivoInvalidoException> {
            cargador.cargar(Path.of("no-existe/ponderaciones.csv"))
        }
    }

    @Test
    fun `el archivo de ejemplo del modelo 2025 carga los 12 factores, 48 caracteristicas y la mezcla 0,4 - 0,6`() {
        val configuracion = cargador.cargar(
            Path.of("src/test/resources/fixtures/modelo2025/ponderaciones_modelo_2025.csv"),
        )

        assertEquals(12, configuracion.pesosFactores.size)
        assertEquals(48, configuracion.caracteristicas.size)
        assertEquals(0.4, configuracion.pesoEncuestas)
        assertEquals(0.6, configuracion.pesoDocumental)
    }
}

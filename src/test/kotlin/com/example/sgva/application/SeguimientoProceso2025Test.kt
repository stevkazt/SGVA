package com.example.sgva.application

import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.ConfiguracionSeguimiento
import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.domain.PreevaluacionEvaluador
import com.example.sgva.usecases.ConsolidarPreevaluacionesUseCase
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import de.siegmar.fastcsv.reader.CsvReader
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

/**
 * Valida la consolidación de preevaluaciones y el semáforo de participación
 * del Módulo 5 (`DESIGN_SGVA.md` §0.1, V6) contra los fixtures reales del
 * ciclo EGSTI-20252 en `src/test/resources/fixtures/proceso2025` (cronograma
 * aprobado, preevaluaciones, participación de actores).
 *
 * `esperados_v6.csv` contiene únicamente códigos y valores numéricos
 * tomados de esos fixtures (no se deriva de este cálculo): es el oráculo
 * contra el que se compara, con una tolerancia de `1e-4`.
 *
 * El avance de fases no se valida aquí contra `CalcularAvanceCicloUseCase`:
 * `cronograma_actividades.csv` no tiene columna `estado` (no hay avance de
 * completitud que derivar de este fixture) y la actividad `2.6` de la fase 2
 * aparece dos veces con `responsables`/fechas distintas, algo que
 * `ActividadCiclo` no puede representar porque su clave dentro de un ciclo
 * es su `id`. Por eso `actividades_fase` se valida aquí solo como el conteo
 * de filas del cronograma por fase —lo que ese nivel del oráculo realmente
 * registra—, sin pasar por el caso de uso. Detalle completo en
 * `logs/V6/P-026.md`.
 */
class SeguimientoProceso2025Test {

    private val ciclo = "EGSTI-20252"
    private val tolerancia = 1e-4
    private val configuracionSeguimiento = ConfiguracionSeguimiento(
        umbralRojoNaranja = 0.6,
        umbralNaranjaVerde = 0.8,
        diasProximaAVencer = 7,
    )

    private val repositorio = RepositorioAcademicoFalso().apply {
        RegistrarCicloAutoevaluacionUseCase(this).ejecutar(
            CicloAutoevaluacion(id = ciclo, programa = "EGSTI", periodo = "20252"),
        )
        filas("preevaluaciones.csv").forEach { (filaCiclo, caracteristica, evaluador, calificacion) ->
            guardarPreevaluacion(
                PreevaluacionEvaluador(
                    ciclo = filaCiclo,
                    caracteristica = caracteristica,
                    evaluador = evaluador,
                    calificacion = calificacion.toDouble(),
                ),
            )
        }
        filas("participacion_actores.csv").forEach { (filaCiclo, actor, poblacion, muestra, respuestas) ->
            guardarParticipacion(
                ParticipacionActor(
                    ciclo = filaCiclo,
                    actor = actor,
                    poblacion = poblacion.toInt(),
                    muestra = muestra.toInt(),
                    respuestas = respuestas.toInt(),
                ),
            )
        }
    }

    @Test
    fun `la consolidacion de preevaluaciones reproduce el cronograma 2025`() {
        val consolidadas = ConsolidarPreevaluacionesUseCase(repositorio).ejecutar(ciclo)
            .associate { it.caracteristica to it.calificacion }

        val diferencias = esperadosNumericosDelNivel("consolidada").mapNotNull { (id, esperado) ->
            diferenciaFueraDeTolerancia(id, esperado, consolidadas[id])
        }

        assertTrue(diferencias.isEmpty(), diferencias.joinToString("\n", prefix = "\n"))
    }

    @Test
    fun `el porcentaje diligenciado reproduce la participacion registrada`() {
        val porcentajes = repositorio.listarParticipacionPorCiclo(ciclo)
            .associate { it.actor to it.porcentajeDiligenciado }

        val diferencias = esperadosNumericosDelNivel("porcentaje").mapNotNull { (actor, esperado) ->
            diferenciaFueraDeTolerancia(actor, esperado, porcentajes[actor])
        }

        assertTrue(diferencias.isEmpty(), diferencias.joinToString("\n", prefix = "\n"))
    }

    @Test
    fun `el semaforo de participacion reproduce los umbrales vigentes`() {
        val semaforos = repositorio.listarParticipacionPorCiclo(ciclo)
            .associate { it.actor to it.semaforo(configuracionSeguimiento) }

        val diferencias = esperadosDeTextoDelNivel("semaforo").mapNotNull { (actor, esperado) ->
            val calculado = semaforos[actor]
            if (calculado?.name == esperado) null else "$actor: esperado $esperado, calculado $calculado"
        }

        assertTrue(diferencias.isEmpty(), diferencias.joinToString("\n", prefix = "\n"))
    }

    @Test
    fun `el conteo de actividades por fase reproduce el cronograma aprobado`() {
        val conteoPorFase = filas("cronograma_actividades.csv")
            .groupingBy { fila -> "fase${fila[2]}" }
            .eachCount()

        val diferencias = esperadosNumericosDelNivel("actividades_fase").mapNotNull { (fase, esperado) ->
            val calculado = conteoPorFase[fase]?.toDouble()
            if (calculado == esperado) null else "$fase: esperado $esperado, calculado $calculado"
        }

        assertTrue(diferencias.isEmpty(), diferencias.joinToString("\n", prefix = "\n"))
    }

    private fun diferenciaFueraDeTolerancia(id: String, esperado: Double, calculado: Double?): String? {
        if (calculado == null) return "$id: esperado $esperado, calculado ausente (null)"
        val diferencia = calculado - esperado
        if (Math.abs(diferencia) <= tolerancia) return null
        return "$id: esperado $esperado, calculado $calculado (diferencia ${"%+.6f".format(diferencia)})"
    }

    /** Lee `esperados_v6.csv` (columnas `nivel,id,valor`) y filtra por [nivel], con `valor` numérico. */
    private fun esperadosNumericosDelNivel(nivel: String): Map<String, Double> =
        filas("esperados_v6.csv").filter { it[0] == nivel }.associate { it[1] to it[2].toDouble() }

    /** Igual que [esperadosNumericosDelNivel] pero conserva `valor` como texto (ej.: el nombre del semáforo). */
    private fun esperadosDeTextoDelNivel(nivel: String): Map<String, String> =
        filas("esperados_v6.csv").filter { it[0] == nivel }.associate { it[1] to it[2] }

    /** Filas de un fixture de `proceso2025`, sin encabezado. */
    private fun filas(nombre: String): List<List<String>> {
        val contenido = String(fixture(nombre))
        return CsvReader.builder().ofCsvRecord(contenido).use { lector -> lector.map { it.fields } }.drop(1)
    }

    private fun fixture(nombre: String): ByteArray =
        checkNotNull(javaClass.getResourceAsStream("/fixtures/proceso2025/$nombre")) { "falta el fixture $nombre" }
            .use { it.readBytes() }
}

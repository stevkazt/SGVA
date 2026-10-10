package com.example.sgva.infrastructure.configuracion

import com.example.sgva.domain.Caracteristica
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.FormatoArchivoInvalidoException
import de.siegmar.fastcsv.reader.CsvReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path

/**
 * Carga [ConfiguracionPonderaciones] desde un archivo externo entregado por el
 * investigador (`DESIGN_SGVA.md` §4 "Carga de configuración de ponderaciones";
 * `CLAUDE.md` delega a Claude CLI el formato y la ubicación concretos). Nunca
 * contiene valores de peso hardcodeados: todos provienen del archivo leído.
 *
 * Formato esperado (encabezado exacto, en ese orden): `tipo,id,factor,nombre,ponderacion`.
 * Cada fila de datos es una de tres clases, según su columna `tipo`:
 *
 * - `FACTOR`: `id` es el identificador del factor; `ponderacion` es su peso
 *   dentro del global. Las columnas `factor` y `nombre` no se usan.
 * - `CARACTERISTICA`: `id`, `factor` y `nombre` alimentan directamente
 *   [Caracteristica]; `ponderacion` es su peso dentro de su factor.
 * - `MEZCLA`: `id` es `ENCUESTAS` o `DOCUMENTAL`; `ponderacion` es el peso de
 *   esa fuente en la mezcla de una característica. Ambas filas son
 *   obligatorias.
 *
 * No aplica ninguna validación de negocio propia (sumas de pesos, pesos no
 * negativos): construye [ConfiguracionPonderaciones] con los valores leídos y
 * deja que sea su propio `init` el que las aplique, para no duplicar esas
 * reglas. Cualquier problema de formato del archivo (columnas, filas, tipos o
 * archivo faltante) se reporta como [FormatoArchivoInvalidoException], con un
 * mensaje claro y sin recurrir a ningún valor por defecto.
 */
class CargadorConfiguracionPonderaciones {

    /** Lee el archivo en [ruta] del sistema de archivos y construye la configuración. */
    fun cargar(ruta: Path): ConfiguracionPonderaciones {
        val contenido = try {
            Files.readString(ruta)
        } catch (causa: NoSuchFileException) {
            throw FormatoArchivoInvalidoException("No se encontró el archivo de ponderaciones '$ruta'.", causa)
        } catch (causa: IOException) {
            throw FormatoArchivoInvalidoException("No se pudo leer el archivo de ponderaciones '$ruta'.", causa)
        }
        return parsear(contenido, ruta.toString())
    }

    /**
     * Construye la configuración a partir del [contenido] ya leído de un
     * archivo CSV. [origen] solo se usa para identificar el archivo en los
     * mensajes de error (ej. una ruta o un nombre de archivo).
     */
    fun parsear(contenido: String, origen: String): ConfiguracionPonderaciones {
        val filas = CsvReader.builder()
            .skipEmptyLines(true)
            .ofCsvRecord(contenido)
            .use { lector -> lector.map { it.fields } }

        if (filas.isEmpty()) {
            throw FormatoArchivoInvalidoException("El archivo de ponderaciones '$origen' no contiene ninguna fila.")
        }

        val encabezado = filas.first()
        if (encabezado != COLUMNAS_ESPERADAS) {
            throw FormatoArchivoInvalidoException(
                "El archivo de ponderaciones '$origen' debe tener las columnas " +
                    "${COLUMNAS_ESPERADAS.joinToString(",")}; encontradas: ${encabezado.joinToString(",")}.",
            )
        }

        val caracteristicas = mutableListOf<Caracteristica>()
        val pesosFactores = mutableMapOf<String, Double>()
        var pesoEncuestas: Double? = null
        var pesoDocumental: Double? = null

        filas.drop(1).forEachIndexed { indice, fila ->
            val numeroFila = indice + 2 // fila 1 es el encabezado
            if (fila.size != COLUMNAS_ESPERADAS.size) {
                throw FormatoArchivoInvalidoException(
                    "Archivo de ponderaciones '$origen', fila $numeroFila: se esperaban " +
                        "${COLUMNAS_ESPERADAS.size} columnas y se encontraron ${fila.size}.",
                )
            }

            val (tipo, id, factor, nombre, ponderacionTexto) = fila
            val ponderacion = parsearPonderacion(ponderacionTexto, origen, numeroFila)

            when (tipo.trim()) {
                "FACTOR" -> pesosFactores[id.trim()] = ponderacion
                "CARACTERISTICA" -> caracteristicas += Caracteristica(
                    id = id.trim(),
                    nombre = nombre.trim(),
                    factor = factor.trim(),
                    ponderacion = ponderacion,
                )
                "MEZCLA" -> when (id.trim()) {
                    "ENCUESTAS" -> pesoEncuestas = ponderacion
                    "DOCUMENTAL" -> pesoDocumental = ponderacion
                    else -> throw FormatoArchivoInvalidoException(
                        "Archivo de ponderaciones '$origen', fila $numeroFila: clave de mezcla desconocida " +
                            "'$id'; se esperaba 'ENCUESTAS' o 'DOCUMENTAL'.",
                    )
                }
                else -> throw FormatoArchivoInvalidoException(
                    "Archivo de ponderaciones '$origen', fila $numeroFila: tipo desconocido '$tipo'; " +
                        "se esperaba FACTOR, CARACTERISTICA o MEZCLA.",
                )
            }
        }

        if (pesoEncuestas == null || pesoDocumental == null) {
            throw FormatoArchivoInvalidoException(
                "El archivo de ponderaciones '$origen' debe incluir las filas MEZCLA,ENCUESTAS y MEZCLA,DOCUMENTAL.",
            )
        }

        return ConfiguracionPonderaciones(
            caracteristicas = caracteristicas,
            pesosFactores = pesosFactores,
            pesoEncuestas = pesoEncuestas,
            pesoDocumental = pesoDocumental,
        )
    }

    private fun parsearPonderacion(texto: String, origen: String, numeroFila: Int): Double =
        texto.trim().toDoubleOrNull() ?: throw FormatoArchivoInvalidoException(
            "Archivo de ponderaciones '$origen', fila $numeroFila: la ponderación '$texto' no es un número válido.",
        )

    companion object {
        private val COLUMNAS_ESPERADAS = listOf("tipo", "id", "factor", "nombre", "ponderacion")
    }
}

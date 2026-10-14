package com.example.sgva.infrastructure.reporting

import com.example.sgva.domain.EstadoCicloAutoevaluacion
import com.example.sgva.domain.FaseCiclo
import com.example.sgva.domain.ReporteEjecutivo
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Genera el PDF físico de un [ReporteEjecutivo] (Fase 4 del anteproyecto —
 * integración de reportes, `DESIGN_SGVA.md` §4). Única clase del proyecto que
 * depende de la librería de generación de documentos (Apache PDFBox); el
 * dominio y los casos de uso no la conocen ni dependen de ella.
 */
@Component
class GeneradorReportePdf {

    fun generar(reporte: ReporteEjecutivo): ByteArray {
        PDDocument().use { documento ->
            val lienzo = LienzoReporte(documento)

            lienzo.titulo("Reporte Ejecutivo SGVA")
            lienzo.subtitulo("Generado el " + FORMATO_FECHA.format(LocalDateTime.now()))

            lienzo.seccion("Indicadores de Calidad")
            if (reporte.indicadores.isEmpty()) {
                lienzo.linea("No hay indicadores disponibles.")
            } else {
                lienzo.tabla(
                    encabezados = listOf("Nombre", "Valor", "Periodo", "Facultad"),
                    anchos = listOf(220f, 70f, 90f, 120f),
                    filas = reporte.indicadores.map {
                        listOf(it.nombre, "%.2f".format(it.valor), it.periodo, it.facultad)
                    },
                )
            }

            lienzo.seccion("Respuestas de Encuesta")
            if (reporte.respuestasEncuesta.isEmpty()) {
                lienzo.linea("No hay respuestas de encuesta registradas.")
            } else {
                lienzo.tabla(
                    encabezados = listOf("Id", "Estamento", "Factor", "Calificación", "Periodo"),
                    anchos = listOf(80f, 90f, 180f, 80f, 70f),
                    filas = reporte.respuestasEncuesta.map {
                        listOf(it.id, it.estamento.name, it.factor, it.calificacion.toString(), it.periodo)
                    },
                )
            }

            reporte.resultadoAutoevaluacion?.let { resultado ->
                lienzo.seccion("Resultado de Autoevaluación")
                lienzo.linea("Programa: ${resultado.programa} - Periodo: ${resultado.periodo}")
                lienzo.tabla(
                    encabezados = listOf("Nivel", "Identificador", "Promedio"),
                    anchos = listOf(110f, 150f, 90f),
                    filas = buildList {
                        add(listOf("Global", "-", formatoPromedio(resultado.promedioGlobal)))
                        resultado.promediosPorFactor.forEach { (factor, promedio) ->
                            add(listOf("Factor", factor, formatoPromedio(promedio)))
                        }
                        resultado.promediosPorCaracteristica.forEach { (caracteristica, promedio) ->
                            add(listOf("Característica", caracteristica, formatoPromedio(promedio)))
                        }
                    },
                )
            }

            reporte.estadoCiclo?.let { seccionEstadoCiclo(lienzo, it) }

            lienzo.cerrar()

            return ByteArrayOutputStream().use { salida ->
                documento.save(salida)
                salida.toByteArray()
            }
        }
    }

    /**
     * Sección "Estado del Ciclo de Autoevaluación" (Módulo 5, V6,
     * `DESIGN_SGVA.md` §4): avance por fase y global, participación de los
     * actores, estado del plan de mejoramiento y comparación con el ciclo
     * inmediatamente anterior del mismo programa, si existe.
     */
    private fun seccionEstadoCiclo(lienzo: LienzoReporte, estado: EstadoCicloAutoevaluacion) {
        lienzo.seccion("Estado del Ciclo de Autoevaluación")
        lienzo.linea("Programa: ${estado.ciclo.programa} - Periodo: ${estado.ciclo.periodo}")
        lienzo.linea("Avance del ciclo: " + formatoPorcentaje(estado.avance.avanceCiclo))

        lienzo.tabla(
            encabezados = listOf("Fase", "Avance"),
            anchos = listOf(300f, 100f),
            filas = FaseCiclo.NOMBRES.toSortedMap().map { (numero, nombre) ->
                listOf(nombre, formatoPorcentaje(estado.avance.avancePorFase[numero]))
            },
        )

        lienzo.linea("Participación de los actores:")
        if (estado.avance.participacionActores.isEmpty()) {
            lienzo.linea("No hay participación de actores registrada.")
        } else {
            lienzo.tabla(
                encabezados = listOf("Actor", "Población", "Muestra", "Respuestas", "% Diligenciado", "Semáforo"),
                anchos = listOf(150f, 70f, 70f, 80f, 90f, 80f),
                filas = estado.avance.participacionActores.map { participacionConSemaforo ->
                    val participacion = participacionConSemaforo.participacion
                    listOf(
                        participacion.actor,
                        participacion.poblacion.toString(),
                        participacion.muestra.toString(),
                        participacion.respuestas.toString(),
                        formatoPorcentaje(participacion.porcentajeDiligenciado),
                        participacionConSemaforo.semaforo?.name ?: "Sin datos",
                    )
                },
            )
        }

        lienzo.linea("Plan de mejoramiento:")
        if (estado.proyectosMejoramiento.isEmpty()) {
            lienzo.linea("No hay proyectos de mejoramiento registrados.")
        } else {
            lienzo.tabla(
                encabezados = listOf("Proyecto", "Estado", "Factores"),
                anchos = listOf(220f, 100f, 150f),
                filas = estado.proyectosMejoramiento.map {
                    listOf(it.nombre, it.estado.name, it.factores.joinToString(", "))
                },
            )
        }

        estado.comparacionCicloAnterior?.let { comparacion ->
            lienzo.linea(
                "Comparación con el ciclo ${comparacion.cicloAnterior.periodo}: avance anterior " +
                    formatoPorcentaje(comparacion.avanceAnterior.avanceCiclo) + ", diferencia " +
                    formatoPorcentaje(comparacion.diferencia.diferenciaAvanceCiclo),
            )
        }
    }

    private fun formatoPromedio(valor: Double?): String = valor?.let { "%.2f".format(it) } ?: "Sin datos"

    private fun formatoPorcentaje(valor: Double?): String = valor?.let { "%.1f%%".format(it * 100) } ?: "Sin datos"

    companion object {
        private val FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }
}

/**
 * Maneja el posicionamiento vertical y la paginación automática del PDF:
 * abre una página nueva cuando el contenido no cabe en la actual. Cada
 * fragmento de texto se dibuja en su propio bloque `beginText`/`endText` con
 * coordenadas absolutas, evitando la aritmética de desplazamientos relativos
 * de PDFBox.
 */
private class LienzoReporte(private val documento: PDDocument) {

    private val fuenteTitulo = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
    private val fuenteSeccion = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
    private val fuenteTexto = PDType1Font(Standard14Fonts.FontName.HELVETICA)

    private val margenIzquierdo = 50f
    private val margenSuperior = 50f
    private val margenInferior = 50f
    private val altoLinea = 16f

    private var contenido: PDPageContentStream = abrirPagina()
    private var y: Float = alturaUtilInicial()

    fun titulo(texto: String) {
        dibujar(texto, margenIzquierdo, y, fuenteTitulo, 18f)
        y -= 26f
    }

    fun subtitulo(texto: String) {
        dibujar(texto, margenIzquierdo, y, fuenteTexto, 10f)
        y -= 24f
    }

    fun seccion(texto: String) {
        asegurarEspacio(altoLinea * 2)
        dibujar(texto, margenIzquierdo, y, fuenteSeccion, 13f)
        y -= altoLinea
    }

    fun linea(texto: String) {
        asegurarEspacio(altoLinea)
        dibujar(texto, margenIzquierdo, y, fuenteTexto, 10f)
        y -= altoLinea
    }

    fun tabla(encabezados: List<String>, anchos: List<Float>, filas: List<List<String>>) {
        asegurarEspacio(altoLinea)
        filaTabla(encabezados, anchos, fuenteSeccion)
        val yLineaSeparadora = y + 4f
        contenido.moveTo(margenIzquierdo, yLineaSeparadora)
        contenido.lineTo(margenIzquierdo + anchos.sum(), yLineaSeparadora)
        contenido.stroke()
        y -= altoLinea

        for (fila in filas) {
            asegurarEspacio(altoLinea)
            filaTabla(fila, anchos, fuenteTexto)
            y -= altoLinea
        }
        y -= altoLinea / 2
    }

    fun cerrar() {
        contenido.close()
    }

    private fun filaTabla(valores: List<String>, anchos: List<Float>, fuente: PDFont) {
        var x = margenIzquierdo
        for ((valor, ancho) in valores.zip(anchos)) {
            dibujar(ajustarAncho(valor, fuente, 9f, ancho), x, y, fuente, 9f)
            x += ancho
        }
    }

    private fun dibujar(texto: String, x: Float, y: Float, fuente: PDFont, tamano: Float) {
        contenido.beginText()
        contenido.setFont(fuente, tamano)
        contenido.newLineAtOffset(x, y)
        contenido.showText(texto)
        contenido.endText()
    }

    private fun ajustarAncho(texto: String, fuente: PDFont, tamano: Float, anchoMaximo: Float): String {
        if (anchoTexto(texto, fuente, tamano) <= anchoMaximo) return texto
        var recorte = texto
        while (recorte.isNotEmpty() && anchoTexto("$recorte...", fuente, tamano) > anchoMaximo) {
            recorte = recorte.dropLast(1)
        }
        return if (recorte.isEmpty()) texto else "$recorte..."
    }

    private fun anchoTexto(texto: String, fuente: PDFont, tamano: Float): Float =
        fuente.getStringWidth(texto) / 1000f * tamano

    private fun asegurarEspacio(alturaNecesaria: Float) {
        if (y - alturaNecesaria < margenInferior) {
            contenido.close()
            contenido = abrirPagina()
            y = alturaUtilInicial()
        }
    }

    private fun abrirPagina(): PDPageContentStream {
        val pagina = PDPage(PDRectangle.LETTER)
        documento.addPage(pagina)
        return PDPageContentStream(documento, pagina)
    }

    private fun alturaUtilInicial(): Float = PDRectangle.LETTER.height - margenSuperior
}

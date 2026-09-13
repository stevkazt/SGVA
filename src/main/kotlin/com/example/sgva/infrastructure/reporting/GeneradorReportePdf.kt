package com.example.sgva.infrastructure.reporting

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

            lienzo.cerrar()

            return ByteArrayOutputStream().use { salida ->
                documento.save(salida)
                salida.toByteArray()
            }
        }
    }

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

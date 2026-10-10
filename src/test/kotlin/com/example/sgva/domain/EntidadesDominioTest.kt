package com.example.sgva.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EntidadesDominioTest {

    // =========================================================================
    // Estudiante
    // =========================================================================

    @Test
    fun `un estudiante valido se construye correctamente`() {
        val estudiante = Estudiante(
            id = "EST-20211-001",
            programa = "Ingeniería de Telecomunicaciones",
            facultad = "Ingeniería",
            cohorte = "20211",
            estado = EstadoEstudiante.GRADUADO,
            puntajeSaberPro = 210,
        )

        assertEquals("20211", estudiante.cohorte)
        assertEquals(210, estudiante.puntajeSaberPro)
    }

    @Test
    fun `el puntaje Saber Pro es opcional cuando el estudiante no ha presentado la prueba`() {
        val estudiante = Estudiante(
            id = "EST-20221-005",
            programa = "Ingeniería de Sistemas",
            facultad = "Ingeniería",
            cohorte = "20221",
            estado = EstadoEstudiante.MATRICULADO,
        )

        assertNull(estudiante.puntajeSaberPro)
    }

    @Test
    fun `la cohorte con guion es rechazada por no cumplir el formato AAAAS`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            estudianteConCohorte("2021-1")
        }
    }

    @Test
    fun `la cohorte con semestre invalido es rechazada`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            estudianteConCohorte("20213")
        }
    }

    @Test
    fun `la cohorte sin digito de semestre es rechazada`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            estudianteConCohorte("2021")
        }
    }

    @Test
    fun `los limites 0 y 300 del puntaje Saber Pro son validos`() {
        assertEquals(0, estudianteConPuntaje(0).puntajeSaberPro)
        assertEquals(300, estudianteConPuntaje(300).puntajeSaberPro)
    }

    @Test
    fun `un puntaje Saber Pro por encima de 300 es rechazado`() {
        assertThrows<PuntajeSaberProFueraDeRangoException> { estudianteConPuntaje(301) }
    }

    @Test
    fun `un puntaje Saber Pro negativo es rechazado`() {
        assertThrows<PuntajeSaberProFueraDeRangoException> { estudianteConPuntaje(-1) }
    }

    @Test
    fun `un id de estudiante en blanco es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            Estudiante(
                id = "  ",
                programa = "Ingeniería de Sistemas",
                facultad = "Ingeniería",
                cohorte = "20211",
                estado = EstadoEstudiante.MATRICULADO,
            )
        }
    }

    // =========================================================================
    // Docente
    // =========================================================================

    @Test
    fun `un docente valido se construye correctamente`() {
        val docente = Docente(
            id = "D001",
            programa = "EGSTI",
            facultad = "Ingeniería",
            nivelFormacion = NivelFormacion.DOCTORADO,
            dedicacion = TipoDedicacion.TIEMPO_COMPLETO,
            periodo = "20261",
        )

        assertEquals("20261", docente.periodo)
        assertEquals(NivelFormacion.DOCTORADO, docente.nivelFormacion)
    }

    @Test
    fun `el periodo del docente con formato invalido es rechazado`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            Docente(
                id = "D001",
                programa = "EGSTI",
                facultad = "Ingeniería",
                nivelFormacion = NivelFormacion.MAESTRIA,
                dedicacion = TipoDedicacion.CATEDRA,
                periodo = "2026-1",
            )
        }
    }

    @Test
    fun `una facultad de docente en blanco es rechazada`() {
        assertThrows<CampoRequeridoVacioException> {
            Docente(
                id = "D001",
                programa = "EGSTI",
                facultad = "",
                nivelFormacion = NivelFormacion.MAESTRIA,
                dedicacion = TipoDedicacion.MEDIO_TIEMPO,
                periodo = "20261",
            )
        }
    }

    @Test
    fun `un programa de docente en blanco es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            Docente(
                id = "D001",
                programa = "  ",
                facultad = "Ingeniería",
                nivelFormacion = NivelFormacion.MAESTRIA,
                dedicacion = TipoDedicacion.MEDIO_TIEMPO,
                periodo = "20261",
            )
        }
    }

    // =========================================================================
    // RespuestaEncuesta
    // =========================================================================

    @Test
    fun `una respuesta de encuesta valida se construye correctamente`() {
        val respuesta = RespuestaEncuesta(
            id = "R001",
            estamento = TipoEstamento.ESTUDIANTE,
            factor = "Infraestructura",
            calificacion = 4,
            periodo = "20261",
        )

        assertEquals(4, respuesta.calificacion)
        assertEquals(TipoEstamento.ESTUDIANTE, respuesta.estamento)
    }

    @Test
    fun `los limites 1 y 5 de la calificacion Likert son validos`() {
        assertEquals(1, respuestaConCalificacion(1).calificacion)
        assertEquals(5, respuestaConCalificacion(5).calificacion)
    }

    @Test
    fun `una calificacion por encima de 5 es rechazada`() {
        assertThrows<CalificacionFueraDeRangoException> { respuestaConCalificacion(6) }
    }

    @Test
    fun `una calificacion de 0 es rechazada`() {
        assertThrows<CalificacionFueraDeRangoException> { respuestaConCalificacion(0) }
    }

    @Test
    fun `el periodo de la respuesta con formato invalido es rechazado`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            RespuestaEncuesta(
                id = "R001",
                estamento = TipoEstamento.PROFESOR,
                factor = "Plan de Estudios",
                calificacion = 3,
                periodo = "2026-1",
            )
        }
    }

    @Test
    fun `un factor de respuesta en blanco es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            RespuestaEncuesta(
                id = "R001",
                estamento = TipoEstamento.EMPLEADOR,
                factor = "  ",
                calificacion = 3,
                periodo = "20261",
            )
        }
    }

    // =========================================================================
    // ResultadoEncuestaPregunta
    // =========================================================================

    @Test
    fun `un resultado de encuesta por pregunta valido se construye correctamente`() {
        val resultado = ResultadoEncuestaPregunta(
            programa = "EGSTI",
            periodo = "20261",
            codigoPregunta = "A2",
            caracteristica = "1.1",
            estamentos = setOf(TipoEstamento.ESTUDIANTE, TipoEstamento.PROFESOR),
            promedio = 4.5,
        )

        assertEquals(4.5, resultado.promedio)
        assertEquals(setOf(TipoEstamento.ESTUDIANTE, TipoEstamento.PROFESOR), resultado.estamentos)
    }

    @Test
    fun `un promedio ausente (null) es valido`() {
        val resultado = resultadoConPromedio(null)

        assertNull(resultado.promedio)
    }

    @Test
    fun `los limites 1,0 y 5,0 del promedio son validos`() {
        assertEquals(1.0, resultadoConPromedio(1.0).promedio)
        assertEquals(5.0, resultadoConPromedio(5.0).promedio)
    }

    @Test
    fun `un promedio de 0 es rechazado como dato invalido, no como ausente`() {
        assertThrows<PromedioFueraDeRangoException> { resultadoConPromedio(0.0) }
    }

    @Test
    fun `un promedio por encima de 5 es rechazado`() {
        assertThrows<PromedioFueraDeRangoException> { resultadoConPromedio(5.1) }
    }

    @Test
    fun `un periodo de resultado con formato invalido es rechazado`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            ResultadoEncuestaPregunta(
                programa = "EGSTI",
                periodo = "2026-1",
                codigoPregunta = "A2",
                caracteristica = "1.1",
                estamentos = setOf(TipoEstamento.ESTUDIANTE),
                promedio = 4.0,
            )
        }
    }

    @Test
    fun `un conjunto de estamentos vacio es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            ResultadoEncuestaPregunta(
                programa = "EGSTI",
                periodo = "20261",
                codigoPregunta = "A2",
                caracteristica = "1.1",
                estamentos = emptySet(),
                promedio = 4.0,
            )
        }
    }

    @Test
    fun `un programa de resultado en blanco es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            ResultadoEncuestaPregunta(
                programa = "  ",
                periodo = "20261",
                codigoPregunta = "A2",
                caracteristica = "1.1",
                estamentos = setOf(TipoEstamento.ESTUDIANTE),
                promedio = 4.0,
            )
        }
    }

    // =========================================================================
    // EvaluacionDocumental
    // =========================================================================

    @Test
    fun `una evaluacion documental valida se construye correctamente`() {
        val evaluacion = EvaluacionDocumental(
            programa = "EGSTI",
            caracteristica = "1.1",
            periodo = "20261",
            calificacion = 4.7,
            justificacion = "Evidencia documental consistente",
            sugerenciasMejora = "Actualizar el PEP",
        )

        assertEquals(4.7, evaluacion.calificacion)
        assertEquals("Evidencia documental consistente", evaluacion.justificacion)
        assertEquals("Actualizar el PEP", evaluacion.sugerenciasMejora)
    }

    @Test
    fun `la justificacion y las sugerencias de mejora son opcionales`() {
        val evaluacion = evaluacionConCalificacion(4.0)

        assertNull(evaluacion.justificacion)
        assertNull(evaluacion.sugerenciasMejora)
    }

    @Test
    fun `los limites 1,0 y 5,0 de la calificacion documental son validos`() {
        assertEquals(1.0, evaluacionConCalificacion(1.0).calificacion)
        assertEquals(5.0, evaluacionConCalificacion(5.0).calificacion)
    }

    @Test
    fun `una calificacion documental de 0 es rechazada, a diferencia del promedio de encuesta`() {
        assertThrows<CalificacionDocumentalFueraDeRangoException> { evaluacionConCalificacion(0.0) }
    }

    @Test
    fun `una calificacion documental por encima de 5 es rechazada`() {
        assertThrows<CalificacionDocumentalFueraDeRangoException> { evaluacionConCalificacion(5.1) }
    }

    @Test
    fun `un periodo de evaluacion documental con formato invalido es rechazado`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            EvaluacionDocumental(
                programa = "EGSTI",
                caracteristica = "1.1",
                periodo = "2026-1",
                calificacion = 4.0,
            )
        }
    }

    @Test
    fun `un programa de evaluacion documental en blanco es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            EvaluacionDocumental(
                programa = "  ",
                caracteristica = "1.1",
                periodo = "20261",
                calificacion = 4.0,
            )
        }
    }

    @Test
    fun `una caracteristica de evaluacion documental en blanco es rechazada`() {
        assertThrows<CampoRequeridoVacioException> {
            EvaluacionDocumental(
                programa = "EGSTI",
                caracteristica = "  ",
                periodo = "20261",
                calificacion = 4.0,
            )
        }
    }

    // =========================================================================
    // ValoracionCualitativa
    // =========================================================================

    @Test
    fun `una calificacion de 4,6 a 5,0 se cumple plenamente`() {
        assertEquals(ValoracionCualitativa.SE_CUMPLE_PLENAMENTE, evaluacionConCalificacion(4.6).valoracionCualitativa)
        assertEquals(ValoracionCualitativa.SE_CUMPLE_PLENAMENTE, evaluacionConCalificacion(5.0).valoracionCualitativa)
    }

    @Test
    fun `una calificacion de 4,0 a 4,5 se cumple en alto grado`() {
        assertEquals(
            ValoracionCualitativa.SE_CUMPLE_EN_ALTO_GRADO,
            evaluacionConCalificacion(4.0).valoracionCualitativa,
        )
        assertEquals(
            ValoracionCualitativa.SE_CUMPLE_EN_ALTO_GRADO,
            evaluacionConCalificacion(4.5).valoracionCualitativa,
        )
    }

    @Test
    fun `una calificacion de 3,0 a 3,9 se cumple aceptablemente`() {
        assertEquals(
            ValoracionCualitativa.SE_CUMPLE_ACEPTABLEMENTE,
            evaluacionConCalificacion(3.0).valoracionCualitativa,
        )
        assertEquals(
            ValoracionCualitativa.SE_CUMPLE_ACEPTABLEMENTE,
            evaluacionConCalificacion(3.9).valoracionCualitativa,
        )
    }

    @Test
    fun `una calificacion de 2,0 a 2,9 se cumple insatisfactoriamente`() {
        assertEquals(
            ValoracionCualitativa.SE_CUMPLE_INSATISFACTORIAMENTE,
            evaluacionConCalificacion(2.0).valoracionCualitativa,
        )
        assertEquals(
            ValoracionCualitativa.SE_CUMPLE_INSATISFACTORIAMENTE,
            evaluacionConCalificacion(2.9).valoracionCualitativa,
        )
    }

    @Test
    fun `una calificacion de 1,0 a 1,9 no se cumple`() {
        assertEquals(ValoracionCualitativa.NO_SE_CUMPLE, evaluacionConCalificacion(1.0).valoracionCualitativa)
        assertEquals(ValoracionCualitativa.NO_SE_CUMPLE, evaluacionConCalificacion(1.9).valoracionCualitativa)
    }

    @Test
    fun `la calificacion se redondea a un decimal con la regla mitad hacia arriba antes de derivar la valoracion`() {
        // 4,55 redondea a 4,6 (mitad hacia arriba): pasa de "alto grado" a "plenamente".
        assertEquals(ValoracionCualitativa.SE_CUMPLE_PLENAMENTE, evaluacionConCalificacion(4.55).valoracionCualitativa)
        // 4,45 redondea a 4,5: sigue en "alto grado".
        assertEquals(
            ValoracionCualitativa.SE_CUMPLE_EN_ALTO_GRADO,
            evaluacionConCalificacion(4.45).valoracionCualitativa,
        )
    }

    @Test
    fun `la calificacion se almacena sin redondear, el redondeo solo se usa para la valoracion`() {
        val evaluacion = evaluacionConCalificacion(4.4667)

        assertEquals(4.4667, evaluacion.calificacion)
        assertEquals(ValoracionCualitativa.SE_CUMPLE_EN_ALTO_GRADO, evaluacion.valoracionCualitativa)
    }

    // =========================================================================
    // Caracteristica
    // =========================================================================

    @Test
    fun `una caracteristica valida se construye correctamente`() {
        val caracteristica = Caracteristica(
            id = "1.1",
            nombre = "Coherencia con la misión",
            factor = "F1",
            ponderacion = 0.5,
        )

        assertEquals("F1", caracteristica.factor)
        assertEquals(0.5, caracteristica.ponderacion)
    }

    @Test
    fun `una ponderacion de caracteristica negativa es rechazada`() {
        assertThrows<PonderacionNegativaException> {
            Caracteristica(id = "1.1", nombre = "Coherencia con la misión", factor = "F1", ponderacion = -0.1)
        }
    }

    @Test
    fun `un id de caracteristica en blanco es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            Caracteristica(id = "  ", nombre = "Coherencia con la misión", factor = "F1", ponderacion = 0.5)
        }
    }

    @Test
    fun `un factor de caracteristica en blanco es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            Caracteristica(id = "1.1", nombre = "Coherencia con la misión", factor = "  ", ponderacion = 0.5)
        }
    }

    // =========================================================================
    // ConfiguracionPonderaciones
    // =========================================================================

    @Test
    fun `una configuracion de ponderaciones valida se construye correctamente`() {
        val configuracion = ConfiguracionPonderaciones(
            caracteristicas = listOf(
                Caracteristica(id = "1.1", nombre = "C1", factor = "F1", ponderacion = 0.6),
                Caracteristica(id = "1.2", nombre = "C2", factor = "F1", ponderacion = 0.4),
                Caracteristica(id = "2.1", nombre = "C3", factor = "F2", ponderacion = 1.0),
            ),
            pesosFactores = mapOf("F1" to 0.7, "F2" to 0.3),
            pesoEncuestas = 0.4,
            pesoDocumental = 0.6,
        )

        assertEquals(0.4, configuracion.pesoEncuestas)
        assertEquals(0.6, configuracion.pesoDocumental)
    }

    @Test
    fun `una tolerancia de 0,001 en la suma de pesos de factores es aceptada`() {
        val configuracion = configuracionConPesosFactores(mapOf("F1" to 0.5005, "F2" to 0.4995))

        assertEquals(1.0, configuracion.pesosFactores.values.sum(), 1e-9)
    }

    @Test
    fun `los pesos de factores que no suman 1,0 son rechazados`() {
        assertThrows<SumaPonderacionesInvalidaException> {
            configuracionConPesosFactores(mapOf("F1" to 0.5, "F2" to 0.3))
        }
    }

    @Test
    fun `un peso de factor negativo es rechazado`() {
        assertThrows<PonderacionNegativaException> {
            configuracionConPesosFactores(mapOf("F1" to 1.3, "F2" to -0.3))
        }
    }

    @Test
    fun `las ponderaciones de caracteristicas de un mismo factor que no suman 1,0 son rechazadas`() {
        assertThrows<SumaPonderacionesInvalidaException> {
            ConfiguracionPonderaciones(
                caracteristicas = listOf(
                    Caracteristica(id = "1.1", nombre = "C1", factor = "F1", ponderacion = 0.6),
                    Caracteristica(id = "1.2", nombre = "C2", factor = "F1", ponderacion = 0.3),
                ),
                pesosFactores = mapOf("F1" to 1.0),
                pesoEncuestas = 0.4,
                pesoDocumental = 0.6,
            )
        }
    }

    @Test
    fun `un peso de la mezcla encuestas-documental negativo es rechazado`() {
        assertThrows<PonderacionNegativaException> {
            configuracionConMezcla(pesoEncuestas = -0.4, pesoDocumental = 0.6)
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private fun configuracionConPesosFactores(pesosFactores: Map<String, Double>): ConfiguracionPonderaciones =
        ConfiguracionPonderaciones(
            caracteristicas = pesosFactores.keys.map { factor ->
                Caracteristica(id = "$factor.1", nombre = "C-$factor", factor = factor, ponderacion = 1.0)
            },
            pesosFactores = pesosFactores,
            pesoEncuestas = 0.4,
            pesoDocumental = 0.6,
        )

    private fun configuracionConMezcla(pesoEncuestas: Double, pesoDocumental: Double): ConfiguracionPonderaciones =
        ConfiguracionPonderaciones(
            caracteristicas = listOf(Caracteristica(id = "1.1", nombre = "C1", factor = "F1", ponderacion = 1.0)),
            pesosFactores = mapOf("F1" to 1.0),
            pesoEncuestas = pesoEncuestas,
            pesoDocumental = pesoDocumental,
        )

    private fun evaluacionConCalificacion(calificacion: Double): EvaluacionDocumental =
        EvaluacionDocumental(
            programa = "EGSTI",
            caracteristica = "1.1",
            periodo = "20261",
            calificacion = calificacion,
        )

    private fun resultadoConPromedio(promedio: Double?): ResultadoEncuestaPregunta =
        ResultadoEncuestaPregunta(
            programa = "EGSTI",
            periodo = "20261",
            codigoPregunta = "A2",
            caracteristica = "1.1",
            estamentos = setOf(TipoEstamento.ESTUDIANTE),
            promedio = promedio,
        )

    private fun respuestaConCalificacion(calificacion: Int): RespuestaEncuesta =
        RespuestaEncuesta(
            id = "R001",
            estamento = TipoEstamento.ESTUDIANTE,
            factor = "Infraestructura",
            calificacion = calificacion,
            periodo = "20261",
        )

    private fun estudianteConCohorte(cohorte: String): Estudiante =
        Estudiante(
            id = "EST-001",
            programa = "Ingeniería de Sistemas",
            facultad = "Ingeniería",
            cohorte = cohorte,
            estado = EstadoEstudiante.MATRICULADO,
        )

    private fun estudianteConPuntaje(puntaje: Int): Estudiante =
        Estudiante(
            id = "EST-001",
            programa = "Ingeniería de Sistemas",
            facultad = "Ingeniería",
            cohorte = "20211",
            estado = EstadoEstudiante.GRADUADO,
            puntajeSaberPro = puntaje,
        )
}

package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Caracteristica
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.infrastructure.parsing.CsvDocumentParser
import com.example.sgva.usecases.CalcularCapacidadInstaladaUseCase
import com.example.sgva.usecases.CalcularDistribucionFormacionUseCase
import com.example.sgva.usecases.CalcularEvolucionMatriculaUseCase
import com.example.sgva.usecases.CalcularPonderacionLikertUseCase
import com.example.sgva.usecases.CalcularResultadoAutoevaluacionUseCase
import com.example.sgva.usecases.CalcularTasaDesercionUseCase
import com.example.sgva.usecases.ConsolidarPuntajesSaberProUseCase
import com.example.sgva.usecases.ConsultarDocentesUseCase
import com.example.sgva.usecases.ConsultarEstudiantesUseCase
import com.example.sgva.usecases.ConsultarEvaluacionDocumentalUseCase
import com.example.sgva.usecases.ConsultarResultadoEncuestaPreguntaUseCase
import com.example.sgva.usecases.ConsultarRespuestasEncuestaUseCase
import com.example.sgva.usecases.GenerarMatrizRadarUseCase
import com.example.sgva.usecases.GenerarReporteEjecutivoUseCase
import com.example.sgva.usecases.ProcesarDatosDocentesUseCase
import com.example.sgva.usecases.ProcesarDatosEstudiantesUseCase
import com.example.sgva.usecases.ProcesarEvaluacionesDocumentalesUseCase
import com.example.sgva.usecases.ProcesarResultadosEncuestaPreguntaUseCase
import com.example.sgva.usecases.ProcesarRespuestasEncuestasUseCase
import com.example.sgva.usecases.RegistrarDocenteUseCase
import com.example.sgva.usecases.RegistrarEstudianteUseCase
import com.example.sgva.usecases.RegistrarEvaluacionDocumentalUseCase
import com.example.sgva.usecases.RegistrarResultadoEncuestaPreguntaUseCase
import com.example.sgva.usecases.RegistrarRespuestaEncuestaUseCase
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/**
 * Cableado de los casos de uso para las pruebas de integración con `MockMvc`
 * de `infrastructure.web`. Replica `ConfiguracionCasosDeUso` de producción,
 * pero sobre [RepositorioAcademicoFalso] —el mismo doble ya usado en las
 * pruebas de casos de uso (`com.example.sgva.application`)— y con un reloj
 * fijo, para que las pruebas sean deterministas y no dependan de un
 * adaptador de persistencia real ni de la fecha del sistema.
 */
@TestConfiguration
class ConfiguracionCasosDeUsoDePrueba {

    @Bean
    fun repositorio(): RepositorioAcademicoFalso = RepositorioAcademicoFalso()

    @Bean
    fun parsers(): List<DocumentParserPort> = listOf(CsvDocumentParser())

    /** Reloj fijo (mismo instante usado en `CalcularEvolucionMatriculaUseCaseTest`). */
    @Bean
    fun reloj(): Clock = Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC)

    @Bean
    fun registrarEstudianteUseCase(repositorio: AcademicDataRepository): RegistrarEstudianteUseCase =
        RegistrarEstudianteUseCase(repositorio)

    @Bean
    fun consultarEstudiantesUseCase(repositorio: AcademicDataRepository): ConsultarEstudiantesUseCase =
        ConsultarEstudiantesUseCase(repositorio)

    @Bean
    fun procesarDatosEstudiantesUseCase(
        parsers: List<DocumentParserPort>,
        registrarEstudianteUseCase: RegistrarEstudianteUseCase,
    ): ProcesarDatosEstudiantesUseCase =
        ProcesarDatosEstudiantesUseCase(parsers, registrarEstudianteUseCase)

    @Bean
    fun registrarDocenteUseCase(repositorio: AcademicDataRepository): RegistrarDocenteUseCase =
        RegistrarDocenteUseCase(repositorio)

    @Bean
    fun consultarDocentesUseCase(repositorio: AcademicDataRepository): ConsultarDocentesUseCase =
        ConsultarDocentesUseCase(repositorio)

    @Bean
    fun procesarDatosDocentesUseCase(
        parsers: List<DocumentParserPort>,
        registrarDocenteUseCase: RegistrarDocenteUseCase,
    ): ProcesarDatosDocentesUseCase =
        ProcesarDatosDocentesUseCase(parsers, registrarDocenteUseCase)

    @Bean
    fun calcularEvolucionMatriculaUseCase(
        repositorio: AcademicDataRepository,
        reloj: Clock,
    ): CalcularEvolucionMatriculaUseCase =
        CalcularEvolucionMatriculaUseCase(repositorio, reloj)

    @Bean
    fun calcularTasaDesercionUseCase(repositorio: AcademicDataRepository): CalcularTasaDesercionUseCase =
        CalcularTasaDesercionUseCase(repositorio)

    @Bean
    fun consolidarPuntajesSaberProUseCase(repositorio: AcademicDataRepository): ConsolidarPuntajesSaberProUseCase =
        ConsolidarPuntajesSaberProUseCase(repositorio)

    @Bean
    fun calcularDistribucionFormacionUseCase(
        repositorio: AcademicDataRepository,
    ): CalcularDistribucionFormacionUseCase =
        CalcularDistribucionFormacionUseCase(repositorio)

    @Bean
    fun calcularCapacidadInstaladaUseCase(
        repositorio: AcademicDataRepository,
    ): CalcularCapacidadInstaladaUseCase =
        CalcularCapacidadInstaladaUseCase(repositorio)

    @Bean
    fun registrarRespuestaEncuestaUseCase(repositorio: AcademicDataRepository): RegistrarRespuestaEncuestaUseCase =
        RegistrarRespuestaEncuestaUseCase(repositorio)

    @Bean
    fun consultarRespuestasEncuestaUseCase(repositorio: AcademicDataRepository): ConsultarRespuestasEncuestaUseCase =
        ConsultarRespuestasEncuestaUseCase(repositorio)

    @Bean
    fun procesarRespuestasEncuestasUseCase(
        parsers: List<DocumentParserPort>,
        registrarRespuestaEncuestaUseCase: RegistrarRespuestaEncuestaUseCase,
    ): ProcesarRespuestasEncuestasUseCase =
        ProcesarRespuestasEncuestasUseCase(parsers, registrarRespuestaEncuestaUseCase)

    @Bean
    fun registrarResultadoEncuestaPreguntaUseCase(
        repositorio: AcademicDataRepository,
    ): RegistrarResultadoEncuestaPreguntaUseCase =
        RegistrarResultadoEncuestaPreguntaUseCase(repositorio)

    @Bean
    fun consultarResultadoEncuestaPreguntaUseCase(
        repositorio: AcademicDataRepository,
    ): ConsultarResultadoEncuestaPreguntaUseCase =
        ConsultarResultadoEncuestaPreguntaUseCase(repositorio)

    @Bean
    fun procesarResultadosEncuestaPreguntaUseCase(
        parsers: List<DocumentParserPort>,
        registrarResultadoEncuestaPreguntaUseCase: RegistrarResultadoEncuestaPreguntaUseCase,
    ): ProcesarResultadosEncuestaPreguntaUseCase =
        ProcesarResultadosEncuestaPreguntaUseCase(parsers, registrarResultadoEncuestaPreguntaUseCase)

    @Bean
    fun registrarEvaluacionDocumentalUseCase(
        repositorio: AcademicDataRepository,
    ): RegistrarEvaluacionDocumentalUseCase =
        RegistrarEvaluacionDocumentalUseCase(repositorio)

    @Bean
    fun consultarEvaluacionDocumentalUseCase(
        repositorio: AcademicDataRepository,
    ): ConsultarEvaluacionDocumentalUseCase =
        ConsultarEvaluacionDocumentalUseCase(repositorio)

    @Bean
    fun procesarEvaluacionesDocumentalesUseCase(
        parsers: List<DocumentParserPort>,
        registrarEvaluacionDocumentalUseCase: RegistrarEvaluacionDocumentalUseCase,
    ): ProcesarEvaluacionesDocumentalesUseCase =
        ProcesarEvaluacionesDocumentalesUseCase(parsers, registrarEvaluacionDocumentalUseCase)

    @Bean
    fun calcularPonderacionLikertUseCase(repositorio: AcademicDataRepository): CalcularPonderacionLikertUseCase =
        CalcularPonderacionLikertUseCase(repositorio)

    @Bean
    fun generarMatrizRadarUseCase(
        calcularPonderacionLikertUseCase: CalcularPonderacionLikertUseCase,
    ): GenerarMatrizRadarUseCase =
        GenerarMatrizRadarUseCase(calcularPonderacionLikertUseCase)

    @Bean
    fun generarReporteEjecutivoUseCase(
        calcularEvolucionMatriculaUseCase: CalcularEvolucionMatriculaUseCase,
        calcularTasaDesercionUseCase: CalcularTasaDesercionUseCase,
        consolidarPuntajesSaberProUseCase: ConsolidarPuntajesSaberProUseCase,
        calcularDistribucionFormacionUseCase: CalcularDistribucionFormacionUseCase,
        calcularCapacidadInstaladaUseCase: CalcularCapacidadInstaladaUseCase,
        consultarRespuestasEncuestaUseCase: ConsultarRespuestasEncuestaUseCase,
        calcularResultadoAutoevaluacionUseCase: CalcularResultadoAutoevaluacionUseCase,
    ): GenerarReporteEjecutivoUseCase =
        GenerarReporteEjecutivoUseCase(
            calcularEvolucionMatriculaUseCase,
            calcularTasaDesercionUseCase,
            consolidarPuntajesSaberProUseCase,
            calcularDistribucionFormacionUseCase,
            calcularCapacidadInstaladaUseCase,
            consultarRespuestasEncuestaUseCase,
            calcularResultadoAutoevaluacionUseCase,
        )

    /**
     * Catálogo sintético de ponderaciones (mismo usado en
     * `CalcularResultadoAutoevaluacionUseCaseTest`): dos factores (F1 con
     * C1/C2, F2 con C3/C4) y una mezcla encuestas/documental de 40 %/60 %. No
     * corresponde al modelo 2025 real.
     */
    @Bean
    fun configuracionPonderaciones(): ConfiguracionPonderaciones = ConfiguracionPonderaciones(
        caracteristicas = listOf(
            Caracteristica(id = "C1", nombre = "Característica 1", factor = "F1", ponderacion = 0.5),
            Caracteristica(id = "C2", nombre = "Característica 2", factor = "F1", ponderacion = 0.5),
            Caracteristica(id = "C3", nombre = "Característica 3", factor = "F2", ponderacion = 0.3),
            Caracteristica(id = "C4", nombre = "Característica 4", factor = "F2", ponderacion = 0.7),
        ),
        pesosFactores = mapOf("F1" to 0.6, "F2" to 0.4),
        pesoEncuestas = 0.4,
        pesoDocumental = 0.6,
    )

    @Bean
    fun calcularResultadoAutoevaluacionUseCase(
        repositorio: AcademicDataRepository,
        configuracionPonderaciones: ConfiguracionPonderaciones,
    ): CalcularResultadoAutoevaluacionUseCase =
        CalcularResultadoAutoevaluacionUseCase(repositorio, configuracionPonderaciones)
}

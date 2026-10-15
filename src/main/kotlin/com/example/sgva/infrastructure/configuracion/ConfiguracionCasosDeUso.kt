package com.example.sgva.infrastructure.configuracion

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.ConfiguracionPonderacionesRepository
import com.example.sgva.domain.ConfiguracionSeguimiento
import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.domain.LectorConfiguracionPonderacionesPort
import com.example.sgva.usecases.CalcularAvanceCicloUseCase
import com.example.sgva.usecases.CalcularCapacidadInstaladaUseCase
import com.example.sgva.usecases.CalcularDistribucionFormacionUseCase
import com.example.sgva.usecases.CalcularEvolucionMatriculaUseCase
import com.example.sgva.usecases.CalcularPonderacionLikertUseCase
import com.example.sgva.usecases.CalcularResultadoAutoevaluacionUseCase
import com.example.sgva.usecases.CalcularTasaDesercionUseCase
import com.example.sgva.usecases.CargarConfiguracionPonderacionesUseCase
import com.example.sgva.usecases.CompararResultadosEntreCiclosUseCase
import com.example.sgva.usecases.ConsolidarPreevaluacionesUseCase
import com.example.sgva.usecases.ConsolidarPuntajesSaberProUseCase
import com.example.sgva.usecases.ConsultarCicloAutoevaluacionUseCase
import com.example.sgva.usecases.ConsultarConfiguracionPonderacionesUseCase
import com.example.sgva.usecases.ConsultarDocentesUseCase
import com.example.sgva.usecases.ConsultarEstudiantesUseCase
import com.example.sgva.usecases.ConsultarEstadoCicloAutoevaluacionUseCase
import com.example.sgva.usecases.ConsultarEvaluacionDocumentalUseCase
import com.example.sgva.usecases.ConsultarGrupoEvaluadorUseCase
import com.example.sgva.usecases.ConsultarParticipacionActorUseCase
import com.example.sgva.usecases.ConsultarPreevaluacionUseCase
import com.example.sgva.usecases.ConsultarProyectoMejoramientoUseCase
import com.example.sgva.usecases.ConsultarResultadoEncuestaPreguntaUseCase
import com.example.sgva.usecases.ConsultarRespuestasEncuestaUseCase
import com.example.sgva.usecases.GenerarMatrizRadarUseCase
import com.example.sgva.usecases.GenerarReporteEjecutivoUseCase
import com.example.sgva.usecases.ProcesarDatosDocentesUseCase
import com.example.sgva.usecases.ProcesarDatosEstudiantesUseCase
import com.example.sgva.usecases.ProcesarEvaluacionesDocumentalesUseCase
import com.example.sgva.usecases.ProcesarParticipacionActorUseCase
import com.example.sgva.usecases.ProcesarPreevaluacionesUseCase
import com.example.sgva.usecases.ProcesarResultadosEncuestaPreguntaUseCase
import com.example.sgva.usecases.ProcesarRespuestasEncuestasUseCase
import com.example.sgva.usecases.RegistrarActividadCicloUseCase
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import com.example.sgva.usecases.RegistrarDocenteUseCase
import com.example.sgva.usecases.RegistrarEstudianteUseCase
import com.example.sgva.usecases.RegistrarEvaluacionDocumentalUseCase
import com.example.sgva.usecases.RegistrarGrupoEvaluadorUseCase
import com.example.sgva.usecases.RegistrarParticipacionActorUseCase
import com.example.sgva.usecases.RegistrarPreevaluacionUseCase
import com.example.sgva.usecases.RegistrarProyectoMejoramientoUseCase
import com.example.sgva.usecases.RegistrarResultadoEncuestaPreguntaUseCase
import com.example.sgva.usecases.RegistrarRespuestaEncuestaUseCase
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/**
 * Composición (wiring) de los casos de uso como beans de Spring.
 *
 * Los casos de uso son Kotlin puro y no se anotan con `@Component`/`@Service`
 * (lo prohíbe `CLAUDE.md` y lo verifica `ArchitectureTest`). Es la
 * infraestructura la que los ensambla aquí, inyectándoles el puerto
 * [AcademicDataRepository], cuyo adaptador en memoria sí es un bean.
 */
@Configuration
class ConfiguracionCasosDeUso {

    @Bean
    fun registrarEstudianteUseCase(repositorio: AcademicDataRepository): RegistrarEstudianteUseCase =
        RegistrarEstudianteUseCase(repositorio)

    @Bean
    fun consultarEstudiantesUseCase(repositorio: AcademicDataRepository): ConsultarEstudiantesUseCase =
        ConsultarEstudiantesUseCase(repositorio)

    @Bean
    fun registrarDocenteUseCase(repositorio: AcademicDataRepository): RegistrarDocenteUseCase =
        RegistrarDocenteUseCase(repositorio)

    @Bean
    fun consultarDocentesUseCase(repositorio: AcademicDataRepository): ConsultarDocentesUseCase =
        ConsultarDocentesUseCase(repositorio)

    @Bean
    fun procesarDatosEstudiantesUseCase(
        parsers: List<DocumentParserPort>,
        registrarEstudianteUseCase: RegistrarEstudianteUseCase,
    ): ProcesarDatosEstudiantesUseCase =
        ProcesarDatosEstudiantesUseCase(parsers, registrarEstudianteUseCase)

    @Bean
    fun procesarDatosDocentesUseCase(
        parsers: List<DocumentParserPort>,
        registrarDocenteUseCase: RegistrarDocenteUseCase,
    ): ProcesarDatosDocentesUseCase =
        ProcesarDatosDocentesUseCase(parsers, registrarDocenteUseCase)

    /** Reloj del sistema; se aísla en un bean para poder fijarlo en pruebas. */
    @Bean
    fun reloj(): Clock = Clock.systemDefaultZone()

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
        consultarEstadoCicloAutoevaluacionUseCase: ConsultarEstadoCicloAutoevaluacionUseCase,
        reloj: Clock,
    ): GenerarReporteEjecutivoUseCase =
        GenerarReporteEjecutivoUseCase(
            calcularEvolucionMatriculaUseCase,
            calcularTasaDesercionUseCase,
            consolidarPuntajesSaberProUseCase,
            calcularDistribucionFormacionUseCase,
            calcularCapacidadInstaladaUseCase,
            consultarRespuestasEncuestaUseCase,
            calcularResultadoAutoevaluacionUseCase,
            consultarEstadoCicloAutoevaluacionUseCase,
            reloj,
        )

    @Bean
    fun lectorConfiguracionPonderaciones(): LectorConfiguracionPonderacionesPort = CargadorConfiguracionPonderaciones()

    @Bean
    fun consultarConfiguracionPonderacionesUseCase(
        repositorio: ConfiguracionPonderacionesRepository,
    ): ConsultarConfiguracionPonderacionesUseCase =
        ConsultarConfiguracionPonderacionesUseCase(repositorio)

    @Bean
    fun cargarConfiguracionPonderacionesUseCase(
        lector: LectorConfiguracionPonderacionesPort,
        repositorio: ConfiguracionPonderacionesRepository,
    ): CargarConfiguracionPonderacionesUseCase =
        CargarConfiguracionPonderacionesUseCase(lector, repositorio)

    /**
     * El resultado de autoevaluación consulta en cada cálculo la configuración
     * de ponderaciones vigente en la base de datos (`DESIGN_SGVA.md` §0.1): no hay ningún valor
     * por defecto, y mientras no se cargue el archivo de ponderaciones el cálculo responde con un
     * mensaje claro, sin impedir el arranque de la aplicación.
     */
    @Bean
    fun calcularResultadoAutoevaluacionUseCase(
        repositorio: AcademicDataRepository,
        consultarConfiguracionPonderaciones: ConsultarConfiguracionPonderacionesUseCase,
    ): CalcularResultadoAutoevaluacionUseCase =
        CalcularResultadoAutoevaluacionUseCase(repositorio, consultarConfiguracionPonderaciones::ejecutar)

    // ===== Módulo 5 — Seguimiento del proceso institucional (V6) =====

    /**
     * Umbrales del semáforo de participación y días de "próxima a vencer"
     * (`DESIGN_SGVA.md` §0.1, V6): criterio del investigador, no de la
     * fuente institucional, por lo que sí tienen un valor por defecto
     * documentado en `application.properties` (a diferencia de las
     * ponderaciones del modelo 2025, que no lo tienen).
     */
    @Bean
    fun configuracionSeguimiento(
        @Value("\${sgva.seguimiento.umbral-rojo-naranja}") umbralRojoNaranja: Double,
        @Value("\${sgva.seguimiento.umbral-naranja-verde}") umbralNaranjaVerde: Double,
        @Value("\${sgva.seguimiento.dias-proxima-a-vencer}") diasProximaAVencer: Int,
    ): ConfiguracionSeguimiento =
        ConfiguracionSeguimiento(umbralRojoNaranja, umbralNaranjaVerde, diasProximaAVencer)

    @Bean
    fun registrarCicloAutoevaluacionUseCase(
        repositorio: AcademicDataRepository,
    ): RegistrarCicloAutoevaluacionUseCase =
        RegistrarCicloAutoevaluacionUseCase(repositorio)

    @Bean
    fun registrarActividadCicloUseCase(repositorio: AcademicDataRepository): RegistrarActividadCicloUseCase =
        RegistrarActividadCicloUseCase(repositorio)

    @Bean
    fun consultarCicloAutoevaluacionUseCase(
        repositorio: AcademicDataRepository,
    ): ConsultarCicloAutoevaluacionUseCase =
        ConsultarCicloAutoevaluacionUseCase(repositorio)

    @Bean
    fun registrarGrupoEvaluadorUseCase(repositorio: AcademicDataRepository): RegistrarGrupoEvaluadorUseCase =
        RegistrarGrupoEvaluadorUseCase(repositorio)

    @Bean
    fun consultarGrupoEvaluadorUseCase(repositorio: AcademicDataRepository): ConsultarGrupoEvaluadorUseCase =
        ConsultarGrupoEvaluadorUseCase(repositorio)

    @Bean
    fun registrarPreevaluacionUseCase(repositorio: AcademicDataRepository): RegistrarPreevaluacionUseCase =
        RegistrarPreevaluacionUseCase(repositorio)

    @Bean
    fun consultarPreevaluacionUseCase(repositorio: AcademicDataRepository): ConsultarPreevaluacionUseCase =
        ConsultarPreevaluacionUseCase(repositorio)

    @Bean
    fun procesarPreevaluacionesUseCase(
        parsers: List<DocumentParserPort>,
        registrarPreevaluacionUseCase: RegistrarPreevaluacionUseCase,
    ): ProcesarPreevaluacionesUseCase =
        ProcesarPreevaluacionesUseCase(parsers, registrarPreevaluacionUseCase)

    @Bean
    fun consolidarPreevaluacionesUseCase(repositorio: AcademicDataRepository): ConsolidarPreevaluacionesUseCase =
        ConsolidarPreevaluacionesUseCase(repositorio)

    @Bean
    fun registrarParticipacionActorUseCase(
        repositorio: AcademicDataRepository,
    ): RegistrarParticipacionActorUseCase =
        RegistrarParticipacionActorUseCase(repositorio)

    @Bean
    fun consultarParticipacionActorUseCase(
        repositorio: AcademicDataRepository,
    ): ConsultarParticipacionActorUseCase =
        ConsultarParticipacionActorUseCase(repositorio)

    @Bean
    fun procesarParticipacionActorUseCase(
        parsers: List<DocumentParserPort>,
        registrarParticipacionActorUseCase: RegistrarParticipacionActorUseCase,
    ): ProcesarParticipacionActorUseCase =
        ProcesarParticipacionActorUseCase(parsers, registrarParticipacionActorUseCase)

    @Bean
    fun registrarProyectoMejoramientoUseCase(
        repositorio: AcademicDataRepository,
    ): RegistrarProyectoMejoramientoUseCase =
        RegistrarProyectoMejoramientoUseCase(repositorio)

    @Bean
    fun consultarProyectoMejoramientoUseCase(
        repositorio: AcademicDataRepository,
    ): ConsultarProyectoMejoramientoUseCase =
        ConsultarProyectoMejoramientoUseCase(repositorio)

    @Bean
    fun calcularAvanceCicloUseCase(
        repositorio: AcademicDataRepository,
        consultarConfiguracionPonderaciones: ConsultarConfiguracionPonderacionesUseCase,
        configuracionSeguimiento: ConfiguracionSeguimiento,
    ): CalcularAvanceCicloUseCase =
        CalcularAvanceCicloUseCase(repositorio, consultarConfiguracionPonderaciones::ejecutar, configuracionSeguimiento)

    @Bean
    fun compararResultadosEntreCiclosUseCase(
        calcularResultadoAutoevaluacionUseCase: CalcularResultadoAutoevaluacionUseCase,
    ): CompararResultadosEntreCiclosUseCase =
        CompararResultadosEntreCiclosUseCase(calcularResultadoAutoevaluacionUseCase)

    @Bean
    fun consultarEstadoCicloAutoevaluacionUseCase(
        consultarCicloAutoevaluacionUseCase: ConsultarCicloAutoevaluacionUseCase,
        calcularAvanceCicloUseCase: CalcularAvanceCicloUseCase,
        consultarProyectoMejoramientoUseCase: ConsultarProyectoMejoramientoUseCase,
    ): ConsultarEstadoCicloAutoevaluacionUseCase =
        ConsultarEstadoCicloAutoevaluacionUseCase(
            consultarCicloAutoevaluacionUseCase,
            calcularAvanceCicloUseCase,
            consultarProyectoMejoramientoUseCase,
        )
}

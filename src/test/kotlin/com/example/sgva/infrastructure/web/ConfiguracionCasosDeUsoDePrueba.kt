package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.infrastructure.parsing.CsvDocumentParser
import com.example.sgva.usecases.CalcularCapacidadInstaladaUseCase
import com.example.sgva.usecases.CalcularDistribucionFormacionUseCase
import com.example.sgva.usecases.CalcularEvolucionMatriculaUseCase
import com.example.sgva.usecases.CalcularTasaDesercionUseCase
import com.example.sgva.usecases.ConsolidarPuntajesSaberProUseCase
import com.example.sgva.usecases.ConsultarDocentesUseCase
import com.example.sgva.usecases.ConsultarEstudiantesUseCase
import com.example.sgva.usecases.ProcesarDatosDocentesUseCase
import com.example.sgva.usecases.ProcesarDatosEstudiantesUseCase
import com.example.sgva.usecases.RegistrarDocenteUseCase
import com.example.sgva.usecases.RegistrarEstudianteUseCase
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
}

package com.example.sgva.infrastructure.configuracion

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.usecases.ConsultarDocentesUseCase
import com.example.sgva.usecases.ConsultarEstudiantesUseCase
import com.example.sgva.usecases.ProcesarDatosDocentesUseCase
import com.example.sgva.usecases.ProcesarDatosEstudiantesUseCase
import com.example.sgva.usecases.RegistrarDocenteUseCase
import com.example.sgva.usecases.RegistrarEstudianteUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
}

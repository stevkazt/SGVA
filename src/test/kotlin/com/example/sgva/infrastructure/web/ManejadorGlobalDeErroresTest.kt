package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Pruebas dedicadas al contrato de [ManejadorGlobalDeErrores]: un test por
 * cada rama de traducción de excepción a código HTTP. Usa
 * [EstudianteController] como vehículo (cualquier controlador serviría, ya
 * que el manejador es transversal), complementando los casos ya cubiertos
 * incidentalmente en [EstudianteControllerTest], [DocenteControllerTest] e
 * [IndicadorControllerTest] con el caso que ningún otro test ejercita: un
 * cuerpo JSON sintácticamente inválido (sin causa de dominio).
 */
@WebMvcTest(controllers = [EstudianteController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class ManejadorGlobalDeErroresTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `EntidadDuplicadaException se traduce a 409 con el mensaje de dominio`() {
        repositorio.guardarEstudiante(
            Estudiante(
                id = "E1",
                programa = "X",
                facultad = "Ingeniería",
                cohorte = "20221",
                estado = EstadoEstudiante.MATRICULADO,
            ),
        )

        mockMvc.perform(
            post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"E1","programa":"Y","facultad":"Ingeniería",
                        "cohorte":"20221","estado":"MATRICULADO"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.mensaje").value("Ya existe un registro de estudiante con id 'E1'."))
    }

    @Test
    fun `DominioException lanzada directamente por un caso de uso (no durante el binding) se traduce a 400`() {
        // FormatoArchivoInvalidoException la lanza ProcesarDatosEstudiantesUseCase.ejecutar()
        // desde el cuerpo del método del controlador, no durante la deserialización del JSON:
        // ejercita la rama `manejarDominio`, distinta de `manejarCuerpoInvalido`.
        val archivo = MockMultipartFile("archivo", "estudiantes.txt", "text/plain", "id\nE1".toByteArray())

        mockMvc.perform(multipart("/api/estudiantes/archivos").file(archivo))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `una violacion de invariante de dominio durante el binding del cuerpo JSON conserva el mensaje de dominio`() {
        // El `init` de Estudiante lanza PuntajeSaberProFueraDeRangoException mientras Jackson
        // construye el objeto: HttpMessageNotReadableException envuelve esa causa de dominio.
        mockMvc.perform(
            post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"E3","programa":"X","facultad":"Ingeniería",
                        "cohorte":"20221","estado":"MATRICULADO","puntajeSaberPro":301}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.mensaje").value(
                    "El puntaje Saber Pro debe estar entre 0 y 300 inclusive; valor recibido: 301.",
                ),
            )
    }

    @Test
    fun `un cuerpo JSON sintacticamente invalido se traduce a 400 con un mensaje generico`() {
        mockMvc.perform(
            post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ esto no es json valido"),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.mensaje").value("El cuerpo de la solicitud no es válido."))
    }
}

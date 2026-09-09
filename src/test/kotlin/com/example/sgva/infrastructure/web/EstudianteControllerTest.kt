package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Pruebas de integración con `MockMvc` de [EstudianteController]: ejercitan el
 * controlador, los casos de uso reales y [ManejadorGlobalDeErrores] juntos,
 * igual que la verificación manual con `curl` hecha durante la
 * implementación, pero incorporada a la suite automatizada.
 */
@WebMvcTest(controllers = [EstudianteController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class EstudianteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `registrar un estudiante valido devuelve 201 con el recurso creado y el header Location`() {
        mockMvc.perform(
            post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"E1","programa":"Ingeniería de Sistemas","facultad":"Ingeniería",
                        "cohorte":"20221","estado":"MATRICULADO","puntajeSaberPro":250}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/estudiantes/E1"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("E1"))
            .andExpect(jsonPath("$.puntajeSaberPro").value(250))

        assertEquals("E1", repositorio.buscarEstudiantePorId("E1")?.id)
    }

    @Test
    fun `registrar un estudiante con id ya existente devuelve 409 sin sobrescribir el original`() {
        repositorio.guardarEstudiante(estudiante(id = "E1", programa = "Original"))

        mockMvc.perform(
            post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"E1","programa":"Otro","facultad":"Ingeniería",
                        "cohorte":"20221","estado":"MATRICULADO"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.mensaje").value("Ya existe un registro de estudiante con id 'E1'."))

        assertEquals("Original", repositorio.buscarEstudiantePorId("E1")?.programa)
    }

    @Test
    fun `registrar un estudiante con cohorte mal formada devuelve 400 con el mensaje de dominio`() {
        mockMvc.perform(
            post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"E2","programa":"X","facultad":"Ingeniería",
                        "cohorte":"2022","estado":"MATRICULADO"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.mensaje").value(
                    "El campo 'cohorte' debe tener formato AAAAS " +
                        "(año de 4 dígitos + semestre 1 o 2, sin separadores); valor recibido: '2022'.",
                ),
            )

        assertTrue(repositorio.listarEstudiantes().isEmpty())
    }

    @Test
    fun `listar devuelve los estudiantes registrados en orden de insercion`() {
        repositorio.guardarEstudiante(estudiante(id = "E1"))
        repositorio.guardarEstudiante(estudiante(id = "E2"))

        mockMvc.perform(get("/api/estudiantes"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("E1"))
            .andExpect(jsonPath("$[1].id").value("E2"))
    }

    @Test
    fun `buscar por id existente devuelve 200 con el estudiante`() {
        repositorio.guardarEstudiante(estudiante(id = "E1"))

        mockMvc.perform(get("/api/estudiantes/E1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("E1"))
    }

    @Test
    fun `buscar por id inexistente devuelve 404`() {
        mockMvc.perform(get("/api/estudiantes/NOEXISTE"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `cargar un archivo csv valido registra los estudiantes y devuelve 201 con el resumen`() {
        val csv = """
            id,programa,facultad,cohorte,estado,puntajeSaberPro
            E100,Sistemas,Ingeniería,20221,MATRICULADO,260
            E101,Sistemas,Ingeniería,20221,DESERTOR,
        """.trimIndent()
        val archivo = MockMultipartFile("archivo", "estudiantes.csv", "text/csv", csv.toByteArray())

        mockMvc.perform(multipart("/api/estudiantes/archivos").file(archivo))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.nombreArchivo").value("estudiantes.csv"))
            .andExpect(jsonPath("$.totalRegistrosLeidos").value(2))
            .andExpect(jsonPath("$.registrados").value(2))
            .andExpect(jsonPath("$.omitidos.length()").value(0))

        assertEquals(2, repositorio.listarEstudiantes().size)
    }

    @Test
    fun `cargar un archivo con formato no soportado devuelve 400 sin registrar nada`() {
        val archivo = MockMultipartFile("archivo", "estudiantes.txt", "text/plain", "id\nE1".toByteArray())

        mockMvc.perform(multipart("/api/estudiantes/archivos").file(archivo))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.mensaje").value(containsString(".csv")))

        assertTrue(repositorio.listarEstudiantes().isEmpty())
    }

    private fun estudiante(
        id: String,
        programa: String = "Ingeniería de Sistemas",
    ): Estudiante = Estudiante(
        id = id,
        programa = programa,
        facultad = "Ingeniería",
        cohorte = "20221",
        estado = EstadoEstudiante.MATRICULADO,
    )
}

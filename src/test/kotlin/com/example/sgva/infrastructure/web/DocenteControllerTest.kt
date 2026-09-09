package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.Docente
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.TipoDedicacion
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
 * Pruebas de integración con `MockMvc` de [DocenteController]: mismo enfoque
 * que [EstudianteControllerTest], ejercitando controlador, casos de uso reales
 * y [ManejadorGlobalDeErrores] juntos.
 */
@WebMvcTest(controllers = [DocenteController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class DocenteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `registrar un docente valido devuelve 201 con el recurso creado y el header Location`() {
        mockMvc.perform(
            post("/api/docentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"D1","facultad":"Ingeniería","nivelFormacion":"DOCTORADO",
                        "dedicacion":"TIEMPO_COMPLETO","periodo":"20221"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/docentes/D1"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("D1"))
            .andExpect(jsonPath("$.nivelFormacion").value("DOCTORADO"))

        assertEquals("D1", repositorio.buscarDocentePorId("D1")?.id)
    }

    @Test
    fun `registrar un docente con id ya existente devuelve 409 sin sobrescribir el original`() {
        repositorio.guardarDocente(docente(id = "D1", nivelFormacion = NivelFormacion.DOCTORADO))

        mockMvc.perform(
            post("/api/docentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"D1","facultad":"Ingeniería","nivelFormacion":"MAESTRIA",
                        "dedicacion":"TIEMPO_COMPLETO","periodo":"20221"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.mensaje").value("Ya existe un registro de docente con id 'D1'."))

        assertEquals(NivelFormacion.DOCTORADO, repositorio.buscarDocentePorId("D1")?.nivelFormacion)
    }

    @Test
    fun `registrar un docente con periodo mal formado devuelve 400 con el mensaje de dominio`() {
        mockMvc.perform(
            post("/api/docentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"D2","facultad":"Ingeniería","nivelFormacion":"MAESTRIA",
                        "dedicacion":"TIEMPO_COMPLETO","periodo":"2022-1"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.mensaje").value(
                    "El campo 'periodo' debe tener formato AAAAS " +
                        "(año de 4 dígitos + semestre 1 o 2, sin separadores); valor recibido: '2022-1'.",
                ),
            )

        assertTrue(repositorio.listarDocentes().isEmpty())
    }

    @Test
    fun `listar devuelve los docentes registrados en orden de insercion`() {
        repositorio.guardarDocente(docente(id = "D1"))
        repositorio.guardarDocente(docente(id = "D2"))

        mockMvc.perform(get("/api/docentes"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("D1"))
            .andExpect(jsonPath("$[1].id").value("D2"))
    }

    @Test
    fun `buscar por id existente devuelve 200 con el docente`() {
        repositorio.guardarDocente(docente(id = "D1"))

        mockMvc.perform(get("/api/docentes/D1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("D1"))
    }

    @Test
    fun `buscar por id inexistente devuelve 404`() {
        mockMvc.perform(get("/api/docentes/NOEXISTE"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `cargar un archivo csv valido registra los docentes y devuelve 201 con el resumen`() {
        val csv = """
            id,facultad,nivelFormacion,dedicacion,periodo
            D100,Ingeniería,DOCTORADO,TIEMPO_COMPLETO,20221
            D101,Ingeniería,MAESTRIA,CATEDRA,20221
        """.trimIndent()
        val archivo = MockMultipartFile("archivo", "docentes.csv", "text/csv", csv.toByteArray())

        mockMvc.perform(multipart("/api/docentes/archivos").file(archivo))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.nombreArchivo").value("docentes.csv"))
            .andExpect(jsonPath("$.totalRegistrosLeidos").value(2))
            .andExpect(jsonPath("$.registrados").value(2))
            .andExpect(jsonPath("$.omitidos.length()").value(0))

        assertEquals(2, repositorio.listarDocentes().size)
    }

    @Test
    fun `cargar un archivo con formato no soportado devuelve 400 sin registrar nada`() {
        val archivo = MockMultipartFile("archivo", "docentes.txt", "text/plain", "id\nD1".toByteArray())

        mockMvc.perform(multipart("/api/docentes/archivos").file(archivo))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.mensaje").value(containsString(".csv")))

        assertTrue(repositorio.listarDocentes().isEmpty())
    }

    private fun docente(
        id: String,
        nivelFormacion: NivelFormacion = NivelFormacion.MAESTRIA,
    ): Docente = Docente(
        id = id,
        facultad = "Ingeniería",
        nivelFormacion = nivelFormacion,
        dedicacion = TipoDedicacion.TIEMPO_COMPLETO,
        periodo = "20221",
    )
}

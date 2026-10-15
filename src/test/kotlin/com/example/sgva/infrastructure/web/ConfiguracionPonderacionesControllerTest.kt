package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioConfiguracionPonderacionesFalso
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ConfiguracionPonderacionesController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class ConfiguracionPonderacionesControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioConfiguracionPonderacionesFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    private fun archivo(nombre: String, contenido: String) =
        MockMultipartFile("archivo", nombre, "text/csv", contenido.toByteArray())

    private val csvValido = """
        tipo,id,factor,nombre,ponderacion
        FACTOR,F1,,,1.0
        CARACTERISTICA,C1,F1,Uno,1.0
        MEZCLA,ENCUESTAS,,,0.4
        MEZCLA,DOCUMENTAL,,,0.6
    """.trimIndent()

    @Test
    fun `consultar sin ponderaciones cargadas devuelve 422 con un mensaje claro`() {
        mockMvc.perform(get("/api/ponderaciones"))
            .andExpect(status().isUnprocessableContent)
            .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("No hay ponderaciones configuradas")))
    }

    @Test
    fun `cargar un archivo valido devuelve 201 y deja la configuracion consultable`() {
        mockMvc.perform(multipart("/api/ponderaciones/archivos").file(archivo("ponderaciones.csv", csvValido)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.nombreArchivo").value("ponderaciones.csv"))
            .andExpect(jsonPath("$.totalRegistrosLeidos").value(4))
            .andExpect(jsonPath("$.registrados").value(4))
            .andExpect(jsonPath("$.omitidos").isEmpty)

        mockMvc.perform(get("/api/ponderaciones"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.pesosFactores.F1").value(1.0))
            .andExpect(jsonPath("$.caracteristicas[0].id").value("C1"))
            .andExpect(jsonPath("$.pesoEncuestas").value(0.4))
            .andExpect(jsonPath("$.pesoDocumental").value(0.6))
    }

    @Test
    fun `cargar un archivo con pesos que no suman uno devuelve 400`() {
        val invalido = csvValido.replace("FACTOR,F1,,,1.0", "FACTOR,F1,,,0.7")

        mockMvc.perform(multipart("/api/ponderaciones/archivos").file(archivo("ponderaciones.csv", invalido)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.mensaje").exists())
    }

    @Test
    fun `cargar un archivo que no es csv devuelve 400`() {
        mockMvc.perform(multipart("/api/ponderaciones/archivos").file(archivo("ponderaciones.xlsx", csvValido)))
            .andExpect(status().isBadRequest)
    }
}

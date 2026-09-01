package com.example.sgva.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EquivalenciaTiempoCompletoTest {

    @Test
    fun `cada dedicacion mapea a su fraccion de jornada de tiempo completo`() {
        assertEquals(1.0, EquivalenciaTiempoCompleto.factor(TipoDedicacion.TIEMPO_COMPLETO))
        assertEquals(0.5, EquivalenciaTiempoCompleto.factor(TipoDedicacion.MEDIO_TIEMPO))
        assertEquals(0.25, EquivalenciaTiempoCompleto.factor(TipoDedicacion.CATEDRA))
    }
}

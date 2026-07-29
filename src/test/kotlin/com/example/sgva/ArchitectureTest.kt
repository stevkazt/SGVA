package com.example.sgva

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.GeneralCodingRules
import org.junit.jupiter.api.Test

class ArchitectureTest {

    private val classes = ClassFileImporter().importPackages("com.example.sgva")

    // =========================================================================
    // 1. REGLAS DE AISLAMIENTO Y DEPEPENDENCIA (CLEAN ARCHITECTURE)
    // =========================================================================

    @Test
    fun `el dominio no debe depender de capas externas ni frameworks`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThroughSkyline()
            .resideInAnyPackage(
                "..usecases..",
                "..infrastructure..",
                "org.springframework..",
                "jakarta..",
                "javax..",
                "com.fasterxml.jackson.."
            )
            .check(classes)
    }

    @Test
    fun `los casos de uso no deben depender de infraestructura ni frameworks`() {
        noClasses()
            .that().resideInAPackage("..usecases..")
            .should().dependOnClassesThroughSkyline()
            .resideInAnyPackage(
                "..infrastructure..",
                "org.springframework..",
                "jakarta..",
                "javax.."
            )
            .check(classes)
    }

    @Test
    fun `el dominio y los casos de uso no deben usar anotaciones de Spring`() {
        noClasses()
            .that().resideInAnyPackage("..domain..", "..usecases..")
            .should().beAnnotatedWith("org.springframework.stereotype.Component")
            .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
            .orShould().beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
            .check(classes)
    }

    // =========================================================================
    // 2. CONVENCIONES DE NOMENCLATURA
    // =========================================================================

    @Test
    fun `las clases de casos de uso deben finalizar en UseCase`() {
        classes()
            .that().resideInAPackage("..usecases..")
            .and().areNotInterfaces()
            .should().haveSimpleNameEndingWith("UseCase")
            .check(classes)
    }

    @Test
    fun `las interfaces de repositorio en el dominio deben finalizar en Repository`() {
        classes()
            .that().resideInAPackage("..domain..")
            .and().areInterfaces()
            .should().haveSimpleNameEndingWith("Repository")
            .check(classes)
    }

    @Test
    fun `las excepciones de dominio deben finalizar en Exception`() {
        classes()
            .that().resideInAPackage("..domain..")
            .and().areAssignableTo(Throwable::class.java)
            .should().haveSimpleNameEndingWith("Exception")
            .check(classes)
    }

    @Test
    fun `los adaptadores rest en infraestructura deben finalizar en Controller`() {
        classes()
            .that().resideInAPackage("..infrastructure..")
            .and().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .should().haveSimpleNameEndingWith("Controller")
            .check(classes)
    }

    // =========================================================================
    // 3. BUENAS PRÁCTICAS DE CÓDIGO
    // =========================================================================

    @Test
    fun `no se debe usar la salida estandar ni de error directa (println)`() {
        NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(classes)
    }

    companion object {
        private val NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
    }
}
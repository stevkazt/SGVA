# Verificación de Control Negativo — ArchitectureTest

**Propósito:** confirmar empíricamente que `ArchitectureTest` detecta una
violación arquitectónica real cuando ocurre, no solo que ha pasado en todas
las iteraciones hasta ahora. Hasta este punto solo había visto la suite
pasar o autocorregirse antes de fallar visiblemente — nunca la había visto
fallar de verdad.

## Método

Introduje manualmente (no mediante Claude Code CLI) una violación
inequívoca: anoté la entidad de dominio `Estudiante` con `@Component` de
Spring, exactamente lo que la regla de desacoplamiento prohíbe.

```diff
 package com.example.sgva.domain
+import org.springframework.stereotype.Component
 
+@Component
 data class Estudiante(
     val id: String,
     ...
 ) {
     init {
         ...
-        if (puntajeSaberPro != null && puntajeSaberPro !in RANGO_SABER_PRO) {
-            throw PuntajeSaberProFueraDeRangoException(puntajeSaberPro)
+        val puntaje = puntajeSaberPro
+        if (puntaje != null && puntaje !in RANGO_SABER_PRO) {
+            throw PuntajeSaberProFueraDeRangoException(puntaje)
         }
     }
 }
```

(El cambio en el `init` fue necesario porque `kotlin("plugin.spring")` abre
automáticamente cualquier clase anotada con `@Component`, lo que le hace
perder al compilador la capacidad de *smart cast* sobre `puntajeSaberPro`.
Es en sí mismo un efecto colateral real de violar la regla, no solo un
detalle técnico para poder compilar.)

## Resultado

```
ArchitectureTest > el dominio y los casos de uso no deben usar anotaciones de Spring() FAILED
    java.lang.AssertionError at ArchitectureTest.kt:54

ArchitectureTest > el dominio no debe depender de capas externas ni frameworks() FAILED
    java.lang.AssertionError at ArchitectureTest.kt:29
```

Ambas reglas fallaron, cada una citando la línea exacta de
`ArchitectureTest.kt` que evalúa la violación. Las otras 5 pruebas que
fallaron en esta misma corrida (`contextLoads`, `AcademicDataRepositoryJpaTest`
x4) fallaron por `ConnectException` — el contenedor `sgva-postgres` no
estaba corriendo en ese momento, algo no relacionado con esta verificación.

## Conclusión

`ArchitectureTest` detecta una violación real cuando ocurre, con mensajes
que identifican la regla específica incumplida. Esto confirma que los
resultados "PASS 8/8" reportados en `logs/V1/` a `logs/V4/` reflejan que el
código efectivamente no violó esas reglas — no que el instrumento sea incapaz
de fallar. El cambio se revirtió inmediatamente después de esta verificación
(`git checkout --`) y nunca se commiteó.

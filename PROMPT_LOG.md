# Registro de Prompts e Interacciones - Tesis SGVA

## Metodología de Registro
Cada interacción (generación inicial, solución de errores o refactorización) debe registrarse secuencialmente para medir la evolución de la deuda técnica y la calidad arquitectónica.

---

## Tabla de Trazabilidad

| ID | Iteración | Herramienta | Tipo | Prompt_Enviado | Archivos_Generados | ArchUnit | Deuda_SonarQube |
|---|---|---|---|---|---|---|---|
| P-000 | V0 | Manual | Configuración | Configuración base del framework e instrumentación de pruebas | Archivos base (`build.gradle.kts`, `ArchitectureTest.kt`, `DomainMarker.kt`) | PASS | 3 Code Smells (Línea Base) |

---

## Detalle de Prompts (Opcional - Expandido)

### P-000: Setup Inicial
- **Objetivo:** Verificación del instrumental de medición.
- **Resultado:** Entorno verificado, ArchUnit en verde y línea base capturada en SonarQube exitosamente.
# Historial de commits (rama main)

Lista de los commits de `main` en orden cronológico, generada con `git log` para la revisión anónima: hash abreviado, fecha (UTC), mensaje y archivos cambiados. Se omiten los nombres y correos de los autores. Los commits medidos con SonarQube en cada prompt están en `anexos/B_sintesis_y_mediciones/sonar_resultados_serie.md`, y cada log de `logs/` cita los commits de su prompt.

El historial completo, con el contenido de cada archivo en cada commit, está en `sgva_historial_anonimo.bundle`, en esta misma carpeta. Para explorarlo: `git clone sgva_historial_anonimo.bundle sgva` y, dentro, `git log`, `git show <hash>` o `git log -p -- <archivo>`. En esa copia los autores figuran como «Anonimo», se reemplazaron los datos que identifican al autor y a la institución, y se retiraron el logo de la universidad y la propuesta de grado; por eso sus hashes difieren de los de esta lista y de los logs. `mapa_hashes.tsv` da la equivalencia entre ambos.

## 1. `1076ad7` — 2026-07-28 23:12 UTC

**Initial commit**

- añadido: `LICENSE`
- añadido: `README.md`

## 2. `f6a7f10` — 2026-07-29 01:21 UTC

**chore(setup): establecer entorno base del laboratorio y calibracion V0**

> - Configuración inicial de Spring Boot 3.x con Kotlin y Gradle.
> - Instrumentación de ArchUnit para control de Arquitectura Limpia (Fitness Functions).
> - Integración del plugin de SonarQube para análisis estático de deuda técnica.
> - Creación de archivos de gobernanza e investigación (DESIGN_SGVA.md, CLAUDE.md, METODOLOGIA_INVESTIGACION.md).
> - Inicialización del registro de trazabilidad (PROMPT_LOG.md) con la línea base P-000 (V0).
> - Incorporación de fixtures de prueba (CSV) y actualización del README.md.

- añadido: `.DS_Store`
- añadido: `.claudeignore`
- añadido: `.gitattributes`
- añadido: `.gitignore`
- añadido: `CLAUDE.md`
- añadido: `DESIGN_SGVA.md`
- añadido: `PROMPT_LOG.md`
- modificado: `README.md`
- añadido: `TESTS_ARQUITECTONICOS.md`
- añadido: `build.gradle.kts`
- añadido: `gradle/wrapper/gradle-wrapper.jar`
- añadido: `gradle/wrapper/gradle-wrapper.properties`
- añadido: `gradlew`
- añadido: `gradlew.bat`
- añadido: `settings.gradle.kts`
- añadido: `src/.DS_Store`
- añadido: `src/main/kotlin/com/example/sgva/SgvaApplication.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/DomainMarker.kt`
- añadido: `src/main/resources/application.properties`
- añadido: `src/test/.DS_Store`
- añadido: `src/test/kotlin/com/example/sgva/ArchitectureTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/SgvaApplicationTests.kt`
- añadido: `src/test/resources/.DS_Store`
- añadido: `src/test/resources/fixtures/datos_docentes_test.csv`
- añadido: `src/test/resources/fixtures/datos_encuestas_test.csv`
- añadido: `src/test/resources/fixtures/datos_estudiantes_test.csv`

## 3. `f2563ae` — 2026-07-29 01:32 UTC

**refactor(tests): actualizar reglas de ArchUnit y refinamiento metodologico**

> - Se incorporan validaciones arquitectónicas adicionales en ArchitectureTest.kt (inmutabilidad, aislamiento estricto y convenciones de nomenclatura).
> - Se remueve la sección de conclusiones redundante en TESTS_ARQUITECTONICOS.md

- modificado: `TESTS_ARQUITECTONICOS.md`
- modificado: `src/test/kotlin/com/example/sgva/ArchitectureTest.kt`

## 4. `0a9a3c9` — 2026-08-27 07:58 UTC

**docs(diseno): alinear alcance, reglas de validacion y version de spring boot**

- modificado: `CLAUDE.md`
- modificado: `DESIGN_SGVA.md`
- modificado: `README.md`

## 5. `801b16a` — 2026-08-27 07:59 UTC

**chore(logging): migrar PROMPT_LOG a indice con carpeta logs/ y automatizar via hook SessionEnd**

- añadido: `.claude/hooks/log-session.sh`
- añadido: `.claude/settings.json`
- modificado: `PROMPT_LOG.md`
- añadido: `logs/V0/P-000.md`

## 6. `da4f829` — 2026-08-27 09:54 UTC

**test(arquitectura): permite sufijo Port en interfaces de dominio**

- modificado: `src/test/kotlin/com/example/sgva/ArchitectureTest.kt`

## 7. `2cfe6fe` — 2026-08-27 09:55 UTC

**feat(dominio): modela entidades Estudiante y Docente con reglas de validación**

- añadido: `src/main/kotlin/com/example/sgva/domain/Docente.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/EstadoEstudiante.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/Estudiante.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ExcepcionesDominio.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/NivelFormacion.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ReglasDeFormato.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/TipoDedicacion.kt`
- añadido: `src/test/kotlin/com/example/sgva/domain/EntidadesDominioTest.kt`

## 8. `9f1c415` — 2026-08-27 09:55 UTC

**docs(logs): completa registro de P-001 y actualiza justificación de pruebas arquitectónicas**

- modificado: `PROMPT_LOG.md`
- modificado: `TESTS_ARQUITECTONICOS.md`
- añadido: `logs/V1/P-001.md`

## 9. `2c3a212` — 2026-08-27 09:58 UTC

**fix(fixtures): corrige formato de id y cohorte a numérico/AAAAS en datos de prueba**

- modificado: `src/test/resources/fixtures/datos_estudiantes_test.csv`

## 10. `4e32d17` — 2026-09-01 10:17 UTC

**docs(V1): actualizar indice PROMPT_LOG con resultados de P-001**

- modificado: `PROMPT_LOG.md`

## 11. `3953a3c` — 2026-09-01 10:42 UTC

**feat(domain): agregar puerto AcademicDataRepository y EntidadDuplicadaException**

- añadido: `src/main/kotlin/com/example/sgva/domain/AcademicDataRepository.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/ExcepcionesDominio.kt`

## 12. `98c29f1` — 2026-09-01 10:42 UTC

**feat(usecases): registrar y consultar estudiantes y docentes**

- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarDocentesUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarEstudiantesUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarDocenteUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarEstudianteUseCase.kt`

## 13. `8f6315b` — 2026-09-01 10:42 UTC

**feat(infrastructure): adaptador en memoria y configuracion de casos de uso**

- añadido: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryEnMemoria.kt`

## 14. `31c205d` — 2026-09-01 10:42 UTC

**test: 12 pruebas para casos de uso y repositorio en memoria**

- añadido: `src/test/kotlin/com/example/sgva/application/GestionDocentesUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/GestionEstudiantesUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/RepositorioAcademicoFalso.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryEnMemoriaTest.kt`

## 15. `365d47f` — 2026-09-01 10:59 UTC

**docs(V1): registrar log P-002 - casos de uso y puerto AcademicDataRepository**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V1/P-002.md`

## 16. `635dfcf` — 2026-09-01 11:33 UTC

**feat(domain): agregar DocumentParserPort, ResultadoIngesta/RegistroOmitido y FormatoArchivoInvalidoException**

- añadido: `src/main/kotlin/com/example/sgva/domain/DocumentParserPort.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/ExcepcionesDominio.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ResultadoIngesta.kt`

## 17. `f3b0632` — 2026-09-01 11:33 UTC

**feat(usecases): orquestar ingesta de archivos y registro masivo de estudiantes y docentes**

- añadido: `src/main/kotlin/com/example/sgva/usecases/IngestarArchivoUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ProcesarArchivoUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ProcesarDatosDocentesUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ProcesarDatosEstudiantesUseCase.kt`

## 18. `90f648e` — 2026-09-01 11:33 UTC

**feat(infrastructure): parsers CSV/Excel, normalizacion de datos y cableado de casos de uso de ingesta**

- modificado: `build.gradle.kts`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/parsing/CsvDocumentParser.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/parsing/ExcelDocumentParser.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/parsing/MapeadorEntidad.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/parsing/NormalizadorDatos.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/parsing/ParserDocumentalBase.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/parsing/TablaCruda.kt`

## 19. `db31a61` — 2026-09-01 11:33 UTC

**test: 20 pruebas para parseo CSV/Excel, normalizacion y orquestacion de ingesta**

- añadido: `src/test/kotlin/com/example/sgva/application/ProcesarDatosArchivoUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/parsing/CsvDocumentParserTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/parsing/ExcelDocumentParserTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/parsing/NormalizadorDatosTest.kt`

## 20. `bb3de0d` — 2026-09-01 11:43 UTC

**docs(V1): registrar log P-003 con resultados de SonarQube y actualizar DESIGN_SGVA**

- modificado: `DESIGN_SGVA.md`
- modificado: `PROMPT_LOG.md`
- añadido: `logs/V1/P-003.md`

## 21. `6ed518c` — 2026-09-01 12:01 UTC

**feat(domain): agregar IndicadorCalidad, ParametrosDeAnalisis y excepciones de calculo**

- modificado: `src/main/kotlin/com/example/sgva/domain/ExcepcionesDominio.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/IndicadorCalidad.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ParametrosDeAnalisis.kt`

## 22. `77518a9` — 2026-09-01 12:01 UTC

**feat(usecases): calcular evolucion de matricula, tasa de desercion y consolidacion Saber Pro**

- añadido: `src/main/kotlin/com/example/sgva/usecases/CalcularEvolucionMatriculaUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/CalcularTasaDesercionUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/CalculoDeIndicadorUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsolidarPuntajesSaberProUseCase.kt`

## 23. `bb6117c` — 2026-09-01 12:01 UTC

**feat(infrastructure): cablear casos de uso de indicadores y bean Clock**

- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`

## 24. `0812005` — 2026-09-01 12:01 UTC

**test: 15 pruebas para indicadores de calidad y casos de uso de calculo (Modulo 1)**

- añadido: `src/test/kotlin/com/example/sgva/application/CalcularEvolucionMatriculaUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/CalcularTasaDesercionUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/ConsolidarPuntajesSaberProUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/domain/IndicadorCalidadTest.kt`

## 25. `46745d6` — 2026-09-01 12:22 UTC

**docs(V1): registrar log P-004 y formalizar metrica de restauraciones arquitectonicas autodetectadas**

- modificado: `PROMPT_LOG.md`
- modificado: `TESTS_ARQUITECTONICOS.md`
- añadido: `logs/V1/P-004.md`

## 26. `1962f9c` — 2026-09-01 12:51 UTC

**feat(domain): agregar EquivalenciaTiempoCompleto y nuevos indicadores canonicos**

- añadido: `src/main/kotlin/com/example/sgva/domain/EquivalenciaTiempoCompleto.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/IndicadorCalidad.kt`

## 27. `73aac88` — 2026-09-01 12:51 UTC

**feat(usecases): calcular distribucion de formacion docente y capacidad instalada**

- añadido: `src/main/kotlin/com/example/sgva/usecases/CalcularCapacidadInstaladaUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/CalcularDistribucionFormacionUseCase.kt`
- modificado: `src/main/kotlin/com/example/sgva/usecases/CalculoDeIndicadorUseCase.kt`

## 28. `562c8f1` — 2026-09-01 12:51 UTC

**feat(infrastructure): cablear casos de uso de distribucion de formacion y capacidad instalada**

- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`

## 29. `c9c7b30` — 2026-09-01 12:51 UTC

**test: 8 pruebas para distribucion de formacion docente y capacidad instalada**

- añadido: `src/test/kotlin/com/example/sgva/application/CalcularCapacidadInstaladaUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/CalcularDistribucionFormacionUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/domain/EquivalenciaTiempoCompletoTest.kt`

## 30. `4d758bf` — 2026-09-01 13:08 UTC

**docs(V1): registrar log P-005 - indicadores de Modulo 2 (docentes)**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V1/P-005.md`

## 31. `13a5c62` — 2026-09-09 02:54 UTC

**feat(infrastructure): controladores REST para estudiantes y docentes**

- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/DocenteController.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/EstudianteController.kt`

## 32. `8c8b947` — 2026-09-09 02:54 UTC

**feat(infrastructure): controlador REST para indicadores de calidad**

- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/IndicadorController.kt`

## 33. `3c54943` — 2026-09-09 02:54 UTC

**feat(infrastructure): manejo global de errores HTTP para excepciones de dominio**

- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/ManejadorGlobalDeErrores.kt`

## 34. `7b1b392` — 2026-09-09 03:37 UTC

**test: 30 pruebas de integracion MockMvc para controladores REST y manejo de errores**

- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionCasosDeUsoDePrueba.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/DocenteControllerTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/EstudianteControllerTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/IndicadorControllerTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/ManejadorGlobalDeErroresTest.kt`

## 35. `04e6840` — 2026-09-09 03:50 UTC

**fix(infrastructure): reemplazar HttpStatus.UNPROCESSABLE_ENTITY deprecado por UNPROCESSABLE_CONTENT**

- modificado: `src/main/kotlin/com/example/sgva/infrastructure/web/ManejadorGlobalDeErrores.kt`

## 36. `67b29d7` — 2026-09-09 03:51 UTC

**docs(V1): registrar log P-006 y cerrar V1 - adaptador REST completo, deuda tecnica corregida**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V1/P-006.md`

## 37. `d1b8b9b` — 2026-09-09 03:55 UTC

**test: exponer RepositorioAcademicoFalso como publico y agregar limpiar() para pruebas web**

- modificado: `src/test/kotlin/com/example/sgva/application/RepositorioAcademicoFalso.kt`

## 38. `453950e` — 2026-09-09 03:57 UTC

**test: exponer RepositorioAcademicoFalso como publico y agregar limpiar(); actualizar log P-006**

- modificado: `logs/V1/P-006.md`

## 39. `5c8c5f4` — 2026-09-09 04:09 UTC

**docs: agregar sintesis de V1 (hallazgos metodologicos y evolucion de deuda tecnica)**

- añadido: `SINTESIS_V1.md`

## 40. `876e331` — 2026-09-12 13:04 UTC

**docs(V1): registrar log P-006 y cerrar V1 - adaptador REST completo**

- modificado: `PROMPT_LOG.md`

## 41. `f8b6772` — 2026-09-12 13:45 UTC

**feat(domain): agregar Modulo 3 - RespuestaEncuesta, PonderacionLikert, MatrizRadar y extension de puertos**

- modificado: `src/main/kotlin/com/example/sgva/domain/AcademicDataRepository.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/DocumentParserPort.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/ExcepcionesDominio.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/MatrizRadar.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/PonderacionLikert.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/RespuestaEncuesta.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/TipoEstamento.kt`

## 42. `0b45bcb` — 2026-09-12 13:45 UTC

**feat(usecases): registro, ingesta y calculo de ponderacion Likert y matriz radar para encuestas**

- añadido: `src/main/kotlin/com/example/sgva/usecases/CalcularPonderacionLikertUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarRespuestasEncuestaUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/GenerarMatrizRadarUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ProcesarRespuestasEncuestasUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarRespuestaEncuestaUseCase.kt`

## 43. `94ec6c8` — 2026-09-12 13:45 UTC

**feat(infrastructure): parseo, persistencia, cableado y controlador REST para encuestas**

- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/MapeadorEntidad.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/NormalizadorDatos.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/ParserDocumentalBase.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryEnMemoria.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/EncuestaController.kt`

## 44. `204c0ce` — 2026-09-12 13:45 UTC

**test: 34 pruebas para Modulo 3 (encuestas de percepcion)**

- añadido: `src/test/kotlin/com/example/sgva/application/CalcularPonderacionLikertUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/GenerarMatrizRadarUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/GestionRespuestasEncuestaUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/ProcesarDatosArchivoUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/RepositorioAcademicoFalso.kt`
- modificado: `src/test/kotlin/com/example/sgva/domain/EntidadesDominioTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/CsvDocumentParserTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/ExcelDocumentParserTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/NormalizadorDatosTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryEnMemoriaTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionCasosDeUsoDePrueba.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/EncuestaControllerTest.kt`

## 45. `a2a2682` — 2026-09-12 13:45 UTC

**docs(V2): registrar log P-007 - Modulo 3 completo con resultados de SonarQube**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V2/P-007.md`

## 46. `cc186a3` — 2026-09-12 14:06 UTC

**docs: agregar documentacion de infraestructura local (SonarQube, PostgreSQL)**

- añadido: `INFRAESTRUCTURA.md`

## 47. `dd0c2bb` — 2026-09-12 15:07 UTC

**docs: agregar documentacion de infraestructura local (SonarQube, PostgreSQL)**

- borrado: `src/main/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryEnMemoria.kt`
- borrado: `src/test/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryEnMemoriaTest.kt`

## 48. `e235294` — 2026-09-12 15:21 UTC

**build: agregar dependencias de JPA y PostgreSQL**

- modificado: `build.gradle.kts`
- modificado: `src/main/resources/application.properties`

## 49. `4082d01` — 2026-09-12 15:21 UTC

**docs(V3): registrar log P-008 - sustitucion de persistencia con resultados de SonarQube**

- añadido: `logs/V3/P-008.md`

## 50. `92abc40` — 2026-09-12 15:23 UTC

**feat(infrastructure): sustituir persistencia en memoria por PostgreSQL via Spring Data JPA**

- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryJpa.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/DocenteEntity.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/EstudianteEntity.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/MapeadorPersistencia.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/RespuestaEncuestaEntity.kt`

## 51. `02a54a2` — 2026-09-12 15:23 UTC

**test: reemplazar AcademicDataRepositoryEnMemoria por RepositorioAcademicoFalso y agregar prueba JPA contra PostgreSQL real**

- modificado: `src/test/kotlin/com/example/sgva/application/CalcularCapacidadInstaladaUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/CalcularDistribucionFormacionUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/CalcularEvolucionMatriculaUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/CalcularPonderacionLikertUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/CalcularTasaDesercionUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/ConsolidarPuntajesSaberProUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/GenerarMatrizRadarUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/ProcesarDatosArchivoUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryJpaTest.kt`

## 52. `c543d90` — 2026-09-13 01:13 UTC

**feat(domain): agregar ReporteEjecutivo como value object para reportes ejecutivos**

- añadido: `src/main/kotlin/com/example/sgva/domain/ReporteEjecutivo.kt`

## 53. `6913b10` — 2026-09-13 01:13 UTC

**feat(usecases): componer GenerarReporteEjecutivoUseCase sobre los casos de uso de indicadores y encuestas**

- añadido: `src/main/kotlin/com/example/sgva/usecases/GenerarReporteEjecutivoUseCase.kt`

## 54. `778372d` — 2026-09-13 01:13 UTC

**feat(infrastructure): generar reportes ejecutivos en PDF con Apache PDFBox y exponerlos via REST**

- modificado: `build.gradle.kts`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/reporting/GeneradorReportePdf.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/ReporteController.kt`

## 55. `fed00dd` — 2026-09-13 01:13 UTC

**test: 9 pruebas para generacion de reportes ejecutivos en PDF**

- añadido: `src/test/kotlin/com/example/sgva/application/GenerarReporteEjecutivoUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/reporting/GeneradorReportePdfTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionCasosDeUsoDePrueba.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/ReporteControllerTest.kt`

## 56. `6d8be07` — 2026-09-13 02:53 UTC

**docs(V4): registrar log P-009 - integracion de reportes PDF con resultados de SonarQube**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V4/P-009.md`

## 57. `8505593` — 2026-09-14 02:56 UTC

**docs: verificacion independiente de resultados reportados por Claude Code CLI en commits de cierre**

- añadido: `logs/verificacion-independiente.md`

## 58. `7515323` — 2026-09-14 16:53 UTC

**docs: verificacion de control negativo - ArchitectureTest detecta violaciones reales**

- añadido: `logs/verificacion-instrumento-negativo.md`

## 59. `a3feb9b` — 2026-09-14 17:06 UTC

**docs: confirmar via git log que ArchitectureTest.kt no fue modificado desde V0 (3 commits, todos previos a P-001)**

- añadido: `logs/verificacion-instrumento-inmutable.md`

## 60. `c91b893` — 2026-09-18 02:46 UTC

**docs(instrumentacion): documentos de los instrumentos de medición y perfil de reglas de SonarQube**

- añadido: `INSTRUMENTOS_DE_MEDICION.md`
- añadido: `reglas_sonar.json`

## 61. `e3e212d` — 2026-09-18 02:47 UTC

**docs(metodologia): documentar amenazas a la validez del estudio**

- añadido: `AMENAZAS_VALIDEZ.md`

## 62. `d60c127` — 2026-09-18 02:48 UTC

**docs(sintesis): agregar síntesis de resultados V2 (expansión funcional)**

- añadido: `SINTESIS_V2.md`

## 63. `0841963` — 2026-09-18 02:48 UTC

**docs(sintesis): agregar síntesis de resultados V3 (estrés arquitectónico - persistencia)**

- añadido: `SINTESIS_V3.md`

## 64. `21d89ed` — 2026-09-18 02:48 UTC

**docs(sintesis): agregar síntesis de resultados V4 (estrés arquitectónico - integración externa)**

- añadido: `SINTESIS_V4.md`

## 65. `5fb42b6` — 2026-09-18 02:48 UTC

**docs(resultados): agregar primer análisis de la hipótesis de investigación**

- añadido: `ANALISIS_HIPOTESIS.md`

## 66. `2d7640c` — 2026-09-18 03:40 UTC

**docs: retirar documentos internos de análisis y verificación del repositorio de trabajo**

- borrado: `AMENAZAS_VALIDEZ.md`
- borrado: `ANALISIS_HIPOTESIS.md`
- borrado: `INFRAESTRUCTURA.md`
- borrado: `INSTRUMENTOS_DE_MEDICION.md`
- borrado: `SINTESIS_V1.md`
- borrado: `SINTESIS_V2.md`
- borrado: `SINTESIS_V3.md`
- borrado: `SINTESIS_V4.md`
- borrado: `TESTS_ARQUITECTONICOS.md`
- borrado: `logs/verificacion-independiente.md`
- borrado: `logs/verificacion-instrumento-inmutable.md`
- borrado: `logs/verificacion-instrumento-negativo.md`

## 67. `0f59ffd` — 2026-09-25 21:22 UTC

**feat(frontend): conectar la exportación del reporte ejecutivo en PDF**

> Agrega reportes.ejecutivo() y el helper peticionArchivo() en el cliente
> API, y un botón de descarga en el Dashboard que reutiliza los filtros
> de facultad/periodo/cohorte ya existentes. Verificado contra el
> backend real: PDF, nombre de archivo desde Content-Disposition y
> manejo de errores 404/409/red vía ErrorDeApi/ErrorDeRed.
>
> Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>

- añadido: `frontend/src/api/client.ts`
- añadido: `frontend/src/pages/DashboardPage.tsx`

## 68. `61c7548` — 2026-09-25 21:22 UTC

**docs(frontend): documentar estructura, rutas y flujo de desarrollo**

> Reemplaza el README por defecto de la plantilla de Vite por
> documentación real del proyecto: stack, estructura de carpetas,
> capa de api/, tabla de rutas/páginas y comandos de desarrollo.
>
> Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>

- añadido: `frontend/README.md`

## 69. `88c5782` — 2026-09-25 21:30 UTC

**chore(frontend): agregar el proyecto base React + Vite + TypeScript**

> Estructura inicial del frontend (generada en una sesión previa, ver
> logs/EXTRA/FRONTEND-01.md): scaffold de Vite, componentes de gráficas
> y UI, y las páginas de carga de archivos y encuestas. Nunca se había
> comprometido al repositorio. El cliente API y el Dashboard con la
> exportación del reporte ejecutivo ya se registraron por separado.
>
> Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>

- añadido: `frontend/.gitignore`
- añadido: `frontend/.oxlintrc.json`
- añadido: `frontend/index.html`
- añadido: `frontend/package-lock.json`
- añadido: `frontend/package.json`
- añadido: `frontend/src/App.tsx`
- añadido: `frontend/src/api/errores.ts`
- añadido: `frontend/src/api/types.ts`
- añadido: `frontend/src/assets/santoto-logo.png`
- añadido: `frontend/src/components/ErrorBanner.tsx`
- añadido: `frontend/src/components/StatTile.tsx`
- añadido: `frontend/src/components/charts/BarraSimpleChart.tsx`
- añadido: `frontend/src/components/charts/LikertChart.tsx`
- añadido: `frontend/src/components/charts/RadarFactorChart.tsx`
- añadido: `frontend/src/components/charts/SerieUnicaChart.tsx`
- añadido: `frontend/src/components/colorEstamento.ts`
- añadido: `frontend/src/index.css`
- añadido: `frontend/src/main.tsx`
- añadido: `frontend/src/pages/EncuestasPage.tsx`
- añadido: `frontend/src/pages/UploadPage.tsx`
- añadido: `frontend/tsconfig.app.json`
- añadido: `frontend/tsconfig.json`
- añadido: `frontend/tsconfig.node.json`
- añadido: `frontend/vite.config.ts`

## 70. `6ef5b21` — 2026-09-25 21:31 UTC

**docs(logs): registrar el extra práctico del frontend fuera del protocolo V0-V4**

> Documenta, fuera de la secuencia medida P-00X del estudio, la sesión
> que generó el frontend base y su verificación de punta a punta contra
> el backend y PostgreSQL reales (carga de archivos, dashboard,
> encuestas, manejo de errores), incluyendo el procedimiento para
> levantar el contenedor Postgres en desarrollo.
>
> Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>

- añadido: `logs/EXTRA/FRONTEND-01-tests.md`
- añadido: `logs/EXTRA/FRONTEND-01.md`

## 71. `5f15530` — 2026-09-25 21:35 UTC

**chore: ignorar Santoto-Design-Manual**

> Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>

- modificado: `.gitignore`

## 72. `c3cb899` — 2026-10-03 00:11 UTC

**chore(infra): agrega archivo docker-compose para base de datos postgresql**

- añadido: `docker-compose.yml`

## 73. `2f0ec8a` — 2026-10-03 00:14 UTC

**docs(root): actualiza instrucciones de ejecución con docker en README frontend y backend**

- modificado: `README.md`
- modificado: `frontend/README.md`

## 74. `0906b09` — 2026-10-03 00:18 UTC

**chore: elimina .DS_Store del rastreo de git y lo añade a .gitignore**

- borrado: `.DS_Store`
- modificado: `.gitignore`

## 75. `486e90b` — 2026-10-07 23:20 UTC

**docs(design): actualiza DESIGN_SGVA para V5 (alineación con proceso institucional)**

> Documenta Caracteristica, EvaluacionDocumental, ConfiguracionPonderaciones y ResultadoAutoevaluacion; el caso de uso CalcularResultadoAutoevaluacionUseCase; la configuración externa de ponderaciones; las reglas para N/A, 0,00 y valores faltantes; y los campos programa/caracteristica en Docente y RespuestaEncuesta. Registra como pendientes las ponderaciones exactas por factor y el caso #DIV/0./run.sh Precondición de P-010.

- modificado: `DESIGN_SGVA.md`

## 76. `4da7207` — 2026-10-07 23:25 UTC

**docs(root): agrega restricción de lectura de la carpeta docs a CLAUDE.md**

- modificado: `CLAUDE.md`

## 77. `b7abb0a` — 2026-10-07 23:31 UTC

**docs(root): agrega aclaración de autoría con Claude Sonnet 5 al README**

- modificado: `README.md`

## 78. `1e14f2e` — 2026-10-07 23:40 UTC

**chore(git): Excluded /docs folder from git**

- modificado: `.gitignore`

## 79. `09e00ce` — 2026-10-10 15:46 UTC

**docs(design): actualiza DESIGN_SGVA para V5 con modelo 2025**

- modificado: `DESIGN_SGVA.md`

## 80. `e5edacb` — 2026-10-10 16:03 UTC

**feat(domain): modela Caracteristica y ConfiguracionPonderaciones con validación de pesos**

- añadido: `src/main/kotlin/com/example/sgva/domain/Caracteristica.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ConfiguracionPonderaciones.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/ExcepcionesDominio.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/ReglasDeFormato.kt`

## 81. `2cd2851` — 2026-10-10 16:03 UTC

**test(domain): agrega pruebas de Caracteristica y ConfiguracionPonderaciones**

- modificado: `src/test/kotlin/com/example/sgva/domain/EntidadesDominioTest.kt`

## 82. `3cea345` — 2026-10-10 16:04 UTC

**docs(V5): registrar log P-010 - dominio de características y ponderaciones**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V5/P-010.md`

## 83. `29bbe09` — 2026-10-10 16:39 UTC

**feat(domain): agrega programa a Docente, amplía TipoEstamento a siete estamentos y modela ResultadoEncuestaPregunta**

- modificado: `src/main/kotlin/com/example/sgva/domain/AcademicDataRepository.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/Docente.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/DocumentParserPort.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/ExcepcionesDominio.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/ReglasDeFormato.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ResultadoEncuestaPregunta.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/TipoEstamento.kt`

## 84. `fedf8d9` — 2026-10-10 16:39 UTC

**feat(usecases): agrega registro, consulta y carga masiva de ResultadoEncuestaPregunta**

- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarResultadoEncuestaPreguntaUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ProcesarResultadosEncuestaPreguntaUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarResultadoEncuestaPreguntaUseCase.kt`

## 85. `42f19b9` — 2026-10-10 16:39 UTC

**feat(infrastructure): persiste y carga ResultadoEncuestaPregunta y el programa de Docente**

- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/MapeadorEntidad.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/NormalizadorDatos.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/ParserDocumentalBase.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryJpa.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/DocenteEntity.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/MapeadorPersistencia.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/ResultadoEncuestaPreguntaEntity.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/ResultadoEncuestaPreguntaController.kt`

## 86. `9081235` — 2026-10-10 16:39 UTC

**test: agrega pruebas de ResultadoEncuestaPregunta y actualiza las de Docente con programa**

- modificado: `src/test/kotlin/com/example/sgva/application/CalcularCapacidadInstaladaUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/CalcularDistribucionFormacionUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/GenerarReporteEjecutivoUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/GestionDocentesUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/GestionResultadosEncuestaPreguntaUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/ProcesarDatosArchivoUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/RepositorioAcademicoFalso.kt`
- modificado: `src/test/kotlin/com/example/sgva/domain/EntidadesDominioTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/CsvDocumentParserTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/ExcelDocumentParserTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/NormalizadorDatosTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryJpaTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionCasosDeUsoDePrueba.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/DocenteControllerTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/IndicadorControllerTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/ResultadoEncuestaPreguntaControllerTest.kt`
- modificado: `src/test/resources/fixtures/datos_docentes_test.csv`
- añadido: `src/test/resources/fixtures/datos_resultados_encuesta_pregunta_test.csv`

## 87. `adc20da` — 2026-10-10 16:44 UTC

**docs(V5): registrar log P-011 - programa, estamentos y encuesta por pregunta**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V5/P-011.md`

## 88. `d6d4076` — 2026-10-10 17:22 UTC

**feat(domain): agrega EvaluacionDocumental y ValoracionCualitativa**

- modificado: `src/main/kotlin/com/example/sgva/domain/AcademicDataRepository.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/DocumentParserPort.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/EvaluacionDocumental.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/ExcepcionesDominio.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ValoracionCualitativa.kt`

## 89. `ce45bbc` — 2026-10-10 17:26 UTC

**feat(usecases): agrega registro, consulta y carga masiva de EvaluacionDocumental**

- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarEvaluacionDocumentalUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ProcesarEvaluacionesDocumentalesUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarEvaluacionDocumentalUseCase.kt`

## 90. `7a58da5` — 2026-10-10 17:26 UTC

**feat(infrastructure): persiste, carga y expone EvaluacionDocumental**

- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/MapeadorEntidad.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/NormalizadorDatos.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/ParserDocumentalBase.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryJpa.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/EvaluacionDocumentalEntity.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/MapeadorPersistencia.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/EvaluacionDocumentalController.kt`

## 91. `14e30c5` — 2026-10-10 17:26 UTC

**test: agrega pruebas de EvaluacionDocumental y ValoracionCualitativa**

- añadido: `src/test/kotlin/com/example/sgva/application/GestionEvaluacionesDocumentalesUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/ProcesarDatosArchivoUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/RepositorioAcademicoFalso.kt`
- modificado: `src/test/kotlin/com/example/sgva/domain/EntidadesDominioTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/CsvDocumentParserTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/ExcelDocumentParserTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/NormalizadorDatosTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryJpaTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionCasosDeUsoDePrueba.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/EvaluacionDocumentalControllerTest.kt`
- añadido: `src/test/resources/fixtures/datos_evaluaciones_documentales_test.csv`

## 92. `6506b5d` — 2026-10-10 17:27 UTC

**docs(V5): registrar log P-012 - evaluación documental y valoración cualitativa**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V5/P-012.md`

## 93. `5569cc7` — 2026-10-10 18:34 UTC

**feat(infrastructure): soporta separador, codificación y filas de título en la lectura de archivos**

- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/CsvDocumentParser.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/ExcelDocumentParser.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/NormalizadorDatos.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/ParserDocumentalBase.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/TablaCruda.kt`

## 94. `a2307c8` — 2026-10-10 18:34 UTC

**test: agrega pruebas de robustez de lectura de archivos**

- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/CsvDocumentParserTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/ExcelDocumentParserTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/parsing/NormalizadorDatosTest.kt`

## 95. `8f11f5d` — 2026-10-10 18:35 UTC

**docs(V5): registrar log P-013 - robustez de lectura de archivos**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V5/P-013.md`

## 96. `802d07f` — 2026-10-10 19:51 UTC

**feat(domain): agrega ResultadoAutoevaluacion con promedios por característica, factor y global**

- añadido: `src/main/kotlin/com/example/sgva/domain/ResultadoAutoevaluacion.kt`

## 97. `25701af` — 2026-10-10 19:51 UTC

**feat(usecases): calcula el resultado de autoevaluación con mezcla y reponderación**

- añadido: `src/main/kotlin/com/example/sgva/usecases/CalcularResultadoAutoevaluacionUseCase.kt`

## 98. `8c7208b` — 2026-10-10 19:51 UTC

**test: agrega pruebas del cálculo de autoevaluación y de ResultadoAutoevaluacion**

- añadido: `src/test/kotlin/com/example/sgva/application/CalcularResultadoAutoevaluacionUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/domain/EntidadesDominioTest.kt`

## 99. `b947c9e` — 2026-10-10 22:10 UTC

**test: agrega fixtures del modelo 2025 para la validación de V5**

- añadido: `src/test/resources/fixtures/modelo2025/esperados_autoevaluacion.csv`
- añadido: `src/test/resources/fixtures/modelo2025/evaluacion_documental.csv`
- añadido: `src/test/resources/fixtures/modelo2025/ponderaciones_modelo_2025.csv`
- añadido: `src/test/resources/fixtures/modelo2025/resultados_encuesta_pregunta.csv`

## 100. `0739b68` — 2026-10-10 22:10 UTC

**feat(infrastructure): carga las ponderaciones del modelo desde un archivo externo**

- modificado: `build.gradle.kts`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/CargadorConfiguracionPonderaciones.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- modificado: `src/main/resources/application.properties`

## 101. `2f17631` — 2026-10-10 22:10 UTC

**test: agrega pruebas de la carga de ponderaciones**

- añadido: `src/test/kotlin/com/example/sgva/infrastructure/configuracion/CargadorConfiguracionPonderacionesTest.kt`

## 102. `24d64c2` — 2026-10-10 22:56 UTC

**docs(V5): registrar log P-014 - resultado de autoevaluación y carga de ponderaciones**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V5/P-014.md`

## 103. `dff05b9` — 2026-10-13 00:48 UTC

**feat(infrastructure): expone el resultado de autoevaluación por programa y periodo**

- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/ResultadoAutoevaluacionController.kt`

## 104. `a2b1f00` — 2026-10-13 00:48 UTC

**test: agrega pruebas del controlador de resultado de autoevaluación**

- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionCasosDeUsoDePrueba.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/ResultadoAutoevaluacionControllerTest.kt`

## 105. `0bc3edb` — 2026-10-13 00:49 UTC

**docs(V5): registrar log P-015 - endpoint REST del resultado de autoevaluación**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V5/P-015.md`

## 106. `4bcb4b7` — 2026-10-13 01:28 UTC

**feat(domain): agrega el resultado de autoevaluación al reporte ejecutivo**

- modificado: `src/main/kotlin/com/example/sgva/domain/ReporteEjecutivo.kt`

## 107. `30de462` — 2026-10-13 01:28 UTC

**feat(usecases): calcula el resultado de autoevaluación en el reporte ejecutivo**

- modificado: `src/main/kotlin/com/example/sgva/usecases/GenerarReporteEjecutivoUseCase.kt`

## 108. `9bde792` — 2026-10-13 01:28 UTC

**feat(infrastructure): muestra el resultado de autoevaluación en el reporte PDF**

- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/reporting/GeneradorReportePdf.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/web/ReporteController.kt`

## 109. `93120db` — 2026-10-13 01:28 UTC

**test: agrega pruebas de la sección de autoevaluación del reporte ejecutivo**

- modificado: `src/test/kotlin/com/example/sgva/application/GenerarReporteEjecutivoUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/reporting/GeneradorReportePdfTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionCasosDeUsoDePrueba.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ReporteControllerTest.kt`

## 110. `7b3abb3` — 2026-10-13 01:35 UTC

**docs(V5): registrar log P-016 - sección de resultado de autoevaluación en el reporte ejecutivo**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V5/P-016.md`

## 111. `f729121` — 2026-10-13 01:35 UTC

**docs(V5): completar la medición de Sonar de P-015 en PROMPT_LOG**

- modificado: `PROMPT_LOG.md`

## 112. `8623d35` — 2026-10-13 01:44 UTC

**test: valida el cálculo de autoevaluación contra el modelo 2025**

- añadido: `src/test/kotlin/com/example/sgva/application/CalcularResultadoAutoevaluacionModelo2025Test.kt`

## 113. `3eeea7e` — 2026-10-13 01:45 UTC

**docs(V5): registrar log P-017 - validación contra el modelo 2025**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V5/P-017.md`

## 114. `e39e561` — 2026-10-13 01:49 UTC

**docs(design): documenta el seguimiento del proceso de autoevaluación para V6**

- modificado: `DESIGN_SGVA.md`

## 115. `335f198` — 2026-10-13 01:55 UTC

**chore(hook): usa V6 como iteración por defecto del log de sesión**

- modificado: `.claude/hooks/log-session.sh`

## 116. `27fc8bf` — 2026-10-13 03:20 UTC

**feat(domain): modela el seguimiento del proceso de autoevaluación**

- modificado: `src/main/kotlin/com/example/sgva/domain/AcademicDataRepository.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ActividadCiclo.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/AvanceCiclo.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/CicloAutoevaluacion.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ComparacionResultados.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ConfiguracionSeguimiento.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/DetalleCiclo.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/DocumentParserPort.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/EstadoActividad.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/EstadoProyecto.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/ExcepcionesDominio.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/FaseCiclo.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/GrupoEvaluador.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/OrigenProyecto.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ParticipacionActor.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/PreevaluacionEvaluador.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/ProyectoMejoramiento.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/TipoSemaforo.kt`

## 117. `1671d9d` — 2026-10-13 03:20 UTC

**feat(usecases): agrega registro, consulta, avance y comparación de ciclos de autoevaluación**

- añadido: `src/main/kotlin/com/example/sgva/usecases/CalcularAvanceCicloUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/CompararResultadosEntreCiclosUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsolidarPreevaluacionesUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarCicloAutoevaluacionUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarGrupoEvaluadorUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarParticipacionActorUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarPreevaluacionUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarProyectoMejoramientoUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ProcesarParticipacionActorUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ProcesarPreevaluacionesUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarActividadCicloUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarCicloAutoevaluacionUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarGrupoEvaluadorUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarParticipacionActorUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarPreevaluacionUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/RegistrarProyectoMejoramientoUseCase.kt`

## 118. `3d80a7d` — 2026-10-13 03:20 UTC

**feat(infrastructure): persiste, carga y expone el seguimiento del proceso de autoevaluación**

- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/MapeadorEntidad.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/parsing/ParserDocumentalBase.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryJpa.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/ActividadCicloEntity.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/CicloAutoevaluacionEntity.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/FaseCicloEntity.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/GrupoEvaluadorEntity.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/MapeadorPersistencia.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/ParticipacionActorEntity.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/PreevaluacionEvaluadorEntity.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/ProyectoMejoramientoEntity.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/CicloAutoevaluacionController.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/ComparacionResultadosController.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/GrupoEvaluadorController.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/ParticipacionActorController.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/PreevaluacionController.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/ProyectoMejoramientoController.kt`
- modificado: `src/main/resources/application.properties`

## 119. `def59d9` — 2026-10-13 03:20 UTC

**test: agrega pruebas del seguimiento del proceso de autoevaluación**

- añadido: `src/test/kotlin/com/example/sgva/application/CalcularAvanceCicloUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/CompararResultadosEntreCiclosUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/GestionCicloAutoevaluacionUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/GestionGrupoEvaluadorYPreevaluacionUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/GestionParticipacionActorUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/GestionProyectoMejoramientoUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/ProcesarDatosArchivoUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/RepositorioAcademicoFalso.kt`
- añadido: `src/test/kotlin/com/example/sgva/domain/SeguimientoEntidadesDominioTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/persistence/AcademicDataRepositoryJpaTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/CicloAutoevaluacionControllerTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/ComparacionResultadosControllerTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionCasosDeUsoDePrueba.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/GrupoEvaluadorControllerTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/ParticipacionActorControllerTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/PreevaluacionControllerTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/ProyectoMejoramientoControllerTest.kt`
- añadido: `src/test/resources/fixtures/datos_participacion_actores_test.csv`
- añadido: `src/test/resources/fixtures/datos_preevaluaciones_test.csv`

## 120. `9e48b1a` — 2026-10-13 03:24 UTC

**docs(V6): registrar log P-018 - seguimiento del proceso de autoevaluación**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V6/P-018.md`

## 121. `8906b6c` — 2026-10-14 11:33 UTC

**test: agrega fixtures del proceso 2025 para la validación de V6**

- añadido: `src/test/resources/fixtures/proceso2025/cronograma_actividades.csv`
- añadido: `src/test/resources/fixtures/proceso2025/esperados_v6.csv`
- añadido: `src/test/resources/fixtures/proceso2025/grupos_evaluadores.csv`
- añadido: `src/test/resources/fixtures/proceso2025/participacion_actores.csv`
- añadido: `src/test/resources/fixtures/proceso2025/preevaluaciones.csv`

## 122. `c3a1b14` — 2026-10-14 11:44 UTC

**test: valida el seguimiento del proceso contra el proceso 2025**

- añadido: `src/test/kotlin/com/example/sgva/application/SeguimientoProceso2025Test.kt`

## 123. `e5356ed` — 2026-10-14 11:46 UTC

**docs(V6): registrar log P-026 - validación contra el proceso 2025**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V6/P-026.md`

## 124. `5a7c75a` — 2026-10-14 11:53 UTC

**docs(V6): completar las notas del investigador de P-010 a P-018 y P-026**

- modificado: `logs/V5/P-010.md`
- modificado: `logs/V5/P-011.md`
- modificado: `logs/V5/P-012.md`
- modificado: `logs/V5/P-013.md`
- modificado: `logs/V5/P-014.md`
- modificado: `logs/V5/P-015.md`
- modificado: `logs/V5/P-016.md`
- modificado: `logs/V5/P-017.md`
- modificado: `logs/V6/P-018.md`
- modificado: `logs/V6/P-026.md`

## 125. `026562b` — 2026-10-14 11:55 UTC

**docs(V6): completar la medición de Sonar de P-010 a P-014 en PROMPT_LOG**

- modificado: `PROMPT_LOG.md`

## 126. `90bcb36` — 2026-10-14 12:20 UTC

**feat(domain): agrega el estado del ciclo de autoevaluación al reporte ejecutivo**

- añadido: `src/main/kotlin/com/example/sgva/domain/EstadoCicloAutoevaluacion.kt`
- modificado: `src/main/kotlin/com/example/sgva/domain/ReporteEjecutivo.kt`

## 127. `dd32438` — 2026-10-14 12:20 UTC

**feat(usecases): compone el estado del ciclo y la comparación con el ciclo anterior en el reporte**

- modificado: `src/main/kotlin/com/example/sgva/usecases/ConsultarCicloAutoevaluacionUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarEstadoCicloAutoevaluacionUseCase.kt`
- modificado: `src/main/kotlin/com/example/sgva/usecases/GenerarReporteEjecutivoUseCase.kt`

## 128. `19aeab0` — 2026-10-14 12:20 UTC

**feat(infrastructure): muestra el estado del ciclo de autoevaluación en el reporte PDF**

- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/reporting/GeneradorReportePdf.kt`

## 129. `9598ea2` — 2026-10-14 12:20 UTC

**test: agrega pruebas de la sección de estado del ciclo en el reporte ejecutivo**

- añadido: `src/test/kotlin/com/example/sgva/application/ConsultarEstadoCicloAutoevaluacionUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/application/GenerarReporteEjecutivoUseCaseTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/reporting/GeneradorReportePdfTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionCasosDeUsoDePrueba.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ReporteControllerTest.kt`

## 130. `c29b9bb` — 2026-10-14 12:21 UTC

**docs(V6): registrar log P-025 - estado del ciclo en el reporte ejecutivo**

- modificado: `PROMPT_LOG.md`
- añadido: `logs/V6/P-025.md`

## 131. `2bb14cf` — 2026-10-14 12:35 UTC

**feat(frontend): agrega la carga de resultados de encuesta y evaluación documental**

- modificado: `frontend/src/api/client.ts`
- modificado: `frontend/src/api/types.ts`
- modificado: `frontend/src/pages/UploadPage.tsx`

## 132. `5ab63d1` — 2026-10-14 12:35 UTC

**feat(frontend): agrega la pantalla de resultado de autoevaluación**

- modificado: `frontend/src/App.tsx`
- modificado: `frontend/src/api/client.ts`
- modificado: `frontend/src/api/types.ts`
- añadido: `frontend/src/pages/ResultadoAutoevaluacionPage.tsx`

## 133. `0ec0608` — 2026-10-14 12:37 UTC

**feat(frontend): agrega el cliente y los tipos del seguimiento del proceso**

- modificado: `frontend/src/api/client.ts`
- modificado: `frontend/src/api/types.ts`
- modificado: `frontend/src/pages/UploadPage.tsx`

## 134. `7553060` — 2026-10-14 12:37 UTC

**feat(frontend): agrega componentes y utilidades del seguimiento del proceso**

- añadido: `frontend/src/components/Insignia.tsx`
- añadido: `frontend/src/components/etiquetas.ts`
- añadido: `frontend/src/descargas.ts`
- añadido: `frontend/src/hooks/useConsulta.ts`
- modificado: `frontend/src/index.css`

## 135. `44c90ae` — 2026-10-14 12:37 UTC

**feat(frontend): agrega las pantallas de ciclos, proyectos y comparación**

- modificado: `frontend/src/App.tsx`
- añadido: `frontend/src/pages/CicloDetallePage.tsx`
- añadido: `frontend/src/pages/CiclosPage.tsx`
- añadido: `frontend/src/pages/ComparacionPage.tsx`
- añadido: `frontend/src/pages/ProyectosPage.tsx`

## 136. `7ff2950` — 2026-10-14 12:38 UTC

**docs(logs): registrar los extras de frontend 02 y 03 fuera del protocolo**

- añadido: `logs/EXTRA/FRONTEND-02.md`
- añadido: `logs/EXTRA/FRONTEND-03.md`

## 137. `4674a17` — 2026-10-14 12:42 UTC

**fix(frontend): fija la escala de 0 a 5 en la gráfica de promedios por factor**

- modificado: `frontend/src/components/charts/BarraSimpleChart.tsx`
- modificado: `frontend/src/pages/ResultadoAutoevaluacionPage.tsx`

## 138. `7531360` — 2026-10-14 13:06 UTC

**docs(V6): completar las notas del investigador de P-025**

- modificado: `logs/V6/P-025.md`

## 139. `744b188` — 2026-10-15 01:30 UTC

**feat(domain): agrega los puertos de la configuración de ponderaciones**

- añadido: `src/main/kotlin/com/example/sgva/domain/ConfiguracionPonderacionesRepository.kt`
- añadido: `src/main/kotlin/com/example/sgva/domain/LectorConfiguracionPonderacionesPort.kt`

## 140. `79ab97c` — 2026-10-15 01:30 UTC

**feat(usecases): consulta y carga las ponderaciones desde la base de datos**

- modificado: `src/main/kotlin/com/example/sgva/usecases/CalcularAvanceCicloUseCase.kt`
- modificado: `src/main/kotlin/com/example/sgva/usecases/CalcularResultadoAutoevaluacionUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/CargarConfiguracionPonderacionesUseCase.kt`
- añadido: `src/main/kotlin/com/example/sgva/usecases/ConsultarConfiguracionPonderacionesUseCase.kt`
- modificado: `src/main/kotlin/com/example/sgva/usecases/GenerarReporteEjecutivoUseCase.kt`

## 141. `cfec1c0` — 2026-10-15 01:30 UTC

**feat(infrastructure): persiste las ponderaciones y las expone por REST**

- modificado: `build.gradle.kts`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/CargadorConfiguracionPonderaciones.kt`
- modificado: `src/main/kotlin/com/example/sgva/infrastructure/configuracion/ConfiguracionCasosDeUso.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/ConfiguracionPonderacionesRepositoryJpa.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/persistence/jpa/PonderacionEntity.kt`
- añadido: `src/main/kotlin/com/example/sgva/infrastructure/web/ConfiguracionPonderacionesController.kt`
- modificado: `src/main/resources/application.properties`

## 142. `85f2e06` — 2026-10-15 01:30 UTC

**test: cubre la carga y consulta de ponderaciones en la base de datos**

- añadido: `src/test/kotlin/com/example/sgva/application/CalculosConConfiguracionVigenteTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/ConfiguracionPonderacionesUseCaseTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/ReporteEjecutivoSinPonderacionesTest.kt`
- añadido: `src/test/kotlin/com/example/sgva/application/RepositorioConfiguracionPonderacionesFalso.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/persistence/ConfiguracionPonderacionesRepositoryJpaTest.kt`
- modificado: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionCasosDeUsoDePrueba.kt`
- añadido: `src/test/kotlin/com/example/sgva/infrastructure/web/ConfiguracionPonderacionesControllerTest.kt`

## 143. `e730e01` — 2026-10-15 01:30 UTC

**feat(frontend): permite cargar y consultar las ponderaciones del modelo**

- modificado: `frontend/src/App.tsx`
- modificado: `frontend/src/api/client.ts`
- modificado: `frontend/src/api/types.ts`
- modificado: `frontend/src/index.css`
- añadido: `frontend/src/pages/PonderacionesPage.tsx`
- modificado: `frontend/src/pages/UploadPage.tsx`

## 144. `18f300f` — 2026-10-15 01:31 UTC

**docs(logs): registrar la mejora de ponderaciones en base de datos**

- añadido: `logs/EXTRA/MEJORA-01.md`

## 145. `0a84193` — 2026-10-25 23:32 UTC

**chore(hook): elimina el hook de registro de sesión**

> Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>
> Claude-Session: https://claude.ai/code/session_01JhJe4mVewvuf6ZSAz4USVB

- borrado: `.claude/hooks/log-session.sh`
- borrado: `.claude/settings.json`

## 146. `e917692` — 2026-10-25 23:32 UTC

**chore(frontend): permite acceder mediante túneles de cloudflare**

> Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>
> Claude-Session: https://claude.ai/code/session_01JhJe4mVewvuf6ZSAz4USVB

- modificado: `frontend/vite.config.ts`

## 147. `798236b` — 2026-10-25 23:36 UTC

**docs(transcripts): agrega los respaldos de los transcripts de las sesiones**

> Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>
> Claude-Session: https://claude.ai/code/session_01JhJe4mVewvuf6ZSAz4USVB

- añadido: `transcripts/3cb36813-006b-4939-a2b4-55906badbdff.jsonl`
- añadido: `transcripts/4a04cf61-3c92-4903-9551-d5223aca3635.jsonl`
- añadido: `transcripts/7feb36e3-f809-4f36-9656-60c6da9c670f.jsonl`
- añadido: `transcripts/8e07c362-ab2a-4993-813a-f1b4f0a42d00.jsonl`
- añadido: `transcripts/SHA256.txt`
- añadido: `transcripts/V5_P-010_sesion-debby_e38c6237.jsonl`
- añadido: `transcripts/V5_P-011_sesion-debby_92a3e996.jsonl`
- añadido: `transcripts/V5_P-012_sesion-debby_c6f90551.jsonl`
- añadido: `transcripts/V5_P-013_sesion-debby_4ff37a6a.jsonl`
- añadido: `transcripts/V5_P-014_sesion-debby_781f7d91.jsonl`
- añadido: `transcripts/V5_P-014_sesion-debby_781f7d91_1541.jsonl`
- añadido: `transcripts/V5_P-015_sesion-debby_b24ca9db.jsonl`
- añadido: `transcripts/V5_P-016_sesion-debby_57c6b914.jsonl`
- añadido: `transcripts/V5_P-017_sesion-debby_c54e0a66.jsonl`
- añadido: `transcripts/V6_P-018_sesion-debby_a509cfc4.jsonl`
- añadido: `transcripts/V6_P-025_sesion-debby_26621f9a.jsonl`
- añadido: `transcripts/V6_P-026_sesion-debby_c371f98d.jsonl`
- añadido: `transcripts/d7c016a6-cdc0-4bba-aa98-49a00c3e1541.jsonl`
- añadido: `transcripts/subagents/agent-abd3d5097bdcd765c.jsonl`
- añadido: `transcripts/subagents/agent-abd3d5097bdcd765c.meta.json`

## 148. `74a59bd` — 2026-10-26 00:12 UTC

**docs(anexos): agrega los anexos citados en la tesis**

> Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>
> Claude-Session: https://claude.ai/code/session_01JhJe4mVewvuf6ZSAz4USVB

- añadido: `anexos/A_documentos_rectores/PROTOCOLO_DE_PROMPTING.md`
- añadido: `anexos/A_documentos_rectores/propuesta_proyecto_de_grado.md`
- añadido: `anexos/B_sintesis_y_mediciones/sintesis_V5_datos.md`
- añadido: `anexos/B_sintesis_y_mediciones/sintesis_V6_datos.md`
- añadido: `anexos/B_sintesis_y_mediciones/sonar_original_V1-V4.md`
- añadido: `anexos/B_sintesis_y_mediciones/sonar_resultados_serie.md`
- añadido: `anexos/C_registros/TESTS_ARQUITECTONICOS.md`
- añadido: `anexos/README.md`

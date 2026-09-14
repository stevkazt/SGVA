# Verificación Independiente de Resultados Reportados

**Propósito:** hasta este punto confié en los resúmenes de Claude Code CLI
("136 tests / 0 fallos", etc.) sin volver a correr las pruebas yo mismo. Para
sostener la validez del estudio, reproduje de forma independiente los
resultados de compilación y pruebas en los commits de cierre de cada versión.

**Método:** para cada commit, `git checkout <hash>` + `./gradlew clean test`
(sin caché incremental), verificando el resultado directamente en la salida
de Gradle. Vuelvo a `main` después de cada verificación.

---

## Resultados

| Commit | Versión | Resultado |
|---|---|---|
| `453950e` | V1 (cierre) | ✅ BUILD SUCCESSFUL |
| `a2a2682` | V2 (cierre) | ✅ BUILD SUCCESSFUL |
| `4082d01` | V3 (commit de docs, no el cierre funcional) | ❌ BUILD FAILED — ver nota |
| `02a54a2` | V3 (cierre funcional real) | ✅ BUILD SUCCESSFUL |
| `6d8be07` | V4 (cierre, `HEAD`) | ✅ BUILD SUCCESSFUL |

---

## Nota sobre el fallo en `4082d01`

Al verificar `4082d01` obtuve 16 errores de compilación
(`Unresolved reference 'persistence'`, `Unresolved reference
'AcademicDataRepositoryEnMemoria'`) en ocho archivos de prueba.

**Causa:** `4082d01` registra el *log* de P-008, pero quedó commiteado
**antes** que el código real de la migración a PostgreSQL
(`e235294`, `92abc40`, `02a54a2`). Esto pasó porque, durante la sesión de
commits de P-008, dos `git add` con rutas a archivos ya eliminados
(`AcademicDataRepositoryEnMemoria.kt` y su test) fallaron silenciosamente
con `fatal: pathspec ... did not match any files`, y seguí ejecutando los
comandos en cadena sin notar que esos dos grupos nunca se agregaron. Corregí
el problema en la misma sesión, pero `4082d01` quedó como un punto
intermedio no compilable en el historial de `main`.

**Conclusión:** no es un resultado falso de la IA — es una inconsistencia de
orden de commits que introduje yo al encadenar `git add`/`commit` sin
verificar el éxito de cada paso. El cierre funcional correcto de V3 es
`02a54a2`, no `4082d01`. No reescribo el historial (ya estaba pusheado);
dejo el commit intermedio documentado y explicado aquí.

---

## Qué cambio a partir de aquí

De ahora en adelante verifico de forma independiente (`git checkout` +
`./gradlew clean test`) el commit de cierre funcional real de cada
iteración, no solo el commit de documentación, y no doy por válido un
resultado únicamente porque la IA lo reportó así.

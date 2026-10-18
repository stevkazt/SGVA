// Genera los datos sintéticos del modo demo (determinista: mismo resultado siempre).
// Uso: node demo/generar-datos.mjs
// Los datos del modelo 2025 y del proceso 2025 NO se generan: se toman de
// src/test/resources/fixtures. Aquí solo se completan los módulos que esos
// fixtures no cubren (estudiantes, docentes, respuestas individuales de
// encuesta) y se deriva un periodo anterior (20251) para la comparación.
import { mkdirSync, readFileSync, writeFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const raiz = join(dirname(fileURLToPath(import.meta.url)), '..')
const fixtures = join(raiz, 'src/test/resources/fixtures')
const salida = join(raiz, 'demo/datos')
mkdirSync(salida, { recursive: true })

// PRNG determinista (mulberry32).
let semilla = 20262025
function azar() {
  semilla = (semilla + 0x6d2b79f5) | 0
  let t = Math.imul(semilla ^ (semilla >>> 15), 1 | semilla)
  t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t
  return ((t ^ (t >>> 14)) >>> 0) / 4294967296
}
const entre = (a, b) => Math.floor(azar() * (b - a + 1)) + a
const elegir = (lista) => lista[Math.floor(azar() * lista.length)]
const escribir = (nombre, filas) => writeFileSync(join(salida, nombre), filas.join('\n') + '\n')

// Estudiantes: cohortes 2020-1 a 2026-2 (la evolución de matrícula mira 7 años).
const estudiantes = ['id,programa,facultad,cohorte,estado,puntajeSaberPro']
for (let anio = 2020; anio <= 2026; anio++) {
  for (const semestre of [1, 2]) {
    const cohorte = `${anio}${semestre}`
    const antiguedad = (2026 - anio) * 2 + (2 - semestre)
    const cantidad = entre(24, 36)
    for (let n = 1; n <= cantidad; n++) {
      let estado = 'MATRICULADO'
      if (antiguedad >= 4) estado = azar() < 0.18 ? 'DESERTOR' : azar() < 0.55 ? 'GRADUADO' : 'MATRICULADO'
      else if (antiguedad >= 2) estado = azar() < 0.12 ? 'DESERTOR' : 'MATRICULADO'
      const saber = estado === 'GRADUADO' || (estado === 'MATRICULADO' && antiguedad >= 3 && azar() < 0.5) ? entre(140, 255) : ''
      estudiantes.push(`${cohorte}${String(n).padStart(3, '0')},EGSTI,Ingeniería,${cohorte},${estado},${saber}`)
    }
  }
}
escribir('estudiantes.csv', estudiantes)

// Docentes del periodo 20252 (el id es único en todo el sistema).
const niveles = ['ESPECIALIZACION', 'MAESTRIA', 'MAESTRIA', 'DOCTORADO']
const dedicaciones = ['TIEMPO_COMPLETO', 'TIEMPO_COMPLETO', 'MEDIO_TIEMPO', 'CATEDRA', 'CATEDRA']
const docentes = ['id,programa,facultad,nivelFormacion,dedicacion,periodo']
for (let n = 1; n <= 16; n++) {
  docentes.push(`D${String(n).padStart(3, '0')},EGSTI,Ingeniería,${elegir(niveles)},${elegir(dedicaciones)},20252`)
}
escribir('docentes.csv', docentes)

// Respuestas individuales de encuesta (alimentan la pantalla Encuestas).
const estamentos = ['ESTUDIANTE', 'PROFESOR', 'EGRESADO', 'DIRECTIVO']
const respuestas = ['id,estamento,factor,calificacion,periodo']
let consecutivo = 1
for (let f = 1; f <= 12; f++) {
  for (const estamento of estamentos) {
    const sesgo = estamento === 'ESTUDIANTE' ? -0.4 : estamento === 'DIRECTIVO' ? 0.3 : 0
    for (let k = 0; k < 5; k++) {
      const nota = Math.min(5, Math.max(1, Math.round(4.1 + sesgo + (azar() - 0.5) * 2.2)))
      respuestas.push(`R${String(consecutivo++).padStart(4, '0')},${estamento},F${f},${nota},20252`)
    }
  }
}
escribir('respuestas_encuesta.csv', respuestas)

// Periodo anterior (20251), derivado del modelo 2025 con notas algo menores,
// para que la pantalla Comparación tenga dos periodos que comparar.
function derivar(archivo, columnaNota, columnaPeriodo) {
  const [encabezado, ...filas] = readFileSync(join(fixtures, 'modelo2025', archivo), 'utf8').trim().split(/\r?\n/)
  const cols = encabezado.split(',')
  const iNota = cols.indexOf(columnaNota)
  const iPeriodo = cols.indexOf(columnaPeriodo)
  const salidaFilas = filas.map((fila) => {
    const celdas = fila.split(',')
    celdas[iPeriodo] = '20251'
    const valor = Number.parseFloat(celdas[iNota])
    if (!Number.isNaN(valor)) celdas[iNota] = Math.min(5, Math.max(1, valor - (0.1 + azar() * 0.5))).toFixed(2)
    return celdas.join(',')
  })
  return [encabezado, ...salidaFilas]
}
escribir('resultados_encuesta_pregunta_20251.csv', derivar('resultados_encuesta_pregunta.csv', 'promedio', 'periodo'))
escribir('evaluacion_documental_20251.csv', derivar('evaluacion_documental.csv', 'calificacion', 'periodo'))

console.log('Datos demo generados en demo/datos')

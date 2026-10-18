// Copia los archivos de ejemplo a frontend/public/ejemplos para que el panel
// de pruebas los ofrezca como descarga. Los toma de las mismas fuentes que
// usa el sembrador, así que la demo y las descargas siempre coinciden.
import { copyFileSync, existsSync, mkdirSync } from 'node:fs'
import { homedir } from 'node:os'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const raiz = join(dirname(fileURLToPath(import.meta.url)), '..')
const destino = join(raiz, 'frontend/public/ejemplos')
mkdirSync(destino, { recursive: true })

const fixtures = join(raiz, 'src/test/resources/fixtures')
const origenes = [
  join(fixtures, 'modelo2025/ponderaciones_modelo_2025.csv'),
  join(fixtures, 'modelo2025/resultados_encuesta_pregunta.csv'),
  join(fixtures, 'modelo2025/evaluacion_documental.csv'),
  join(fixtures, 'proceso2025/preevaluaciones.csv'),
  join(fixtures, 'proceso2025/participacion_actores.csv'),
  join(raiz, 'demo/datos/estudiantes.csv'),
  join(raiz, 'demo/datos/docentes.csv'),
]
for (const origen of origenes) copyFileSync(origen, join(destino, origen.split('/').pop()))
console.log(`${origenes.length} archivos de ejemplo copiados a frontend/public/ejemplos`)

// Documento general de la aplicación (docs/ no está versionado: se toma de
// DOC_GENERAL o, por defecto, de la copia de trabajo en ~/SGVA/docs).
const documento = process.env.DOC_GENERAL ?? join(homedir(), 'SGVA/docs/documento_general_sgva.html')
if (existsSync(documento)) {
  const destinoDoc = join(raiz, 'frontend/public/documentacion')
  mkdirSync(destinoDoc, { recursive: true })
  copyFileSync(documento, join(destinoDoc, 'documento_general_sgva.html'))
  console.log('Documento general copiado a frontend/public/documentacion')
} else {
  console.warn(`No se encontró ${documento}; el panel enlazará a un documento que no existe.`)
}

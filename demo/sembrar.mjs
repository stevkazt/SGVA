// Carga los datos demo en un backend SGVA ya levantado, usando solo su API.
// Uso: node demo/sembrar.mjs http://localhost:8090
import { readFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const raiz = join(dirname(fileURLToPath(import.meta.url)), '..')
const fixtures = join(raiz, 'src/test/resources/fixtures')
const datos = join(raiz, 'demo/datos')

async function enviar(base, ruta, init) {
  const respuesta = await fetch(base + ruta, init)
  if (!respuesta.ok) {
    throw new Error(`${init?.method ?? 'GET'} ${ruta} -> HTTP ${respuesta.status}: ${await respuesta.text()}`)
  }
  return respuesta
}

async function subirArchivo(base, ruta, archivo) {
  const formulario = new FormData()
  const nombre = archivo.split('/').pop()
  formulario.append('archivo', new Blob([readFileSync(archivo)]), nombre)
  const resultado = await (await enviar(base, ruta, { method: 'POST', body: formulario })).json()
  console.log(`  ${nombre}: ${resultado.registrados}/${resultado.totalRegistrosLeidos} registrados`)
}

function enJson(base, ruta, cuerpo) {
  return enviar(base, ruta, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(cuerpo),
  })
}

function leerCsv(archivo) {
  const [encabezado, ...filas] = readFileSync(archivo, 'utf8').trim().split(/\r?\n/)
  const cols = encabezado.split(',')
  return filas.map((fila) => Object.fromEntries(fila.split(',').map((v, i) => [cols[i], v])))
}

// Estado de cada actividad del cronograma 2025 en la demo: las fases 1 y 2 y las
// dos primeras de la fase 3 están completadas, la 3.3 está en curso y el resto
// está planificado (con fechas pasadas aparecen como vencidas en el seguimiento).
function estadoActividad(id) {
  if (id.startsWith('1.') || id.startsWith('2.') || id === '3.1' || id === '3.2') return 'COMPLETADA'
  return id === '3.3' ? 'EN_CURSO' : 'PLANIFICADA'
}

function actividadesDelCronograma() {
  const vistas = new Map()
  return leerCsv(join(fixtures, 'proceso2025/cronograma_actividades.csv')).map((fila) => {
    // El 2.6 aparece dos veces en el cronograma institucional: se distingue con sufijo.
    const veces = (vistas.get(fila.actividad) ?? 0) + 1
    vistas.set(fila.actividad, veces)
    const id = veces === 1 ? fila.actividad : `${fila.actividad}-${veces}`
    return {
      id,
      fase: Number(fila.fase),
      descripcion: `Actividad ${id} del cronograma de autoevaluación`,
      responsable: fila.responsables.split(';')[0],
      fechaInicioPlanificada: fila.fechaInicioPlanificada,
      fechaFinPlanificada: fila.fechaFinPlanificada,
      estado: estadoActividad(fila.actividad),
    }
  })
}

const proyectos = [
  {
    id: 'PM-01',
    programa: 'EGSTI',
    nombre: 'Fortalecimiento de la investigación formativa',
    aspectosMejora: ['Pocos productos de investigación con estudiantes', 'Semilleros sin continuidad'],
    origen: 'AUTOEVALUACION',
    objetivo: 'Aumentar la participación de estudiantes en proyectos de investigación.',
    actividades: ['Crear una línea de semilleros', 'Convocatoria anual de proyectos', 'Socialización de resultados'],
    factores: ['F4'],
    estado: 'EN_EJECUCION',
  },
  {
    id: 'PM-02',
    programa: 'EGSTI',
    nombre: 'Seguimiento a egresados',
    aspectosMejora: ['Baja respuesta de egresados en la encuesta'],
    origen: 'RECOMENDACION_CNA',
    objetivo: 'Mantener un registro actualizado de egresados y su trayectoria.',
    actividades: ['Actualizar la base de egresados', 'Encuesta anual de seguimiento'],
    factores: ['F9'],
    estado: 'PLANIFICADO',
  },
  {
    id: 'PM-03',
    programa: 'EGSTI',
    nombre: 'Actualización de recursos de laboratorio',
    aspectosMejora: ['Equipos con más de cinco años de uso'],
    origen: 'AUTOEVALUACION',
    objetivo: 'Renovar los equipos del laboratorio de redes.',
    actividades: ['Inventario de equipos', 'Plan de compras'],
    factores: ['F11'],
    estado: 'CERRADO',
  },
]

export async function sembrar(base) {
  console.log(`Sembrando datos demo en ${base}`)
  await subirArchivo(base, '/api/ponderaciones/archivos', join(fixtures, 'modelo2025/ponderaciones_modelo_2025.csv'))
  await subirArchivo(base, '/api/estudiantes/archivos', join(datos, 'estudiantes.csv'))
  await subirArchivo(base, '/api/docentes/archivos', join(datos, 'docentes.csv'))
  await subirArchivo(base, '/api/encuestas/archivos', join(datos, 'respuestas_encuesta.csv'))
  await subirArchivo(base, '/api/resultados-encuesta-pregunta/archivos', join(fixtures, 'modelo2025/resultados_encuesta_pregunta.csv'))
  await subirArchivo(base, '/api/resultados-encuesta-pregunta/archivos', join(datos, 'resultados_encuesta_pregunta_20251.csv'))
  await subirArchivo(base, '/api/evaluaciones-documentales/archivos', join(fixtures, 'modelo2025/evaluacion_documental.csv'))
  await subirArchivo(base, '/api/evaluaciones-documentales/archivos', join(datos, 'evaluacion_documental_20251.csv'))

  await enJson(base, '/api/ciclos', { id: 'EGSTI-20251', programa: 'EGSTI', periodo: '20251' })
  await enJson(base, '/api/ciclos', {
    id: 'EGSTI-20252',
    programa: 'EGSTI',
    periodo: '20252',
    actividades: actividadesDelCronograma(),
  })
  for (const fila of leerCsv(join(fixtures, 'proceso2025/grupos_evaluadores.csv'))) {
    await enJson(base, `/api/ciclos/${fila.ciclo}/grupos-evaluadores`, {
      factores: fila.factores.split(';'),
      taller: Number(fila.taller),
      fechaTaller: fila.fechaTaller || null,
    })
  }
  await subirArchivo(base, '/api/preevaluaciones/archivos', join(fixtures, 'proceso2025/preevaluaciones.csv'))
  await subirArchivo(base, '/api/participacion-actores/archivos', join(fixtures, 'proceso2025/participacion_actores.csv'))
  for (const proyecto of proyectos) await enJson(base, '/api/proyectos-mejoramiento', proyecto)
  console.log('Datos demo cargados.')
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  await sembrar(process.argv[2] ?? 'http://localhost:8090')
}

/** Textos del panel de pruebas. Se basan en docs/documento_general_sgva.html (sección 5). */

export interface AyudaPantalla {
  titulo: string
  que: string
  necesita: string
  /** Sección del documento general que explica esta pantalla. */
  ancla: string
}

/** Qué hace cada pantalla y qué datos necesita, según la ruta actual. */
export const ayudaPorRuta: Record<string, AyudaPantalla> = {
  '/': {
    titulo: 'Dashboard',
    que: 'Indicadores de estudiantes y docentes de una facultad: matrícula por cohorte, Saber Pro, formación docente, capacidad instalada y deserción.',
    necesita: 'Estudiantes y/o docentes cargados. Prueba filtrar por facultad y escribe una cohorte (por ejemplo 20211) para la deserción; después pulsa «Aplicar filtros».',
    ancla: 'web-dashboard',
  },
  '/carga': {
    titulo: 'Carga de datos',
    que: 'Sube un archivo CSV o Excel de uno de los siete tipos. Muestra cuántas filas se leyeron, registraron y omitieron (duplicados).',
    necesita: 'Un archivo con las columnas exactas de cada tipo. En el panel hay archivos de ejemplo listos. Si una fila es inválida se rechaza todo el archivo y se indica la fila.',
    ancla: 'web-carga',
  },
  '/encuestas': {
    titulo: 'Encuestas',
    que: 'Compara la percepción de cada estamento sobre cada factor (barras y radar). Es información de apoyo; no entra en la nota de autoevaluación.',
    necesita: 'Respuestas individuales de encuesta. Esas solo se cargan por API, por eso en la base limpia esta pantalla queda vacía.',
    ancla: 'web-encuestas',
  },
  '/autoevaluacion': {
    titulo: 'Autoevaluación',
    que: 'Calcula la nota por característica, factor y global: 40 % encuestas y 60 % evaluación documental, con los pesos del modelo.',
    necesita: 'Programa EGSTI y periodo 20252 (en la demo). Necesita ponderaciones, resultados de encuesta por pregunta y evaluación documental de ese programa y periodo.',
    ancla: 'web-autoevaluacion',
  },
  '/ponderaciones': {
    titulo: 'Ponderaciones',
    que: 'Muestra los pesos del modelo: 12 factores, 48 características y la mezcla encuestas/documental. Solo lectura.',
    necesita: 'El archivo de ponderaciones cargado (en la base limpia, súbelo primero en Carga de datos).',
    ancla: 'web-ponderaciones',
  },
  '/ciclos': {
    titulo: 'Ciclos',
    que: 'Lista los ciclos de autoevaluación (programa + periodo) y permite registrar uno nuevo con sus cuatro fases.',
    necesita: 'Registrar el ciclo antes de cargar sus preevaluaciones o su participación.',
    ancla: 'web-ciclos',
  },
  '/proyectos': {
    titulo: 'Proyectos',
    que: 'Plan de mejoramiento: proyectos con sus aspectos a mejorar, origen, objetivo, actividades, factores y estado.',
    necesita: 'Proyectos registrados (por API). En la demo hay tres de ejemplo.',
    ancla: 'web-proyectos',
  },
  '/comparacion': {
    titulo: 'Comparación',
    que: 'Compara los resultados de autoevaluación de un programa entre dos periodos.',
    necesita: 'Datos de autoevaluación de los dos periodos. En la demo: 20251 (datos sintéticos) y 20252.',
    ancla: 'web-comparacion',
  },
}

export function ayudaDe(ruta: string): AyudaPantalla {
  if (ruta.startsWith('/ciclos/')) {
    return {
      titulo: 'Detalle del ciclo',
      que: 'Avance por fase, actividades vencidas, semáforo de participación, grupos evaluadores y factores sin preevaluar. Desde aquí se consolidan las preevaluaciones y se descarga el PDF.',
      necesita: 'Un ciclo registrado. Prueba «Consolidar preevaluaciones» y el reporte PDF.',
      ancla: 'web-ciclos',
    }
  }
  return ayudaPorRuta[ruta] ?? ayudaPorRuta['/']
}

export interface PasoRecorrido {
  texto: string
  ruta: string
}

export const recorridoDemo: PasoRecorrido[] = [
  { texto: 'Revisa los pesos del modelo en Ponderaciones (requisito para calcular).', ruta: '/ponderaciones' },
  { texto: 'Consulta la Autoevaluación de EGSTI, periodo 20252: nota global 4,29.', ruta: '/autoevaluacion' },
  { texto: 'Mira el Dashboard: matrícula, Saber Pro y deserción (cohorte 20211).', ruta: '/' },
  { texto: 'Abre el ciclo EGSTI-20252: fases, actividades, semáforo y PDF.', ruta: '/ciclos' },
  { texto: 'Mira el plan de mejoramiento.', ruta: '/proyectos' },
  { texto: 'Compara 20251 con 20252.', ruta: '/comparacion' },
  { texto: 'Cambia a «Base limpia» y sube tus propios archivos.', ruta: '/carga' },
]

export const recorridoLimpia: PasoRecorrido[] = [
  { texto: '1. Sube las ponderaciones (tipo «Ponderaciones del modelo»).', ruta: '/carga' },
  { texto: '2. Sube estudiantes y docentes; mira el Dashboard.', ruta: '/' },
  { texto: '3. Sube resultados de encuesta por pregunta y evaluación documental (EGSTI, 20252).', ruta: '/carga' },
  { texto: '4. Consulta la Autoevaluación de EGSTI, 20252.', ruta: '/autoevaluacion' },
  { texto: '5. Registra un ciclo con id EGSTI-20252, programa EGSTI y periodo 20252.', ruta: '/ciclos' },
  { texto: '6. Sube preevaluaciones y participación, y consolida desde el detalle del ciclo.', ruta: '/carga' },
]

export interface ArchivoEjemplo {
  etiqueta: string
  archivo: string
  tipoCarga: string
}

/** Archivos de ejemplo descargables, en el orden en que conviene cargarlos. */
export const archivosEjemplo: ArchivoEjemplo[] = [
  { etiqueta: 'Ponderaciones del modelo 2025', archivo: 'ponderaciones_modelo_2025.csv', tipoCarga: 'Ponderaciones del modelo' },
  { etiqueta: 'Estudiantes', archivo: 'estudiantes.csv', tipoCarga: 'Estudiantes' },
  { etiqueta: 'Docentes', archivo: 'docentes.csv', tipoCarga: 'Docentes' },
  { etiqueta: 'Resultados de encuesta (20252)', archivo: 'resultados_encuesta_pregunta.csv', tipoCarga: 'Resultados de encuesta por pregunta' },
  { etiqueta: 'Evaluación documental (20252)', archivo: 'evaluacion_documental.csv', tipoCarga: 'Evaluación documental' },
  { etiqueta: 'Preevaluaciones (ciclo EGSTI-20252)', archivo: 'preevaluaciones.csv', tipoCarga: 'Preevaluaciones por evaluador' },
  { etiqueta: 'Participación (ciclo EGSTI-20252)', archivo: 'participacion_actores.csv', tipoCarga: 'Participación de actores' },
]

/** Documento general de la aplicación, servido junto con la demo. */
export const RUTA_DOCUMENTO = 'documentacion/documento_general_sgva.html'

export const referenciasDocumento: { etiqueta: string; ancla: string }[] = [
  { etiqueta: 'Documento general (inicio)', ancla: '' },
  { etiqueta: 'Qué hace cada módulo', ancla: 'modulos' },
  { etiqueta: 'Guía de uso de la interfaz', ancla: 'web' },
  { etiqueta: 'Formato de cada archivo a cargar', ancla: 'web-formatos' },
  { etiqueta: 'Mensajes de error frecuentes', ancla: 'web-errores' },
  { etiqueta: 'Archivo de ponderaciones', ancla: 'ponderaciones' },
  { etiqueta: 'API REST', ancla: 'api' },
  { etiqueta: 'Glosario', ancla: 'glosario' },
]

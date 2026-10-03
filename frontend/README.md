# SGVA — Frontend

Interfaz web del **SGVA** (Sistema de Gestión Visual de Datos para la Autoevaluación Académica). Consume la API REST del backend Spring Boot (ver `README.md` en la raíz del repositorio) para cargar datos, calcular indicadores de calidad y visualizar resultados de encuestas de percepción.

## Stack Tecnológico

- **React 19** + **TypeScript**
- **Vite** (dev server y build)
- **react-router-dom** (`HashRouter`) para las rutas
- **Recharts** para las gráficas
- **Oxlint** para el análisis estático

## Estructura

```
src/
├── api/            # Cliente HTTP hacia el backend (fetch), tipos y traducción de errores
├── components/      # Componentes reutilizables (gráficas, banners de error, stat tiles)
├── pages/           # Una página por ruta (Dashboard, Carga de datos, Encuestas)
├── App.tsx          # Navegación y definición de rutas
└── main.tsx         # Punto de entrada
```

### `src/api/`

- `client.ts` — funciones de acceso a cada grupo de endpoints (`indicadores`, `encuestas`, `reportes`, `cargarArchivo`), construidas sobre un helper `peticion()` que traduce respuestas no exitosas a `ErrorDeApi` y fallos de red a `ErrorDeRed`.
- `types.ts` — tipos espejo de los DTOs del dominio Kotlin (`com.example.sgva.domain`), solo los campos que el frontend consume.
- `errores.ts` — `mensajeDeError()` traduce cualquier error capturado a un mensaje apto para mostrar al usuario.

### Rutas y páginas

| Ruta         | Página          | Qué hace                                                                                                                                                                                                                                                                          |
| ------------ | --------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/`          | `DashboardPage` | Indicadores de calidad (Módulos 1 y 2): evolución de matrícula, Saber Pro, distribución de formación docente, capacidad instalada y tasa de deserción, filtrables por facultad/periodo/cohorte. Incluye la descarga del reporte ejecutivo en PDF (`GET /api/reportes/ejecutivo`). |
| `/carga`     | `UploadPage`    | Carga masiva de estudiantes o docentes desde un archivo CSV/Excel (`POST /api/{estudiantes,docentes}/archivos`), con el detalle de registros omitidos.                                                                                                                            |
| `/encuestas` | `EncuestasPage` | Ponderación Likert y matriz radar de percepción por factor y estamento (Módulo 3), filtrables por periodo.                                                                                                                                                                        |

Los endpoints de registro/consulta individual (`POST/GET /api/estudiantes`, `/api/docentes`, `/api/encuestas`) son de uso interno del backend — la vía de entrada de datos desde el frontend es siempre la carga masiva por archivo en `/carga`.

## Desarrollo

```bash
npm install
npm run dev
```

El servidor de desarrollo de Vite expone un proxy de `/api` hacia el backend (`vite.config.ts`), así que no hace falta configurar CORS: solo asegúrate de levantar primero la base de datos (`docker compose up -d` en el repositorio del backend) y de que el backend esté corriendo (por defecto en `http://localhost:8080`; puede sobreescribirse con la variable de entorno `VITE_BACKEND_URL`).

```bash
npm run build     # tsc -b && vite build
npm run lint       # oxlint
npm run preview    # sirve el build de producción localmente
```

/** Dispara en el navegador la descarga de un archivo recibido como `Blob`. */
export function descargarBlob(blob: Blob, nombreArchivo: string): void {
  const url = URL.createObjectURL(blob)
  const enlace = document.createElement('a')
  enlace.href = url
  enlace.download = nombreArchivo
  enlace.click()
  URL.revokeObjectURL(url)
}

/** Fecha local de hoy en formato ISO (`AAAA-MM-DD`), sin el desfase de `toISOString()` por zona horaria. */
export function hoyIso(): string {
  return new Date().toLocaleDateString('sv-SE')
}

export function porcentaje(fraccion: number | null): string {
  return fraccion === null ? 'Sin datos' : `${(fraccion * 100).toFixed(1)} %`
}

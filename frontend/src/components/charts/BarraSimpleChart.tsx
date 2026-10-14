import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'

interface Punto {
  etiqueta: string
  valor: number
}

interface Props {
  datos: Punto[]
  colorBarra?: string
  layout?: 'horizontal' | 'vertical'
  /** Tope fijo del eje de valores (p. ej. 5 para calificaciones); por defecto lo calcula Recharts. */
  maximo?: number
}

/** Barras de una sola serie, para comparar categorías (periodos, niveles de formación, etc.). */
export function BarraSimpleChart({ datos, colorBarra = 'var(--series-1)', layout = 'horizontal', maximo }: Props) {
  const esVertical = layout === 'vertical'
  const dominio: [number, number | 'auto'] = [0, maximo ?? 'auto']
  const marcas = maximo === undefined ? undefined : Array.from({ length: maximo + 1 }, (_, i) => i)

  return (
    <ResponsiveContainer width="100%" height={260}>
      <BarChart
        data={datos}
        layout={esVertical ? 'vertical' : 'horizontal'}
        margin={{ top: 8, right: 16, bottom: 0, left: esVertical ? 24 : 0 }}
      >
        <CartesianGrid stroke="var(--gridline)" horizontal={!esVertical} vertical={esVertical} />
        {esVertical ? (
          <>
            <XAxis type="number" domain={dominio} ticks={marcas} stroke="var(--baseline)" tick={{ fill: 'var(--text-muted)', fontSize: 12 }} />
            <YAxis
              type="category"
              dataKey="etiqueta"
              stroke="var(--baseline)"
              tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
              width={140}
            />
          </>
        ) : (
          <>
            <XAxis
              dataKey="etiqueta"
              stroke="var(--baseline)"
              tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
            />
            <YAxis
              domain={dominio}
              ticks={marcas}
              stroke="var(--baseline)"
              tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
              width={48}
            />
          </>
        )}
        <Tooltip
          contentStyle={{
            background: 'var(--surface-1)',
            border: '1px solid var(--border)',
            borderRadius: 8,
            fontSize: 13,
            color: 'var(--text-primary)',
          }}
          cursor={{ fill: 'var(--gridline)' }}
          formatter={(valor) => Number(valor).toLocaleString('es-CO')}
        />
        <Bar dataKey="valor" fill={colorBarra} radius={esVertical ? [0, 4, 4, 0] : [4, 4, 0, 0]} maxBarSize={48} />
      </BarChart>
    </ResponsiveContainer>
  )
}

import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'

interface Punto {
  etiqueta: string
  valor: number
}

interface Props {
  datos: Punto[]
  colorBarra?: string
  layout?: 'horizontal' | 'vertical'
}

/** Barras de una sola serie, para comparar categorías (periodos, niveles de formación, etc.). */
export function BarraSimpleChart({ datos, colorBarra = 'var(--series-1)', layout = 'horizontal' }: Props) {
  const esVertical = layout === 'vertical'

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
            <XAxis type="number" stroke="var(--baseline)" tick={{ fill: 'var(--text-muted)', fontSize: 12 }} />
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
            <YAxis stroke="var(--baseline)" tick={{ fill: 'var(--text-muted)', fontSize: 12 }} width={48} />
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

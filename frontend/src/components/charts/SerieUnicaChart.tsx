import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import type { IndicadorCalidad } from '../../api/types'

interface Props {
  datos: IndicadorCalidad[]
  colorSerie?: string
}

/** Línea de un solo indicador a través de los periodos (ej. evolución de matrícula). */
export function SerieUnicaChart({ datos, colorSerie = 'var(--series-1)' }: Props) {
  const ordenados = [...datos].sort((a, b) => a.periodo.localeCompare(b.periodo))

  return (
    <ResponsiveContainer width="100%" height={260}>
      <LineChart data={ordenados} margin={{ top: 8, right: 16, bottom: 0, left: 0 }}>
        <CartesianGrid stroke="var(--gridline)" vertical={false} />
        <XAxis
          dataKey="periodo"
          stroke="var(--baseline)"
          tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
        />
        <YAxis stroke="var(--baseline)" tick={{ fill: 'var(--text-muted)', fontSize: 12 }} width={48} />
        <Tooltip
          contentStyle={{
            background: 'var(--surface-1)',
            border: '1px solid var(--border)',
            borderRadius: 8,
            fontSize: 13,
            color: 'var(--text-primary)',
          }}
          formatter={(valor) => Number(valor).toLocaleString('es-CO')}
        />
        <Line
          type="monotone"
          dataKey="valor"
          stroke={colorSerie}
          strokeWidth={2}
          dot={{ r: 4, fill: colorSerie, strokeWidth: 0 }}
          activeDot={{ r: 5 }}
        />
      </LineChart>
    </ResponsiveContainer>
  )
}

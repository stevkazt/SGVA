import {
  Legend,
  PolarAngleAxis,
  PolarGrid,
  PolarRadiusAxis,
  Radar,
  RadarChart,
  ResponsiveContainer,
  Tooltip,
} from 'recharts'
import type { MatrizRadar, TipoEstamento } from '../../api/types'
import { COLOR_ESTAMENTO, ETIQUETA_ESTAMENTO } from '../colorEstamento'

interface Props {
  matriz: MatrizRadar
}

/** Un eje por factor, una serie por estamento — comparación de percepción entre estamentos. */
export function RadarFactorChart({ matriz }: Props) {
  const filas = matriz.factores.map((factor) => {
    const fila: Record<string, string | number> = { factor }
    for (const serie of matriz.series) {
      const promedio = serie.promediosPorFactor[factor]
      if (promedio !== undefined) fila[serie.estamento] = promedio
    }
    return fila
  })

  return (
    <ResponsiveContainer width="100%" height={340}>
      <RadarChart data={filas} outerRadius="70%">
        <PolarGrid stroke="var(--gridline)" />
        <PolarAngleAxis dataKey="factor" tick={{ fill: 'var(--text-muted)', fontSize: 12 }} />
        <PolarRadiusAxis
          domain={[0, 5]}
          tick={{ fill: 'var(--text-muted)', fontSize: 11 }}
          stroke="var(--baseline)"
        />
        <Tooltip
          contentStyle={{
            background: 'var(--surface-1)',
            border: '1px solid var(--border)',
            borderRadius: 8,
            fontSize: 13,
            color: 'var(--text-primary)',
          }}
          formatter={(valor, nombre) => [Number(valor).toFixed(2), ETIQUETA_ESTAMENTO[nombre as TipoEstamento]]}
        />
        <Legend
          formatter={(valor: string) => ETIQUETA_ESTAMENTO[valor as TipoEstamento]}
          wrapperStyle={{ fontSize: 12, color: 'var(--text-secondary)' }}
        />
        {matriz.series.map((serie) => (
          <Radar
            key={serie.estamento}
            name={serie.estamento}
            dataKey={serie.estamento}
            stroke={COLOR_ESTAMENTO[serie.estamento]}
            fill={COLOR_ESTAMENTO[serie.estamento]}
            fillOpacity={0.15}
            strokeWidth={2}
          />
        ))}
      </RadarChart>
    </ResponsiveContainer>
  )
}

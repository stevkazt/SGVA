import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import type { PonderacionLikert, TipoEstamento } from '../../api/types'
import { COLOR_ESTAMENTO, ETIQUETA_ESTAMENTO } from '../colorEstamento'

interface Props {
  datos: PonderacionLikert[]
}

const ORDEN_ESTAMENTOS: TipoEstamento[] = ['ESTUDIANTE', 'PROFESOR', 'EGRESADO', 'EMPLEADOR']

/** Barras agrupadas: un grupo por factor, una barra por estamento presente en los datos. */
export function LikertChart({ datos }: Props) {
  const factores = [...new Set(datos.map((d) => d.factor))].sort()
  const estamentosPresentes = ORDEN_ESTAMENTOS.filter((e) => datos.some((d) => d.estamento === e))

  const filas = factores.map((factor) => {
    const fila: Record<string, string | number> = { factor }
    for (const estamento of estamentosPresentes) {
      const punto = datos.find((d) => d.factor === factor && d.estamento === estamento)
      if (punto) fila[estamento] = punto.promedio
    }
    return fila
  })

  return (
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={filas} margin={{ top: 8, right: 16, bottom: 0, left: 0 }}>
        <CartesianGrid stroke="var(--gridline)" vertical={false} />
        <XAxis dataKey="factor" stroke="var(--baseline)" tick={{ fill: 'var(--text-muted)', fontSize: 12 }} />
        <YAxis
          domain={[0, 5]}
          stroke="var(--baseline)"
          tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
          width={32}
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
        {estamentosPresentes.map((estamento) => (
          <Bar
            key={estamento}
            dataKey={estamento}
            name={estamento}
            fill={COLOR_ESTAMENTO[estamento]}
            radius={[3, 3, 0, 0]}
            maxBarSize={28}
          />
        ))}
      </BarChart>
    </ResponsiveContainer>
  )
}

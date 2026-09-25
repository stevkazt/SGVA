interface Props {
  etiqueta: string
  valor: string
  meta?: string
}

export function StatTile({ etiqueta, valor, meta }: Props) {
  return (
    <div className="stat-tile">
      <span className="stat-tile__label">{etiqueta}</span>
      <span className="stat-tile__value">{valor}</span>
      {meta && <span className="stat-tile__meta">{meta}</span>}
    </div>
  )
}

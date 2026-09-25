interface Props {
  mensaje: string
}

export function ErrorBanner({ mensaje }: Props) {
  return (
    <div className="error-banner" role="alert">
      <span aria-hidden="true">⚠</span>
      <span>{mensaje}</span>
    </div>
  )
}

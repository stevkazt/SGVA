export type TonoInsignia = 'bueno' | 'alerta' | 'critico' | 'neutro'

interface Props {
  texto: string
  tono: TonoInsignia
}

/** Etiqueta de estado; el color nunca va solo, siempre acompaña un texto. */
export function Insignia({ texto, tono }: Props) {
  return <span className={`insignia insignia--${tono}`}>{texto}</span>
}

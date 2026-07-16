export function formatearHoraAMPM(hora) {
  if (!hora) return ''
  const [hh, mm] = hora.split(':').map(Number)
  if (isNaN(hh) || isNaN(mm)) return hora
  const periodo = hh >= 12 ? 'PM' : 'AM'
  const h12 = hh % 12 || 12
  return `${String(h12).padStart(2, '0')}:${String(mm).padStart(2, '0')} ${periodo}`
}

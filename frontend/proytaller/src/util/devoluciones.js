const COLORES_ESTADO = {
  BORRADOR: '#9E9E9E',
  CONFIRMADA: '#1976D2',
  ENVIADA: '#F57C00',
  EN_PROCESO: '#7B1FA2',
  COMPLETADA: '#2E7D32',
  CANCELADA: '#5B6675'
}

export function colorEstadoDevolucion(estado) {
  return COLORES_ESTADO[estado] || '#5B6675'
}

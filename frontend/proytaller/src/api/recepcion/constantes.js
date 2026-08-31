

const colorEstadoRecepcion = {
  EN_REVISION: '#1976D2',
  PARCIAL: '#F57C00',
  COMPLETADA: '#2E7D32',
  RECHAZADA: '#D32F2F',
  ANULADA: '#5B6675'
}

export const colorEstadoR = (estado) => colorEstadoRecepcion[estado] || '#5B6675'

const colorEstadoDistribucion = {
  PENDIENTE_DE_DISTRIBUIR: '#F57C00',
  PARCIALMENTE_DISTRIBUIDA: '#1976D2',
  DISTRIBUIDA: '#2E7D32'
}

export const colorEstadoD = (estado) => colorEstadoDistribucion[estado] || '#5B6675'
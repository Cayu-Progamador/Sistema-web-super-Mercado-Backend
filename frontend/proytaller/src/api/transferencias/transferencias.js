import request from '../../util/request'
import { Dialog } from 'quasar'
import ConfirmarCancelacionTransferenciaDialog from '../../components/transferencias/ConfirmarCancelacionTransferenciaDialog.vue'
import ConfirmarEliminarBorradorDialog from '../../components/transferencias/ConfirmarEliminarBorradorDialog.vue'
import ConfirmarProcesarTransferenciaDialog from '../../components/transferencias/ConfirmarProcesarTransferenciaDialog.vue'

export function listarTransferencias(params) {
  console.log('[Transferencias] Consultando lista:', params)
  return request({ url: '/api/transferencias', method: 'get', params })
    .then(response => {
      console.log('[Transferencias] Respuesta recibida:', response)
      return response
    })
    .catch(error => {
      console.error('[Transferencias] Error al consultar:', error?.response?.status, error?.response?.data || error)
      throw error
    })
}

export function listarEstadosTransferencia() {
  console.log('[Transferencias] Consultando estados')
  return request({ url: '/api/transferencias/estados', method: 'get' })
    .then(response => {
      console.log('[Transferencias] Estados recibidos:', response)
      return response
    })
    .catch(error => {
      console.error('[Transferencias] Error al consultar estados:', error?.response?.status, error?.response?.data || error)
      throw error
    })
}

export function crearTransferencia(data) {
  const borradorId = data?.idTransferencia || window.__transferenciaBorradorId
  if (!data?.borrador && borradorId) {
    const payload = { ...data }
    delete payload.borrador
    delete payload.idTransferencia
    delete window.__transferenciaBorradorId
    return request({ url: `/api/transferencias/${borradorId}`, method: 'put', data: payload })
      .then(() => request({ url: `/api/transferencias/${borradorId}/confirmar`, method: 'patch' }))
  }
  if (data?.borrador && borradorId) {
    const payload = { ...data }
    delete payload.borrador
    delete payload.idTransferencia
    delete window.__transferenciaBorradorId
    return request({ url: `/api/transferencias/${borradorId}`, method: 'put', data: payload })
  }
  const esBorrador = Boolean(data?.borrador)
  const payload = { ...data }
  delete payload.borrador
  return request({ url: esBorrador ? '/api/transferencias/borradores' : '/api/transferencias', method: 'post', data: payload })
}

export function guardarBorradorTransferencia(data) {
  return request({ url: '/api/transferencias/borradores', method: 'post', data })
}

export function actualizarBorradorTransferencia(id, data) {
  return request({ url: `/api/transferencias/${id}`, method: 'put', data })
}

export function procesarTransferencia(id) {
  return new Promise((resolve, reject) => {
    Dialog.create({ component: ConfirmarProcesarTransferenciaDialog })
      .onOk(() => request({ url: `/api/transferencias/${id}/procesar`, method: 'patch' }).then(resolve).catch(reject))
      .onCancel(() => {})
  })
}
export function completarTransferencia(id) { return request({ url: `/api/transferencias/${id}/completar`, method: 'patch' }) }
export function cancelarTransferencia(id) {
  return new Promise((resolve, reject) => {
    Dialog.create({ component: ConfirmarCancelacionTransferenciaDialog })
      .onOk(() => request({ url: `/api/transferencias/${id}/cancelar`, method: 'patch' }).then(resolve).catch(reject))
      .onCancel(() => {})
  })
}
export function eliminarBorradorTransferencia(id) {
  return new Promise((resolve, reject) => {
    Dialog.create({ component: ConfirmarEliminarBorradorDialog })
      .onOk(() => request({ url: `/api/transferencias/${id}`, method: 'delete' }).then(resolve).catch(reject))
      .onCancel(() => {})
  })
}

export function descargarPdfTransferencia(id) {
  return request({ url: `/api/transferencias/${id}/pdf`, method: 'get', responseType: 'blob' })
}

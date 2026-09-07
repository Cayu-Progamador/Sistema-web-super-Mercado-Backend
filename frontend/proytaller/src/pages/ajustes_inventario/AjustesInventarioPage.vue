<template>
  <q-page class="page">
    <div class="container">
      <header class="inventory-header">
        <div>
          <div class="title-row">
            <q-icon name="tune" color="green-8" size="32px" />
            <h1>Gestion de ajustes de inventario</h1>
          </div>
          <p>Controla entradas y salidas justificadas con trazabilidad.</p>
        </div>
        <q-btn unelevated no-caps color="green-8" icon="add" label="Nuevo ajuste" @click="abrirNuevo" />
      </header>

      <div class="cards row q-col-gutter-md q-mb-lg">
        <div v-for="card in cards" :key="card.label" class="col-6 col-md-3">
          <q-card flat bordered class="card">
            <q-card-section class="row items-center no-wrap">
              <div class="card-icon" :class="card.tone">
                <q-icon :name="card.icon" size="22px" />
              </div>
              <div class="q-ml-md">
                <div class="card-label">{{ card.label }}</div>
                <div class="card-value">{{ card.value }}</div>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <AjustesFiltros @filtrar="filtrar" @limpiar="limpiar" />

      <div class="row items-center justify-between q-mb-md">
        <div class="list-title">
          Ajustes registrados
          <q-badge color="green-8">{{ pagination.rowsNumber }}</q-badge>
        </div>
      </div>

      <AjustesTabla
        :rows="filas"
        :pagination="pagination"
        :loading="cargando"
        @request="cargar"
        @ver="ver"
        @editar="editar"
        @eliminar="eliminar"
        @enviar="enviar"
        @aprobar="aprobar"
        @rechazar="rechazar"
        @aplicar="aplicar"
        @pdf="pdf"
      />
    </div>

    <NuevoAjusteDialog v-model="nuevo" :ajuste-editar="ajusteEditar" @guardar="guardar" />
    <DetalleAjusteDialog v-model="detalle" :ajuste="seleccionado" @pdf="pdf" />
    <ConfirmarEliminarAjusteDialog v-model="eliminarDialog" :ajuste="borradorEliminar" :loading="procesando" @confirmar="confirmarEliminacion" />
    <ConfirmarEnviarAjusteDialog v-model="enviarDialog" :ajuste="ajusteEnviar" :loading="procesando" @confirmar="confirmarEnvio" />
    <ConfirmarRechazarAjusteDialog v-model="rechazoDialog" :ajuste="ajusteRechazar" :loading="procesando" @confirmar="confirmarRechazo" />
    <ConfirmarAprobarAjusteDialog v-model="aprobacionDialog" :ajuste="ajusteAprobar" :loading="procesando" @confirmar="confirmarAprobacion" />
    <ConfirmarAplicarAjusteDialog v-model="aplicacionDialog" :ajuste="ajusteAplicar" :loading="procesando" @confirmar="confirmarAplicacion" />

    <q-dialog v-model="confirmacion" persistent>
      <q-card class="confirm">
        <q-card-section>
          <div class="confirm-title">
            <q-icon name="warning_amber" color="orange-8" size="28px" />
            {{ accionTitulo }}
          </div>
          <p>{{ accionTexto }}</p>
          <q-input
            v-if="accion === 'rechazar'"
            v-model="motivo"
            outlined
            type="textarea"
            autogrow
            label="Motivo obligatorio"
          />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat no-caps label="Cancelar" v-close-popup />
          <q-btn unelevated no-caps color="green-8" label="Confirmar" :loading="procesando" @click="confirmarAccion" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import {
  listarAjustes,
  obtenerAjuste,
  descargarAjustePdf,
  crearAjuste,
  actualizarBorradorAjuste,
  enviarAjuste,
  aprobarAjuste,
  rechazarAjuste,
  aplicarAjuste,
  eliminarBorradorAjuste
} from '../../api/ajustesInventario/ajustesInventario'
import AjustesFiltros from '../../components/ajustes_inventario/AjustesFiltros.vue'
import AjustesTabla from '../../components/ajustes_inventario/AjustesTabla.vue'
import NuevoAjusteDialog from '../../components/ajustes_inventario/NuevoAjusteDialog.vue'
import DetalleAjusteDialog from '../../components/ajustes_inventario/DetalleAjusteDialog.vue'
import ConfirmarEliminarAjusteDialog from '../../components/ajustes_inventario/ConfirmarEliminarAjusteDialog.vue'
import ConfirmarEnviarAjusteDialog from '../../components/ajustes_inventario/ConfirmarEnviarAjusteDialog.vue'
import ConfirmarRechazarAjusteDialog from '../../components/ajustes_inventario/ConfirmarRechazarAjusteDialog.vue'
import ConfirmarAprobarAjusteDialog from '../../components/ajustes_inventario/ConfirmarAprobarAjusteDialog.vue'
import ConfirmarAplicarAjusteDialog from '../../components/ajustes_inventario/ConfirmarAplicarAjusteDialog.vue'

const $q = useQuasar()
const nuevo = ref(false)
const detalle = ref(false)
const confirmacion = ref(false)
const eliminarDialog = ref(false)
const enviarDialog = ref(false)
const rechazoDialog = ref(false)
const aprobacionDialog = ref(false)
const aplicacionDialog = ref(false)
const seleccionado = ref(null)
const ajusteEditar = ref(null)
const borradorEliminar = ref(null)
const ajusteEnviar = ref(null)
const ajusteRechazar = ref(null)
const ajusteAprobar = ref(null)
const ajusteAplicar = ref(null)
const accion = ref('')
const motivo = ref('')
const pagination = ref({ page: 1, rowsPerPage: 5, rowsNumber: 0 })
const filtros = ref({})
const filas = ref([])
const cargando = ref(false)
const procesando = ref(false)

const cards = computed(() => [
  { label: 'Total ajustes', value: pagination.value.rowsNumber, icon: 'tune' },
  { label: 'Pendientes aprobacion', value: filas.value.filter((x) => x.estado === 'PENDIENTE_APROBACION').length, icon: 'schedule', tone: 'warning' },
  { label: 'Aplicados', value: filas.value.filter((x) => x.estado === 'APLICADO').length, icon: 'check_circle' },
  { label: 'Salidas del periodo', value: filas.value.filter((x) => x.tipo === 'SALIDA').length, icon: 'trending_down', tone: 'danger' }
])

const accionTitulo = computed(() => ({
  enviar: 'Enviar a aprobación',
  aprobar: 'Aprobar ajuste',
  rechazar: 'Rechazar ajuste',
  aplicar: 'Aplicar ajuste'
}[accion.value] || 'Confirmar accion'))

const accionTexto = computed(() => accion.value === 'enviar'
  ? 'El ajuste dejará de ser editable y quedará pendiente de aprobación.'
  : accion.value === 'aplicar'
    ? 'Se actualizara el stock y se registrara el movimiento en Kardex.'
    : 'Confirma que deseas realizar esta operacion.')

function mapearFila(item) {
  return {
    ...item,
    id: item.id,
    fecha: item.fechaCreacion?.slice(0, 10) || '--',
    productos: Number(item.productos || 0),
    cantidad: Number(item.cantidadTotal || 0),
    solicitante: item.creadoPor || '--',
    almacen: item.almacen || '--',
    ubicacion: item.ubicacion || '--'
  }
}

async function cargar(ctx) {
  const p = ctx?.pagination || pagination.value
  pagination.value = { ...pagination.value, ...p }
  cargando.value = true
  const camposOrden = { ajuste: 'codigo', fecha: 'fechaCreacion', tipo: 'tipo', estado: 'estado' }
  try {
    const respuesta = await listarAjustes({
      buscar: filtros.value.buscar || undefined,
      tipo: filtros.value.tipo || undefined,
      estado: filtros.value.estado || undefined,
      fechaDesde: filtros.value.desde || undefined,
      fechaHasta: filtros.value.hasta || undefined,
      page: p.page - 1,
      size: p.rowsPerPage,
      sortBy: camposOrden[p.sortBy] || 'fechaCreacion',
      sortDir: p.descending ? 'desc' : 'asc'
    })
    filas.value = (respuesta?.content || []).map(mapearFila)
    pagination.value.rowsNumber = Number(respuesta?.totalElements || 0)
  } catch (error) {
    filas.value = []
    pagination.value.rowsNumber = 0
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudieron cargar los ajustes.' })
  } finally {
    cargando.value = false
  }
}

function construirPayload(formulario) {
  return {
    tipo: formulario.tipo,
    motivo: formulario.motivo,
    observacion: formulario.observacion,
    fechaLevantamiento: formulario.fechaLevantamiento || null,
    responsableLevantamiento: formulario.responsable || null,
    lugarHallazgo: formulario.lugarHallazgo || null,
    fechaHallazgo: formulario.fechaHallazgo || null,
    encontradoPor: formulario.encontradoPor || null,
    detalles: (formulario.productos || []).map((item) => ({
      idProducto: item.producto,
      idPresentacion: Number(item.presentacionId || 0) > 0 ? item.presentacionId : null,
      cantidadPresentacion: item.cantidadPresentacion ? Number(item.cantidadPresentacion) : null,
      idLote: item.lote || null,
      codigoLote: item.codigoLote || null,
      fechaFabricacion: item.fechaFabricacion || null,
      fechaVencimiento: item.fechaVencimiento || null,
      idUbicacion: item.ubicacion,
      cantidad: Number(item.cantidad),
      costoUnitario: Number(item.costo || 0),
      tipoMerma: formulario.tipoMerma || null,
      causaMerma: formulario.causaMerma || null
    }))
  }
}

async function guardar(formulario) {
  try {
    const payload = construirPayload(formulario)
    if (formulario.idAjuste) {
      await actualizarBorradorAjuste(formulario.idAjuste, payload)
      $q.notify({ type: 'positive', message: 'Borrador actualizado correctamente.' })
    } else {
      await crearAjuste(payload)
      $q.notify({ type: 'positive', message: 'Ajuste guardado como borrador.' })
    }
    ajusteEditar.value = null
    await cargar()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo guardar el ajuste.' })
  }
}

function abrirNuevo() {
  ajusteEditar.value = null
  nuevo.value = true
}

async function ver(row) {
  try {
    seleccionado.value = await obtenerAjuste(row.id)
    detalle.value = true
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo cargar el detalle.' })
  }
}

async function editar(row) {
  try {
    ajusteEditar.value = await obtenerAjuste(row.id)
    nuevo.value = true
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo cargar el borrador.' })
  }
}

function eliminar(row) {
  borradorEliminar.value = row
  eliminarDialog.value = true
}

async function confirmarEliminacion(row) {
  procesando.value = true
  try {
    await eliminarBorradorAjuste(row.id)
    eliminarDialog.value = false
    $q.notify({ type: 'positive', message: 'Borrador eliminado.' })
    await cargar()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo eliminar el borrador.' })
  } finally {
    procesando.value = false
  }
}

function abrirAccion(row, tipoAccion) {
  seleccionado.value = row
  accion.value = tipoAccion
  motivo.value = ''
  confirmacion.value = true
}

async function aprobar(row) {
  try {
    ajusteAprobar.value = await obtenerAjuste(row.id)
    aprobacionDialog.value = true
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo cargar el detalle del ajuste.' })
  }
}
async function enviar(row) {
  try {
    ajusteEnviar.value = await obtenerAjuste(row.id)
    enviarDialog.value = true
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo cargar el detalle del borrador.' })
  }
}
async function rechazar(row) {
  try {
    ajusteRechazar.value = await obtenerAjuste(row.id)
    rechazoDialog.value = true
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo cargar el detalle del ajuste.' })
  }
}
async function aplicar(row) {
  try {
    ajusteAplicar.value = await obtenerAjuste(row.id)
    aplicacionDialog.value = true
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo cargar el detalle del ajuste.' })
  }
}

async function confirmarEnvio(ajuste) {
  procesando.value = true
  try {
    await enviarAjuste(ajuste.id)
    enviarDialog.value = false
    $q.notify({ type: 'positive', message: 'Ajuste enviado a aprobación.' })
    await cargar()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo enviar el ajuste a aprobación.' })
  } finally {
    procesando.value = false
  }
}

async function confirmarRechazo({ ajuste, motivo: motivoRechazo }) {
  procesando.value = true
  try {
    await rechazarAjuste(ajuste.id, motivoRechazo)
    rechazoDialog.value = false
    $q.notify({ type: 'positive', message: 'Ajuste rechazado correctamente.' })
    await cargar()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo rechazar el ajuste.' })
  } finally {
    procesando.value = false
  }
}

async function confirmarAprobacion(ajuste) {
  procesando.value = true
  try {
    await aprobarAjuste(ajuste.id)
    aprobacionDialog.value = false
    $q.notify({ type: 'positive', message: 'Ajuste aprobado correctamente.' })
    await cargar()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo aprobar el ajuste.' })
  } finally {
    procesando.value = false
  }
}

async function confirmarAplicacion(ajuste) {
  procesando.value = true
  try {
    await aplicarAjuste(ajuste.id)
    aplicacionDialog.value = false
    $q.notify({ type: 'positive', message: 'Ajuste aplicado. Stock y Kardex actualizados.' })
    await cargar()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo aplicar el ajuste.' })
  } finally {
    procesando.value = false
  }
}

async function confirmarAccion() {
  if (accion.value === 'rechazar' && !motivo.value.trim()) {
    $q.notify({ type: 'warning', message: 'El motivo es obligatorio.' })
    return
  }

  procesando.value = true
  try {
    if (accion.value === 'aprobar') await aprobarAjuste(seleccionado.value.id)
    if (accion.value === 'rechazar') await rechazarAjuste(seleccionado.value.id, motivo.value.trim())
    if (accion.value === 'aplicar') await aplicarAjuste(seleccionado.value.id)
    confirmacion.value = false
    $q.notify({ type: 'positive', message: `Ajuste ${seleccionado.value.codigo} actualizado.` })
    await cargar()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo actualizar el ajuste.' })
  } finally {
    procesando.value = false
  }
}

async function pdf(row) {
  try {
    const archivo = await descargarAjustePdf(row.id)
    const url = URL.createObjectURL(archivo)
    const enlace = document.createElement('a')
    enlace.href = url
    enlace.download = `ajuste_${row.codigo || row.id}.pdf`
    document.body.appendChild(enlace)
    enlace.click()
    enlace.remove()
    URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: 'Comprobante PDF descargado correctamente.' })
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo descargar el comprobante PDF.' })
  }
}

function filtrar(value) {
  filtros.value = value
  pagination.value.page = 1
  cargar()
}

function limpiar() {
  filtros.value = {}
  pagination.value.page = 1
  cargar()
}

onMounted(() => cargar())
</script>

<style scoped>
.page { min-height: 100%; background: #fff; color: #263238; font-family: 'Nunito', sans-serif; }
.container { max-width: 1500px; margin: auto; padding: 24px; }
.inventory-header { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-bottom: 24px; }
.title-row { display: flex; align-items: center; gap: 10px; }
.inventory-header h1 { margin: 0; font-size: 28px; font-weight: 900; }
.inventory-header p { margin: 4px 0; color: #6b7280; font-size: 13px; }
.card { border-radius: 16px; border-color: #dfe7dc; background: #fff; }
.card-icon { width: 44px; height: 44px; border-radius: 13px; display: grid; place-items: center; background: #eaf4d8; color: #4a8c25; }
.card-icon.warning { background: #fff4db; color: #b76b00; }
.card-icon.danger { background: #ffebee; color: #c62828; }
.card-label { font-size: 11px; color: #7b857c; }
.card-value { font-size: 24px; font-weight: 900; color: #356d20; }
.list-title { font-size: 16px; font-weight: 900; }
.confirm { width: min(480px, 94vw); border-radius: 18px; }
.confirm-title { display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 900; }
.confirm p { color: #667067; font-size: 13px; line-height: 1.6; }
@media (max-width: 700px) {
  .container { padding: 14px; }
  .inventory-header { align-items: flex-start; flex-direction: column; }
  .inventory-header > .q-btn { width: 100%; }
  .inventory-header h1 { font-size: 23px; }
}
</style>

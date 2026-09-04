<template>
  <div class="vencimientos-tab">
    <div class="search-toolbar">
      <q-input
        v-model="filtro.buscar"
        outlined
        dense
        clearable
        debounce="400"
        label="Buscar producto o lote"
      >
        <template #prepend>
          <q-icon name="search" />
        </template>
      </q-input>
      <q-btn flat round icon="filter_alt" @click="filtrosAbiertos = !filtrosAbiertos">
        <q-tooltip>Mostrar filtros</q-tooltip>
      </q-btn>
    </div>

    <q-card v-show="filtrosAbiertos" flat bordered class="filters-card">
      <q-card-section class="row q-col-gutter-md">
        <q-select
          v-model="filtro.estado"
          outlined
          dense
          clearable
          emit-value
          map-options
          label="Estado"
          :options="estados"
        />
        <q-input
          v-model="filtro.fechaDesde"
          outlined
          dense
          type="date"
          label="Vence desde"
        />
        <q-input
          v-model="filtro.fechaHasta"
          outlined
          dense
          type="date"
          label="Vence hasta"
        />
        <q-btn
          unelevated
          no-caps
          color="green-8"
          icon="filter_alt"
          label="Filtrar"
          @click="aplicarFiltros"
        />
        <q-btn
          outline
          no-caps
          color="green-8"
          icon="restart_alt"
          label="Limpiar"
          @click="limpiarFiltros"
        />
        <q-btn
          outline
          no-caps
          color="green-8"
          icon="picture_as_pdf"
          label="Descargar PDF"
          :loading="descargandoPdf"
          @click="descargarPdf"
        />
      </q-card-section>
    </q-card>

    <div class="row q-col-gutter-md cards">
      <div
        v-for="card in cards"
        :key="card.key"
        class="col-12 col-sm-6 col-md-3"
      >
        <q-card flat bordered class="metric-card">
          <q-card-section class="row items-center no-wrap">
            <div class="metric-icon" :class="card.tone">
              <q-icon :name="card.icon" size="24px" />
            </div>
            <div class="q-ml-md">
              <div class="metric-label">{{ card.label }}</div>
              <div class="metric-value">{{ card.value }}</div>
              <div class="metric-help">{{ card.help }}</div>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>

    <q-card flat bordered class="table-card">
      <q-table
        v-model:pagination="pagination"
        :rows="filas"
        :columns="columns"
        :loading="cargando"
        row-key="idExistencia"
        rows-per-page-label="Registros por página"
        no-data-label="No hay vencimientos"
        @request="cargarListado"
      >
        <template #body-cell-estado="propsTabla">
          <q-td :props="propsTabla">
            <q-badge
              :color="colorEstado(propsTabla.value)"
              :label="propsTabla.value"
            />
          </q-td>
        </template>

        <template #body-cell-acciones="propsTabla">
          <q-td :props="propsTabla" class="actions-cell">
            <q-btn
              flat
              round
              dense
              icon="receipt_long"
              color="grey-7"
              @click="emit('kardex', propsTabla.row)"
            >
              <q-tooltip>Ver Kardex</q-tooltip>
            </q-btn>
            <q-btn
              v-if="esVencido(propsTabla.row)"
              flat
              round
              dense
              icon="delete_sweep"
              color="negative"
              @click="emit('baja', propsTabla.row)"
            >
              <q-tooltip>Dar de baja</q-tooltip>
            </q-btn>
          </q-td>
        </template>
      </q-table>
    </q-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useQuasar } from 'quasar'

import {
  descargarVencimientosPdf,
  listarVencimientos,
  obtenerResumenVencimientos
} from '../../api/vencimiento/vencimiento'

const emit = defineEmits(['kardex', 'baja'])
const $q = useQuasar()

const filas = ref([])
const resumen = ref({
  vencidos: 0,
  vencenHoy: 0,
  proximos7Dias: 0,
  proximos30Dias: 0
})
const cargando = ref(false)
const descargandoPdf = ref(false)
const filtrosAbiertos = ref(false)

const filtro = reactive({
  buscar: '',
  estado: null,
  fechaDesde: '',
  fechaHasta: ''
})
const aplicado = ref({ ...filtro })

const pagination = ref({
  page: 1,
  rowsPerPage: 8,
  rowsNumber: 0,
  sortBy: 'fechaVencimiento',
  descending: false
})

const estados = [
  { label: 'Normal', value: 'NORMAL' },
  { label: 'Próximo', value: 'PROXIMO' },
  { label: 'Urgente', value: 'URGENTE' },
  { label: 'Vence hoy', value: 'VENCE_HOY' },
  { label: 'Vencido', value: 'VENCIDO' }
]

const cards = computed(() => [
  {
    key: 'vencidos',
    label: 'Vencidos',
    value: resumen.value.vencidos,
    icon: 'event_busy',
    tone: 'red',
    help: 'Lotes fuera de fecha'
  },
  {
    key: 'hoy',
    label: 'Vencen hoy',
    value: resumen.value.vencenHoy,
    icon: 'today',
    tone: 'orange',
    help: 'Requieren atención hoy'
  },
  {
    key: 'siete-dias',
    label: 'Próximos 7 días',
    value: resumen.value.proximos7Dias,
    icon: 'schedule',
    tone: 'amber',
    help: 'Vencen durante esta semana'
  },
  {
    key: 'treinta-dias',
    label: 'Próximos 30 días',
    value: resumen.value.proximos30Dias,
    icon: 'date_range',
    tone: 'green',
    help: 'Vencen entre 8 y 30 días'
  }
])

const columns = [
  { name: 'codigoProducto', label: 'Código', field: 'codigoProducto', align: 'left' },
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left' },
  { name: 'lote', label: 'Lote', field: 'lote', align: 'left' },
  { name: 'fechaVencimiento', label: 'Vencimiento', field: 'fechaVencimiento', align: 'left' },
  { name: 'diasRestantes', label: 'Días', field: 'diasRestantes', align: 'right' },
  { name: 'cantidad', label: 'Cantidad', field: 'cantidad', align: 'right' },
  { name: 'unidad', label: 'Unidad', field: 'unidad', align: 'left' },
  { name: 'almacen', label: 'Almacén', field: 'almacen', align: 'left' },
  { name: 'ubicacion', label: 'Ubicación', field: 'ubicacion', align: 'left' },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'left' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
]

async function cargarListado({ pagination: pagina = pagination.value } = {}) {
  cargando.value = true

  try {
    const response = await listarVencimientos({
      page: pagina.page - 1,
      size: pagina.rowsPerPage,
      buscar: aplicado.value.buscar?.trim() || undefined,
      estado: aplicado.value.estado || undefined,
      fechaDesde: aplicado.value.fechaDesde || undefined,
      fechaHasta: aplicado.value.fechaHasta || undefined
    })

    filas.value = response?.content || []
    pagination.value = {
      ...pagina,
      rowsNumber: response?.totalElements || 0
    }
  } catch (error) {
    filas.value = []
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'No se pudieron cargar los vencimientos.'
    })
  } finally {
    cargando.value = false
  }
}

async function cargarResumen() {
  try {
    resumen.value = await obtenerResumenVencimientos()
  } catch (error) {
    $q.notify({
      type: 'warning',
      message: 'No se pudo cargar el resumen de vencimientos.'
    })
  }
}

function aplicarFiltros() {
  aplicado.value = { ...filtro }
  pagination.value.page = 1
  cargarListado({ pagination: pagination.value })
}

function limpiarFiltros() {
  Object.assign(filtro, {
    buscar: '',
    estado: null,
    fechaDesde: '',
    fechaHasta: ''
  })
  aplicarFiltros()
}

async function descargarPdf() {
  descargandoPdf.value = true
  try {
    const archivo = await descargarVencimientosPdf({
      buscar: aplicado.value.buscar?.trim() || undefined,
      estado: aplicado.value.estado || undefined,
      fechaDesde: aplicado.value.fechaDesde || undefined,
      fechaHasta: aplicado.value.fechaHasta || undefined
    })
    const url = URL.createObjectURL(archivo)
    const enlace = document.createElement('a')
    enlace.href = url
    enlace.download = `vencimientos_${new Date().toISOString().slice(0, 10)}.pdf`
    document.body.appendChild(enlace)
    enlace.click()
    enlace.remove()
    URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: 'PDF de vencimientos descargado.' })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'No se pudo descargar el PDF de vencimientos.'
    })
  } finally {
    descargandoPdf.value = false
  }
}

function colorEstado(estado) {
  return {
    NORMAL: 'green-7',
    'PRÓXIMO': 'amber-8',
    URGENTE: 'orange-8',
    VENCE_HOY: 'deep-orange-8',
    VENCIDO: 'red-6'
  }[estado] || 'grey-7'
}

function esVencido(fila) {
  const estado = String(fila.estado || '').trim().toUpperCase()
  if (estado === 'VENCIDO') return true
  if (Number(fila.diasRestantes) < 0) return true
  if (!fila.fechaVencimiento) return false

  const hoy = new Date()
  hoy.setHours(0, 0, 0, 0)
  const vencimiento = new Date(`${fila.fechaVencimiento}T00:00:00`)
  return vencimiento < hoy
}

watch(
  () => filtro.buscar,
  () => aplicarFiltros()
)

onMounted(async () => {
  await Promise.all([
    cargarListado({ pagination: pagination.value }),
    cargarResumen()
  ])
})

defineExpose({
  actualizar: async () => {
    await Promise.all([
      cargarListado({ pagination: pagination.value }),
      cargarResumen()
    ])
  }
})
</script>

<style scoped>
.vencimientos-tab{width:100%;padding:12px 8px}.search-toolbar{display:flex;align-items:center;gap:10px;width:100%;max-width:680px;margin:0 16px 14px;padding:4px 0}.search-toolbar .q-input{flex:1}.search-toolbar :deep(.q-field__control){border-radius:12px;background:#fff}.search-toolbar>.q-btn{width:42px;height:42px;border:1px solid #c8dfc1;border-radius:12px;background:#f0f8e9}.filters-card,.table-card,.metric-card{border-color:#dfe7dc!important;border-radius:16px}.filters-card{margin:0 16px 24px;background:#fff}.filters-card :deep(.q-card__section){display:flex;align-items:flex-end;gap:10px;padding:18px}.filters-card :deep(.q-field){min-width:180px;flex:1}.filters-card :deep(.q-btn){min-height:40px}.cards{margin:0 0 24px;padding:8px}.metric-card{min-height:86px}.actions-cell{white-space:nowrap}:deep(.q-table thead tr){background:#f0f8e9}:deep(.q-table tbody td){height:58px}:deep(.q-table__middle){overflow-x:auto}:deep(.q-table){min-width:1200px}@media(max-width:700px){.vencimientos-tab{padding:8px 4px}.search-toolbar{margin:0 4px 14px;max-width:none}.filters-card{margin:0 0 16px}.filters-card :deep(.q-card__section){padding:12px}.filters-card :deep(.q-field){min-width:100%;flex-basis:100%}.filters-card :deep(.q-btn){flex:1}.cards{padding:4px;margin-bottom:14px}.metric-card{min-height:72px}}

.metric-card {
  height: 100%;
  border-radius: 15px;
}

.metric-icon {
  width: 48px;
  height: 48px;
  flex: 0 0 48px;
  border-radius: 13px;
  display: grid;
  place-items: center;
  color: #fff;
}

.metric-icon.green { background: #4a8c25; }
.metric-icon.amber { background: #d59a32; }
.metric-icon.red { background: #c95f58; }
.metric-icon.orange { background: #df8033; }

.metric-label {
  font-size: 12px;
  color: #71806c;
  font-weight: 800;
}

.metric-value {
  font-size: 25px;
  color: #29472d;
  font-weight: 900;
  line-height: 1.15;
}

.metric-help {
  margin-top: 3px;
  font-size: 11px;
  color: #9aa39a;
}
</style>

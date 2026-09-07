<template>
  <div class="kardex-tab">
    <q-card flat bordered class="filters-card q-mb-md">
      <q-card-section class="row q-col-gutter-md items-end">
        <q-input
          v-model="filtro.buscar"
          outlined
          dense
          clearable
          debounce="400"
          class="col-12 col-md-4"
          label="Buscar producto o documento"
        >
          <template #prepend>
            <q-icon name="search" />
          </template>
        </q-input>

        <q-select
          v-model="filtro.idProducto"
          outlined
          dense
          clearable
          emit-value
          map-options
          class="col-12 col-sm-6 col-md-3"
          label="Producto"
          :options="productos"
          :loading="cargandoCatalogos"
        />

        <q-select
          v-model="filtro.idLote"
          outlined
          dense
          clearable
          emit-value
          map-options
          class="col-12 col-sm-6 col-md-2"
          label="Lote"
          :options="lotes"
          :loading="cargandoCatalogos"
        />

        <q-input
          v-model="filtro.fechaDesde"
          outlined
          dense
          type="date"
          class="col-12 col-sm-6 col-md-2"
          label="Desde"
        />

        <q-input
          v-model="filtro.fechaHasta"
          outlined
          dense
          type="date"
          class="col-12 col-sm-6 col-md-2"
          label="Hasta"
        />

        <div class="col-12 col-md row q-gutter-sm justify-end">
          <q-btn
            unelevated
            no-caps
            color="green-8"
            icon="filter_alt"
            label="Filtrar"
            @click="consultar"
          />
          <q-btn
            flat
            no-caps
            color="green-8"
            icon="restart_alt"
            label="Limpiar"
            @click="limpiar"
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
        </div>
      </q-card-section>
    </q-card>

    <q-card flat bordered class="table-card">
      <q-table
        v-model:pagination="pagination"
        :rows="movimientos"
        :columns="columns"
        :loading="cargando"
        row-key="id"
        no-data-label="No hay movimientos para los filtros seleccionados"
        rows-per-page-label="Registros por página"
        @request="cargar"
      >
        <template #body-cell-salida="propsTabla">
          <q-td :props="propsTabla">
            <span :class="esCantidadNumerica(propsTabla.row.salida) ? 'salida-valor' : ''">
              {{ formatearCantidad(propsTabla.row.salida) }}
            </span>
          </q-td>
        </template>
      </q-table>
    </q-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useQuasar } from 'quasar'

import { descargarKardexPdf, listarKardex } from '../../api/kardex/kardex'
import { listarLotes } from '../../api/lotes/lotes'
import { listarProductos } from '../../api/productos/productos'

const props = defineProps({
  productoId: {
    type: Number,
    default: null
  },
  loteId: {
    type: Number,
    default: null
  },
  consultaKey: {
    type: Number,
    default: 0
  }
})

const $q = useQuasar()
const movimientos = ref([])
const productos = ref([])
const lotes = ref([])
const cargando = ref(false)
const cargandoCatalogos = ref(false)
const descargandoPdf = ref(false)

const filtro = reactive({
  buscar: '',
  idProducto: null,
  idLote: null,
  fechaDesde: '',
  fechaHasta: ''
})

const aplicado = ref({ ...filtro })
const pagination = ref({
  page: 1,
  rowsPerPage: 8,
  rowsNumber: 0,
  sortBy: 'fecha',
  descending: true
})

const columns = [
  { name: 'fecha', label: 'Fecha/Hora', field: 'fecha', align: 'left', format: formatearFecha },
  { name: 'codigoProducto', label: 'Código', field: 'codigoProducto', align: 'left' },
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left' },
  { name: 'documento', label: 'Documento/Referencia', field: 'documento', align: 'left', format: valor => valor || '—' },
  { name: 'operacion', label: 'Operación', field: 'operacion', align: 'left' },
  { name: 'lote', label: 'Lote', field: 'lote', align: 'left', format: valor => valor || '—' },
  { name: 'almacen', label: 'Almacén', field: 'almacen', align: 'left', format: valor => valor || '—' },
  { name: 'ubicacion', label: 'Ubicación', field: 'ubicacion', align: 'left', format: valor => valor || '—' },
  { name: 'entrada', label: 'Entrada', field: 'entrada', align: 'right', format: formatearCantidad },
  { name: 'salida', label: 'Salida', field: 'salida', align: 'right', format: formatearCantidad },
  { name: 'saldo', label: 'Saldo', field: 'saldo', align: 'right', format: formatearCantidad },
  { name: 'unidad', label: 'Unidad', field: 'unidad', align: 'left', format: valor => valor || '—' },
  { name: 'usuario', label: 'Usuario', field: 'usuario', align: 'left' },
  { name: 'observacion', label: 'Observación', field: 'observacion', align: 'left', format: valor => valor || '—' }
]

async function cargar({ pagination: pagina = pagination.value } = {}) {
  cargando.value = true

  try {
    const response = await listarKardex({
      page: pagina.page - 1,
      size: pagina.rowsPerPage,
      buscar: aplicado.value.buscar?.trim() || undefined,
      idProducto: aplicado.value.idProducto || undefined,
      idLote: aplicado.value.idLote || undefined,
      fechaDesde: aplicado.value.fechaDesde || undefined,
      fechaHasta: aplicado.value.fechaHasta || undefined
    })

    console.log('[Kardex] Respuesta completa del backend:', response)
    console.table((response?.content || []).map(movimiento => ({
      id: movimiento.id,
      producto: movimiento.producto,
      operacion: movimiento.operacion,
      entrada: movimiento.entrada,
      salida: movimiento.salida,
      saldo: movimiento.saldo,
      unidad: movimiento.unidad,
      documento: movimiento.documento
    })))
    movimientos.value = response?.content || []
    pagination.value = {
      ...pagina,
      rowsNumber: response?.totalElements || 0
    }
  } catch (error) {
    movimientos.value = []
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'No se pudo cargar el Kardex.'
    })
  } finally {
    cargando.value = false
  }
}

function consultar() {
  aplicado.value = { ...filtro }
  pagination.value.page = 1
  cargar({ pagination: pagination.value })
}

function limpiar() {
  Object.assign(filtro, {
    buscar: '',
    idProducto: null,
    idLote: null,
    fechaDesde: '',
    fechaHasta: ''
  })
  consultar()
}

async function cargarCatalogos() {
  cargandoCatalogos.value = true

  try {
    const [productosResponse, lotesResponse] = await Promise.all([
      listarProductos({ page: 0, size: 100 }),
      listarLotes({ page: 0, size: 100 })
    ])

    productos.value = (productosResponse?.content || []).map(producto => ({
      label: `${producto.codigoInterno || ''} - ${producto.nombre}`.replace(/^ - /, ''),
      value: producto.idProducto
    }))

    lotes.value = (lotesResponse?.content || []).map(lote => ({
      label: `${lote.codigoLote} - ${lote.producto}`,
      value: lote.idLote
    }))
  } catch (error) {
    $q.notify({
      type: 'warning',
      message: 'No se pudieron cargar los productos o lotes para filtrar.'
    })
  } finally {
    cargandoCatalogos.value = false
  }
}

function formatearFecha(valor) {
  if (!valor) return '—'
  return new Intl.DateTimeFormat('es-BO', {
    dateStyle: 'short',
    timeStyle: 'short'
  }).format(new Date(valor))
}

function formatearCantidad(valor) {
  return esCantidadNumerica(valor)
    ? Number(valor).toLocaleString('es-BO')
    : '—'
}

function esCantidadNumerica(valor) {
  return valor !== null
    && valor !== undefined
    && valor !== ''
    && Number.isFinite(Number(valor))
}

async function descargarPdf() {
  descargandoPdf.value = true

  try {
    const archivo = await descargarKardexPdf({
      buscar: aplicado.value.buscar?.trim() || undefined,
      idProducto: aplicado.value.idProducto || undefined,
      idLote: aplicado.value.idLote || undefined,
      fechaDesde: aplicado.value.fechaDesde || undefined,
      fechaHasta: aplicado.value.fechaHasta || undefined
    })

    const url = URL.createObjectURL(archivo)
    const enlace = document.createElement('a')
    enlace.href = url
    enlace.download = `kardex_${new Date().toISOString().slice(0, 10)}.pdf`
    document.body.appendChild(enlace)
    enlace.click()
    enlace.remove()
    URL.revokeObjectURL(url)

    $q.notify({
      type: 'positive',
      message: 'PDF de Kardex descargado correctamente.'
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'No se pudo descargar el PDF de Kardex.'
    })
  } finally {
    descargandoPdf.value = false
  }
}

watch(
  () => filtro.buscar,
  () => consultar()
)

watch(
  () => [props.productoId, props.loteId, props.consultaKey],
  ([productoId, loteId]) => {
    filtro.idProducto = productoId
    filtro.idLote = loteId
    consultar()
  }
)

onMounted(async () => {
  filtro.idProducto = props.productoId
  filtro.idLote = props.loteId
  aplicado.value = { ...filtro }
  await cargarCatalogos()
  await cargar({ pagination: pagination.value })
})

defineExpose({ cargar: consultar })
</script>

<style scoped>
.filters-card,
.table-card {
  border-color: #dfe7dc !important;
  border-radius: 16px;
}

.filters-card {
  background: #fff;
}

:deep(.q-table thead tr) {
  background: #f0f8e9;
}

:deep(.q-table__middle) {
  overflow-x: auto;
}

:deep(.q-table) {
  min-width: 1550px;
}

.salida-valor {
  color: #c62828;
  font-weight: 700;
}

@media (max-width: 700px) {
  .kardex-tab :deep(.q-btn) {
    flex: 1;
  }
}
</style>

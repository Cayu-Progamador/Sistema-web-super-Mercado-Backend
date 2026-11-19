<template>
  <div class="lotes-tab">
    <div class="search-toolbar q-mb-sm"><q-input v-model="filtro.buscar" outlined dense clearable class="search-input"
        label="Producto o código de lote"><template #prepend><q-icon name="search" /></template></q-input><q-btn flat
        round color="green-8" :icon="filtrosAbiertos ? 'filter_alt_off' : 'filter_alt'"
        @click="filtrosAbiertos = !filtrosAbiertos"><q-tooltip>{{ filtrosAbiertos ? 'Ocultar filtros' : 'Mostrar filtros'
          }}</q-tooltip></q-btn></div>
    <q-card v-show="filtrosAbiertos" flat bordered class="filters-card q-mb-md">
      <q-card-section class="row q-col-gutter-md items-center">
        <q-input v-if="false" v-model="filtro.buscar" outlined dense clearable class="col-12 col-md-4"
          label="Producto o código de lote">
          <template #prepend><q-icon name="search" /></template>
        </q-input>
        <q-select v-model="filtro.producto" outlined dense clearable class="col-12 col-sm-6 col-md-2" label="Producto"
          :options="productos" option-label="label" option-value="value" emit-value map-options />
        <div class="col-12 col-sm-6 col-md-4 row q-gutter-sm justify-end">
          <q-btn unelevated no-caps color="green-8" icon="filter_alt" label="Filtrar"
            :disable="rangoVencimientoInvalido" @click="aplicar" />
          <q-btn flat round color="green-8" icon="restart_alt" @click="limpiar">
            <q-tooltip>Limpiar filtros</q-tooltip>
          </q-btn>
          <q-btn outline no-caps color="green-8" icon="picture_as_pdf" label="Descargar PDF"
            :disable="rangoVencimientoInvalido" :loading="descargandoPdf" @click="descargarPdf" />
        </div>
        <div class="col-12 row q-col-gutter-md">
          <q-input v-model="filtro.desde" outlined dense type="date" class="col-12 col-sm-6 col-md-3"
            label="Vencimiento desde" :max="filtro.hasta || undefined"
            :error="rangoVencimientoInvalido"
            error-message="La fecha inicial no puede ser mayor que la final." />
          <q-input v-model="filtro.hasta" outlined dense type="date" class="col-12 col-sm-6 col-md-3"
            label="Vencimiento hasta" :min="filtro.desde || undefined"
            :error="rangoVencimientoInvalido"
            error-message="La fecha final debe ser igual o posterior a la inicial." />
        </div>
      </q-card-section>
    </q-card>

    <div class="row items-center justify-between q-mb-sm">
      <div class="section-title">Lotes registrados <q-badge color="green-8">{{ filtrados.length }}</q-badge></div>
      <div class="text-caption text-grey-6">Los lotes se crean normalmente desde Recepción</div>
    </div>

    <q-card flat bordered class="table-card">
      <q-table :rows="filas" :columns="columns" row-key="idLote" rows-per-page-label="Lotes por página"
        v-model:pagination="pagination" :loading="cargando" @request="cargar" no-data-label="No se encontraron lotes">
        <template #body-cell-estado="p"><q-td :props="p"><q-badge rounded :color="colorEstado(p.value)"
              :label="p.value" /></q-td></template>
        <template #body-cell-acciones="p"><q-td :props="p" class="actions-cell"><q-btn flat round dense
              icon="visibility" color="green-8" @click="verLote(p.row)"><q-tooltip>Ver
                lote</q-tooltip></q-btn><q-btn v-permiso="'INVENTARIO_EDITAR'" flat round dense icon="edit" color="green-8"
              @click="$emit('editar', p.row)"><q-tooltip>Editar lote</q-tooltip></q-btn><q-btn flat round dense
              icon="receipt_long" color="grey-7" @click="$emit('kardex', p.row)"><q-tooltip>Ver
                Kardex</q-tooltip></q-btn></q-td></template>
      </q-table>
    </q-card><VerLoteDialog v-model="verDialog" :lote="loteVisto" @kardex="$emit('kardex', $event)" />
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted, watch } from 'vue'
import { useQuasar } from 'quasar'
import { descargarLotesPdf, listarLotes } from '../../api/lotes/lotes'
import VerLoteDialog from './VerLoteDialog.vue'

const props = defineProps({ rows: { type: Array, default: () => [] } })
const $q = useQuasar()
const filas = ref([...props.rows])
const verDialog = ref(false)
const loteVisto = ref(null)
function verLote (lote) { loteVisto.value = lote; verDialog.value = true }
const cargando = ref(false)
const descargandoPdf = ref(false)
const pagination = ref({ page: 1, rowsPerPage: 8, rowsNumber: 0 })
defineEmits(['ver', 'editar', 'kardex'])
const filtro = reactive({ buscar: '', producto: null, desde: '', hasta: '' })
const filtrosAbiertos = ref(false)
const aplicado = ref({ ...filtro })
const rangoVencimientoInvalido = computed(() => Boolean(
  filtro.desde && filtro.hasta && filtro.desde > filtro.hasta
))
const productos = computed(() => [...new Map(filas.value.filter(r => r.idProducto).map(r => [r.idProducto, { label: r.producto, value: r.idProducto }])).values()])
const filtrados = computed(() => filas.value.filter((r) => {
  const f = aplicado.value
  const texto = `${r.producto || ''} ${r.codigoLote || ''}`.toLowerCase()
  return (!f.buscar || texto.includes(f.buscar.toLowerCase())) && (!f.producto || r.idProducto === f.producto) && (!f.desde || r.fechaVencimiento >= f.desde) && (!f.hasta || r.fechaVencimiento <= f.hasta)
}))
const columns = [{ name: 'codigoLote', label: 'Código lote', field: 'codigoLote', align: 'left' }, { name: 'producto', label: 'Producto', field: 'producto', align: 'left' }, { name: 'fechaIngreso', label: 'Ingreso', field: 'fechaIngreso' }, { name: 'fechaVencimiento', label: 'Vencimiento', field: 'fechaVencimiento' }, { name: 'cantidad', label: 'Cantidad', field: 'cantidad' }, { name: 'estado', label: 'Estado', field: 'estado' }, { name: 'acciones', label: 'Acciones', field: 'acciones' }]
function aplicar() { if (rangoVencimientoInvalido.value) return; aplicado.value = { ...filtro }; pagination.value.page = 1; cargar({ pagination: pagination.value }) }
function limpiar() { Object.assign(filtro, { buscar: '', producto: null, desde: '', hasta: '' }); aplicar() }
async function descargarPdf () { descargandoPdf.value = true; try { const archivo = await descargarLotesPdf({ buscar: aplicado.value.buscar || undefined, idProducto: aplicado.value.producto || undefined, fechaDesde: aplicado.value.desde || undefined, fechaHasta: aplicado.value.hasta || undefined }); guardarPdf(archivo, 'lotes'); $q.notify({ type: 'positive', message: 'PDF de lotes descargado.' }) } catch (error) { $q.notify({ type: 'negative', message: error.response?.data?.message || 'No se pudo descargar el PDF de lotes.' }) } finally { descargandoPdf.value = false } }
function guardarPdf (archivo, nombre) { const url = URL.createObjectURL(archivo); const enlace = document.createElement('a'); enlace.href = url; enlace.download = `${nombre}_${new Date().toISOString().slice(0, 10)}.pdf`; document.body.appendChild(enlace); enlace.click(); enlace.remove(); URL.revokeObjectURL(url) }
watch(() => filtro.buscar, () => { aplicado.value.buscar = filtro.buscar; pagination.value.page = 1; cargar({ pagination: pagination.value }) })
function colorEstado(estado) { return { ACTIVO: 'green-7', POR_VENCER: 'orange-8', VENCIDO: 'red-6', AGOTADO: 'grey-7', BLOQUEADO: 'blue-grey-7' }[estado] || 'grey-7' }
async function cargar ({ pagination: pagina }) { cargando.value = true; try { const response = await listarLotes({ page: pagina.page - 1, size: pagina.rowsPerPage, buscar: aplicado.value.buscar || undefined, idProducto: aplicado.value.producto || undefined, estado: aplicado.value.estado || undefined, fechaDesde: aplicado.value.desde || undefined, fechaHasta: aplicado.value.hasta || undefined }); filas.value = (response?.content || []).map(l => ({ ...l, cantidad: l.cantidad || 0, fechaIngreso: l.fechaIngreso?.slice(0, 10) })); pagination.value = { ...pagina, rowsNumber: response?.totalElements || 0 } } finally { cargando.value = false } }
onMounted(() => cargar({ pagination: pagination.value }))
</script>

<style scoped>
.search-toolbar {
  margin: 0 16px 12px;
  padding: 6px 0;
  display: flex;
  align-items: center;
  gap: 10px;
  max-width: 680px
}

.search-input {
  flex: 1;
  min-width: 0
}

.search-toolbar>.q-btn {
  flex: 0 0 42px
}

.filters-card {
  margin-left: 16px;
  margin-right: 16px
}

.filters-card,
.table-card {
  border-color: #dfe7dc !important;
  border-radius: 16px
}

.filters-card {
  background: #fff
}

.section-title {
  font-size: 16px;
  font-weight: 900;
  color: #2a5c1a
}

.actions-cell {
  white-space: nowrap
}

:deep(.q-table thead tr) {
  background: #f0f8e9
}

:deep(.q-table tbody td) {
  height: 58px
}

@media(max-width:700px) {
  .lotes-tab {
    width: 100%
  }

  .filters-card :deep(.q-btn) {
    flex: 1
  }

  .section-title {
    font-size: 14px
  }
}

.search-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 4px 0
}

.search-input {
  flex: 1;
  min-width: 0
}

.search-toolbar :deep(.q-field__control) {
  border-radius: 12px;
  background: #fff
}

.search-toolbar :deep(.q-field--focused .q-field__control) {
  border-color: #4a8c25
}

.search-toolbar>.q-btn {
  width: 42px;
  height: 42px;
  border: 1px solid #c8dfc1;
  border-radius: 12px;
  background: #f0f8e9
}

@media(max-width:600px) {
  .search-toolbar {
    gap: 6px
  }

  .search-toolbar>.q-btn {
    width: 40px;
    height: 40px
  }

  .search-input :deep(input) {
    font-size: 13px
  }
}
</style>
<style scoped>
.search-toolbar {
  max-width: 480px !important
}

.search-input {
  max-width: 420px
}
</style>

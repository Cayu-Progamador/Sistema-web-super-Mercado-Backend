<template>
  <div class="existencias-tab">
    <div class="search-toolbar q-mb-sm"><q-input v-model="filtro.buscar" outlined dense clearable class="search-input" label="Buscar por nombre, código o código de barras"><template #prepend><q-icon name="search" /></template></q-input><q-btn flat round color="green-8" :icon="filtrosAbiertos ? 'filter_alt_off' : 'filter_alt'" @click="filtrosAbiertos=!filtrosAbiertos"><q-tooltip>{{ filtrosAbiertos ? 'Ocultar filtros' : 'Mostrar filtros' }}</q-tooltip></q-btn></div>
    <q-card v-show="filtrosAbiertos" flat bordered class="filters-card q-mb-md">
      <q-card-section class="row q-col-gutter-md items-center">
        <q-input v-if="false" v-model="filtro.buscar" outlined dense clearable class="col-12 col-md-4"
          label="Buscar por nombre, código o código de barras">
          <template #prepend><q-icon name="search" /></template>
        </q-input>
        <q-select v-model="filtro.categoria" outlined dense clearable class="col-12 col-sm-6 col-md-2"
          label="Categoría" :options="categorias" />
        <q-select v-model="filtro.almacen" outlined dense clearable class="col-12 col-sm-6 col-md-2"
          label="Almacén" :options="almacenes" />
        <q-select v-model="filtro.ubicacion" outlined dense clearable class="col-12 col-sm-6 col-md-2"
          label="Ubicación" :options="ubicaciones" />
        <q-select v-model="filtro.estado" outlined dense clearable class="col-12 col-sm-6 col-md-2"
          label="Estado de stock" :options="estados" />
        <q-input v-model="filtro.lote" outlined dense clearable class="col-12 col-sm-6 col-md-3"
          label="Lote" />
        <q-input v-model="filtro.vencimiento" outlined dense type="date" class="col-12 col-sm-6 col-md-3"
          label="Vence hasta" />
        <div class="col-12 col-md row q-gutter-sm justify-end">
          <q-btn unelevated no-caps color="green-8" icon="search" label="Buscar" @click="aplicar" />
          <q-btn flat no-caps color="green-8" icon="restart_alt" label="Limpiar" @click="limpiar" />
          <q-btn outline no-caps color="green-8" icon="picture_as_pdf" label="Descargar PDF" :loading="descargandoPdf" @click="descargarPdf" />
        </div>
      </q-card-section>
    </q-card>

    <q-table :rows="filas" :columns="columns" row-key="idExistencia" v-model:pagination="pagination" :loading="cargando" @request="cargar"
      rows-per-page-label="Registros por página" no-data-label="No se encontraron existencias">
      <template #body-cell-estado="p"><q-td :props="p"><q-badge rounded :color="colorEstado(p.value)" :label="p.value" /></q-td></template>
      <template #body-cell-acciones="p"><q-td :props="p" class="actions-cell"><q-btn flat round dense icon="visibility" color="green-8" @click="$emit('detalle', p.row)"><q-tooltip>Ver detalle</q-tooltip></q-btn><q-btn flat round dense icon="receipt_long" color="grey-7" @click="$emit('kardex', p.row)"><q-tooltip>Ver Kardex</q-tooltip></q-btn></q-td></template>
    </q-table>
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { useQuasar } from 'quasar'
import { descargarExistenciasPdf, listarExistencias, listarEstadosExistencia } from '../../api/inventario/inventario'
import { listarAlmacenes } from '../../api/almacen/almacen'

const props = defineProps({ rows: { type: Array, default: () => [] } })
const $q = useQuasar()
const filas = ref([...props.rows])
defineEmits(['detalle', 'kardex'])
const filtro = reactive({ buscar: '', categoria: null, almacen: null, ubicacion: null, estado: null, lote: '', vencimiento: '' })
const filtrosAbiertos = ref(false)
const aplicado = ref({ ...filtro })
const cargando = ref(false)
const descargandoPdf = ref(false)
const pagination = ref({ page: 1, rowsPerPage: 8, rowsNumber: 0 })
const estados = ['DISPONIBLE', 'STOCK BAJO', 'STOCK CRÍTICO', 'SIN STOCK', 'SOBRESTOCK']
const categorias = computed(() => [...new Set(props.rows.map((r) => r.categoria).filter(Boolean))])
const almacenes = ref([])
const ubicaciones = computed(() => [...new Set(filas.value.filter(r => !filtro.almacen || r.almacen === filtro.almacen).map((r) => r.ubicacion).filter(Boolean))])
const filtradas = computed(() => props.rows.filter((r) => { const f = aplicado.value; const texto = `${r.nombre || ''} ${r.codigo || ''}`.toLowerCase(); return (!f.buscar || texto.includes(f.buscar.toLowerCase())) && (!f.categoria || r.categoria === f.categoria) && (!f.almacen || r.almacen === f.almacen) && (!f.ubicacion || r.ubicacion === f.ubicacion) && (!f.estado || r.estado === f.estado) && (!f.lote || r.lote?.toLowerCase().includes(f.lote.toLowerCase())) && (!f.vencimiento || r.vencimiento <= f.vencimiento) }))
const columns = [{ name: 'nombre', label: 'Producto', field: 'nombre', align: 'left' }, { name: 'almacen', label: 'Almacén', field: 'almacen', align: 'left' }, { name: 'ubicacion', label: 'Ubicación', field: 'ubicacion' }, { name: 'lote', label: 'Lote', field: 'lote' }, { name: 'vencimiento', label: 'Vencimiento', field: 'vencimiento' }, { name: 'stockActual', label: 'Stock actual', field: 'stockActual' }, { name: 'stockMinimo', label: 'Mínimo', field: 'stockMinimo' }, { name: 'stockMaximo', label: 'Máximo', field: 'stockMaximo' }, { name: 'unidadBase', label: 'Unidad', field: 'unidadBase' }, { name: 'estado', label: 'Estado', field: 'estado' }, { name: 'acciones', label: 'Acciones', field: 'acciones' }]
function aplicar () { aplicado.value = { ...filtro }; pagination.value.page = 1; cargar({ pagination: pagination.value }) }
function limpiar () { Object.assign(filtro, { buscar: '', categoria: null, almacen: null, ubicacion: null, estado: null, lote: '', vencimiento: '' }); aplicar() }
async function descargarPdf () { descargandoPdf.value = true; try { const archivo = await descargarExistenciasPdf({ buscar: aplicado.value.buscar || undefined, idAlmacen: aplicado.value.almacen || undefined, idUbicacion: aplicado.value.ubicacion || undefined, categoria: aplicado.value.categoria || undefined, estado: aplicado.value.estado || undefined, lote: aplicado.value.lote || undefined, vencimientoHasta: aplicado.value.vencimiento || undefined }); guardarPdf(archivo, 'existencias'); $q.notify({ type: 'positive', message: 'PDF de existencias descargado.' }) } catch (error) { $q.notify({ type: 'negative', message: error.response?.data?.message || 'No se pudo descargar el PDF de existencias.' }) } finally { descargandoPdf.value = false } }
function guardarPdf (archivo, nombre) { const url = URL.createObjectURL(archivo); const enlace = document.createElement('a'); enlace.href = url; enlace.download = `${nombre}_${new Date().toISOString().slice(0, 10)}.pdf`; document.body.appendChild(enlace); enlace.click(); enlace.remove(); URL.revokeObjectURL(url) }
async function cargar ({ pagination: pagina }) { cargando.value = true; try { const params = { page: pagina.page - 1, size: pagina.rowsPerPage, buscar: aplicado.value.buscar || undefined, idAlmacen: aplicado.value.almacen || undefined, idUbicacion: aplicado.value.ubicacion || undefined, categoria: aplicado.value.categoria || undefined, estado: aplicado.value.estado || undefined, lote: aplicado.value.lote || undefined, vencimientoHasta: aplicado.value.vencimiento || undefined }; const response = await listarExistencias(params); filas.value = (response?.content || []).map(e => ({ ...e, nombre: e.nombreProducto, codigo: e.codigoProducto, almacen: e.almacen, ubicacion: e.nombreUbicacion || e.codigoUbicacion, lote: e.codigoLote, vencimiento: e.fechaVencimiento, stockActual: e.cantidad })); pagination.value = { ...pagina, rowsNumber: response?.totalElements || 0 } } finally { cargando.value = false } }
function colorEstado (v) { return { DISPONIBLE: 'green-7', 'STOCK BAJO': 'amber-8', 'STOCK CRÍTICO': 'orange-8', 'SIN STOCK': 'red-6', SOBRESTOCK: 'blue-7' }[v] || 'grey-7' }
function actualizarDesdeBoton (event) { if (event.target?.closest('button')?.innerText?.trim() === 'Actualizar') cargar({ pagination: { ...pagination.value, page: 1 } }) }
onMounted(async () => { document.addEventListener('click', actualizarDesdeBoton); try { const response = await listarAlmacenes({ page: 0, size: 100 }); almacenes.value = (response?.content || response || []).map(a => a.nombre) } catch (error) { console.error('No se pudieron cargar los filtros', error) } await cargar({ pagination: pagination.value }) })
onBeforeUnmount(() => document.removeEventListener('click', actualizarDesdeBoton))
watch(() => filtro.buscar, async () => { aplicado.value.buscar = filtro.buscar; pagination.value.page = 1; await cargar({ pagination: pagination.value }) })
watch(() => filtro.almacen, () => { if (filtro.ubicacion && !ubicaciones.value.includes(filtro.ubicacion)) filtro.ubicacion = null })
</script>

<style scoped>
.search-toolbar{margin:0 16px 12px;padding:6px 0;display:flex;align-items:center;gap:10px;max-width:680px}.search-input{flex:1;min-width:0}.search-toolbar>.q-btn{flex:0 0 42px}.filters-card{margin-left:16px;margin-right:16px}
.filters-card{border-color:#dfe7dc!important;border-radius:16px;background:#fff}.actions-cell{white-space:nowrap}:deep(.q-table thead tr){background:#f0f8e9}:deep(.q-table tbody td){height:58px}@media(max-width:700px){.existencias-tab{width:100%}.filters-card :deep(.q-btn){flex:1}.filters-card :deep(.q-card__section){padding:14px}.existencias-tab :deep(.q-table__middle){overflow-x:auto}.existencias-tab :deep(.q-table){min-width:1100px}}
.search-toolbar{display:flex;align-items:center;gap:10px;width:100%;padding:4px 0}.search-input{flex:1;min-width:0}.search-toolbar :deep(.q-field__control){border-radius:12px;background:#fff}.search-toolbar :deep(.q-field--focused .q-field__control){border-color:#4a8c25}.search-toolbar>.q-btn{width:42px;height:42px;border:1px solid #c8dfc1;border-radius:12px;background:#f0f8e9}@media(max-width:600px){.search-toolbar{gap:6px}.search-toolbar>.q-btn{width:40px;height:40px}.search-input :deep(input){font-size:13px}}
</style>
<style scoped>.search-toolbar{max-width:480px!important}.search-input{max-width:420px}</style>

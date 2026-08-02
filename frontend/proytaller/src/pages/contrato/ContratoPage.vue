<template>
  <q-page class="contrato-page">
    <div class="page-inner">
     

      <!-- Header -->
      <div class="row items-center justify-between q-mb-lg">
        <div class="col">
          <div class="text-h4 text-bold header-title">Gestión de Contratos</div>
          <div class="text-subtitle2 header-subtitle">Administración de contratos laborales de los empleados</div>
        </div>
      </div>

      <!-- KPI Cards -->
      <KpiCards ref="kpiCardsRef" />

      <!-- Toolbar -->
      <div class="row items-center justify-between q-my-md toolbar-row">
        <div class="row items-center q-gutter-sm">
          <q-btn unelevated color="green-8" icon="add" label="Nuevo Contrato" @click="abrirNuevo" no-caps />
          <q-btn outline color="green-8" icon="picture_as_pdf" label="PDF" @click="exportarPDF" no-caps />
          <q-btn outline color="green-8" icon="table_view" label="Excel" @click="exportarExcel" no-caps />
          <q-btn flat color="green-8" icon="refresh" @click="recargar" />
          
        </div>
        <div class="row items-center q-gutter-sm">
          <q-input v-model="searchText" outlined dense placeholder="Buscar por Nombre o Ci..." class="search-input" debounce="300" @update:model-value="onSearch">
            <template v-slot:prepend>
              <q-icon name="search" color="green-8" />
            </template>
          </q-input>
          <q-btn flat :icon="filtrosVisibles ? 'expand_less' : 'filter_list'" label="Filtros" color="green-8" @click="filtrosVisibles = !filtrosVisibles">
            <q-badge v-if="filtrosActivos" color="primary" floating>!</q-badge>
          </q-btn>
        </div>
      </div>

      <!-- Collapsible Filters -->
      <q-slide-transition>
        <div v-show="filtrosVisibles">
          <q-card flat bordered class="filters-card q-mb-md q-pa-md">
            <div class="row q-col-gutter-md">
              <div class="col-12 col-md-3">
                <q-select v-model="filtros.estado" :options="estadoOptions" label="Estado" outlined dense clearable map-options emit-value class="filter-field" />
              </div>
              <div class="col-12 col-md-3">
                <q-select v-model="filtros.tipoContrato" :options="tipoContratoOptions" label="Tipo Contrato" outlined dense clearable class="filter-field" />
              </div>
              <div class="col-12 col-md-3">
                <q-select v-model="filtros.tipoJornada" :options="tipoJornadaOptions" label="Tipo Jornada" outlined dense clearable class="filter-field" />
              </div>
              <div class="col-12 col-md-3">
                <q-select v-model="filtros.controlaAsistencia" :options="asistenciaOptions" label="Control Asistencia" outlined dense clearable emit-value map-options class="filter-field" />
              </div>
              <div class="col-12 col-md-3">
                <q-input v-model="filtros.fechaDesde" label="Fecha Inicio Desde" outlined dense type="date" class="filter-field" />
              </div>
              <div class="col-12 col-md-3">
                <q-input v-model="filtros.fechaHasta" label="Fecha Inicio Hasta" outlined dense type="date" class="filter-field" />
              </div>
              <div class="col-12 col-md-3">
                <q-input v-model="filtros.fechaFinDesde" label="Fecha Fin Desde" outlined dense type="date" class="filter-field" />
              </div>
              <div class="col-12 col-md-3">
                <q-input v-model="filtros.fechaFinHasta" label="Fecha Fin Hasta" outlined dense type="date" class="filter-field" />
              </div>
              <div class="col-12 row justify-end q-gutter-sm q-mt-sm">
                <q-btn flat color="green-8" icon="clear" label="Limpiar" @click="limpiarFiltros" no-caps />
                <q-btn unelevated color="green-8" icon="search" label="Buscar" @click="aplicarFiltros" no-caps />
              </div>
            </div>
          </q-card>
        </div>
      </q-slide-transition>

      <!-- Table -->
      <ContractsTable
        ref="tableRef"
        @ver-detalle="abrirDetalle"
        @editar="abrirEditar"
        @renovar="abrirRenovar"
        @finalizar="finalizarContrato"
        @activar="activarContrato"
        @suspender="suspenderContrato"
        @pdf="exportarPDFIndividual"
      />
    </div>

    <!-- Dialog Detalle -->
    <ContractDetailDialog v-model="dialogAbierto" :contrato-id="contratoSeleccionadoId" />

    <!-- Dialog Nuevo/Editar -->
    <NewContractDialog v-model="dialogNuevo" :contrato-id="editarContratoId" :es-editar="!!editarContratoId" @saved="onSaved" />

    <!-- Dialog Renovar -->
    <RenewContractDialog v-model="dialogRenovar" :contrato-id="renovarContratoId" @saved="onSaved" />

    <!-- Dialog Finalizar -->
    <ConfirmarFinalizarDialog v-model="dialogFinalizar" :contrato-id="finalizarContratoId" :empleado-nombre="finalizarEmpleadoNombre" @finalizado="onFinalizado" />

    <!-- Dialog Activar / Suspender -->
    <ConfirmarEstadoContratoDialog v-model="dialogEstado" :contrato-id="estadoContratoId" :empleado-nombre="estadoEmpleadoNombre" :tipo="estadoTipo" @finalizado="onFinalizado" />
  </q-page>
</template>

<script setup>
import { ref, reactive, computed, provide } from 'vue'
import { useQuasar } from 'quasar'
import KpiCards from '../../components/contrato/KpiCards.vue'
import ContractsTable from '../../components/contrato/ContractsTable.vue'
import ContractDetailDialog from '../../components/contrato/ContractDetailDialog.vue'
import NewContractDialog from '../../components/contrato/NewContractDialog.vue'
import RenewContractDialog from '../../components/contrato/RenewContractDialog.vue'
import ConfirmarFinalizarDialog from '../../components/contrato/ConfirmarFinalizarDialog.vue'
import ConfirmarEstadoContratoDialog from '../../components/contrato/ConfirmarEstadoContratoDialog.vue'
import { exportarContratosPDF, exportarContratosExcel } from '../../api/contrato/contrato'

const $q = useQuasar()
const tableRef = ref(null)
const kpiCardsRef = ref(null)

const searchText = ref('')
const filtrosVisibles = ref(false)
const filtrosActivos = ref(false)

const filtros = reactive({
  estado: null,
  tipoContrato: null,
  tipoJornada: null,
  controlaAsistencia: null,
  fechaDesde: null,
  fechaHasta: null,
  fechaFinDesde: null,
  fechaFinHasta: null
})

const estadoOptions = [
  { label: 'Activo', value: 'ACTIVO' },
  { label: 'Vencido', value: 'VENCIDO' },
  { label: 'Finalizado', value: 'FINALIZADO' },
  { label: 'Suspendido', value: 'SUSPENDIDO' }
]

const tipoContratoOptions = ['Indefinido', 'Plazo Fijo', 'Temporal']
const tipoJornadaOptions = ['Tiempo Completo', 'Medio Tiempo']
const asistenciaOptions = [
  { label: 'Controla', value: true },
  { label: 'No Controla', value: false }
]

const dialogAbierto = ref(false)
const contratoSeleccionadoId = ref(null)
const dialogNuevo = ref(false)
const editarContratoId = ref(null)
const dialogRenovar = ref(false)
const renovarContratoId = ref(null)
const dialogFinalizar = ref(false)
const finalizarContratoId = ref(null)
const finalizarEmpleadoNombre = ref('')
const dialogEstado = ref(false)
const estadoContratoId = ref(null)
const estadoEmpleadoNombre = ref('')
const estadoTipo = ref('activar')

function abrirNuevo() {
  editarContratoId.value = null
  dialogNuevo.value = true
}

function abrirEditar(id) {
  editarContratoId.value = id
  dialogNuevo.value = true
}

function abrirDetalle(id) {
  contratoSeleccionadoId.value = id
  dialogAbierto.value = true
}

function abrirRenovar(id) {
  renovarContratoId.value = id
  dialogRenovar.value = true
}

function onSaved() {
  tableRef.value?.recargar()
  kpiCardsRef.value?.cargarDatos()
}

function onSearch(val) {
  tableRef.value?.buscar(val)
}

function aplicarFiltros() {
  filtrosActivos.value = Object.values(filtros).some(v => v !== null && v !== undefined && v !== '')
  tableRef.value?.filtrar(filtros)
}

function limpiarFiltros() {
  filtros.estado = null
  filtros.tipoContrato = null
  filtros.tipoJornada = null
  filtros.controlaAsistencia = null
  filtros.fechaDesde = null
  filtros.fechaHasta = null
  filtros.fechaFinDesde = null
  filtros.fechaFinHasta = null
  filtrosActivos.value = false
  tableRef.value?.filtrar(null)
}

function recargar() {
  tableRef.value?.recargar()
}

async function finalizarContrato(id) {
  try {
    const { getContrato } = await import('../../api/contrato/contrato')
    const data = await getContrato(id)
    const emp = data.empleado
    finalizarEmpleadoNombre.value = emp ? `${emp.nombres || ''} ${emp.apellidos || ''}`.trim() : 'Empleado'
    finalizarContratoId.value = id
    dialogFinalizar.value = true
  } catch {
    $q.notify({ type: 'negative', message: 'Error al obtener datos del contrato' })
  }
}

async function activarContrato(id) {
  try {
    const { getContrato } = await import('../../api/contrato/contrato')
    const data = await getContrato(id)
    const emp = data.empleado
    estadoEmpleadoNombre.value = emp ? `${emp.nombres || ''} ${emp.apellidos || ''}`.trim() : 'Empleado'
    estadoContratoId.value = id
    estadoTipo.value = 'activar'
    dialogEstado.value = true
  } catch {
    $q.notify({ type: 'negative', message: 'Error al obtener datos del contrato' })
  }
}

async function suspenderContrato(id) {
  try {
    const { getContrato } = await import('../../api/contrato/contrato')
    const data = await getContrato(id)
    const emp = data.empleado
    estadoEmpleadoNombre.value = emp ? `${emp.nombres || ''} ${emp.apellidos || ''}`.trim() : 'Empleado'
    estadoContratoId.value = id
    estadoTipo.value = 'suspender'
    dialogEstado.value = true
  } catch {
    $q.notify({ type: 'negative', message: 'Error al obtener datos del contrato' })
  }
}

function onFinalizado() {
  tableRef.value?.recargar()
  kpiCardsRef.value?.cargarDatos()
}

async function exportarPDFIndividual(row) {
  try {
    const { descargarPdfContrato } = await import('../../api/contrato/contrato')
    const blob = await descargarPdfContrato(row.id)
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `Contrato_${row.id}.pdf`
    link.click()
    URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: 'PDF descargado correctamente' })
  } catch {
    $q.notify({ type: 'negative', message: 'Error al descargar el PDF' })
  }
}

function exportarPDF() {
  const params = {}
  if (searchText.value) params.busqueda = searchText.value
  if (filtros.estado) params.estado = filtros.estado
  if (filtros.tipoContrato) params.tipoContrato = filtros.tipoContrato
  if (filtros.tipoJornada) params.tipoJornada = filtros.tipoJornada
  if (filtros.controlaAsistencia != null) params.controlaAsistencia = filtros.controlaAsistencia
  if (filtros.fechaDesde) params.fechaDesde = filtros.fechaDesde
  if (filtros.fechaHasta) params.fechaHasta = filtros.fechaHasta
  if (filtros.fechaFinDesde) params.fechaFinDesde = filtros.fechaFinDesde
  if (filtros.fechaFinHasta) params.fechaFinHasta = filtros.fechaFinHasta
  exportarContratosPDF(params)
    .then(blob => {
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = 'reporte_contratos.pdf'
      link.click()
      URL.revokeObjectURL(url)
      $q.notify({ type: 'positive', message: 'PDF exportado correctamente' })
    })
    .catch(() => $q.notify({ type: 'negative', message: 'Error al exportar PDF' }))
}

function exportarExcel() {
  const params = {}
  if (searchText.value) params.busqueda = searchText.value
  if (filtros.estado) params.estado = filtros.estado
  if (filtros.tipoContrato) params.tipoContrato = filtros.tipoContrato
  if (filtros.tipoJornada) params.tipoJornada = filtros.tipoJornada
  if (filtros.controlaAsistencia != null) params.controlaAsistencia = filtros.controlaAsistencia
  if (filtros.fechaDesde) params.fechaDesde = filtros.fechaDesde
  if (filtros.fechaHasta) params.fechaHasta = filtros.fechaHasta
  if (filtros.fechaFinDesde) params.fechaFinDesde = filtros.fechaFinDesde
  if (filtros.fechaFinHasta) params.fechaFinHasta = filtros.fechaFinHasta
  exportarContratosExcel(params)
    .then(blob => {
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = 'reporte_contratos.xlsx'
      link.click()
      URL.revokeObjectURL(url)
      $q.notify({ type: 'positive', message: 'Excel exportado correctamente' })
    })
    .catch(() => $q.notify({ type: 'negative', message: 'Error al exportar Excel' }))
}
</script>

<style scoped>
.contrato-page {
  background: #F5F7FA;
  min-height: 100vh;
}
.page-inner {
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px 32px;
}
.breadcrumb {
  font-size: 13px;
}
.header-title {
  color: #1a1a1a;
  font-weight: 700;
}
.header-subtitle {
  color: #6b7280;
  font-weight: 400;
  margin-top: 2px;
}
.toolbar-row {
  gap: 8px;
}
.toolbar-row :deep(.q-btn) {
  border-radius: 8px;
  font-weight: 600;
  height: 40px;
}
.search-input {
  width: 280px;
}
.search-input :deep(.q-field__control) {
  border-radius: 10px;
  background: white;
  border: 1.5px solid #2E7D32 !important;
  box-shadow: none !important;
}
.search-input :deep(.q-field__control:focus-within) {
  border-color: #2E7D32 !important;
  box-shadow: 0 0 0 3px rgba(46,125,50,0.15) !important;
}
.search-input :deep(.q-field__before),
.search-input :deep(.q-field__after) {
  border: none !important;
}
.search-input :deep(.q-field__control::before),
.search-input :deep(.q-field__control::after) {
  display: none !important;
}
.search-input :deep(input),
.search-input :deep(.q-field__label),
.search-input :deep(.q-icon) {
  color: #1B5E20 !important;
}
.filters-card {
  border-radius: 12px;
  background: white;
  border: 1px solid #e5e7eb;
}
.filter-field :deep(.q-field__control) {
  border-radius: 8px;
  border: 1.5px solid #2E7D32 !important;
  box-shadow: none !important;
}
.filter-field :deep(.q-field__control:focus-within) {
  border-color: #2E7D32 !important;
  box-shadow: 0 0 0 3px rgba(46,125,50,0.15) !important;
}
.filter-field :deep(.q-field__before),
.filter-field :deep(.q-field__after) {
  border: none !important;
}
.filter-field :deep(.q-field__control::before),
.filter-field :deep(.q-field__control::after) {
  display: none !important;
}
.filter-field :deep(input),
.filter-field :deep(.q-field__label),
.filter-field :deep(.q-icon) {
  color: #1B5E20 !important;
}
</style>

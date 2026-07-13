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
      <KpiCards />

      <!-- Toolbar -->
      <div class="row items-center justify-between q-my-md toolbar-row">
        <div class="row items-center q-gutter-sm">
          <q-btn unelevated color="primary" icon="add" label="Nuevo Contrato" class="btn-primary-custom" @click="abrirNuevo" no-caps />
          <q-btn outline color="primary" icon="picture_as_pdf" label="PDF" class="btn-outline-custom" @click="exportarPDF" no-caps />
          <q-btn outline color="primary" icon="table_view" label="Excel" class="btn-outline-custom" @click="exportarExcel" no-caps />
          <q-btn flat color="grey-7" icon="refresh" @click="recargar" />
        </div>
        <div class="row items-center q-gutter-sm">
          <q-input v-model="searchText" outlined dense placeholder="Buscar contrato..." class="search-input" debounce="300" @update:model-value="onSearch">
            <template v-slot:prepend>
              <q-icon name="search" color="grey-5" />
            </template>
          </q-input>
          <q-btn flat :icon="filtrosVisibles ? 'expand_less' : 'filter_list'" label="Filtros" color="grey-7" @click="filtrosVisibles = !filtrosVisibles">
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
                <q-select v-model="filtros.controlaAsistencia" :options="asistenciaOptions" label="Control Asistencia" outlined dense clearable class="filter-field" />
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
                <q-btn flat color="grey-7" icon="clear" label="Limpiar" @click="limpiarFiltros" no-caps />
                <q-btn unelevated color="primary" icon="search" label="Buscar" @click="aplicarFiltros" no-caps />
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
        @historial="abrirHistorial"
        @finalizar="finalizarContrato"
      />
    </div>

    <!-- Drawer Detalle -->
    <ContractDetailDrawer v-model="drawerAbierto" :contrato-id="contratoSeleccionadoId" />

    <!-- Dialog Nuevo/Editar -->
    <NewContractDialog v-model="dialogNuevo" :contrato-id="editarContratoId" :es-editar="!!editarContratoId" @saved="onSaved" />

    <!-- Dialog Renovar -->
    <RenewContractDialog v-model="dialogRenovar" :contrato-id="renovarContratoId" @saved="onSaved" />

    <!-- Dialog Historial -->
    <ContractHistoryDialog v-model="dialogHistorial" :contrato-id="historialContratoId" />
  </q-page>
</template>

<script setup>
import { ref, reactive, computed, provide } from 'vue'
import { useQuasar } from 'quasar'
import KpiCards from '../../components/contrato/KpiCards.vue'
import ContractsTable from '../../components/contrato/ContractsTable.vue'
import ContractDetailDrawer from '../../components/contrato/ContractDetailDrawer.vue'
import NewContractDialog from '../../components/contrato/NewContractDialog.vue'
import RenewContractDialog from '../../components/contrato/RenewContractDialog.vue'
import ContractHistoryDialog from '../../components/contrato/ContractHistoryDialog.vue'

const $q = useQuasar()
const tableRef = ref(null)

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

const drawerAbierto = ref(false)
const contratoSeleccionadoId = ref(null)
const dialogNuevo = ref(false)
const editarContratoId = ref(null)
const dialogRenovar = ref(false)
const renovarContratoId = ref(null)
const dialogHistorial = ref(false)
const historialContratoId = ref(null)

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
  drawerAbierto.value = true
}

function abrirRenovar(id) {
  renovarContratoId.value = id
  dialogRenovar.value = true
}

function abrirHistorial(id) {
  historialContratoId.value = id
  dialogHistorial.value = true
}

function onSaved() {
  tableRef.value?.recargar()
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
    const { finalizarContrato } = await import('../../api/contrato/contrato')
    await finalizarContrato(id, 'FINALIZADO')
    $q.notify({ type: 'positive', message: 'Contrato finalizado correctamente' })
    tableRef.value?.recargar()
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.message || 'Error al finalizar contrato' })
  }
}

function exportarPDF() {
  $q.notify({ type: 'info', message: 'Exportando PDF...' })
}

function exportarExcel() {
  $q.notify({ type: 'info', message: 'Exportando Excel...' })
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
.btn-primary-custom {
  border-radius: 8px;
  font-weight: 600;
  padding: 0 20px;
  height: 40px;
  box-shadow: 0 2px 8px rgba(46, 125, 50, 0.2);
}
.btn-outline-custom {
  border-radius: 8px;
  font-weight: 500;
  height: 40px;
}
.search-input {
  width: 280px;
}
.search-input :deep(.q-field__control) {
  border-radius: 10px;
  background: white;
}
.filters-card {
  border-radius: 12px;
  background: white;
  border: 1px solid #e5e7eb;
}
.filter-field :deep(.q-field__control) {
  border-radius: 8px;
  background: #f9fafb;
}
</style>

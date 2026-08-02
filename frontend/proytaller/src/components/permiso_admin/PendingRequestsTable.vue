<template>
  <div>
    <div class="toolbar">
      <div class="toolbar-left">
        <q-btn
          unelevated
          no-caps
          color="green-8"
          icon="picture_as_pdf"
          label="Exportar PDF"
          size="12px"
          class="toolbar-btn"
          @click="$emit('exportar-pdf')"
        />
        <q-btn
          flat
          no-caps
          icon="refresh"
          label="Actualizar"
          size="12px"
          color="green-8"
          class="toolbar-icon-btn"
          @click="$emit('actualizar')"
        />
      </div>
      <div class="toolbar-right">
        <q-input
          v-model="busqueda"
          outlined
          dense
          clearable
          debounce="300"
          placeholder="Buscar empleado o código..."
          class="search-input"
        >
          <template v-slot:prepend>
            <q-icon name="search" color="green-5" size="16px" />
          </template>
        </q-input>
        <q-btn
          flat
          no-caps
          icon="filter_list"
          color="green-8"
          size="12px"
          label="Filtros"
          class="toolbar-icon-btn"
          @click="mostrarFiltros = !mostrarFiltros"
        />
      </div>
    </div>

    <q-slide-transition>
      <div v-show="mostrarFiltros" class="filters-panel">
        <q-select
          v-model="filtroTipo"
          :options="tipoOptions"
          outlined
          dense
          label="Tipo de permiso"
          class="filter-item"
          emit-value
          map-options
        />
        <q-input
          v-model="filtroFechaInicio"
          outlined
          dense
          label="Desde"
          type="date"
          class="filter-item"
        />
        <q-input
          v-model="filtroFechaFin"
          outlined
          dense
          label="Hasta"
          type="date"
          class="filter-item"
        />
        <div class="filter-actions">
          <q-btn unelevated no-caps color="green-8" label="Buscar" size="12px" class="filter-btn" @click="aplicarFiltros" />
          <q-btn flat no-caps color="grey-7" label="Limpiar" size="12px" @click="limpiarFiltros" />
        </div>
      </div>
    </q-slide-transition>

    <q-table
      :rows="filteredRows"
      :columns="columns"
      row-key="id"
      flat
      dense
      :pagination="{ rowsPerPage: 10 }"
      class="pending-table"
      hide-bottom
    >
      <template v-slot:body-cell-empleado="props">
        <td class="cell-empleado">
          <q-avatar size="28px" class="cell-avatar" color="green-2" text-color="green-8">
            <q-icon name="person" size="16px" />
          </q-avatar>
          <div class="cell-empleado-info">
            <div class="cell-nombre">{{ props.row.nombreEmpleado }}</div>
            <div class="cell-codigo">#{{ props.row.id }}</div>
          </div>
        </td>
      </template>

      <template v-slot:body-cell-tipo="props">
        <td>
          <q-chip
            :label="props.row.nombreTipo"
            :color="getTipoColor(props.row.idTipo)"
            text-color="green-10"
            class="table-chip"
          />
        </td>
      </template>

      <template v-slot:body-cell-fechas="props">
        <td class="cell-fechas">
          {{ props.row.fechaInicio }}
          <template v-if="props.row.fechaFin"> — {{ props.row.fechaFin }}</template>
        </td>
      </template>

      <template v-slot:body-cell-dias="props">
        <td class="cell-dias">{{ calcularDias(props.row) }}</td>
      </template>

      <template v-slot:body-cell-estado="props">
        <td>
          <q-chip
            :label="props.row.nombreEstado"
            :color="getEstadoColor(props.row.nombreEstado)"
            text-color="green-10"
            class="table-chip"
          />
        </td>
      </template>

      <template v-slot:body-cell-acciones="props">
        <td class="cell-acciones">
          <q-btn
            flat
            round
            dense
            icon="visibility"
            color="green-9"
            class="action-btn action-view"
            @click="$emit('ver-detalle', props.row)"
          >
            <q-tooltip class="bg-green-9">Ver detalle</q-tooltip>
          </q-btn>
          <q-btn
            flat
            round
            dense
            icon="check_circle"
            color="green-7"
            class="action-btn action-approve"
            @click="$emit('aprobar', props.row)"
          >
            <q-tooltip class="bg-green-7">Aprobar</q-tooltip>
          </q-btn>
          <q-btn
            flat
            round
            dense
            icon="cancel"
            color="orange-8"
            class="action-btn action-reject"
            @click="$emit('rechazar', props.row)"
          >
            <q-tooltip class="bg-orange-8">Rechazar</q-tooltip>
          </q-btn>
        </td>
      </template>

      <template v-slot:no-data>
        <div class="empty-state">
          <q-icon name="check_circle_outline" size="40px" color="green-4" />
          <div class="empty-text">No se encontraron solicitudes</div>
          <div class="empty-hint">Prueba con otros filtros</div>
        </div>
      </template>
    </q-table>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  solicitudes: { type: Array, default: () => [] },
  tiposPermiso: { type: Array, default: () => [] },
})

defineEmits(['aprobar', 'rechazar', 'ver-detalle', 'exportar-pdf', 'actualizar'])

const busqueda = ref('')
const mostrarFiltros = ref(false)
const filtroTipo = ref(null)
const filtroFechaInicio = ref('')
const filtroFechaFin = ref('')

const tipoOptions = computed(() =>
  props.tiposPermiso.map(t => ({ label: t.nombre, value: t.id }))
)

function aplicarFiltros() {
}

function limpiarFiltros() {
  filtroTipo.value = null
  filtroFechaInicio.value = ''
  filtroFechaFin.value = ''
}

const columns = [
  { name: 'empleado', label: 'Empleado', align: 'left', field: 'nombreEmpleado', sortable: true },
  { name: 'tipo', label: 'Tipo', align: 'left', field: 'nombreTipo', sortable: true },
  { name: 'fechas', label: 'Fechas', align: 'left', field: 'fechaInicio', sortable: true },
  { name: 'dias', label: 'Días', align: 'right', field: 'dias', sortable: true },
  { name: 'estado', label: 'Estado', align: 'left', field: 'nombreEstado', sortable: true },
  { name: 'acciones', label: 'Acciones', align: 'center', field: 'acciones', sortable: false },
]

const filteredRows = computed(() => {
  let rows = [...props.solicitudes]
  if (busqueda.value) {
    const q = busqueda.value.toLowerCase()
    rows = rows.filter(r =>
      r.nombreEmpleado?.toLowerCase().includes(q) ||
      String(r.id).includes(q)
    )
  }
  if (filtroTipo.value) {
    rows = rows.filter(r => r.idTipo === filtroTipo.value)
  }
  if (filtroFechaInicio.value) {
    rows = rows.filter(r => r.fechaInicio >= filtroFechaInicio.value)
  }
  if (filtroFechaFin.value) {
    rows = rows.filter(r => (r.fechaFin || r.fechaInicio) <= filtroFechaFin.value)
  }
  return rows
})

function getTipoColor(id) {
  const map = { 1: '#2E7D32', 2: '#1976D2', 3: '#F57C00', 4: '#7B1FA2' }
  return map[id] || '#718096'
}

function getEstadoColor(estado) {
  const map = {
    Pendiente: '#1976D2',
    'En revisión': '#F57C00',
    Aprobado: '#2E7D32',
    Rechazado: '#D32F2F',
    Cancelado: '#5B6675',
  }
  return map[estado] || '#5B6675'
}

function calcularDias(row) {
  if (!row.fechaFin) return 1
  const s = new Date(row.fechaInicio + 'T12:00:00')
  const e = new Date(row.fechaFin + 'T12:00:00')
  return Math.round((e - s) / (1000 * 60 * 60 * 24)) + 1
}

</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}
.toolbar-left, .toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.toolbar-btn {
  border-radius: 10px !important;
  font-weight: 600 !important;
  padding: 6px 16px !important;
}
.toolbar-icon-btn:hover {
  background: #E8F5E9 !important;
}
.search-input {
  width: 220px;
}
@media (max-width: 599px) {
  .search-input {
    width: 160px;
  }
  .toolbar-btn {
    padding: 6px 10px !important;
    font-size: 11px !important;
  }
}
.search-input :deep(.q-field__control) {
  border-radius: 10px !important;
  background: #fff !important;
}
.filters-panel {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 12px 0;
}
@media (max-width: 599px) {
  .filters-panel {
    flex-direction: column;
  }
  .filter-item {
    width: 100% !important;
  }
}
.filter-item {
  width: 180px;
}
.filter-item :deep(.q-field__control),
.search-input :deep(.q-field__control) {
  border-radius: 10px !important;
  background: #fff !important;
  border: 1px solid #2E7D32 !important;
  box-shadow: none !important;
}
.search-input :deep(input::placeholder) {
  color: #81C784 !important;
  opacity: 1 !important;
}
.filter-item :deep(.q-field__control::before),
.search-input :deep(.q-field__control::before) {
  border: none !important;
}
.filter-item :deep(.q-field__control::after),
.search-input :deep(.q-field__control::after) {
  border: none !important;
}
.filter-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.filter-btn {
  border-radius: 10px !important;
  font-weight: 600 !important;
  padding: 6px 18px !important;
}
.pending-table {
  border-radius: 12px !important;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06) !important;
}
@media (max-width: 699px) {
  .pending-table {
    overflow-x: auto !important;
  }
}
.pending-table :deep(th) {
  font-size: 11px;
  font-weight: 700;
  color: #1B5E20;
  text-transform: uppercase;
  background: #F5F7FA;
  padding: 10px 12px !important;
}
.pending-table :deep(td) {
  font-size: 13px;
  padding: 10px 12px !important;
}
.pending-table :deep(.q-table--dense .q-td) {
  padding: 8px 12px !important;
}
.pending-table :deep(.table-chip) {
  padding: 4px 14px !important;
  font-size: 13px;
  font-weight: 700;
  opacity: 1 !important;
}
.pending-table :deep(td:nth-child(4)) {
  padding-right: 24px !important;
}
.pending-table :deep(th:nth-child(4)) {
  padding-right: 24px !important;
}
.pending-table :deep(td:nth-child(5)) {
  padding-left: 24px !important;
}
.pending-table :deep(th:nth-child(5)) {
  padding-left: 24px !important;
}
.cell-empleado {
  display: flex;
  align-items: center;
  gap: 10px;
}
.cell-avatar {
  border-radius: 8px !important;
}
.cell-empleado-info {
  display: flex;
  flex-direction: column;
}
.cell-nombre {
  font-weight: 600;
  color: #2D3748;
}
.cell-codigo {
  font-size: 11px;
  color: #718096;
}
.cell-fechas {
  color: #4A5568;
  white-space: nowrap;
}
.cell-dias {
  font-weight: 600;
  color: #2E7D32;
}
.cell-acciones {
  white-space: nowrap;
  display: flex;
  gap: 4px;
  justify-content: center;
}
.action-btn {
  transition: all 0.2s ease;
  border-radius: 8px !important;
  width: 32px !important;
  height: 32px !important;
  font-size: 12px !important;
}
.action-btn:hover {
  transform: scale(1.08);
}
.action-view:hover {
  background: #E8F5E9 !important;
  box-shadow: 0 0 0 2px #2E7D32;
}
.action-approve:hover {
  background: #E8F5E9 !important;
  box-shadow: 0 0 0 2px #388E3C;
}
.action-reject:hover {
  background: #FFF3E0 !important;
  box-shadow: 0 0 0 2px #F57C00;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 50px 0;
}
.empty-text {
  font-size: 15px;
  color: #4A5568;
  font-weight: 600;
}
.empty-hint {
  font-size: 12px;
  color: #A0AEC0;
  font-weight: 400;
}
</style>

<template>
  <div>
    <div class="history-toolbar">
      <q-input
        v-model="busqueda"
        outlined
        dense
        clearable
        debounce="300"
        placeholder="Buscar empleado..."
        class="history-search"
      >
          <template v-slot:prepend>
            <q-icon name="search" color="green-5" size="16px" />
          </template>
      </q-input>
      <div class="toolbar-right">
        <q-select
          v-model="filtroEstado"
          :options="estadoOptions"
          outlined
          dense
          clearable
          placeholder="Estado"
          class="estado-filter"
          emit-value
          map-options
        />
        <q-btn
          unelevated
          no-caps
          color="green-8"
          icon="table_chart"
          label="Exportar Excel"
          size="12px"
          class="toolbar-btn"
          @click="$emit('exportar-excel')"
        />
      </div>
    </div>

    <q-table
      :rows="filteredRows"
      :columns="columns"
      row-key="id"
      flat
      dense
      :pagination="{ rowsPerPage: 10 }"
      class="history-table"
      hide-bottom
    >
      <template v-slot:body-cell-empleado="props">
        <td class="cell-empleado">
          <q-avatar size="26px" class="cell-avatar" color="green-2" text-color="green-8">
            <q-icon name="person" size="14px" />
          </q-avatar>
          <span class="cell-nombre">{{ props.row.nombreEmpleado }}</span>
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
        </td>
      </template>

      <template v-slot:no-data>
        <div class="empty-state">
          <q-icon name="history" size="40px" color="grey-4" />
          <div class="empty-text">No se encontraron solicitudes</div>
          <div class="empty-hint">No hay solicitudes resueltas aún</div>
        </div>
      </template>
    </q-table>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  solicitudes: { type: Array, default: () => [] },
})

defineEmits(['ver-detalle', 'exportar-excel'])

const busqueda = ref('')
const filtroEstado = ref(null)

const estadoOptions = [
  { label: 'Todos', value: null },
  { label: 'Aprobado', value: 'Aprobado' },
  { label: 'Rechazado', value: 'Rechazado' },
  { label: 'Cancelado', value: 'Cancelado' },
  { label: 'Expirado', value: 'Expirado' },
]

const columns = [
  { name: 'empleado', label: 'Empleado', align: 'left', field: 'nombreEmpleado', sortable: true },
  { name: 'tipo', label: 'Tipo', align: 'left', field: 'nombreTipo', sortable: true },
  { name: 'fechas', label: 'Fechas', align: 'left', field: 'fechaInicio', sortable: true },
  { name: 'dias', label: 'Días', align: 'right', field: 'dias', sortable: true },
  { name: 'estado', label: 'Estado', align: 'left', field: 'nombreEstado', sortable: true },
  { name: 'acciones', label: '', align: 'center', field: 'acciones', sortable: false },
]

const filteredRows = computed(() => {
  let rows = [...props.solicitudes]
  if (busqueda.value) {
    const q = busqueda.value.toLowerCase()
    rows = rows.filter(r => r.nombreEmpleado?.toLowerCase().includes(q))
  }
  if (filtroEstado.value) {
    rows = rows.filter(r => r.nombreEstado === filtroEstado.value)
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
    Expirado: '#5B6675',
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
.history-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}
@media (max-width: 599px) {
  .history-search {
    width: 100% !important;
  }
}
.history-search {
  width: 280px;
}
.history-search :deep(.q-field__control) {
  border-radius: 10px !important;
  background: #fff !important;
  border: 1px solid #2E7D32 !important;
  box-shadow: none !important;
}
.history-search :deep(input::placeholder) {
  color: #81C784 !important;
  opacity: 1 !important;
}
.history-search :deep(.q-field__control::before) {
  border: none !important;
}
.history-search :deep(.q-field__control::after) {
  border: none !important;
}
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.estado-filter {
  width: 150px;
}
.estado-filter :deep(.q-field__control) {
  border-radius: 10px !important;
  background: #fff !important;
  border: 1px solid #2E7D32 !important;
  box-shadow: none !important;
}
.estado-filter :deep(.q-field__control::before) {
  border: none !important;
}
.estado-filter :deep(.q-field__control::after) {
  border: none !important;
}
.toolbar-btn {
  border-radius: 10px !important;
  font-weight: 600 !important;
  padding: 6px 16px !important;
}
.history-table {
  border-radius: 12px !important;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06) !important;
}
@media (max-width: 699px) {
  .history-table {
    overflow-x: auto !important;
  }
}
.history-table :deep(th) {
  font-size: 11px;
  font-weight: 700;
  color: #1B5E20;
  text-transform: uppercase;
  background: #F5F7FA;
  padding: 10px 12px !important;
}
.history-table :deep(td) {
  font-size: 13px;
  padding: 10px 12px !important;
}
.history-table :deep(.table-chip) {
  padding: 4px 14px !important;
  font-size: 13px;
  font-weight: 700;
  opacity: 1 !important;
}
.cell-empleado {
  display: flex;
  align-items: center;
  gap: 8px;
}
.cell-avatar {
  border-radius: 6px !important;
}
.cell-nombre {
  font-weight: 600;
  color: #2D3748;
}
.cell-fechas {
  color: #4A5568;
}
.cell-dias {
  font-weight: 600;
  color: #2E7D32;
}
.cell-acciones {
  text-align: center;
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

<template>
  <q-card flat bordered style="border-radius:16px">
    <q-table
      flat
      :rows="rows"
      :columns="columns"
      row-key="idAsistencia"
      :loading="loading"
      :pagination="pagination"
      class="admin-table"
      @request="onRequest"
      binary-state-sort
    >
      <template v-slot:body-cell-empleado="props">
        <q-td :props="props">
          <div class="row items-center no-wrap q-gutter-sm">
            <q-avatar size="36px" color="primary" text-color="white">
              {{ props.row.nombreEmpleado ? props.row.nombreEmpleado.charAt(0).toUpperCase() : '?' }}
            </q-avatar>
            <div>
              <div class="text-weight-medium" style="font-size:0.85rem">{{ props.row.nombreEmpleado }}</div>
              <div style="font-size:0.7rem; color:#888">
                {{ props.row.codigoEmpleado || 'EMP-'+String(props.row.idEmpleado).padStart(3,'0') }}
              </div>
            </div>
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-turno="props">
        <q-td :props="props" style="font-size:0.85rem">
          {{ props.row.turnoNombre || '--' }}
        </q-td>
      </template>

      <template v-slot:body-cell-horaEntrada="props">
        <q-td :props="props">
          <span class="text-weight-medium">{{ formatearHoraAMPM(props.row.horaEntrada) || '--' }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-horaSalida="props">
        <q-td :props="props">
          <span class="text-weight-medium">{{ formatearHoraAMPM(props.row.horaSalida) || '--' }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-horasTrabajadas="props">
        <q-td :props="props">
          <span>{{ props.row.horasTrabajadas ? props.row.horasTrabajadas + 'h' : '--' }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-estado="props">
        <q-td :props="props">
          <q-chip
            :color="chipColor(props.row.estado)"
            text-color="white"
            size="sm"
            class="estado-chip"
          >
            {{ chipLabel(props.row.estado) }}
          </q-chip>
        </q-td>
      </template>

      <template v-slot:body-cell-observaciones="props">
        <q-td :props="props" style="font-size:0.85rem; max-width:150px">
          <span class="ellipsis">{{ props.row.observacion || '--' }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-acciones="props">
        <q-td :props="props">
          <div class="row no-wrap justify-center q-gutter-xs">
            <q-btn flat round dense icon="visibility" size="md" color="teal-9" @click="$emit('ver-detalle', props.row)">
              <q-tooltip>Ver detalle</q-tooltip>
            </q-btn>
            <q-btn flat round dense icon="fact_check" size="md" color="orange-8" @click="$emit('justificar', props.row)">
              <q-tooltip>Justificar</q-tooltip>
            </q-btn>
            <q-btn flat round dense icon="more_vert" size="md" color="light-green-6">
              <q-tooltip>M&aacute;s acciones</q-tooltip>
            </q-btn>
          </div>
        </q-td>
      </template>

      <template v-slot:no-data>
        <div class="text-center q-pa-md text-grey-6">
          <q-icon name="info" size="40px" class="q-mb-sm" />
          <div>No se encontraron registros para esta fecha</div>
        </div>
      </template>
    </q-table>
  </q-card>
</template>

<script setup>
import { ref } from 'vue'
import { formatearHoraAMPM } from '../../util/formatearHora'

defineProps({
  rows: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  pagination: { type: Object, default: () => ({ page: 1, rowsPerPage: 10, rowsNumber: 0, sortBy: 'fecha', descending: true }) }
})

defineEmits(['ver-detalle', 'justificar', 'request'])

const columns = [
  { name: 'empleado', label: 'Empleado', align: 'left', field: 'nombreEmpleado', sortable: true },
  { name: 'turno', label: 'Turno', align: 'center', field: 'turnoNombre', sortable: true },
  { name: 'horaEntrada', label: 'Hora Entrada', align: 'center', field: 'horaEntrada', sortable: true },
  { name: 'horaSalida', label: 'Hora Salida', align: 'center', field: 'horaSalida', sortable: true },
  { name: 'horasTrabajadas', label: 'Horas Trab.', align: 'center', field: 'horasTrabajadas' },
  { name: 'estado', label: 'Estado', align: 'center', field: 'estado', sortable: true },
  { name: 'acciones', label: 'Acciones', align: 'center' }
]

function chipColor(estado) {
  switch (estado) {
    case 'COMPLETO': return 'positive'
    case 'PRESENTE': return 'blue'
    case 'TARDANZA': return 'orange'
    case 'FALTA': return 'negative'
    case 'JUSTIFICADO': return 'blue-5'
    default: return 'grey'
  }
}

function chipLabel(estado) {
  switch (estado) {
    case 'COMPLETO': return 'A tiempo'
    case 'PRESENTE': return 'Pendiente salida'
    case 'TARDANZA': return 'Tardanza'
    case 'FALTA': return 'Falta'
    case 'JUSTIFICADO': return 'Justificado'
    default: return estado || '--'
  }
}

function onRequest(requestProps) {
  emit('request', requestProps)
}
</script>

<style scoped>
.admin-table :deep(.q-table__card) {
  border-radius: 16px;
  border: 2px solid #006051;
  font-family: 'Nunito', sans-serif;
}

.admin-table :deep(.q-table__top) {
  padding: 0;
}

.admin-table :deep(thead th) {
  font-weight: 700;
  color: #006051;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-family: 'Nunito', sans-serif;
}

.admin-table :deep(tbody tr:nth-child(even)) {
  background: #f0faf8;
}

.admin-table :deep(tbody tr:hover) {
  background: #e0f5f0;
}

.admin-table :deep(td) {
  font-size: 0.9rem;
  font-family: 'Nunito', sans-serif;
  color: #333;
}

.admin-table :deep(.q-table__bottom) {
  font-family: 'Nunito', sans-serif;
}
</style>

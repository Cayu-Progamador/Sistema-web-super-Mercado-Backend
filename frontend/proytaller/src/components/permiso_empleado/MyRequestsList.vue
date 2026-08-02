<template>
  <div class="my-requests">
    <div class="requests-header">
      <div class="requests-title">Mis solicitudes</div>
      <q-select
        v-if="props.solicitudes.length > 0"
        v-model="filtroEstado"
        :options="estadoOptions"
        outlined
        dense
        emit-value
        map-options
        class="filter-select"
      />
    </div>

    <div v-if="loading" class="q-pa-md text-center">
      <q-spinner-dots color="green-7" size="40px" />
    </div>

    <div v-else-if="filteredList.length === 0" class="empty-state">
      <q-icon name="inbox" size="48px" color="grey-4" />
      <div class="empty-text">No tienes solicitudes con este estado</div>
    </div>

    <div v-else class="requests-list">
      <RequestCard
        v-for="item in filteredList"
        :key="item.id"
        :tipo-nombre="item.nombreTipo"
        :tipo-color="getTipoColor(item.idTipo)"
        :tipo-icon="getTipoIcon(item.idTipo)"
        :fechas="formatearFechas(item.fechaInicio, item.fechaFin)"
        :dias-texto="calcularDias(item.fechaInicio, item.fechaFin)"
        :motivo="item.motivo"
        :estado="item.nombreEstado"
        :progreso="getProgreso(item.nombreEstado)"
        @ver-detalle="$emit('ver-detalle', item)"
        @cancelar="$emit('cancelar', item)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import RequestCard from './RequestCard.vue'

const props = defineProps({
  solicitudes: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false }
})

defineEmits(['ver-detalle', 'cancelar'])

const filtroEstado = ref('Todos')

const estadoOptions = [
  { label: 'Todos', value: 'Todos' },
  { label: 'Pendiente', value: 'Pendiente' },
  { label: 'Aprobado', value: 'Aprobado' },
  { label: 'Rechazado', value: 'Rechazado' },
  { label: 'Cancelado', value: 'Cancelado' }
]

const filteredList = computed(() => {
  if (filtroEstado.value === 'Todos') return props.solicitudes
  return props.solicitudes.filter(s => s.nombreEstado === filtroEstado.value)
})

const tipoColors = { 1: '#2E7D32', 2: '#1976D2', 3: '#F57C00', 4: '#7B1FA2' }
const tipoIcons = { 1: 'beach_access', 2: 'local_hospital', 3: 'person', 4: 'family_restroom' }

function getTipoColor(id) {
  return tipoColors[id] || '#2E7D32'
}
function getTipoIcon(id) {
  return tipoIcons[id] || 'event'
}
function getProgreso(estado) {
  if (estado === 'Pendiente') return 1
  if (estado === 'En revisión') return 2
  if (estado === 'Aprobado') return 4
  if (estado === 'Rechazado') return 4
  if (estado === 'Expirado') return 2
  return 1
}
function formatearFechas(inicio, fin) {
  const fmt = d => {
    const [y, m, day] = d.split('-')
    return `${day}/${m}`
  }
  if (!fin) return fmt(inicio)
  return `${fmt(inicio)} - ${fmt(fin)}`
}
function calcularDias(inicio, fin) {
  if (!fin) return '1 día'
  const start = new Date(inicio + 'T12:00:00')
  const end = new Date(fin + 'T12:00:00')
  const diff = Math.round((end - start) / (1000 * 60 * 60 * 24)) + 1
  return `${diff} día(s)`
}
</script>

<style scoped>
.my-requests {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.requests-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.requests-title {
  font-size: 18px;
  font-weight: 700;
  color: #1B5E20;
}
.filter-select {
  width: 160px;
}
.filter-select :deep(.q-field__control) {
  border-radius: 10px !important;
  background: #fff !important;
}
.filter-select :deep(.q-field__native) {
  font-weight: 600 !important;
  font-size: 13px !important;
}
.requests-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 40px 0;
}
.empty-text {
  font-size: 14px;
  color: #A0AEC0;
  font-weight: 500;
}
</style>

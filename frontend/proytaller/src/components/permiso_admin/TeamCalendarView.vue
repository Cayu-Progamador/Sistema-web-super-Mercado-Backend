<template>
  <div>
    <div class="calendar-toolbar">
      <div class="calendar-nav">
        <q-btn flat round dense icon="chevron_left" @click="mesAnterior" />
        <div class="calendar-title">{{ mesActualTexto }} {{ añoActual }}</div>
        <q-btn flat round dense icon="chevron_right" @click="mesSiguiente" />
        <q-btn flat no-caps dense label="Hoy" size="12px" color="green-8" @click="irHoy" class="q-ml-sm" />
      </div>
      <div class="calendar-count" v-if="totalPermisosMes > 0">{{ totalPermisosMes }} permiso(s) este mes</div>
      <q-select
        v-model="filtroTipo"
        :options="tipoOptions"
        outlined
        dense
        clearable
        label="Tipo de permiso"
        class="calendar-filter"
        emit-value
        map-options
      />
    </div>

    <div class="calendar-grid">
      <div class="calendar-header">
        <div v-for="d in diasSemana" :key="d" class="calendar-header-cell">{{ d }}</div>
      </div>
      <div class="calendar-body">
        <div
          v-for="(day, i) in diasDelMes"
          :key="i"
          class="calendar-cell"
          :class="{ 'calendar-cell--today': day.isToday, 'calendar-cell--other': !day.isCurrentMonth }"
        >
          <div class="calendar-day-number">{{ day.numero }}</div>
          <div class="calendar-pills">
            <div
              v-for="(p, pi) in day.permisos.slice(0, 2)"
              :key="pi"
              class="calendar-pill"
              :style="{ background: getTipoColor(p.idTipo) }"
            >
              {{ p.nombreEmpleado.split(' ')[0] }}
              <q-tooltip anchor="center middle" self="bottom middle" class="bg-green-9">
                <div><strong>{{ p.nombreTipo }}</strong></div>
                <div>Inicio: {{ formatFecha(p.fechaInicio) }}</div>
                <div>Fin: {{ formatFecha(p.fechaFin || p.fechaInicio) }}</div>
                <div>Duración: {{ diasPermiso(p) }} día(s)</div>
              </q-tooltip>
            </div>
            <div v-if="day.permisos.length > 2" class="calendar-pill calendar-pill--more">
              +{{ day.permisos.length - 2 }} más
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="calendar-legend">
      <div v-for="t in legendTipos" :key="t.value" class="legend-item">
        <div class="legend-dot" :style="{ background: getTipoColor(t.value) }"></div>
        <span class="legend-label">{{ t.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  solicitudes: { type: Array, default: () => [] },
})

const fechaActual = ref(new Date())
const filtroTipo = ref(null)

const diasSemana = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb']

const legendTipos = computed(() => {
  const map = new Map()
  props.solicitudes.forEach(s => {
    if (s.idTipo && !map.has(s.idTipo)) map.set(s.idTipo, { label: s.nombreTipo, value: s.idTipo })
  })
  return Array.from(map.values())
})

const tipoOptions = computed(() => {
  const map = new Map()
  props.solicitudes.forEach(s => {
    if (s.idTipo && !map.has(s.idTipo)) map.set(s.idTipo, { label: s.nombreTipo, value: s.idTipo })
  })
  const tipos = Array.from(map.values())
  return [{ label: 'Todos', value: -1 }, ...tipos]
})

const mesActualTexto = computed(() => {
  return fechaActual.value.toLocaleString('es-ES', { month: 'long' })
})

const añoActual = computed(() => fechaActual.value.getFullYear())

const totalPermisosMes = computed(() => {
  const año = fechaActual.value.getFullYear()
  const mes = String(fechaActual.value.getMonth() + 1).padStart(2, '0')
  return props.solicitudes.filter(s => {
    if (filtroTipo.value && filtroTipo.value !== -1 && s.idTipo !== filtroTipo.value) return false
    if (s.nombreEstado !== 'Aprobado') return false
    return s.fechaInicio?.startsWith(`${año}-${mes}`) || s.fechaFin?.startsWith(`${año}-${mes}`)
  }).length
})

const diasDelMes = computed(() => {
  const año = fechaActual.value.getFullYear()
  const mes = fechaActual.value.getMonth()
  const hoy = new Date()

  const primerDia = new Date(año, mes, 1)
  const ultimoDia = new Date(año, mes + 1, 0)
  const diasEnMes = ultimoDia.getDate()
  const diaSemanaInicio = primerDia.getDay()

  const days = []

  for (let i = 0; i < diaSemanaInicio; i++) {
    days.push({ numero: '', permisos: [], isCurrentMonth: false, isToday: false })
  }

  for (let d = 1; d <= diasEnMes; d++) {
    const date = new Date(año, mes, d)
    const dateStr = `${año}-${String(mes + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    const permisosDelDia = props.solicitudes.filter(s => {
      if (filtroTipo.value && filtroTipo.value !== -1 && s.idTipo !== filtroTipo.value) return false
      const inicio = s.fechaInicio
      const fin = s.fechaFin || s.fechaInicio
      return dateStr >= inicio && dateStr <= fin && s.nombreEstado === 'Aprobado'
    }).map(s => ({
      ...s,
      color: getTipoColor(s.idTipo)
    }))
    days.push({
      numero: d,
      permisos: permisosDelDia,
      isCurrentMonth: true,
      isToday: date.toDateString() === hoy.toDateString(),
    })
  }

  const totalCells = Math.ceil(days.length / 7) * 7
  while (days.length < totalCells) {
    days.push({ numero: '', permisos: [], isCurrentMonth: false, isToday: false })
  }

  return days
})

function mesAnterior() {
  fechaActual.value = new Date(fechaActual.value.getFullYear(), fechaActual.value.getMonth() - 1, 1)
}

function mesSiguiente() {
  fechaActual.value = new Date(fechaActual.value.getFullYear(), fechaActual.value.getMonth() + 1, 1)
}

function irHoy() {
  fechaActual.value = new Date()
}

const tipoColores = { 1: '#2E7D32', 2: '#1976D2', 3: '#F57C00', 4: '#7B1FA2' }
function getTipoColor(id) {
  return tipoColores[id] || '#718096'
}

function formatFecha(f) {
  if (!f) return ''
  const [y, m, d] = f.split('-')
  return `${d}/${m}/${y}`
}

function diasPermiso(p) {
  const inicio = new Date(p.fechaInicio)
  const fin = new Date(p.fechaFin || p.fechaInicio)
  return Math.round((fin - inicio) / 86400000) + 1
}
</script>

<style scoped>
.calendar-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 10px;
}
.calendar-nav {
  display: flex;
  align-items: center;
  gap: 4px;
}
.calendar-title {
  font-size: 20px;
  font-weight: 700;
  color: #1B5E20;
  text-transform: capitalize;
  min-width: 160px;
  text-align: center;
}
.calendar-count {
  font-size: 13px;
  font-weight: 600;
  color: #2E7D32;
  background: #E8F5E9;
  padding: 4px 14px;
  border-radius: 20px;
}
.calendar-filter {
  width: 180px;
}
.calendar-filter :deep(.q-field__control) {
  border-radius: 10px !important;
  background: #fff !important;
  border: 1px solid #2E7D32 !important;
  box-shadow: none !important;
}
.calendar-filter :deep(.q-field__control::before) {
  border: none !important;
}
.calendar-filter :deep(.q-field__control::after) {
  border: none !important;
}
.calendar-grid {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  background: #fff;
  border: 1px solid #A5D6A7;
}
.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #E8F5E9;
  border-bottom: 2px solid #A5D6A7;
}
.calendar-header-cell {
  padding: 8px 6px;
  font-size: 11px;
  font-weight: 700;
  color: #1B5E20;
  text-transform: uppercase;
  text-align: center;
  border-right: 1px solid #C8E6C9;
}
.calendar-header-cell:last-child {
  border-right: none;
}
.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}
.calendar-cell {
  min-height: 80px;
  padding: 4px;
  border-right: 1px solid #C8E6C9;
  border-bottom: 1px solid #C8E6C9;
}
@media (max-width: 599px) {
  .calendar-cell {
    min-height: 60px;
    padding: 2px;
  }
  .calendar-day-number {
    font-size: 10px;
  }
  .calendar-pill {
    font-size: 8px;
    padding: 0 3px;
  }
}
.calendar-cell--other {
  background: #FAFBFC;
}
.calendar-cell--today {
  background: #E8F5E9;
}
.calendar-day-number {
  font-size: 12px;
  font-weight: 600;
  color: #2D3748;
  margin-bottom: 4px;
}
.calendar-cell--today .calendar-day-number {
  color: #2E7D32;
}
.calendar-pills {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.calendar-pill {
  font-size: 10px;
  color: #fff;
  padding: 1px 6px;
  border-radius: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}
.calendar-pill--more {
  background: #E2E8F0 !important;
  color: #4A5568 !important;
  font-weight: 600;
}
.calendar-legend {
  display: flex;
  gap: 16px;
  margin-top: 14px;
  flex-wrap: wrap;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}
.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 3px;
}
.legend-label {
  font-size: 12px;
  color: #4A5568;
  font-weight: 500;
}
</style>

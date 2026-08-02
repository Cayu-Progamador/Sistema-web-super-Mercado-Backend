<template>
  <q-drawer
    v-model="visible"
    side="right"
    overlay
    behavior="mobile"
    bordered
    :width="420"
    class="drawer-card"
    style="background:#f0f7eb"
  >
    <q-scroll-area class="fit">
      <div v-if="empleado" class="q-pa-md">
        <div class="row items-center q-mb-md">
          <q-btn flat round dense icon="close" style="color:#2a5c1a" @click="visible = false" />
          <q-space />
          <q-chip :color="chipColor(empleado.estado)" text-color="white" size="sm" class="estado-chip">
            {{ chipLabel(empleado.estado) }}
          </q-chip>
        </div>

        <div class="text-center q-mb-lg">
          <q-avatar size="80px" color="primary" text-color="white" class="q-mb-sm">
            {{ iniciales }}
          </q-avatar>
          <div class="text-h6 text-weight-bold">{{ empleado.nombreEmpleado }}</div>
          <div style="font-size:0.85rem; color:#666">{{ empleado.codigoEmpleado || 'EMP-'+String(empleado.idEmpleado).padStart(3,'0') }}</div>
          <div style="font-size:0.85rem; color:#666">{{ empleado.cargo || '--' }}</div>
        </div>

        <q-separator class="q-mb-md" />

        <div class="drawer-section-title q-mb-sm">
          <q-icon name="today" class="q-mr-xs" size="16px" />
          Registro de hoy
        </div>
        <q-card flat bordered class="q-pa-sm q-mb-md" style="border-radius:12px">
          <div class="row q-col-gutter-sm">
            <div class="col-6">
              <div class="text-caption text-grey">Turno</div>
              <div class="text-weight-medium">{{ empleado.turnoNombre || '--' }}</div>
            </div>
            <div class="col-6">
              <div class="text-caption text-grey">Horas trabajadas</div>
              <div class="text-weight-medium">{{ empleado.horasTrabajadas || '--' }}</div>
            </div>
            <div class="col-6">
              <div class="text-caption text-grey">Entrada</div>
              <div class="text-weight-medium">{{ formatearHora(empleado.horaEntrada) || '--' }}</div>
            </div>
            <div class="col-6">
              <div class="text-caption text-grey">Salida</div>
              <div class="text-weight-medium">{{ formatearHora(empleado.horaSalida) || '--' }}</div>
            </div>
          </div>
          <div v-if="empleado.observacion" class="q-mt-sm">
            <div class="text-caption text-grey">Observaci&oacute;n</div>
            <div style="font-size:0.85rem">{{ empleado.observacion }}</div>
          </div>
        </q-card>

        <div class="drawer-section-title q-mb-sm">
          <q-icon name="date_range" class="q-mr-xs" size="16px" style="color:#2a5c1a" />
          <span style="color:#2a5c1a">Resumen del mes</span>
        </div>
        <div class="row q-col-gutter-xs q-mb-md">
          <div class="col-6 col-sm-3">
            <q-card flat bordered class="kpi-card q-pa-sm text-center">
              <div class="kpi-number" style="color:#2a5c1a">{{ resumen.presentes }}</div>
              <div class="kpi-label">Presentes</div>
            </q-card>
          </div>
          <div class="col-6 col-sm-3">
            <q-card flat bordered class="kpi-card q-pa-sm text-center">
              <div class="kpi-number" style="color:#e65100">{{ resumen.tardanzas }}</div>
              <div class="kpi-label">Tardanzas</div>
            </q-card>
          </div>
          <div class="col-6 col-sm-3">
            <q-card flat bordered class="kpi-card q-pa-sm text-center">
              <div class="kpi-number" style="color:#c62828">{{ resumen.faltas }}</div>
              <div class="kpi-label">Faltas</div>
            </q-card>
          </div>
          <div class="col-6 col-sm-3">
            <q-card flat bordered class="kpi-card q-pa-sm text-center">
              <div class="kpi-number" style="color:#1565c0">{{ resumen.justificados }}</div>
              <div class="kpi-label">Justificados</div>
            </q-card>
          </div>
        </div>

        <div class="drawer-section-title q-mb-sm">
          <q-icon name="calendar_month" class="q-mr-xs" size="16px" />
          Calendario de asistencia - {{ mesNombre }}
        </div>
        <div class="calendar-grid q-mb-sm">
          <div class="calendar-header">L</div>
          <div class="calendar-header">M</div>
          <div class="calendar-header">M</div>
          <div class="calendar-header">J</div>
          <div class="calendar-header">V</div>
          <div class="calendar-header">S</div>
          <div class="calendar-header">D</div>
          <div
            v-for="(dia, idx) in diasCalendario"
            :key="idx"
            class="calendar-cell"
            :class="{
              'calendar-presente': dia.estado === 'PRESENTE' || dia.estado === 'COMPLETO',
              'calendar-tardanza': dia.estado === 'TARDANZA',
              'calendar-ausente': dia.estado === 'AUSENTE' || dia.estado === 'FALTA',
              'bg-blue-2 text-blue-9': dia.estado === 'JUSTIFICADO',
              'calendar-permiso': dia.estado === 'PERMISO',
              'calendar-futuro': dia.estado === 'FUTURO',
              'calendar-descanso': dia.estado === 'DESCANSO',
              'today': dia.esHoy
            }"
          >
            {{ dia.numero }}
          </div>
        </div>
        <div class="calendar-legend q-mb-md">
          <div class="legend-item">
            <span class="legend-dot" style="background:#2a5c1a"></span> Presente
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background:#e65100"></span> Tardanza
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background:#c62828"></span> Falta
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background:#1565c0"></span> Justificado
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background:#e0e0e0"></span> Permiso
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background:#f5f5f5; border:1px solid #ccc"></span> Futuro
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background:#ffffff; border:1px solid #e8e8e8"></span> Descanso
          </div>
        </div>
      </div>
    </q-scroll-area>
  </q-drawer>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  empleado: { type: Object, default: null },
  resumen: { type: Object, default: () => ({ presentes: 0, tardanzas: 0, faltas: 0, justificados: 0 }) },
  diasCalendario: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)

const iniciales = computed(() => {
  return props.empleado?.nombreEmpleado
    ? props.empleado.nombreEmpleado.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase()
    : '??'
})

const mesNombre = computed(() => {
  if (!props.diasCalendario.length) return ''
  const fecha = props.diasCalendario.find(d => d.fecha)?.fecha
  if (!fecha) return ''
  const [anio, mes] = fecha.split('-')
  const nombres = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Setiembre','Octubre','Noviembre','Diciembre']
  return `${nombres[parseInt(mes) - 1]} ${anio}`
})

watch(() => props.modelValue, (val) => { visible.value = val })
watch(visible, (val) => { emit('update:modelValue', val) })

function chipColor(estado) {
  switch (estado) {
    case 'A tiempo': case 'COMPLETO': return 'positive'
    case 'PRESENTE': case 'Pendiente salida': return 'blue'
    case 'TARDANZA': return 'orange'
    case 'FALTA': case 'AUSENTE': return 'negative'
    case 'JUSTIFICADO': return 'blue-5'
    case 'PERMISO': return 'teal'
    default: return 'grey'
  }
}

function chipLabel(estado) {
  switch (estado) {
    case 'COMPLETO': return 'A tiempo'
    case 'PRESENTE': return 'Pendiente salida'
    case 'TARDANZA': return 'Tardanza'
    case 'FALTA': return 'Falta'
    case 'AUSENTE': return 'Falta'
    case 'JUSTIFICADO': return 'Justificado'
    case 'PERMISO': return 'Permiso'
    default: return estado || '--'
  }
}

function formatearHora(hora) {
  if (!hora) return ''
  return hora.split(':').slice(0, 2).join(':')
}
</script>

<style scoped>
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
}

.calendar-cell {
  aspect-ratio: 1;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  font-weight: 500;
  cursor: default;
  transition: all 0.15s ease;
  border: 1px solid rgba(0,0,0,0.06);
}

.calendar-header {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.65rem;
  font-weight: 700;
  color: #2a5c1a;
  padding-bottom: 2px;
}

.calendar-presente {
  background: #2a5c1a;
  color: #ffffff;
  font-weight: 600;
}

.calendar-tardanza {
  background: #e65100;
  color: #ffffff;
  font-weight: 600;
}

.calendar-ausente {
  background: #c62828;
  color: #ffffff;
  font-weight: 600;
}

.calendar-permiso {
  background: #616161;
  color: #ffffff;
  font-weight: 600;
}

.calendar-futuro {
  background: #e0e0e0;
  color: #999;
  font-weight: 400;
}

.calendar-descanso {
  background: #ffffff;
  color: #bbb;
  font-weight: 400;
  border: 1px solid #e8e8e8;
}

.calendar-cell:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(0,0,0,0.1);
}

.calendar-cell.today {
  border: 2px solid #2a5c1a;
  font-weight: 700;
  box-shadow: 0 2px 6px rgba(42,92,26,0.2);
}

.calendar-legend {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.7rem;
  color: #666;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 3px;
}

.kpi-card {
  border-radius: 12px;
  border: 1px solid #4a8c3f !important;
  box-shadow: 0 2px 6px rgba(42,92,26,0.12) !important;
}

.drawer-section-title {
  font-size: 0.85rem;
  font-weight: 700;
  color: #2a5c1a;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

:deep(.drawer-card) {
  background: #f0f7eb !important;
}

.kpi-number {
  font-size: 1.3rem;
  font-weight: 800;
  color: #2a5c1a;
  margin-bottom: 2px;
}

.kpi-label {
  font-size: 0.65rem;
  color: #2a5c1a;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
</style>

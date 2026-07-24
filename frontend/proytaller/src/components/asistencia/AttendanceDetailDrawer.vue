<template>
  <q-drawer
    v-model="visible"
    side="right"
    overlay
    behavior="mobile"
    bordered
    :width="420"
    class="drawer-card"
  >
    <q-scroll-area class="fit">
      <div v-if="empleado" class="q-pa-md">
        <div class="row items-center q-mb-md">
          <q-btn flat round dense icon="close" @click="visible = false" />
          <q-space />
          <q-chip :color="chipColor(empleado.estadoActual)" text-color="white" size="sm" class="estado-chip">
            {{ chipLabel(empleado.estadoActual) }}
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
              <div class="text-weight-medium">{{ empleado.turno || '--' }}</div>
            </div>
            <div class="col-6">
              <div class="text-caption text-grey">Horas trabajadas</div>
              <div class="text-weight-medium">{{ empleado.horasTrabajadas || '--' }}</div>
            </div>
            <div class="col-6">
              <div class="text-caption text-grey">Entrada</div>
              <div class="text-weight-medium">{{ empleado.horaEntrada || '--' }}</div>
            </div>
            <div class="col-6">
              <div class="text-caption text-grey">Salida</div>
              <div class="text-weight-medium">{{ empleado.horaSalida || '--' }}</div>
            </div>
          </div>
          <div v-if="empleado.observacion" class="q-mt-sm">
            <div class="text-caption text-grey">Observaci&oacute;n</div>
            <div style="font-size:0.85rem">{{ empleado.observacion }}</div>
          </div>
        </q-card>

        <div class="drawer-section-title q-mb-sm">
          <q-icon name="date_range" class="q-mr-xs" size="16px" />
          Resumen del mes
        </div>
        <div class="row q-col-gutter-sm q-mb-md">
          <div class="col-3">
            <q-card flat bordered class="q-pa-sm text-center" style="border-radius:12px">
              <div class="drawer-kpi-number text-positive">{{ resumen.presentes }}</div>
              <div class="drawer-kpi-label">Presentes</div>
            </q-card>
          </div>
          <div class="col-3">
            <q-card flat bordered class="q-pa-sm text-center" style="border-radius:12px">
              <div class="drawer-kpi-number text-orange">{{ resumen.tardanzas }}</div>
              <div class="drawer-kpi-label">Tardanzas</div>
            </q-card>
          </div>
          <div class="col-3">
            <q-card flat bordered class="q-pa-sm text-center" style="border-radius:12px">
              <div class="drawer-kpi-number text-negative">{{ resumen.faltas }}</div>
              <div class="drawer-kpi-label">Faltas</div>
            </q-card>
          </div>
          <div class="col-3">
            <q-card flat bordered class="q-pa-sm text-center" style="border-radius:12px">
              <div class="drawer-kpi-number text-grey-7">{{ resumen.justificados }}</div>
              <div class="drawer-kpi-label">Justificados</div>
            </q-card>
          </div>
        </div>

        <div class="drawer-section-title q-mb-sm">
          <q-icon name="calendar_month" class="q-mr-xs" size="16px" />
          Calendario de asistencia
        </div>
        <div class="calendar-grid q-mb-sm">
          <div
            v-for="(dia, idx) in diasCalendario"
            :key="idx"
            class="calendar-cell"
            :class="{
              'bg-green-2 text-green-9': dia.estado === 'PRESENTE' || dia.estado === 'COMPLETO',
              'bg-orange-2 text-orange-9': dia.estado === 'TARDANZA',
              'bg-red-2 text-red-9': dia.estado === 'AUSENTE' || dia.estado === 'FALTA',
              'bg-blue-2 text-blue-9': dia.estado === 'JUSTIFICADO',
              'bg-grey-2 text-grey-7': dia.estado === 'PERMISO' || dia.estado === 'FUTURO',
              'today': dia.esHoy
            }"
          >
            {{ dia.numero }}
          </div>
        </div>
        <div class="calendar-legend q-mb-md">
          <div class="legend-item">
            <span class="legend-dot bg-green-4"></span> Presente
          </div>
          <div class="legend-item">
            <span class="legend-dot bg-orange-4"></span> Tardanza
          </div>
          <div class="legend-item">
            <span class="legend-dot bg-red-4"></span> Falta
          </div>
          <div class="legend-item">
            <span class="legend-dot bg-blue-4"></span> Justificado
          </div>
          <div class="legend-item">
            <span class="legend-dot bg-grey-3"></span> Permiso / Futuro
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

watch(() => props.modelValue, (val) => { visible.value = val })
watch(visible, (val) => { emit('update:modelValue', val) })

function chipColor(estado) {
  switch (estado) {
    case 'A tiempo': case 'COMPLETO': return 'positive'
    case 'PRESENTE': case 'Pendiente salida': return 'blue'
    case 'TARDANZA': return 'orange'
    case 'AUSENTE': return 'negative'
    case 'PERMISO': return 'grey'
    default: return 'grey'
  }
}

function chipLabel(estado) {
  switch (estado) {
    case 'COMPLETO': return 'A tiempo'
    case 'PRESENTE': return 'Pendiente salida'
    case 'TARDANZA': return 'Tardanza'
    case 'AUSENTE': return 'Falta'
    case 'PERMISO': return 'Permiso'
    default: return estado || '--'
  }
}
</script>

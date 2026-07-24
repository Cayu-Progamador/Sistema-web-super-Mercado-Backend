<template>
  <q-card class="clock-gradient text-white q-pa-xl">
    <div class="row items-center q-mb-md">
      <div class="col">
        <div class="live-time">{{ formatearHoraAMPM(horaActual) }}</div>
        <div class="live-date q-mt-xs">{{ fechaFormateada }}</div>
      </div>
      <div class="col-auto text-center">
        <q-avatar size="60px" class="employee-avatar">
          <img v-if="fotoUrl" :src="fotoUrl" />
          <span v-else class="text-h4 text-weight-light">{{ iniciales }}</span>
        </q-avatar>
        <div class="text-weight-bold q-mt-xs" style="font-size:0.9rem">{{ nombreEmpleado }}</div>
        <div style="font-size:0.75rem; opacity:0.7">{{ codigoEmpleado }}</div>
        <div style="font-size:0.75rem; opacity:0.7">{{ cargo }}</div>
      </div>
    </div>

    <div class="text-center q-mb-xs" v-if="turnoNombre">
      <q-chip size="sm" outline color="white" text-color="white" style="opacity:0.85">
        <q-icon name="badge" class="q-mr-xs" /> Turno: {{ turnoNombre }}
      </q-chip>
    </div>

    <div class="row q-col-gutter-md q-mb-sm" v-if="horaEntradaEsperada">
      <div class="col text-center">
        <div style="font-size:0.75rem; opacity:0.7">Entrada esperada</div>
        <div class="text-weight-bold">{{ formatearHoraAMPM(horaEntradaEsperada) }}</div>
      </div>
      <div class="col text-center">
        <div style="font-size:0.75rem; opacity:0.7">Salida esperada</div>
        <div class="text-weight-bold">{{ formatearHoraAMPM(horaSalidaEsperada) }}</div>
      </div>
      <div class="col text-center" v-if="toleranciaMinutos">
        <div style="font-size:0.75rem; opacity:0.7">Tolerancia</div>
        <div class="text-weight-bold">{{ toleranciaMinutos }} min</div>
      </div>
    </div>

    <q-separator dark class="q-my-md" style="opacity:0.15" />

    <div class="text-center q-mb-md" v-if="!errorMessage">
      <span class="status-badge" :style="{ background: estadoColor }">
        <q-icon :name="estadoIcono" />
        {{ estadoTexto }}
      </span>
    </div>

    <div v-if="errorMessage" class="q-mb-md q-pa-sm bg-red-7 text-white text-center rounded-borders" style="border-radius:8px; font-size:0.85rem">
      <q-icon name="warning" class="q-mr-xs" />
      {{ errorMessage }}
    </div>

    <div v-else-if="sinTurnoHoy" class="q-mb-md q-pa-sm bg-orange-8 text-white text-center rounded-borders" style="border-radius:8px; font-size:0.85rem">
      <q-icon name="event_busy" class="q-mr-xs" />
      No tienes turno asignado para hoy
    </div>

    <div class="row q-col-gutter-md q-mb-md">
      <div class="col-12 col-sm-6">
        <q-btn
          :disable="!btnEntradaActivo"
          class="mark-btn entrada full-width"
          unelevated
          no-caps
          @click="$emit('marcar', 'ENTRADA')"
        >
          <div class="column items-center q-py-sm">
            <q-icon name="login" size="24px" />
            <span>Marcar Entrada</span>
            <small v-if="horaEntradaHoy" style="opacity:0.6">
              {{ formatearHoraAMPM(horaEntradaHoy) }}
            </small>
            <small v-else-if="!btnEntradaActivo && horaEntradaEsperada && !errorMessage" style="opacity:0.6">
              disponible desde {{ formatearHoraAMPM(inicioPermitido) }}
            </small>
          </div>
        </q-btn>
      </div>
      <div class="col-12 col-sm-6">
        <q-btn
          :disable="!horaEntradaHoy || !!horaSalidaHoy || !!errorMessage || sinTurnoHoy || !btnSalidaActivo"
          class="mark-btn salida full-width"
          unelevated
          no-caps
          @click="$emit('marcar', 'SALIDA')"
        >
          <div class="column items-center q-py-sm">
            <q-icon name="logout" size="24px" />
            <span>Marcar Salida</span>
            <small v-if="horaSalidaHoy" style="opacity:0.6">
              {{ formatearHoraAMPM(horaSalidaHoy) }}
            </small>
          </div>
        </q-btn>
      </div>
    </div>

    <div class="text-center geo-text">
      <q-icon name="location_on" size="14px" class="q-mr-xs" />
      Verificaci&oacute;n por sucursal &middot; Supermercado Central
    </div>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'
import { formatearHoraAMPM } from '../../util/formatearHora'

const props = defineProps({
  horaActual: { type: String, default: '' },
  fechaFormateada: { type: String, default: '' },
  nombreEmpleado: { type: String, default: '' },
  codigoEmpleado: { type: String, default: '' },
  cargo: { type: String, default: '' },
  fotoUrl: { type: String, default: '' },
  horaEntradaHoy: { type: String, default: null },
  horaSalidaHoy: { type: String, default: null },
  estadoHoy: { type: String, default: null },
  horaEntradaEsperada: { type: String, default: null },
  horaSalidaEsperada: { type: String, default: null },
  toleranciaMinutos: { type: Number, default: null },
  turnoNombre: { type: String, default: null },
  errorMessage: { type: String, default: null }
})

defineEmits(['marcar'])

const iniciales = computed(() => {
  return props.nombreEmpleado
    ? props.nombreEmpleado.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase()
    : '??'
})

const estadoTexto = computed(() => {
  if (props.estadoHoy === 'JUSTIFICADO') return 'Ausencia justificada'
  if (props.estadoHoy === 'FALTA') return 'Falta'
  if (props.estadoHoy === 'TARDANZA') return `Tardanza - ${props.horaEntradaHoy ? formatearHoraAMPM(props.horaEntradaHoy) : ''}`
  if (!props.horaEntradaHoy && !props.horaSalidaHoy) return 'Aún no marcaste entrada'
  if (props.horaEntradaHoy && !props.horaSalidaHoy) return `En turno desde las ${formatearHoraAMPM(props.horaEntradaHoy)}`
  return 'Jornada completada'
})

const estadoIcono = computed(() => {
  if (props.estadoHoy === 'JUSTIFICADO') return 'verified'
  if (props.estadoHoy === 'FALTA') return 'block'
  if (props.estadoHoy === 'TARDANZA') return 'warning'
  if (!props.horaEntradaHoy) return 'schedule'
  if (!props.horaSalidaHoy) return 'play_circle'
  return 'check_circle'
})

const sinTurnoHoy = computed(() => {
  return !props.errorMessage
    && !props.horaEntradaHoy
    && !props.horaEntradaEsperada
    && !props.turnoNombre
})

const estadoColor = computed(() => {
  if (!props.horaEntradaHoy) return 'rgba(255,152,0,0.25)'
  if (!props.horaSalidaHoy) return 'rgba(76,175,80,0.25)'
  return 'rgba(33,150,243,0.25)'
})

const inicioPermitido = computed(() => {
  return props.horaEntradaEsperada?.substring(0, 5) || ''
})

const btnEntradaActivo = computed(() => {
  if (props.horaEntradaHoy) return false
  if (props.errorMessage) return false
  if (sinTurnoHoy.value) return false
  if (!props.horaEntradaEsperada) return false
  const ahora = props.horaActual
  const esperada = props.horaEntradaEsperada
  const salida = props.horaSalidaEsperada
  const [ah, am] = ahora.split(':').map(Number)
  const minutosAhora = ah * 60 + am
  const [eh, em] = esperada.split(':').map(Number)
  const minutosEntrada = eh * 60 + em
  if (salida) {
    const [sh, sm] = salida.split(':').map(Number)
    const minutosSalida = sh * 60 + sm
    if (minutosAhora > minutosSalida) return false
  }
  return minutosAhora >= minutosEntrada
})

const btnSalidaActivo = computed(() => {
  if (!props.horaSalidaEsperada) return false
  const ahora = props.horaActual
  const [ah, am] = ahora.split(':').map(Number)
  const minutosAhora = ah * 60 + am
  const [sh, sm] = props.horaSalidaEsperada.split(':').map(Number)
  const minutosSalida = sh * 60 + sm
  return minutosAhora >= minutosSalida
})
</script>

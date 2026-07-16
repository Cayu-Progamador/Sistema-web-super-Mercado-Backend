<template>
  <q-page class="asistencia-page q-pa-md">
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4 text-bold" style="color:#1B5E20; font-family:'DM Sans',sans-serif">
        Control de Asistencia
      </div>
    </div>

    <div class="row q-col-gutter-md">
      <div class="col-12 col-lg-5">
        <div class="column q-gutter-y-md">
          <ClockCard
            :hora-actual="horaActual"
            :fecha-formateada="fechaFormateada"
            :nombre-empleado="nombreEmpleado"
            :codigo-empleado="codigoEmpleado"
            :cargo="cargoEmpleado"
            :foto-url="fotoUrl"
            :hora-entrada-hoy="horaEntradaHoy"
            :hora-salida-hoy="horaSalidaHoy"
            :estado-hoy="estadoHoy"
            :hora-entrada-esperada="horaEntradaEsperada"
            :hora-salida-esperada="horaSalidaEsperada"
            :tolerancia-minutos="toleranciaMinutos"
            :turno-nombre="turnoNombre"
            :error-message="errorAsistencia"
            @marcar="onMarcar"
          />
          <TodayTimeline
            :hora-entrada="horaEntradaHoy"
            :hora-salida="horaSalidaHoy"
            :hora-entrada-esperada="horaEntradaEsperada"
            :hora-salida-esperada="horaSalidaEsperada"
          />
        </div>
      </div>

      <div class="col-12 col-lg-7">
        <div class="column q-gutter-y-md">
          <MonthlySummary
            :asistencias="resumen.asistencias"
            :tardanzas="resumen.tardanzas"
            :faltas="resumen.faltas"
            :puntualidad="resumen.puntualidad"
          />
          <WeeklyTable
            :rows="semanaRows"
            @descargar="descargarReporte"
          />
        </div>
      </div>
    </div>

    <div class="row q-col-gutter-md q-mt-md">
      <div class="col-6 col-md-3">
        <q-btn
          outline
          no-caps
          color="orange"
          icon="fact_check"
          label="Justificar inasistencia"
          class="full-width"
          @click="mostrarDialogJustificar = true"
        />
      </div>
    </div>

    <JustifyDialog
      v-model="mostrarDialogJustificar"
      tipo="justificar"
      @enviar="onJustificar"
    />
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useQuasar } from 'quasar'
import { useAuthStore } from '../../store/store'
import {
  marcarAsistencia,
  obtenerAsistenciaHoy,
  listarMisAsistencias,
  obtenerMiResumen
} from '../../api/asistencia/asistencia'
import { formatearHoraAMPM } from '../../util/formatearHora'
import ClockCard from '../../components/asistencia/ClockCard.vue'
import TodayTimeline from '../../components/asistencia/TodayTimeline.vue'
import MonthlySummary from '../../components/asistencia/MonthlySummary.vue'
import WeeklyTable from '../../components/asistencia/WeeklyTable.vue'
import JustifyDialog from '../../components/asistencia/JustifyDialog.vue'

const $q = useQuasar()
const store = useAuthStore()

const horaActual = ref('')
const fechaFormateada = ref('')
let intervalId = null

const hoyBackend = ref(null)
const resumen = ref({ asistencias: 0, tardanzas: 0, faltas: 0, puntualidad: 0 })
const errorAsistencia = ref(null)
const semanaData = ref([])

const mostrarDialogJustificar = ref(false)
const mostrarDialogPermiso = ref(false)

const nombreEmpleado = computed(() => store.fullName || 'Empleado')
const codigoEmpleado = computed(() => 'EMP-001')
const cargoEmpleado = computed(() => store.userInfo?.cargo || '')
const fotoUrl = computed(() => store.userInfo?.fotoUrl || '')
const horaEntradaHoy = computed(() => hoyBackend.value?.horaEntrada || null)
const horaSalidaHoy = computed(() => hoyBackend.value?.horaSalida || null)
const estadoHoy = computed(() => hoyBackend.value?.estado || null)
const horaEntradaEsperada = computed(() => hoyBackend.value?.horaEntradaEsperada || null)
const horaSalidaEsperada = computed(() => hoyBackend.value?.horaSalidaEsperada || null)
const toleranciaMinutos = computed(() => hoyBackend.value?.toleranciaMinutos || null)
const turnoNombre = computed(() => hoyBackend.value?.turnoNombre || null)

const semanaRows = computed(() => {
  const diasSemana = ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado']
  return semanaData.value.map(item => {
    const d = new Date(item.fecha + 'T12:00:00')
    const diaSemana = diasSemana[d.getDay()]
    const [y, m, day] = item.fecha.split('-')
    const fechaCorta = `${day}/${m}`
    const entrada = formatearHoraAMPM(item.horaEntrada) || '--'
    const salida = formatearHoraAMPM(item.horaSalida) || '--'
    const horas = item.horaEntrada && item.horaSalida
      ? calcHoras(item.horaEntrada, item.horaSalida)
      : '--'
    const estadoMap = {
      PRESENTE: 'Pendiente salida',
      TARDANZA: 'Tardanza',
      COMPLETO: 'A tiempo',
      FALTA: 'Falta',
      JUSTIFICADO: 'Justificado'
    }
    const estado = estadoMap[item.estado] || (item.horaEntrada && !item.horaSalida ? 'Pendiente salida' : 'Falta')
    return {
      id: item.idAsistencia,
      dia: diaSemana,
      fecha: fechaCorta,
      entrada,
      salida,
      horas,
      estado
    }
  })
})

function calcHoras(ent, sal) {
  const [eh, em] = ent.split(':').map(Number)
  const [sh, sm] = sal.split(':').map(Number)
  const diff = (sh * 60 + sm) - (eh * 60 + em)
  if (diff <= 0) return '0h 0m'
  const h = Math.floor(diff / 60)
  const m = diff % 60
  return `${h}h ${m}m`
}

function actualizarHora() {
  const ahora = new Date()
  horaActual.value = ahora.toLocaleTimeString('es-ES', {     hour: '2-digit', minute: '2-digit' })
  fechaFormateada.value = ahora.toLocaleDateString('es-ES', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  })
}

function obtenerSemanaActual() {
  const hoy = new Date()
  const dia = hoy.getDay()
  const diff = hoy.getDate() - dia + (dia === 0 ? -6 : 1)
  const lunes = new Date(hoy.setDate(diff))
  const domingo = new Date(new Date(lunes).setDate(lunes.getDate() + 6))
  const fmt = d => d.toISOString().split('T')[0]
  return { desde: fmt(lunes), hasta: fmt(domingo) }
}

async function cargarDatos() {
  errorAsistencia.value = null

  try {
    const hoyRes = await obtenerAsistenciaHoy()
    hoyBackend.value = hoyRes || null
  } catch (e) {
    errorAsistencia.value = e.response?.data?.message || e.message || 'Error al cargar datos de asistencia'
    return
  }

  try {
    const semanaRes = await listarMisAsistencias(obtenerSemanaActual())
    semanaData.value = semanaRes || []
  } catch (_) {}

  try {
    const resumenRes = await obtenerMiResumen({
      anio: new Date().getFullYear(),
      mes: new Date().getMonth() + 1
    })
    resumen.value = resumenRes || { asistencias: 0, tardanzas: 0, faltas: 0, puntualidad: 0 }
  } catch (_) {}
}

async function onMarcar(tipo) {
  try {
    const result = await marcarAsistencia({ tipo })
    await cargarDatos()
    const label = tipo === 'ENTRADA' ? 'Entrada registrada' : 'Salida registrada'
    const hora = tipo === 'ENTRADA' ? result.horaEntrada : result.horaSalida
    $q.notify({
      type: 'positive',
      message: `${label} a las ${hora}`,
      position: 'top',
      timeout: 3000
    })
  } catch (e) {
    $q.notify({
      type: 'negative',
      message: e.response?.data?.message || e.message || 'Error al marcar asistencia'
    })
  }
}

function onJustificar(data) {
  $q.notify({ type: 'positive', message: 'Justificación enviada correctamente' })
}

function onSolicitarPermiso(data) {
  $q.notify({ type: 'positive', message: 'Solicitud de permiso enviada correctamente' })
}

function descargarReporte() {
  $q.notify({ type: 'info', message: 'Funcionalidad de descarga en desarrollo' })
}

onMounted(() => {
  actualizarHora()
  intervalId = setInterval(actualizarHora, 1000)
  cargarDatos()
})

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId)
})
</script>

<style scoped src="../../assets/styles/asistencia/asistencia.css"></style>

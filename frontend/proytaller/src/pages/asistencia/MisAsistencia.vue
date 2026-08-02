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
            :permiso-activo="permisoActivo"
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
            :permisos="resumen.permisos"
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
          @click="abrirJustificar"
        />
      </div>
    </div>

    <JustifyDialog
      v-model="mostrarDialogJustificar"
      tipo="justificar"
      :ausencias="misAusencias"
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
  obtenerMiResumen,
  justificarMiAsistencia,
  listarMisAusencias,
  descargarReporteSemanal
} from '../../api/asistencia/asistencia'
import { listarMisSolicitudes } from '../../api/permiso_personal/permiso_personal'
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
let refreshIntervalId = null

const hoyBackend = ref(null)
const resumen = ref({ asistencias: 0, tardanzas: 0, faltas: 0, puntualidad: 0, permisos: 0 })
const errorAsistencia = ref(null)
const semanaData = ref([])

const mostrarDialogJustificar = ref(false)
const mostrarDialogPermiso = ref(false)
const permisoActivo = ref(null)
const misAusencias = ref([])

async function abrirJustificar() {
  try {
    const res = await listarMisAusencias()
    misAusencias.value = res || []
  } catch {
    misAusencias.value = []
  }
  mostrarDialogJustificar.value = true
}

const nombreEmpleado = computed(() => store.fullName || 'Empleado')
const codigoEmpleado = computed(() => {
  if (hoyBackend.value?.idEmpleado) return `EMP-${String(hoyBackend.value.idEmpleado).padStart(3, '0')}`
  const username = store.getUsername
  if (username) return `EMP-${username.toUpperCase()}`
  return 'EMP-???'
})
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
      JUSTIFICADO: 'Justificado',
      PERMISO: 'Permiso'
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
  return { fechaDesde: fmt(lunes), fechaHasta: fmt(domingo) }
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
    resumen.value = resumenRes || { asistencias: 0, tardanzas: 0, faltas: 0, puntualidad: 0, permisos: 0 }
  } catch (_) {}

  await verificarPermisoHoy()
}

async function verificarPermisoHoy() {
  permisoActivo.value = null
  try {
    const solicitudes = await listarMisSolicitudes()
    if (!solicitudes || solicitudes.length === 0) return
    const hoy = new Date()
    const hoyStr = hoy.toISOString().split('T')[0]
    const aprobadoHoy = solicitudes.find(s =>
      s.nombreEstado === 'Aprobado'
      && s.fechaInicio <= hoyStr
      && s.fechaFin >= hoyStr
    )
    if (aprobadoHoy) {
      permisoActivo.value = aprobadoHoy
    }
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

async function onJustificar(data) {
  try {
    await justificarMiAsistencia({
      fecha: data.fecha,
      tipoJustificacion: data.tipoJustificacion,
      motivo: data.motivo
    })
    $q.notify({ type: 'positive', message: 'Justificación enviada correctamente' })
    cargarDatos()
  } catch (e) {
    $q.notify({
      type: 'negative',
      message: e.response?.data?.message || e.message || 'Error al justificar'
    })
  }
}

function onSolicitarPermiso(data) {
  $q.notify({ type: 'positive', message: 'Solicitud de permiso enviada correctamente' })
}

async function descargarReporte() {
  try {
    const semana = obtenerSemanaActual()
    const blob = await descargarReporteSemanal({
      fechaDesde: semana.fechaDesde,
      fechaHasta: semana.fechaHasta
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'reporte-asistencia-semanal.pdf')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: 'Reporte descargado correctamente' })
  } catch (e) {
    $q.notify({
      type: 'negative',
      message: 'Error al descargar el reporte'
    })
  }
}

onMounted(() => {
  actualizarHora()
  intervalId = setInterval(actualizarHora, 1000)
  cargarDatos()
  refreshIntervalId = setInterval(cargarDatos, 30000)
})

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId)
  if (refreshIntervalId) clearInterval(refreshIntervalId)
})
</script>

<style scoped src="../../assets/styles/asistencia/asistencia.css"></style>

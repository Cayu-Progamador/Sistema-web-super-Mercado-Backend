<template>
  <q-page class="asistencia-page q-pa-md">
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4 text-bold" style="color:#1B5E20; font-family:'DM Sans',sans-serif">
        Panel de Asistencia
      </div>
    </div>

    <AttendanceKpis
      :presentes="kpis.presentes"
      :ausentes="kpis.ausentes"
      :tardanzas="kpis.tardanzas"
      :permisos="kpis.permisos"
      :total="kpis.total"
    />

    <AttendanceToolbar
      :fecha-formateada="fechaFormateada"
      :filtros-visibles="showFilters"
      @cambiar-fecha="cambiarFecha"
      @ir-hoy="irHoy"
      @buscar="onBuscar"
      @exportar-pdf="exportarPDF"
      @exportar-excel="exportarExcel"
      @actualizar="cargarTabla"
      @toggle-filtros="showFilters = !showFilters"
    />

    <AttendanceFilters
      :visible="showFilters"
      @aplicar-filtros="aplicarFiltros"
    />

    <AttendanceTable
      :rows="tableRows"
      :loading="tableLoading"
      :pagination="tablePagination"
      @ver-detalle="verDetalle"
      @justificar="mostrarJustificar"
      @request="onTableRequest"
    />

    <AttendanceDetailDrawer
      v-model="showDrawer"
      :empleado="drawerEmpleado"
      :resumen="drawerResumen"
      :dias-calendario="drawerDias"
    />

    <JustifyDialog
      v-model="showJustifyDialog"
      :empleado="justificarEmpleado"
      @enviar="onGuardarJustificacion"
    />
  </q-page>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useQuasar } from 'quasar'
import {
  listarAsistenciasAdmin,
  obtenerResumenHoyAdmin,
  obtenerDetalleEmpleadoAdmin,
  justificarAsistenciaAdmin
} from '../../api/asistencia/asistencia'
import AttendanceKpis from '../../components/asistencia/AttendanceKpis.vue'
import AttendanceToolbar from '../../components/asistencia/AttendanceToolbar.vue'
import AttendanceFilters from '../../components/asistencia/AttendanceFilters.vue'
import AttendanceTable from '../../components/asistencia/AttendanceTable.vue'
import AttendanceDetailDrawer from '../../components/asistencia/AttendanceDetailDrawer.vue'
import JustifyDialog from '../../components/asistencia/JustifyDialog.vue'

const $q = useQuasar()

const selectedDate = ref(new Date())
const showFilters = ref(false)
const showDrawer = ref(false)
const showJustifyDialog = ref(false)
const tableLoading = ref(false)
const tableRows = ref([])
const busqueda = ref('')
const filtrosActivos = ref({})

const kpis = reactive({ presentes: 0, ausentes: 0, tardanzas: 0, permisos: 0, total: 0 })

const tablePagination = ref({
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0,
  sortBy: 'fecha',
  descending: true
})

const drawerEmpleado = ref(null)
const drawerResumen = reactive({ presentes: 0, tardanzas: 0, faltas: 0, justificados: 0 })
const drawerDias = ref([])
const justificarIdAsistencia = ref(null)
const justificarEmpleado = ref(null)

const fechaFormateada = computed(() => {
  return selectedDate.value.toLocaleDateString('es-ES', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  }).replace(/^\w/, c => c.toUpperCase())
})

function fmtDate(d) {
  return d.toISOString().split('T')[0]
}

async function cargarKpis() {
  try {
    const data = await obtenerResumenHoyAdmin()
    Object.assign(kpis, data)
  } catch {
    kpis.presentes = 0
    kpis.ausentes = 0
    kpis.tardanzas = 0
    kpis.permisos = 0
    kpis.total = 0
  }
}

async function cargarTabla() {
  tableLoading.value = true
  try {
    const { page, rowsPerPage, sortBy, descending } = tablePagination.value
    const params = {
      page: page - 1,
      size: rowsPerPage,
      sortBy,
      sortDir: descending ? 'desc' : 'asc',
      fechaDesde: fmtDate(selectedDate.value),
      fechaHasta: fmtDate(selectedDate.value)
    }
    if (busqueda.value?.trim()) {
      params.busqueda = busqueda.value.trim()
    }
    if (filtrosActivos.value.estado) {
      params.estado = filtrosActivos.value.estado
    }
    if (filtrosActivos.value.turno) {
      params.idTurno = filtrosActivos.value.turno
    }
    const res = await listarAsistenciasAdmin(params)
    tableRows.value = res.content || []
    tablePagination.value = {
      page: (res.number || 0) + 1,
      rowsPerPage: res.size || 10,
      rowsNumber: res.totalElements || 0,
      sortBy: sortBy,
      descending: descending
    }
  } catch {
    tableRows.value = []
    tablePagination.value.rowsNumber = 0
  } finally {
    tableLoading.value = false
  }
}

function cambiarFecha(delta) {
  const d = new Date(selectedDate.value)
  d.setDate(d.getDate() + delta)
  selectedDate.value = d
  cargarTabla()
}

function irHoy() {
  selectedDate.value = new Date()
  cargarTabla()
}

function onBuscar(val) {
  busqueda.value = val
  tablePagination.value.page = 1
  cargarTabla()
}

function aplicarFiltros(filtros) {
  filtrosActivos.value = filtros
  tablePagination.value.page = 1
  cargarTabla()
}

function onTableRequest(requestProps) {
  const { pagination } = requestProps
  tablePagination.value.sortBy = pagination.sortBy
  tablePagination.value.descending = pagination.descending
  tablePagination.value.page = pagination.page
  tablePagination.value.rowsPerPage = pagination.rowsPerPage
  cargarTabla()
}

async function verDetalle(row) {
  const anio = selectedDate.value.getFullYear()
  const mes = selectedDate.value.getMonth() + 1
  try {
    const data = await obtenerDetalleEmpleadoAdmin(row.idEmpleado, { anio, mes })
    drawerEmpleado.value = { ...row, ...data }
    drawerResumen.presentes = data.presentes || 0
    drawerResumen.tardanzas = data.tardanzas || 0
    drawerResumen.faltas = data.faltas || 0
    drawerResumen.justificados = data.justificados || 0
    drawerDias.value = generarCalendario(mes, anio, data.dias || [])
  } catch {
    drawerEmpleado.value = { ...row }
    drawerResumen.presentes = 0
    drawerResumen.tardanzas = 0
    drawerResumen.faltas = 0
    drawerResumen.justificados = 0
    drawerDias.value = generarCalendario(mes, anio, [])
  }
  showDrawer.value = true
}

function mostrarJustificar(row) {
  justificarIdAsistencia.value = row.idAsistencia
  justificarEmpleado.value = { nombreEmpleado: row.nombreEmpleado, cargo: row.cargo }
  showJustifyDialog.value = true
}

async function onGuardarJustificacion(data) {
  try {
    await justificarAsistenciaAdmin(justificarIdAsistencia.value, data)
    $q.notify({ type: 'positive', message: 'Justificaci&oacute;n guardada correctamente' })
    cargarTabla()
  } catch (e) {
    const msg = e.response?.data?.message || 'Error al guardar justificaci&oacute;n'
    $q.notify({ type: 'negative', message: msg })
  }
}

function exportarPDF() {
  $q.notify({ type: 'info', message: 'Exportando PDF...' })
}

function exportarExcel() {
  $q.notify({ type: 'info', message: 'Exportando Excel...' })
}

function generarCalendario(mes, anio, diasData) {
  const dias = []
  const totalDias = new Date(anio, mes, 0).getDate()
  const hoy = new Date()
  const mapa = {}
  diasData.forEach(d => { mapa[d.fecha] = d.estado })

  for (let i = 1; i <= totalDias; i++) {
    const fechaStr = `${anio}-${String(mes).padStart(2, '0')}-${String(i).padStart(2, '0')}`
    const esHoy = anio === hoy.getFullYear() && mes === hoy.getMonth() + 1 && i === hoy.getDate()
    const fecha = new Date(anio, mes - 1, i)
    const esFuturo = fecha > hoy
    dias.push({
      numero: i,
      fecha: fechaStr,
      estado: esFuturo ? 'FUTURO' : (mapa[fechaStr] || 'AUSENTE'),
      esHoy
    })
  }
  return dias
}

let kpiIntervalId = null

onMounted(() => {
  cargarKpis()
  cargarTabla()
  kpiIntervalId = setInterval(cargarKpis, 30000)
})

onUnmounted(() => {
  if (kpiIntervalId) clearInterval(kpiIntervalId)
})
</script>

<style scoped src="../../assets/styles/asistencia/asistencia.css"></style>

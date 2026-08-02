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
      :justificados="kpis.justificados"
      :permisos="kpis.permisos"
      :total="kpis.total"
      @click-kpi="onClickKpi"
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
      @ver-ausentes="abrirAusentes"
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
      @editar="mostrarEditar"
      @descargar="descargarPDF"
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
      :fecha-fija="justificarFecha"
      @enviar="onGuardarJustificacion"
    />

    <EditAttendanceDialog
      v-model="showEditDialog"
      :empleado="editRow"
      @guardar="onGuardarEdicion"
    />

    <AusentesDrawer
      v-model="showAusentesDrawer"
      :empleados="ausentesLista"
      :mes="ausentesMes"
      :anio="ausentesAnio"
      :modo="ausentesModo"
      @cambiar-mes="onCambiarMesAusentes"
      @ir-hoy="abrirAusentes"
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
  justificarAsistenciaAdmin,
  justificarAusenteAdmin,
  editarAsistenciaAdmin,
  crearAsistenciaAdmin,
  descargarPdfAsistenciaAdmin,
  exportarPDFAdmin,
  exportarExcelAdmin,
  listarAusentesAdmin,
  listarAusentesDelDiaAdmin,
  listarAusentesDetalleAdmin
} from '../../api/asistencia/asistencia'
import AttendanceKpis from '../../components/asistencia/AttendanceKpis.vue'
import AttendanceToolbar from '../../components/asistencia/AttendanceToolbar.vue'
import AttendanceFilters from '../../components/asistencia/AttendanceFilters.vue'
import AttendanceTable from '../../components/asistencia/AttendanceTable.vue'
import AttendanceDetailDrawer from '../../components/asistencia/AttendanceDetailDrawer.vue'
import JustifyDialog from '../../components/asistencia/JustifyDialog.vue'
import EditAttendanceDialog from '../../components/asistencia/EditAttendanceDialog.vue'
import AusentesDrawer from '../../components/asistencia/AusentesDrawer.vue'

const $q = useQuasar()

function formatearFecha(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const selectedDate = ref(new Date())
const showFilters = ref(false)
const showDrawer = ref(false)
const showJustifyDialog = ref(false)
const showAusentesDrawer = ref(false)
const ausentesLista = ref([])
const ausentesMes = ref(new Date().getMonth() + 1)
const ausentesAnio = ref(new Date().getFullYear())
const ausentesModo = ref('dia')
const tableLoading = ref(false)
const tableRows = ref([])
const busqueda = ref('')
const filtrosActivos = ref({})

const kpis = reactive({ presentes: 0, ausentes: 0, tardanzas: 0, justificados: 0, permisos: 0, total: 0 })

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
const justificarIdContrato = ref(null)
const justificarEmpleado = ref(null)
const justificarFecha = ref('')
const showEditDialog = ref(false)
const editRow = ref(null)

const fechaFormateada = computed(() => {
  return selectedDate.value.toLocaleDateString('es-ES', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  }).replace(/^\w/, c => c.toUpperCase())
})

function fmtDate(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

async function cargarKpis() {
  try {
    const data = await obtenerResumenHoyAdmin()
    Object.assign(kpis, data)
  } catch {
    kpis.presentes = 0
    kpis.ausentes = 0
    kpis.tardanzas = 0
    kpis.justificados = 0
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
    $q.notify({ type: 'negative', message: 'Error al cargar datos' })
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
    $q.notify({ type: 'negative', message: 'Error al cargar detalle del empleado' })
  }
  showDrawer.value = true
}

function onClickKpi(key) {
  if (key === 'ausentes') abrirAusentes()
}

function abrirAusentes() {
  ausentesModo.value = 'dia'
  cargarAusentes()
  showAusentesDrawer.value = true
}

function onCambiarMesAusentes(delta, mes, anio) {
  ausentesModo.value = 'mes'
  if (delta !== 0) {
    ausentesMes.value += delta
    if (ausentesMes.value > 12) { ausentesMes.value = 1; ausentesAnio.value++ }
    if (ausentesMes.value < 1) { ausentesMes.value = 12; ausentesAnio.value-- }
  } else {
    ausentesMes.value = mes
    ausentesAnio.value = anio
  }
  cargarAusentes()
}

async function cargarAusentes() {
  try {
    const res = ausentesModo.value === 'dia'
      ? await listarAusentesDelDiaAdmin(formatearFecha(selectedDate.value))
      : await listarAusentesDetalleAdmin(ausentesMes.value, ausentesAnio.value)
    ausentesLista.value = res
  } catch {
    ausentesLista.value = []
    $q.notify({ type: 'negative', message: 'Error al cargar ausentes' })
  }
}

function mostrarJustificar(row) {
  if (row.estado === 'JUSTIFICADO') {
    $q.notify({ type: 'warning', message: 'Esta asistencia ya está justificada' })
    return
  }
  if (row.estado === 'PERMISO') {
    $q.notify({ type: 'warning', message: 'No puedes justificar un día con permiso aprobado' })
    return
  }
  if (row.horaEntrada) {
    $q.notify({ type: 'warning', message: 'No puedes justificar un registro que tiene entrada marcada' })
    return
  }
  justificarIdAsistencia.value = row.idAsistencia || null
  justificarIdContrato.value = row.idEmpleado
  justificarEmpleado.value = { nombreEmpleado: row.nombreEmpleado, cargo: row.cargo }
  justificarFecha.value = row.fecha || ''
  showJustifyDialog.value = true
}

async function onGuardarJustificacion(data) {
  try {
    if (justificarIdAsistencia.value) {
      await justificarAsistenciaAdmin(justificarIdAsistencia.value, data)
    } else {
      await justificarAusenteAdmin(justificarIdContrato.value, {
        fecha: justificarFecha.value,
        tipoJustificacion: data.tipoJustificacion,
        motivo: data.motivo
      })
    }
    $q.notify({ type: 'positive', message: 'Justificaci&oacute;n guardada correctamente' })
    cargarTabla()
  } catch (e) {
    const msg = e.response?.data?.message || 'Error al guardar justificaci&oacute;n'
    $q.notify({ type: 'negative', message: msg })
  }
}

function descargarArchivo(res, nombre) {
  const url = window.URL.createObjectURL(new Blob([res]))
  const link = document.createElement('a')
  link.href = url
  link.setAttribute('download', nombre)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

function exportarPDF() {
  const params = {
    fechaDesde: fmtDate(selectedDate.value),
    fechaHasta: fmtDate(selectedDate.value),
    sortBy: tablePagination.value.sortBy || 'fecha',
    sortDir: tablePagination.value.descending ? 'desc' : 'asc'
  }
  if (busqueda.value?.trim()) params.busqueda = busqueda.value.trim()
  if (filtrosActivos.value.estado) params.estado = filtrosActivos.value.estado
  if (filtrosActivos.value.turno) params.idTurno = filtrosActivos.value.turno
  exportarPDFAdmin(params).then(res => descargarArchivo(res, 'asistencia.pdf'))
    .catch(() => $q.notify({ type: 'negative', message: 'Error al exportar PDF' }))
}

function exportarExcel() {
  const params = {
    fechaDesde: fmtDate(selectedDate.value),
    fechaHasta: fmtDate(selectedDate.value),
    sortBy: tablePagination.value.sortBy || 'fecha',
    sortDir: tablePagination.value.descending ? 'desc' : 'asc'
  }
  if (busqueda.value?.trim()) params.busqueda = busqueda.value.trim()
  if (filtrosActivos.value.estado) params.estado = filtrosActivos.value.estado
  if (filtrosActivos.value.turno) params.idTurno = filtrosActivos.value.turno
  exportarExcelAdmin(params).then(res => descargarArchivo(res, 'asistencia.xlsx'))
    .catch(() => $q.notify({ type: 'negative', message: 'Error al exportar Excel' }))
}

function generarCalendario(mes, anio, diasData) {
  const dias = []
  const totalDias = new Date(anio, mes, 0).getDate()
  const hoy = new Date()
  const mapa = {}
  diasData.forEach(d => { mapa[d.fecha] = d.estado })

  const primerDia = new Date(anio, mes - 1, 1).getDay()
  const offset = (primerDia + 6) % 7
  for (let i = 0; i < offset; i++) {
    dias.push({ numero: '', fecha: '', estado: '', esHoy: false })
  }

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

function mostrarEditar(row) {
  editRow.value = row
  showEditDialog.value = true
}

async function onGuardarEdicion(data) {
  try {
    if (data.idAsistencia) {
      await editarAsistenciaAdmin(data.idAsistencia, {
        horaEntrada: data.horaEntrada,
        horaSalida: data.horaSalida,
        estado: data.estado
      })
      $q.notify({ type: 'positive', message: 'Registro actualizado correctamente' })
    } else {
      await crearAsistenciaAdmin({
        idContrato: editRow.value?.idEmpleado,
        fecha: editRow.value?.fecha,
        horaEntrada: data.horaEntrada,
        horaSalida: data.horaSalida,
        estado: data.estado
      })
      $q.notify({ type: 'positive', message: 'Asistencia registrada correctamente' })
    }
    showEditDialog.value = false
    cargarTabla()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al guardar registro' })
  }
}

async function descargarPDF(row) {
  try {
    const blob = await descargarPdfAsistenciaAdmin(row.idAsistencia)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `asistencia-${row.idAsistencia}.pdf`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: 'PDF descargado correctamente' })
  } catch {
    $q.notify({ type: 'negative', message: 'Error al descargar PDF' })
  }
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

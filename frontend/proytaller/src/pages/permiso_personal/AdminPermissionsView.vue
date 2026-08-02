<template>
  <div class="admin-permissions">
    <div class="page-header">
      <div>
        <div class="page-title">Gestión de Permisos</div>
        <div class="page-subtitle">Panel de administración — RRHH</div>
      </div>
      
    </div>

    <PermissionsAdminKpis
      :pendientes="kpiPendientes"
      :aprobadas-mes="kpiAprobadasMes"
      :rechazadas="kpiRechazadas"
      :con-permiso-hoy="kpiConPermisoHoy"
    />

    <div class="main-tabs">
      <q-tabs
        v-model="tabActivo"
        class="custom-tabs"
        active-color="green-8"
        indicator-color="green-8"
        align="left"
      >
        <q-tab name="bandeja" label="Bandeja de solicitudes" icon="inbox" />
        <q-tab name="calendario" label="Calendario del equipo" icon="calendar_month" />
        <q-tab name="historico" label="Histórico" icon="history" />
      </q-tabs>

      <q-separator />

      <q-tab-panels v-model="tabActivo" animated class="tab-panels">
        <q-tab-panel name="bandeja" class="tab-panel">
          <PendingRequestsTable
            :solicitudes="pendientes"
            :tipos-permiso="tiposPermiso"
            @aprobar="abrirAprobar"
            @rechazar="abrirRechazar"
            @ver-detalle="abrirDetalle"
            @exportar-pdf="exportarPDF"
            @actualizar="cargarDatos"
          />
        </q-tab-panel>

        <q-tab-panel name="calendario" class="tab-panel">
          <TeamCalendarView :solicitudes="todas" />
        </q-tab-panel>

        <q-tab-panel name="historico" class="tab-panel">
          <PermissionsHistoryTable
            :solicitudes="historico"
            @ver-detalle="abrirDetalle"
            @exportar-excel="exportarExcel"
          />
        </q-tab-panel>
      </q-tab-panels>
    </div>

    <RejectRequestDialog
      v-model="rejectDialogVisible"
      :solicitud="solicitudSeleccionada"
      @confirmar="confirmarRechazo"
    />

    <ConfirmarAprobacionDialog
      v-model="approveDialogVisible"
      :solicitud="solicitudSeleccionada"
      @confirmar="confirmarAprobacion"
    />

    <RequestDetailDrawerAdmin
      v-model="detailDrawerVisible"
      :solicitud="solicitudSeleccionada"
      :historial="historialActual"
      @aprobar="abrirAprobar"
      @rechazar="abrirRechazar"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import PermissionsAdminKpis from '../../components/permiso_admin/PermissionsAdminKpis.vue'
import PendingRequestsTable from '../../components/permiso_admin/PendingRequestsTable.vue'
import TeamCalendarView from '../../components/permiso_admin/TeamCalendarView.vue'
import PermissionsHistoryTable from '../../components/permiso_admin/PermissionsHistoryTable.vue'
import RejectRequestDialog from '../../components/permiso_admin/RejectRequestDialog.vue'
import ConfirmarAprobacionDialog from '../../components/permiso_admin/ConfirmarAprobacionDialog.vue'
import RequestDetailDrawerAdmin from '../../components/permiso_admin/RequestDetailDrawerAdmin.vue'
import {
  listarSolicitudesPorEstado,
  listarTiposPermiso,
  aprobarSolicitud,
  rechazarSolicitud,
  obtenerHistorial,
  exportarPermisosPDF,
  exportarPermisosExcel,
} from '../../api/permiso_personal/permiso_personal'

const $q = useQuasar()

const tabActivo = ref('bandeja')

const pendientes = ref([])
const aprobadas = ref([])
const rechazadas = ref([])
const expiradas = ref([])
const canceladas = ref([])
const todas = ref([])
const tiposPermiso = ref([])
const solicitudSeleccionada = ref(null)
const historialActual = ref([])
const rejectDialogVisible = ref(false)
const approveDialogVisible = ref(false)
const detailDrawerVisible = ref(false)
const loading = ref(false)

const historico = computed(() =>
  todas.value.filter(s =>
    ['Aprobado', 'Rechazado', 'Cancelado', 'Expirado'].includes(s.nombreEstado)
  )
)

const kpiPendientes = computed(() => pendientes.value.length)
const kpiAprobadasMes = computed(() => {
  const now = new Date()
  const mes = now.getMonth()
  const anio = now.getFullYear()
  return aprobadas.value.filter(s => {
    const f = new Date(s.fechaInicio)
    return f.getMonth() === mes && f.getFullYear() === anio
  }).length
})
const kpiRechazadas = computed(() => rechazadas.value.length)
const kpiConPermisoHoy = computed(() => {
  const hoy = new Date().toISOString().split('T')[0]
  return aprobadas.value.filter(s =>
    s.fechaInicio <= hoy &&
    (s.fechaFin || s.fechaInicio) >= hoy
  ).length
})

onMounted(() => {
  cargarDatos()
  listarTiposPermiso().then(res => {
    if (res && res.length) tiposPermiso.value = res
  }).catch(() => {})
})

async function cargarDatos() {
  loading.value = true
  try {
    const [resPendientes, resAprobadas, resRechazadas, resExpiradas, resCanceladas] = await Promise.all([
      listarSolicitudesPorEstado('Pendiente'),
      listarSolicitudesPorEstado('Aprobado'),
      listarSolicitudesPorEstado('Rechazado'),
      listarSolicitudesPorEstado('Expirado'),
      listarSolicitudesPorEstado('Cancelado'),
    ])
    pendientes.value = resPendientes || []
    aprobadas.value = resAprobadas || []
    rechazadas.value = resRechazadas || []
    expiradas.value = resExpiradas || []
    canceladas.value = resCanceladas || []
    todas.value = [...pendientes.value, ...aprobadas.value, ...rechazadas.value, ...expiradas.value, ...canceladas.value]
    console.log('📦 Backend response - pendientes:', resPendientes)
  } catch (e) {
    pendientes.value = []
    aprobadas.value = []
    rechazadas.value = []
    expiradas.value = []
    canceladas.value = []
    todas.value = []
  }
  const allData = todas.value
  if (!tiposPermiso.value.length && allData.length) {
    const map = new Map()
    allData.forEach(s => {
      if (s.idTipo && !map.has(s.idTipo)) map.set(s.idTipo, { id: s.idTipo, nombre: s.nombreTipo })
    })
    tiposPermiso.value = Array.from(map.values())
  }
  loading.value = false
}

function abrirDetalle(solicitud) {
  solicitudSeleccionada.value = solicitud
  historialActual.value = []
  detailDrawerVisible.value = true
  obtenerHistorial(solicitud.id).then(res => {
    historialActual.value = res || []
  }).catch(() => {})
}

function abrirRechazar(solicitud) {
  solicitudSeleccionada.value = solicitud
  rejectDialogVisible.value = true
}

async function confirmarRechazo(motivo) {
  if (!solicitudSeleccionada.value) return
  try {
    await rechazarSolicitud(solicitudSeleccionada.value.id, { comentario: motivo })
    $q.notify({ type: 'positive', message: 'Solicitud rechazada', position: 'top' })
    rejectDialogVisible.value = false
    detailDrawerVisible.value = false
    await cargarDatos()
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error al rechazar', position: 'top' })
  }
}

function abrirAprobar(solicitud) {
  solicitudSeleccionada.value = solicitud
  approveDialogVisible.value = true
}

function confirmarAprobacion(finishCallback) {
  if (!solicitudSeleccionada.value) return
  aprobarSolicitud(solicitudSeleccionada.value.id, { comentario: '' })
    .then(() => {
      $q.notify({ type: 'positive', message: 'Solicitud aprobada', position: 'top' })
      detailDrawerVisible.value = false
      approveDialogVisible.value = false
      if (finishCallback) finishCallback()
      cargarDatos()
    })
    .catch(() => {
      $q.notify({ type: 'negative', message: 'Error al aprobar', position: 'top' })
      if (finishCallback) finishCallback()
    })
}

async function exportarPDF() {
  try {
    const blob = await exportarPermisosPDF('Pendiente')
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `solicitudes_permiso_${new Date().toISOString().split('T')[0]}.pdf`
    link.click()
    window.URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: 'PDF exportado correctamente', position: 'top' })
  } catch {
    $q.notify({ type: 'negative', message: 'Error al exportar PDF', position: 'top' })
  }
}

async function exportarExcel() {
  try {
    const blob = await exportarPermisosExcel()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `solicitudes_permiso_${new Date().toISOString().split('T')[0]}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: 'Excel exportado correctamente', position: 'top' })
  } catch {
    $q.notify({ type: 'negative', message: 'Error al exportar Excel', position: 'top' })
  }
}
</script>

<style scoped>
.admin-permissions {
  padding: 20px 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: #F5F7FA;
  min-height: 100%;
  overflow-x: hidden;
}
@media (max-width: 599px) {
  .admin-permissions {
    padding: 14px 12px 20px;
  }
  .main-tabs {
    border-radius: 10px !important;
  }
  .tab-panel {
    padding: 10px;
  }
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.page-title {
  font-size: 24px;
  font-weight: 800;
  color: #1B5E20;
}
.page-subtitle {
  font-size: 13px;
  color: #718096;
  margin-top: 2px;
}
.page-btn {
  border-radius: 10px !important;
  font-weight: 700 !important;
  padding: 8px 20px !important;
}
.main-tabs {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  overflow: hidden;
}
.custom-tabs {
  padding: 0 16px;
  background: #fff;
}
.custom-tabs :deep(.q-tab) {
  font-weight: 600;
  font-size: 13px;
  text-transform: none;
  min-height: 44px;
}
.tab-panels {
  background: transparent;
}
.tab-panel {
  padding: 16px;
}
</style>

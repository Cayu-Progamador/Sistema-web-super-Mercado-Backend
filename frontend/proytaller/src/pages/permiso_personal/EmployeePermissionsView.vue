<template>
  <q-page class="permiso-page q-pa-lg">
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="text-h4 text-bold" style="color:#1B5E20">
          Gestión de Permisos
        </div>
        <div class="text-subtitle2 text-grey-6 q-mt-xs">
          Solicita y da seguimiento a tus permisos, vacaciones y licencias
        </div>
      </div>
    </div>

    <div class="row q-col-gutter-lg">
      <div class="col-12 col-md-4 col-lg-4">
        <div class="column q-gutter-y-md">
          <ResumenPeriodoCard
            :total-solicitudes="resumenPeriodo.total"
            :dias-solicitados="resumenPeriodo.diasSolicitados"
            :pendientes="resumenPeriodo.pendientes"
            :aprobadas="resumenPeriodo.aprobadas"
            :rechazadas="resumenPeriodo.rechazadas"
          />

          <PermissionTypesCard :items="tiposPermiso" />

          <q-banner v-if="restriction" class="restriction-banner" rounded>
            <template v-slot:avatar>
              <q-icon name="block" color="orange-8" />
            </template>
            {{ restriction }}
          </q-banner>

          <q-btn
            unelevated
            no-caps
            color="green-8"
            icon="add"
            label="Nueva solicitud de permiso"
            class="full-width new-request-btn"
            :disable="!!restriction"
            @click="mostrarNuevo = true"
          />
        </div>
      </div>

      <div class="col-12 col-md-8 col-lg-8">
        <MyRequestsList
          :solicitudes="solicitudes"
          :loading="cargandoSolicitudes"
          @ver-detalle="abrirDetalle"
          @cancelar="onCancelarSolicitud"
        />
      </div>
    </div>

    <NewRequestDialog
      v-model="mostrarNuevo"
      :tipos-permiso="tiposPermiso"
      @enviar="onEnviarSolicitud"
    />

    <RequestDetailDrawer
      v-model="mostrarDetalle"
      :solicitud="solicitudSeleccionada"
    />

    <CancelarPermisoDialog
      v-model="mostrarCancelar"
      :solicitud="solicitudCancelar"
      @cancelado="onCancelado"
    />
  </q-page>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useAuthStore } from '../../store/store'
import {
  listarTiposPermiso,
  crearSolicitud,
  listarMisSolicitudes,
  cancelarSolicitud
} from '../../api/permiso_personal/permiso_personal'
import ResumenPeriodoCard from '../../components/permiso_empleado/ResumenPeriodoCard.vue'
import PermissionTypesCard from '../../components/permiso_empleado/PermissionTypesCard.vue'
import MyRequestsList from '../../components/permiso_empleado/MyRequestsList.vue'
import NewRequestDialog from '../../components/permiso_empleado/NewRequestDialog.vue'
import RequestDetailDrawer from '../../components/permiso_empleado/RequestDetailDrawer.vue'
import CancelarPermisoDialog from '../../components/permiso_empleado/CancelarPermisoDialog.vue'

const $q = useQuasar()
const store = useAuthStore()

const tiposPermiso = ref([])
const solicitudes = ref([])
const cargandoSolicitudes = ref(false)
const mostrarNuevo = ref(false)
const mostrarDetalle = ref(false)
const solicitudSeleccionada = ref(null)
const mostrarCancelar = ref(false)
const solicitudCancelar = ref(null)

const restriction = computed(() => {
  if (!store.controlaAsistencia) return 'No tienes un contrato activo con control de asistencia. Contacta a RRHH.'

  const pendiente = solicitudes.value.some(s => s.nombreEstado === 'Pendiente')
  if (pendiente) return 'Tienes una solicitud pendiente. Espera que sea revisada antes de crear otra.'

  const hoy = new Date().toISOString().split('T')[0]
  const vigente = solicitudes.value.some(s =>
    s.nombreEstado === 'Aprobado' && s.fechaFin && s.fechaFin >= hoy
  )
  if (vigente) return 'Tienes un permiso aprobado vigente. Debes esperar a que termine.'

  const mesActual = new Date().getMonth()
  const anioActual = new Date().getFullYear()
  const aprobadasMes = solicitudes.value.filter(s => {
    if (s.nombreEstado !== 'Aprobado' || !s.createdAt) return false
    const fecha = new Date(s.createdAt[0], s.createdAt[1] - 1)
    return fecha.getMonth() === mesActual && fecha.getFullYear() === anioActual
  }).length
  if (aprobadasMes >= 2) return 'Has alcanzado el límite de 2 solicitudes aprobadas este mes.'

  return null
})

const resumenPeriodo = computed(() => {
  const total = solicitudes.value.length
  const pendientes = solicitudes.value.filter(s => s.nombreEstado === 'Pendiente').length
  const aprobadas = solicitudes.value.filter(s => s.nombreEstado === 'Aprobado').length
  const rechazadas = solicitudes.value.filter(s => s.nombreEstado === 'Rechazado').length
  const diasSolicitados = solicitudes.value.reduce((acc, s) => {
    if (!s.fechaInicio) return acc
    const start = new Date(s.fechaInicio + 'T12:00:00')
    if (!s.fechaFin) return acc + 1
    const end = new Date(s.fechaFin + 'T12:00:00')
    const diff = Math.round((end - start) / (1000 * 60 * 60 * 24)) + 1
    return acc + (diff > 0 ? diff : 1)
  }, 0)
  return { total, diasSolicitados, pendientes, aprobadas, rechazadas }
})

async function cargarTipos() {
  try {
    tiposPermiso.value = await listarTiposPermiso()
  } catch {
    tiposPermiso.value = []
  }
}

async function cargarSolicitudes() {
  cargandoSolicitudes.value = true
  try {
    solicitudes.value = await listarMisSolicitudes()
  } catch {
    solicitudes.value = []
  } finally {
    cargandoSolicitudes.value = false
  }
}

async function onEnviarSolicitud(data) {
  try {
    const payload = {
      idTipo: data.idTipo,
      fechaInicio: data.fechaInicio,
      fechaFin: data.fechaFin,
      motivo: data.motivo
    }
    await crearSolicitud(payload)
    $q.notify({
      type: 'positive',
      message: 'Solicitud enviada correctamente',
      position: 'top',
      timeout: 3000
    })
    await cargarSolicitudes()
  } catch (e) {
    $q.notify({
      type: 'negative',
      message: e.response?.data?.message || e.message || 'Error al enviar solicitud'
    })
  }
}

function onCancelarSolicitud(item) {
  solicitudCancelar.value = item
  mostrarCancelar.value = true
}

function onCancelado() {
  cargarSolicitudes()
}

function abrirDetalle(item) {
  solicitudSeleccionada.value = item
  mostrarDetalle.value = true
}

onMounted(() => {
  cargarTipos()
  cargarSolicitudes()
})
</script>

<style scoped src="../../assets/styles/permiso_personal/permiso_personal.css"></style>

<style scoped>
.new-request-btn {
  border-radius: 12px !important;
  padding: 14px 0 !important;
  font-weight: 700 !important;
  font-size: 15px !important;
  box-shadow: 0 4px 16px rgba(46, 125, 50, 0.25) !important;
  transition: all 0.2s !important;
}
.new-request-btn:hover {
  box-shadow: 0 6px 24px rgba(46, 125, 50, 0.35) !important;
  transform: translateY(-1px);
}
.restriction-banner {
  background: #FFF3E0 !important;
  color: #E65100 !important;
  font-size: 12px !important;
  border-radius: 10px !important;
}
</style>

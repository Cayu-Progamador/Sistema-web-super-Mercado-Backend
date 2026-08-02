<template>
  <q-page class="perfil-page q-pa-md">

    <!-- HEADER -->
    <div class="perfil-header q-mb-md">
      <div class="row items-center no-wrap q-gutter-md">
        <div class="avatar-wrap">
          <div class="avatar-circle">
            <img v-if="perfil.fotoUrl" :src="perfil.fotoUrl" class="avatar-img" />
            <span v-else>{{ iniciales }}</span>
          </div>
          <div class="avatar-edit">
            <q-icon name="photo_camera" size="10px" color="white" />
          </div>
        </div>
        <div>
          <div class="ph-title">Gestión de Perfil</div>
          <div class="ph-sub">Administra tu información personal y configuración de cuenta</div>
        </div>
      </div>

      <!-- Cambia el componente CambiarFotoPerfil -->
      <CambiarFotoPerfil v-model="mostrarFoto" :nombre-usuario="perfil.nombres + ' ' + perfil.apellidos"
        :foto-url="perfil.fotoUrl" @foto-actualizada="onFotoActualizada" />
    </div>

    <!-- TABS -->
    <div class="perfil-tabs q-mb-md">
      <div v-for="tab in tabs" :key="tab.key" class="perfil-tab" :class="{ active: tabActiva === tab.key }"
        @click="tabActiva = tab.key">
        {{ tab.label }}
      </div>
    </div>

      <!-- contenido segun tab activa-->
      <CambiarPassword v-if="tabActiva === 'password'" />

      <!-- CONTENIDO -->
      <div class="perfil-grid" v-if="tabActiva === 'info'">

        <!-- COLUMNA IZQUIERDA -->
        <div class="col-left">

          <!-- INFORMACIÓN PERSONAL -->
          <q-card class="perfil-card q-mb-md" flat>
            <div class="card-title-row">
              <q-icon name="account_circle" class="card-title-icon" />
              Información Personal
            </div>

            <!-- Avatar + nombre -->
            <div class="row items-center no-wrap q-gutter-md q-mb-lg">
              <div class="avatar-lg-wrap" @click="mostrarFoto = true" style="cursor: pointer">
                <div class="avatar-lg">
                  <img v-if="perfil.fotoUrl" :src="perfil.fotoUrl" class="avatar-lg-img" />
                  <span v-else>{{ iniciales }}</span>
                </div>
                <div class="avatar-lg-edit">
                  <q-icon name="photo_camera" size="12px" color="white" />
                </div>
              </div>
              <div>
                <div class="profile-name">{{ perfil.nombres }} {{ perfil.apellidos }}</div>
                <div class="profile-badge" v-if="perfil.rol">
                  <q-icon name="verified_user" size="12px" />
                  {{ perfil.rol }}
                </div>
              </div>
            </div>

            <!-- Campos -->
            <div class="fields-grid">
              <div class="field-group">
                <label class="field-lbl">Nombres</label>
                <q-input v-model="perfil.nombres" outlined dense class="field-input" bg-color="white" readonly />
              </div>
              <div class="field-group">
                <label class="field-lbl">Apellidos</label>
                <q-input v-model="perfil.apellidos" outlined dense class="field-input" bg-color="white" readonly />
              </div>
              <div class="field-group">
                <label class="field-lbl">CI</label>
                <q-input v-model="perfil.ci" outlined dense class="field-input" bg-color="white" readonly>
                  <template #prepend>
                    <q-icon name="badge" class="input-icon" />
                  </template>
                </q-input>
              </div>
              <div class="field-group">
                <label class="field-lbl">Sexo</label>
                <q-input v-model="perfil.sexo" outlined dense class="field-input" bg-color="white" readonly>
                  <template #prepend>
                    <q-icon name="wc" class="input-icon" />
                  </template>
                </q-input>
              </div>
              <div class="field-group">
                <label class="field-lbl">Correo Electrónico</label>
                <q-input v-model="perfil.correo" outlined dense class="field-input" bg-color="white">
                  <template #prepend>
                    <q-icon name="mail_outline" class="input-icon" />
                  </template>
                </q-input>
              </div>
              <div class="field-group">
                <label class="field-lbl">Teléfono</label>
                <q-input v-model="perfil.telefono" outlined dense class="field-input" bg-color="white">
                  <template #prepend>
                    <q-icon name="phone" class="input-icon" />
                  </template>
                </q-input>
              </div>
              <div class="field-group">
                <label class="field-lbl">Usuario</label>
                <q-input v-model="perfil.usuario" outlined dense class="field-input" bg-color="white" readonly>
                  <template #prepend>
                    <q-icon name="alternate_email" class="input-icon" />
                  </template>
                </q-input>
              </div>
              <div class="field-group">
                <label class="field-lbl">Cargo</label>
                <q-input v-model="perfil.cargo" outlined dense class="field-input" bg-color="white" readonly>
                  <template #prepend>
                    <q-icon name="work_outline" class="input-icon" />
                  </template>
                </q-input>
              </div>
              <div class="field-group">
                <label class="field-lbl">Fecha de Contratación</label>
                <q-input :model-value="formatearFecha(perfil.fechaContratacion)" outlined dense class="field-input" bg-color="white" readonly>
                  <template #prepend>
                    <q-icon name="handshake" class="input-icon" />
                  </template>
                </q-input>
              </div>
              <div class="field-group field-full">
                <label class="field-lbl">Dirección</label>
                <q-input v-model="perfil.direccion" outlined dense class="field-input" bg-color="white" readonly>
                  <template #prepend>
                    <q-icon name="place" class="input-icon" />
                  </template>
                </q-input>
              </div>
              <div class="field-group">
                <label class="field-lbl">Fecha de Registro</label>
                <q-input v-model="perfil.fechaRegistro" outlined dense class="field-input" bg-color="white" readonly>
                  <template #prepend>
                    <q-icon name="calendar_today" class="input-icon" />
                  </template>
                </q-input>
              </div>
              <div class="field-group">
                <label class="field-lbl">Último Inicio de Sesión</label>
                <q-input v-model="perfil.ultimoAcceso" outlined dense class="field-input" bg-color="white" readonly>
                  <template #prepend>
                    <q-icon name="access_time" class="input-icon" />
                  </template>
                </q-input>
              </div>
            </div>

            <div class="row justify-end q-mt-md">
              <q-btn label="Guardar Cambios" icon="save" class="btn-save" unelevated @click="guardarCambios"
                :loading="guardando">
                <template #loading>
                  <q-spinner-dots color="white" size="1em" />
                </template>
              </q-btn>
            </div>
          </q-card>



        </div>

        <!-- COLUMNA DERECHA -->
        <div class="col-right">

          <!-- RESUMEN DE CUENTA -->
          <q-card class="perfil-card q-mb-md" flat>
            <div class="card-title-row">
              <q-icon name="badge" class="card-title-icon" />
              Resumen de Cuenta
            </div>

            <div class="resumen-row">
              <div class="resumen-left">
                <div class="resumen-icon ri-green">
                  <q-icon name="shield" size="16px" style="color:#4a8c25" />
                </div>
                <span class="resumen-label">Rol Actual</span>
              </div>
                <span class="badge-admin">{{ perfil.rol }}</span>
            </div>

            <div class="resumen-row">
              <div class="resumen-left">
                <div class="resumen-icon ri-teal">
                  <q-icon name="verified_user" size="16px" style="color:#0f6e56" />
                </div>
                <span class="resumen-label">Estado</span>
              </div>
              <span :class="perfil.activo ? 'badge-activo' : 'badge-inactivo'">
                <span :class="perfil.activo ? 'dot-activo' : 'dot-inactivo'"></span>
                {{ perfil.activo ? 'Activo' : 'Inactivo' }}
              </span>
            </div>

          </q-card>

          <!-- ESTADÍSTICAS -->
          <q-card class="perfil-card" flat>
            <div class="card-title-row">
              <q-icon name="bar_chart" class="card-title-icon" />
              Estadísticas de Actividad
            </div>

            <div class="stat-grid">
              <div class="stat-card">
                <div class="stat-icon ri-green">
                  <q-icon name="login" size="15px" style="color:#4a8c25" />
                </div>
                <div class="stat-num">{{ perfil.totalIniciosSesion ?? '0' }}</div>
                <div class="stat-lbl">Total Inicios de Sesión</div>
              </div>
              <div class="stat-card">
                <div class="stat-icon ri-teal">
                  <q-icon name="date_range" size="15px" style="color:#0f6e56" />
                </div>
                <div class="stat-num">{{ perfil.iniciosUltimos7Dias ?? '0' }}</div>
                <div class="stat-lbl">Últimos 7 días</div>
              </div>
              <div class="stat-card">
                <div class="stat-icon ri-orange">
                  <q-icon name="calendar_month" size="15px" style="color:#d97b1a" />
                </div>
                <div class="stat-num">{{ perfil.iniciosUltimos30Dias ?? '0' }}</div>
                <div class="stat-lbl">Últimos 30 días</div>
              </div>
              <div class="stat-card">
                <div class="stat-icon ri-green">
                  <q-icon name="person_add" size="15px" style="color:#4a8c25" />
                </div>
                <div class="stat-num" style="font-size:13px">{{ formatearFecha(perfil.fechaRegistro) }}</div>
                <div class="stat-lbl">Cuenta Creada</div>
              </div>
            </div>
          </q-card>

        </div>
      </div>
  </q-page>
</template>

<script setup>
import CambiarPassword from '../../components/auth/nuevoPassword/ChangePassword.vue'
import CambiarFotoPerfil from '../../components/perfil/CambiarFotoPerfil.vue'
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useAuthStore } from '../../store/store'
import { getUserList, actualizarMiPerfil } from '../../api/usuario/usuario'

const $q = useQuasar()
const authStore = useAuthStore()
const guardando = ref(false)
const tabActiva = ref('info')
const mostrarFoto = ref(false)

const tabs = [
  { key: 'info', label: 'Información Personal' },
  { key: 'password', label: 'Cambiar Contraseña' },
]

const perfil = ref({
  nombres: '',
  apellidos: '',
  ci: '',
  sexo: '',
  correo: '',
  telefono: '',
  usuario: '',
  cargo: '',
  fechaContratacion: '',
  direccion: '',
  fechaRegistro: '',
  ultimoAcceso: '',
  fotoUrl: '',
  rol: '',
  activo: true,
  totalIniciosSesion: 0,
  iniciosUltimos7Dias: 0,
  iniciosUltimos30Dias: 0
})

function formatearFecha(fecha) {
  if (!fecha) return ''
  if (fecha.includes('T')) fecha = fecha.split('T')[0]
  const [y, m, d] = fecha.split('-')
  if (!y || !m || !d) return fecha
  return `${d}/${m}/${y}`
}

const iniciales = computed(() => {
  const nombre = perfil.value.nombres || ''
  const apellido = perfil.value.apellidos || ''
  return (nombre.charAt(0) + apellido.charAt(0)).toUpperCase() || 'U'
})

const onFotoActualizada = (urlFoto) => {
  if (urlFoto) {
    const urlAbsoluta = urlFoto.startsWith('http') ? urlFoto : `${import.meta.env.VITE_API_URL}${urlFoto}`
    perfil.value.fotoUrl = urlAbsoluta
    authStore.setFotoUrl(urlAbsoluta)
  }
}

const cargarPerfil = async () => {
  try {
    const data = await getUserList()
    console.log('PERFIL API:', data)
    perfil.value.usuario = data.username || ''
    const nombreCompleto = data.nombreEmpleado || ''
    const partes = nombreCompleto.split(' ')
    perfil.value.nombres = partes.slice(0, -2).join(' ') || partes[0] || ''
    perfil.value.apellidos = partes.slice(-2).join(' ') || ''
    perfil.value.ci = data.ci || ''
    perfil.value.sexo = data.sexo || ''
    perfil.value.fechaContratacion = data.fechaContratacion || ''
    perfil.value.fechaRegistro = formatearFecha(data.fechaRegistro)
    perfil.value.ultimoAcceso = formatearFecha(data.ultimoAcceso)
    perfil.value.activo = data.activo !== undefined ? data.activo : true
    perfil.value.totalIniciosSesion = data.totalIniciosSesion ?? 0
    perfil.value.iniciosUltimos7Dias = data.iniciosUltimos7Dias ?? 0
    perfil.value.iniciosUltimos30Dias = data.iniciosUltimos30Dias ?? 0
    perfil.value.correo = data.correo || ''
    perfil.value.telefono = data.telefono || ''
    perfil.value.direccion = data.direccion || ''
    perfil.value.cargo = data.cargo || ''
    perfil.value.rol = (data.roles && data.roles.length > 0) ? data.roles[0].replace('ROLE_', '') : ''
    perfil.value.fotoUrl = data.fotoUrl ? `${import.meta.env.VITE_API_URL}${data.fotoUrl}` : ''

    if (data.fotoUrl) {
      authStore.setFotoUrl(perfil.value.fotoUrl)
    }
  } catch (error) {
    console.error('Error al cargar perfil:', error)
  }
}

onMounted(() => {
  cargarPerfil()
})

const columnsActividad = [
  { name: 'id', label: '#', field: 'id', align: 'left', style: 'width:40px' },
  { name: 'actividad', label: 'Actividad', field: 'actividad', align: 'left', style: 'width:140px' },
  { name: 'descripcion', label: 'Descripción', field: 'descripcion', align: 'left' },
  { name: 'fecha', label: 'Fecha y Hora', field: 'fecha', align: 'left', style: 'width:140px' },
  { name: 'ip', label: 'IP Address', field: 'ip', align: 'left', style: 'width:110px' },
  { name: 'dispositivo', label: 'Dispositivo', field: 'dispositivo', align: 'left', style: 'width:120px' }
]

const actividad = ref([
  { id: 1, actividad: 'Inicio de Sesión', descripcion: 'Sesión iniciada correctamente', fecha: '25/05/2024 10:45 AM', ip: '192.168.1.100', dispositivo: 'Chrome - Windows' },
  { id: 2, actividad: 'Actualización', descripcion: 'Información personal actualizada', fecha: '24/05/2024 04:20 PM', ip: '192.168.1.100', dispositivo: 'Chrome - Windows' },
  { id: 3, actividad: 'Cambio Contraseña', descripcion: 'Contraseña actualizada', fecha: '20/05/2024 11:30 AM', ip: '192.168.1.100', dispositivo: 'Chrome - Windows' },
  { id: 4, actividad: 'Inicio de Sesión', descripcion: 'Sesión iniciada correctamente', fecha: '20/05/2024 09:15 AM', ip: '192.168.1.105', dispositivo: 'Firefox - Windows' },
  { id: 5, actividad: 'Cierre de Sesión', descripcion: 'Sesión cerrada', fecha: '20/05/2024 09:10 AM', ip: '192.168.1.105', dispositivo: 'Firefox - Windows' }
])



const guardarCambios = async () => {
  guardando.value = true
  try {
    await actualizarMiPerfil({
      correo: perfil.value.correo,
      telefono: perfil.value.telefono
    })
    $q.notify({ type: 'positive', message: 'Perfil actualizado correctamente' })
  } catch (e) {
    $q.notify({
      type: 'negative',
      message: e.response?.data?.message || 'Error al actualizar el perfil'
    })
  } finally {
    guardando.value = false
  }
}




</script>

<style scoped src="../../assets/styles/perfil/perfil.css"></style>
<template>
  <q-page class="perfil-page q-pa-md">

    <!-- HEADER -->
    <div class="perfil-header q-mb-md">
      <div class="row items-center no-wrap q-gutter-md">
        <div class="avatar-wrap">
          <div class="avatar-circle">AS</div>
          <div class="avatar-edit">
            <q-icon name="photo_camera" size="10px" color="white" />
          </div>
        </div>
        <div>
          <div class="ph-title">Gestión de Perfil</div>
          <div class="ph-sub">Administra tu información personal y configuración de cuenta</div>
        </div>
      </div>
      
    </div>

      <!-- Cambia el componente CambiarFotoPerfil -->
      <CambiarFotoPerfil
        v-model="mostrarFoto"
        nombre-usuario="Admin Sistema"
        @foto-actualizada="onFotoActualizada"
      />

    <!-- TABS -->
    <div class="perfil-tabs q-mb-md">
      <div
        v-for="tab in tabs"
        :key="tab.key"
        class="perfil-tab"
        :class="{ active: tabActiva === tab.key }"
        @click="tabActiva = tab.key"
      >
        {{ tab.label }}
      </div>
    </div>

    <!-- contenido segun tab activa-->
    <CambiarPassword     v-if="tabActiva === 'password'" />

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
            <div class="avatar-lg-wrap"  @click="mostrarFoto =  true" style="cursor: pointer">
              <div class="avatar-lg">AS</div>
              <div class="avatar-lg-edit">
                <q-icon name="photo_camera" size="12px" color="white" />
              </div>
            </div>
            <div>
              <div class="profile-name">Admin Sistema</div>
              <div class="profile-role">Administrador del Sistema</div>
              <div class="profile-badge">
                <q-icon name="verified_user" size="12px" />
                ADMIN
              </div>
            </div>
          </div>

          <!-- Campos -->
          <div class="fields-grid">
            <div class="field-group">
              <label class="field-lbl">Nombres</label>
              <q-input
                v-model="perfil.nombres"
                outlined dense
                class="field-input"
                bg-color="white"
              />
            </div>
            <div class="field-group">
              <label class="field-lbl">Apellidos</label>
              <q-input
                v-model="perfil.apellidos"
                outlined dense
                class="field-input"
                bg-color="white"
              />
            </div>
            <div class="field-group">
              <label class="field-lbl">Correo Electrónico</label>
              <q-input
                v-model="perfil.correo"
                outlined dense
                class="field-input"
                bg-color="white"
              >
                <template #prepend>
                  <q-icon name="mail_outline" class="input-icon" />
                </template>
              </q-input>
            </div>
            <div class="field-group">
              <label class="field-lbl">Teléfono</label>
              <q-input
                v-model="perfil.telefono"
                outlined dense
                class="field-input"
                bg-color="white"
              >
                <template #prepend>
                  <q-icon name="phone" class="input-icon" />
                </template>
              </q-input>
            </div>
            <div class="field-group">
              <label class="field-lbl">Usuario</label>
              <q-input
                v-model="perfil.usuario"
                outlined dense
                class="field-input"
                bg-color="white"
                readonly
              >
                <template #prepend>
                  <q-icon name="alternate_email" class="input-icon" />
                </template>
              </q-input>
            </div>
            <div class="field-group">
              <label class="field-lbl">Cargo</label>
              <q-input
                v-model="perfil.cargo"
                outlined dense
                class="field-input"
                bg-color="white"
                readonly
              >
                <template #prepend>
                  <q-icon name="work_outline" class="input-icon" />
                </template>
              </q-input>
            </div>
            <div class="field-group field-full">
              <label class="field-lbl">Dirección</label>
              <q-input
                v-model="perfil.direccion"
                outlined dense
                class="field-input"
                bg-color="white"
              >
                <template #prepend>
                  <q-icon name="place" class="input-icon" />
                </template>
              </q-input>
            </div>
            <div class="field-group">
              <label class="field-lbl">Fecha de Registro</label>
              <q-input
                v-model="perfil.fechaRegistro"
                outlined dense
                class="field-input"
                bg-color="white"
                readonly
              >
                <template #prepend>
                  <q-icon name="calendar_today" class="input-icon" />
                </template>
              </q-input>
            </div>
            <div class="field-group">
              <label class="field-lbl">Último Inicio de Sesión</label>
              <q-input
                v-model="perfil.ultimoAcceso"
                outlined dense
                class="field-input"
                bg-color="white"
                readonly
              >
                <template #prepend>
                  <q-icon name="access_time" class="input-icon" />
                </template>
              </q-input>
            </div>
          </div>

          <div class="row justify-end q-mt-md">
            <q-btn
              label="Guardar Cambios"
              icon="save"
              class="btn-save"
              unelevated
              @click="guardarCambios"
              :loading="guardando"
            >
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
            <span class="badge-admin">ADMIN</span>
          </div>

          <div class="resumen-row">
            <div class="resumen-left">
              <div class="resumen-icon ri-teal">
                <q-icon name="verified_user" size="16px" style="color:#0f6e56" />
              </div>
              <span class="resumen-label">Estado</span>
            </div>
            <span class="badge-activo">
              <span class="dot-activo"></span>
              Activo
            </span>
          </div>

          <div class="resumen-row">
            <div class="resumen-left">
              <div class="resumen-icon ri-blue">
                <q-icon name="computer" size="16px" style="color:#185fa5" />
              </div>
              <span class="resumen-label">Sesiones Activas</span>
            </div>
            <span class="resumen-val">1</span>
          </div>

          <div class="resumen-row" style="border-bottom:none">
            <div class="resumen-left">
              <div class="resumen-icon ri-orange">
                <q-icon name="place" size="16px" style="color:#d97b1a" />
              </div>
              <span class="resumen-label">IP Actual</span>
            </div>
            <span class="resumen-val" style="font-size:11.5px">192.168.1.100</span>
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
              <div class="stat-num">45</div>
              <div class="stat-lbl">Total Inicios de Sesión</div>
            </div>
            <div class="stat-card">
              <div class="stat-icon ri-teal">
                <q-icon name="date_range" size="15px" style="color:#0f6e56" />
              </div>
              <div class="stat-num">12</div>
              <div class="stat-lbl">Últimos 7 días</div>
            </div>
            <div class="stat-card">
              <div class="stat-icon ri-orange">
                <q-icon name="calendar_month" size="15px" style="color:#d97b1a" />
              </div>
              <div class="stat-num">38</div>
              <div class="stat-lbl">Últimos 30 días</div>
            </div>
            <div class="stat-card">
              <div class="stat-icon ri-green">
                <q-icon name="person_add" size="15px" style="color:#4a8c25" />
              </div>
              <div class="stat-num" style="font-size:13px">15/01/2024</div>
              <div class="stat-lbl">Cuenta Creada</div>
            </div>
          </div>
        </q-card>

      </div>
    </div>
  </q-page>
</template>

<script setup>
import CambiarPassword  from '../../components/auth/nuevoPassword/ChangePassword.vue'
import CambiarFotoPerfil from '../../components/perfil/CambiarFotoPerfil.vue'
import { ref } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const guardando = ref(false)
const tabActiva = ref('info')
const mostrarFoto = ref(false)

const tabs = [
  { key: 'info',       label: 'Información Personal' },
  { key: 'password',   label: 'Cambiar Contraseña' },
]

//metodo para actualizar foto perfil
const onFotoActualizada = (urlFoto) => {
  console.log(urlFoto)
}

const perfil = ref({
  nombres:       'Admin',
  apellidos:     'Sistema',
  correo:        'admin@supermercado.com',
  telefono:      '999 888 777',
  usuario:       'admin',
  cargo:         'Administrador del Sistema',
  direccion:     'Av. Principal 123, Ciudad, País',
  fechaRegistro: '15/01/2024 08:30 AM',
  ultimoAcceso:  '25/05/2024 10:45 AM'
})

const columnsActividad = [
  { name: 'id',          label: '#',           field: 'id',          align: 'left', style: 'width:40px'  },
  { name: 'actividad',   label: 'Actividad',   field: 'actividad',   align: 'left', style: 'width:140px' },
  { name: 'descripcion', label: 'Descripción', field: 'descripcion', align: 'left'                       },
  { name: 'fecha',       label: 'Fecha y Hora',field: 'fecha',       align: 'left', style: 'width:140px' },
  { name: 'ip',          label: 'IP Address',  field: 'ip',          align: 'left', style: 'width:110px' },
  { name: 'dispositivo', label: 'Dispositivo', field: 'dispositivo', align: 'left', style: 'width:120px' }
]

const actividad = ref([
  { id: 1, actividad: 'Inicio de Sesión',    descripcion: 'Sesión iniciada correctamente',     fecha: '25/05/2024 10:45 AM', ip: '192.168.1.100', dispositivo: 'Chrome - Windows' },
  { id: 2, actividad: 'Actualización',       descripcion: 'Información personal actualizada',  fecha: '24/05/2024 04:20 PM', ip: '192.168.1.100', dispositivo: 'Chrome - Windows' },
  { id: 3, actividad: 'Cambio Contraseña',   descripcion: 'Contraseña actualizada',            fecha: '20/05/2024 11:30 AM', ip: '192.168.1.100', dispositivo: 'Chrome - Windows' },
  { id: 4, actividad: 'Inicio de Sesión',    descripcion: 'Sesión iniciada correctamente',     fecha: '20/05/2024 09:15 AM', ip: '192.168.1.105', dispositivo: 'Firefox - Windows' },
  { id: 5, actividad: 'Cierre de Sesión',    descripcion: 'Sesión cerrada',                   fecha: '20/05/2024 09:10 AM', ip: '192.168.1.105', dispositivo: 'Firefox - Windows' }
])



const guardarCambios = async () => {
  guardando.value = true
  await new Promise(r => setTimeout(r, 1200))
  guardando.value = false
  $q.notify({ type: 'positive', message: 'Perfil actualizado correctamente' })
}




</script>

<style scoped src="../../assets/styles/perfil/perfil.css">

</style>
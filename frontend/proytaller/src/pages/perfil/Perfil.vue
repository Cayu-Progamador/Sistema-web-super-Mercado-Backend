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
    <div class="perfil-grid">

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
            <div class="avatar-lg-wrap">
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

        <!-- ACTIVIDAD RECIENTE -->
        <q-card class="perfil-card" flat>
          <div class="card-title-row">
            <q-icon name="history" class="card-title-icon" />
            Actividad Reciente
          </div>

          <q-table
            flat
            :rows="actividad"
            :columns="columnsActividad"
            row-key="id"
            hide-pagination
            class="actividad-table"
          >
            <template #body-cell-actividad="props">
              <q-td :props="props">
                <span :class="['act-badge', getBadgeClass(props.row.actividad)]">
                  {{ props.row.actividad }}
                </span>
              </q-td>
            </template>

            <template #body-cell-descripcion="props">
              <q-td :props="props">
                <span class="act-desc">{{ props.row.descripcion }}</span>
              </q-td>
            </template>

            <template #body-cell-fecha="props">
              <q-td :props="props">
                <span class="act-meta">{{ props.row.fecha }}</span>
              </q-td>
            </template>

            <template #body-cell-ip="props">
              <q-td :props="props">
                <span class="act-meta">{{ props.row.ip }}</span>
              </q-td>
            </template>

            <template #body-cell-dispositivo="props">
              <q-td :props="props">
                <span class="act-meta">{{ props.row.dispositivo }}</span>
              </q-td>
            </template>
          </q-table>

          <div class="ver-mas" @click="verMasActividad">
            <q-icon name="expand_more" size="16px" />
            Ver más actividad
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
import { ref } from 'vue'
import { useQuasar } from 'quasar'
import CambiarPassword  from '../../components/auth/nuevoPassword/ChangePassword.vue'
const $q = useQuasar()
const guardando = ref(false)
const tabActiva = ref('info')

const tabs = [
  { key: 'info',       label: 'Información Personal' },
  { key: 'password',   label: 'Cambiar Contraseña' },
  { key: 'prefs',      label: 'Preferencias' },
  { key: 'actividad',  label: 'Actividad Reciente' }
]

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

const getBadgeClass = (tipo) => {
  if (tipo === 'Inicio de Sesión')  return 'ab-green'
  if (tipo === 'Actualización')     return 'ab-teal'
  if (tipo === 'Cambio Contraseña') return 'ab-orange'
  if (tipo === 'Cierre de Sesión')  return 'ab-red'
  return 'ab-green'
}

const guardarCambios = async () => {
  guardando.value = true
  await new Promise(r => setTimeout(r, 1200))
  guardando.value = false
  $q.notify({ type: 'positive', message: 'Perfil actualizado correctamente' })
}

const verMasActividad = () => {
  $q.notify({ type: 'info', message: 'Cargando más actividad...' })
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

/* ── Página ── */
.perfil-page {
  background: #f7f9f4 !important;
  font-family: 'Nunito', sans-serif;
}

/* ── Header ── */
.perfil-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.avatar-wrap { position: relative; width: 52px; height: 52px; flex-shrink: 0; }
.avatar-circle {
  width: 52px; height: 52px;
  border-radius: 50%;
  background: #eaf4d8;
  border: 2.5px solid #c8e0a0;
  display: flex; align-items: center; justify-content: center;
  font-size: 18px; font-weight: 900; color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}
.avatar-edit {
  position: absolute; bottom: 0; right: 0;
  width: 18px; height: 18px;
  background: #4a8c25; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  border: 2px solid #fff;
}

.ph-title {
  font-family: 'Nunito', sans-serif;
  font-size: 20px; font-weight: 900; color: #2a5c1a;
}
.ph-sub {
  font-family: 'Nunito', sans-serif;
  font-size: 12px; font-weight: 500; color: #9dbf78; margin-top: 2px;
}

.breadcrumb {
  display: flex; align-items: center; gap: 6px;
  font-size: 12px; font-weight: 600; color: #bdd49a;
  font-family: 'Nunito', sans-serif;
}
.bc-active { color: #4a8c25 !important; }

/* ── Tabs ── */
.perfil-tabs {
  display: flex;
  border-bottom: 2px solid #e4edd8;
}
.perfil-tab {
  padding: 10px 20px;
  font-size: 13px; font-weight: 700;
  color: #9dbf78;
  cursor: pointer;
  border-bottom: 2.5px solid transparent;
  margin-bottom: -2px;
  transition: all 0.2s;
  font-family: 'Nunito', sans-serif;
}
.perfil-tab.active { color: #2a5c1a; border-bottom-color: #4a8c25; }
.perfil-tab:hover:not(.active) { color: #5a8040; }

/* ── Grid principal ── */
.perfil-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 16px;
  align-items: start;
}

/* ── Cards ── */
.perfil-card {
  background: #ffffff !important;
  border: 1px solid #e4edd8 !important;
  border-radius: 16px !important;
  padding: 20px !important;
}

.card-title-row {
  display: flex; align-items: center; gap: 8px;
  font-size: 12px; font-weight: 800;
  color: #2a5c1a;
  text-transform: uppercase; letter-spacing: 0.1em;
  margin-bottom: 16px;
  font-family: 'Nunito', sans-serif;
}
.card-title-icon { color: #7aaa4e !important; font-size: 18px !important; }

/* ── Avatar grande ── */
.avatar-lg-wrap { position: relative; flex-shrink: 0; }
.avatar-lg {
  width: 88px; height: 88px;
  border-radius: 50%;
  background: #eaf4d8;
  border: 3px solid #c8e0a0;
  display: flex; align-items: center; justify-content: center;
  font-size: 30px; font-weight: 900; color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}
.avatar-lg-edit {
  position: absolute; bottom: 2px; right: 2px;
  width: 26px; height: 26px;
  background: #4a8c25; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  border: 2px solid #fff; cursor: pointer;
}

.profile-name {
  font-family: 'Nunito', sans-serif;
  font-size: 18px; font-weight: 900; color: #2a5c1a;
}
.profile-role {
  font-family: 'Nunito', sans-serif;
  font-size: 12.5px; font-weight: 500; color: #9dbf78; margin-top: 3px;
}
.profile-badge {
  display: inline-flex; align-items: center; gap: 5px;
  margin-top: 7px;
  background: #eaf4d8; color: #3b6d11;
  border: 1px solid #c8e0a0;
  font-size: 11px; font-weight: 800;
  padding: 3px 10px; border-radius: 20px;
  font-family: 'Nunito', sans-serif;
}

/* ── Fields ── */
.fields-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.field-full { grid-column: 1 / -1; }
.field-group { display: flex; flex-direction: column; gap: 5px; }
.field-lbl {
  font-size: 11px; font-weight: 700; color: #7aaa4e;
  text-transform: uppercase; letter-spacing: 0.08em;
  font-family: 'Nunito', sans-serif;
}

/* Quasar input overrides */
.field-input :deep(.q-field__control) {
  background: #fbfdf8 !important;
  border: 1.5px solid #ddecc5 !important;
  border-radius: 10px !important;
  box-shadow: none !important;
}
.field-input :deep(.q-field__control::before),
.field-input :deep(.q-field__control::after) { display: none !important; }
.field-input :deep(.q-field--focused .q-field__control) {
  border-color: #4a8c25 !important;
  box-shadow: 0 0 0 3px rgba(74,140,37,0.1) !important;
}
.field-input :deep(.q-field__native) {
  color: #2a5c1a !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 13.5px !important;
  font-weight: 600 !important;
}
.field-input :deep(.q-field__label) { display: none !important; }
.field-input :deep(.q-focus-helper) { display: none !important; }
.input-icon { color: #bdd49a !important; font-size: 18px !important; }

/* ── Botón guardar ── */
.btn-save {
  background: #4a8c25 !important;
  color: #ffffff !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 14px !important;
  font-weight: 800 !important;
  border-radius: 10px !important;
  padding: 8px 22px !important;
  box-shadow: 0 4px 14px rgba(74,140,37,0.28) !important;
  transition: all 0.2s !important;
}
.btn-save:hover {
  background: #3d7a1e !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 6px 20px rgba(74,140,37,0.38) !important;
}

/* ── Tabla actividad ── */
.actividad-table :deep(thead tr) { background: #f7f9f4 !important; }
.actividad-table :deep(thead th) {
  font-family: 'Nunito', sans-serif !important;
  font-size: 11px !important;
  font-weight: 800 !important;
  text-transform: uppercase !important;
  letter-spacing: 0.08em !important;
  color: #7aaa4e !important;
  border-bottom: 2px solid #e4edd8 !important;
}
.actividad-table :deep(tbody td) {
  border-bottom: 1px solid #f0f5ea !important;
  font-family: 'Nunito', sans-serif !important;
}
.actividad-table :deep(tbody tr:hover td) { background: #fbfdf8 !important; }

.act-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11.5px; font-weight: 700;
  font-family: 'Nunito', sans-serif;
  white-space: nowrap;
}
.ab-green  { background: #eaf4d8; color: #3b6d11; border: 1px solid #c8e0a0; }
.ab-teal   { background: #e1f5ee; color: #0f6e56; border: 1px solid #9fe1cb; }
.ab-orange { background: #fef3e2; color: #a05c10; border: 1px solid #f5c97a; }
.ab-red    { background: #fef2f2; color: #991b1b; border: 1px solid #fca5a5; }

.act-desc  { font-size: 13px; font-weight: 600; color: #5a8040; }
.act-meta  { font-size: 12.5px; font-weight: 500; color: #9dbf78; }

.ver-mas {
  display: flex; align-items: center; justify-content: center; gap: 5px;
  padding: 14px;
  font-size: 13px; font-weight: 700; color: #4a8c25;
  cursor: pointer;
  border-top: 1px solid #e4edd8;
  font-family: 'Nunito', sans-serif;
  transition: color 0.2s;
}
.ver-mas:hover { color: #3d7a1e; }

/* ── Resumen de cuenta ── */
.resumen-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f5ea;
}
.resumen-left { display: flex; align-items: center; gap: 10px; }
.resumen-icon {
  width: 32px; height: 32px;
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.ri-green  { background: #eaf4d8; }
.ri-teal   { background: #e1f5ee; }
.ri-orange { background: #fef3e2; }
.ri-blue   { background: #e6f1fb; }

.resumen-label {
  font-size: 12.5px; font-weight: 600; color: #5a8040;
  font-family: 'Nunito', sans-serif;
}
.resumen-val {
  font-size: 13px; font-weight: 800; color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}

.badge-admin {
  background: #eaf4d8; color: #3b6d11;
  border: 1px solid #c8e0a0;
  font-size: 11px; font-weight: 800;
  padding: 3px 10px; border-radius: 20px;
  font-family: 'Nunito', sans-serif;
}
.badge-activo {
  display: inline-flex; align-items: center; gap: 4px;
  background: #e1f5ee; color: #0f6e56;
  border: 1px solid #9fe1cb;
  font-size: 11px; font-weight: 800;
  padding: 3px 10px; border-radius: 20px;
  font-family: 'Nunito', sans-serif;
}
.dot-activo {
  width: 6px; height: 6px;
  border-radius: 50%; background: #0f6e56;
}

/* ── Estadísticas ── */
.stat-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.stat-card {
  background: #fbfdf8;
  border: 1px solid #e4edd8;
  border-radius: 12px;
  padding: 12px;
}
.stat-icon {
  width: 28px; height: 28px;
  border-radius: 7px;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 8px;
}
.stat-num {
  font-size: 22px; font-weight: 900; color: #2a5c1a;
  line-height: 1;
  font-family: 'Nunito', sans-serif;
}
.stat-lbl {
  font-size: 11px; font-weight: 600; color: #9dbf78;
  margin-top: 3px;
  font-family: 'Nunito', sans-serif;
}
</style>
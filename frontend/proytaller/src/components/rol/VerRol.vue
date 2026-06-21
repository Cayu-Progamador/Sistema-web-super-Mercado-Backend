<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card">

      <div class="accent-bar"></div>

      <div class="dialog-header">
        <div class="header-left">
          <div class="header-icon">
            <q-icon name="shield" size="22px" color="#4a8c25" />
          </div>
          <div>
            <div class="header-eyebrow">DETALLE DEL ROL</div>
            <div class="header-title">{{ displayNombre }}</div>
          </div>
        </div>
        <q-btn icon="close" flat round dense class="close-btn" v-close-popup />
      </div>

      <q-card-section class="q-px-lg q-pt-md q-pb-sm">

        <!-- Loading -->
        <div v-if="cargando" class="text-center q-py-lg">
          <q-spinner-facebook color="green-7" size="40px" />
          <div class="q-mt-sm text-grey-7">Cargando detalle...</div>
        </div>

        <template v-if="!cargando && detalle">

          <!-- Info básica -->
          <div class="info-grid q-mb-lg">
            <div class="info-item">
              <div class="info-label">Descripción</div>
              <div class="info-value">{{ detalle.descripcion || 'Sin descripción' }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">Estado</div>
              <div class="row items-center q-gutter-xs">
                <span :class="['estado-badge', detalle.estado ? 'badge-activo' : 'badge-inactivo']">
                  <span :class="['badge-dot', detalle.estado ? 'dot-activo' : 'dot-inactivo']"></span>
                  {{ detalle.estado ? 'Activo' : 'Inactivo' }}
                </span>
              </div>
            </div>
            <div class="info-item">
              <div class="info-label">Usuarios Asignados</div>
              <div class="info-value">{{ detalle.cantidadUsuarios }}</div>
            </div>
          </div>

          <q-separator class="q-mb-md" />

          <!-- Permisos -->
          <div class="section-title">
            <q-icon name="lock" size="16px" color="#82bd43" />
            Permisos Asociados ({{ detalle.permisos?.length || 0 }})
          </div>
          <div v-if="detalle.permisos && detalle.permisos.length" class="permisos-grid q-mb-lg">
            <div v-for="permiso in detalle.permisos" :key="permiso.idPermiso" class="permiso-chip">
              <q-icon name="check_circle" size="14px" color="green-7" />
              <span class="permiso-nombre">{{ permiso.nombre }}</span>
              <span class="permiso-codigo">{{ permiso.codigo }}</span>
            </div>
          </div>
          <div v-else class="text-grey-6 q-mb-lg" style="font-size:13px;">
            Este rol no tiene permisos asociados.
          </div>

          <q-separator class="q-mb-md" />

          <!-- Usuarios -->
          <div class="section-title">
            <q-icon name="people" size="16px" color="#82bd43" />
            Usuarios con este Rol ({{ detalle.usuarios?.length || 0 }})
          </div>
          <div v-if="detalle.usuarios && detalle.usuarios.length" class="usuarios-list q-mb-sm">
            <div v-for="usr in detalle.usuarios" :key="usr.username" class="usuario-item">
              <div class="usuario-avatar">
                <q-icon name="person" size="18px" color="green-7" />
              </div>
              <div class="usuario-info">
                <div class="usuario-nombre">{{ usr.nombreCompleto || usr.username }}</div>
                <div class="usuario-username">@{{ usr.username }}</div>
              </div>
              <div class="usuario-extra">
                <span v-if="usr.correo" class="usuario-correo">{{ usr.correo }}</span>
                <q-chip
                  dense
                  size="12px"
                  :color="usr.activo ? 'green-7' : 'grey-6'"
                  text-color="white"
                  class="q-ml-sm"
                >{{ usr.activo ? 'Activo' : 'Inactivo' }}</q-chip>
              </div>
            </div>
          </div>
          <div v-else class="text-grey-6 q-mb-md" style="font-size:13px;">
            No hay usuarios asignados a este rol.
          </div>

        </template>
      </q-card-section>

      <q-card-actions align="right" class="q-px-lg q-pb-md">
        <q-btn flat label="Cerrar" v-close-popup class="btn-cancel" />
      </q-card-actions>

    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { obtenerDetalleRol } from '../../api/rol/rol'

const props = defineProps({
  rolId: { type: Number, default: null }
})

const abierto = defineModel()
const cargando = ref(false)
const detalle = ref(null)

const displayNombre = ref('')

watch(abierto, async (val) => {
  if (val && props.rolId) {
    await cargarDetalle(props.rolId)
  }
})

const cargarDetalle = async (id) => {
  cargando.value = true
  detalle.value = null
  try {
    const data = await obtenerDetalleRol(id)
    detalle.value = data
    displayNombre.value = (data.nombre || '').replace('ROLE_', '')
  } catch (error) {
    console.error('Error al cargar detalle del rol:', error)
    detalle.value = null
  } finally {
    cargando.value = false
  }
}
</script>

<style scoped>
.dialog-card {
  width: 100%;
  max-width: 580px;
  border-radius: 20px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e4edd8;
  box-shadow: 0 20px 60px rgba(42,92,26,0.15);
  font-family: 'Nunito', sans-serif;
}
.accent-bar {
  height: 3px;
  background: linear-gradient(90deg, #82bd43, #4a8c25, #64992b);
}
.dialog-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  background: #f0f7e8;
  border-bottom: 1px solid #c8e0a0;
  padding: 16px 20px 14px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #eaf4d8;
  border: 1.5px solid #82bd43;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.header-eyebrow {
  font-size: 11px;
  font-weight: 600;
  color: #7aaa4e;
  font-family: 'Nunito', sans-serif;
}
.header-title {
  font-size: 16px;
  font-weight: 900;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}
.close-btn {
  color: #7aaa4e !important;
  background: #f0f7e8 !important;
  border-radius: 8px !important;
}
.close-btn:hover { background: #ddecc5 !important; color: #4a8c25 !important; }

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.info-label {
  font-size: 10px;
  font-weight: 800;
  color: #7aaa4e;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-family: 'Nunito', sans-serif;
}
.info-value {
  font-size: 14px;
  font-weight: 700;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}

.estado-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  font-family: 'Nunito', sans-serif;
}
.badge-activo {
  background: #eaf4d8;
  color: #4a8c25;
  border: 1px solid #c8e0a0;
}
.badge-inactivo {
  background: #fef3e2;
  color: #a05c10;
  border: 1px solid #f5c97a;
}
.badge-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}
.dot-activo { background: #4a8c25; }
.dot-inactivo { background: #d97b1a; }

.section-title {
  font-size: 13px;
  font-weight: 800;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 10px;
}

.permisos-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.permiso-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #f0f7e8;
  border: 1px solid #c8e0a0;
  border-radius: 8px;
  font-size: 12px;
  font-family: 'Nunito', sans-serif;
}
.permiso-nombre {
  font-weight: 700;
  color: #2a5c1a;
}
.permiso-codigo {
  font-size: 10px;
  color: #7aaa4e;
  font-weight: 600;
  margin-left: 4px;
}

.usuarios-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.usuario-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: #f7f9f4;
  border: 1px solid #e4edd8;
  border-radius: 10px;
}
.usuario-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #eaf4d8;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.usuario-info {
  flex: 1;
  min-width: 0;
}
.usuario-nombre {
  font-size: 13px;
  font-weight: 700;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}
.usuario-username {
  font-size: 11px;
  color: #7aaa4e;
  font-weight: 600;
  font-family: 'Nunito', sans-serif;
}
.usuario-extra {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}
.usuario-correo {
  font-size: 11px;
  color: #5a8040;
  font-weight: 600;
  font-family: 'Nunito', sans-serif;
}

.btn-cancel {
  background: #fff;
  color: #5a5a5a;
  border: 1.5px solid #d0d0d0;
  border-radius: 9px;
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 13px;
}
.btn-cancel:hover { background: #f7f7f7; border-color: #bbb; }
</style>

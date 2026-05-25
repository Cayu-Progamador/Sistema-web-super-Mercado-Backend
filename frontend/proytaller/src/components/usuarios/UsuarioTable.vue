<template>
  <q-page class="q-pa-md page-bg">
    <CardsUsuario />

    <q-card flat bordered class="table-card">

      <!-- HEADER -->
      <div class="row items-center justify-between q-pa-md q-col-gutter-md table-header">

        <div>
          <div class="text-h6 text-weight-bold table-title">
            Lista de Usuarios
          </div>
          <div class="text-caption table-subtitle">
            Administra y consulta los usuarios del sistema
          </div>
        </div>

        <div class="row items-center q-gutter-sm">

          <q-input outlined dense debounce="300" v-model="search" placeholder="Buscar usuario..." class="search-input">
            <template v-slot:append>
              <q-icon name="search" class="search-icon" />
            </template>
          </q-input>

          <q-btn outline class="btn-filter" icon="filter_list" label="Filtros" no-caps />

          <q-btn class="btn-add" icon="add" label="Nuevo Usuario" @click="mostrarModal = true" unelevated />

        </div>
      </div>

      <!-- TABLA -->
      <q-table flat :rows="usuarios" :columns="columns" row-key="id" hide-pagination class="custom-table">

        <!-- AVATAR + NOMBRE -->
        <template v-slot:body-cell-nombre="props">
          <q-td :props="props">
            <div class="row items-center no-wrap q-gutter-sm">
              <q-avatar size="38px" class="user-avatar">
                <img :src="props.row.avatar" />
              </q-avatar>
              <span class="text-weight-medium user-name">
                {{ props.row.nombre }}
              </span>
            </div>
          </q-td>
        </template>

        <!-- ROL -->
        <template v-slot:body-cell-rol="props">
          <q-td :props="props">
            <span :class="['rol-badge', `rol-${props.row.rol.toLowerCase()}`]">
              {{ props.row.rol }}
            </span>
          </q-td>
        </template>

        <!-- ESTADO -->
        <template v-slot:body-cell-estado="props">
          <q-td :props="props">
            <span :class="['estado-badge', props.row.estado === 'Activo' ? 'estado-activo' : 'estado-inactivo']">
              <span class="estado-dot"></span>
              {{ props.row.estado }}
            </span>
          </q-td>
        </template>

        <!-- ACCIONES -->
        <template v-slot:body-cell-acciones="props">
          <q-td :props="props">
            <div class="row no-wrap q-gutter-xs">
              <q-btn flat round dense class="action-btn action-view" icon="visibility" />
              <q-btn flat round dense class="action-btn action-edit" icon="edit" />
              <q-btn flat round dense class="action-btn action-delete" icon="delete" />
            </div>
          </q-td>
        </template>

      </q-table>

    </q-card>

    <!-- MODAL -->
    <q-dialog v-model="mostrarModal" persistent>
      <UsuarioForm @cerrar="mostrarModal = false" />
    </q-dialog>
  </q-page>
</template>

<script setup>
import CardsUsuario from '../../components/usuarios/CardsUsuario.vue'
import UsuarioForm from '../../components/usuarios/UsuarioForm.vue'
import { ref } from 'vue'

const mostrarModal = ref(false)
const search = ref('')

const columns = [
  { name: 'id',      label: 'ID',                 field: 'id',      align: 'left' },
  { name: 'nombre',  label: 'Nombre Completo',    field: 'nombre',  align: 'left' },
  { name: 'usuario', label: 'Usuario',             field: 'usuario', align: 'left' },
  { name: 'correo',  label: 'Correo Electrónico', field: 'correo',  align: 'left' },
  { name: 'rol',     label: 'Rol',                field: 'rol',     align: 'left' },
  { name: 'estado',  label: 'Estado',             field: 'estado',  align: 'left' },
  { name: 'ultimo',  label: 'Último Acceso',      field: 'ultimo',  align: 'left' },
  { name: 'acciones',label: 'Acciones',           field: 'acciones',align: 'center' }
]

const usuarios = ref([
  { id: 1, nombre: 'Carlos Pérez',    usuario: 'carlos12',       correo: 'carlos.perez@super.com',    rol: 'ADMIN',     estado: 'Activo',   ultimo: '25/05/2024 08:35', avatar: 'https://i.pravatar.cc/150?img=1'  },
  { id: 2, nombre: 'Maria Gómez',     usuario: 'maria.gomez',    correo: 'maria.gomez@super.com',     rol: 'CAJERO',    estado: 'Activo',   ultimo: '25/05/2024 09:10', avatar: 'https://i.pravatar.cc/150?img=5'  },
  { id: 3, nombre: 'Luis Martínez',   usuario: 'luis.martinez',  correo: 'luis.martinez@super.com',   rol: 'EMPLEADO',  estado: 'Activo',   ultimo: '24/05/2024 17:45', avatar: 'https://i.pravatar.cc/150?img=3'  },
  { id: 4, nombre: 'Ana Rodríguez',   usuario: 'ana.rodriguez',  correo: 'ana.rodriguez@super.com',   rol: 'CAJERO',    estado: 'Inactivo', ultimo: '20/05/2024 14:20', avatar: 'https://i.pravatar.cc/150?img=10' },
  { id: 5, nombre: 'Pedro Sánchez',   usuario: 'pedro.sanchez',  correo: 'pedro.sanchez@super.com',   rol: 'EMPLEADO',  estado: 'Activo',   ultimo: '25/05/2024 07:50', avatar: 'https://i.pravatar.cc/150?img=12' }
])

const getRolColor = (rol) => {
  switch (rol) {
    case 'ADMIN':    return 'deep-purple'
    case 'CAJERO':   return 'primary'
    case 'EMPLEADO': return 'orange'
    default:         return 'grey'
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800&display=swap');

/* ── Página ── */
.page-bg {
  background: #f7f9f4 !important;
  font-family: 'Nunito', sans-serif;
}

/* ── Card principal ── */
.table-card {
  border-radius: 18px !important;
  overflow: hidden;
  border: 1px solid #ddecc5 !important;
  background: #ffffff !important;
  box-shadow: 0 4px 24px rgba(74, 140, 37, 0.08) !important;
}

/* ── Header de la tabla ── */
.table-header {
  background: #f7f9f4;
  border-bottom: 1px solid #e4edd8;
}

.table-title {
  color: #2a5c1a !important;
  font-family: 'Nunito', sans-serif;
  font-weight: 800 !important;
  font-size: 17px !important;
}

.table-subtitle {
  color: #7aaa4e !important;
  font-family: 'Nunito', sans-serif;
  font-size: 12.5px !important;
}

/* ── Buscador ── */
.search-input {
  width: 250px;
}

.search-input :deep(.q-field__control) {
  border-radius: 10px !important;
  background: #ffffff !important;
}

.search-input :deep(.q-field__control:before) {
  border-color: #ddecc5 !important;
  border-radius: 10px !important;
}

.search-input :deep(.q-field__control:hover:before) {
  border-color: #7aaa4e !important;
}

.search-input :deep(.q-field__native) {
  color: #2a5c1a !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 13.5px !important;
}

.search-input :deep(input::placeholder) {
  color: #bdd49a !important;
}

.search-icon {
  color: #9dbf78 !important;
}

/* ── Botón Filtros ── */
.btn-filter {
  border-color: #c8e0a0 !important;
  color: #5a8040 !important;
  border-radius: 10px !important;
  font-family: 'Nunito', sans-serif !important;
  font-weight: 600 !important;
  font-size: 13.5px !important;
}
.btn-filter:hover {
  background: #f0f7e8 !important;
}

/* ── Botón Nuevo Usuario ── */
.btn-add {
  background: #4a8c25 !important;
  color: #ffffff !important;
  border-radius: 10px !important;
  font-family: 'Nunito', sans-serif !important;
  font-weight: 700 !important;
  font-size: 13.5px !important;
  box-shadow: 0 3px 12px rgba(74, 140, 37, 0.25) !important;
  transition: all 0.2s !important;
}
.btn-add:hover {
  background: #3d7a1e !important;
  box-shadow: 0 5px 18px rgba(74, 140, 37, 0.35) !important;
  transform: translateY(-1px);
}

/* ── Tabla ── */
.custom-table :deep(thead tr) {
  background: #f0f7e8 !important;
}

.custom-table :deep(thead th) {
  font-family: 'Nunito', sans-serif !important;
  font-weight: 700 !important;
  font-size: 12px !important;
  text-transform: uppercase !important;
  letter-spacing: 0.06em !important;
  color: #5a8040 !important;
  border-bottom: 1.5px solid #ddecc5 !important;
}

.custom-table :deep(tbody td) {
  height: 68px;
  font-family: 'Nunito', sans-serif !important;
  font-size: 13.5px !important;
  color: #374131 !important;
  border-bottom: 1px solid #f0f5ea !important;
}

.custom-table :deep(tbody tr:hover td) {
  background: #fbfdf8 !important;
}

/* ── Avatar ── */
.user-avatar {
  border: 2px solid #c8e0a0 !important;
}

.user-name {
  color: #2a5c1a !important;
  font-weight: 600 !important;
}

/* ── Badges de Rol ── */
.rol-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11.5px;
  font-weight: 700;
  font-family: 'Nunito', sans-serif;
  letter-spacing: 0.04em;
}

.rol-admin {
  background: #e1f5ee;
  color: #0f6e56;
  border: 1px solid #9fe1cb;
}

.rol-cajero {
  background: #eaf4d8;
  color: #3b6d11;
  border: 1px solid #c8e0a0;
}

.rol-empleado {
  background: #fef3e2;
  color: #a05c10;
  border: 1px solid #f5c97a;
}

/* ── Badges de Estado ── */
.estado-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  font-family: 'Nunito', sans-serif;
}

.estado-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  display: inline-block;
  flex-shrink: 0;
}

.estado-activo {
  background: #eaf4d8;
  color: #3b6d11;
  border: 1px solid #c8e0a0;
}
.estado-activo .estado-dot { background: #4a8c25; }

.estado-inactivo {
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fca5a5;
}
.estado-inactivo .estado-dot { background: #dc2626; }

/* ── Botones de acción ── */
.action-btn {
  border-radius: 8px !important;
  transition: all 0.15s !important;
}

.action-view {
  color: #0f6e56 !important;
}
.action-view:hover {
  background: #e1f5ee !important;
}

.action-edit {
  color: #4a8c25 !important;
}
.action-edit:hover {
  background: #eaf4d8 !important;
}

.action-delete {
  color: #b91c1c !important;
}
.action-delete:hover {
  background: #fef2f2 !important;
}
</style>
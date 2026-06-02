<template>
  <q-page class="roles-page q-pa-md">

    <!-- CARDS SUPERIORES -->
    <div class="top-cards q-mb-md">

      <q-card class="top-card" flat>
        <q-card-section class="row items-center no-wrap q-pa-md">
          <div class="tc-icon tc-i1">
            <q-icon name="shield" size="24px" style="color:#4a8c25" />
          </div>
          <div class="q-ml-md">
            <div class="tc-title">Total Roles</div>
            <div class="tc-num">5</div>
            <div class="tc-lbl">Roles registrados</div>
          </div>
        </q-card-section>
      </q-card>

      <q-card class="top-card" flat>
        <q-card-section class="row items-center no-wrap q-pa-md">
          <div class="tc-icon tc-i2">
            <q-icon name="verified_user" size="24px" style="color:#0f6e56" />
          </div>
          <div class="q-ml-md">
            <div class="tc-title">Roles Activos</div>
            <div class="tc-num">4</div>
            <div class="tc-lbl">Roles habilitados</div>
          </div>
        </q-card-section>
      </q-card>

      <q-card class="top-card" flat>
        <q-card-section class="row items-center no-wrap q-pa-md">
          <div class="tc-icon tc-i3">
            <q-icon name="gpp_bad" size="24px" style="color:#d97b1a" />
          </div>
          <div class="q-ml-md">
            <div class="tc-title">Roles Inactivos</div>
            <div class="tc-num">1</div>
            <div class="tc-lbl">Roles deshabilitados</div>
          </div>
        </q-card-section>
      </q-card>

      <q-card class="top-card" flat>
        <q-card-section class="row items-center no-wrap q-pa-md">
          <div class="tc-icon tc-i4">
            <q-icon name="lock" size="24px" style="color:#6d28d9" />
          </div>
          <div class="q-ml-md">
            <div class="tc-title">Permisos Asignados</div>
            <div class="tc-num">28</div>
            <div class="tc-lbl">Total de permisos</div>
          </div>
        </q-card-section>
      </q-card>

    </div>

    <!-- MAIN GRID -->
    <div class="main-grid q-mb-md">

      <!-- LISTA DE ROLES -->
      <q-card class="perfil-card" flat>

        <div class="list-header">
          <div class="card-title-row">
            <q-icon name="list" class="card-title-icon" />
            Lista de Roles
          </div>
          <div class="row items-center q-gutter-sm">
            <q-input v-model="search" outlined dense placeholder="Buscar rol..." class="search-input">
              <template #append>
                <q-icon name="search" class="input-icon" />
              </template>
            </q-input>
            <q-btn label="Nuevo Rol" icon="add" class="btn-nuevo" unelevated @click="mostrarModal = true" />
          </div>
        </div>

        <q-table flat :rows="rolesFiltrados" :columns="columns" row-key="id" hide-pagination class="roles-table">
          <template #body-cell-id="props">
            <q-td :props="props">
              <span class="rol-num">{{ props.row.id }}</span>
            </q-td>
          </template>

          <template #body-cell-nombre="props">
            <q-td :props="props">
              <div class="row items-center no-wrap q-gutter-xs">
                <div :class="['rol-icon', props.row.iconClass]">
                  <q-icon :name="props.row.icon" size="16px" />
                </div>
                <span class="rol-name">{{ props.row.nombre }}</span>
              </div>
            </q-td>
          </template>

          <template #body-cell-descripcion="props">
            <q-td :props="props">
              <span class="rol-desc">{{ props.row.descripcion }}</span>
            </q-td>
          </template>

          <template #body-cell-usuarios="props">
            <q-td :props="props" class="text-center">
              <span class="rol-users">{{ props.row.usuarios }}</span>
            </q-td>
          </template>

          <template #body-cell-estado="props">
            <q-td :props="props">
              <span :class="['estado-badge', props.row.estado === 'Activo' ? 'badge-activo' : 'badge-inactivo']">
                <span :class="['badge-dot', props.row.estado === 'Activo' ? 'dot-activo' : 'dot-inactivo']"></span>
                {{ props.row.estado }}
              </span>
            </q-td>
          </template>

          <template #body-cell-acciones="props">
            <q-td :props="props">
              <div class="row no-wrap q-gutter-xs">
                <q-btn flat round dense class="act-btn act-edit" icon="edit" @click="editarRol(props.row)" />
                <q-btn flat round dense class="act-btn act-del" icon="delete" @click="eliminarRol(props.row)" />
                <q-btn flat round dense class="act-btn act-view" icon="visibility" @click="verRol(props.row)" />
              </div>
            </q-td>
          </template>
        </q-table>

        <div class="pagination-row">
          <span class="pag-info">Mostrando 1 a {{ roles.length }} de {{ roles.length }} roles</span>
          <div class="row q-gutter-xs">
            <q-btn flat dense class="pag-btn" icon="chevron_left" />
            <q-btn flat dense class="pag-btn pag-active">1</q-btn>
            <q-btn flat dense class="pag-btn" icon="chevron_right" />
          </div>
        </div>

      </q-card>

      <!-- PERMISOS DEL ROL -->
      <q-card class="perfil-card" flat>
        <div class="card-title-row q-mb-md">
          <q-icon name="lock" class="card-title-icon" />
          Permisos del Rol
        </div>

        <div class="q-mb-md">
          <div class="perm-section-lbl">Seleccionar rol</div>
          <q-select v-model="rolSeleccionado" :options="opcionesRoles" outlined dense class="perm-select-input" />
        </div>

        <div class="perm-count">
          Permisos asignados (<strong>{{ permisos.length }}</strong>)
        </div>

        <div class="perm-list">
          <div v-for="perm in permisos" :key="perm.nombre" class="perm-item">
            <div class="row items-center q-gutter-sm">
              <div class="perm-check">
                <q-icon name="check" size="11px" color="white" />
              </div>
              <span class="perm-name">{{ perm.nombre }}</span>
            </div>
            <span :class="['perm-tag', perm.tagClass]">{{ perm.modulo }}</span>
          </div>
        </div>

        <div class="ver-todos" @click="verTodosPermisos">
          <q-icon name="arrow_forward" size="14px" />
          Ver todos los permisos ({{ permisos.length }})
        </div>
      </q-card>

    </div>

    <!-- BOTTOM GRID -->
    <div class="bottom-grid">

      <!-- INFO MÓDULO -->
      <q-card class="perfil-card" flat>
        <div class="card-title-row q-mb-sm">
          <q-icon name="info_outline" class="card-title-icon" />
          Información del Módulo
        </div>
        <p class="info-desc">
          Los roles permiten agrupar permisos y asignarlos a los usuarios del sistema.
          Cada rol puede tener uno o más permisos según las necesidades del negocio.
        </p>
        <div v-for="item in infoItems" :key="item" class="info-item">
          <q-icon name="check_circle_outline" size="16px" style="color:#4a8c25;flex-shrink:0" />
          <span>{{ item }}</span>
        </div>
      </q-card>

      <!-- ESTADÍSTICAS -->
      <q-card class="perfil-card" flat>
        <div class="card-title-row q-mb-md">
          <q-icon name="pie_chart" class="card-title-icon" />
          Estadísticas
        </div>
        <div class="chart-wrap">
          <svg width="100" height="100" viewBox="0 0 100 100">
            <circle cx="50" cy="50" r="35" fill="none" stroke="#e4edd8" stroke-width="18" />
            <circle cx="50" cy="50" r="35" fill="none" stroke="#4a8c25" stroke-width="18" stroke-dasharray="22 198"
              stroke-dashoffset="0" transform="rotate(-90 50 50)" />
            <circle cx="50" cy="50" r="35" fill="none" stroke="#0f6e56" stroke-width="18" stroke-dasharray="44 176"
              stroke-dashoffset="-22" transform="rotate(-90 50 50)" />
            <circle cx="50" cy="50" r="35" fill="none" stroke="#d97b1a" stroke-width="18" stroke-dasharray="110 110"
              stroke-dashoffset="-66" transform="rotate(-90 50 50)" />
            <circle cx="50" cy="50" r="35" fill="none" stroke="#7aaa4e" stroke-width="18" stroke-dasharray="44 176"
              stroke-dashoffset="-176" transform="rotate(-90 50 50)" />
            <circle cx="50" cy="50" r="26" fill="white" />
          </svg>
          <div class="chart-legend">
            <div v-for="item in estadisticas" :key="item.nombre" class="legend-item">
              <div class="row items-center q-gutter-xs">
                <span class="legend-dot" :style="{ background: item.color }"></span>
                <span class="legend-name">{{ item.nombre }}</span>
              </div>
              <span class="legend-val">{{ item.val }}</span>
            </div>
          </div>
        </div>
      </q-card>

      <!-- ACCIONES RÁPIDAS -->
      <q-card class="perfil-card" flat>
        <div class="card-title-row q-mb-md">
          <q-icon name="flash_on" class="card-title-icon" />
          Acciones Rápidas
        </div>
        <div class="acc-list">
          <q-btn label="Nuevo Rol" icon="add" class="acc-btn ab-primary" unelevated @click="mostrarModal = true" />
          <q-btn label="Asignar Permisos" icon="security" class="acc-btn ab-teal" unelevated />
          <q-btn label="Exportar Roles" icon="download" class="acc-btn ab-orange" unelevated />
          <q-btn label="Auditoría de Roles" icon="description" class="acc-btn ab-gray" unelevated />
        </div>
      </q-card>

    </div>

    <!-- MODAL NUEVO ROL -->
    <q-dialog v-model="mostrarModal" persistent>
      <q-card class="modal-card">
        <div class="accent-bar" />
        <q-card-section class="modal-header">
          <div>
            <div class="modal-eyebrow">Gestión de Roles</div>
            <div class="modal-title">Nuevo Rol</div>
          </div>
          <q-btn flat round dense icon="close" v-close-popup class="close-btn" />
        </q-card-section>

        <q-separator style="background:#e4edd8" />

        <q-card-section class="q-pt-md">
          <q-form @submit.prevent="guardarRol" class="q-gutter-md">

            <div class="field-group">
              <label class="field-lbl">Nombre del Rol</label>
              <q-input v-model="nuevoRol.nombre" outlined dense placeholder="ej. SUPERVISOR" class="field-input">
                <template #prepend><q-icon name="shield" class="input-icon" /></template>
              </q-input>
            </div>

            <div class="field-group">
              <label class="field-lbl">Descripción</label>
              <q-input v-model="nuevoRol.descripcion" outlined dense type="textarea" rows="3"
                placeholder="Describe las responsabilidades del rol..." class="field-input" />
            </div>

            <div class="field-group">
              <label class="field-lbl">Estado</label>
              <q-select v-model="nuevoRol.estado" :options="['Activo', 'Inactivo']" outlined dense
                class="field-input" />
            </div>

            <q-separator style="background:#e4edd8" />

            <div class="row justify-end q-gutter-sm">
              <q-btn label="Cancelar" flat class="btn-cancel" v-close-popup />
              <q-btn label="Guardar Rol" type="submit" icon="save" class="btn-save" unelevated :loading="guardando">
                <template #loading><q-spinner-dots color="white" size="1em" /></template>
              </q-btn>
            </div>

          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const mostrarModal = ref(false)
const guardando = ref(false)
const search = ref('')
const rolSeleccionado = ref('ADMIN - Administrador del sistema')

const nuevoRol = ref({ nombre: '', descripcion: '', estado: 'Activo' })

const opcionesRoles = [
  'ADMIN - Administrador del sistema',
  'SUPERVISOR',
  'CAJERO',
  'ALMACENERO',
  'INVITADO'
]

const columns = [
  { name: 'id', label: '#', field: 'id', align: 'left', style: 'width:40px' },
  { name: 'nombre', label: 'Rol', field: 'nombre', align: 'left', style: 'width:130px' },
  { name: 'descripcion', label: 'Descripción', field: 'descripcion', align: 'left' },
  { name: 'usuarios', label: 'Usuarios', field: 'usuarios', align: 'center', style: 'width:80px' },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'left', style: 'width:90px' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center', style: 'width:100px' }
]

const roles = ref([
  { id: 1, nombre: 'ADMIN', descripcion: 'Administrador del sistema con acceso total a todas las funciones.', usuarios: 1, estado: 'Activo', icon: 'shield', iconClass: 'ri-teal' },
  { id: 2, nombre: 'SUPERVISOR', descripcion: 'Supervisor general con acceso a reportes y gestión operativa.', usuarios: 2, estado: 'Activo', icon: 'manage_accounts', iconClass: 'ri-green' },
  { id: 3, nombre: 'CAJERO', descripcion: 'Encargado de realizar ventas y manejo de caja.', usuarios: 5, estado: 'Activo', icon: 'point_of_sale', iconClass: 'ri-orange' },
  { id: 4, nombre: 'ALMACENERO', descripcion: 'Responsable de inventario, almacén y productos.', usuarios: 2, estado: 'Activo', icon: 'inventory_2', iconClass: 'ri-blue' },
  { id: 5, nombre: 'INVITADO', descripcion: 'Acceso limitado solo para consultas básicas.', usuarios: 0, estado: 'Inactivo', icon: 'person', iconClass: 'ri-gray' }
])

const rolesFiltrados = computed(() =>
  roles.value.filter(r =>
    r.nombre.toLowerCase().includes(search.value.toLowerCase()) ||
    r.descripcion.toLowerCase().includes(search.value.toLowerCase())
  )
)

const permisos = ref([
  { nombre: 'Ver Dashboard', modulo: 'Dashboard', tagClass: 'pt-dashboard' },
  { nombre: 'Gestionar Usuarios', modulo: 'Usuarios', tagClass: 'pt-usuarios' },
  { nombre: 'Gestionar Roles', modulo: 'Roles', tagClass: 'pt-roles' },
  { nombre: 'Gestionar Permisos', modulo: 'Permisos', tagClass: 'pt-permisos' },
  { nombre: 'Gestionar Productos', modulo: 'Productos', tagClass: 'pt-productos' },
  { nombre: 'Gestionar Ventas', modulo: 'Ventas', tagClass: 'pt-ventas' },
  { nombre: 'Gestionar Compras', modulo: 'Compras', tagClass: 'pt-compras' },
  { nombre: 'Ver Reportes', modulo: 'Reportes', tagClass: 'pt-reportes' }
])

const infoItems = [
  'Crea roles según la estructura de tu empresa',
  'Asigna permisos específicos a cada rol',
  'Controla el acceso de los usuarios a las funciones',
  'Mantén la seguridad y organización del sistema'
]

const estadisticas = [
  { nombre: 'ADMIN', color: '#4a8c25', val: '1 (20%)' },
  { nombre: 'SUPERVISOR', color: '#0f6e56', val: '2 (20%)' },
  { nombre: 'CAJERO', color: '#d97b1a', val: '5 (50%)' },
  { nombre: 'ALMACENERO', color: '#7aaa4e', val: '2 (20%)' },
  { nombre: 'INVITADO', color: '#c8e0a0', val: '0 (0%)' }
]

const guardarRol = async () => {
  guardando.value = true
  await new Promise(r => setTimeout(r, 1000))
  roles.value.push({
    id: roles.value.length + 1,
    nombre: nuevoRol.value.nombre.toUpperCase(),
    descripcion: nuevoRol.value.descripcion,
    usuarios: 0,
    estado: nuevoRol.value.estado,
    icon: 'shield',
    iconClass: 'ri-green'
  })
  guardando.value = false
  mostrarModal.value = false
  nuevoRol.value = { nombre: '', descripcion: '', estado: 'Activo' }
  $q.notify({ type: 'positive', message: 'Rol creado exitosamente' })
}

const editarRol = (rol) => {
  $q.notify({ type: 'info', message: `Editando rol: ${rol.nombre}` })
}

const eliminarRol = (rol) => {
  $q.dialog({
    title: 'Eliminar Rol',
    message: `¿Estás seguro de eliminar el rol ${rol.nombre}?`,
    cancel: true,
    ok: { label: 'Eliminar', color: 'negative' }
  }).onOk(() => {
    roles.value = roles.value.filter(r => r.id !== rol.id)
    $q.notify({ type: 'negative', message: `Rol ${rol.nombre} eliminado` })
  })
}

const verRol = (rol) => {
  $q.notify({ type: 'info', message: `Viendo rol: ${rol.nombre}` })
}

const verTodosPermisos = () => {
  $q.notify({ type: 'info', message: 'Cargando todos los permisos...' })
}
</script>

<style scoped src="../../assets/styles/roles/rol.css"></style>
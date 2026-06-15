<template>
  <q-page class="roles-page q-pa-md">
    <div class="text-h6 q-mb-sm">Gestión de Rol</div>
    <!-- CARDS SUPERIORES -->
    <div class="top-cards q-mb-md">
      <q-card class="top-card" flat>
        <q-card-section class="row items-center no-wrap q-pa-md">
          <div class="tc-icon tc-i1">
            <q-icon name="shield" size="24px" />
          </div>
          <div class="q-ml-md">
            <div class="tc-title">Total Roles</div>
            <div class="tc-num">{{ roles.length }}</div>
            <div class="tc-lbl">Roles registrados</div>
          </div>
        </q-card-section>
      </q-card>

      <q-card class="top-card" flat>
        <q-card-section class="row items-center no-wrap q-pa-md">
          <div class="tc-icon tc-i2">
            <q-icon name="verified_user" size="24px" />
          </div>
          <div class="q-ml-md">
            <div class="tc-title">Roles Activos</div>
            <div class="tc-num">{{ rolesActivos }}</div>
            <div class="tc-lbl">Roles habilitados</div>
          </div>
        </q-card-section>
      </q-card>

      <q-card class="top-card" flat>
        <q-card-section class="row items-center no-wrap q-pa-md">
          <div class="tc-icon tc-i3">
            <q-icon name="gpp_bad" size="24px" />
          </div>
          <div class="q-ml-md">
            <div class="tc-title">Roles Inactivos</div>
            <div class="tc-num">{{ rolesInactivos }}</div>
            <div class="tc-lbl">Roles deshabilitados</div>
          </div>
        </q-card-section>
      </q-card>

      <q-card class="top-card" flat>
        <q-card-section class="row items-center no-wrap q-pa-md">
          <div class="tc-icon tc-i4">
            <q-icon name="lock" size="24px" />
          </div>
          <div class="q-ml-md">
            <div class="tc-title">Permisos</div>
            <div class="tc-num">{{ totalPermisos }}</div>
            <div class="tc-lbl">Total de permisos</div>
          </div>
        </q-card-section>
      </q-card>
    </div>

    <!-- TABLA DE ROLES -->
    <q-card class="table-card" flat bordered>
      <div class="table-header">
        <div>
          <div class="text-h6 text-weight-bold table-title">Lista de Roles</div>
          <div class="text-caption table-subtitle">Administra los roles del sistema</div>
        </div>
        <div class="row items-center q-gutter-sm">
          <q-input v-model="search" outlined dense debounce="300" placeholder="Buscar rol..." class="search-input">
            <template #append>
              <q-icon name="search" class="search-icon" />
            </template>
          </q-input>
          <q-btn label="Nuevo Rol" icon="add" class="btn-nuevo" unelevated @click="abrirDialog" />
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
              <div :class="['rol-icon', getRolIconClass(props.row.nombre)]">
                <q-icon :name="getRolIcon(props.row.nombre)" size="16px" />
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
            <q-chip dense :color="props.row.usuarios > 0 ? 'green-7' : 'grey-6'" text-color="white" size="sm">
              {{ props.row.usuarios }}
            </q-chip>
          </q-td>
        </template>

        <template #body-cell-estado="props">
          <q-td :props="props">
            <span :class="['estado-badge', props.row.activo ? 'badge-activo' : 'badge-inactivo']">
              <span :class="['badge-dot', props.row.activo ? 'dot-activo' : 'dot-inactivo']"></span>
              {{ props.row.activo ? 'Activo' : 'Inactivo' }}
            </span>
          </q-td>
        </template>

        <template #body-cell-acciones="props">
          <q-td :props="props">
            <div class="row no-wrap q-gutter-xs">
              <q-btn flat round dense class="act-btn act-view" icon="visibility" @click="verRol(props.row)" />
              <q-btn flat round dense class="act-btn act-edit" icon="edit" @click="editarRol(props.row)" />
              <q-btn flat round dense class="act-btn act-del" icon="delete" @click="eliminarRol(props.row)" />
            </div>
          </q-td>
        </template>
      </q-table>
    </q-card>
    
    <EditarRol
     v-model="mostrarModal"
     @guardar="onGuardarNuevo"
    />
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted  } from 'vue'
import { useQuasar } from 'quasar'
import { listarRoles} from '../../api/rol/rol'
import EditarRol from '../../components/rol/EditarRol.vue' 

const $q = useQuasar()
const mostrarModal = ref(false)
const mostrarVer = ref(false)
const mostrarEditar = ref(false)
const guardando = ref(false)
const search = ref('')
const rolSeleccionado = ref({})
const roles = ref([])


const columns = [
  { name: 'id', label: '#', field: 'id', align: 'left' },
  { name: 'nombre', label: 'Rol', field: 'nombre', align: 'left', style: 'width:150px' },
  { name: 'descripcion', label: 'Descripción', field: 'descripcion', align: 'left' },
  { name: 'usuarios', label: 'Usuarios', field: 'usuarios', align: 'center', style: 'width:100px' },
  { name: 'estado', label: 'Estado', field: 'activo', align: 'left', style: 'width:100px' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center', style: 'width:120px' }
]

const abrirDialog = () => {
  mostrarModal.value = true
}

//cargar los dato de roles
const cargarRoles = async () => {
  try {
    const respuesta = await listarRoles()
    roles.value = respuesta.map(r => ({
      id: r.idRol,
      nombre: (r.nombre || '__').replace('ROLE_',''),
      descripcion: (r.descripcion || "__"),
      usuarios: r.cantidadUsuarios,
      activo: r.estado
    }))

  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Error al cargar roles'
    })
  }
}



const rolesActivos = computed(() => roles.value.filter(r => r.activo).length)
const rolesInactivos = computed(() => roles.value.filter(r => !r.activo).length)
const totalPermisos = computed(() => 28) // Ajustar según tu backend

const rolesFiltrados = computed(() =>
  roles.value.filter(r =>
    (r.nombre || '').toLowerCase().includes(search.value.toLowerCase()) ||
    (r.descripcion || '').toLowerCase().includes(search.value.toLowerCase())
  )
)

//icons
const iconosEstaticos = ['shield', 'manage_accounts', 'point_of_sale', 'inventory_2', 'person', 'work', 'star'];
const coloresEstaticos = ['ri-green', 'ri-teal', 'ri-orange', 'ri-blue', 'ri-gray'];

const obtenerIndice = (nombre, maximo) => {
  const rolLimpio = (nombre || '').toUpperCase().replace('ROLE_', '');
  let suma = 0;
  for (let i = 0; i < rolLimpio.length; i++) {
    suma += rolLimpio.charCodeAt(i);
  }
  return suma % maximo;
}
//icons
const getRolIcon = (nombre) => {
  const indice = obtenerIndice(nombre, iconosEstaticos.length);
  return iconosEstaticos[indice];
}

//colores
const getRolIconClass = (nombre) => {
  const indice = obtenerIndice(nombre, coloresEstaticos.length);
  return coloresEstaticos[indice];
}



const verRol = (rol) => {
  rolSeleccionado.value = { ...rol }
  mostrarVer.value = true
}

const editarRol = (rol) => {
  rolSeleccionado.value = { ...rol }
  mostrarEditar.value = true
}

const onGuardarEditar = (data) => {
  const index = roles.value.findIndex(r => r.id === data.id)
  if (index >= 0) {
    roles.value[index] = { ...roles.value[index], ...data }
    $q.notify({ type: 'positive', message: 'Rol actualizado correctamente' })
  }
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
onMounted(() => {
  cargarRoles()
})

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

.roles-page {
  background: #f5f7f0;
  font-family: 'Nunito', sans-serif;
}

/* Cards superiores */
.top-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.top-card {
  background: #ffffff;
  border: 1px solid #e4edd8;
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(42,92,26,0.06);
  transition: all 0.2s;
}
.top-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(42,92,26,0.12);
}

.tc-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tc-i1 { background: #eaf4d8; color: #4a8c25; }
.tc-i2 { background: #e0f2ec; color: #0f6e56; }
.tc-i3 { background: #fef3e2; color: #d97b1a; }
.tc-i4 { background: #f3e8ff; color: #6d28d9; }

.tc-title {
  font-size: 12px;
  font-weight: 700;
  color: #9dbf78;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-family: 'Nunito', sans-serif;
}
.tc-num {
  font-size: 24px;
  font-weight: 900;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
  margin: 2px 0;
}
.tc-lbl {
  font-size: 11px;
  font-weight: 600;
  color: #7aaa4e;
  font-family: 'Nunito', sans-serif;
}

/* Tabla */
.table-card {
  background: #ffffff;
  border: 1px solid #e4edd8;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(42,92,26,0.08);
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #f0f7e8;
  border-bottom: 1px solid #c8e0a0;
}
.table-title {
  font-size: 16px;
  font-weight: 900;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}
.table-subtitle {
  font-size: 12px;
  font-weight: 600;
  color: #7aaa4e;
  font-family: 'Nunito', sans-serif;
}

.search-input :deep(.q-field__control) {
  border-radius: 10px;
  background: #ffffff;
  border: 1px solid #c8e0a0;
}
.search-input :deep(.q-field__control:focus-within) {
  border-color: #82bd43;
  box-shadow: 0 0 0 3px rgba(130,189,67,0.15);
}
.search-icon {
  color: #9dbf78;
}

.btn-nuevo {
  background: #82bd43;
  color: #ffffff;
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  border-radius: 9px;
  box-shadow: 0 4px 14px rgba(74,140,37,0.3);
}
.btn-nuevo:hover {
  background: #4a8c25;
}

/* Tabla */
.roles-table :deep(th) {
  font-size: 10px;
  font-weight: 800;
  color: #5a8040;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  background: #f7f9f4;
  padding: 12px 16px;
  font-family: 'Nunito', sans-serif;
}
.roles-table :deep(td) {
  padding: 12px 16px;
  font-family: 'Nunito', sans-serif;
}
.roles-table :deep(tr:hover) {
  background: #f0f7e8;
}

.rol-num {
  font-size: 13px;
  font-weight: 800;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}

.rol-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.ri-green { background: #eaf4d8; color: #4a8c25; }
.ri-teal { background: #e0f2ec; color: #0f6e56; }
.ri-orange { background: #fef3e2; color: #d97b1a; }
.ri-blue { background: #e3f2fd; color: #1976d2; }
.ri-gray { background: #f0f0f0; color: #757575; }

.rol-name {
  font-size: 14px;
  font-weight: 800;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}
.rol-desc {
  font-size: 13px;
  font-weight: 600;
  color: #5a8040;
  font-family: 'Nunito', sans-serif;
}

/* Estado badges */
.estado-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
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

/* Botones de acción */
.act-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  transition: all 0.2s;
}
.act-view {
  color: #7aaa4e;
  background: #f0f7e8;
}
.act-view:hover { background: #ddecc5; color: #4a8c25; }
.act-edit {
  color: #d97b1a;
  background: #fef3e2;
}
.act-edit:hover { background: #f5dbb8; color: #a05c10; }
.act-del {
  color: #c62828;
  background: #ffebee;
}
.act-del:hover { background: #ffcdd2; color: #b71c1c; }

/* Modal */
.modal-card {
  width: 100%;
  max-width: 480px;
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
.modal-header {
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
.modal-icon {
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
.modal-eyebrow {
  font-size: 11px;
  font-weight: 600;
  color: #7aaa4e;
  font-family: 'Nunito', sans-serif;
}
.modal-title {
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

.field-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.field-lbl {
  font-size: 11px;
  font-weight: 800;
  color: #5a8040;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-family: 'Nunito', sans-serif;
  display: flex;
  align-items: center;
  gap: 6px;
}
.label-icon {
  color: #82bd43;
}
.field-input :deep(.q-field__control) {
  border-radius: 10px;
  background: #f7f9f4;
  border: 1px solid #e4edd8;
}
.field-input :deep(.q-field__control:focus-within) {
  border-color: #82bd43;
  box-shadow: 0 0 0 3px rgba(130,189,67,0.15);
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
.btn-save {
  background: #82bd43;
  color: #fff;
  border-radius: 9px;
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  box-shadow: 0 4px 14px rgba(74,140,37,0.3);
}
.btn-save:hover { background: #4a8c25; }

/* Responsive */
@media (max-width: 1200px) {
  .top-cards { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .top-cards { grid-template-columns: 1fr; }
  .table-header { flex-direction: column; gap: 12px; }
}
</style>
<template>
  <q-page class="q-pa-md page-bg">
    <CardsUsuario />

    <q-card flat bordered class="table-card">

      <div class="row items-center justify-between q-pa-md q-col-gutter-md table-header">
        <div>
          <div class="text-h6 text-weight-bold table-title">Lista de Usuarios</div>
          <div class="text-caption table-subtitle">Administra y consulta los usuarios del sistema</div>
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

      <q-table flat :rows="usuarios" :columns="columns" row-key="idUsuario" :loading="loading"
        v-model:pagination="pagination" @request="onRequest" rows-per-page-label="Usuarios por página"
        :pagination-label="getPaginationLabel" :rows-per-page-options="[10, 20, 30, 40, 50]" class="custom-table">

        <template v-slot:body-cell-nombreCompleto="props">
          <q-td :props="props">
            <div class="row items-center no-wrap q-gutter-sm">
              <q-avatar size="38px" class="user-avatar color-avatar" color="primary" text-color="white">
                {{ props.row.nombreCompleto ? props.row.nombreCompleto.charAt(0).toUpperCase() : 'U' }}
              </q-avatar>
              <span class="text-weight-medium user-name">
                {{ props.row.nombreCompleto }}
              </span>
            </div>
          </q-td>
        </template>

        <template v-slot:body-cell-rol="props">
          <q-td :props="props">
            <q-chip dense :color="getRolColor(props.row.rol)" text-color="white"
              style="font-size: 13px; padding: 6px 14px !important; border-radius: 20px; font-weight: 500;">
              {{ formatRol(props.row.rol) }}
            </q-chip>
          </q-td>
        </template>

        <template v-slot:body-cell-activo="props">
          <q-td :props="props">
            <span :class="['estado-badge', props.row.activo ? 'estado-activo' : 'estado-inactivo']">
              <span class="estado-dot"></span>
              {{ props.row.activo ? 'Activo' : 'Inactivo' }}
            </span>
          </q-td>
        </template>

        <template v-slot:body-cell-ultimoAcceso="props">
          <q-td :props="props">
            {{ formatFecha(props.row.ultimoAcceso) }}
          </q-td>
        </template>

        <template v-slot:body-cell-acciones="props">
          <q-td :props="props">
            <div class="row no-wrap q-gutter-xs">
              <q-btn flat round dense class="action-btn action-view" icon="visibility" />
              <q-btn flat round dense class="action-btn action-edit" icon="edit" />
              <!-- DESACTIVAR (solo si está activo) -->
              <q-btn v-if="props.row.activo" flat round dense class="action-btn action-delete" icon="block" color="red"
                @click="desactivarUsuarios(props.row.idUsuario, props.row.username, props.row.rol)" />
              <!-- ACTIVAR (solo si está inactivo) -->
              <q-btn v-else flat round dense class="action-btn" icon="check" color="green"
                @click="activarUsuarios(props.row.idUsuario, props.row.username, props.row.rol)" />

            </div>
          </q-td>
        </template>
        
      </q-table>
    </q-card>
    
    <q-dialog v-model="mostrarModal" persistent>
      <UsuarioForm @cerrar="mostrarModal = false" />
    </q-dialog>
      <!-- Agrega al final antes de cerrar q-page -->
    <DesactivarUsuarioDialog
      v-model="mostrarDesactivar"
      :id="usuarioSeleccionado.id"
      :nombre="usuarioSeleccionado.nombre"
      :rol="usuarioSeleccionado.rol"
      @confirmar="onConfirmarDesactivar"
    />
    <!-- dialogo de activar usuario -->
    <ActivarUsuarioDialog
      v-model="mostrarActivar"
      :id="usuarioSeleccionado.id"
      :nombre="usuarioSeleccionado.nombre"
      :rol="usuarioSeleccionado.rol"
      @confirmar="onConfirmarActivar"
    />
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import CardsUsuario from '../../components/usuarios/CardsUsuario.vue'
import UsuarioForm from '../../components/usuarios/UsuarioForm.vue'
import DesactivarUsuarioDialog from '../../components/usuarios/DesactivarUsuarioDialog.vue'
import ActivarUsuarioDialog from '../../components/usuarios/ActivarUsuarioDialog.vue'
import { listarUsuarios, desactivarUsuario, activarUsuario } from '../../api/usuario/usuario'

const mostrarModal = ref(false)
const mostrarDesactivar = ref(false)
const mostrarActivar = ref(false)
const usuarioSeleccionado = ref({id:null, nombre:'',rol:''})
const search = ref('')
const loading = ref(false)
const usuarios = ref([])
const $q = useQuasar()

const columns = [
  { name: 'idUsuario', label: 'ID', field: 'idUsuario', align: 'left' },
  { name: 'nombreCompleto', label: 'Usuario', field: 'nombreCompleto', align: 'left' },
  { name: 'username', label: 'Username', field: 'username', align: 'left' },
  { name: 'correo', label: 'Correo', field: 'correo', align: 'left' },
  { name: 'rol', label: 'Rol', field: 'rol', align: 'left' },
  { name: 'activo', label: 'Estado', field: 'activo', align: 'left' },
  { name: 'ultimoAcceso', label: 'Último Acceso', field: 'ultimoAcceso', align: 'left' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
]

const getPaginationLabel = (firstRowIndex, endRowIndex, totalRowsNumber) => {
  return `${firstRowIndex}-${endRowIndex} de ${totalRowsNumber}`
}

const pagination = ref({
  sortBy: 'idUsuario',
  descending: false,
  page: 1,
  rowsPerPage: 5,
  rowsNumber: 0
})

// Quita el prefijo ROLE_ y capitaliza, ej: ROLE_ADMIN → Admin
const formatRol = (rol) => {
  if (!rol) return '—'
  const sinPrefijo = rol.replace('ROLE_', '')
  return sinPrefijo.charAt(0).toUpperCase() + sinPrefijo.slice(1).toLowerCase()
}

// Asigna color automático y estable según el string del rol
const getRolColor = (rol) => {
  if (!rol) return 'grey-6'
  const colores = ['blue-8', 'green-7', 'deep-purple-6', 'amber-8', 'teal-7', 'pink-6', 'orange-8']
  const indice = rol
    .split('')
    .reduce((acc, char) => acc + char.charCodeAt(0), 0) % colores.length
  return colores[indice]
}

// Formatea fecha como dd/mm/aaaa, hh:mm
const formatFecha = (fecha) => {
  if (!fecha) return '—'
  return new Date(fecha).toLocaleString('es-ES', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const cargarUsuarios = async (page = 1, size = 5) => {

  if (!page || isNaN(page)) page = 1
  if (!size || isNaN(size)) size = 5

  const respuesta = await listarUsuarios(page - 1, size)

  usuarios.value = respuesta.content

  pagination.value.page = page
  pagination.value.rowsPerPage = size
  pagination.value.rowsNumber = respuesta.totalElements
}

//actualizado con el dialogo
const activarUsuarios = (id, nombre, rol = '') => {
  usuarioSeleccionado.value = {
    id,
    nombre,
    rol
  }
  mostrarActivar.value = true
}

//  ACTIVAR USUARIO la confirmacion
const onConfirmarActivar = async (id) => {
  try {
    await activarUsuario(id)

    $q.notify({
      type: 'positive',
      message: 'Usuario activado correctamente'
    })

    await cargarUsuarios()

  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Error al activar usuario'
    })
  }
}

//  DESACTIVAR USUARIO
const desactivarUsuarios = (id, nombre, rol = '') => {
  usuarioSeleccionado.value = { id, nombre, rol }
  mostrarDesactivar.value = true
}

// agrega este método
const onConfirmarDesactivar = async (id) => {
  try {
    await desactivarUsuario(id)
    $q.notify({ type: 'positive', message: 'Usuario desactivado correctamente' })
    await cargarUsuarios()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Error al desactivar usuario'
    })
  }
}

const onRequest = (props) => {
  const { page, rowsPerPage } = props.pagination
  cargarUsuarios(page, rowsPerPage)
}

onMounted(() => {
  cargarUsuarios(pagination.value.page, pagination.value.rowsPerPage)
})
</script>

<style scoped src="../../assets/styles/user/userTable.css"></style>
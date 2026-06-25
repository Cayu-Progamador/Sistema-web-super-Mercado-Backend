<template>
  <q-page class="q-pa-md page-bg">
    <CardsUsuario :key="dashboardKey" />

    <FiltroUsuario @buscar="onBuscar" @limpiar="onLimpiar" @roles-cargados="onRolesCargados" />

    <q-card flat bordered class="table-card">
      <div class="row items-center justify-between q-pa-md table-header">
        <div>
          <div class="text-h6 text-weight-bold table-title">Lista de Usuarios</div>
          <div class="text-caption table-subtitle">Administra y consulta los usuarios del sistema</div>
        </div>
        <div class="row items-center q-gutter-sm">
          <q-btn class="btn-add" icon="picture_as_pdf" label="PDF" @click="exportarPDF" unelevated color="red" />
          <q-btn class="btn-add" icon="table_chart" label="Excel" @click="exportarExcel" unelevated color="green" />
          <q-btn class="btn-add" icon="add" label="Nuevo Usuario" @click="mostrarModal = true" unelevated />
        </div>
      </div>

      <q-table flat :rows="usuarios" :columns="columns" row-key="idUsuario" :loading="loading"
        v-model:pagination="pagination" hide-pagination class="custom-table">

        <template v-slot:body-cell-numero="props">
          <q-td :props="props">
            {{
              ((pagination.page - 1) * pagination.rowsPerPage) +
              props.rowIndex + 1
            }}
          </q-td>
        </template>

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
              <q-btn flat round dense class="action-btn action-view" icon="visibility" @click="verUsuario(props.row)"/>

              <q-btn flat round dense class="action-btn action-edit" icon="edit" @click="editarUsuario(props.row)" />

              <q-btn v-if="props.row.activo" flat round dense class="action-btn action-delete" icon="block" color="red"
                @click="confirmarAccion('desactivar', props.row.idUsuario, props.row.username, props.row.rol)" />
              <q-btn v-else flat round dense class="action-btn" icon="check" color="green"
                @click="confirmarAccion('activar', props.row.idUsuario, props.row.username, props.row.rol)" />
            </div>
          </q-td>
        </template>

        <template #bottom>
          <div class="q-table__bottom row items-center q-pa-md">
            <div class="q-table__control">
              <span class="q-table__bottom-item">Rows per page:</span>
              <q-select
                v-model="pagination.rowsPerPage"
                :options="[5, 10, 15, 25, 30, 50]"
                dense
                borderless
                emit-value
                map-options
                class="q-table__select inline-block"
                style="min-width: 70px"
                @update:model-value="val => onRowsPerPageChange(val)"
              />
            </div>
            <span class="q-table__bottom-item q-ml-auto">
              {{ pagination.rowsNumber === 0 ? '0' : (pagination.page - 1) * pagination.rowsPerPage + 1 }}-{{ Math.min(pagination.page * pagination.rowsPerPage, pagination.rowsNumber) }} of {{ pagination.rowsNumber }}
            </span>
            <div class="q-table__control q-ml-sm">
              <q-btn dense flat round icon="first_page" :disable="pagination.page <= 1" @click="goToPage(1)" />
              <q-btn dense flat round icon="chevron_left" :disable="pagination.page <= 1" @click="goToPage(pagination.page - 1)" />
              <q-btn dense flat round icon="chevron_right" :disable="pagination.page >= Math.ceil(pagination.rowsNumber / pagination.rowsPerPage)" @click="goToPage(pagination.page + 1)" />
              <q-btn dense flat round icon="last_page" :disable="pagination.page >= Math.ceil(pagination.rowsNumber / pagination.rowsPerPage)" @click="goToPage(Math.ceil(pagination.rowsNumber / pagination.rowsPerPage))" />
            </div>
          </div>
        </template>

      </q-table>
    </q-card>

    <q-dialog v-model="mostrarModal" persistent>
      <UsuarioForm @cerrar="mostrarModal = false" />
    </q-dialog>

    <ConfirmarUsuarioDialog v-model="mostrarConfirmar" :id="usuarioSeleccionado.id"
      :nombre="usuarioSeleccionado.nombre" :rol="usuarioSeleccionado.rol" :tipo="tipoConfirmar"
      @confirmar="onConfirmarAccion" />

    <EditarUsuarioDialog v-model="mostrarEditar" :id="usuarioSeleccionado.id" :username="usuarioSeleccionado.username"
      :roles="usuarioSeleccionado.roles" :empleado-id="usuarioSeleccionado.empleadoId" :empleados="listaEmpleados"
      :roles-disponibles="listaRoles" @guardar="onConfirmarEditar" />

    <DetalleUsuario
      v-model="mostrarVer"
      :id-usuario="usuarioSeleccionado.id"
    />

  </q-page>
</template>

<script setup>
  import DetalleUsuario from '../../components/usuarios/DetalleUsuarios.vue'
  import CardsUsuario from '../../components/usuarios/CardsUsuario.vue'
  import UsuarioForm from '../../components/usuarios/UsuarioForm.vue'
  import ConfirmarUsuarioDialog from '../../components/usuarios/ConfirmarUsuarioDialog.vue'
  import EditarUsuarioDialog from '../../components/usuarios/EditarUsuarioDialog.vue'
  import FiltroUsuario from '../../components/usuarios/FiltroUsuario.vue'
  import { getEmpleadoListaEditar, getEmpleadoLista } from '../../api/empleado/empleado'
  import { listarUsuarios, desactivarUsuario, activarUsuario, actualizarUsuario, filtrarUsuarios, exportarUsuarios, exportarUsuariosPDF as exportPDF, exportarUsuariosExcel as exportExcel } from '../../api/usuario/usuario'
  import { ref, onMounted } from 'vue'
  import { useQuasar } from 'quasar'
  import { useAuthStore } from '../../store/store'

  const mostrarModal = ref(false)
  const mostrarConfirmar = ref(false)
  const tipoConfirmar = ref('activar')
  const usuarioSeleccionado = ref({
    id: null,
    nombre: '',
    username: '',
    roles: [],
    empleadoId: null
  })

  const filtrosActivos = ref({})
  const listaRoles = ref([])

  const loading = ref(false)
  const usuarios = ref([])

  const dashboardKey = ref(0)

  const mostrarEditar = ref(false)
  const listaEmpleados = ref([])

  const mostrarVer = ref(false)

  const $q = useQuasar()

  const columns = [
    {
      name: 'numero',
      label: 'N°',
      align: 'center'
    },
    { name: 'nombreCompleto', label: 'Empleado', field: 'nombreCompleto', align: 'left' },
    { name: 'username', label: 'usuario', field: 'username', align: 'left' },
    { name: 'correo', label: 'Correo', field: 'correo', align: 'left' },
    { name: 'rol', label: 'Rol', field: 'rol', align: 'left' },
    { name: 'activo', label: 'Estado', field: 'activo', align: 'left' },
    { name: 'ultimoAcceso', label: 'Último Acceso', field: 'ultimoAcceso', align: 'left' },
    { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
  ]

  const pagination = ref({
    sortBy: null,
    descending: false,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
  })

  const formatRol = (rol) => {
    if (!rol) return '—'
    const sinPrefijo = rol.replace('ROLE_', '')
    return sinPrefijo.charAt(0).toUpperCase() + sinPrefijo.slice(1).toLowerCase()
  }

  const getRolColor = (rol) => {
    if (!rol) return 'grey-6'
    const colores = ['blue-8', 'green-7', 'deep-purple-6', 'amber-8', 'teal-7', 'pink-6', 'orange-8']
    const indice = rol
      .split('')
      .reduce((acc, char) => acc + char.charCodeAt(0), 0) % colores.length
    return colores[indice]
  }

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

  const verUsuario = (row) => {
    usuarioSeleccionado.value = {
      id: row.idUsuario
    }
    mostrarVer.value = true
  }

  const cargarEmpleados = async () => {
    try {
      const respuesta = await getEmpleadoLista()
      listaEmpleados.value = respuesta
    } catch (error) {
      console.error('Error cargando empleados:', error)
    }
  }

  const editarUsuario = async (row) => {
    usuarioSeleccionado.value = {
      id: row.idUsuario,
      nombre: row.nombreCompleto || row.username,
      username: row.username,
      rol: row.rol,
      roles: row.rol ? [row.rol] : [],
      empleadoId: row.empleadoId || null
    }

    try {
      const respuesta = await getEmpleadoListaEditar(row.idUsuario)
      listaEmpleados.value = respuesta
    } catch (error) {
      console.error('Error cargando empleados:', error)
    }

    mostrarEditar.value = true
  }

  const onConfirmarEditar = async (data) => {
    try {
      await actualizarUsuario(data.id, {
        username: data.username,
        password: data.password,
        empleadoId: data.empleadoId,
        roles: data.roles
      })

      $q.notify({
        type: 'positive',
        message: 'Usuario actualizado correctamente'
      })

      await cargarUsuarios()
      dashboardKey.value++

    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || 'Error al actualizar usuario'
      })
    }
  }

  const cargarUsuarios = async () => {
    const { page, rowsPerPage } = pagination.value
    loading.value = true
    try {
      const pageIndex = Number(page) - 1
      const size = Number(rowsPerPage) || 10
      const params = {
        page: pageIndex,
        size,
        ...filtrosActivos.value
      }
      const tieneFiltros = Object.keys(filtrosActivos.value).length > 0
      const respuesta = tieneFiltros
        ? await filtrarUsuarios(params)
        : await listarUsuarios(pageIndex, size)
      usuarios.value = respuesta.content
      pagination.value = {
        page: respuesta.number + 1,
        rowsPerPage: respuesta.size,
        rowsNumber: respuesta.totalElements,
        sortBy: null,
        descending: false
      }
    } catch (error) {
      $q.notify({ type: 'negative', message: 'Error al cargar usuarios' })
    } finally {
      loading.value = false
    }
  }

  const onBuscar = (params) => {
    filtrosActivos.value = params
    pagination.value.page = 1
    cargarUsuarios()
  }

  const onLimpiar = () => {
    filtrosActivos.value = {}
    pagination.value.page = 1
    cargarUsuarios()
  }

  const onRolesCargados = (roles) => {
    listaRoles.value = roles
  }

  const authStore = useAuthStore()

  const downloadBlob = (blob, filename) => {
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    a.click()
    URL.revokeObjectURL(url)
  }

  const exportarPDF = async () => {
    try {
      const blob = await exportPDF(filtrosActivos.value)
      downloadBlob(blob, 'reporte_usuarios.pdf')
    } catch (error) {
      console.error('Error exportar PDF:', error)
      $q.notify({ type: 'negative', message: error.message || 'Error al exportar PDF' })
    }
  }

  const exportarExcel = async () => {
    try {
      const blob = await exportExcel(filtrosActivos.value)
      downloadBlob(blob, 'reporte_usuarios.xlsx')
    } catch (error) {
      console.error('Error exportar Excel:', error)
      $q.notify({ type: 'negative', message: error.message || 'Error al exportar Excel' })
    }
  }

  const goToPage = (page) => {
    pagination.value.page = page
    cargarUsuarios()
  }

  const onRowsPerPageChange = (val) => {
    pagination.value.rowsPerPage = val
    pagination.value.page = 1
    cargarUsuarios()
  }

  const confirmarAccion = (tipo, id, nombre, rol = '') => {
    tipoConfirmar.value = tipo
    usuarioSeleccionado.value = { id, nombre, rol }
    mostrarConfirmar.value = true
  }

  const onConfirmarAccion = async (id) => {
    const esActivar = tipoConfirmar.value === 'activar'
    try {
      if (esActivar) {
        await activarUsuario(id)
      } else {
        await desactivarUsuario(id)
      }
      $q.notify({
        type: 'positive',
        message: esActivar ? 'Usuario activado correctamente' : 'Usuario desactivado correctamente'
      })
      await cargarUsuarios()
      dashboardKey.value++
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || `Error al ${esActivar ? 'activar' : 'desactivar'} usuario`
      })
    }
  }

  onMounted(() => {
    cargarUsuarios()
    cargarEmpleados()
  })
</script>

<style scoped src="../../assets/styles/user/userTable.css"></style>

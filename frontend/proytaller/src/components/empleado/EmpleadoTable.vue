<template>
  <q-page class="q-pa-md page-bg">
    <q-card flat bordered class="table-card">
      <div class="row items-center justify-between q-pa-md table-header">
        <div>
          <div class="text-h6 text-weight-bold table-title">Lista de Empleados</div>
          <div class="text-caption table-subtitle">Administra y consulta los empleados del sistema</div>
        </div>
        <div class="row items-center q-gutter-sm">
          <q-btn class="btn-add" icon="add" label="Nuevo Empleado" @click="mostrarForm = true" unelevated />
        </div>
      </div>

      <div class="row q-px-md q-pb-sm q-gutter-sm items-center">
        <q-input
          v-model="search"
          dense
          outlined
          placeholder="Buscar por nombre, cargo o teléfono..."
          clearable
          class="col-12 col-sm-5"
          @update:model-value="onSearchChange"
        >
          <template v-slot:prepend>
            <q-icon name="search" />
          </template>
        </q-input>
        <q-select
          v-model="filtroEstado"
          :options="estadoOptions"
          dense
          outlined
          clearable
          placeholder="Estado"
          class="col-6 col-sm-2"
          @update:model-value="cargarEmpleados"
        />
      </div>

      <q-table
        flat
        :rows="empleados"
        :columns="columns"
        row-key="idEmpleado"
        :loading="loading"
        v-model:pagination="pagination"
        hide-pagination
        class="custom-table"
        @update:pagination="onSortChange"
      >
        <template v-slot:body-cell-numero="props">
          <q-td :props="props">
            {{ ((pagination.page - 1) * pagination.rowsPerPage) + props.rowIndex + 1 }}
          </q-td>
        </template>

        <template v-slot:body-cell-nombreCompleto="props">
          <q-td :props="props">
            <div class="row items-center no-wrap q-gutter-sm">
              <q-avatar size="38px" color="primary" text-color="white">
                {{ props.row.nombreCompleto ? props.row.nombreCompleto.charAt(0).toUpperCase() : 'E' }}
              </q-avatar>
              <span class="text-weight-medium">
                {{ props.row.nombreCompleto }}
              </span>
            </div>
          </q-td>
        </template>

        <template v-slot:body-cell-estado="props">
          <q-td :props="props">
            <span :class="['estado-badge', props.row.estado ? 'estado-activo' : 'estado-inactivo']">
              <span class="estado-dot"></span>
              {{ props.row.estado ? 'Activo' : 'Inactivo' }}
            </span>
          </q-td>
        </template>

        <template v-slot:body-cell-acciones="props">
          <q-td :props="props">
            <div class="row no-wrap q-gutter-xs">
              <q-btn flat round dense class="action-btn action-edit" icon="edit" @click="editarEmpleado(props.row)" />
              <q-btn
                v-if="props.row.estado"
                flat round dense class="action-btn action-delete"
                icon="block" color="red"
                @click="confirmarAccion('desactivar', props.row)"
              />
              <q-btn
                v-else
                flat round dense class="action-btn"
                icon="check" color="green"
                @click="confirmarAccion('activar', props.row)"
              />
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
                dense borderless emit-value map-options
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

    <q-dialog v-model="mostrarForm" persistent>
      <EmpleadoForm
        :empleado="empleadoSeleccionado"
        :es-editar="esEditar"
        @guardar="onGuardar"
        @cerrar="cerrarForm"
      />
    </q-dialog>

    <ConfirmarEmpleadoDialog
      v-model="mostrarConfirmar"
      :id="empleadoSeleccionado.id"
      :nombre="empleadoSeleccionado.nombre"
      :tipo="tipoConfirmar"
      @confirmar="onConfirmarAccion"
    />
  </q-page>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { listarEmpleados, desactivarEmpleado, activarEmpleado } from '../../api/empleado/empleado'
import EmpleadoForm from './EmpleadoForm.vue'
import ConfirmarEmpleadoDialog from './ConfirmarEmpleadoDialog.vue'

const props = defineProps({
  externalFilters: {
    type: Object,
    default: null
  }
})

const $q = useQuasar()
const loading = ref(false)
const empleados = ref([])
const mostrarForm = ref(false)
const mostrarConfirmar = ref(false)
const esEditar = ref(false)
const tipoConfirmar = ref('activar')
const empleadoSeleccionado = ref({ id: null, nombre: '' })

const search = ref('')
const filtroEstado = ref(null)
const filtroCargo = ref(null)
const filtroFechaDesde = ref(null)
const filtroFechaHasta = ref(null)
const filtroOrden = ref(null)
let searchTimeout = null

const estadoOptions = [
  { label: 'Activo', value: true },
  { label: 'Inactivo', value: false }
]

const columns = [
  { name: 'numero', label: 'N&deg;', align: 'center' },
  { name: 'nombreCompleto', label: 'Nombre', field: 'nombreCompleto', align: 'left', sortable: true },
  { name: 'cargo', label: 'Cargo', field: 'cargo', align: 'left', sortable: true },
  { name: 'telefono', label: 'Tel&eacute;fono', field: 'telefono', align: 'left' },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'left', sortable: true },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
]

const pagination = ref({
  sortBy: 'nombreCompleto',
  descending: false,
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
})

const cargarEmpleados = async () => {
  const { page, rowsPerPage, sortBy, descending } = pagination.value
  loading.value = true
  try {
    const pageIndex = Number(page) - 1
    const size = Number(rowsPerPage) || 10
    const params = {
      page: pageIndex,
      size,
      sortBy,
      sortDir: descending ? 'desc' : 'asc'
    }
    if (search.value?.trim()) {
      params.busqueda = search.value.trim()
    }
    if (filtroEstado.value !== null && filtroEstado.value !== undefined) {
      params.estado = filtroEstado.value
    }
    if (filtroCargo.value) {
      params.cargo = filtroCargo.value
    }
    if (filtroFechaDesde.value) {
      params.fechaDesde = filtroFechaDesde.value
    }
    if (filtroFechaHasta.value) {
      params.fechaHasta = filtroFechaHasta.value
    }
    if (filtroOrden.value === 'antiguos') {
      params.sortBy = 'fechaCreacion'
      params.sortDir = 'asc'
    } else if (filtroOrden.value === 'recientes') {
      params.sortBy = 'fechaCreacion'
      params.sortDir = 'desc'
    } else if (filtroOrden.value === 'nombre-asc') {
      params.sortBy = 'nombreCompleto'
      params.sortDir = 'asc'
    } else if (filtroOrden.value === 'nombre-desc') {
      params.sortBy = 'nombreCompleto'
      params.sortDir = 'desc'
    }
    const respuesta = await listarEmpleados(params)
    empleados.value = respuesta.content
    pagination.value = {
      page: respuesta.number + 1,
      rowsPerPage: respuesta.size,
      rowsNumber: respuesta.totalElements,
      sortBy: sortBy,
      descending: descending
    }
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Error al cargar empleados' })
  } finally {
    loading.value = false
  }
}

const onSearchChange = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    pagination.value.page = 1
    cargarEmpleados()
  }, 350)
}

const onSortChange = (newPagination) => {
  if (newPagination.sortBy !== pagination.value.sortBy || newPagination.descending !== pagination.value.descending) {
    pagination.value.sortBy = newPagination.sortBy
    pagination.value.descending = newPagination.descending
    cargarEmpleados()
  }
}

const editarEmpleado = (row) => {
  empleadoSeleccionado.value = row
  esEditar.value = true
  mostrarForm.value = true
}

const confirmarAccion = (tipo, row) => {
  tipoConfirmar.value = tipo
  empleadoSeleccionado.value = { id: row.idEmpleado, nombre: row.nombreCompleto }
  mostrarConfirmar.value = true
}

const onConfirmarAccion = async (id) => {
  const esActivar = tipoConfirmar.value === 'activar'
  try {
    if (esActivar) {
      await activarEmpleado(id)
    } else {
      await desactivarEmpleado(id)
    }
    $q.notify({
      type: 'positive',
      message: esActivar ? 'Empleado activado correctamente' : 'Empleado desactivado correctamente'
    })
    await cargarEmpleados()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || `Error al ${esActivar ? 'activar' : 'desactivar'} empleado`
    })
  }
}

const onGuardar = () => {
  cargarEmpleados()
  cerrarForm()
}

const cerrarForm = () => {
  mostrarForm.value = false
  empleadoSeleccionado.value = { id: null, nombre: '' }
  esEditar.value = false
}

const goToPage = (page) => {
  pagination.value.page = page
  cargarEmpleados()
}

const onRowsPerPageChange = (val) => {
  pagination.value.rowsPerPage = val
  pagination.value.page = 1
  cargarEmpleados()
}

watch(() => props.externalFilters, (val) => {
  if (val) {
    search.value = val.search ?? ''
    filtroEstado.value = val.estado ?? null
    filtroCargo.value = val.cargo ?? null
    filtroFechaDesde.value = val.fechaDesde ?? null
    filtroFechaHasta.value = val.fechaHasta ?? null
    filtroOrden.value = val.ordenarPor ?? null
    pagination.value.page = 1
    cargarEmpleados()
  }
}, { deep: true })

onMounted(() => {
  cargarEmpleados()
})
</script>

<style scoped src="../../assets/styles/empleado/empleado.css"></style>

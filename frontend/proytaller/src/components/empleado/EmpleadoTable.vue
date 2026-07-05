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

      <q-table
        flat
        :rows="empleados"
        :columns="columns"
        row-key="idEmpleado"
        :loading="loading"
        v-model:pagination="pagination"
        hide-pagination
        class="custom-table"
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

        <template v-slot:body-cell-activo="props">
          <q-td :props="props">
            <span :class="['estado-badge', props.row.activo ? 'estado-activo' : 'estado-inactivo']">
              <span class="estado-dot"></span>
              {{ props.row.activo ? 'Activo' : 'Inactivo' }}
            </span>
          </q-td>
        </template>

        <template v-slot:body-cell-acciones="props">
          <q-td :props="props">
            <div class="row no-wrap q-gutter-xs">
              <q-btn flat round dense class="action-btn action-edit" icon="edit" @click="editarEmpleado(props.row)" />
              <q-btn
                v-if="props.row.activo"
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
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { listarEmpleados, desactivarEmpleado, activarEmpleado } from '../../api/empleado/empleado'
import EmpleadoForm from './EmpleadoForm.vue'
import ConfirmarEmpleadoDialog from './ConfirmarEmpleadoDialog.vue'

const $q = useQuasar()
const loading = ref(false)
const empleados = ref([])
const mostrarForm = ref(false)
const mostrarConfirmar = ref(false)
const esEditar = ref(false)
const tipoConfirmar = ref('activar')
const empleadoSeleccionado = ref({ id: null, nombre: '' })

const columns = [
  { name: 'numero', label: 'N&deg;', align: 'center' },
  { name: 'nombreCompleto', label: 'Nombre', field: 'nombreCompleto', align: 'left' },
  { name: 'ci', label: 'CI', field: 'ci', align: 'left' },
  { name: 'correo', label: 'Correo', field: 'correo', align: 'left' },
  { name: 'telefono', label: 'Tel&eacute;fono', field: 'telefono', align: 'left' },
  { name: 'activo', label: 'Estado', field: 'activo', align: 'left' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
]

const pagination = ref({
  sortBy: null,
  descending: false,
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
})

const cargarEmpleados = async () => {
  const { page, rowsPerPage } = pagination.value
  loading.value = true
  try {
    const pageIndex = Number(page) - 1
    const size = Number(rowsPerPage) || 10
    const respuesta = await listarEmpleados(pageIndex, size)
    empleados.value = respuesta.content
    pagination.value = {
      page: respuesta.number + 1,
      rowsPerPage: respuesta.size,
      rowsNumber: respuesta.totalElements,
      sortBy: null,
      descending: false
    }
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Error al cargar empleados' })
  } finally {
    loading.value = false
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

onMounted(() => {
  cargarEmpleados()
})
</script>

<style scoped src="../../assets/styles/empleado/empleado.css"></style>

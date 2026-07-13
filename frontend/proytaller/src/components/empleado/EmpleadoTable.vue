<template>
  <q-page class="q-pa-md page-bg">
    <q-card flat bordered class="table-card">
      <div class="row items-center justify-between q-pa-md table-header">
        <div>
          <div class="text-h6 text-weight-bold table-title">Lista de Empleados</div>
          <div class="text-caption table-subtitle">Administra y consulta los empleados del sistema</div>
        </div>
        <div class="row items-center q-gutter-sm">
          <q-btn
            color="white"
            unelevated
            no-caps
            icon="download"
            label="Exportar"
            dropdown-icon="arrow_drop_down"
            class="export-btn"
          >
            <q-menu class="export-menu" :offset="[0, 8]">
              <q-list dense class="q-py-xs" style="min-width: 170px">
                <q-item clickable v-close-popup @click="exportarPDF" class="export-menu-item">
                  <q-item-section avatar class="export-menu-icon">
                    <q-icon name="picture_as_pdf" class="export-icon-pdf" />
                  </q-item-section>
                  <q-item-section class="export-menu-label">Exportar PDF</q-item-section>
                </q-item>
                <q-separator class="export-menu-sep" />
                <q-item clickable v-close-popup @click="exportarExcel" class="export-menu-item">
                  <q-item-section avatar class="export-menu-icon">
                    <q-icon name="table_view" class="export-icon-excel" />
                  </q-item-section>
                  <q-item-section class="export-menu-label">Exportar Excel</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
          </q-btn>
          <q-btn class="btn-add" icon="add" label="Nuevo Empleado" @click="mostrarForm = true" unelevated />
        </div>
      </div>

     

      <q-table
        flat
        :rows="empleados"
        :columns="columns"
        row-key="id"
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
            <div class="row no-wrap justify-center q-gutter-xs">
              <q-btn flat round dense class="action-btn action-detail" icon="visibility" @click="verDetalle(props.row)">
                <q-tooltip color="#0d4d33" text-color="white">Ver detalle</q-tooltip>
              </q-btn>
              <q-btn flat round dense class="action-btn action-edit" icon="edit" @click="editarEmpleado(props.row)">
                <q-tooltip color="#0d4d33" text-color="white">Editar</q-tooltip>
              </q-btn>
              <q-btn
                v-if="props.row.estado"
                flat round dense class="action-btn action-delete"
                icon="block" color="red"
                @click="confirmarAccion('desactivar', props.row)"
              >
                <q-tooltip color="#0d4d33" text-color="white">Desactivar</q-tooltip>
              </q-btn>
              <q-btn
                v-else
                flat round dense class="action-btn"
                icon="check" color="green"
                @click="confirmarAccion('activar', props.row)"
              >
                <q-tooltip color="#0d4d33" text-color="white">Activar</q-tooltip>
              </q-btn>
            </div>
          </q-td>
        </template>

        <template #bottom>
          <div class="q-table__bottom row items-center q-pa-md">
            <div class="q-table__control">
              <span class="q-table__bottom-item">Numero de paginas:</span>
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

    <EmpleadoDetalleDialog
      v-model="mostrarDetalle"
      :id-empleado="empleadoDetalleId"
    />
  </q-page>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { listarEmpleados, desactivarEmpleado, activarEmpleado, obtenerEmpleado, exportarEmpleadosPDF as exportPDF, exportarEmpleadosExcel as exportExcel } from '../../api/empleado/empleado'
import EmpleadoForm from './EmpleadoForm.vue'
import ConfirmarEmpleadoDialog from './ConfirmarEmpleadoDialog.vue'
import EmpleadoDetalleDialog from './EmpleadoDetalleDialog.vue'

const props = defineProps({
  externalFilters: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['actualizado'])

const $q = useQuasar()
const loading = ref(false)
const empleados = ref([])
const mostrarForm = ref(false)
const mostrarConfirmar = ref(false)
const mostrarDetalle = ref(false)
const empleadoDetalleId = ref(null)
const esEditar = ref(false)
const tipoConfirmar = ref('activar')
const empleadoSeleccionado = ref({ id: null, nombre: '' })

const search = ref('')
const filtroEstado = ref(null)
const filtroFechaDesde = ref(null)
const filtroFechaHasta = ref(null)
const filtroOrden = ref('recientes')
let searchTimeout = null

const estadoOptions = [
  { label: 'Activo', value: true },
  { label: 'Inactivo', value: false }
]

const columns = [
  { name: 'numero', label: 'N°', align: 'center' },
  { name: 'nombreCompleto', label: 'Nombre', field: 'nombreCompleto', align: 'left', sortable: true },
  { name: 'telefono', label: 'Telefono', field: 'telefono', align: 'left' },
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
    if (filtroEstado.value === 'activo') {
      params.estado = true
    } else if (filtroEstado.value === 'inactivo') {
      params.estado = false
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

const editarEmpleado = async (row) => {
  try {
    const empleadoCompleto = await obtenerEmpleado(row.id)
    empleadoSeleccionado.value = empleadoCompleto
    esEditar.value = true
    mostrarForm.value = true
  } catch {
    $q.notify({ type: 'negative', message: 'Error al obtener datos del empleado' })
  }
}

const confirmarAccion = (tipo, row) => {
  tipoConfirmar.value = tipo
  empleadoSeleccionado.value = { id: row.id, nombre: row.nombreCompleto }
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
    emit('actualizado')
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
  emit('actualizado')
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
    filtroFechaDesde.value = val.fechaDesde ?? null
    filtroFechaHasta.value = val.fechaHasta ?? null
    filtroOrden.value = val.ordenarPor ?? 'recientes'
  } else {
    search.value = ''
    filtroEstado.value = null
    filtroFechaDesde.value = null
    filtroFechaHasta.value = null
    filtroOrden.value = 'recientes'
  }
  pagination.value.page = 1
  cargarEmpleados()
}, { deep: true })

const downloadBlob = (blob, filename) => {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}

const exportarParams = computed(() => ({
  busqueda: search.value || undefined,
  estado: filtroEstado.value === 'activo' ? true : filtroEstado.value === 'inactivo' ? false : undefined,
  fechaDesde: filtroFechaDesde.value || undefined,
  fechaHasta: filtroFechaHasta.value || undefined
}))

const verDetalle = (row) => {
  empleadoDetalleId.value = row.id
  mostrarDetalle.value = true
}

const exportarPDF = async () => {
  try {
    const blob = await exportPDF(exportarParams.value)
    downloadBlob(blob, 'reporte_empleados.pdf')
  } catch (error) {
    console.error('Error exportar PDF:', error)
    $q.notify({ type: 'negative', message: error.message || 'Error al exportar PDF' })
  }
}

const exportarExcel = async () => {
  try {
    const blob = await exportExcel(exportarParams.value)
    downloadBlob(blob, 'reporte_empleados.xlsx')
  } catch (error) {
    console.error('Error exportar Excel:', error)
    $q.notify({ type: 'negative', message: error.message || 'Error al exportar Excel' })
  }
}

onMounted(() => {
  cargarEmpleados()
})
</script>

<style scoped src="../../assets/styles/empleado/empleado.css"></style>

<style scoped>
.action-detail {
  color: #0d4d33 !important;
}
.action-detail:hover {
  background: #e8f5e9 !important;
}
.export-btn {
  border: 1px solid #0d4d33 !important;
  border-radius: 8px !important;
  height: 38px;
  padding: 0 16px !important;
  font-weight: 500;
  color: #0d4d33 !important;
}
.export-btn:hover {
  background: #f0f7e8 !important;
}
.export-btn .q-btn__wrapper:before {
  box-shadow: none !important;
}
.export-btn::before {
  box-shadow: none !important;
}
.export-menu {
  border-radius: 8px;
}
.export-menu-item {
  min-height: 40px;
  border-radius: 6px;
  margin: 2px 6px;
}
.export-menu-item:hover {
  background: #f0f0f0 !important;
}
.export-menu-icon {
  min-width: 36px;
}
.export-menu-sep {
  margin: 2px 12px;
}
.export-icon-pdf {
  color: #ffab24 !important;
  font-size: 22px;
}
.export-icon-excel {
  color: #0d4d33 !important;
  font-size: 22px;
}
.export-menu-label {
  color: #0d4d33 !important;
  font-weight: 500;
}
.btn-add {
  height: 38px !important;
}

</style>

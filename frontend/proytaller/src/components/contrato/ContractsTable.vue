<template>
  <q-card flat bordered class="table-card">
    <q-table
      :rows="rows"
      :columns="columns"
      :loading="loading"
      row-key="id"
      flat
      bordered
      :pagination.sync="pagination"
      @request="onRequest"
      :rows-per-page-options="[10, 20, 50]"
      class="main-table"
    >
      <template v-slot:loading>
        <q-inner-loading showing color="primary" />
      </template>

      <template v-slot:body-cell-empleado="props">
        <q-td :props="props">
          <div class="row items-center no-wrap">
            <q-avatar size="32px" class="q-mr-sm">
              <img :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(props.value?.nombres + ' ' + props.value?.apellidos)}&background=2E7D32&color=fff&size=32`" />
            </q-avatar>
            <div>
              <div class="text-body2 text-weight-medium">{{ props.value?.nombres }} {{ props.value?.apellidos }}</div>
              <div class="text-caption text-grey-6">CI: {{ props.value?.cedula }}</div>
            </div>
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-controlaAsistencia="props">
        <q-td :props="props">
          <q-icon :name="props.value ? 'check_circle' : 'cancel'" :color="props.value ? 'green' : 'grey-4'" size="22px" />
        </q-td>
      </template>

      <template v-slot:body-cell-estado="props">
        <q-td :props="props">
          <q-chip
            :color="chipColor(props.value)"
            text-color="white"
            dense
            size="12px"
            class="estado-chip"
          >
            {{ props.value }}
          </q-chip>
        </q-td>
      </template>

      <template v-slot:body-cell-acciones="props">
        <q-td :props="props" class="text-center">
          <q-btn flat round icon="visibility" size="md" @click="$emit('ver-detalle', props.row.id)" style="color: #006051">
            <q-tooltip class="bg-primary text-white">Ver detalle</q-tooltip>
          </q-btn>
          <q-btn v-if="props.row.estado === 'ACTIVO' || props.row.estado === 'SUSPENDIDO'" flat round icon="edit" size="md" @click="$emit('editar', props.row.id)" style="color: #F57C00">
            <q-tooltip class="bg-orange text-white">Editar</q-tooltip>
          </q-btn>
          <q-btn flat round icon="picture_as_pdf" size="md" style="color: #8BC34A" @click="$emit('pdf', props.row)">
            <q-tooltip class="bg-positive text-white">PDF</q-tooltip>
          </q-btn>
          <q-btn v-if="props.row.estado === 'VENCIDO' || props.row.estado === 'FINALIZADO'" flat round icon="autorenew" size="md" @click="$emit('renovar', props.row.id)" style="color: #006051">
            <q-tooltip class="bg-primary text-white">Renovar</q-tooltip>
          </q-btn>
          <q-btn v-if="props.row.estado === 'ACTIVO' || props.row.estado === 'VENCIDO' || props.row.estado === 'SUSPENDIDO'" flat round icon="stop_circle" size="md" @click="$emit('finalizar', props.row.id)" style="color: #C10015">
            <q-tooltip class="bg-negative text-white">Finalizar</q-tooltip>
          </q-btn>
          <q-btn v-if="props.row.estado === 'ACTIVO'" flat round icon="pause_circle" size="md" @click="$emit('suspender', props.row.id)" style="color: #F57C00">
            <q-tooltip class="bg-orange text-white">Suspender</q-tooltip>
          </q-btn>
          <q-btn v-if="props.row.estado === 'SUSPENDIDO'" flat round icon="play_arrow" size="md" @click="$emit('activar', props.row.id)" style="color: #006051">
            <q-tooltip class="bg-primary text-white">Activar</q-tooltip>
          </q-btn>
        </q-td>
      </template>

      <template v-slot:no-data>
        <div class="text-center q-pa-lg text-grey-6">
          <q-icon name="contract" size="48px" color="grey-4" />
          <div class="q-mt-sm">No se encontraron contratos</div>
        </div>
      </template>
    </q-table>
  </q-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listarContratos } from '../../api/contrato/contrato'

defineEmits(['ver-detalle', 'editar', 'renovar', 'finalizar', 'activar', 'suspender', 'pdf'])

const rows = ref([])
const loading = ref(false)
const searchText = ref('')
const filtrosAplicados = ref(null)

const pagination = reactive({
  sortBy: 'id',
  descending: true,
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
})

const columns = [
  { name: 'id', label: 'N° Contrato', align: 'left', field: row => `CTR-${String(row.id).padStart(4, '0')}`, sortable: true, style: 'width: 120px' },
  { name: 'empleado', label: 'Empleado', align: 'left', field: row => row.empleado, sortable: false, style: 'min-width: 200px' },
  { name: 'cargo', label: 'Cargo', align: 'left', field: 'cargoNombre', sortable: true, style: 'min-width: 150px' },
  { name: 'tipoContrato', label: 'Tipo Contrato', align: 'left', field: 'tipoContratoNombre', sortable: true, style: 'width: 130px' },
  { name: 'tipoJornada', label: 'Jornada', align: 'left', field: 'tipoJornadaNombre', sortable: true, style: 'width: 120px' },
  { name: 'sueldoBase', label: 'Sueldo', align: 'right', field: 'sueldoBase', sortable: true, format: v => `Bs. ${v?.toLocaleString()}`, style: 'width: 110px' },
  { name: 'horasSemana', label: 'Horas/Sem', align: 'center', field: 'horasSemana', sortable: true, style: 'width: 100px' },
  { name: 'controlaAsistencia', label: 'Ctrl Asis', align: 'center', field: 'controlaAsistencia', sortable: true, style: 'width: 90px' },
  { name: 'estado', label: 'Estado', align: 'center', field: 'estado', sortable: true, style: 'width: 110px' },
  { name: 'fechaInicio', label: 'Inicio', align: 'left', field: 'fechaInicio', sortable: true, format: f => f || '-', style: 'width: 110px' },
  { name: 'fechaFin', label: 'Fin', align: 'left', field: 'fechaFin', sortable: true, format: f => f || '-', style: 'width: 110px' },
  { name: 'acciones', label: 'Acciones', align: 'center', field: 'id', sortable: false, style: 'width: 200px' }
]

function chipColor(estado) {
  const map = { ACTIVO: 'green-7', VENCIDO: 'red-6', FINALIZADO: 'grey-6', SUSPENDIDO: 'orange-7' }
  return map[estado] || 'grey-5'
}

async function onRequest(props) {
  loading.value = true
  const { page, rowsPerPage, sortBy, descending } = props.pagination
  try {
    const params = {
      page: page - 1,
      size: rowsPerPage,
      sort: `${sortBy},${descending ? 'desc' : 'asc'}`
    }
    if (searchText.value) params.search = searchText.value
    if (filtrosAplicados.value) Object.assign(params, filtrosAplicados.value)

    const res = await listarContratos(params)
    rows.value = res?.content || []
    pagination.page = page
    pagination.rowsPerPage = rowsPerPage
    pagination.sortBy = sortBy
    pagination.descending = descending
    pagination.rowsNumber = res?.totalElements || 0
  } catch {
    rows.value = []
  } finally {
    loading.value = false
  }
}

function recargar() {
  onRequest({ pagination })
}

function buscar(val) {
  searchText.value = val
  onRequest({ pagination: { ...pagination, page: 1 } })
}

function filtrar(filtros) {
  filtrosAplicados.value = filtros
  onRequest({ pagination: { ...pagination, page: 1 } })
}

onMounted(() => {
  onRequest({ pagination })
})

defineExpose({ recargar, buscar, filtrar })
</script>

<style scoped>
.table-card {
  border-radius: 14px;
  background: white;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.main-table :deep(.q-table) {
  font-size: 13px;
}
.main-table :deep(th) {
  font-weight: 600;
  color: #374151;
  background: #f9fafb;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}
.main-table :deep(td) {
  border-bottom: 1px solid #f0f0f0;
}
.estado-chip {
  font-weight: 600;
  border-radius: 6px;
  padding: 2px 10px;
}
</style>

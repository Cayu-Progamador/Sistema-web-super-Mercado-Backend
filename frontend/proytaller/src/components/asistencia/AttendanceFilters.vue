<template>
  <q-slide-transition>
    <div v-show="visible">
      <q-card flat bordered class="filters-card q-mb-md q-pa-md">
        <div class="row q-col-gutter-md items-end">
          <div class="col-12 col-md-3">
            <q-select
              v-model="filtros.turno"
              :options="turnoOptions"
              label="Turno"
              outlined
              dense
              clearable
              class="filter-field"
              emit-value
              map-options
            />
          </div>
          <div class="col-12 col-md-3">
            <q-select
              v-model="filtros.estado"
              :options="estadoOptions"
              label="Estado"
              outlined
              dense
              clearable
              class="filter-field"
              emit-value
              map-options
            />
          </div>
          <div class="col-12 col-md-3">
            <q-input
              v-model="filtros.fechaDesde"
              type="date"
              label="Fecha Desde"
              outlined
              dense
              :max="filtros.fechaHasta || fechaHoy"
              :error="!!errorFechaDesde"
              :error-message="errorFechaDesde"
              class="filter-field"
            />
          </div>
          <div class="col-12 col-md-3">
            <q-input
              v-model="filtros.fechaHasta"
              type="date"
              label="Fecha Hasta"
              outlined
              dense
              :min="filtros.fechaDesde || undefined"
              :max="fechaHoy"
              :error="!!errorFechaHasta"
              :error-message="errorFechaHasta"
              class="filter-field"
            />
          </div>
          <div class="col-12 row q-gutter-sm justify-end">
            <q-btn flat no-caps color="teal-9" icon="clear" label="Limpiar" @click="limpiar" />
            <q-btn unelevated no-caps color="teal-9" icon="search" label="Buscar" :disable="rangoFechasInvalido" @click="buscar" />
          </div>
        </div>
      </q-card>
    </div>
  </q-slide-transition>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { obtenerFiltrosAsistencia } from '../../api/asistencia/asistencia'

defineProps({
  visible: { type: Boolean, default: false }
})

const emit = defineEmits(['aplicar-filtros'])

const filtros = reactive({
  turno: null,
  estado: null,
  fechaDesde: null,
  fechaHasta: null
})

const fechaActual = new Date()
const fechaHoy = `${fechaActual.getFullYear()}-${String(fechaActual.getMonth() + 1).padStart(2, '0')}-${String(fechaActual.getDate()).padStart(2, '0')}`

const errorFechaDesde = computed(() => {
  if (filtros.fechaDesde && filtros.fechaDesde > fechaHoy) {
    return 'No se permiten fechas posteriores a hoy.'
  }
  if (filtros.fechaDesde && filtros.fechaHasta && filtros.fechaDesde > filtros.fechaHasta) {
    return 'La fecha desde no puede ser mayor que la fecha hasta.'
  }
  return ''
})

const errorFechaHasta = computed(() => {
  if (filtros.fechaHasta && filtros.fechaHasta > fechaHoy) {
    return 'No se permiten fechas posteriores a hoy.'
  }
  if (filtros.fechaDesde && filtros.fechaHasta && filtros.fechaHasta < filtros.fechaDesde) {
    return 'La fecha hasta no puede ser menor que la fecha desde.'
  }
  return ''
})

const rangoFechasInvalido = computed(() =>
  !!errorFechaDesde.value || !!errorFechaHasta.value
)

const turnoOptions = ref([])
const estadoOptions = ref([])

async function cargarOpciones() {
  try {
    const res = await obtenerFiltrosAsistencia()
    turnoOptions.value = (res.turnos || []).map(t => ({
      label: t.nombre,
      value: t.id
    }))
    estadoOptions.value = res.estados || []
  } catch {
    turnoOptions.value = []
    estadoOptions.value = []
  }
}

function limpiar() {
  filtros.turno = null
  filtros.estado = null
  filtros.fechaDesde = null
  filtros.fechaHasta = null
  emit('aplicar-filtros', { ...filtros })
}

function buscar() {
  if (rangoFechasInvalido.value) return
  emit('aplicar-filtros', { ...filtros })
}

onMounted(cargarOpciones)
</script>

<style scoped>
.filters-card {
  border: 1px solid #bce9e2 !important;
  border-radius: 16px;
  background: white;
}

.filter-field :deep(.q-field--outlined .q-field__control) {
  background: white;
  border-radius: 10px;
}

.filter-field :deep(.q-field--outlined .q-field__control::before) {
  border: 2px solid #006051 !important;
  border-radius: 10px;
  transition: border-color 0.2s ease;
}

.filter-field :deep(.q-field--outlined .q-field__control:hover::before) {
  border-color: #006051 !important;
}

.filter-field :deep(.q-field--outlined .q-field__control:focus-within::before) {
  border-color: #006051 !important;
  box-shadow: 0 0 0 3px rgba(0,96,81,0.1);
}

.filter-field :deep(.q-field__native) {
  font-family: 'Nunito', sans-serif;
  font-size: 0.85rem;
  color: #333;
  font-weight: 500;
}

.filter-field :deep(.q-field__label) {
  font-family: 'Nunito', sans-serif;
  color: #006051;
  font-weight: 600;
}

.filter-field :deep(.q-field--outlined .q-field__control .q-icon) {
  color: #006051 !important;
  font-size: 1.1rem;
}

.filter-field :deep(.q-field--focused .q-field__label) {
  color: #006051 !important;
}

.filter-field :deep(.q-field__control:focus-within) {
  border-color: #006051;
}
</style>

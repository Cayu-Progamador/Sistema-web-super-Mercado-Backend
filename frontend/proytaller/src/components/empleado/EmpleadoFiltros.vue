<template>
  <div>
    <div class="row items-center q-gutter-sm q-mb-md">
      <q-input
        v-model="local.search"
        outlined
        dense
        placeholder="Buscar por nombre, apellido, CI"
        class="search-input"
        debounce="300"
        @update:model-value="emitSearch"
      >
        <template v-slot:prepend>
          <q-icon name="search" class="search-icon" />
        </template>
      </q-input>
      <q-btn flat :icon="filtrosVisibles ? 'expand_less' : 'filter_list'" label="Filtros" class="filter-toggle-btn" @click="filtrosVisibles = !filtrosVisibles" no-caps>
        <q-badge v-if="filtrosActivos" color="primary" floating>!</q-badge>
      </q-btn>
    </div>

    <q-slide-transition>
      <div v-show="filtrosVisibles">
        <q-card flat bordered class="filters-card q-mb-md q-pa-md">
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-3">
              <q-select
                v-model="local.estado"
                :options="estadoOptions"
                option-label="label"
                option-value="value"
                emit-value
                map-options
                outlined
                dense
                clearable
                label="Estado"
                class="filter-field"
                @update:model-value="onFilterChange"
              />
            </div>
            <div class="col-12 col-md-3">
              <q-input
                v-model="local.fechaDesde"
                outlined
                dense
                label="Fecha Desde"
                type="date"
                clearable
                class="filter-field"
                @update:model-value="onFilterChange"
              />
            </div>
            <div class="col-12 col-md-3">
              <q-input
                v-model="local.fechaHasta"
                outlined
                dense
                label="Fecha Hasta"
                type="date"
                clearable
                class="filter-field"
                @update:model-value="onFilterChange"
              />
            </div>
            <div class="col-12 col-md-3">
              <q-select
                v-model="local.ordenarPor"
                :options="ordenarOptions"
                outlined
                dense
                map-options
                emit-value
                label="Ordenar por"
                class="filter-field"
                @update:model-value="onFilterChange"
              />
            </div>
            <div class="col-12 row justify-end q-gutter-sm q-mt-sm">
              <q-btn flat color="grey-7" icon="clear" label="Limpiar" @click="resetFilters" no-caps />
              <q-btn unelevated color="primary" icon="search" label="Buscar" @click="emitSearch" no-caps />
            </div>
          </div>
        </q-card>
      </div>
    </q-slide-transition>
  </div>
</template>

<script setup>
import { reactive, ref, computed, watch } from 'vue'

const props = defineProps({
  filters: {
    type: Object,
    default: () => ({
      search: '',
      estado: null,
      fechaDesde: null,
      fechaHasta: null,
      ordenarPor: null
    })
  }
})

const emit = defineEmits(['search', 'reset', 'filter-change'])

const filtrosVisibles = ref(false)
const filtrosActivos = ref(false)

const estadoOptions = [
  { label: 'Todos', value: 'todos' },
  { label: 'Activo', value: 'activo' },
  { label: 'Inactivo', value: 'inactivo' }
]

const ordenarOptions = [
  { label: 'Más recientes', value: 'recientes' },
  { label: 'Más antiguos', value: 'antiguos' },
  { label: 'Nombre A-Z', value: 'nombre-asc' },
  { label: 'Nombre Z-A', value: 'nombre-desc' }
]

const local = reactive({
  search: props.filters?.search ?? '',
  estado: props.filters?.estado ?? 'todos',
  fechaDesde: props.filters?.fechaDesde ?? null,
  fechaHasta: props.filters?.fechaHasta ?? null,
  ordenarPor: props.filters?.ordenarPor ?? 'recientes'
})

watch(() => props.filters, (val) => {
  if (val) {
    local.search = val.search ?? ''
    local.estado = val.estado ?? 'todos'
    local.fechaDesde = val.fechaDesde ?? null
    local.fechaHasta = val.fechaHasta ?? null
    local.ordenarPor = val.ordenarPor ?? 'recientes'
  }
}, { deep: true })

function onFilterChange() {
  emit('filter-change', { ...local })
}

function emitSearch() {
  filtrosActivos.value = Object.values(local).some(v => v !== null && v !== undefined && v !== '')
  emit('search', { ...local })
}

function resetFilters() {
  local.search = ''
  local.estado = 'todos'
  local.fechaDesde = null
  local.fechaHasta = null
  local.ordenarPor = 'recientes'
  filtrosActivos.value = false
  emit('reset')
}
</script>

<style scoped>
.search-input {
  width: 320px;
}
.search-input :deep(.q-field__control) {
  border-radius: 10px;
  background: white;
}
.search-icon {
  color: #2a5c1a;
}
.search-icon:hover {
  color: #2a5c1a;
}
.filter-toggle-btn {
  color: #2a5c1a;
}
.filter-toggle-btn:hover {
  color: #2a5c1a;
}
.filters-card {
  border-radius: 12px;
  background: white;
  border: 1px solid #e5e7eb;
}
.filter-field :deep(.q-field__control) {
  border-radius: 8px;
  background: #f9fafb;
}
@media (max-width: 600px) {
  .search-input {
    width: 100%;
  }
}
</style>

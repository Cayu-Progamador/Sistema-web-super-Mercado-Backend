<template>
  <q-card flat class="filter-card q-mb-lg">
    <q-card-section class="q-pa-lg">
      <div class="filter-section">
        <div class="row items-end q-col-gutter-md q-mb-md">
          <div class="col-12 col-md filter-search-col">
            <label class="filter-field-label">Búsqueda</label>
            <q-input
              v-model="local.search"
              outlined
              dense
              placeholder="Buscar por nombre, apellido, CI o código..."
              clearable
              class="filter-input"
              :class="{ 'has-value': local.search }"
              @clear="onFilterChange"
              @keyup.enter="emitSearch"
            >
              <template v-slot:prepend>
                <q-icon name="search" size="20px" class="input-icon" />
              </template>
            </q-input>
          </div>
          <div class="col-12 col-sm-auto filter-actions">
            <q-btn
              unelevated
              class="btn-filter-search q-mr-sm"
              icon="search"
              label="Buscar"
              @click="emitSearch"
            />
            <q-btn
              flat
              class="btn-filter-reset"
              icon="clear"
              label="Limpiar"
              @click="resetFilters"
            />
          </div>
        </div>

        <div class="row q-col-gutter-md">
          <div class="col-12 col-sm-6 col-md">
            <label class="filter-field-label">Estado</label>
            <q-select
              v-model="local.estado"
              :options="estadoOptions"
              outlined
              dense
              placeholder="Todos"
              map-options
              emit-value
              clearable
              class="filter-select"
              @update:model-value="onFilterChange"
            >
              <template v-slot:selected-item="scope">
                <div class="flex items-center">
                  <span
                    class="status-dot"
                    :class="scope.value === true ? 'dot-active' : 'dot-inactive'"
                  />
                  {{ scope.value === true ? 'Activo' : 'Inactivo' }}
                </div>
              </template>
            </q-select>
          </div>
          <div class="col-12 col-sm-6 col-md">
            <label class="filter-field-label">Cargo</label>
            <q-select
              v-model="local.cargo"
              :options="cargoOptions"
              outlined
              dense
              placeholder="Todos"
              clearable
              class="filter-select"
              @update:model-value="onFilterChange"
            />
          </div>
          <div class="col-12 col-sm-6 col-md">
            <label class="filter-field-label">Fecha Desde</label>
            <q-input
              v-model="local.fechaDesde"
              outlined
              dense
              placeholder="Seleccionar fecha"
              clearable
              class="filter-input"
              @clear="onFilterChange"
            >
              <template v-slot:append>
                <q-icon name="calendar_today" size="18px" class="input-icon cursor-pointer">
                  <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                    <q-date
                      v-model="local.fechaDesde"
                      mask="YYYY-MM-DD"
                      @update:model-value="onFilterChange"
                      class="filter-datepicker"
                    />
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>
          </div>
          <div class="col-12 col-sm-6 col-md">
            <label class="filter-field-label">Fecha Hasta</label>
            <q-input
              v-model="local.fechaHasta"
              outlined
              dense
              placeholder="Seleccionar fecha"
              clearable
              class="filter-input"
              @clear="onFilterChange"
            >
              <template v-slot:append>
                <q-icon name="calendar_today" size="18px" class="input-icon cursor-pointer">
                  <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                    <q-date
                      v-model="local.fechaHasta"
                      mask="YYYY-MM-DD"
                      @update:model-value="onFilterChange"
                      class="filter-datepicker"
                    />
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>
          </div>
          <div class="col-12 col-sm-6 col-md">
            <label class="filter-field-label">Ordenar por</label>
            <q-select
              v-model="local.ordenarPor"
              :options="ordenarOptions"
              outlined
              dense
              placeholder="Más recientes"
              map-options
              emit-value
              clearable
              class="filter-select"
              @update:model-value="onFilterChange"
            >
              <template v-slot:prepend>
                <q-icon name="sort" size="18px" class="input-icon" />
              </template>
            </q-select>
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  filters: {
    type: Object,
    default: () => ({
      search: '',
      estado: null,
      cargo: null,
      fechaDesde: null,
      fechaHasta: null,
      ordenarPor: null
    })
  }
})

const emit = defineEmits(['search', 'reset', 'filter-change'])

const estadoOptions = [
  { label: 'Activo', value: true },
  { label: 'Inactivo', value: false }
]

const cargoOptions = [
  { label: 'Todos', value: null },
  { label: 'Administrador', value: 'Administrador' },
  { label: 'Gerente', value: 'Gerente' },
  { label: 'Supervisor', value: 'Supervisor' },
  { label: 'Cajero', value: 'Cajero' },
  { label: 'Bodeguero', value: 'Bodeguero' }
]

const ordenarOptions = [
  { label: 'Más recientes', value: 'recientes' },
  { label: 'Más antiguos', value: 'antiguos' },
  { label: 'Nombre A-Z', value: 'nombre-asc' },
  { label: 'Nombre Z-A', value: 'nombre-desc' }
]

const local = reactive({
  search: props.filters?.search || '',
  estado: props.filters?.estado ?? null,
  cargo: props.filters?.cargo ?? null,
  fechaDesde: props.filters?.fechaDesde || null,
  fechaHasta: props.filters?.fechaHasta || null,
  ordenarPor: props.filters?.ordenarPor || null
})

watch(() => props.filters, (val) => {
  if (val) {
    local.search = val.search ?? ''
    local.estado = val.estado ?? null
    local.cargo = val.cargo ?? null
    local.fechaDesde = val.fechaDesde ?? null
    local.fechaHasta = val.fechaHasta ?? null
    local.ordenarPor = val.ordenarPor ?? null
  }
}, { deep: true })

function onFilterChange() {
  emit('filter-change', { ...local })
}

function emitSearch() {
  emit('search', { ...local })
}

function resetFilters() {
  local.search = ''
  local.estado = null
  local.cargo = null
  local.fechaDesde = null
  local.fechaHasta = null
  local.ordenarPor = null
  emit('reset')
}
</script>

<style scoped src="../../assets/styles/empleado/empleadoFiltros.css"></style>

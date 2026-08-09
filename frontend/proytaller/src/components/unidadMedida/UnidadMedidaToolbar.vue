<template>
  <div class="q-mb-md">
    <q-card class="cat-filters">
      <q-card-section class="q-pa-md">
        <div class="row items-center justify-between" style="gap: 12px; flex-wrap: wrap;">
          <div class="row items-center" style="gap: 12px; flex-wrap: wrap;">
            <q-input
              v-model="modelo.nombre"
              dense
              outlined
              clearable
              placeholder="Buscar por nombre o abreviatura..."
              class="cat-search cat-campo"
              @keyup.enter="emit('buscar')"
              @update:model-value="onBusquedaCambio"
            >
              <template v-slot:prepend>
                <q-icon name="search" color="green-8" size="20px" />
              </template>
            </q-input>

            <q-select
              v-model="modelo.tipo"
              :options="tipoOptions"
              emit-value
              map-options
              dense
              outlined
              label="Tipo"
              class="cat-campo cat-select-estado"
              style="min-width: 170px;"
            />

            <q-select
              v-model="modelo.estado"
              :options="estados"
              emit-value
              map-options
              dense
              outlined
              label="Estado"
              class="cat-campo cat-select-estado"
              style="min-width: 170px;"
            />

            <q-btn label="Buscar" icon="search" no-caps class="cat-btn-buscar-nuevo" @click="emit('buscar')" />
            <q-btn label="Limpiar filtros" icon="close" no-caps class="cat-btn-limpiar" @click="emit('limpiar')" />
          </div>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount } from 'vue'

const props = defineProps({
  modelo: {
    type: Object,
    required: true
  },
  tipos: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['buscar', 'limpiar'])

let debounceTimer = null

function onBusquedaCambio() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => emit('buscar'), 400)
}

onBeforeUnmount(() => clearTimeout(debounceTimer))

const tipoOptions = computed(() => [
  { label: 'Todos', value: 'todos' },
  ...(props.tipos || []).map((t) => ({ label: t.nombre, value: t.idTipoUnidad ?? t.id }))
])

const estados = [
  { label: 'Todos', value: 'todos' },
  { label: 'Activo', value: 'activo' },
  { label: 'Inactivo', value: 'inactivo' }
]
</script>

<style scoped src="../../assets/styles/categorias/categorias.css"></style>
<style scoped>
.cat-filters {
  border: 1px solid #e7eef2;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(46, 125, 50, 0.06);
}

.cat-campo {
  width: 300px;
  max-width: 100%;
}

.cat-campo :deep(.q-field__control) {
  background: #F1F8E9;
  border-radius: 8px;
  border: 1.5px solid #A5D6A7;
  min-height: 40px;
  color: #2E7D32;
}

.cat-campo :deep(.q-field__control::before) {
  border: none;
}

.cat-campo :deep(.q-field__control::after) {
  border: none;
}

.cat-campo :deep(.q-field__control:hover) {
  border-color: #81C784;
}

.cat-campo :deep(.q-field--focused .q-field__control) {
  border-color: #66BB6A;
}

.cat-campo :deep(.q-field__native) {
  color: #2E7D32;
}

.cat-campo :deep(.q-field__native::placeholder) {
  color: #81C784;
}

.cat-campo :deep(.q-field__label) {
  color: #388E3C;
}

.cat-campo :deep(.q-field__marginal) {
  color: #2E7D32;
}

.cat-campo :deep(.q-field__clearable) {
  color: #2E7D32;
}

.cat-campo :deep(.q-select__dropdown-icon) {
  color: #2E7D32;
}

.cat-btn-buscar-nuevo {
  background: #2E7D32 !important;
  color: #ffffff !important;
  font-weight: 700;
  border-radius: 8px;
  min-height: 40px;
  box-shadow: 0 2px 6px rgba(46, 125, 50, 0.3);
}

.cat-btn-buscar-nuevo:hover {
  background: #276B2B !important;
}

.cat-btn-buscar-nuevo .q-icon {
  font-size: 20px;
}

.cat-btn-limpiar {
  background: #ffffff;
  color: #2E7D32;
  border: 0.5px solid #A5D6A7;
  border-radius: 8px;
  min-height: 40px;
  font-weight: 600;
}

.cat-btn-limpiar:hover {
  background: #F1F8E9;
  border-color: #81C784;
  color: #1B5E20;
}

.cat-btn-limpiar .q-icon {
  font-size: 20px;
}
</style>

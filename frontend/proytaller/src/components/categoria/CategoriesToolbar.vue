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
              placeholder="Buscar por nombre..."
              class="cat-search cat-campo"
              @keyup.enter="emit('buscar')"
              @update:model-value="onBusquedaCambio"
            >
              <template v-slot:prepend>
                <q-icon name="search" color="green-8" size="20px" />
              </template>
            </q-input>

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

          <q-btn-toggle
            :model-value="vista"
            :options="vistas"
            unelevated
            class="cat-vista-toggle"
            @update:model-value="emit('vista-cambio', $event)"
          />
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup>
import { onBeforeUnmount } from 'vue'

const props = defineProps({
  modelo: {
    type: Object,
    required: true
  },
  vista: {
    type: String,
    default: 'tabla'
  }
})

const emit = defineEmits(['buscar', 'limpiar', 'vista-cambio'])

let debounceTimer = null

function onBusquedaCambio() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => emit('buscar'), 400)
}

onBeforeUnmount(() => clearTimeout(debounceTimer))

const estados = [
  { label: 'Todos', value: 'todos' },
  { label: 'Activo', value: 'activo' },
  { label: 'Inactivo', value: 'inactivo' }
]

const vistas = [
  { label: 'Tabla', value: 'tabla', icon: 'table_rows' },
  { label: 'Árbol', value: 'arbol', icon: 'account_tree' }
]
</script>

<style scoped src="../../assets/styles/categorias/categorias.css"></style>
<style scoped>
.cat-filters {
  border: 1px solid #e7eef2;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(46, 125, 50, 0.06);
}

/* ===== Campos de búsqueda / estado (sin estilos Quasar) ===== */
.cat-campo {
  width: 320px;
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

/* ===== Botón Buscar (igual que Nueva Categoría) ===== */
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

/* ===== Botón Limpiar (verde claro, hover borde más oscuro) ===== */
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

.cat-vista-toggle {
  background: #ffffff;
  border: 1px solid #e7eef2;
  border-radius: 12px;
  padding: 3px;
  width: 40%;
  min-width: 260px;
  max-width: 380px;
  box-shadow: 0 2px 10px rgba(46, 125, 50, 0.05);
}

.cat-vista-toggle :deep(.q-btn-group) {
  width: 100%;
  display: flex;
}

.cat-vista-toggle :deep(.q-btn) {
  color: #5B6675 !important;
  background: #ffffff !important;
  border-radius: 9px;
  min-height: 34px;
  flex: 1 1 0;
  font-weight: 600;
  text-transform: none;
}

.cat-vista-toggle :deep(.q-btn .q-icon) {
  font-size: 18px;
}

.cat-vista-toggle :deep(.q-btn[aria-pressed="true"]) {
  background: #ffffff !important;
  color: #2E7D32 !important;
}
</style>

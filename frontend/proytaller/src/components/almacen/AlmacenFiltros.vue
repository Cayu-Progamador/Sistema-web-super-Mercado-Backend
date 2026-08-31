<template>
  <q-card flat bordered class="alm-filtros q-mb-lg q-pa-md">
    <div class="row q-col-gutter-md items-end">
      <div class="col-12 col-md-6">
        <q-input v-model.trim="local.buscar" outlined dense clearable label="Buscar almacén" placeholder="Nombre o código del almacén..." color="green-8" class="alm-filter-field" @keyup.enter="emitir">
          <template #prepend><q-icon name="search" color="green-8" /></template>
        </q-input>
      </div>
      <div class="col-12 col-sm-6 col-md-3">
        <q-select v-model="local.estado" :options="estados" outlined dense clearable emit-value map-options label="Estado" placeholder="Todos los estados" color="green-8" class="alm-filter-field" />
      </div>
      <div class="col-12 col-sm-6 col-md-3">
        <q-select v-model="local.tipo" :options="opcionesTipo" outlined dense clearable emit-value map-options label="Tipo de almacén" placeholder="Todos los tipos" color="green-8" class="alm-filter-field" />
      </div>
      <div class="col-12 row justify-end q-gutter-sm q-mt-sm alm-filter-actions">
        <q-btn flat no-caps icon="clear" label="Limpiar" color="green-8" @click="limpiar" />
        <q-btn unelevated no-caps icon="search" label="Buscar" color="green-8" @click="emitir" />
      </div>
    </div>
  </q-card>
</template>

<script setup>
import { computed, reactive } from 'vue'
const props = defineProps({ tipos: { type: Array, default: () => [] } })
const emit = defineEmits(['filtrar', 'limpiar'])
const local = reactive({ buscar: '', estado: null, tipo: null })
const estados = [{ label: 'Todos', value: null }, { label: 'Activo', value: 'ACTIVO' }, { label: 'Inactivo', value: 'INACTIVO' }]
const opcionesTipo = computed(() => [{ label: 'Todos', value: null }, ...props.tipos])
function emitir() { emit('filtrar', { ...local }) }
function limpiar() { Object.assign(local, { buscar: '', estado: null, tipo: null }); emit('limpiar') }
</script>

<style scoped>
.alm-filtros { border-radius: 12px; border-color: #e5e7eb !important; background: #fff; box-shadow: 0 3px 16px rgba(42,92,26,.06); }
.alm-filter-field :deep(.q-field__control) { border: 1.5px solid #4a8c25; border-radius: 8px; box-shadow: none; }
.alm-filter-field :deep(.q-field__control::before),
.alm-filter-field :deep(.q-field__control::after) { display: none; }
.alm-filter-field :deep(.q-field__control:focus-within) { border-color: #4a8c25; box-shadow: 0 0 0 3px rgba(74,140,37,.14); }
.alm-filter-field :deep(input),
.alm-filter-field :deep(.q-field__label),
.alm-filter-field :deep(.q-icon) { color: #2a5c1a; }
@media (max-width: 599px) {
  .alm-filtros { padding: 14px; }
  .alm-filter-actions { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-left: 0; }
  .alm-filter-actions .q-btn { width: 100%; min-height: 44px; margin: 0; }
}
</style>

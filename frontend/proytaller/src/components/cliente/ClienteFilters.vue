<template>
  <q-card flat bordered class="filters-card">
    <q-card-section class="row q-col-gutter-md items-center">
      <div class="col-12 col-md-7">
        <q-input v-model="local.busqueda" outlined dense clearable label="Buscar por nombre, apellido o CI"
          color="green-8" @update:model-value="emitir">
          <template #prepend><q-icon name="search" color="green-8" /></template>
        </q-input>
      </div>
      <div class="col-12 col-sm-6 col-md-3">
        <q-select v-model="local.estado" :options="estados" outlined dense clearable emit-value map-options
          label="Estado" color="green-8" @update:model-value="emitir" />
      </div>
      <div class="col-12 col-sm-6 col-md-2">
        <q-btn outline color="green-8" icon="restart_alt" label="Limpiar" no-caps class="full-width"
          @click="limpiar" />
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({ modelo: { type: Object, default: () => ({}) } })
const emit = defineEmits(['filtro', 'limpiar'])
const local = reactive({ busqueda: '', estado: null })
const estados = [
  { label: 'Activos', value: true },
  { label: 'Inactivos', value: false }
]

watch(() => props.modelo, (value) => Object.assign(local, { busqueda: '', estado: null }, value || {}), { immediate: true, deep: true })
function emitir() { emit('filtro', { ...local }) }
function limpiar() { Object.assign(local, { busqueda: '', estado: null }); emit('limpiar') }
</script>

<style scoped>
.filters-card { border-radius: 16px; border-color: #e0e8e2; background: #fff; }
</style>

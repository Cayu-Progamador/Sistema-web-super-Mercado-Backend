<template>
  <q-card flat bordered class="filter-card">
    <q-card-section class="row q-col-gutter-md items-end">
      <div class="col-12 col-md-4">
        <q-input v-model="model.busqueda" outlined dense label="Buscar por venta o cliente" clearable>
          <template #prepend><q-icon name="search" /></template>
        </q-input>
      </div>
      <div class="col-12 col-sm-4 col-md-2">
        <q-select v-model="model.estado" outlined dense emit-value map-options label="Estado" :options="estados" />
      </div>
      <div class="col-12 col-sm-4 col-md-2">
        <q-input v-model="model.desde" outlined dense type="date" label="Desde" />
      </div>
      <div class="col-12 col-sm-4 col-md-2">
        <q-input v-model="model.hasta" outlined dense type="date" label="Hasta" />
      </div>
      <div class="col-12 col-md-2">
        <q-btn unelevated color="positive" icon="clear" label="Limpiar" class="full-width" @click="$emit('limpiar')" />
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
defineProps({ model: { type: Object, required: true } })
defineEmits(['limpiar'])
const estados = [
  { label: 'Todos', value: '' },
  { label: 'Pendiente', value: 'PENDIENTE' },
  { label: 'Parcial', value: 'PARCIAL' },
  { label: 'Vencido', value: 'VENCIDO' },
  { label: 'Pagado', value: 'PAGADO' }
]
</script>

<style scoped>
.filter-card { border-radius: 14px; }
</style>

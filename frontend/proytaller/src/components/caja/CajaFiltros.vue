<template>
  <q-card flat bordered class="filters-card">
    <q-card-section class="row q-col-gutter-md items-end">
      <div class="col-12 col-md-3">
        <q-input v-model="model.busqueda" outlined dense label="Buscar cajero o caja" clearable>
          <template #prepend><q-icon name="search" /></template>
        </q-input>
      </div>

      <div class="col-12 col-sm-4 col-md-2">
        <q-select v-model="model.caja" outlined dense label="Caja" emit-value map-options :options="cajas" />
      </div>

      <div class="col-12 col-sm-4 col-md-2">
        <q-input v-model="model.desde" outlined dense type="date" label="Desde"
          :max="model.hasta || hoy" :error="!!errorDesde" :error-message="errorDesde" />
      </div>

      <div class="col-12 col-sm-4 col-md-2">
        <q-input v-model="model.hasta" outlined dense type="date" label="Hasta"
          :min="model.desde || undefined" :max="hoy" :error="!!errorHasta" :error-message="errorHasta" />
      </div>

      <div class="col-12 col-md-3">
        <q-btn unelevated color="positive" icon="clear" label="Limpiar filtros" class="full-width"
          @click="$emit('limpiar')" />
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ model: { type: Object, required: true } })
defineEmits(['limpiar'])

const fechaActual = new Date()
const hoy = `${fechaActual.getFullYear()}-${String(fechaActual.getMonth() + 1).padStart(2, '0')}-${String(fechaActual.getDate()).padStart(2, '0')}`

const errorDesde = computed(() => {
  if (props.model.desde && props.model.desde > hoy) return 'No puede seleccionar una fecha futura.'
  if (props.model.desde && props.model.hasta && props.model.desde > props.model.hasta) {
    return 'La fecha desde no puede ser posterior a hasta.'
  }
  return ''
})

const errorHasta = computed(() => {
  if (props.model.hasta && props.model.hasta > hoy) return 'No puede seleccionar una fecha futura.'
  if (props.model.desde && props.model.hasta && props.model.desde > props.model.hasta) {
    return 'La fecha hasta debe ser igual o posterior a desde.'
  }
  return ''
})

const cajas = [
  { label: 'Todas', value: '' },
  ...Array.from({ length: 8 }, (_, i) => ({ label: `Caja ${String(i + 1).padStart(2, '0')}`, value: String(i + 1) }))
]
</script>

<style scoped>
.filters-card { border-radius: 15px; }
</style>

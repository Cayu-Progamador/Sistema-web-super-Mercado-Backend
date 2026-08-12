<template>
  <q-card class="prod-filters" flat>
    <q-card-section class="q-pa-sm row items-end q-col-gutter-sm">
      <div class="col-12 col-md-4 col-lg-2">
        <q-select v-model="model.categoria" :options="categorias" label="Categoría" outlined dense clearable color="primary" emit-value map-options />
      </div>
      <div class="col-12 col-md-4 col-lg-2">
        <q-select v-model="model.subcategoria" :options="subcategorias" label="Subcategoría" outlined dense clearable color="primary" emit-value map-options />
      </div>
      <div class="col-12 col-md-4 col-lg-2">
        <q-select v-model="model.marca" :options="marcas" label="Marca" outlined dense clearable color="primary" emit-value map-options />
      </div>
      <div class="col-12 col-md-4 col-lg-2">
        <q-select v-model="model.estado" :options="estados" label="Estado" outlined dense clearable color="primary" emit-value map-options />
      </div>
      <div class="col-12 col-md-4 col-lg-2">
        <q-select v-model="model.unidadBase" :options="unidades" label="Unidad base" outlined dense clearable color="primary" emit-value map-options />
      </div>
    </q-card-section>

    <q-card-section class="q-pt-none q-px-sm q-pb-sm">
      <div class="row justify-end q-gutter-sm">
        <q-btn class="prod-btn prod-btn-cancelar" label="Limpiar" icon="close" no-caps @click="$emit('limpiar')" />
        <q-btn class="prod-btn prod-btn-buscar" label="Buscar" icon="search" no-caps @click="$emit('buscar')" />
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  categorias: { type: Array, default: () => [] },
  subcategorias: { type: Array, default: () => [] },
  marcas: { type: Array, default: () => [] },
  unidades: { type: Array, default: () => [] },
  modelo: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['update', 'buscar', 'limpiar','filtro'])

const estados = [
  { label: 'Activo', value: 'activo' },
  { label: 'Inactivo', value: 'inactivo' }
]

const model = reactive({
  categoria: null,
  subcategoria: null,
  marca: null,
  estado: null,
  unidadBase: null
})

watch(model, (val) => emit('filtro', { ...val }), { deep: true, immediate: true })

watch(
  () => props.modelo,
  (val) => {
    if (val) {
      Object.assign(model, val)
    }
  },
  { deep: true }
)
</script>

<style scoped>
.prod-filters {
  background: #ffffff;
  border: 1.5px solid #c8e6c9;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(46, 125, 50, 0.06);
}
.prod-filters :deep(.q-field__control) {
  border-radius: 8px;
  background: #ffffff;
  border: 1px solid #c8e6c9;
}
.prod-btn {
  border-radius: 8px;
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
}
.prod-btn-buscar {
  background: #2E7D32;
  color: #ffffff;
}
.prod-btn-cancelar {
  color: #5B6675;
  border: 1px solid #d0d5dd;
}
</style>
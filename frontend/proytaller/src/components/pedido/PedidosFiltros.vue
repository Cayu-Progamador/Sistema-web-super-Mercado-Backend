<template>
  <q-card flat bordered class="filters-card q-mb-md q-pa-md">
    <div class="row q-col-gutter-md">
      <div class="col-12 col-md-3">
        <q-select
          v-model="filtros.idProveedor"
          :options="proveedorOpciones"
          label="Proveedor"
          outlined
          dense
          clearable
          emit-value
          map-options
          color="green-8"
          placeholder="Todos los proveedores"
          class="filter-field"
        />
      </div>

      <div class="col-12 col-md-3">
        <q-select
          v-model="filtros.estado"
          :options="estadoOpciones"
          label="Estado"
          outlined
          dense
          clearable
          emit-value
          map-options
          color="green-8"
          placeholder="Todos los estados"
          class="filter-field"
        />
      </div>

      <div class="col-12 col-md-3">
        <q-input v-model="filtros.fechaDesde" label="Fecha desde" outlined dense type="date" color="green-8" class="filter-field" />
      </div>

      <div class="col-12 col-md-3">
        <q-input v-model="filtros.fechaHasta" label="Fecha hasta" outlined dense type="date" color="green-8" class="filter-field" />
      </div>

      <div class="col-12 row justify-end q-gutter-sm q-mt-sm">
        <q-btn flat color="green-8" icon="clear" label="Limpiar" @click="limpiar" no-caps />
        <q-btn unelevated color="green-8" icon="search" label="Buscar" @click="aplicar" no-caps />
      </div>
    </div>
  </q-card>
</template>

<script setup>
import { reactive, computed, ref, onMounted } from 'vue'
import { listarEstados } from '../../api/pedido/pedido'

const props = defineProps({
  proveedores: { type: Array, default: () => [] }
})

const emit = defineEmits(['filtrar', 'limpiar'])

const estados = ref([])

const proveedorOpciones = computed(() =>
  props.proveedores.map((p) => ({ label: p.nombre, value: p.id }))
)

const estadoOpciones = computed(() =>
  estados.value.map((e) => ({ label: e.replace(/_/g, ' '), value: e }))
)

const filtros = reactive({
  idProveedor: null,
  estado: null,
  fechaDesde: null,
  fechaHasta: null
})

onMounted(async () => {
  try {
    estados.value = await listarEstados()
  } catch (e) {
    estados.value = []
  }
})

function aplicar() {
  emit('filtrar', { ...filtros })
}

function limpiar() {
  filtros.idProveedor = null
  filtros.estado = null
  filtros.fechaDesde = null
  filtros.fechaHasta = null
  emit('limpiar')
}
</script>

<style scoped>
.filters-card {
  border-radius: 12px;
  background: white;
  border: 1px solid #e5e7eb;
}

.filter-field :deep(.q-field__control) {
  border-radius: 8px;
  border: 1.5px solid #2E7D32 !important;
  box-shadow: none !important;
}

.filter-field :deep(.q-field__control:focus-within) {
  border-color: #2E7D32 !important;
  box-shadow: 0 0 0 3px rgba(46,125,50,0.15) !important;
}

.filter-field :deep(.q-field__before),
.filter-field :deep(.q-field__after) {
  border: none !important;
}

.filter-field :deep(.q-field__control::before),
.filter-field :deep(.q-field__control::after) {
  display: none !important;
}

.filter-field :deep(input),
.filter-field :deep(.q-field__label),
.filter-field :deep(.q-icon) {
  color: #1B5E20 !important;
}
</style>

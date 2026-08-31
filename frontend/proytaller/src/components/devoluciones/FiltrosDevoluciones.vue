<template>
  <q-card flat bordered class="dv-filters-card q-mb-md q-pa-md">
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
          class="dv-filter-field"
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
          class="dv-filter-field"
        />
      </div>

      <div class="col-12 col-md-3">
        <q-input
          v-model="filtros.fechaDesde"
          label="Fecha desde"
          outlined
          dense
          type="date"
          color="green-8"
          class="dv-filter-field"
        />
      </div>

      <div class="col-12 col-md-3">
        <q-input
          v-model="filtros.fechaHasta"
          label="Fecha hasta"
          outlined
          dense
          type="date"
          color="green-8"
          class="dv-filter-field"
        />
      </div>

      <div class="col-12 row justify-end q-gutter-sm q-mt-sm">
        <q-btn flat color="green-8" icon="clear" label="Limpiar" no-caps @click="limpiar" />
        <q-btn unelevated color="green-8" icon="search" label="Buscar" no-caps @click="aplicar" />
      </div>
    </div>
  </q-card>
</template>

<script setup>
import { computed, reactive } from 'vue'

const props = defineProps({
  proveedores: { type: Array, default: () => [] },
  estados: { type: Array, default: () => [] }
})

const emit = defineEmits(['filtrar', 'limpiar'])

const proveedorOpciones = computed(() =>
  props.proveedores.map((p) => ({ label: p.nombre, value: p.id }))
)

const estadoOpciones = computed(() =>
  props.estados.map((estado) => ({ label: estado.replace(/_/g, ' '), value: estado }))
)

const filtros = reactive({
  idProveedor: null,
  estado: null,
  fechaDesde: null,
  fechaHasta: null
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

<style scoped src="../../assets/styles/devoluciones/devoluciones.css"></style>

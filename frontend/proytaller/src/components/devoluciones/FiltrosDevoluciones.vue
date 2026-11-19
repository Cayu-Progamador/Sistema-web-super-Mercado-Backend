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
          :max="filtros.fechaHasta || hoy"
          :error="!!errorFechaDesde"
          :error-message="errorFechaDesde"
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
          :min="filtros.fechaDesde || undefined"
          :max="hoy"
          :error="!!errorFechaHasta"
          :error-message="errorFechaHasta"
          class="dv-filter-field"
        />
      </div>

      <div class="col-12 row justify-end q-gutter-sm q-mt-sm">
        <q-btn flat color="green-8" icon="clear" label="Limpiar" no-caps @click="limpiar" />
        <q-btn unelevated color="green-8" icon="search" label="Buscar" no-caps :disable="rangoFechasInvalido" @click="aplicar" />
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

const fechaActual = new Date()
const hoy = `${fechaActual.getFullYear()}-${String(fechaActual.getMonth() + 1).padStart(2, '0')}-${String(fechaActual.getDate()).padStart(2, '0')}`

const errorFechaDesde = computed(() => {
  if (filtros.fechaDesde && filtros.fechaDesde > hoy) return 'No puede seleccionar una fecha futura.'
  if (filtros.fechaDesde && filtros.fechaHasta && filtros.fechaDesde > filtros.fechaHasta) {
    return 'La fecha desde no puede ser mayor que la fecha hasta.'
  }
  return ''
})

const errorFechaHasta = computed(() => {
  if (filtros.fechaHasta && filtros.fechaHasta > hoy) return 'No puede seleccionar una fecha futura.'
  if (filtros.fechaDesde && filtros.fechaHasta && filtros.fechaHasta < filtros.fechaDesde) {
    return 'La fecha hasta no puede ser menor que la fecha desde.'
  }
  return ''
})

const rangoFechasInvalido = computed(() =>
  !!errorFechaDesde.value || !!errorFechaHasta.value
)

function aplicar() {
  if (rangoFechasInvalido.value) return
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

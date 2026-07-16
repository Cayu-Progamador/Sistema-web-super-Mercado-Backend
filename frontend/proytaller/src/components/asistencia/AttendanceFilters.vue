<template>
  <q-slide-transition>
    <div v-show="visible">
      <q-card flat bordered class="filters-card q-mb-md q-pa-md">
        <div class="row q-col-gutter-md items-end">
          <div class="col-12 col-md-2">
            <q-select
              v-model="filtros.sucursal"
              :options="sucursalOptions"
              label="Sucursal"
              outlined
              dense
              clearable
              class="filter-field"
              emit-value
              map-options
            />
          </div>
          <div class="col-12 col-md-2">
            <q-select
              v-model="filtros.departamento"
              :options="departamentoOptions"
              label="Departamento"
              outlined
              dense
              clearable
              class="filter-field"
              emit-value
              map-options
            />
          </div>
          <div class="col-12 col-md-2">
            <q-select
              v-model="filtros.turno"
              :options="turnoOptions"
              label="Turno"
              outlined
              dense
              clearable
              class="filter-field"
              emit-value
              map-options
            />
          </div>
          <div class="col-12 col-md-3">
            <q-select
              v-model="filtros.estado"
              :options="estadoOptions"
              label="Estado"
              outlined
              dense
              clearable
              class="filter-field"
              emit-value
              map-options
            />
          </div>
          <div class="col-12 col-md-3 row q-gutter-sm justify-end">
            <q-btn flat no-caps color="grey" icon="clear" label="Limpiar" @click="limpiar" />
            <q-btn unelevated no-caps color="primary" icon="search" label="Buscar" @click="$emit('aplicar-filtros', { ...filtros })" />
          </div>
        </div>
      </q-card>
    </div>
  </q-slide-transition>
</template>

<script setup>
import { reactive } from 'vue'

defineProps({
  visible: { type: Boolean, default: false }
})

const emit = defineEmits(['aplicar-filtros'])

const filtros = reactive({
  sucursal: null,
  departamento: null,
  turno: null,
  estado: null
})

const sucursalOptions = [
  { label: 'Central', value: 1 },
  { label: 'Sucursal Norte', value: 2 },
  { label: 'Sucursal Sur', value: 3 }
]

const departamentoOptions = [
  { label: 'Ventas', value: 1 },
  { label: 'Cajas', value: 2 },
  { label: 'Bodega', value: 3 },
  { label: 'Atenci&oacute;n al cliente', value: 4 },
  { label: 'Administraci&oacute;n', value: 5 }
]

const turnoOptions = [
  { label: 'Matutino', value: 1 },
  { label: 'Vespertino', value: 2 },
  { label: 'Nocturno', value: 3 }
]

const estadoOptions = [
  { label: 'A tiempo', value: 'A_TIEMPO' },
  { label: 'Tardanza', value: 'TARDANZA' },
  { label: 'Falta', value: 'FALTA' },
  { label: 'Permiso', value: 'PERMISO' },
  { label: 'Pendiente', value: 'PENDIENTE' }
]

function limpiar() {
  filtros.sucursal = null
  filtros.departamento = null
  filtros.turno = null
  filtros.estado = null
  emit('aplicar-filtros', { ...filtros })
}
</script>

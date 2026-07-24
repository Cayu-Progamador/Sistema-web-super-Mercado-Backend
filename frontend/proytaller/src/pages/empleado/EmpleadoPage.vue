<template>
  <q-page class="q-pa-md bg-teal-1">
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4 text-bold text">Gestion de Empleados</div>
    </div>
    <EmpleadoDashboard :key="dashboardKey" />
    <EmpleadoFiltros
      :filters="filterState"
      @search="onSearch"
      @reset="onReset"
    />
    <q-card flat bordered class="q-pa-md">
      <EmpleadoTable
        :external-filters="appliedFilters"
        @actualizado="recargarDashboard"
      />
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, reactive } from 'vue'
import EmpleadoDashboard from '../../components/empleado/EmpleadoDashboard.vue'
import EmpleadoFiltros from '../../components/empleado/EmpleadoFiltros.vue'
import EmpleadoTable from '../../components/empleado/EmpleadoTable.vue'

const filterState = reactive({
  search: '',
  estado: null,
  fechaDesde: null,
  fechaHasta: null,
  ordenarPor: 'recientes'
})

const appliedFilters = ref(null)
const dashboardKey = ref(0)

function recargarDashboard() {
  dashboardKey.value++
}

function onSearch(filters) {
  appliedFilters.value = { ...filters }
}

function onReset() {
  appliedFilters.value = null
  filterState.search = ''
  filterState.estado = null
  filterState.fechaDesde = null
  filterState.fechaHasta = null
  filterState.ordenarPor = null
}
</script>

<style>
.text {
  font-family: 'DM Sans', sans-serif !important;
  color: #2a5c1a;
  user-select: none;
}
</style>

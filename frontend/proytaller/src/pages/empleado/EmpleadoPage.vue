<template>
  <q-page class="q-pa-md bg-teal-1">
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4 text-bold text">Gesti&oacute;n de Empleados</div>
    </div>
    <q-card flat bordered class="q-pa-md">
      <EmpleadoTable />
    </q-card>
  </q-page>
</template>

<script setup>
import EmpleadoTable from '../../components/empleado/EmpleadoTable.vue'
</script>

<style>
.text {
  font-family: 'DM Sans', sans-serif !important;
  color: #2a5c1a;
  user-select: none;
}
</style>

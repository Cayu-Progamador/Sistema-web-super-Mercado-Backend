<template>
  <q-page class="q-pa-md bg-teal-1">
    <!-- Cabecera de la página -->
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4 text-bold text">Gestión de Usuarios</div>
    </div>

    <!-- Aquí irá tu componente <UsuarioTable /> en el futuro -->
    <q-card flat bordered class="q-pa-md text-center text-grey">
      <UsuarioTable />
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue';
import UsuarioTable from '../../components/usuarios/UsuarioTable.vue'

</script>
<style>
.text {
  font-family: 'DM Sans', sans-serif !important;
  color: #2a5c1a;
  user-select: none;
} 
</style>
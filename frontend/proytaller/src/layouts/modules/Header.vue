<template>
  <q-toolbar class="q-pa-md">
    <!-- Botón menú -->
    <q-btn 
      flat 
      @click="$emit('toggle-drawer')" 
      round 
      dense 
      icon="menu"
    />
    <q-space />
    <div>
      <HandleLogout />
    </div>
  </q-toolbar>
</template>
<script setup>
import  HandleLogout  from '../../components/HandleLogout.vue' 
</script>



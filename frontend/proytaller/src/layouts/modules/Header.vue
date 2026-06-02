<template>
  <q-toolbar class="custom-toolbar">

    <!-- Botón menú -->
    <q-btn
      flat
      @click="$emit('toggle-drawer')"
      round
      dense
      icon="menu"
      class="menu-btn"
    />

    <q-space />

    <!-- Título / breadcrumb -->
    <div class="toolbar-brand">
      <span class="brand-dot"></span>
      Sistema de Gestión
    </div>

    <q-space />

    <HandleLogout />

  </q-toolbar>
</template>

<script setup>
import HandleLogout from '../../components/logout/HandleLogout.vue'
</script>

<style scoped src="../../assets/styles/layout/header.css">

</style>
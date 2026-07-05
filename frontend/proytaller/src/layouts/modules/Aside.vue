<template>
  <q-scroll-area class="fit" :horizontal-thumb-style="{ opacity: 0 }">
    <q-list padding class="sidebar-list">

      <!-- Logo -->
      <div v-show="!isMini" class="q-pa-sm text-center q-mb-sm logo-area">
        <LogoSupermercado />
      </div>

      <q-separator v-show="!isMini" class="sidebar-separator q-mb-sm" />

      <!-- Dashboard -->
      <q-item clickable v-ripple to="/" exact class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="dashboard" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Dashboard</q-item-section>
      </q-item>

      <!-- Usuarios -->
      <q-item clickable v-ripple to="/usuarios" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="people" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Usuarios</q-item-section>
      </q-item>
      
      <!-- perfil modulo -->
      <q-item clickable v-ripple to="/perfil" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="person" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Perfil</q-item-section>
      </q-item>
<!-- Gesrion de roles -->
      <q-item clickable v-ripple to="/roles" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="security" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Roles</q-item-section>
      </q-item>

      <!-- Gestion de empleados -->
      <q-item clickable v-ripple to="/empleado" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="people" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Empleados</q-item-section>
      </q-item>  

      
    </q-list>
  </q-scroll-area>
</template>

<script setup>
import LogoSupermercado from '../../components/logo/LogoSupermercado.vue'

defineProps({
  isMini: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped src="../../assets/styles/layout/aside.css">

</style>
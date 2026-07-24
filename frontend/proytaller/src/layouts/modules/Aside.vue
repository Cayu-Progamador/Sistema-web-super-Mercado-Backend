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

      <q-separator v-show="!isMini" class="sidebar-separator q-my-sm" />

      <div v-show="!isMini" class="sidebar-section-label">General</div>

      <q-item clickable v-ripple to="/usuarios" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="people" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Usuarios</q-item-section>
      </q-item>

      <q-item clickable v-ripple to="/roles" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="security" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Roles</q-item-section>
      </q-item>

      <q-item clickable v-ripple to="/perfil" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="person" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Perfil</q-item-section>
      </q-item>

      <q-separator v-show="!isMini" class="sidebar-separator q-my-sm" />

      <div v-show="!isMini" class="sidebar-section-label">RRHH</div>

      <q-item clickable v-ripple to="/empleado" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="badge" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Empleados</q-item-section>
      </q-item>

      <q-item clickable v-ripple to="/cargos" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="work_history" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Cargos</q-item-section>
      </q-item>

      <q-item clickable v-ripple to="/contratos" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="history_edu" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Contratos</q-item-section>
      </q-item>

      <q-item v-if="tieneAcceso" clickable v-ripple to="/asistencia" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="person_pin" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Mi Asistencia</q-item-section>
      </q-item>

      <q-item v-if="tieneAcceso" clickable v-ripple to="/asistencia/admin" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="admin_panel_settings" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Panel Asistencia</q-item-section>
      </q-item>

    </q-list>
  </q-scroll-area>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import LogoSupermercado from '../../components/logo/LogoSupermercado.vue'
import { tieneAccesoAsistencia } from '../../api/asistencia/asistencia'

defineProps({
  isMini: {
    type: Boolean,
    default: false
  }
})

const tieneAcceso = ref(false)
const esAdmin = ref(false)

onMounted(async () => {
  try {
    const res = await tieneAccesoAsistencia()
    tieneAcceso.value = res.tieneAcceso
    esAdmin.value = res.esAdmin
  } catch {
    tieneAcceso.value = false
    esAdmin.value = false
  }
})
</script>

<style scoped src="../../assets/styles/layout/aside.css">

</style>
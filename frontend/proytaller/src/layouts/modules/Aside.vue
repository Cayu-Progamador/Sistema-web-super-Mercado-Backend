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

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800&display=swap');

/* ── Fondo del sidebar ── */
.fit {
  background: #7aaa4e  !important;
  border-right: 1px solid #e4edd8;
}

/* ── Lista ── */
.sidebar-list {
  font-family: 'Nunito', sans-serif;
}

/* ── Área del logo ── */
.logo-area {
  padding-top: 1rem !important;
  padding-bottom: 0.5rem !important;
}

/* ── Separador ── */
.sidebar-separator {
  background: #e4edd8 !important;
}

/* ── Items base ── */
.sidebar-item {
  border-radius: 10px !important;
  margin: 2px 8px !important;
  transition: background 0.18s, color 0.18s !important;
  min-height: 44px !important;
}

.sidebar-item:hover {
  background: #f0f7e8 !important;
}

.sidebar-item:hover .sidebar-icon {
  color: #4a8c25 !important;
}

.sidebar-item:hover .sidebar-label {
  color: #2a5c1a !important;
}

/* ── Item activo ── */
.sidebar-item-active {
  background: #eaf4d8 !important;
  border-left: 3px solid #4a8c25 !important;
  border-radius: 0 10px 10px 0 !important;
  margin-left: 0 !important;
  padding-left: 13px !important;
}

.sidebar-item-active .sidebar-icon {
  color: #4a8c25 !important;
}

.sidebar-item-active .sidebar-label {
  color: #2a5c1a !important;
  font-weight: 700 !important;
}

/* ── Ícono ── */
.sidebar-icon {
  color: #fff !important;
  font-size: 22px !important;
  transition: color 0.18s !important;
}

/* ── Label ── */
.sidebar-label {
  font-family: 'Nunito', sans-serif !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  color: #fff !important;
  transition: color 0.18s !important;
}
</style>
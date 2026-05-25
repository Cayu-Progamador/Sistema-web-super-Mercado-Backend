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

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800&display=swap');

/* ── Toolbar ── */
.custom-toolbar {
  background: #0f6e56 !important;
  border-bottom: 1px solid #e4edd8 !important;
  min-height: 60px !important;
  padding: 0 1.25rem !important;
  font-family: 'Nunito', sans-serif;
}

/* ── Botón menú ── */
.menu-btn {
  color: #7aaa4e !important;
  transition: background 0.18s, color 0.18s !important;
}
.menu-btn:hover {
  background: #f0f7e8 !important;
  color: #4a8c25 !important;
}

/* ── Texto central ── */
.toolbar-brand {
  display: flex;
  align-items: center;
  gap: 7px;
  font-family: 'Nunito', sans-serif;
  font-size: 14px;
  font-weight: 700;
  color: #7aaa4e;
  letter-spacing: 0.03em;
}

.brand-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #d97b1a;
  flex-shrink: 0;
}
</style>
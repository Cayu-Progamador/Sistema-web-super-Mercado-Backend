<template>
  <q-card class="types-card">
    <div class="types-header">
      <q-icon name="category" size="18px" color="#2E7D32" />
      <span class="types-title">Tipos de permiso</span>
    </div>
    <q-separator class="types-sep" />
    <div class="types-list">
      <div
        v-for="item in items"
        :key="item.id"
        class="type-row"
      >
        <div class="type-dot" :style="{ background: item.color }"></div>
        <div class="type-info">
          <div class="type-name">{{ item.nombre }}</div>
          <div class="type-desc">{{ item.descripcion }}</div>
        </div>
      </div>
    </div>
  </q-card>
</template>

<script setup>
defineProps({
  items: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.types-card {
  border-radius: 14px !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06) !important;
  border: 1px solid #E8EDF2 !important;
  overflow: hidden !important;
}
.types-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px;
}
.types-title {
  font-size: 15px;
  font-weight: 700;
  color: #1B5E20;
}
.types-sep {
  background: #E8EDF2 !important;
}
.types-list {
  padding: 6px 0;
}
.type-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  transition: background 0.15s;
}
.type-row:hover {
  background: #F0F4F8;
}
.type-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}
.type-info {
  flex: 1;
  min-width: 0;
}
.type-name {
  font-size: 13px;
  font-weight: 600;
  color: #2D3748;
}
.type-desc {
  font-size: 11px;
  color: #718096;
  margin-top: 1px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

<template>
  <div class="prod-toolbar prod-toolbar-grid">
    <div class="prod-toolbar-actions">
      <q-btn class="prod-btn prod-btn-primario" label="Nuevo Producto" icon="add" no-caps @click="$emit('nuevo')" />
      <q-btn class="prod-btn prod-btn-importar" label="Importar" icon="cloud_upload" no-caps @click="$emit('importar')" />
      <q-btn class="prod-btn prod-btn-exportar" label="Exportar" icon="download" no-caps @click="$emit('exportar')" />
      <q-btn class="prod-btn prod-btn-icono" icon="refresh" @click="$emit('actualizar')">
        <q-tooltip class="bg-primary text-white">Recargar</q-tooltip>
      </q-btn>
      <q-btn
        class="prod-btn prod-btn-filtro"
        :icon="filtrosVisibles ? 'filter_alt_off' : 'filter_alt'"
        :label="filtrosVisibles ? 'Ocultar filtros' : 'Filtros'"
        no-caps
        @click="$emit('toggle-filtros')"
      >
        <q-tooltip>Mostrar / ocultar panel de filtros</q-tooltip>
      </q-btn>
    </div>
    <q-input
      :model-value="busqueda"
      @update:model-value="$emit('update:busqueda', $event)"
      outlined
      dense
      placeholder="Buscar por nombre, código o marca..."
      class="prod-search"
      clearable
    >
      <template v-slot:prepend>
        <q-icon name="search" size="18px" />
      </template>
    </q-input>
  </div>
</template>

<script setup>
defineProps({
  busqueda: { type: String, default: '' },
  filtrosVisibles: { type: Boolean, default: false }
})

defineEmits(['nuevo', 'importar', 'exportar', 'actualizar', 'update:busqueda'])
</script>

<style scoped>
.prod-toolbar {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  background: transparent;
}
.prod-toolbar-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.prod-btn {
  border-radius: 8px;
  font-family: 'Nunito', sans-serif;
}
.prod-btn-primario {
  background: #2E7D32 !important;
  color: #ffffff !important;
  font-weight: 700;
  box-shadow: 0 2px 6px rgba(46, 125, 50, 0.3);
}
.prod-btn-importar {
  background: #388E3C !important;
  color: #ffffff !important;
  font-weight: 600;
}
.prod-btn-exportar {
  background: #ffffff;
  color: #2E7D32;
  border: 1.5px solid #c8e6c9;
  font-weight: 600;
}
.prod-btn-exportar:hover {
  background: #f1f8e9;
}
.prod-btn-icono {
  color: #2E7D32;
  border: 1.5px solid #c8e6c9;
}
.prod-btn-filtro {
  color: #2E7D32;
  border: 1.5px solid #c8e6c9;
}
.prod-search {
  max-width: 320px;
  width: 100%;
}
.prod-search :deep(.q-field__control) {
  border-radius: 10px;
  background: #ffffff;
  min-height: 40px;
  border: 1.5px solid #c8e6c9;
}
.prod-search :deep(.q-field__control::before) {
  display: none;
}
</style>
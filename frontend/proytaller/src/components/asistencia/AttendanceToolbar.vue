<template>
  <div class="row items-center q-gutter-sm q-mb-md">
    <q-btn dense flat round icon="chevron_left" size="md" class="date-nav-btn" @click="$emit('cambiar-fecha', -1)" />
    <div class="date-label text-center">{{ fechaFormateada }}</div>
    <q-btn dense flat round icon="chevron_right" size="md" class="date-nav-btn" @click="$emit('cambiar-fecha', 1)" />
    <q-btn flat dense no-caps icon="today" size="sm" class="date-nav-btn q-ml-xs" label="Hoy" @click="$emit('ir-hoy')" />

    <q-space />

    <q-input
      v-model="busquedaLocal"
      outlined
      dense
      placeholder="Buscar empleado o c&oacute;digo"
      debounce="300"
      style="min-width: 220px"
      class="filter-field"
      color="teal-9"
      @update:model-value="$emit('buscar', busquedaLocal)"
    >
      <template v-slot:prepend>
        <q-icon name="search" color="teal-9" />
      </template>
    </q-input>

    <q-btn outline no-caps icon="picture_as_pdf" color="orange-8" size="md" class="toolbar-btn q-ml-xs" @click="$emit('exportar-pdf')">
      <q-tooltip>Exportar PDF</q-tooltip>
    </q-btn>
    <q-btn outline no-caps icon="table_view" color="teal-9" size="md" class="toolbar-btn" @click="$emit('exportar-excel')">
      <q-tooltip>Exportar Excel</q-tooltip>
    </q-btn>
    <q-btn outline no-caps icon="refresh" color="light-green-6" size="md" class="toolbar-btn" @click="$emit('actualizar')">
      <q-tooltip>Actualizar</q-tooltip>
    </q-btn>
    <q-btn
      flat no-caps
      :icon="filtrosVisibles ? 'expand_less' : 'filter_list'"
      color="teal-9"
      size="md"
      class="toolbar-btn q-ml-sm"
      :label="filtrosVisibles ? 'Ocultar filtros' : 'Filtros'"
      @click="$emit('toggle-filtros')"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  fechaFormateada: { type: String, default: '' },
  filtrosVisibles: { type: Boolean, default: false }
})

defineEmits(['cambiar-fecha', 'ir-hoy', 'buscar', 'exportar-pdf', 'exportar-excel', 'actualizar', 'toggle-filtros'])

const busquedaLocal = ref('')
</script>

<style scoped>
.date-label {
  font-size: 1.1rem;
  font-weight: 700;
  color: #006051;
  min-width: 240px;
  text-align: center;
  font-family: 'Nunito', sans-serif;
}

.date-nav-btn {
  color: #006051 !important;
  border-radius: 12px !important;
}

.date-nav-btn:hover {
  background: #e0f5f0 !important;
}

.filter-field :deep(.q-field--outlined .q-field__control) {
  border-radius: 12px;
  background: white;
}

.filter-field :deep(.q-field--outlined .q-field__control::before) {
  border: 2px solid #bce9e2 !important;
  border-radius: 12px;
  transition: border-color 0.2s ease;
}

.filter-field :deep(.q-field--outlined .q-field__control:hover::before) {
  border-color: #006051 !important;
}

.filter-field :deep(.q-field--outlined .q-field__control:focus-within::before) {
  border-color: #006051 !important;
  box-shadow: 0 0 0 3px rgba(0,96,81,0.1);
}

.filter-field :deep(.q-field__native) {
  font-family: 'Nunito', sans-serif;
  font-size: 0.85rem;
  color: #333;
}

.filter-field :deep(.q-field__native::placeholder) {
  color: #8BC34A;
  opacity: 0.7;
}

.filter-field :deep(.q-field--outlined .q-field__control .q-icon) {
  color: #006051 !important;
  font-size: 1.2rem;
}

.toolbar-btn {
  border-radius: 10px !important;
  font-weight: 600 !important;
  font-family: 'Nunito', sans-serif !important;
  transition: all 0.2s ease !important;
}

.toolbar-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.filter-field :deep(.q-field__native) {
  font-family: 'Nunito', sans-serif;
  font-size: 0.85rem;
  color: #333;
}

.filter-field :deep(.q-field__native::placeholder) {
  color: #8BC34A;
  opacity: 0.7;
}

.filter-field :deep(.q-icon) {
  color: #006051;
  font-size: 1.2rem;
}
</style>

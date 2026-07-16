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
      @update:model-value="$emit('buscar', busquedaLocal)"
    >
      <template v-slot:prepend>
        <q-icon name="search" color="primary" />
      </template>
    </q-input>

    <q-btn outline no-caps dense icon="picture_as_pdf" color="red" size="sm" class="toolbar-btn" @click="$emit('exportar-pdf')">
      <q-tooltip>Exportar PDF</q-tooltip>
    </q-btn>
    <q-btn outline no-caps dense icon="table_view" color="positive" size="sm" class="toolbar-btn" @click="$emit('exportar-excel')">
      <q-tooltip>Exportar Excel</q-tooltip>
    </q-btn>
    <q-btn outline no-caps dense icon="refresh" color="primary" size="sm" class="toolbar-btn" @click="$emit('actualizar')">
      <q-tooltip>Actualizar</q-tooltip>
    </q-btn>
    <q-btn
      flat no-caps dense
      :icon="filtrosVisibles ? 'expand_less' : 'filter_list'"
      color="primary"
      size="sm"
      class="toolbar-btn"
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

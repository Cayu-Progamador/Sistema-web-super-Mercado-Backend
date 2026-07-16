<template>
  <q-card flat bordered class="q-pa-md" style="border-radius:16px">
    <div class="row items-center justify-between q-mb-md">
      <div class="text-weight-bold text-h6" style="color:#1B5E20">
        <q-icon name="calendar_view_week" class="q-mr-sm" />
        Historial semanal
      </div>
      <q-btn
        outline
        dense
        no-caps
        color="primary"
        icon="download"
        label="Descargar reporte"
        size="sm"
        @click="$emit('descargar')"
      />
    </div>
    <q-table
      flat
      :rows="rows"
      :columns="columns"
      row-key="dia"
      hide-pagination
      :rows-per-page-options="[0]"
      class="weekly-table"
    >
      <template v-slot:body-cell-dia="props">
        <q-td :props="props">
          <div>
            <div class="text-weight-medium">{{ props.row.dia }}</div>
            <div class="text-caption text-grey">{{ props.row.fecha }}</div>
          </div>
        </q-td>
      </template>
      <template v-slot:body-cell-horas="props">
        <q-td :props="props">
          <span class="text-weight-medium">{{ props.row.horas }}</span>
        </q-td>
      </template>
      <template v-slot:body-cell-estado="props">
        <q-td :props="props">
          <q-chip
            :color="chipColor(props.row.estado)"
            text-color="white"
            size="sm"
            class="estado-chip"
          >
            {{ props.row.estado }}
          </q-chip>
        </q-td>
      </template>
      <template v-slot:no-data>
        <div class="text-center q-pa-md text-grey">
          <q-icon name="info" size="24px" class="q-mr-sm" />
          No hay registros esta semana
        </div>
      </template>
    </q-table>
  </q-card>
</template>

<script setup>
defineEmits(['descargar'])

const props = defineProps({
  rows: {
    type: Array,
    default: () => []
  }
})

const columns = [
  { name: 'dia', label: 'D&iacute;a', align: 'left', field: 'dia' },
  { name: 'entrada', label: 'Entrada', align: 'center', field: 'entrada' },
  { name: 'salida', label: 'Salida', align: 'center', field: 'salida' },
  { name: 'horas', label: 'Horas', align: 'center', field: 'horas' },
  { name: 'estado', label: 'Estado', align: 'center', field: 'estado' }
]

function chipColor(estado) {
  switch (estado) {
    case 'A tiempo': return 'positive'
    case 'Tardanza': return 'orange'
    case 'Falta': return 'negative'
    case 'Justificado': return 'blue'
    case 'Pendiente salida': return 'warning'
    default: return 'grey'
  }
}
</script>

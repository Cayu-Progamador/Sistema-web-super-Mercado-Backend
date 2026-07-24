<template>
  <q-card flat bordered class="weekly-card q-pa-md">
    <div class="row items-center justify-between q-mb-md">
      <div class="text-weight-bold text-h6" style="color:#006051">
        <q-icon name="calendar_view_week" class="q-mr-sm" />
        Historial semanal
      </div>
      <q-btn
        unelevated
        dense
        no-caps
        icon="download"
        label="Descargar reporte"
        size="sm"
        @click="$emit('descargar')"
        style="background:#006051; color:#ffffff; border-radius:10px; font-weight:700; padding:10px 24px; font-size:0.85rem"
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
      style="color:#006051"
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
  { name: 'dia', label: 'Dia', align: 'left', field: 'dia' },
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

<style scoped>
.weekly-card {
  border-radius: 16px !important;
  border-color: #bce9e2 !important;
  background: #ffffff !important;
  box-shadow: 0 2px 16px rgba(0, 96, 81, 0.07) !important;
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
}

.weekly-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06), 0 2px 8px rgba(0, 0, 0, 0.04) !important;
  transform: translateY(-3px);
  border-color: rgba(0, 0, 0, 0.06) !important;
}

:deep(.weekly-table thead th) {
  color: #006051 !important;
  font-weight: 700 !important;
  font-size: 0.8rem !important;
  text-transform: uppercase !important;
  letter-spacing: 0.5px !important;
  border-bottom: 2px solid #bce9e2 !important;
}

:deep(.weekly-table tbody td) {
  color: #006051 !important;
  font-weight: 500 !important;
  border-bottom: 1px solid #e8f5e9 !important;
}

:deep(.weekly-table tbody tr:hover td) {
  background: #f1faf1 !important;
}

:deep(.weekly-table .q-table__card) {
  background: transparent !important;
  box-shadow: none !important;
}

:deep(.weekly-table .text-grey) {
  color: #8BC34A !important;
}
</style>

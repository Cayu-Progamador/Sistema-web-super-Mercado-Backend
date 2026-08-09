<template>
  <q-table
    :rows="rows"
    :columns="columns"
    :pagination="paginacion"
    :rows-per-page-options="[5, 10, 20, 50]"
    :rows-number="rowsNumber"
    row-key="id"
    class="pv-table"
    flat
    :loading="loading"
    :visible-columns="visibleColumns"
    @request="onRequest"
  >
    <template v-slot:loading>
      <q-inner-loading showing color="primary" />
    </template>

    <template v-slot:body-cell-proveedor="props">
      <q-td :props="props">
        <div class="pv-prov">
          <q-avatar :color="props.row.color" text-color="white" size="38px" font-size="15px" class="pv-avatar">
            {{ initials(props.row.nombre) }}
          </q-avatar>
          <div class="pv-prov-meta">
            <div class="pv-prov-nombre">{{ props.row.nombre }}</div>
            <div class="pv-prov-sub">{{ props.row.categoria }}</div>
          </div>
        </div>
      </q-td>
    </template>

    <template v-slot:body-cell-tipo="props">
      <q-td :props="props">
        <q-chip :color="props.row.tipo === 'Empresa' ? 'green-8' : 'green-6'" text-color="white" dense class="pv-chip-tipo">
          <q-icon :name="props.row.tipo === 'Empresa' ? 'apartment' : 'person'" size="15px" />
          {{ props.row.tipo }}
        </q-chip>
      </q-td>
    </template>

    <template v-slot:body-cell-documento="props">
      <q-td :props="props">
        <template v-if="props.row.documento.tipo === 'CI' || filtroTipo === 'persona'">
          <span class="pv-doc-num">CI: {{ props.row.documento.numero }}</span>
        </template>
        <template v-else>
          <span class="pv-doc-num">{{ props.row.documento.numero }}</span>
        </template>
      </q-td>
    </template>

    <template v-slot:body-cell-contacto="props">
      <q-td :props="props">
        <div class="pv-contacto">
          <div class="pv-contacto-nombre">{{ props.row.contacto.nombre }}</div>
          <div class="pv-contacto-tel">{{ props.row.contacto.telefono }}</div>
        </div>
      </q-td>
    </template>

    <template v-slot:body-cell-ciudad="props">
      <q-td :props="props">
        <span class="pv-ciudad">{{ props.row.ciudad || '—' }}</span>
      </q-td>
    </template>

    <template v-slot:body-cell-estado="props">
      <q-td :props="props">
        <q-chip :color="props.row.estado === 'Activo' ? 'green-7' : 'grey-6'" text-color="white" dense class="pv-chip-estado">
          <q-icon :name="props.row.estado === 'Activo' ? 'check_circle' : 'cancel'" size="14px" />
          {{ props.row.estado }}
        </q-chip>
      </q-td>
    </template>

    <template v-slot:body-cell-acciones="props">
      <q-td :props="props" class="pv-acciones">
        <q-btn flat round dense icon="visibility" size="md" @click="$emit('ver-detalle', props.row)" style="color: #006051">
          <q-tooltip class="bg-primary text-white">Ver detalle</q-tooltip>
        </q-btn>
        <q-btn flat round dense icon="edit" size="md" @click="$emit('editar', props.row)" style="color: #F57C00">
          <q-tooltip class="bg-orange text-white">Editar proveedor</q-tooltip>
        </q-btn>
        <q-btn flat round dense icon="file_download" size="md" @click="$emit('exportar', props.row)" style="color: #8BC34A">
          <q-tooltip class="bg-positive text-white">Exportar</q-tooltip>
        </q-btn>
        <q-btn
          flat
          round
          dense
          :icon="props.row.estado === 'Activo' ? 'pause_circle_outline' : 'play_circle_outline'"
          :color="props.row.estado === 'Activo' ? 'orange-8' : 'green-7'"
          @click="$emit('cambiar-estado', props.row)"
        >
          <q-tooltip class="bg-orange text-white">{{ props.row.estado === 'Activo' ? 'Desactivar' : 'Activar' }}</q-tooltip>
        </q-btn>
      </q-td>
    </template>

    <template v-slot:no-data>
      <div class="pv-no-data">
        <q-icon name="inbox" size="40px" color="grey-4" />
        <div>No se encontraron proveedores con los filtros aplicados.</div>
      </div>
    </template>
  </q-table>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  rows: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  filtroTipo: { type: String, default: 'todos' },
  rowsNumber: { type: Number, default: 0 }
})

const emit = defineEmits(['ver-detalle', 'editar', 'historial', 'cambiar-estado', 'eliminar', 'exportar', 'pagina-cambio'])

const paginacion = {
  sortBy: 'nombre',
  descending: false,
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
}

function onRequest(requestProp) {
  emit('pagina-cambio', {
    page: requestProp.pagination.page,
    rowsPerPage: requestProp.pagination.rowsPerPage,
    sortBy: requestProp.pagination.sortBy,
    descending: requestProp.pagination.descending
  })
}

const columns = computed(() => [
  { name: 'proveedor', label: 'Proveedor', field: 'nombre', align: 'left', sortable: true },
  { name: 'tipo', label: 'Tipo', field: 'tipo', align: 'left', sortable: true },
  { name: 'documento', label: props.filtroTipo === 'persona' ? 'CI' : props.filtroTipo === 'empresa' ? 'NIT' : 'NIT / CI', field: 'documento', align: 'left' },
  { name: 'contacto', label: 'Contacto y teléfono', field: 'contacto', align: 'left' },
  { name: 'ciudad', label: 'Ciudad', field: 'ciudad', align: 'left', sortable: true },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'center', sortable: true },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'right' }
])

const visibleColumns = [
  'proveedor',
  'tipo',
  'documento',
  'contacto',
  'ciudad',
  'estado',
  'acciones'
]

function initials(nombre) {
  return nombre
    .split(' ')
    .filter((p) => p.length > 0)
    .slice(0, 2)
    .map((p) => p[0].toUpperCase())
    .join('')
}
</script>

<style scoped>
.pv-table {
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 2px 12px rgba(0, 96, 81, 0.08);
  border: 1.5px solid #c8dfda;
}

.pv-table :deep(.q-table__top),
.pv-table :deep(.q-table__card) {
  background: #ffffff;
}

.pv-table :deep(.q-table thead tr th) {
  background: #E8F5E9 !important;
  color: #006051 !important;
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 2px solid #c8dfda;
  padding: 12px 14px;
}

.pv-table :deep(.q-table tbody td) {
  border-bottom: 1px solid #e8f0ec;
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  color: #2E7D32;
  padding: 12px 14px;
}

.pv-table :deep(.q-table tbody tr:hover) {
  background: #F1F8E9 !important;
}

.pv-table :deep(.q-table__separator) {
  background: #c8dfda;
}

.pv-table :deep(.q-pagination .q-btn) {
  border-radius: 8px;
}

.pv-table :deep(.q-pagination .q-btn.q-btn--active) {
  background: #006051 !important;
  color: #ffffff !important;
}

.pv-prov {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pv-avatar {
  border-radius: 50% !important;
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 700;
}

.pv-prov-nombre {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13.5px;
  color: #2E7D32;
}

.pv-prov-sub {
  font-size: 11.5px;
  color: #66a35f;
  font-family: 'Nunito', sans-serif;
}

.pv-chip-tipo {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  padding: 8px 18px;
  border-radius: 999px;
  box-shadow: 0 2px 6px rgba(46, 125, 50, 0.25);
  gap: 6px;
}

.pv-chip-estado {
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 12.5px;
  padding: 8px 18px;
  border-radius: 999px;
  gap: 6px;
}

.pv-doc {
  font-size: 11px;
  font-weight: 700;
  color: #8a9a90;
}

.pv-doc-num {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 12.5px;
  color: #2E7D32;
}

.pv-contacto-nombre {
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 12.5px;
  color: #2E7D32;
}

.pv-contacto-tel {
  font-size: 11.5px;
  color: #66a35f;
  font-family: 'Nunito', sans-serif;
}

.pv-ciudad {
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  font-size: 13px;
  color: #2E7D32;
}

.pv-acciones {
  white-space: nowrap;
}

.pv-no-data {
  padding: 40px 0;
  text-align: center;
  color: #2E7D32;
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
</style>

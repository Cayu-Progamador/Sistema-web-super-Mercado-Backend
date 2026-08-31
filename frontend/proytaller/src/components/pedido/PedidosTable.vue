<template>
  <q-card flat bordered class="table-card">
    <q-table
      :rows="rows"
      :columns="columnas"
      :loading="loading"
      row-key="idPedido"
      :pagination="paginacion"
      :rows-per-page-options="[10, 20, 50]"
      :filter="filtro"
      binary-state-sort
      flat
      class="main-table"
      @update:pagination="paginacion = $event"
    >
      <template v-slot:loading>
        <q-inner-loading showing color="primary" />
      </template>

      <template v-slot:no-data>
        <div class="text-center q-pa-lg text-grey-6">
          <q-icon name="shopping_cart" size="48px" color="grey-4" />
          <div class="q-mt-sm">No se encontraron pedidos</div>
          <div class="text-caption text-grey-5">Ajuste los filtros o cree un nuevo pedido.</div>
        </div>
      </template>

      <template v-slot:body-cell-numero="props">
        <q-td :props="props">
          <span class="num-pedido" @click="$emit('ver', props.row)">{{ props.value }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-proveedor="props">
        <q-td :props="props">
          <div class="row items-center no-wrap">
            <q-avatar class="prov-avatar">
              {{ iniciales(props.value) }}
            </q-avatar>
            <div class="text-body2 text-weight-medium">{{ props.value }}</div>
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-fechaPedido="props">
        <q-td :props="props">{{ formatFecha(props.value) }}</q-td>
      </template>

      <template v-slot:body-cell-fechaEntregaEstimada="props">
        <q-td :props="props">
          <span v-if="props.value">{{ formatFecha(props.value) }}</span>
          <span v-else class="text-grey-5">—</span>
        </q-td>
      </template>

      <template v-slot:body-cell-productos="props">
        <q-td :props="props">
          <q-badge outline color="green-7" class="prod-chip">
            <q-icon name="inventory_2" size="14px" class="q-mr-xs" />
            {{ props.row.detalles.length }}
          </q-badge>
        </q-td>
      </template>

      <template v-slot:body-cell-estado="props">
        <q-td :props="props">
          <q-chip :color="colorEstado(props.value)" text-color="white" dense size="12px" class="estado-chip">
            {{ props.value.replace(/_/g, ' ') }}
          </q-chip>
        </q-td>
      </template>

      <template v-slot:body-cell-usuario="props">
        <q-td :props="props">
          <div class="text-body2">{{ props.value }}</div>
        </q-td>
      </template>

      <template v-slot:body-cell-acciones="props">
        <q-td :props="props" class="text-center">
          <q-btn flat round icon="visibility" size="md" style="color: #006051" @click="$emit('ver', props.row)">
            <q-tooltip class="bg-primary text-white">Ver detalle</q-tooltip>
          </q-btn>

          <q-btn v-if="props.row.estado === 'BORRADOR'" flat round icon="edit" size="md" style="color: #F57C00" @click="$emit('editar', props.row)">
            <q-tooltip class="bg-orange text-white">Editar pedido</q-tooltip>
          </q-btn>

          <q-btn
            flat
            round
            icon="picture_as_pdf"
            size="md"
            style="color: #8BC34A"
            :disable="props.row.estado === 'BORRADOR' || props.row.estado === 'CANCELADO'"
            @click="$emit('pdf', props.row)"
          >
            <q-tooltip class="bg-positive text-white">PDF</q-tooltip>
          </q-btn>

          <q-btn
            v-if="props.row.estado === 'BORRADOR'"
            flat
            round
            icon="cancel"
            size="md"
            style="color: #C10015"
            @click="$emit('cancelar', props.row)"
          >
            <q-tooltip class="bg-negative text-white">Cancelar pedido</q-tooltip>
          </q-btn>
        </q-td>
      </template>
    </q-table>
  </q-card>
</template>

<script setup>
import { ref } from 'vue'

const estadosColor = {
  BORRADOR: 'grey-6',
  ENVIADO: 'blue-6',
  PARCIALMENTE_RECIBIDO: 'orange-6',
  COMPLETADO: 'positive',
  CANCELADO: 'negative'
}

const colorEstado = (estado) => estadosColor[estado] || 'grey-6'

const props = defineProps({
  rows: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  filtro: { type: String, default: '' }
})

defineEmits(['ver', 'editar', 'pdf', 'cancelar'])

const paginacion = ref({ sortBy: 'fechaPedido', descending: true, page: 1, rowsPerPage: 10 })

const columnas = [
  { name: 'numero', label: 'N.º Pedido', field: 'numero', align: 'left', sortable: true, style: 'min-width: 130px' },
  { name: 'proveedor', label: 'Proveedor', field: 'proveedor', align: 'left', sortable: true, style: 'min-width: 200px' },
  { name: 'fechaPedido', label: 'Fecha Pedido', field: 'fechaPedido', align: 'left', sortable: true, style: 'width: 130px' },
  { name: 'fechaEntregaEstimada', label: 'Entrega Estimada', field: 'fechaEntregaEstimada', align: 'left', sortable: true, style: 'width: 130px' },
  { name: 'productos', label: 'Productos', field: 'productos', align: 'center', style: 'width: 90px' },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'center', sortable: true, style: 'width: 110px' },
  { name: 'usuario', label: 'Usuario', field: 'usuario', align: 'left', sortable: true, style: 'min-width: 140px' },
  { name: 'acciones', label: 'Acciones', align: 'center', field: 'idPedido', sortable: false, style: 'width: 150px' }
]

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  const dd = String(d.getDate()).padStart(2, '0')
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const yyyy = d.getFullYear()
  return `${dd}/${mm}/${yyyy}`
}

function iniciales(nombre) {
  if (!nombre) return '?'
  return nombre.split(' ').map((n) => n[0]).slice(0, 2).join('').toUpperCase()
}
</script>

<style scoped>
.table-card {
  border-radius: 14px;
  background: white;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.main-table :deep(.q-table) {
  font-size: 13px;
  font-family: 'Nunito', sans-serif;
}

.main-table :deep(th) {
  font-weight: 700;
  color: #374151;
  background: #f9fafb;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.main-table :deep(td) {
  border-bottom: 1px solid #f0f0f0;
}

.main-table :deep(.q-table__bottom) {
  font-family: 'Nunito', sans-serif;
  font-size: 12.5px;
  color: #374151;
}

.main-table :deep(.q-table__bottom-item) {
  color: #2E7D32;
  font-weight: 600;
}

.main-table :deep(.q-btn) {
  border-radius: 8px;
}

.num-pedido {
  color: #2E7D32;
  font-weight: 700;
  cursor: pointer;
}

.num-pedido:hover {
  text-decoration: underline;
}

.estado-chip {
  font-weight: 600;
  border-radius: 6px;
  padding: 2px 10px;
}

.prov-avatar {
  width: 44px !important;
  height: 44px !important;
  min-width: 44px !important;
  font-size: 20px !important;
  font-weight: 800 !important;
  background: #E8F5E9 !important;
  color: #1B5E20 !important;
  margin-right: 12px !important;
}

.prod-chip {
  font-weight: 600;
  border-radius: 6px;
  padding: 2px 8px;
}
</style>
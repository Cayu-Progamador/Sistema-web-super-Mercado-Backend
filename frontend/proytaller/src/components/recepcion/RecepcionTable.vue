<template>
  <q-card flat bordered class="rp-table-card">
    <q-table
      :rows="rows"
      :columns="columnas"
      :loading="loading"
      row-key="idRecepcion"
      :pagination="paginacion"
      :rows-per-page-options="[10, 20, 50]"
      :filter="filtro"
      binary-state-sort
      flat
      class="rp-table"
      @update:pagination="paginacion = $event"
    >
      <template v-slot:loading>
        <q-inner-loading showing color="primary" />
      </template>

      <template v-slot:no-data>
        <div class="text-center q-pa-lg text-grey-6">
          <q-icon name="inventory" size="48px" color="grey-4" />
          <div class="q-mt-sm">No se encontraron recepciones</div>
          <div class="text-caption text-grey-5">Ajuste los filtros o cree una nueva recepción.</div>
        </div>
      </template>

      <template v-slot:body-cell-numero="props">
        <q-td :props="props">
          <span class="rp-num" @click="$emit('ver', props.row)">{{ props.value }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-numeroPedido="props">
        <q-td :props="props">
          <span class="rp-num">{{ props.value }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-proveedor="props">
        <q-td :props="props">
          <div class="row items-center no-wrap">
            <q-avatar class="rp-prod-avatar">
              {{ iniciales(props.value) }}
            </q-avatar>
            <div class="text-body2 text-weight-medium">{{ props.value }}</div>
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-fechaRecepcion="props">
        <q-td :props="props">{{ formatFecha(props.value) }}</q-td>
      </template>

      <template v-slot:body-cell-productos="props">
        <q-td :props="props">
          <q-badge outline color="green-7" class="rp-cant-chip">
            <q-icon name="inventory_2" size="14px" class="q-mr-xs" />
            {{ props.row.detalle.length }}
          </q-badge>
        </q-td>
      </template>

      <template v-slot:body-cell-cantidad="props">
        <q-td :props="props">
          <span class="text-weight-bold">{{ formatNum(props.value) }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-estado="props">
        <q-td :props="props">
          <q-chip
            dense
            size="11px"
            class="rp-chip-estado"
            text-color="white"
            :style="{ background: colorEstadoR(props.value) }"
          >
            {{ props.value.replace(/_/g, ' ') }}
          </q-chip>
        </q-td>
      </template>

      <template v-slot:body-cell-distribucion="props">
        <q-td :props="props">
          <q-chip
            dense
            size="11px"
            class="rp-chip-estado"
            text-color="white"
            :style="{ background: colorEstadoD(props.value) }"
          >
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
          <q-btn
            flat
            round
            icon="visibility"
            size="md"
            style="color: #006051"
            @click="$emit('ver', props.row)"
          >
            <q-tooltip class="bg-primary text-white">Ver detalle</q-tooltip>
          </q-btn>
          <q-btn
            flat
            round
            icon="picture_as_pdf"
            size="md"
            color="teal-7"
            class="q-ml-xs"
            @click="$emit('pdf', props.row)"
          >
            <q-tooltip class="bg-primary text-white">Descargar PDF</q-tooltip>
          </q-btn>
          <q-btn
            v-if="props.row.estado !== 'ANULADA' && props.row.estado !== 'RECHAZADA' && props.row.distribucion === 'PENDIENTE_DE_DISTRIBUIR'"
            flat
            round
            icon="local_shipping"
            size="md"
            color="green-8"
            class="q-ml-xs"
            @click="$emit('distribuir', props.row)"
          >
            <q-tooltip class="bg-primary text-white">Distribuir</q-tooltip>
          </q-btn>
          <q-btn
            v-else-if="props.row.estado !== 'ANULADA' && props.row.estado !== 'RECHAZADA' && props.row.distribucion === 'PARCIALMENTE_DISTRIBUIDA'"
            flat
            round
            icon="play_circle_filled"
            size="md"
            color="orange-8"
            class="q-ml-xs"
            @click="$emit('distribuir', props.row)"
          >
            <q-tooltip class="bg-primary text-white">Continuar distribución</q-tooltip>
          </q-btn>
          <q-btn
            v-if="props.row.estado !== 'ANULADA' && props.row.estado !== 'RECHAZADA' && props.row.distribucion === 'PENDIENTE_DE_DISTRIBUIR'"
            flat
            round
            icon="block"
            size="md"
            color="red-8"
            class="q-ml-xs"
            @click="$emit('anular', props.row)"
          >
            <q-tooltip class="bg-red text-white">Anular recepción</q-tooltip>
          </q-btn>
        </q-td>
      </template>
    </q-table>
  </q-card>
</template>

<script setup>
import { ref } from 'vue'
import { colorEstadoR, colorEstadoD } from '../../api/recepcion/constantes'

const props = defineProps({
  rows: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  filtro: { type: String, default: '' }
})

defineEmits(['ver', 'distribuir', 'anular', 'pdf'])

const paginacion = ref({ sortBy: 'fechaRecepcion', descending: true, page: 1, rowsPerPage: 10 })

const columnas = [
  { name: 'numero', label: 'N.º Recepción', field: 'numero', align: 'left', sortable: true, style: 'min-width: 120px' },
  { name: 'numeroPedido', label: 'N.º Pedido', field: 'numeroPedido', align: 'left', sortable: true, style: 'min-width: 120px' },
  { name: 'proveedor', label: 'Proveedor', field: 'proveedor', align: 'left', sortable: true, style: 'min-width: 190px' },
  { name: 'fechaRecepcion', label: 'Fecha', field: 'fechaRecepcion', align: 'left', sortable: true, style: 'width: 110px' },
  { name: 'productos', label: 'Productos', field: 'productos', align: 'center', style: 'width: 100px' },
  { name: 'totalCantidad', label: 'Cantidad', field: 'totalCantidad', align: 'center', sortable: true, style: 'width: 90px' },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'center', sortable: true, style: 'min-width: 120px' },
  { name: 'distribucion', label: 'Distribución', field: 'distribucion', align: 'center', sortable: true, style: 'min-width: 180px' },
  { name: 'usuario', label: 'Usuario', field: 'usuario', align: 'left', sortable: true, style: 'min-width: 130px' },
  { name: 'acciones', label: 'Acciones', align: 'center', field: 'idRecepcion', sortable: false, style: 'width: 70px' }
]

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

function formatNum(n) {
  return Number(n || 0).toLocaleString('es-BO')
}

function iniciales(nombre) {
  if (!nombre) return '?'
  return nombre.split(' ').map((n) => n[0]).slice(0, 2).join('').toUpperCase()
}
</script>

<style scoped src="../../assets/styles/recepcion/recepcion.css"></style>

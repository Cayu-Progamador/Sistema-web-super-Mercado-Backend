<template>
  <div>
    <div class="rp-seccion-title">
      <q-icon name="info" color="green-7" size="20px" />
      Información general
    </div>

    <q-card flat bordered class="rp-detalle-card q-mb-md">
      <q-card-section>
        <div class="row q-col-gutter-md">
          <div class="col-6 col-md-3">
            <div class="rp-mini-label">N.º Pedido</div>
            <div class="rp-mini-valor">{{ pedidoSeleccionado?.numero || '—' }}</div>
          </div>
          <div class="col-6 col-md-3">
            <div class="rp-mini-label">Proveedor</div>
            <div class="rp-mini-valor">{{ pedidoSeleccionado?.proveedor || '—' }}</div>
          </div>
          <div class="col-6 col-md-3">
            <div class="rp-mini-label">Fecha de recepción</div>
            <div class="rp-mini-valor">{{ formatFecha(hoy) }}</div>
          </div>
<div class="col-6 col-md-3">
            <div class="rp-mini-label">Usuario</div>
            <div class="rp-mini-valor">{{ usuario }}</div>
          </div>
        </div>
      </q-card-section>
    </q-card>

    <q-separator class="q-my-md" />

    <div class="rp-seccion-title">
      <q-icon name="inventory_2" color="green-7" size="20px" />
      Detalle de recepción
    </div>

    <q-card flat bordered class="rp-detalle-card q-mb-md">
      <q-card-section class="q-pa-none">
        <q-table
          :rows="items"
          :columns="columnas"
          row-key="idProducto"
          flat
          dense
          hide-bottom
          :pagination="{ rowsPerPage: 0 }"
          class="rp-rec-table"
        >
          <template v-slot:body-cell-producto="props">
            <q-td :props="props">
              <div class="text-weight-bold">{{ props.value }}</div>
              <div v-if="props.row.usaLote" class="rp-factor-hint">
                Lote: {{ props.row.codigoLote || '—' }} · Vence: {{ formatFecha(props.row.fechaVencimiento) }}
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-solicitado="props">
            <q-td :props="props">{{ formatNum(props.value) }} {{ props.row.unidadBase }}</q-td>
          </template>

          <template v-slot:body-cell-recibido="props">
            <q-td :props="props">{{ formatNum(props.value) }}</q-td>
          </template>

          <template v-slot:body-cell-aceptado="props">
            <q-td :props="props">{{ formatNum(props.value) }}</q-td>
          </template>

          <template v-slot:body-cell-rechazado="props">
            <q-td :props="props">
              <span :style="{ color: props.value > 0 ? '#D32F2F' : '#2E7D32' }">{{ formatNum(props.value) }}</span>
            </q-td>
          </template>

          <template v-slot:body-cell-precio="props">
            <q-td :props="props">Bs {{ formatNum(props.value) }}</q-td>
          </template>

          <template v-slot:body-cell-subtotal="props">
            <q-td :props="props">
              <span class="rp-subtotal">Bs {{ formatNum(props.value) }}</span>
            </q-td>
          </template>
        </q-table>
      </q-card-section>
    </q-card>

    <q-separator class="q-my-md" />

    <div class="rp-total-general q-mb-md">
      <div>
        <div class="rp-mini-label">Total general de la recepción</div>
        <div class="rp-mini-valor">Bs {{ formatNum(totalGeneral) }}</div>
      </div>
      <div class="text-right">
        <div class="rp-mini-label">Unidades recibidas</div>
        <div class="rp-mini-valor">{{ formatNum(totalRecibido) }}</div>
      </div>
    </div>

    <q-input
      v-model="obs"
      label="Observaciones"
      type="textarea"
      outlined
      dense
      color="green-8"
      autogrow
      placeholder="Comentarios sobre la mercadería recibida..."
      class="q-mb-sm"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, default: () => [] },
  pedidoSeleccionado: { type: Object, default: null },
  usuario: { type: String, default: '' },
  observacion: { type: String, default: '' }
})

const emit = defineEmits(['update:observacion', 'update:usuario'])

const obs = computed({
  get: () => props.observacion,
  set: (v) => emit('update:observacion', v)
})

const columnas = [
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left', style: 'min-width: 200px' },
  { name: 'solicitado', label: 'Solicitado', field: 'solicitado', align: 'center' },
  { name: 'recibido', label: 'Recibido', field: 'recibido', align: 'center' },
  { name: 'aceptado', label: 'Aceptado', field: 'aceptado', align: 'center' },
  { name: 'rechazado', label: 'Rechazado', field: 'rechazado', align: 'center' },
  { name: 'precio', label: 'Precio', field: 'precio', align: 'center' },
  { name: 'subtotal', label: 'Subtotal', field: 'subtotal', align: 'center' }
]

const hoy = new Date().toISOString().slice(0, 10)

const totalRecibido = computed(() => props.items.reduce((s, r) => s + (r.recibido || 0), 0))
const totalGeneral = computed(() => props.items.reduce((s, r) => s + (r.subtotal || 0), 0))

function formatNum(n) {
  return Number(n || 0).toLocaleString('es-BO')
}

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}
</script>

<style scoped src="../../../assets/styles/recepcion/recepcion.css"></style>
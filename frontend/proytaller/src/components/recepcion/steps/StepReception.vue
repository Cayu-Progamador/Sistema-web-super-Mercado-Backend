<template>
  <div>
    <div class="row items-center justify-between q-mb-md">
      <div>
        <div class="text-subtitle1 text-bold text-dark">Verificación de mercadería</div>
        <div class="text-caption text-grey-6">
          Registre las cantidades recibidas. Aceptado + Rechazado = Recibido.
        </div>
      </div>
      <q-chip outline color="green-7" class="rp-cant-chip">
        <q-icon name="assignment" size="15px" class="q-mr-xs" />
        {{ pedidoSeleccionado?.numero || '—' }}
      </q-chip>
    </div>

    <q-separator class="q-mb-md" />

    <q-table
      :rows="items"
      :columns="tablaColumnas"
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
          <div class="rp-factor-hint">{{ props.row.solicitado }} {{ unidadBase(props.row) }} → Factor {{ props.row.factor }}</div>
        </q-td>
      </template>

      <template v-slot:body-cell-solicitado="props">
        <q-td :props="props">
          <span class="text-weight-bold">{{ formatNum(props.value) }} {{ unidadBase(props.row) }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-recibido="props">
        <q-td :props="props">
          <q-input
            :model-value="props.row.recibido"
            type="number"
            dense
            outlined
            min="0"
            class="rp-num-input"
            style="width: 110px"
            @keydown="bloquearSignos"
            @update:model-value="setRecibido(props.row, $event)"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-pendiente="props">
        <q-td :props="props">
          <span
            class="text-weight-bold"
            :style="{ color: pendiente(props.row) > 0 ? '#F57C00' : '#2E7D32' }"
          >
            {{ formatNum(pendiente(props.row)) }} {{ unidadBase(props.row) }}
          </span>
        </q-td>
      </template>

      <template v-slot:body-cell-aceptado="props">
        <q-td :props="props">
          <q-input
            :model-value="props.row.aceptado"
            type="number"
            dense
            outlined
            min="0"
            class="rp-num-input"
            style="width: 100px"
            @keydown="bloquearSignos"
            @update:model-value="setAceptado(props.row, $event)"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-rechazado="props">
        <q-td :props="props">
          <q-input
            :model-value="props.row.rechazado"
            type="number"
            dense
            outlined
            min="0"
            class="rp-num-input"
            style="width: 100px"
            @keydown="bloquearSignos"
            @update:model-value="setRechazado(props.row, $event)"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-motivo="props">
        <q-td :props="props">
          <q-select
            v-if="props.row.rechazado > 0"
            :model-value="props.row.motivo"
            :options="opcionesMotivo"
            dense
            outlined
            emit-value
            map-options
            options-dense
            placeholder="Motivo de rechazo"
            class="rp-num-input"
            style="min-width: 180px"
            @update:model-value="props.row.motivo = $event"
          />
          <span v-else class="text-grey-5">—</span>
        </q-td>
      </template>

      <template v-slot:body-cell-precio="props">
        <q-td :props="props">
          <q-input
            :model-value="props.row.precio"
            type="number"
            dense
            outlined
            min="0"
            step="0.01"
            class="rp-num-input"
            style="width: 110px"
            @keydown="bloquearSignos"
            @update:model-value="setPrecio(props.row, $event)"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-subtotal="props">
        <q-td :props="props">
          <span class="rp-subtotal">Bs {{ formatNum(props.row.subtotal) }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-estadoFila="props">
        <q-td :props="props">
          <q-chip dense size="10px" text-color="white" :style="{ background: estadoFila(props.row).color }">
            {{ estadoFila(props.row).label }}
          </q-chip>
        </q-td>
      </template>
    </q-table>

    <q-card class="q-mt-md rp-detalle-card">
      <q-card-section>
        <div class="rp-resumen-minis">
          <div class="rp-mini-kpi rp-mini-kpi-rev">
            <div class="rp-mini-label">Total recibido</div>
            <div class="rp-mini-valor">{{ formatNum(totalRecibido) }}</div>
          </div>
          <div class="rp-mini-kpi rp-mini-kpi-ace">
            <div class="rp-mini-label">Total aceptado</div>
            <div class="rp-mini-valor">{{ formatNum(totalAceptado) }}</div>
          </div>
          <div class="rp-mini-kpi rp-mini-kpi-rec">
            <div class="rp-mini-label">Total rechazado</div>
            <div class="rp-mini-valor">{{ formatNum(totalRechazado) }}</div>
          </div>
          <div class="rp-mini-kpi rp-mini-kpi-tot">
            <div class="rp-mini-label">Total general</div>
            <div class="rp-mini-valor">Bs {{ formatNum(totalGeneral) }}</div>
          </div>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, default: () => [] },
  pedidoSeleccionado: { type: Object, default: null },
  motivos: { type: Array, default: () => [] }
})

const opcionesMotivo = computed(() =>
  props.motivos.map((m) => ({ label: m.nombre.replace(/_/g, ' '), value: m.idMotivoDevolucion }))
)

const tablaColumnas = [
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left', style: 'min-width: 220px' },
  { name: 'solicitado', label: 'Solicitado', field: 'solicitado', align: 'center', style: 'width: 100px' },
  { name: 'recibido', label: 'Recibido', align: 'center', style: 'width: 130px' },
  { name: 'pendiente', label: 'Pendiente', align: 'center', style: 'width: 110px' },
  { name: 'aceptado', label: 'Aceptado', align: 'center', style: 'width: 120px' },
  { name: 'rechazado', label: 'Rechazado', align: 'center', style: 'width: 120px' },
  { name: 'motivo', label: 'Motivo de rechazo', align: 'center', style: 'min-width: 180px' },
  { name: 'precio', label: 'Precio Unit.', align: 'center', style: 'width: 110px' },
  { name: 'subtotal', label: 'Subtotal', align: 'center', style: 'width: 120px' },
  { name: 'estadoFila', label: 'Estado', align: 'center', style: 'width: 110px' }
]

function unidadBase(row) {
  return row.unidadBase || 'UND'
}

function setRecibido(row, val) {
  const n = Number(val) || 0
  row.recibido = Math.min(n, row.solicitado)
  row.aceptado = row.recibido - row.rechazado
  if (row.aceptado < 0) {
    row.aceptado = 0
    row.rechazado = row.recibido
  }
  row.subtotal = row.aceptado * row.precio
}

function setAceptado(row, val) {
  const n = Number(val) || 0
  const max = Math.min(n, row.recibido)
  row.aceptado = max
  row.rechazado = row.recibido - row.aceptado
  row.subtotal = row.aceptado * row.precio
}

function setRechazado(row, val) {
  const n = Number(val) || 0
  const max = Math.min(n, row.recibido)
  row.rechazado = max
  row.aceptado = row.recibido - row.rechazado
  row.subtotal = row.aceptado * row.precio
}

function setPrecio(row, val) {
  row.precio = Number(val) || 0
  row.subtotal = row.aceptado * row.precio
}

function bloquearSignos(e) {
  if (['-', '+', 'e', 'E'].includes(e.key)) e.preventDefault()
}

function pendiente(row) {
  return Math.max(0, row.solicitado - row.recibido)
}

function estadoFila(row) {
  if (row.rechazado > 0 && row.aceptado === 0) return { label: 'Con rechazo', color: '#D32F2F' }
  if (row.rechazado > 0) return { label: 'Con rechazo', color: '#D32F2F' }
  if (row.recibido >= row.solicitado) return { label: 'Completo', color: '#2E7D32' }
  return { label: 'Parcial', color: '#F57C00' }
}

const totalRecibido = computed(() => props.items.reduce((s, r) => s + (r.recibido || 0), 0))
const totalAceptado = computed(() => props.items.reduce((s, r) => s + (r.aceptado || 0), 0))
const totalRechazado = computed(() => props.items.reduce((s, r) => s + (r.rechazado || 0), 0))
const totalGeneral = computed(() => props.items.reduce((s, r) => s + (r.subtotal || 0), 0))

function formatNum(n) {
  return Number(n || 0).toLocaleString('es-BO')
}
</script>

<style scoped src="../../../assets/styles/recepcion/recepcion.css"></style>
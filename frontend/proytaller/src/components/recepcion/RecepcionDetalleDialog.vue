<template>
  <q-dialog v-model="visible" class="rp-detail-dialog">
    <q-card class="rp-detail-dialog" style="width: 100%; max-width: 980px">
      <q-card-section class="row items-start justify-between rp-detail-header">
        <div>
          <div class="text-h6 text-bold q-mb-sm">{{ recepcion?.numero || '—' }}</div>
          <div class="row q-col-gutter-xl">
            <div class="col-auto">
              <div class="rp-mini-label">N.º Pedido</div>
              <div class="text-bold rp-detail-value">{{ recepcion?.numeroPedido || '—' }}</div>
            </div>
            <div class="col-auto">
              <div class="rp-mini-label">Proveedor</div>
              <div class="text-bold rp-detail-value">{{ recepcion?.proveedor || '—' }}</div>
            </div>
            <div class="col-auto">
              <div class="rp-mini-label">Fecha</div>
              <div class="text-bold rp-detail-value">{{ formatFecha(recepcion?.fechaRecepcion) }}</div>
            </div>
            <div class="col-auto">
              <div class="rp-mini-label">Usuario</div>
              <div class="text-bold rp-detail-value">{{ recepcion?.usuario || '—' }}</div>
            </div>
          </div>
        </div>
        <div class="row items-center q-gutter-xs no-wrap">
          <q-chip dense size="11px" text-color="white" class="rp-chip-estado" :style="{ background: colorEstadoR(recepcion?.estado) }">
            {{ (recepcion?.estado || '').replace(/_/g, ' ') }}
          </q-chip>
          <q-chip dense size="11px" text-color="white" class="rp-chip-estado" :style="{ background: colorEstadoD(recepcion?.distribucion) }">
            {{ (recepcion?.distribucion || '').replace(/_/g, ' ') }}
          </q-chip>
          <q-btn flat round dense icon="close" color="grey-8" @click="visible = false">
            <q-tooltip>Cerrar</q-tooltip>
          </q-btn>
        </div>
      </q-card-section>

      <q-card-section>
        <div class="rp-seccion-title">
          <q-icon name="inventory_2" color="green-7" size="20px" />
          Detalle de recepción
        </div>
        <q-table
          :rows="recepcion?.detalle || []"
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
              <div v-if="props.row.lote" class="rp-factor-hint">
                Lote: {{ props.row.lote }} · Vence: {{ formatFecha(props.row.vencimiento) }}
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-rechazado="props">
            <q-td :props="props">
              <span :style="{ color: props.value > 0 ? '#D32F2F' : '#2E7D32' }">{{ formatNum(props.value) }}</span>
            </q-td>
          </template>

          <template v-slot:body-cell-subtotal="props">
            <q-td :props="props">
              <span class="rp-subtotal">Bs {{ formatNum(props.value) }}</span>
            </q-td>
          </template>
        </q-table>
      </q-card-section>

      <q-separator />

      <q-card-section class="row items-center justify-between">
        <div class="text-caption text-grey-6">
          {{ recepcion?.observacion ? `Observación: ${recepcion.observacion}` : 'Sin observaciones' }}
        </div>
        <div class="text-right">
          <div class="rp-mini-label">Total general</div>
          <div class="text-h6 text-bold text-primary">Bs {{ formatNum(recepcion?.totalGeneral) }}</div>
        </div>
      </q-card-section>

      <q-card-actions align="right" class="q-pa-md">
        <q-btn
          flat
          color="red-7"
          icon="picture_as_pdf"
          label="PDF"
          no-caps
          class="rp-btn"
          @click="emit('pdf', recepcion)"
        />
        <q-btn flat color="grey-7" label="Cerrar" no-caps class="rp-btn" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { colorEstadoR, colorEstadoD } from '../../api/recepcion/constantes'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  recepcion: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'pdf'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
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

<style scoped src="../../assets/styles/recepcion/recepcion.css"></style>

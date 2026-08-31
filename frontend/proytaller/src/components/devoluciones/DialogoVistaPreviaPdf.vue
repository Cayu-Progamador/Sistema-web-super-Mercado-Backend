<template>
  <q-dialog v-model="visible">
    <q-card class="dv-pdf-card" style="width: 100%; max-width: 720px">
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6">Vista previa del documento</div>
        <q-space />
        <q-btn flat round dense icon="close" v-close-popup />
      </q-card-section>

      <q-card-section>
        <div class="dv-pdf-head">
          <div class="text-bold text-h6">Supermercado La Martita</div>
          <div class="text-subtitle2 text-grey-7">Devolución al Proveedor</div>
          <div class="text-subtitle2 text-grey-7">{{ devolucion?.numero || '—' }}</div>
        </div>

        <div class="dv-pdf-meta row q-col-gutter-md q-mt-md">
          <div class="col-6">
            <div class="dv-mini-label">Proveedor</div>
            <div class="text-weight-bold">{{ devolucion?.proveedor }}</div>
          </div>
          <div class="col-6">
            <div class="dv-mini-label">Recepción</div>
            <div class="text-weight-bold">{{ devolucion?.numeroRecepcion }}</div>
          </div>
          <div class="col-6">
            <div class="dv-mini-label">Fecha</div>
            <div class="text-weight-bold">{{ formatFecha(devolucion?.fechaDevolucion) }}</div>
          </div>
          <div class="col-6">
            <div class="dv-mini-label">Estado</div>
            <q-chip dense size="11px" text-color="white" class="dv-chip-estado" :style="{ background: colorEstadoD(devolucion?.estado) }">
              {{ (devolucion?.estado || '').replace(/_/g, ' ') }}
            </q-chip>
          </div>
        </div>

        <q-table
          :rows="devolucion?.detalle || []"
          :columns="columnas"
          row-key="id"
          flat
          dense
          hide-bottom
          class="q-mt-md dv-rec-table"
          :pagination="{ rowsPerPage: 0 }"
        >
          <template v-slot:body-cell-cantidadDevolver="props">
            <q-td :props="props">
              <span class="dv-subtotal">{{ formatNum(props.value) }}</span>
            </q-td>
          </template>
        </q-table>

        <div class="row items-center justify-between q-mt-md">
          <div class="text-caption text-grey-6">
            {{ devolucion?.observacion ? `Observación: ${devolucion.observacion}` : 'Sin observaciones' }}
          </div>
          <div class="text-right">
            <div class="dv-mini-label">Total de unidades</div>
            <div class="text-h6 text-bold text-primary">{{ formatNum(devolucion?.cantidadTotal) }}</div>
          </div>
        </div>
      </q-card-section>

      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat color="grey-7" label="Cerrar" no-caps class="dv-btn" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { colorEstadoDevolucion } from '../../util/devoluciones'

const colorEstadoD = colorEstadoDevolucion

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  devolucion: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const columnas = [
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left', style: 'min-width: 200px' },
  { name: 'lote', label: 'Lote', field: 'lote', align: 'center', style: 'width: 110px' },
  { name: 'cantidadDevolver', label: 'Cantidad', field: 'cantidadDevolver', align: 'center', style: 'width: 100px' },
  { name: 'motivo', label: 'Motivo', field: 'motivo', align: 'left', style: 'min-width: 200px' }
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

<style scoped src="../../assets/styles/devoluciones/devoluciones.css"></style>

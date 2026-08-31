<template>
  <q-dialog v-model="visible" class="dv-detail-dialog">
    <q-card class="dv-detail-dialog" style="width: 100%; max-width: 980px">
      <q-card-section class="row items-start justify-between dv-detail-header">
        <div>
          <div class="text-h6 text-bold q-mb-sm">{{ devolucion?.numero || '—' }}</div>
          <div class="row q-col-gutter-xl">
            <div class="col-auto">
              <div class="dv-mini-label">Recepción</div>
              <div class="text-bold text-white">{{ devolucion?.numeroRecepcion || '—' }}</div>
            </div>
            <div class="col-auto">
              <div class="dv-mini-label">Proveedor</div>
              <div class="text-bold text-white">{{ devolucion?.proveedor || '—' }}</div>
            </div>
            <div class="col-auto">
              <div class="dv-mini-label">Fecha</div>
              <div class="text-bold text-white">{{ formatFecha(devolucion?.fechaDevolucion) }}</div>
            </div>
            <div class="col-auto">
              <div class="dv-mini-label">Usuario</div>
              <div class="text-bold text-white">{{ devolucion?.usuario || '—' }}</div>
            </div>
          </div>
        </div>
        <q-chip dense size="11px" text-color="white" class="dv-chip-estado" :style="{ background: colorEstadoD(devolucion?.estado) }">
          {{ (devolucion?.estado || '').replace(/_/g, ' ') }}
        </q-chip>
      </q-card-section>

      <q-card-section>
        <div class="dv-seccion-title">
          <q-icon name="inventory_2" color="green-7" size="20px" />
          Productos devueltos
        </div>
        <q-table
          :rows="devolucion?.detalle || []"
          :columns="columnas"
          row-key="id"
          flat
          dense
          hide-bottom
          :pagination="{ rowsPerPage: 0 }"
          class="dv-rec-table"
        >
          <template v-slot:body-cell-producto="props">
            <q-td :props="props">
              <div class="text-weight-bold">{{ props.value }}</div>
              <div class="dv-factor-hint">
                Lote: {{ props.row.lote }} · {{ props.row.ubicacion }}
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-cantidadDevolver="props">
            <q-td :props="props">
              <span class="dv-subtotal">{{ formatNum(props.value) }}</span>
            </q-td>
          </template>
        </q-table>
      </q-card-section>

      <q-banner
        v-if="devolucion?.estado === 'COMPLETADA'"
        rounded
        class="bg-positive text-white dv-banner-inventario q-mx-md q-mb-md"
      >
        <template v-slot:avatar>
          <q-icon name="inventory" />
        </template>
        Impacto en inventario: Movimiento de salida registrado tipo DEVOLUCIÓN A PROVEEDOR
      </q-banner>

      <q-card-section class="row items-center justify-between">
        <div class="text-caption text-grey-6">
          {{ devolucion?.observacion ? `Observación: ${devolucion.observacion}` : 'Sin observaciones' }}
        </div>
        <div class="text-right">
          <div class="dv-mini-label">Total de unidades</div>
          <div class="text-h6 text-bold text-primary">{{ formatNum(devolucion?.cantidadTotal) }}</div>
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

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  devolucion: { type: Object, default: null }
})

const colorEstadoD = colorEstadoDevolucion

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const columnas = [
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left', style: 'min-width: 220px' },
  { name: 'lote', label: 'Lote', field: 'lote', align: 'center', style: 'width: 120px' },
  { name: 'disponible', label: 'Disponible', field: 'disponible', align: 'center', style: 'width: 110px' },
  { name: 'cantidadDevolver', label: 'Cantidad', field: 'cantidadDevolver', align: 'center', style: 'width: 110px' },
  { name: 'motivo', label: 'Motivo', field: 'motivo', align: 'left', style: 'min-width: 220px' }
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

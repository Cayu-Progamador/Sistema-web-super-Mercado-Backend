<template>
  <div>
    <div class="row items-center justify-between q-mb-md">
      <div>
        <div class="text-subtitle1 text-bold text-dark">Seleccione el pedido a recibir</div>
        <div class="text-caption text-grey-6">Solo se muestran pedidos enviados y pendientes de recepción.</div>
      </div>
      <q-badge outline color="green-7" class="rp-cant-chip">
        {{ pedidos.length }} pendientes
      </q-badge>
    </div>

    <q-separator class="q-mb-md" />

    <div v-if="pedidos.length" class="rp-pedido-grid">
      <div
        v-for="ped in pedidos"
        :key="ped.idPedido"
        class="rp-pedido-card"
        :class="{ 'rp-seleccionada': pedidoSeleccionado?.idPedido === ped.idPedido }"
        @click="seleccionar(ped)"
      >
        <div class="row items-center justify-between">
          <span class="rp-pedido-num">{{ ped.numero }}</span>
          <q-chip dense size="11px" text-color="white" :style="{ background: colorEstadoR('EN_REVISION') }">
            {{ ped.estado.replace(/_/g, ' ') }}
          </q-chip>
        </div>
        <div class="rp-pedido-prov">
          <q-icon name="storefront" size="14px" class="q-mr-xs rp-pedido-prov-ic" />
          {{ ped.proveedor }}
        </div>
        <div class="rp-pedido-fecha">
          <q-icon name="event" size="13px" class="q-mr-xs" />
          Pedido: {{ formatFecha(ped.fechaPedido) }}
        </div>
        <div class="row items-center q-gutter-sm q-mt-sm">
          <q-chip size="10px" color="green-2" text-color="green-9" class="fw-600">
            <q-icon name="inventory_2" size="12px" class="q-mr-xs" />
            {{ ped.detalles.length }} productos
          </q-chip>
          <q-chip v-if="pedidoSeleccionado?.idPedido === ped.idPedido" size="10px" color="green-7" text-color="white" class="fw-600">
            <q-icon name="check_circle" size="12px" class="q-mr-xs" />
            Seleccionado
          </q-chip>
        </div>
      </div>
    </div>

    <div v-else class="text-center q-pa-lg text-grey-6">
      <q-icon name="inventory" size="48px" color="grey-4" />
      <div class="q-mt-sm">No hay pedidos enviados pendientes de recepción</div>
    </div>

    <template v-if="pedidoSeleccionado">
      <q-separator class="q-my-md" />
      <div class="rp-pedido-sel-card">
        <div class="row q-col-gutter-md">
          <div class="col-auto">
            <div class="rp-mini-label">N.º Pedido</div>
            <div class="rp-mini-valor">{{ pedidoSeleccionado.numero }}</div>
          </div>
          <div class="col-auto">
            <div class="rp-mini-label">Proveedor</div>
            <div class="rp-mini-valor">{{ pedidoSeleccionado.proveedor }}</div>
          </div>
          <div class="col-auto">
            <div class="rp-mini-label">Fecha del pedido</div>
            <div class="rp-mini-valor">{{ formatFecha(pedidoSeleccionado.fechaPedido) }}</div>
          </div>
          <div class="col-auto">
            <div class="rp-mini-label">Entrega estimada</div>
            <div class="rp-mini-valor">{{ formatFecha(pedidoSeleccionado.fechaEntregaEstimada) }}</div>
          </div>
          <div class="col-auto">
            <div class="rp-mini-label">Estado</div>
            <q-chip dense size="11px" text-color="white" :style="{ background: colorEstadoR('EN_REVISION') }" class="q-mt-xs">
              {{ pedidoSeleccionado.estado.replace(/_/g, ' ') }}
            </q-chip>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { colorEstadoR } from '../../../api/recepcion/constantes'

const props = defineProps({
  pedidos: { type: Array, default: () => [] },
  pedidoSeleccionado: { type: Object, default: null }
})

const emit = defineEmits(['seleccionar'])

function seleccionar(ped) {
  emit('seleccionar', ped)
}

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}
</script>

<style scoped src="../../../assets/styles/recepcion/recepcion.css"></style>
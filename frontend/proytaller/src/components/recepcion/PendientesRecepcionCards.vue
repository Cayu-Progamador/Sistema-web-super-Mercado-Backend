<template>
  <section class="rppc-panel q-mt-md">
    <div class="row items-center justify-between q-mb-md">
      <div class="row items-center no-wrap">
        <q-avatar size="42px" square class="rppc-title-avatar" :class="esFaltantes ? 'rppc-avatar-faltantes' : 'rppc-avatar-devueltos'">
          <q-icon :name="esFaltantes ? 'pending_actions' : 'assignment_return'" size="22px" />
        </q-avatar>
        <div class="q-ml-sm">
          <div class="text-subtitle1 text-weight-bold" style="color: #263238">{{ titulo }}</div>
          <div class="text-caption" style="color: #4a9e8a">{{ subtitulo }}</div>
        </div>
      </div>
      <q-btn flat round dense icon="close" color="grey-7" @click="$emit('cerrar')">
        <q-tooltip>Cerrar listado</q-tooltip>
      </q-btn>
    </div>

    <div v-if="items.length" class="row q-col-gutter-md">
      <div v-for="item in items" :key="clave(item)" class="col-12 col-sm-6">
        <q-card class="rppc-card">
          <q-card-section class="q-pa-md">
            <template v-if="esFaltantes">
              <div class="row items-start justify-between q-mb-sm no-wrap">
                <div class="row items-center no-wrap" style="gap: 12px; min-width: 0">
                  <q-avatar size="46px" square class="rppc-item-avatar rppc-avatar-faltantes">
                    <q-icon name="inventory_2" size="25px" />
                  </q-avatar>
                  <div style="min-width: 0">
                    <div class="text-subtitle1 text-weight-bold ellipsis" style="color: #2e7d32">{{ item.producto }}</div>
                    <div class="text-caption ellipsis" style="color: #66a35f">{{ item.proveedor || 'Proveedor no disponible' }}</div>
                  </div>
                </div>
                <q-chip dense square class="rppc-chip rppc-chip-faltante">{{ item.numeroPedido }}</q-chip>
              </div>

              <div class="row q-mt-sm" style="gap: 8px">
                <div class="col rppc-metric">
                  <div class="rppc-metric-value">{{ formatoNumero(item.solicitado) }}</div>
                  <div class="text-caption text-weight-medium">Solicitado</div>
                </div>
                <div class="col rppc-metric">
                  <div class="rppc-metric-value">{{ formatoNumero(item.recibido) }}</div>
                  <div class="text-caption text-weight-medium">Recibido</div>
                </div>
                <div class="col rppc-metric rppc-metric-warning">
                  <div class="rppc-metric-value">{{ formatoNumero(item.faltante) }}</div>
                  <div class="text-caption text-weight-medium">Faltante</div>
                </div>
              </div>
            </template>

            <template v-else>
              <div class="row items-start justify-between q-mb-sm no-wrap">
                <div class="row items-center no-wrap" style="gap: 12px; min-width: 0">
                  <q-avatar size="46px" square class="rppc-item-avatar rppc-avatar-devueltos">
                    <q-icon name="assignment_return" size="25px" />
                  </q-avatar>
                  <div style="min-width: 0">
                    <div class="text-subtitle1 text-weight-bold ellipsis" style="color: #2e7d32">{{ item.numeroRecepcion }}</div>
                    <div class="text-caption ellipsis" style="color: #66a35f">{{ item.proveedor || 'Proveedor no disponible' }}</div>
                  </div>
                </div>
                <q-chip dense square class="rppc-chip" :class="chipEstado(item.estado)">{{ textoEstado(item.estado) }}</q-chip>
              </div>

              <div class="rppc-products">
                <div v-for="(detalle, indice) in item.items || []" :key="indice" class="rppc-product-row">
                  <span class="ellipsis">{{ detalle.producto }}</span>
                  <span class="text-weight-bold">{{ formatoNumero(detalle.cantidad) }}</span>
                  <span class="rppc-motivo">{{ detalle.motivo || 'Sin motivo' }}</span>
                </div>
              </div>
              <div class="text-caption q-mt-sm" style="color: #5b6675">Registrada: {{ formatoFecha(item.fechaDevolucion) }}</div>
            </template>
          </q-card-section>

          <q-separator />
          <q-card-actions v-if="esFaltantes" class="q-pa-sm">
            <q-btn no-caps outline align="left" class="rppc-action col" label="Nueva recepción" icon="add" color="green-8" @click="$emit('nueva-recepcion', item)" />
          </q-card-actions>
          <q-card-actions v-else-if="item.estado === 'EN_PROCESO'" class="q-pa-sm">
            <q-btn no-caps outline align="left" class="rppc-action col" label="Recibir reemplazo" icon="assignment_turned_in" color="orange-8" @click="$emit('recibir-reemplazo', item)" />
          </q-card-actions>
        </q-card>
      </div>
    </div>

    <q-card v-else class="rppc-empty">
      <q-card-section class="text-center q-pa-lg text-grey-7">
        <q-icon :name="esFaltantes ? 'task_alt' : 'verified'" size="42px" color="green-5" />
        <div class="q-mt-sm">{{ mensajeVacio }}</div>
      </q-card-section>
    </q-card>
  </section>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  tipo: { type: String, required: true },
  items: { type: Array, default: () => [] }
})

defineEmits(['cerrar', 'nueva-recepcion', 'recibir-reemplazo'])

const esFaltantes = computed(() => props.tipo === 'faltantes')
const titulo = computed(() => esFaltantes.value ? 'Pedidos con productos faltantes' : 'Productos rechazados')
const subtitulo = computed(() => esFaltantes.value
  ? 'Registra una nueva recepción cuando el proveedor entregue lo pendiente.'
  : 'Productos dañados o rechazados pendientes de solución por el proveedor.')
const mensajeVacio = computed(() => esFaltantes.value
  ? 'No hay productos pendientes por recibir.'
  : 'No hay devoluciones registradas.')

function clave(item) {
  return esFaltantes.value
    ? `${item.numeroPedido}|${item.producto}|${item.unidad || ''}`
    : item.idDevolucion
}

function formatoNumero(valor) {
  return Number(valor || 0).toLocaleString('es-BO')
}

function formatoFecha(fecha) {
  if (!fecha) return 'Sin fecha'
  const date = new Date(fecha)
  if (Number.isNaN(date.getTime())) return fecha
  return `${String(date.getDate()).padStart(2, '0')}/${String(date.getMonth() + 1).padStart(2, '0')}/${date.getFullYear()}`
}

function textoEstado(estado) {
  return (estado || 'SIN_ESTADO').replace(/_/g, ' ')
}

function chipEstado(estado) {
  if (estado === 'EN_PROCESO') return 'rppc-chip-proceso'
  if (estado === 'COMPLETADA') return 'rppc-chip-completada'
  return 'rppc-chip-cancelada'
}
</script>

<style scoped src="../../assets/styles/recepcion/pendientesRecepcionCards.css"></style>

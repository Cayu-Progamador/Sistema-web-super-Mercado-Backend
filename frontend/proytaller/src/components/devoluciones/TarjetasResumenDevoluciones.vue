<template>
  <div class="dv-kpi-grid q-mb-md">
    <div
      v-for="card in cards"
      :key="card.id"
      class="dv-card-col"
    >
      <q-card class="dv-stat-card" :style="{ '--delay': `${card.delay}s` }">
        <q-card-section class="dv-card-content">
          <div class="dv-card-main">
            <div class="dv-card-icon-wrapper" :class="`dv-icon-${card.id}`">
              <q-icon :name="card.icon" size="21px" />
            </div>
            <div class="dv-card-info">
              <span class="dv-card-label">{{ card.title }}</span>
              <span class="dv-card-value">{{ card.value }}</span>
              <span class="dv-card-desc">{{ card.desc }}</span>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  total: { type: Number, default: 0 },
  borradores: { type: Number, default: 0 },
  solicitadas: { type: Number, default: 0 },
  enviadas: { type: Number, default: 0 },
  recibidas: { type: Number, default: 0 },
  rechazadas: { type: Number, default: 0 }
})

const cards = computed(() => [
  {
    id: 'borrador',
    title: 'Borradores',
    icon: 'edit_note',
    value: String(props.borradores),
    desc: 'Pendientes de confirmar',
    delay: 0
  },
  {
    id: 'solicitada',
    title: 'Confirmadas',
    icon: 'send',
    value: String(props.solicitadas),
    desc: 'Esperando envío',
    delay: 0.08
  },
  {
    id: 'enviada',
    title: 'Enviadas',
    icon: 'local_shipping',
    value: String(props.enviadas),
    desc: 'En camino al proveedor',
    delay: 0.16
  },
  {
    id: 'recibida',
    title: 'Completadas',
    icon: 'check_circle',
    value: String(props.recibidas),
    desc: 'Aceptadas por proveedor',
    delay: 0.24
  },
  {
    id: 'rechazada',
    title: 'En proceso',
    icon: 'cancel',
    value: String(props.rechazadas),
    desc: 'No aceptadas',
    delay: 0.32
  },
  {
    id: 'anulada',
    title: 'Canceladas',
    icon: 'block',
    value: String(props.total - props.borradores - props.solicitadas - props.enviadas - props.recibidas - props.rechazadas),
    desc: 'Devoluciones anuladas',
    delay: 0.4
  }
])
</script>

<style scoped src="../../assets/styles/devoluciones/devoluciones.css"></style>

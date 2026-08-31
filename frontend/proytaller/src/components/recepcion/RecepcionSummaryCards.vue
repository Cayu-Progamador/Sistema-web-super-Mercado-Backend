<template>
  <div class="rp-kpi-grid q-mb-md">
    <div
      v-for="card in cards"
      :key="card.id"
      class="rp-card-col"
    >
      <q-card class="rp-stat-card" :style="{ '--delay': `${card.delay}s` }">
        <q-card-section class="rp-card-content">
          <div class="rp-card-main">
            <div class="rp-card-icon-wrapper" :class="`rp-icon-${card.id}`">
              <q-icon :name="card.icon" size="21px" />
            </div>
            <div class="rp-card-info">
              <span class="rp-card-label">{{ card.title }}</span>
              <span class="rp-card-value">{{ card.value }}</span>
              <span class="rp-card-desc">{{ card.desc }}</span>
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
  revision: { type: Number, default: 0 },
  completas: { type: Number, default: 0 },
  parciales: { type: Number, default: 0 },
  rechazadas: { type: Number, default: 0 },
  anuladas: { type: Number, default: 0 }
})

const cards = computed(() => [
  {
    id: 'total',
    title: 'Total Recepciones',
    icon: 'inventory',
    value: String(props.total),
    desc: 'Recepciones registradas',
    delay: 0
  },
  {
    id: 'revision',
    title: 'En Revisión',
    icon: 'fact_check',
    value: String(props.revision),
    desc: 'Pendientes de validar',
    delay: 0.08
  },
  {
    id: 'completa',
    title: 'Completas',
    icon: 'check_circle',
    value: String(props.completas),
    desc: 'Recepción finalizada',
    delay: 0.16
  },
  {
    id: 'parcial',
    title: 'Parciales',
    icon: 'hourglass_bottom',
    value: String(props.parciales),
    desc: 'Recepción incompleta',
    delay: 0.24
  },
  {
    id: 'rechazada',
    title: 'Rechazadas',
    icon: 'not_interested',
    value: String(props.rechazadas),
    desc: 'Mercadería no aceptada',
    delay: 0.32
  },
  {
    id: 'anulada',
    title: 'Anuladas',
    icon: 'block',
    value: String(props.anuladas),
    desc: 'Recepciones anuladas',
    delay: 0.4
  }
])
</script>

<style scoped src="../../assets/styles/recepcion/recepcion.css"></style>

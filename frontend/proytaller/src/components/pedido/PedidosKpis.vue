<template>
  <div class="row q-col-gutter-lg q-mb-xl">
    <div
      v-for="card in cards"
      :key="card.id"
      class="col-12 col-sm-6 col-md-4 col-lg-2 card-col"
    >
      <q-card
        class="pd-stat-card"
        :style="{ '--delay': `${card.delay}s` }"
      >
        <q-card-section class="card-content q-pa-lg">
          <div class="card-main">
            <div class="card-icon-wrapper" :class="`icon-${card.color}`">
              <q-icon :name="card.icon" size="22px" />
            </div>
            <div class="card-info">
              <span class="card-label">{{ card.title }}</span>
              <span class="card-value">{{ card.value }}</span>
              <span class="card-desc">{{ card.desc }}</span>
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
  enviados: { type: Number, default: 0 },
  parciales: { type: Number, default: 0 },
  completados: { type: Number, default: 0 },
  cancelados: { type: Number, default: 0 }
})

const cards = computed(() => [
  {
    id: 'total',
    title: 'Total Pedidos',
    icon: 'receipt_long',
    color: 'green',
    value: String(props.total),
    desc: 'Pedidos registrados',
    delay: 0
  },
  {
    id: 'borradores',
    title: 'Borradores',
    icon: 'edit_note',
    color: 'grey',
    value: String(props.borradores),
    desc: 'Sin enviar',
    delay: 0.1
  },
  {
    id: 'enviados',
    title: 'Enviados',
    icon: 'send',
    color: 'blue',
    value: String(props.enviados),
    desc: 'Pedidos enviados',
    delay: 0.2
  },
  {
    id: 'parciales',
    title: 'Parcialmente Recibidos',
    icon: 'hourglass_bottom',
    color: 'orange',
    value: String(props.parciales),
    desc: 'Recepción en curso',
    delay: 0.3
  },
  {
    id: 'completados',
    title: 'Completados',
    icon: 'check_circle',
    color: 'purple',
    value: String(props.completados),
    desc: 'Recepción finalizada',
    delay: 0.4
  },
  {
    id: 'cancelados',
    title: 'Cancelados',
    icon: 'cancel',
    color: 'red',
    value: String(props.cancelados),
    desc: 'Pedidos anulados',
    delay: 0.5
  }
])
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800&display=swap');

.card-col {
  --delay: 0s;
}

.pd-stat-card {
  border-radius: 16px !important;
  background: #ffffff !important;
  border: 1px solid #E0E8DF !important;
  box-shadow: 0 2px 16px rgba(46, 125, 50, 0.07) !important;
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
  animation: cardEntrance 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
  animation-delay: var(--delay);
}

.pd-stat-card:hover {
  box-shadow: 0 10px 32px rgba(46, 125, 50, 0.14), 0 2px 8px rgba(0, 0, 0, 0.04) !important;
  transform: translateY(-3px);
  border-color: #C8E6C9 !important;
}

.card-content {
  padding: 20px !important;
}

.card-main {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.card-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.pd-stat-card:hover .card-icon-wrapper {
  transform: scale(1.08);
}

.icon-green {
  background: #388E3C;
  color: #ffffff;
}

.icon-blue {
  background: #1976D2;
  color: #ffffff;
}

.icon-orange {
  background: #F57C00;
  color: #ffffff;
}

.icon-purple {
  background: #2E7D32;
  color: #ffffff;
}

.icon-grey {
  background: #9E9E9E;
  color: #ffffff;
}

.icon-red {
  background: #D32F2F;
  color: #ffffff;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.card-label {
  font-family: 'Nunito', sans-serif;
  font-size: 12.5px;
  font-weight: 600;
  color: #5B8A53;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-value {
  font-family: 'Nunito', sans-serif;
  font-size: 25px;
  font-weight: 800;
  color: #1B5E20;
  line-height: 1.2;
}

.card-desc {
  font-family: 'Nunito', sans-serif;
  font-size: 11px;
  font-weight: 500;
  color: #9DBB92;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
}

@keyframes cardEntrance {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 599px) {
  .card-content {
    padding: 20px !important;
  }
}
</style>

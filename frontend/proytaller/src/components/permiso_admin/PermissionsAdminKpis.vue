<template>
  <div class="kpi-grid">
    <div
      v-for="card in cards"
      :key="card.id"
      class="kpi-col"
    >
      <q-card
        class="permiso-stat-card"
        style="height: 100%"
        :style="{ '--delay': `${card.delay}s` }"
      >
        <q-card-section class="card-content">
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
  pendientes: { type: Number, default: 0 },
  aprobadasMes: { type: Number, default: 0 },
  rechazadas: { type: Number, default: 0 },
  conPermisoHoy: { type: Number, default: 0 },
})

const cards = computed(() => [
  {
    id: 'pendientes',
    title: 'Pendientes',
    value: String(props.pendientes),
    desc: 'Solicitudes por revisar',
    icon: 'hourglass_empty',
    color: 'blue',
    delay: 0,
  },
  {
    id: 'aprobadas',
    title: 'Aprobadas este mes',
    value: String(props.aprobadasMes),
    desc: 'Permisos concedidos',
    icon: 'check_circle',
    color: 'green',
    delay: 0.1,
  },
  {
    id: 'rechazadas',
    title: 'Rechazadas',
    value: String(props.rechazadas),
    desc: 'Solicitudes denegadas',
    icon: 'cancel',
    color: 'red',
    delay: 0.2,
  },
  {
    id: 'hoy',
    title: 'Con permiso hoy',
    value: String(props.conPermisoHoy),
    desc: 'Personal ausente',
    icon: 'event_busy',
    color: 'orange',
    delay: 0.3,
  },
])
</script>

<style scoped>
.kpi-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
}
.kpi-col {
  display: flex;
  flex: 1 1 100%;
  min-width: 0;
}
@media (min-width: 433px) {
  .kpi-col {
    flex: 0 0 calc(50% - 8px);
    max-width: calc(50% - 8px);
  }
}
@media (min-width: 900px) {
  .kpi-col {
    flex: 0 0 calc(25% - 12px);
    max-width: calc(25% - 12px);
  }
}
@media (min-width: 1200px) {
  .kpi-col {
    flex: 1 1 0;
    min-width: 180px;
    max-width: 260px;
  }
}

.permiso-stat-card {
  width: 100%;
  border-radius: 16px !important;
  background: #ffffff !important;
  border: 1px solid #C8E6C9 !important;
  box-shadow: 0 2px 16px rgba(46, 125, 50, 0.07) !important;
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
  animation: cardEntrance 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
  animation-delay: var(--delay);
}

.permiso-stat-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06), 0 2px 8px rgba(0, 0, 0, 0.04) !important;
  transform: translateY(-3px);
  border-color: rgba(0, 0, 0, 0.06) !important;
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
  width: 55px;
  height: 55px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.permiso-stat-card:hover .card-icon-wrapper {
  transform: scale(1.08);
}

.icon-blue {
  background: #2E7D32;
  color: #ffffff;
}
.icon-green {
  background: #2E7D32;
  color: #ffffff;
}
.icon-red {
  background: #C10015;
  color: #ffffff;
}
.icon-orange {
  background: #F57C00;
  color: #ffffff;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.card-label {
  font-size: 13px;
  font-weight: 600;
  color: #388E3C;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-value {
  font-size: 26px;
  font-weight: 800;
  color: #1B5E20;
  line-height: 1.2;
}

.card-desc {
  font-size: 11.5px;
  font-weight: 500;
  color: #66BB6A;
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

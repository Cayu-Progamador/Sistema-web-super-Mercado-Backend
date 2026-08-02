<template>
  <div class="kpi-grid q-mb-xl">
    <div
      v-for="card in cards"
      :key="card.id"
      class="kpi-col"
    >
      <q-card
        class="asistencia-stat-card"
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
  asistencias: { type: Number, default: 0 },
  tardanzas: { type: Number, default: 0 },
  faltas: { type: Number, default: 0 },
  puntualidad: { type: Number, default: 0 },
  permisos: { type: Number, default: 0 }
})

const cards = computed(() => [
  {
    id: 'asistencias',
    title: 'Asistencias',
    icon: 'fact_check',
    color: 'green',
    value: String(props.asistencias),
    desc: 'Días asistidos en el mes',
    delay: 0
  },
  {
    id: 'tardanzas',
    title: 'Tardanzas',
    icon: 'access_time',
    color: 'orange',
    value: String(props.tardanzas),
    desc: 'Llegadas tarde registradas',
    delay: 0.1
  },
  {
    id: 'faltas',
    title: 'Faltas',
    icon: 'block',
    color: 'red',
    value: String(props.faltas),
    desc: 'Inasistencias en el mes',
    delay: 0.2
  },
  {
    id: 'puntualidad',
    title: 'Puntualidad',
    icon: 'verified',
    color: 'blue',
    value: `${props.puntualidad}%`,
    desc: 'Porcentaje de puntualidad',
    delay: 0.3
  }
])
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800&display=swap');

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
    flex: 0 0 calc(50% - 16px);
    max-width: calc(50% - 16px);
  }
}
@media (min-width: 768px) {
  .kpi-col {
    flex: 0 0 calc(33.333% - 16px);
    max-width: calc(33.333% - 16px);
  }
}
@media (min-width: 1200px) {
  .kpi-col {
    flex: 1 1 0;
    min-width: 180px;
    max-width: 260px;
  }
}

.asistencia-stat-card {
  border-radius: 16px !important;
  background: #ffffff !important;
  border: 1px solid #bce9e2 !important;
  box-shadow: 0 2px 16px rgba(0, 96, 81, 0.07) !important;
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
  animation: cardEntrance 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
  animation-delay: var(--delay);
}

.asistencia-stat-card:hover {
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

.asistencia-stat-card:hover .card-icon-wrapper {
  transform: scale(1.08);
}

.icon-green {
  background: #2E7D32;
  color: #ffffff;
}

.icon-orange {
  background: #d97b1a;
  color: #ffffff;
}

.icon-red {
  background: #C10015;
  color: #ffffff;
}

.icon-blue {
  background: #1565C0;
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
  font-size: 13px;
  font-weight: 600;
  color: #4a9e8a;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-value {
  font-family: 'Nunito', sans-serif;
  font-size: 26px;
  font-weight: 800;
  color: #006051;
  line-height: 1.2;
}

.card-desc {
  font-family: 'Nunito', sans-serif;
  font-size: 11.5px;
  font-weight: 500;
  color: #4a9e8a;
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

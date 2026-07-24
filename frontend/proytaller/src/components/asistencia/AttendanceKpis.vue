<template>
  <div class="kpi-grid q-mb-md">
    <div v-for="(kpi, idx) in kpis" :key="kpi.label" class="kpi-col" :style="{ '--delay': `${idx * 0.1}s` }">
      <q-card class="asistencia-stat-card">
        <q-card-section class="card-content">
          <div class="card-main">
            <div class="card-icon-wrapper" :class="`icon-${kpi.color}`">
              <q-icon :name="kpi.icono" size="22px" />
            </div>
            <div class="card-info">
              <span class="card-label">{{ kpi.label }}</span>
              <span class="card-value">{{ kpi.valor }}</span>
              <span class="card-desc">{{ kpi.desc }}</span>
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
  presentes: { type: Number, default: 0 },
  ausentes: { type: Number, default: 0 },
  tardanzas: { type: Number, default: 0 },
  permisos: { type: Number, default: 0 },
  total: { type: Number, default: 0 }
})

const kpis = computed(() => [
  { label: 'Presentes hoy', valor: props.presentes, icono: 'check_circle', color: 'green', desc: 'Marcaron asistencia' },
  { label: 'Ausentes', valor: props.ausentes, icono: 'cancel', color: 'red', desc: 'Sin registro hoy' },
  { label: 'Tardanzas', valor: props.tardanzas, icono: 'access_time', color: 'orange', desc: 'Llegaron tarde' },
  { label: 'Justificados', valor: props.permisos, icono: 'event_busy', color: 'lime', desc: 'Ausencias justificadas' },
  { label: '% Asistencia', valor: props.total > 0 ? Math.round((props.presentes / props.total) * 100) + '%' : '0%', icono: 'analytics', color: 'green', desc: 'Del total de empleados' }
])
</script>

<style scoped>
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
}

@media (max-width: 900px) {
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .kpi-grid {
    grid-template-columns: 1fr;
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
  background: #006051;
  color: #ffffff;
}

.icon-red {
  background: #C10015;
  color: #ffffff;
}

.icon-orange {
  background: #d97b1a;
  color: #ffffff;
}

.icon-lime {
  background: #8BC34A;
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
  color: #6b7280;
  overflow: hidden;
  text-overflow: ellipsis;
}

@keyframes cardEntrance {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

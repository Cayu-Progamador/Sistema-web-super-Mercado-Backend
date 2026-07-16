<template>
  <div class="row q-col-gutter-sm q-mb-md">
    <div class="col-6 col-md-2" v-for="kpi in kpis" :key="kpi.label">
      <q-card flat bordered class="admin-kpi-card q-pa-sm q-px-md">
        <div class="row items-center no-wrap">
          <div class="col">
            <div class="kpi-number" :style="{ color: kpi.color }">{{ kpi.valor }}</div>
            <div class="kpi-label">{{ kpi.label }}</div>
            <div v-if="kpi.variacion !== undefined" class="kpi-variacion" :class="kpi.variacion >= 0 ? 'text-positive' : 'text-negative'">
              <q-icon :name="kpi.variacion >= 0 ? 'trending_up' : 'trending_down'" size="14px" />
              {{ Math.abs(kpi.variacion) }}%
            </div>
          </div>
          <div class="col-auto">
            <q-icon :name="kpi.icono" :style="{ fontSize: '1.8rem', color: kpi.color, opacity: 0.2 }" />
          </div>
        </div>
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
  { label: 'Presentes hoy', valor: props.presentes, color: '#2E7D32', icono: 'check_circle', variacion: 12 },
  { label: 'Ausentes', valor: props.ausentes, color: '#D32F2F', icono: 'cancel', variacion: -5 },
  { label: 'Tardanzas', valor: props.tardanzas, color: '#F57C00', icono: 'access_time', variacion: 8 },
  { label: 'Permisos', valor: props.permisos, color: '#757575', icono: 'event_busy', variacion: 3 },
  { label: '% Asistencia', valor: props.total > 0 ? Math.round((props.presentes / props.total) * 100) : 0, color: '#1565C0', icono: 'analytics', variacion: 2 }
])
</script>

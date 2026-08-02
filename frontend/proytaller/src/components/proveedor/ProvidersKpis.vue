<template>
  <div class="pv-kpis row q-col-gutter-md">
    <q-card v-for="kpi in kpis" :key="kpi.key" class="pv-kpi-card col-6 col-lg" flat>
      <q-card-section class="q-pa-md">
        <div class="pv-kpi-top">
          <div class="pv-kpi-icon" :style="{ background: kpi.bg, color: kpi.color }">
            <q-icon :name="kpi.icon" size="20px" />
          </div>
          <q-icon name="more_horiz" size="18px" class="pv-kpi-more" color="grey-4" />
        </div>
        <div class="pv-kpi-num">{{ kpi.valor }}</div>
        <div class="pv-kpi-label">{{ kpi.label }}</div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  total: { type: Number, default: 0 },
  empresas: { type: Number, default: 0 },
  personas: { type: Number, default: 0 },
  activos: { type: Number, default: 0 },
  calificacion: { type: Number, default: 0 }
})

const kpis = computed(() => [
  {
    key: 'total',
    label: 'Total Proveedores',
    valor: props.total,
    icon: 'storefront',
    color: '#2E7D32',
    bg: '#E8F5E9'
  },
  {
    key: 'empresas',
    label: 'Proveedores Empresa',
    valor: props.empresas,
    icon: 'apartment',
    color: '#1976D2',
    bg: '#E7F1FC'
  },
  {
    key: 'personas',
    label: 'Proveedores Persona',
    valor: props.personas,
    icon: 'person',
    color: '#7B1FA2',
    bg: '#F3E8FB'
  },
  {
    key: 'activos',
    label: 'Proveedores Activos',
    valor: props.activos,
    icon: 'verified_user',
    color: '#388E3C',
    bg: '#E8F5E9'
  },
  {
    key: 'rating',
    label: 'Calificación Promedio',
    valor: props.calificacion.toFixed(1),
    icon: 'star',
    color: '#F9A825',
    bg: '#FFF8E1'
  }
])
</script>

<style scoped>
.pv-kpi-card {
  border-radius: 14px;
  border: 1px solid #e6ebf1;
  box-shadow: 0 1px 3px rgba(16, 24, 40, 0.05) !important;
  background: #ffffff;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.pv-kpi-card:hover {
  box-shadow: 0 6px 18px rgba(16, 24, 40, 0.09) !important;
  transform: translateY(-1px);
}

.pv-kpi-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.pv-kpi-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pv-kpi-more {
  opacity: 0.6;
}

.pv-kpi-num {
  font-size: 24px;
  font-weight: 800;
  color: #16231c;
  font-family: 'DM Sans', 'Nunito', sans-serif;
  line-height: 1.1;
}

.pv-kpi-label {
  font-size: 11px;
  font-weight: 700;
  color: #6b7a72;
  margin-top: 4px;
  font-family: 'Nunito', sans-serif;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}
</style>

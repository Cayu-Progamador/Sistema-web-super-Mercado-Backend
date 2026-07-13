<template>
  <div class="row q-col-gutter-md q-mb-md kpi-row">
    <div v-for="kpi in kpis" :key="kpi.label" class="col-12 col-sm-6 col-md-2-4">
      <q-card flat bordered class="kpi-card" :style="{ borderLeftColor: kpi.color, borderLeftWidth: '4px', borderLeftStyle: 'solid' }">
        <q-card-section class="q-py-md q-px-lg">
          <div class="row items-center justify-between">
            <div>
              <div class="kpi-number" :style="{ color: kpi.color }">{{ kpi.valor }}</div>
              <div class="kpi-title">{{ kpi.label }}</div>
            </div>
            <div class="kpi-icon-wrapper" :style="{ background: kpi.bg }">
              <q-icon :name="kpi.icon" :color="kpi.color" size="32px" />
            </div>
          </div>
          <div class="kpi-variacion q-mt-sm">
            <q-icon :name="kpi.trending === 'up' ? 'trending_up' : 'trending_down'" :color="kpi.trending === 'up' ? 'green' : 'red'" size="18px" />
            <span class="q-ml-xs" :class="kpi.trending === 'up' ? 'text-green' : 'text-red'">{{ kpi.porcentaje }}</span>
            <span class="text-grey-6 q-ml-xs">vs mes anterior</span>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listarContratos } from '../../api/contrato/contrato'

const kpis = ref([
  { label: 'Total Contratos', valor: 0, icon: 'contract', color: '#2E7D32', bg: '#e8f5e9', trending: 'up', porcentaje: '+12%' },
  { label: 'Vigentes', valor: 0, icon: 'check_circle', color: '#1565C0', bg: '#e3f2fd', trending: 'up', porcentaje: '+5%' },
  { label: 'Próximos a vencer', valor: 0, icon: 'schedule', color: '#E65100', bg: '#fff3e0', trending: 'down', porcentaje: '-2%' },
  { label: 'Vencidos', valor: 0, icon: 'error_outline', color: '#C62828', bg: '#ffebee', trending: 'up', porcentaje: '+1%' },
  { label: 'Suspendidos', valor: 0, icon: 'pause_circle', color: '#616161', bg: '#f5f5f5', trending: 'down', porcentaje: '-3%' }
])

async function cargarKPIs() {
  try {
      const res = await listarContratos({ page: 0, size: 1 })
      const total = res?.totalElements || 0
      const items = res?.content || []
      const activos = items.filter(c => c.estado === 'ACTIVO').length
      const vencidos = items.filter(c => c.estado === 'VENCIDO').length
      const suspendidos = items.filter(c => c.estado === 'SUSPENDIDO').length
    kpis.value[0].valor = total
    kpis.value[1].valor = activos
    kpis.value[3].valor = vencidos
    kpis.value[4].valor = suspendidos
  } catch {
    kpis.value.forEach(k => { k.valor = 0 })
  }
}

onMounted(cargarKPIs)

defineExpose({ cargarKPIs })
</script>

<style scoped>
.kpi-row {
  margin-bottom: 0 !important;
}
.kpi-card {
  border-radius: 14px;
  background: white;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  transition: transform 0.2s, box-shadow 0.2s;
}
.kpi-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
.kpi-number {
  font-size: 32px;
  font-weight: 700;
  line-height: 1.2;
}
.kpi-title {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
  margin-top: 2px;
}
.kpi-icon-wrapper {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.kpi-variacion {
  font-size: 12px;
  display: flex;
  align-items: center;
}
</style>

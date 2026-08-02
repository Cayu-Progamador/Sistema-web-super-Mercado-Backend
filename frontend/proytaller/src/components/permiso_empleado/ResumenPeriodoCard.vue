<template>
  <q-card class="periodo-card">
    <div class="periodo-inner">
      <div class="periodo-top">
        <div class="periodo-info">
          <div class="periodo-label">Resumen del período</div>
          <div class="periodo-mes">{{ mesActual }}</div>
        </div>
        <div class="periodo-icon">
          <q-icon name="summarize" size="32px" />
        </div>
      </div>

      <div class="periodo-stats row q-col-gutter-x-sm q-mt-md">
        <div class="col-4">
          <div class="stat-box stat-total">
            <div class="stat-num">{{ totalSolicitudes }}</div>
            <div class="stat-label">Solicitudes</div>
          </div>
        </div>
        <div class="col-4">
          <div class="stat-box stat-dias">
            <div class="stat-num">{{ diasSolicitados }}</div>
            <div class="stat-label">Días pedidos</div>
          </div>
        </div>
        <div class="col-4">
          <div class="stat-box stat-pendientes">
            <div class="stat-num">{{ pendientes }}</div>
            <div class="stat-label">Pendientes</div>
          </div>
        </div>
      </div>

      <div class="periodo-footer row q-gutter-x-sm q-mt-sm">
        <div class="mini-chip mini-aprobado">
          <q-icon name="check_circle" size="14px" />
          <span>{{ aprobadas }} aprobadas</span>
        </div>
        <div class="mini-chip mini-rechazado">
          <q-icon name="cancel" size="14px" />
          <span>{{ rechazadas }} rechazadas</span>
        </div>
      </div>
    </div>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  totalSolicitudes: { type: Number, default: 0 },
  diasSolicitados: { type: Number, default: 0 },
  pendientes: { type: Number, default: 0 },
  aprobadas: { type: Number, default: 0 },
  rechazadas: { type: Number, default: 0 }
})

const meses = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Setiembre', 'Octubre', 'Noviembre', 'Diciembre'
]
const mesActual = computed(() => {
  const d = new Date()
  return `${meses[d.getMonth()]} ${d.getFullYear()}`
})
</script>

<style scoped>
.periodo-card {
  border-radius: 16px !important;
  overflow: hidden !important;
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
}
.periodo-inner {
  background: linear-gradient(135deg, #2E7D32 0%, #388E3C 50%, #43A047 100%);
  border-radius: 16px;
  padding: 22px 20px 18px;
  color: #fff;
}
.periodo-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.periodo-label {
  font-size: 13px;
  font-weight: 600;
  opacity: 0.85;
  letter-spacing: 0.3px;
}
.periodo-mes {
  font-size: 22px;
  font-weight: 800;
  margin-top: 2px;
}
.periodo-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.periodo-stats .stat-box {
  text-align: center;
  padding: 10px 4px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.13);
}
.stat-num {
  font-size: 24px;
  font-weight: 800;
  line-height: 1;
}
.stat-label {
  font-size: 10px;
  font-weight: 500;
  opacity: 0.8;
  margin-top: 4px;
}
.periodo-footer {
  display: flex;
}
.mini-chip {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 6px 4px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.13);
  font-size: 11px;
  font-weight: 600;
}
</style>

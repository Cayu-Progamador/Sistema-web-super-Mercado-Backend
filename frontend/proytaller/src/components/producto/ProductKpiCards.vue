<template>
  <div class="prod-kpi-grid q-mb-lg">
    <div v-for="card in cards" :key="card.id" class="prod-kpi-col">
      <q-card
        class="prod-stat-card"
        style="height: 100%"
        :style="{ '--delay': `${card.delay}s` }"
      >
        <q-card-section class="prod-card-content">
          <div class="prod-card-main">
            <div class="prod-card-icon" :class="`prod-icon-${card.color}`">
              <q-icon :name="card.icon" size="22px" />
            </div>
            <div class="prod-card-info">
              <span class="prod-card-label">{{ card.title }}</span>
              <span class="prod-card-value">{{ card.value }}</span>
              <span class="prod-card-desc">{{ card.desc }}</span>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProductoDashboard } from '../../api/productos/productos'

const cards = ref([
  {
    id: 'total',
    title: 'Total Productos',
    icon: 'inventory_2',
    color: 'green',
    value: '0',
    desc: 'Productos registrados en el catálogo',
    delay: 0
  },
  {
    id: 'activos',
    title: 'Activos',
    icon: 'check_circle',
    color: 'green',
    value: '0',
    desc: 'Productos en venta activos',
    delay: 0.1
  },
  {
    id: 'inactivos',
    title: 'Inactivos',
    icon: 'block',
    color: 'red',
    value: '0',
    desc: 'Productos desactivados',
    delay: 0.2
  },
  {
    id: 'presentaciones',
    title: 'Con Presentaciones',
    icon: 'layers',
    color: 'orange',
    value: '0',
    desc: 'Productos con presentaciones de venta',
    delay: 0.3
  },
  {
    id: 'categorias',
    title: 'Categorías',
    icon: 'category',
    color: 'blue',
    value: '0',
    desc: 'Categorías del catálogo',
    delay: 0.4
  }
])

async function cargarDatos() {
  try {
    const data = await getProductoDashboard()
    cards.value[0].value = String(data.total ?? 0)
    cards.value[1].value = String(data.activos ?? 0)
    cards.value[2].value = String(data.inactivos ?? 0)
    cards.value[3].value = String(data.conPresentaciones ?? 0)
    cards.value[4].value = String(data.categorias ?? 0)
  } catch (e) {
    console.error('Error cargando dashboard de productos:', e)
  }
}

onMounted(cargarDatos)

defineExpose({ cargarDatos })
</script>

<style scoped>
.prod-kpi-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
}
.prod-kpi-col {
  display: flex;
  flex: 1 1 100%;
  min-width: 0;
}
@media (min-width: 433px) {
  .prod-kpi-col {
    flex: 0 0 calc(50% - 16px);
    max-width: calc(50% - 16px);
  }
}
@media (min-width: 768px) {
  .prod-kpi-col {
    flex: 0 0 calc(33.333% - 16px);
    max-width: calc(33.333% - 16px);
  }
}
@media (min-width: 1200px) {
  .prod-kpi-col {
    flex: 1 1 0;
    min-width: 180px;
    max-width: 260px;
  }
}

.prod-stat-card {
  border-radius: 16px !important;
  background: #ffffff !important;
  border: 1px solid #c8e6c9 !important;
  box-shadow: 0 2px 16px rgba(46, 125, 50, 0.08) !important;
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
  animation: prodCardEntrance 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
  animation-delay: var(--delay);
}

.prod-stat-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06), 0 2px 8px rgba(0, 0, 0, 0.04) !important;
  transform: translateY(-3px);
  border-color: rgba(46, 125, 50, 0.25) !important;
}

.prod-card-content {
  padding: 20px !important;
}

.prod-card-main {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.prod-card-icon {
  width: 55px;
  height: 55px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #ffffff;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.prod-stat-card:hover .prod-card-icon {
  transform: scale(1.08);
}

.prod-icon-green {
  background: #2E7D32;
}

.prod-icon-orange {
  background: #d97b1a;
}

.prod-icon-red {
  background: #C10015;
}

.prod-icon-blue {
  background: #00695C;
}

.prod-card-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.prod-card-label {
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: #55924F;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
}

.prod-card-value {
  font-family: 'Nunito', sans-serif;
  font-size: 26px;
  font-weight: 800;
  color: #2E7D32;
  line-height: 1.2;
}

.prod-card-desc {
  font-family: 'Nunito', sans-serif;
  font-size: 11.5px;
  font-weight: 500;
  color: #55924F;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
}

@keyframes prodCardEntrance {
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
  .prod-card-content {
    padding: 20px !important;
  }
}
</style>
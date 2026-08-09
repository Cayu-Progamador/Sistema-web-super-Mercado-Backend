<template>
  <div class="row q-col-gutter-lg q-mb-xl">
    <div
      v-for="card in cards"
      :key="card.id"
      class="col-12 col-sm-6 col-md-3 card-col"
    >
      <q-card
        class="pv-stat-card"
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
  empresas: { type: Number, default: 0 },
  personas: { type: Number, default: 0 },
  activos: { type: Number, default: 0 },
  inactivos: { type: Number, default: 0 }
})

const cards = computed(() => [
  {
    id: 'total',
    title: 'Total Proveedores',
    icon: 'storefront',
    color: 'green',
    value: String(props.total),
    desc: 'Registrados en el sistema',
    delay: 0
  },
  {
    id: 'empresas',
    title: 'Proveedores Empresa',
    icon: 'apartment',
    color: 'blue',
    value: String(props.empresas),
    desc: 'Empresas proveedoras',
    delay: 0.1
  },
  {
    id: 'personas',
    title: 'Proveedores Persona',
    icon: 'person',
    color: 'purple',
    value: String(props.personas),
    desc: 'Personas naturales',
    delay: 0.2
  },
  {
    id: 'activos',
    title: 'Proveedores Activos',
    icon: 'verified_user',
    color: 'green',
    value: String(props.activos),
    desc: 'Con operación vigente',
    delay: 0.3
  },
  {
    id: 'inactivos',
    title: 'Proveedores Inactivos',
    icon: 'person_remove',
    color: 'orange',
    value: String(props.inactivos),
    desc: 'Sin operación activa',
    delay: 0.4
  }
])
</script>

<style scoped>
.card-col {
  --delay: 0s;
}

.pv-stat-card {
  border-radius: 16px !important;
  background: #ffffff !important;
  border: 1px solid #e4edd8 !important;
  box-shadow: 0 2px 16px rgba(74, 140, 37, 0.07) !important;
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
  animation: cardEntrance 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
  animation-delay: var(--delay);
}

.pv-stat-card:hover {
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

.pv-stat-card:hover .card-icon-wrapper {
  transform: scale(1.08);
}

.icon-green {
  background: #0f6e56;
  color: #ffffff;
}

.icon-blue {
  background: #4a8c25;
  color: #ffffff;
}

.icon-orange {
  background: #d97b1a;
  color: #ffffff;
}

.icon-purple {
  background: #2a5c1a;
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
  color: #7aaa4e;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-value {
  font-family: 'Nunito', sans-serif;
  font-size: 26px;
  font-weight: 800;
  color: #2a5c1a;
  line-height: 1.2;
}

.card-desc {
  font-family: 'Nunito', sans-serif;
  font-size: 11.5px;
  font-weight: 500;
  color: #9dbf78;
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

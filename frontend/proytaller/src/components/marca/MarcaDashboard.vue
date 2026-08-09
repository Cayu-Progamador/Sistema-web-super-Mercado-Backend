<template>
  <div class="cat-dash-grid q-mb-xl">
    <div
      v-for="card in cards"
      :key="card.id"
      class="cat-dash-col"
    >
      <q-card
        class="categoria-stat-card"
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
import { ref, watch } from 'vue'

const props = defineProps({
  marcas: {
    type: Array,
    default: () => []
  }
})

const cards = ref([
  {
    id: 'total',
    title: 'Total Marcas',
    icon: 'branding_watermark',
    color: 'green',
    value: '0',
    desc: 'Todas las marcas registradas',
    delay: 0
  },
  {
    id: 'activas',
    title: 'Marcas Activas',
    icon: 'check_circle',
    color: 'blue',
    value: '0',
    desc: 'Marcas habilitadas',
    delay: 0.1
  },
  {
    id: 'inactivas',
    title: 'Marcas Inactivas',
    icon: 'cancel',
    color: 'orange',
    value: '0',
    desc: 'Marcas deshabilitadas',
    delay: 0.2
  },
  {
    id: 'nProductos',
    title: 'Productos',
    icon: 'inventory_2',
    color: 'purple',
    value: '0',
    desc: 'Total de productos de las marcas',
    delay: 0.3
  }
])

function recalcular() {
  const marcas = props.marcas || []
  cards.value[0].value = String(marcas.length)
  cards.value[1].value = String(marcas.filter((m) => m.estado === 'activo').length)
  cards.value[2].value = String(marcas.filter((m) => m.estado === 'inactivo').length)
  cards.value[3].value = String(marcas.reduce((acc, m) => acc + (Number(m.nProductos) || 0), 0))
}

watch(
  () => props.marcas,
  () => recalcular(),
  { immediate: true, deep: true }
)
</script>

<style scoped src="../../assets/styles/unidadMedida/unidadDashboard.css"></style>
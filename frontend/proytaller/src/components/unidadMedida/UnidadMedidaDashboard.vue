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
  unidades: {
    type: Array,
    default: () => []
  }
})

const cards = ref([
  {
    id: 'total',
    title: 'Total Unidades',
    icon: 'straighten',
    color: 'green',
    value: '0',
    desc: 'Todas las unidades registradas',
    delay: 0
  },
  {
    id: 'activas',
    title: 'Unidades Activas',
    icon: 'check_circle',
    color: 'blue',
    value: '0',
    desc: 'Unidades habilitadas',
    delay: 0.1
  },
  {
    id: 'inactivas',
    title: 'Unidades Inactivas',
    icon: 'cancel',
    color: 'orange',
    value: '0',
    desc: 'Unidades deshabilitadas',
    delay: 0.2
  },
  {
    id: 'tipos',
    title: 'Tipos',
    icon: 'category',
    color: 'purple',
    value: '0',
    desc: 'Tipos de medida disponibles',
    delay: 0.3
  }
])

function recalcular() {
  const unidades = props.unidades || []
  cards.value[0].value = String(unidades.length)
  cards.value[1].value = String(unidades.filter((u) => u.estado === 'activo').length)
  cards.value[2].value = String(unidades.filter((u) => u.estado === 'inactivo').length)
  cards.value[3].value = String(new Set(unidades.map((u) => u.tipoNombre)).size)
}

watch(
  () => props.unidades,
  () => recalcular(),
  { immediate: true, deep: true }
)
</script>

<style scoped src="../../assets/styles/unidadMedida/unidadDashboard.css"></style>

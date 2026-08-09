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
import { ref, onMounted } from 'vue'
import { obtenerResumenCategorias } from '../../api/categoria/categoria'

const cards = ref([
  {
    id: 'total',
    title: 'Total Categorías',
    icon: 'category',
    color: 'green',
    value: '0',
    desc: 'Todas las categorías registradas',
    delay: 0
  },
  {
    id: 'activas',
    title: 'Categorías Activas',
    icon: 'check_circle',
    color: 'blue',
    value: '0',
    desc: 'Categorías habilitadas',
    delay: 0.1
  },
  {
    id: 'inactivas',
    title: 'Categorías Inactivas',
    icon: 'cancel',
    color: 'orange',
    value: '0',
    desc: 'Categorías deshabilitadas',
    delay: 0.2
  },
  {
    id: 'subcategorias',
    title: 'Subcategorías',
    icon: 'playlist_add',
    color: 'purple',
    value: '0',
    desc: 'Total de subcategorías',
    delay: 0.3
  }
])

const cargarDatos = async () => {
  try {
    const data = await obtenerResumenCategorias()
    cards.value[0].value = String(data.totalCategorias ?? 0)
    cards.value[1].value = String(data.categoriasActivas ?? 0)
    cards.value[2].value = String(data.categoriasInactivas ?? 0)
    cards.value[3].value = String(data.subcategorias ?? 0)
  } catch {
    // valores por defecto ya están en 0
  }
}

onMounted(() => {
  cargarDatos()
})
</script>

<style scoped src="../../assets/styles/categoria/categoriaDashboard.css"></style>

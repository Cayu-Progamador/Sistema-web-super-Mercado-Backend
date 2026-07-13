<template>
  <div class="row q-col-gutter-lg q-mb-xl">
    <div
      v-for="card in cards"
      :key="card.id"
      class="col-12 col-sm-6 col-md-3 card-col"
    >
      <q-card
        class="empleado-stat-card"
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
import { getDashboardEmpleados } from '../../api/empleado/empleado'

const cards = ref([
  {
    id: 'total',
    title: 'Total Empleados',
    icon: 'groups',
    color: 'green',
    value: '0',
    desc: 'Activos en la empresa',
    delay: 0
  },
  {
    id: 'activos',
    title: 'Activos',
    icon: 'how_to_reg',
    color: 'blue',
    value: '0',
    desc: 'Empleados activos',
    delay: 0.1
  },
  {
    id: 'inactivos',
    title: 'Inactivos',
    icon: 'person_remove',
    color: 'orange',
    value: '0',
    desc: 'Empleados inactivos',
    delay: 0.2
  },
  {
    id: 'cargos',
    title: 'Cargos',
    icon: 'business',
    color: 'purple',
    value: '0',
    desc: 'Tipos de cargo',
    delay: 0.3
  }
])

const cargarDatos = async () => {
  try {
    const data = await getDashboardEmpleados()
    cards.value[0].value = String(data.totalEmpleados ?? 0)
    cards.value[1].value = String(data.empleadosActivos ?? 0)
    cards.value[2].value = String(data.empleadosInactivos ?? 0)
    cards.value[3].value = String(data.cargos ?? 0)
  } catch {
    // valores por defecto ya están en 0
  }
}

onMounted(() => {
  cargarDatos()
})
</script>

<style scoped src="../../assets/styles/empleado/empleadoDashboard.css"></style>

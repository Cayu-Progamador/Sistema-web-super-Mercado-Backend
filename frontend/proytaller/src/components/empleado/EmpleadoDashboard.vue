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
          <div class="card-body">
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
            <div class="card-trend" :class="`trend-${card.color}`">
              <svg
                width="80"
                height="48"
                viewBox="0 0 80 48"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <defs>
                  <linearGradient
                    :id="`trend-fill-${card.id}`"
                    x1="0"
                    y1="0"
                    x2="0"
                    y2="1"
                  >
                    <stop offset="0%" :stop-color="card.trendColor" stop-opacity="0.25" />
                    <stop offset="100%" :stop-color="card.trendColor" stop-opacity="0" />
                  </linearGradient>
                </defs>
                <path
                  :d="card.trendPath"
                  :stroke="card.trendColor"
                  stroke-width="2.5"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
                <path
                  :d="card.trendFillPath"
                  :fill="`url(#trend-fill-${card.id})`"
                />
                <circle
                  :cx="card.trendDotX"
                  :cy="card.trendDotY"
                  r="3"
                  :fill="card.trendColor"
                  class="trend-dot"
                />
              </svg>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script setup>
const cards = [
  {
    id: 'total',
    title: 'Total Empleados',
    icon: 'groups',
    color: 'green',
    value: '32',
    desc: 'Activos en la empresa',
    delay: 0,
    trendColor: '#006051',
    trendPath: 'M4 36 Q24 30, 40 18 T76 4',
    trendFillPath: 'M4 36 Q24 30, 40 18 T76 4 L76 48 L4 48 Z',
    trendDotX: 76,
    trendDotY: 4
  },
  {
    id: 'activos',
    title: 'Activos',
    icon: 'how_to_reg',
    color: 'blue',
    value: '28',
    desc: 'Empleados activos',
    delay: 0.1,
    trendColor: '#4a8c25',
    trendPath: 'M4 34 Q28 26, 44 14 T76 2',
    trendFillPath: 'M4 34 Q28 26, 44 14 T76 2 L76 48 L4 48 Z',
    trendDotX: 76,
    trendDotY: 2
  },
  {
    id: 'inactivos',
    title: 'Inactivos',
    icon: 'person_remove',
    color: 'orange',
    value: '4',
    desc: 'Empleados inactivos',
    delay: 0.2,
    trendColor: '#F57C00',
    trendPath: 'M4 28 Q20 22, 40 30 T76 24',
    trendFillPath: 'M4 28 Q20 22, 40 30 T76 24 L76 48 L4 48 Z',
    trendDotX: 76,
    trendDotY: 24
  },
  {
    id: 'departamentos',
    title: 'Departamentos',
    icon: 'business',
    color: 'purple',
    value: '6',
    desc: 'Áreas de trabajo',
    delay: 0.3,
    trendColor: '#0f6e56',
    trendPath: 'M4 34 Q20 26, 40 18 T76 4',
    trendFillPath: 'M4 34 Q20 26, 40 18 T76 4 L76 48 L4 48 Z',
    trendDotX: 76,
    trendDotY: 4
  }
]
</script>

<style scoped src="../../assets/styles/empleado/empleadoDashboard.css"></style>

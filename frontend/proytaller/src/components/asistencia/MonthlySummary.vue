<template>
  <div class="row q-col-gutter-md">
    <div class="col-6 col-md-3">
      <q-card flat bordered class="kpi-card q-pa-md">
        <div class="row items-center no-wrap">
          <div class="col">
            <div class="kpi-number text-positive">{{ asistencias }}</div>
            <div class="kpi-label">Asistencias</div>
          </div>
          <div class="col-auto">
            <q-icon name="fact_check" class="kpi-icon text-positive" />
          </div>
        </div>
      </q-card>
    </div>
    <div class="col-6 col-md-3">
      <q-card flat bordered class="kpi-card q-pa-md">
        <div class="row items-center no-wrap">
          <div class="col">
            <div class="kpi-number text-orange">{{ tardanzas }}</div>
            <div class="kpi-label">Tardanzas</div>
          </div>
          <div class="col-auto">
            <q-icon name="access_time" class="kpi-icon text-orange" />
          </div>
        </div>
      </q-card>
    </div>
    <div class="col-6 col-md-3">
      <q-card flat bordered class="kpi-card q-pa-md">
        <div class="row items-center no-wrap">
          <div class="col">
            <div class="kpi-number text-negative">{{ faltas }}</div>
            <div class="kpi-label">Faltas</div>
          </div>
          <div class="col-auto">
            <q-icon name="block" class="kpi-icon text-negative" />
          </div>
        </div>
      </q-card>
    </div>
    <div class="col-6 col-md-3">
      <q-card flat bordered class="kpi-card q-pa-md">
        <div class="row items-center no-wrap">
          <div class="col">
            <div class="kpi-number text-primary">{{ puntualidad }}%</div>
            <div class="kpi-label">Puntualidad</div>
          </div>
          <div class="col-auto">
            <q-icon name="verified" class="kpi-icon text-primary" />
          </div>
        </div>
      </q-card>
    </div>
  </div>
</template>

<script setup>
defineProps({
  asistencias: { type: Number, default: 0 },
  tardanzas: { type: Number, default: 0 },
  faltas: { type: Number, default: 0 },
  puntualidad: { type: Number, default: 0 }
})
</script>

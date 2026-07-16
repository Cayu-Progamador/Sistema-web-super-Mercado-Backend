<template>
  <q-card flat bordered class="timeline-card q-pa-md">
    <div class="text-weight-bold text-h6 q-mb-md" style="color:#1B5E20">
      <q-icon name="today" class="q-mr-sm" />
      Registro de hoy
    </div>
    <q-timeline :color="timelineColor" side="right">
      <q-timeline-entry
        :title="formatearHoraAMPM(horaEntrada) || 'Pendiente'"
        subtitle="Entrada"
        :icon="horaEntrada ? 'login' : 'hourglass_empty'"
        :color="horaEntrada ? 'green' : 'grey'"
      >
        <div v-if="horaEntradaEsperada" class="text-caption" style="opacity:0.7">
          Esperado: {{ formatearHoraAMPM(horaEntradaEsperada) }}
        </div>
      </q-timeline-entry>
      <q-timeline-entry
        :title="formatearHoraAMPM(horaSalida) || 'Pendiente'"
        subtitle="Salida"
        :icon="horaSalida ? 'logout' : 'hourglass_empty'"
        :color="horaSalida ? 'blue' : 'grey'"
      >
        <div v-if="horaSalidaEsperada" class="text-caption" style="opacity:0.7">
          Esperado: {{ formatearHoraAMPM(horaSalidaEsperada) }}
        </div>
      </q-timeline-entry>
    </q-timeline>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'
import { formatearHoraAMPM } from '../../util/formatearHora'

const props = defineProps({
  horaEntrada: { type: String, default: null },
  horaSalida: { type: String, default: null },
  horaEntradaEsperada: { type: String, default: null },
  horaSalidaEsperada: { type: String, default: null }
})

const timelineColor = computed(() => {
  if (props.horaEntrada && props.horaSalida) return 'positive'
  if (props.horaEntrada) return 'warning'
  return 'grey'
})
</script>

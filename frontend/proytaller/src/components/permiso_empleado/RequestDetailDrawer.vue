<template>
  <q-drawer
    v-model="visible"
    side="right"
    overlay
    class="detail-drawer"
    :width="$q.screen.lt.sm ? windowWidth : 420"
  >
    <div class="drawer-content" v-if="solicitud" style="background: #F1F8E9;">
      <div class="drawer-accent-bar"></div>

      <div class="drawer-header">
        <div class="drawer-header-left">
          <div class="drawer-icon-circle" :style="{ background: tipoColor + '18', color: tipoColor }">
            <q-icon :name="tipoIcon" size="22px" />
          </div>
          <div>
            <div class="drawer-tipo">{{ solicitud.nombreTipo }}</div>
            <div class="drawer-fechas">{{ fechasTexto }}</div>
            <q-chip
              :label="solicitud.nombreEstado"
              :color="estadoColor"
              text-color="white"
              size="11px"
              dense
              class="q-mt-xs"
            />
          </div>
        </div>
        <q-btn flat round dense icon="close" class="drawer-close" @click="cerrar" />
      </div>

      <q-separator color="green-5" />

      <div class="drawer-body">
        <div class="drawer-section">
          <div class="section-label">Motivo de la solicitud</div>
          <div class="section-text">{{ solicitud.motivo }}</div>
        </div>

        <q-separator color="green-5" />

        <div class="drawer-section">
          <div class="section-label">Flujo de aprobación</div>
          <q-timeline color="green-7" class="q-mt-sm">
            <q-timeline-entry
              title="Solicitud enviada"
              subtitle="creada"
              icon="send"
              color="green-7"
            >
              <div class="tl-text">{{ formatearFecha(solicitud.createdAt) }}</div>
            </q-timeline-entry>

            <q-timeline-entry
              title="En revisión"
              subtitle="revisión"
              icon="search"
              :color="timelineRevisionColor"
            >
              <div class="tl-text" v-if="solicitud.nombreEstado === 'Pendiente'">
                Esperando revisión
              </div>
              <div class="tl-text" v-else-if="solicitud.nombreEstado === 'En revisión'">
                En revisión
              </div>
              <div class="tl-text" v-else>
                Revisada
              </div>
            </q-timeline-entry>

            <q-timeline-entry
              title="Decisión"
              :icon="timelineDecisionIcon"
              :color="timelineDecisionColor"
            >
              <div class="tl-text" v-if="['Pendiente', 'En revisión'].includes(solicitud.nombreEstado)">
                Pendiente
              </div>
              <div v-else-if="solicitud.nombreEstado === 'Aprobado'" class="tl-text tl-aprobado">
                Aprobada
              </div>
              <div v-else-if="solicitud.nombreEstado === 'Cancelado'" class="tl-text tl-cancelado">
                Cancelada
              </div>
              <div v-else-if="solicitud.nombreEstado === 'Expirado'" class="tl-text tl-cancelado">
                Expirada
              </div>
              <div v-else-if="solicitud.nombreEstado === 'Rechazado'" class="tl-text tl-rechazado">
                Rechazada
                <div v-if="solicitud.comentarioAdmin" class="tl-comentario">
                  "{{ solicitud.comentarioAdmin }}"
                </div>
              </div>
            </q-timeline-entry>
          </q-timeline>
        </div>
      </div>
    </div>
  </q-drawer>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { date, useQuasar } from 'quasar'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  solicitud: { type: Object, default: null }
})

const $q = useQuasar()
const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const windowWidth = ref(window.innerWidth)

function onResize() {
  windowWidth.value = window.innerWidth
}

onMounted(() => window.addEventListener('resize', onResize))
onUnmounted(() => window.removeEventListener('resize', onResize))

const tipoColors = { 1: '#2E7D32', 2: '#1976D2', 3: '#F57C00', 4: '#7B1FA2' }
const tipoIcons = { 1: 'beach_access', 2: 'local_hospital', 3: 'person', 4: 'family_restroom' }

const tipoColor = computed(() => tipoColors[props.solicitud?.idTipo] || '#2E7D32')
const tipoIcon = computed(() => tipoIcons[props.solicitud?.idTipo] || 'event')

const estadoMap = {
  Pendiente: 'blue-7',
  'En revisión': 'orange-8',
  Aprobado: 'green-7',
  Rechazado: 'red-7',
  Cancelado: 'grey-6',
  Expirado: 'grey-7'
}
const estadoColor = computed(() => estadoMap[props.solicitud?.nombreEstado] || 'grey-6')

const timelineRevisionColor = computed(() => {
  const est = props.solicitud?.nombreEstado
  if (['En revisión', 'Aprobado', 'Rechazado', 'Cancelado', 'Expirado'].includes(est)) return 'orange-8'
  return 'grey-4'
})

const timelineDecisionColor = computed(() => {
  const est = props.solicitud?.nombreEstado
  if (est === 'Aprobado') return 'green-7'
  if (est === 'Rechazado') return 'red-7'
  if (['Cancelado', 'Expirado'].includes(est)) return 'grey-6'
  return 'grey-4'
})

const timelineDecisionIcon = computed(() => {
  const est = props.solicitud?.nombreEstado
  if (est === 'Aprobado') return 'check_circle'
  if (est === 'Rechazado') return 'cancel'
  if (['Cancelado', 'Expirado'].includes(est)) return 'cancel'
  return 'hourglass_empty'
})

const fechasTexto = computed(() => {
  if (!props.solicitud) return ''
  const fmt = d => {
    const [y, m, day] = d.split('-')
    return `${day}/${m}/${y}`
  }
  if (!props.solicitud.fechaFin) return fmt(props.solicitud.fechaInicio)
  return `${fmt(props.solicitud.fechaInicio)} - ${fmt(props.solicitud.fechaFin)}`
})

function formatearFecha(fecha) {
  if (!fecha) return ''
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return ''
  return d.toLocaleString('es-ES', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

function cerrar() {
  visible.value = false
}
</script>

<style scoped>
.detail-drawer {
  background: #F1F8E9 !important;
  border-radius: 16px 0 0 16px !important;
}
.drawer-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.drawer-accent-bar {
  height: 4px;
  background: linear-gradient(90deg, #1B5E20, #4CAF50);
}
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 20px 16px;
}
.drawer-header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}
.drawer-icon-circle {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.drawer-tipo {
  font-size: 16px;
  font-weight: 800;
  color: #1B5E20;
}
.drawer-fechas {
  font-size: 12px;
  color: #2E7D32;
  margin-top: 2px;
}
.drawer-close {
  border-radius: 10px !important;
  background: #E8F5E9 !important;
  color: #2E7D32 !important;
}
.drawer-body {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 20px;
}
.drawer-section {
  padding: 16px 20px;
}
.section-label {
  font-size: 13px;
  font-weight: 700;
  color: #1B5E20;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}
.section-text {
  font-size: 14px;
  color: #2E7D32;
  line-height: 1.5;
}
.tl-text {
  font-size: 13px;
  color: #388E3C;
}
.tl-aprobado {
  color: #2E7D32;
  font-weight: 700;
}
.tl-rechazado {
  color: #D32F2F;
  font-weight: 700;
}
.tl-cancelado {
  color: #718096;
  font-weight: 700;
}
.tl-comentario {
  font-weight: 400;
  font-style: italic;
  margin-top: 4px;
  font-size: 12px;
}

@media (max-width: 599px) {
  .detail-drawer {
    width: 100% !important;
    max-width: 100vw !important;
  }
  .drawer-header {
    padding: 14px 16px 12px;
  }
  .drawer-section {
    padding: 12px 16px;
  }
}
</style>

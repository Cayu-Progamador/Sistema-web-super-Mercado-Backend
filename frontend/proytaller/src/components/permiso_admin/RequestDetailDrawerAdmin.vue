<template>
  <q-drawer
    v-model="visible"
    side="right"
    overlay
    class="admin-drawer"
    :width="$q.screen.lt.sm ? windowWidth : 440"
  >
    <div class="drawer-content" v-if="solicitud" style="background: #F5F7FA;">
      <div class="drawer-accent-bar"></div>

      <div class="drawer-header">
        <div class="drawer-user">
          <q-avatar size="44px" class="drawer-avatar">
            <q-icon name="person" size="24px" color="green-7" />
          </q-avatar>
          <div>
            <div class="drawer-name">{{ solicitud.nombreEmpleado }}</div>
            <div class="drawer-meta">{{ solicitud.idEmpleado }} · {{ solicitud.nombreCargo || '—' }}</div>
          </div>
        </div>
        <div class="drawer-header-right">
          <q-chip
            :label="solicitud.nombreEstado"
            :color="chipColor"
            text-color="white"
            size="11px"
            dense
          />
          <q-btn flat round dense icon="close" class="drawer-close" @click="visible = false" />
        </div>
      </div>

      <q-separator color="green-3" />

      <div class="drawer-body">
        <div class="drawer-section">
          <div class="section-label">Detalle de la solicitud</div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-key">Tipo</span>
              <span class="detail-val">{{ solicitud.nombreTipo }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-key">Inicio</span>
              <span class="detail-val">{{ solicitud.fechaInicio }}</span>
            </div>
            <div class="detail-item" v-if="solicitud.fechaFin">
              <span class="detail-key">Fin</span>
              <span class="detail-val">{{ solicitud.fechaFin }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-key">Días</span>
              <span class="detail-val">{{ dias }} día(s)</span>
            </div>
            <div class="detail-item">
              <span class="detail-key">Día solicitado</span>
              <span class="detail-val">{{ formatearFecha(solicitud.createdAt) }}</span>
            </div>
          </div>
        </div>

        <q-separator color="green-3" />

        <div class="drawer-section">
          <div class="section-label">Motivo</div>
          <div class="section-text">{{ solicitud.motivo }}</div>
        </div>

        <div class="drawer-section" v-if="solicitud.comentarioAdmin">
          <div class="section-label">Comentario del admin</div>
          <div class="section-text section-text--rechazo">{{ solicitud.comentarioAdmin }}</div>
        </div>

        <q-separator color="green-3" />

        <div class="drawer-section">
          <div class="section-label">Historial</div>
          <div class="historial-list">
            <div v-for="h in historial" :key="h.id" class="historial-item">
              <div class="historial-dot" :style="{ background: getEstadoColor(h.estadoNuevo) }"></div>
              <div class="historial-info">
                <div class="historial-estado">{{ h.estadoNuevo }}</div>
                <div class="historial-meta">{{ h.nombreUsuario }} · {{ formatearFecha(h.fechaCambio) }}</div>
                <div class="historial-comentario" v-if="h.comentario">"{{ h.comentario }}"</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="drawer-footer" v-if="solicitud.nombreEstado === 'Pendiente'">
        <button class="btn-drawer btn-drawer--rechazar" @click="$emit('rechazar', solicitud)">
          <q-icon name="cancel" size="15px" /> Rechazar
        </button>
        <button class="btn-drawer btn-drawer--aprobar" @click="$emit('aprobar', solicitud)">
          <q-icon name="check_circle" size="15px" /> Aprobar
        </button>
      </div>
    </div>
  </q-drawer>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { date, useQuasar } from 'quasar'

const $q = useQuasar()

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  solicitud: { type: Object, default: null },
  historial: { type: Array, default: () => [] },
})

const emit = defineEmits(['update:modelValue', 'aprobar', 'rechazar'])

const visible = ref(false)
const windowWidth = ref(window.innerWidth)

function onResize() {
  windowWidth.value = window.innerWidth
}

onMounted(() => window.addEventListener('resize', onResize))
onUnmounted(() => window.removeEventListener('resize', onResize))

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const chipColor = computed(() => {
  const map = {
    Pendiente: 'blue-7',
    'En revisión': 'orange-8',
    Aprobado: 'green-7',
    Rechazado: 'red-7',
    Cancelado: 'grey-6',
    Expirado: 'grey-7',
  }
  return map[props.solicitud?.nombreEstado] || 'grey-6'
})

const dias = computed(() => {
  if (!props.solicitud) return 1
  if (!props.solicitud.fechaFin) return 1
  const s = new Date(props.solicitud.fechaInicio + 'T12:00:00')
  const e = new Date(props.solicitud.fechaFin + 'T12:00:00')
  return Math.round((e - s) / (1000 * 60 * 60 * 24)) + 1
})

function formatearFecha(f) {
  if (!f) return ''
  const d = new Date(f)
  return d.toLocaleString('es-ES', {
    day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit',
  })
}

function getEstadoColor(estado) {
  const map = {
    Pendiente: '#1976D2',
    'En revisión': '#F57C00',
    Aprobado: '#2E7D32',
    Rechazado: '#D32F2F',
    Cancelado: '#5B6675',
    Expirado: '#5B6675',
  }
  return map[estado] || '#718096'
}
</script>

<style scoped>
.admin-drawer {
  border-radius: 16px 0 0 16px !important;
  background: #F5F7FA !important;
}
.drawer-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.drawer-accent-bar {
  height: 4px;
  background: linear-gradient(90deg, #2E7D32, #66BB6A);
}
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 20px 16px;
}
.drawer-user {
  display: flex;
  align-items: center;
  gap: 12px;
}
.drawer-header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.drawer-close {
  border-radius: 10px !important;
  background: #E8F5E9 !important;
  color: #2E7D32 !important;
}
.drawer-avatar {
  background: #E8F5E9 !important;
  border-radius: 12px !important;
}
.drawer-name {
  font-size: 16px;
  font-weight: 700;
  color: #1B5E20;
}
.drawer-meta {
  font-size: 12px;
  color: #4A5568;
  margin-top: 1px;
}
.drawer-body {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 12px;
}
.drawer-section {
  padding: 14px 20px;
}
.section-label {
  font-size: 12px;
  font-weight: 700;
  color: #1B5E20;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 10px;
}
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px 16px;
}
.detail-item {
  display: flex;
  flex-direction: column;
  gap: 1px;
}
.detail-key {
  font-size: 11px;
  color: #718096;
  font-weight: 500;
  text-transform: uppercase;
}
.detail-val {
  font-size: 14px;
  color: #2D3748;
  font-weight: 600;
}
.section-text {
  font-size: 14px;
  color: #4A5568;
  line-height: 1.5;
}
.section-text--rechazo {
  color: #D32F2F;
  font-style: italic;
}
.historial-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.historial-item {
  display: flex;
  gap: 10px;
}
.historial-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-top: 5px;
  flex-shrink: 0;
}
.historial-info {
  flex: 1;
}
.historial-estado {
  font-size: 13px;
  font-weight: 600;
  color: #2D3748;
}
.historial-meta {
  font-size: 11px;
  color: #718096;
}
.historial-comentario {
  font-size: 12px;
  color: #4A5568;
  font-style: italic;
  margin-top: 2px;
}
.drawer-footer {
  padding: 12px 20px 16px;
  display: flex;
  gap: 10px;
  border-top: 1px solid #E2E8F0;
  background: #fff;
}
.btn-drawer {
  flex: 1; padding: 10px; border: none; border-radius: 9px;
  font-size: 13px; font-weight: 800; cursor: pointer;
  display: flex; align-items: center; justify-content: center; gap: 6px;
  transition: all 0.25s ease;
}
.btn-drawer--aprobar {
  background: #2E7D32; color: #fff;
  box-shadow: 0 4px 14px rgba(46,125,50,0.3);
}
.btn-drawer--aprobar:hover { background: #1B5E20; transform: translateY(-1px); box-shadow: 0 6px 20px rgba(46,125,50,0.4); }
.btn-drawer--aprobar:active { transform: translateY(0); }
.btn-drawer--rechazar {
  background: #F57C00; color: #fff;
  box-shadow: 0 4px 14px rgba(245,124,0,0.3);
}
.btn-drawer--rechazar:hover { background: #E65100; transform: translateY(-1px); box-shadow: 0 6px 20px rgba(245,124,0,0.4); }
.btn-drawer--rechazar:active { transform: translateY(0); }

@media (max-width: 599px) {
  .admin-drawer {
    width: 100% !important;
    max-width: 100vw !important;
  }
  .drawer-header {
    padding: 14px 16px 12px;
  }
  .drawer-section {
    padding: 12px 16px;
  }
  .drawer-footer {
    padding: 10px 16px 14px;
  }
  .detail-grid {
    grid-template-columns: 1fr 1fr;
    gap: 6px 12px;
  }
}
</style>

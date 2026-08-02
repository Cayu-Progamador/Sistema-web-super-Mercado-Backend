<template>
  <q-card class="request-card" flat bordered>
    <div class="rc-body">
      <div class="rc-left">
        <div class="rc-icon-circle" :style="{ background: tipoColor + '18', color: tipoColor }">
          <q-icon :name="tipoIcon" size="20px" />
        </div>
        <div class="rc-info">
          <div class="rc-tipo">{{ tipoNombre }}</div>
          <div class="rc-fechas">{{ fechas }}</div>
          <div class="rc-dias">{{ diasTexto }}</div>
          <div class="rc-motivo">{{ motivo }}</div>
        </div>
      </div>
      <div class="rc-right">
        <q-chip
          :label="estadoLabel"
          :color="estadoColor"
          text-color="white"
          size="11px"
          dense
          class="rc-chip"
        />
      </div>
    </div>
    <div class="rc-progress-bar" v-if="mostrarProgreso">
      <div class="progress-segments">
        <div class="seg" :class="{ active: progreso >= 1, done: progreso > 1 }"></div>
        <div class="seg" :class="seg2Class"></div>
        <div class="seg" :class="seg3Class"></div>
      </div>
      <div class="progress-labels">
        <span>Enviada</span>
        <span>Revisión</span>
        <span>Decisión</span>
      </div>
    </div>
    <div class="rc-actions">
      <q-btn
        unelevated
        dense
        no-caps
        color="green-8"
        label="Ver detalle"
        size="12px"
        class="btn-detalle"
        @click="$emit('ver-detalle')"
        style="background: linear-gradient(135deg, #2E7D32, #388E3C); color: #fff;"
      />
      <q-btn
        v-if="mostrarCancelar"
        unelevated
        dense
        no-caps
        color="red-6"
        label="Cancelar"
        size="12px"
        class="btn-cancelar"
        @click="$emit('cancelar')"
      />
    </div>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  tipoNombre: String,
  tipoColor: { type: String, default: '#2E7D32' },
  tipoIcon: { type: String, default: 'event' },
  fechas: String,
  diasTexto: String,
  motivo: String,
  estado: { type: String, default: 'Pendiente' },
  progreso: { type: Number, default: 1 }
})

defineEmits(['ver-detalle', 'cancelar'])

const estadoMap = {
  Pendiente: { label: 'Pendiente', color: 'blue-7' },
  'En revisión': { label: 'En revisión', color: 'orange-8' },
  Aprobado: { label: 'Aprobado', color: 'green-7' },
  Rechazado: { label: 'Rechazado', color: 'red-7' },
  Cancelado: { label: 'Cancelado', color: 'grey-6' },
  Expirado: { label: 'Expirado', color: 'grey-7' }
}

const estadoLabel = computed(() => estadoMap[props.estado]?.label || props.estado)
const estadoColor = computed(() => estadoMap[props.estado]?.color || 'grey-6')
const seg2Class = computed(() => {
  const e = props.estado
  if (['Aprobado', 'Rechazado'].includes(e)) return { done: true }
  if (e === 'En revisión') return { 'revision-active': true }
  return {}
})
const seg3Class = computed(() => {
  const e = props.estado
  if (e === 'Rechazado') return { 'rechazado-active': true }
  if (e === 'Aprobado') return { done: true }
  return {}
})
const mostrarProgreso = computed(() => !['Cancelado', 'Expirado'].includes(props.estado))
const mostrarCancelar = computed(() => props.estado === 'Pendiente')
</script>

<style scoped>
.request-card {
  border-radius: 12px !important;
  border: 1px solid #E8EDF2 !important;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04) !important;
  padding: 16px 18px 12px;
}
.rc-body {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}
.rc-left {
  display: flex;
  gap: 14px;
  flex: 1;
  min-width: 0;
}
.rc-icon-circle {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.rc-info {
  flex: 1;
  min-width: 0;
}
.rc-tipo {
  font-size: 14px;
  font-weight: 700;
  color: #1B5E20;
}
.rc-fechas {
  font-size: 12px;
  color: #4A5568;
  margin-top: 2px;
}
.rc-dias {
  font-size: 11px;
  font-weight: 600;
  color: #2E7D32;
  margin-top: 1px;
}
.rc-motivo {
  font-size: 12px;
  color: #718096;
  margin-top: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.rc-right {
  flex-shrink: 0;
}
.rc-chip {
  font-weight: 600 !important;
}
.rc-progress-bar {
  margin-top: 12px;
}
.progress-segments {
  display: flex;
  gap: 4px;
}
.seg {
  flex: 1;
  height: 4px;
  border-radius: 4px;
  background: #E2E8F0;
  transition: all 0.3s;
}
.seg.active {
  background: #2E7D32;
}
.seg.done {
  background: #2E7D32;
}
.seg.revision-active {
  background: #F57C00;
}
.seg.rechazado-active {
  background: #D32F2F;
}
.progress-labels {
  display: flex;
  justify-content: space-between;
  font-size: 10px;
  color: #A0AEC0;
  margin-top: 2px;
}
.rc-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 8px;
}
.btn-detalle {
  border-radius: 8px !important;
  font-weight: 700 !important;
  padding: 4px 14px !important;
  box-shadow: 0 2px 8px rgba(46, 125, 50, 0.2) !important;
}
.btn-cancelar {
  border-radius: 8px !important;
  font-weight: 700 !important;
  padding: 4px 14px !important;
  box-shadow: 0 2px 8px rgba(229, 57, 53, 0.2) !important;
}
</style>

<template>
  <q-dialog v-model="visible" persistent transition-show="fade" transition-hide="fade">
    <q-card class="dialog-card">
      <div class="accent-bar accent-bar--rechazar"></div>

      <div class="dialog-header dialog-header--rechazar">
        <div class="header-left">
          <div class="warn-icon warn-icon--rechazar">
            <q-icon name="cancel" size="22px" color="orange-8" />
          </div>
          <div>
            <div class="dialog-title">Rechazar Permiso</div>
            <div class="dialog-sub">La solicitud será rechazada</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn close-btn--rechazar" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">
        <div class="user-card">
          <div class="user-avatar user-avatar--rechazar">{{ iniciales }}</div>
          <div>
            <div class="user-name">{{ solicitud?.nombreEmpleado }}</div>
            <div class="user-sub">#{{ solicitud?.id }} · {{ solicitud?.nombreTipo }}</div>
          </div>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">Inicio</span>
            <span class="info-value">{{ solicitud?.fechaInicio }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Fin</span>
            <span class="info-value">{{ solicitud?.fechaFin || solicitud?.fechaInicio }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Días</span>
            <span class="info-value">{{ dias }} día(s)</span>
          </div>
        </div>

        <div class="warn-box warn-box--rechazar">
          <q-icon name="warning_amber" size="17px" color="orange-8" />
          <p>Al rechazar este permiso, el empleado <strong>deberá registrar asistencia</strong> con normalidad durante el período solicitado.</p>
        </div>

        <div class="consecuencias consecuencias--rechazar">
          <div class="cons-title">Motivo del rechazo *</div>
          <q-input
            v-model="motivo"
            type="textarea"
            outlined
            dense
            autogrow
            placeholder="Indica el motivo del rechazo"
            :rules="[val => !!val || 'El motivo es obligatorio']"
            class="motivo-input"
            input-class="motivo-textarea"
          />
        </div>
      </q-card-section>

      <div class="dialog-footer">
        <button class="btn-cancel" @click="cerrar">
          <q-icon name="close" size="15px" />Cancelar
        </button>
        <button
          class="btn-accion--rechazar"
          :class="{ 'btn-disabled': !motivo }"
          :disabled="!motivo"
          @click="confirmar"
        >
          <q-icon name="cancel" size="16px" />
          Rechazar Permiso
        </button>
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  solicitud: { type: Object, default: null },
})

const emit = defineEmits(['update:modelValue', 'confirmar'])

const visible = ref(false)
const motivo = ref('')

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) motivo.value = ''
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const iniciales = computed(() => {
  if (!props.solicitud?.nombreEmpleado) return 'E'
  return props.solicitud.nombreEmpleado.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase()
})

const dias = computed(() => {
  if (!props.solicitud) return 1
  if (!props.solicitud.fechaFin) return 1
  const s = new Date(props.solicitud.fechaInicio + 'T12:00:00')
  const e = new Date(props.solicitud.fechaFin + 'T12:00:00')
  return Math.round((e - s) / (1000 * 60 * 60 * 24)) + 1
})

function cerrar() {
  visible.value = false
}

function confirmar() {
  if (!motivo.value) return
  emit('confirmar', motivo.value)
  motivo.value = ''
}
</script>

<style scoped>
.dialog-card {
  width: 100%;
  max-width: 430px;
  border-radius: 20px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #ffe0b2;
  box-shadow: 0 20px 60px rgba(245,124,0,0.15);
  font-family: 'Nunito', sans-serif;
  animation: dialogPopIn 0.3s ease-out;
}
@keyframes dialogPopIn {
  from { opacity: 0; transform: scale(0.92) translateY(16px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.accent-bar { height: 3px; }
.accent-bar--rechazar { background: linear-gradient(90deg, #F57C00, #FFB74D, #F57C00); }
.dialog-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  border-bottom: 1px solid; padding: 16px 20px 14px;
  background: #FFF3E0; border-bottom-color: #FFCC80;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.warn-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  transition: transform 0.3s ease;
  background: #FFF3E0; border: 1.5px solid #FFB74D;
}
.header-left:hover .warn-icon { transform: rotate(-10deg) scale(1.05); }
.dialog-title { font-size: 15px; font-weight: 900; color: #BF360C; margin-bottom: 2px; }
.dialog-sub { font-size: 11px; font-weight: 600; color: #EF6C00; }
.close-btn {
  border-radius: 8px; transition: all 0.2s ease;
  color: #EF6C00; background: #FFE0B2;
}
.close-btn:hover { background: #FFCC80; color: #BF360C; transform: rotate(90deg); }
.dialog-body { padding: 18px 20px 4px; }
.user-card {
  display: flex; align-items: center; gap: 12px;
  background: #FFF8E1; border: 1px solid #FFE0B2;
  border-radius: 12px; padding: 12px 14px; margin-bottom: 14px;
  transition: all 0.2s ease;
}
.user-card:hover { border-color: #FFB74D; box-shadow: 0 2px 8px rgba(245,124,0,0.06); }
.user-avatar {
  width: 42px; height: 42px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 900; flex-shrink: 0;
  transition: transform 0.2s ease;
  background: #FFF3E0; border: 2px solid #FFB74D; color: #BF360C;
}
.user-card:hover .user-avatar { transform: scale(1.05); }
.user-name { font-size: 14px; font-weight: 800; color: #E65100; }
.user-sub { font-size: 11px; font-weight: 600; color: #FF9800; margin-top: 2px; }
.info-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px;
  margin-bottom: 14px;
}
.info-item {
  background: #FFF8E1; border: 1px solid #FFE0B2;
  border-radius: 10px; padding: 10px 12px; text-align: center;
}
.info-label { display: block; font-size: 10px; font-weight: 700; color: #FF9800; text-transform: uppercase; margin-bottom: 2px; }
.info-value { display: block; font-size: 13px; font-weight: 800; color: #BF360C; }
.warn-box {
  display: flex; align-items: flex-start; gap: 10px;
  border-radius: 10px; padding: 11px 13px; margin-bottom: 14px;
  animation: warnSlideIn 0.35s ease-out 0.1s both;
  background: #FFF3E0; border: 1px solid #FFCC80;
}
@keyframes warnSlideIn {
  from { opacity: 0; transform: translateX(-8px); }
  to { opacity: 1; transform: translateX(0); }
}
.warn-box p { font-size: 12.5px; font-weight: 600; line-height: 1.6; margin: 0; color: #E65100; }
.warn-box p strong { font-weight: 800; color: #BF360C; }
.consecuencias {
  border-radius: 10px; padding: 12px 14px; margin-bottom: 14px;
  background: #FFF3E0; border: 1px solid #FFCC80;
}
.cons-title { font-size: 12px; font-weight: 800; color: #BF360C; margin-bottom: 8px; }
.motivo-input :deep(.q-field__control) {
  border-radius: 9px !important;
  background: #fff !important;
  border: 1.5px solid #FFB74D !important;
}
.motivo-input :deep(.q-field__control::before),
.motivo-input :deep(.q-field__control::after) {
  border: none !important;
}
.motivo-input :deep(.q-field__native) {
  font-size: 13px;
  color: #4A5568;
  min-height: 60px !important;
}
.dialog-footer {
  display: flex; gap: 10px; padding: 14px 20px 18px;
  border-top: 1px solid #FFE0B2;
}
.btn-cancel {
  flex: 1; padding: 11px; background: #fff; color: #5a5a5a;
  border: 1.5px solid #d0d0d0; border-radius: 9px;
  font-size: 14px; font-weight: 700; cursor: pointer;
  display: flex; align-items: center; justify-content: center; gap: 6px;
  transition: all 0.2s ease;
}
.btn-cancel:hover { background: #f7f7f7; border-color: #bbb; }
.btn-cancel:active { transform: scale(0.97); }
.btn-accion--rechazar {
  flex: 2; padding: 11px; background: #F57C00; color: #fff;
  border: none; border-radius: 9px; font-size: 14px; font-weight: 800;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 7px;
  box-shadow: 0 4px 14px rgba(245,124,0,0.3);
  transition: all 0.25s ease;
}
.btn-accion--rechazar:hover:not(.btn-disabled) { background: #E65100; transform: translateY(-1px); box-shadow: 0 6px 20px rgba(245,124,0,0.4); }
.btn-accion--rechazar:active:not(.btn-disabled) { transform: translateY(0); }
.btn-disabled { opacity: 0.5; cursor: not-allowed; transform: none; box-shadow: none; }
</style>

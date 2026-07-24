<template>
  <q-dialog v-model="visible" persistent>
    <q-card class="dialog-card">

      <div class="accent-bar accent-bar--justificar"></div>

      <div class="dialog-header dialog-header--justificar">
        <div class="header-left">
          <div class="warn-icon warn-icon--justificar">
            <q-icon name="fact_check" size="22px" style="color: #006051" />
          </div>
          <div>
            <div class="dialog-title">Justificar inasistencia</div>
            <div class="dialog-sub">Registra el motivo de tu ausencia</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn close-btn--justificar" @click="cerrar" />
      </div>

      <div v-if="empleado" class="empleado-info">
        <q-icon name="person" size="16px" style="color: #006051" />
        <span class="empleado-nombre">{{ empleado.nombreEmpleado }}</span>
        <span class="empleado-sep">|</span>
        <span class="empleado-cargo">{{ empleado.cargo }}</span>
      </div>

      <q-card-section class="dialog-body">
        <q-input
          v-model="form.fecha"
          label="Fecha *"
          type="date"
          outlined
          dense
          stack-label
          class="form-field"
          hint="Selecciona el día que faltaste"
        />
        <q-select
          v-model="form.tipoJustificacion"
          :options="tipoOptions"
          label="Tipo"
          outlined
          dense
          stack-label
          emit-value
          map-options
          class="form-field"
          hint="Ej: Enfermedad, Duelo, etc."
        />
        <q-input
          v-model="form.motivo"
          label="Motivo *"
          type="textarea"
          outlined
          dense
          stack-label
          rows="3"
          class="form-field"
          hint="Ej: Tuve una cita médica programada"
          :maxlength="500"
          counter
        />
      </q-card-section>

      <div class="dialog-footer">
        <button class="btn-cancel" @click="cerrar">
          <q-icon name="close" size="15px" />Cancelar
        </button>
        <button
          class="btn-accion--justificar"
          :class="{ 'btn-disabled': !form.fecha || !form.motivo }"
          :disabled="!form.fecha || !form.motivo"
          @click="enviar"
        >
          <q-icon name="check" size="16px" />
          Enviar justificación
        </button>
      </div>

    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  tipo: { type: String, default: 'justificar' },
  empleado: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'enviar'])

const visible = ref(false)
const form = reactive({
  fecha: '',
  tipoJustificacion: null,
  motivo: ''
})

const tipoOptions = [
  { label: 'Enfermedad', value: 'ENFERMEDAD' },
  { label: 'Asunto personal', value: 'PERSONAL' },
  { label: 'Capacitaci&oacute;n', value: 'CAPACITACION' },
  { label: 'Duelo', value: 'DUELO' },
  { label: 'Otro', value: 'OTRO' }
]

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (!val) {
    form.fecha = ''
    form.tipoJustificacion = null
    form.motivo = ''
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

function cerrar() {
  visible.value = false
}

function enviar() {
  if (!form.fecha || !form.motivo) return
  emit('enviar', { ...form, tipoSolicitud: props.tipo })
  visible.value = false
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

.dialog-card {
  width: 100% !important;
  max-width: 430px !important;
  border-radius: 20px !important;
  overflow: hidden !important;
  background: #ffffff !important;
  border: 1px solid #bce9e2 !important;
  box-shadow: 0 20px 60px rgba(0, 96, 81, 0.15) !important;
  font-family: 'Nunito', sans-serif;
}

.accent-bar {
  height: 3px;
}
.accent-bar--justificar {
  background: linear-gradient(90deg, #8BC34A, #006051, #006051);
}

.dialog-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  border-bottom: 1px solid;
  padding: 16px 20px 14px;
}
.dialog-header--justificar {
  background: #f0faf0;
  border-bottom-color: #bce9e2;
}

.header-left { display: flex; align-items: center; gap: 12px; }
.warn-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.warn-icon--justificar {
  background: #e0f5e0; border: 1.5px solid #8BC34A;
}

.dialog-title {
  font-size: 15px; font-weight: 900; color: #006051;
  font-family: 'Nunito', sans-serif; margin-bottom: 2px;
}
.dialog-sub {
  font-size: 11px; font-weight: 600; color: #4a9e8a;
  font-family: 'Nunito', sans-serif;
}

.close-btn {
  border-radius: 8px !important;
}
.close-btn--justificar {
  color: #4a9e8a !important;
  background: #e0f5e0 !important;
}
.close-btn:hover { background: #c8eac8 !important; color: #006051 !important; }

.empleado-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px 0;
  font-family: 'Nunito', sans-serif;
}
.empleado-nombre {
  font-weight: 800;
  font-size: 14px;
  color: #006051;
}
.empleado-sep {
  color: #8BC34A;
  font-weight: 600;
}
.empleado-cargo {
  font-weight: 600;
  font-size: 13px;
  color: #4a9e8a;
}
.dialog-body {
  padding: 14px 20px 4px !important;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-field :deep(.q-field__control) {
  border-radius: 10px !important;
  background: #f7faf7 !important;
}

.form-field :deep(.q-field__control:hover) {
  background: #f0f8f0 !important;
}

.form-field :deep(.q-field__native),
.form-field :deep(.q-field__input) {
  font-family: 'Nunito', sans-serif !important;
  font-weight: 600 !important;
  color: #006051 !important;
}

.form-field :deep(.q-field__label) {
  font-family: 'Nunito', sans-serif !important;
  font-weight: 600 !important;
  color: #4a9e8a !important;
}

.form-field :deep(.q-field__control:after) {
  border-color: #8BC34A !important;
}

.form-field :deep(.q-field__bottom) {
  font-family: 'Nunito', sans-serif !important;
  font-size: 11px !important;
  color: #8BC34A !important;
  padding-top: 2px !important;
}

.form-field :deep(.q-field__counter) {
  font-family: 'Nunito', sans-serif !important;
  font-size: 11px !important;
  color: #4a9e8a !important;
}

.dialog-footer {
  display: flex; gap: 10px;
  padding: 14px 20px 18px;
  border-top: 1px solid #bce9e2;
}
.btn-cancel {
  flex: 1; padding: 11px;
  background: #fff; color: #5a5a5a;
  border: 1.5px solid #d0d0d0; border-radius: 9px;
  font-family: 'Nunito', sans-serif;
  font-size: 14px; font-weight: 700;
  cursor: pointer; transition: all 0.2s;
  display: flex; align-items: center; justify-content: center; gap: 6px;
}
.btn-cancel:hover { background: #f7f7f7; border-color: #bbb; }

.btn-accion--justificar {
  flex: 2; padding: 11px;
  background: #006051;
  color: #fff;
  border: none; border-radius: 9px;
  font-family: 'Nunito', sans-serif;
  font-size: 14px; font-weight: 800;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  box-shadow: 0 4px 14px rgba(0, 96, 81, 0.3);
  transition: all 0.2s;
}
.btn-accion--justificar:hover:not(.btn-disabled) {
  background: #004d41; transform: translateY(-1px);
}

.btn-disabled {
  opacity: 0.5 !important;
  cursor: not-allowed !important;
  transform: none !important;
  box-shadow: none !important;
}
</style>

<template>
  <q-dialog v-model="visible" persistent :maximized="$q.screen.lt.sm">
    <q-card class="new-request-dialog">
      <div class="dialog-accent-bar"></div>

      <div class="dialog-top-bar">
        <div class="dialog-top-left">
          <div class="dialog-icon-circle">
            <q-icon name="add_circle_outline" size="22px" color="#fff" />
          </div>
          <div>
            <div class="dialog-title">Nueva solicitud de permiso</div>
            <div class="dialog-sub">Completa los datos para enviar tu solicitud</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="dialog-close-btn" @click="cerrar" />
      </div>

      <q-card-section class="dialog-form">
        <q-select
          v-model="form.idTipo"
          :options="tipoOptions"
          label="Tipo de permiso *"
          outlined
          dense
          stack-label
          emit-value
          map-options
          class="form-field"
          options-selected-class="text-green-8"
        >
          <template v-slot:option="opt">
            <q-item v-bind="opt.itemProps">
              <q-item-section avatar>
                <div class="opt-dot" :style="{ background: opt.opt.color }"></div>
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ opt.opt.label }}</q-item-label>
                <q-item-label caption>{{ opt.opt.desc }}</q-item-label>
              </q-item-section>
            </q-item>
          </template>
        </q-select>

        <div class="row q-col-gutter-sm">
          <div class="col-12 col-sm-6">
            <q-input
              v-model="form.fechaInicio"
              label="Fecha inicio *"
              type="date"
              outlined
              dense
              stack-label
              class="form-field"
              :min="hoy"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-input
              v-model="form.fechaFin"
              label="Fecha fin"
              type="date"
              outlined
              dense
              stack-label
              class="form-field"
              :min="form.fechaInicio || undefined"
            />
          </div>
        </div>

        <q-banner v-if="errorFechas" class="error-banner" rounded>
          <template v-slot:avatar>
            <q-icon name="warning" color="red-7" />
          </template>
          {{ errorFechas }}
        </q-banner>
        <q-banner v-else-if="diasCalculados > 0" class="dias-banner" rounded>
          <template v-slot:avatar>
            <q-icon name="info" color="#2E7D32" />
          </template>
          Esta solicitud equivale a <strong>{{ diasCalculados }} día(s)</strong>
        </q-banner>

        <q-input
          v-model="form.motivo"
          label="Motivo *"
          type="textarea"
          outlined
          dense
          stack-label
          rows="3"
          class="form-field"
          :maxlength="500"
          counter
        />
      </q-card-section>

      <div class="dialog-footer">
        <q-btn
          outline
          no-caps
          color="grey-7"
          label="Cancelar"
          class="footer-btn"
          @click="cerrar"
        />
        <q-btn
          unelevated
          no-caps
          color="green-8"
          label="Enviar solicitud"
          class="footer-btn footer-primary"
          :disabled="!puedeEnviar"
          :loading="enviando"
          @click="enviar"
        />
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  tiposPermiso: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:modelValue', 'enviar'])

const visible = ref(false)
const enviando = ref(false)
const hoy = new Date().toISOString().split('T')[0]

const form = reactive({
  idTipo: null,
  fechaInicio: '',
  fechaFin: '',
  motivo: '',
  archivo: null
})

const tipoOptions = computed(() =>
  props.tiposPermiso.map(t => ({
    label: t.nombre,
    value: t.id,
    desc: t.descripcion || '',
    color: getTipoColor(t.id)
  }))
)

function getTipoColor(id) {
  const map = { 1: '#2E7D32', 2: '#1976D2', 3: '#F57C00', 4: '#7B1FA2' }
  return map[id] || '#2E7D32'
}

const diasCalculados = computed(() => {
  if (!form.fechaInicio) return 0
  const start = new Date(form.fechaInicio + 'T12:00:00')
  if (!form.fechaFin) return 1
  const end = new Date(form.fechaFin + 'T12:00:00')
  if (end < start) return 0
  return Math.round((end - start) / (1000 * 60 * 60 * 24)) + 1
})

const errorFechas = computed(() => {
  if (!form.fechaInicio || !form.fechaFin) return ''
  if (form.fechaFin < form.fechaInicio) return 'La fecha fin no puede ser anterior a la fecha inicio'
  return ''
})

const puedeEnviar = computed(() =>
  form.idTipo && form.fechaInicio && form.motivo.trim() && !errorFechas.value
)

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (!val) {
    form.idTipo = null
    form.fechaInicio = ''
    form.fechaFin = ''
    form.motivo = ''
    form.archivo = null
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

function cerrar() {
  visible.value = false
}

async function enviar() {
  if (!puedeEnviar.value) return
  enviando.value = true
  try {
    await emit('enviar', {
      idTipo: form.idTipo,
      fechaInicio: form.fechaInicio,
      fechaFin: form.fechaFin || null,
      motivo: form.motivo.trim(),
      archivo: form.archivo
    })
    visible.value = false
  } finally {
    enviando.value = false
  }
}
</script>

<style scoped>
.new-request-dialog {
  width: 100% !important;
  max-width: 480px !important;
  border-radius: 16px !important;
}
.dialog-accent-bar {
  height: 3px;
  background: linear-gradient(90deg, #2E7D32, #66BB6A, #2E7D32);
}
.dialog-top-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 20px 12px;
  border-bottom: 1px solid #E8EDF2;
}
.dialog-top-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.dialog-icon-circle {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #2E7D32, #388E3C);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.dialog-title {
  font-size: 16px;
  font-weight: 800;
  color: #1B5E20;
}
.dialog-sub {
  font-size: 11px;
  color: #718096;
  margin-top: 1px;
}
.dialog-close-btn {
  border-radius: 8px !important;
  background: #F0F4F8 !important;
  color: #4A5568 !important;
}
.dialog-form {
  padding: 14px 20px 0 !important;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.form-field :deep(.q-field__control) {
  border-radius: 10px !important;
  background: #F7FAFC !important;
}
.form-field :deep(.q-field__native) {
  font-weight: 500 !important;
}
.form-field :deep(.q-field__control:after) {
  border-color: #2E7D32 !important;
}
.opt-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}
.dias-banner {
  background: #E8F5E9 !important;
  color: #2E7D32 !important;
  font-size: 12px;
  min-height: 36px !important;
}
.error-banner {
  background: #FFEBEE !important;
  color: #D32F2F !important;
  font-size: 12px;
  min-height: 36px !important;
}
.dialog-footer {
  display: flex;
  gap: 10px;
  padding: 12px 20px 16px;
  border-top: 1px solid #E8EDF2;
}
.footer-btn {
  flex: 1;
  border-radius: 8px !important;
  font-weight: 700 !important;
  font-size: 13px !important;
  padding: 8px 0 !important;
}
.footer-primary {
  box-shadow: 0 4px 14px rgba(46, 125, 50, 0.3) !important;
}
</style>

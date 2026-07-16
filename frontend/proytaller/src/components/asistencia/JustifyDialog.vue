<template>
  <q-dialog v-model="visible" persistent max-width="500px">
    <q-card style="border-radius:20px; min-width:400px">
      <q-card-section class="q-pb-none">
        <div class="text-h6 text-weight-bold" style="color:#1B5E20">
          {{ tipo === 'justificar' ? 'Justificar inasistencia' : 'Solicitar permiso' }}
        </div>
      </q-card-section>

      <q-card-section class="q-gutter-y-md">
        <q-input
          v-model="form.fecha"
          label="Fecha"
          type="date"
          outlined
          dense
          stack-label
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
        />
        <q-input
          v-model="form.motivo"
          label="Motivo"
          type="textarea"
          outlined
          dense
          stack-label
          rows="3"
        />
        <q-file
          v-model="form.archivo"
          label="Adjuntar documento (opcional)"
          outlined
          dense
          stack-label
          accept=".pdf,.jpg,.jpeg,.png"
        >
          <template v-slot:prepend>
            <q-icon name="attach_file" />
          </template>
        </q-file>
      </q-card-section>

      <q-card-actions align="right" class="q-pa-md q-pt-none">
        <q-btn flat no-caps label="Cancelar" color="grey" v-close-popup />
        <q-btn
          unelevated
          no-caps
          :label="tipo === 'justificar' ? 'Enviar justificaci&oacute;n' : 'Solicitar permiso'"
          color="primary"
          :disable="!form.fecha || !form.motivo"
          @click="enviar"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  tipo: { type: String, default: 'justificar' }
})

const emit = defineEmits(['update:modelValue', 'enviar'])

const visible = ref(false)
const form = reactive({
  fecha: '',
  tipoJustificacion: null,
  motivo: '',
  archivo: null
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
    form.archivo = null
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

function enviar() {
  emit('enviar', { ...form, tipoSolicitud: props.tipo })
  visible.value = false
}
</script>

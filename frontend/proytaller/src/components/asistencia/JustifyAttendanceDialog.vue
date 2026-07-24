<template>
  <q-dialog v-model="visible" persistent max-width="500px">
    <q-card style="border-radius:20px; min-width:420px">
      <q-card-section class="q-pb-none">
        <div class="text-h6 text-weight-bold" style="color:#1B5E20">
          Justificar inasistencia
        </div>
        <div v-if="empleado" class="text-caption text-grey q-mt-xs">
          {{ empleado.nombreEmpleado }} &middot; {{ empleado.fecha }}
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
          v-model="form.tipo"
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
          rows="4"
        />

      </q-card-section>

      <q-card-actions align="right" class="q-pa-md q-pt-none">
        <q-btn flat no-caps label="Cancelar" color="grey" v-close-popup />
        <q-btn
          unelevated
          no-caps
          label="Guardar justificaci&oacute;n"
          color="primary"
          :disable="!form.fecha || !form.tipo || !form.motivo"
          @click="guardar"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  empleado: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'guardar'])

const visible = ref(false)
const form = reactive({
  fecha: '',
  tipo: null,
  motivo: ''
})

const tipoOptions = [
  { label: 'Falta justificada', value: 'FALTA_JUSTIFICADA' },
  { label: 'Permiso m&eacute;dico', value: 'PERMISO_MEDICO' },
  { label: 'Permiso personal', value: 'PERMISO_PERSONAL' },
  { label: 'Licencia', value: 'LICENCIA' }
]

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.empleado) {
    form.fecha = props.empleado.fecha || ''
  }
  if (!val) {
    form.fecha = ''
    form.tipo = null
    form.motivo = ''
  }
})

watch(visible, (val) => { emit('update:modelValue', val) })

function guardar() {
  emit('guardar', { ...form, idAsistencia: props.empleado?.idAsistencia })
  visible.value = false
}
</script>

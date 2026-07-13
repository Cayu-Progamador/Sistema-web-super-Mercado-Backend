<template>
  <q-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    maximized
    transition-show="slide-up"
    transition-hide="slide-down"
  >
    <q-card class="dialog-card">
      <q-card-section class="dialog-header q-px-lg q-py-md">
        <div class="row items-center justify-between">
          <div class="text-h5 text-weight-bold">Renovar Contrato</div>
          <q-btn flat round dense icon="close" v-close-popup />
        </div>
      </q-card-section>

      <q-separator />

      <q-card-section class="dialog-body q-pa-lg">
        <template v-if="loading">
          <q-skeleton type="rect" height="300px" />
        </template>

        <template v-else-if="contratoActual">
          <q-banner class="bg-primary text-white q-mb-md rounded-borders">
            <template v-slot:avatar>
              <q-icon name="autorenew" />
            </template>
            Estás renovando el contrato de <strong>{{ nombreEmpleado }}</strong>. Se creará un nuevo contrato con los datos actualizados.
          </q-banner>

          <div class="row q-col-gutter-lg">
            <!-- Contrato Actual -->
            <div class="col-12 col-md-6">
              <q-card flat bordered class="contract-compare-card">
                <q-card-section class="bg-grey-1 q-pa-sm text-center">
                  <div class="text-weight-bold text-grey-8">CONTRATO ACTUAL</div>
                </q-card-section>
                <q-card-section class="q-pa-md">
                  <div class="row comp-row"><span class="comp-label">N° Contrato</span><span class="comp-value">CTR-{{ String(contratoActual.id).padStart(4, '0') }}</span></div>
                  <div class="row comp-row"><span class="comp-label">Cargo</span><span class="comp-value">{{ contratoActual.cargoNombre }}</span></div>
                  <div class="row comp-row"><span class="comp-label">Tipo Contrato</span><span class="comp-value">{{ contratoActual.tipoContratoNombre }}</span></div>
                  <div class="row comp-row"><span class="comp-label">Jornada</span><span class="comp-value">{{ contratoActual.tipoJornadaNombre }}</span></div>
                  <div class="row comp-row"><span class="comp-label">Sueldo Base</span><span class="comp-value" style="color:#2E7D32">${{ contratoActual.sueldoBase?.toLocaleString() }}</span></div>
                  <div class="row comp-row"><span class="comp-label">Inicio</span><span class="comp-value">{{ contratoActual.fechaInicio }}</span></div>
                  <div class="row comp-row"><span class="comp-label">Fin</span><span class="comp-value">{{ contratoActual.fechaFin || 'Indefinido' }}</span></div>
                  <div class="row comp-row"><span class="comp-label">Estado</span>
                    <q-chip dense :color="chipColor(contratoActual.estado)" text-color="white" size="12px">{{ contratoActual.estado }}</q-chip>
                  </div>
                </q-card-section>
              </q-card>
            </div>

            <!-- Nuevo Contrato -->
            <div class="col-12 col-md-6">
              <q-card flat bordered class="contract-compare-card border-primary">
                <q-card-section class="bg-green-1 q-pa-sm text-center">
                  <div class="text-weight-bold text-green-8">NUEVO CONTRATO</div>
                </q-card-section>
                <q-card-section class="q-pa-md">
                  <div class="row q-col-gutter-sm">
                    <div class="col-12">
                      <q-select v-model="nuevoForm.cargo" :options="cargoOptions" label="Cargo *" outlined dense emit-value map-options class="q-mb-sm" />
                    </div>
                    <div class="col-12">
                      <q-select v-model="nuevoForm.tipoContrato" :options="tipoContratoOptions" label="Tipo Contrato *" outlined dense emit-value map-options class="q-mb-sm" />
                    </div>
                    <div class="col-12">
                      <q-select v-model="nuevoForm.tipoJornada" :options="tipoJornadaOptions" label="Tipo Jornada *" outlined dense emit-value map-options class="q-mb-sm" />
                    </div>
                    <div class="col-12">
                      <q-input v-model="nuevoForm.sueldoBase" label="Sueldo Base ($) *" outlined dense type="number" class="q-mb-sm" />
                    </div>
                    <div class="col-6">
                      <q-input v-model="nuevoForm.fechaInicio" label="Fecha Inicio *" outlined dense type="date" class="q-mb-sm" />
                    </div>
                    <div class="col-6">
                      <q-input v-model="nuevoForm.fechaFin" label="Fecha Fin" outlined dense type="date" class="q-mb-sm" />
                    </div>
                  </div>
                </q-card-section>
              </q-card>
            </div>
          </div>
        </template>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right" class="q-pa-md dialog-actions">
        <q-btn flat label="Cancelar" color="grey-7" v-close-popup no-caps />
        <q-btn unelevated color="primary" label="Confirmar Renovación" @click="confirmarRenovacion" no-caps :loading="saving" :disable="saving" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useQuasar } from 'quasar'
import { getContrato, createContrato, listarTiposContrato, listarTiposJornada } from '../../api/contrato/contrato'
import { listarCargos } from '../../api/cargo/cargo'

const props = defineProps({ modelValue: Boolean, contratoId: Number })
const emit = defineEmits(['update:modelValue', 'saved'])

const $q = useQuasar()
const saving = ref(false)
const loading = ref(false)
const contratoActual = ref(null)

const nuevoForm = reactive({
  cargo: null,
  tipoContrato: null,
  tipoJornada: null,
  sueldoBase: null,
  fechaInicio: '',
  fechaFin: ''
})

const cargoOptions = ref([])
const tipoContratoOptions = ref([])
const tipoJornadaOptions = ref([])

const nombreEmpleado = computed(() => {
  if (!contratoActual.value?.empleado) return ''
  return `${contratoActual.value.empleado.nombres} ${contratoActual.value.empleado.apellidos}`
})

function chipColor(estado) {
  return { ACTIVO: 'green-7', VENCIDO: 'red-6', FINALIZADO: 'grey-6', SUSPENDIDO: 'orange-7' }[estado] || 'grey-5'
}

async function cargarCargos() {
  try {
    const res = await listarCargos()
    cargoOptions.value = (res || []).map(c => ({ label: c.nombre, value: c.id }))
  } catch { cargoOptions.value = [] }
}

async function cargarTipos() {
  try {
    const [tcRes, tjRes] = await Promise.all([listarTiposContrato(), listarTiposJornada()])
    tipoContratoOptions.value = (tcRes || []).map(t => ({ label: t.nombre, value: t.id }))
    tipoJornadaOptions.value = (tjRes || []).map(t => ({ label: t.nombre, value: t.id }))
  } catch {
    tipoContratoOptions.value = []
    tipoJornadaOptions.value = []
  }
}

watch(() => props.contratoId, async (id) => {
  if (!id) return
  loading.value = true
  try {
    const res = await getContrato(id)
    contratoActual.value = res.data
    nuevoForm.cargo = res.cargoId || null
    nuevoForm.tipoContrato = res.tipoContratoId || null
    nuevoForm.tipoJornada = res.tipoJornadaId || null
    nuevoForm.sueldoBase = res.sueldoBase || null
    nuevoForm.fechaInicio = ''
    nuevoForm.fechaFin = ''
  } catch {
    contratoActual.value = null
    $q.notify({ type: 'negative', message: 'Error al cargar contrato' })
  } finally {
    loading.value = false
  }
}, { immediate: true })

async function confirmarRenovacion() {
  saving.value = true
  try {
    const payload = {
      empleadoId: contratoActual.value.empleado.id,
      cargoId: nuevoForm.cargo,
      tipoContratoId: nuevoForm.tipoContrato,
      tipoJornadaId: nuevoForm.tipoJornada,
      fechaInicio: nuevoForm.fechaInicio,
      fechaFin: nuevoForm.fechaFin || null,
      estado: 'ACTIVO',
      sueldoBase: parseFloat(nuevoForm.sueldoBase) || 0,
      controlaAsistencia: contratoActual.value.controlaAsistencia,
      contratoRenovadoId: props.contratoId
    }
    await createContrato(payload)
    $q.notify({ type: 'positive', message: 'Contrato renovado correctamente' })
    emit('saved')
    emit('update:modelValue', false)
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.message || 'Error al renovar contrato' })
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  cargarCargos()
  cargarTipos()
})
</script>



<style scoped>
.dialog-card {
  border-radius: 16px;
  max-width: 1000px;
  margin: 0 auto;
}
.dialog-header {
  background: #f9fafb;
  border-radius: 16px 16px 0 0;
}
.dialog-body {
  background: #fafafa;
}
.dialog-actions {
  background: #f9fafb;
  border-radius: 0 0 16px 16px;
}
.contract-compare-card {
  border-radius: 12px;
}
.contract-compare-card.border-primary {
  border-color: #2E7D32 !important;
  border-width: 2px;
}
.comp-row {
  padding: 6px 0;
  font-size: 13px;
  border-bottom: 1px solid #f0f0f0;
}
.comp-label {
  color: #6b7280;
  width: 120px;
}
.comp-value {
  color: #1f2937;
  font-weight: 500;
}
</style>

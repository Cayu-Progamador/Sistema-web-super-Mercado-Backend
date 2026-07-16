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
          <div class="header-left">
            <div class="header-icon">
              <q-icon name="autorenew" size="24px" style="color: #006051" />
            </div>
            <div>
              <div class="text-h5 text-weight-bold" style="color: #004d40">Renovar Contrato</div>
              <div class="text-subtitle2" style="color: #00897b">Crea un nuevo contrato con datos actualizados</div>
            </div>
          </div>
          <q-btn flat round dense icon="close" style="color: #00897b; background: #e0f2f1; border-radius: 8px;" v-close-popup />
        </div>
      </q-card-section>

      <q-separator style="border-color: #e4edd8" />

      <q-card-section class="dialog-body q-pa-lg">
        <template v-if="loading">
          <div class="row q-col-gutter-lg">
            <div class="col-12 col-md-6"><q-skeleton type="rect" height="300px" /></div>
            <div class="col-12 col-md-6"><q-skeleton type="rect" height="300px" /></div>
          </div>
        </template>

        <template v-else-if="contratoActual">
          <q-banner class="q-mb-md rounded-banners" style="background: #e8f5e9; border: 1px solid #a5d6a7; border-radius: 12px;">
            <template v-slot:avatar>
              <q-avatar style="background: #006051; color: white; font-size: 13px; font-weight: 700;">{{ iniciales }}</q-avatar>
            </template>
            <span style="color: #004d40; font-weight: 600;">
              Estás renovando el contrato de <strong style="color: #006051;">{{ nombreEmpleado }}</strong>. Se creará un nuevo contrato con los datos actualizados.
            </span>
          </q-banner>

          <div class="row q-col-gutter-lg">
            <!-- Contrato Actual -->
            <div class="col-12 col-md-6">
              <q-card flat bordered class="contract-card contract-card--old">
                <div class="contract-card-header">
                  <q-icon name="description" size="16px" style="color: #8BC34A" />
                  <span>CONTRATO ACTUAL</span>
                </div>
                <q-card-section class="q-pa-md">
                  <div class="comp-row"><span class="comp-label">N° Contrato</span><span class="comp-value">CTR-{{ String(contratoActual.id).padStart(4, '0') }}</span></div>
                  <div class="comp-row"><span class="comp-label">Empleado</span><span class="comp-value">{{ nombreEmpleado }}</span></div>
                  <div class="comp-row"><span class="comp-label">Cargo</span><span class="comp-value">{{ contratoActual.cargoNombre }}</span></div>
                  <div class="comp-row"><span class="comp-label">Tipo Contrato</span><span class="comp-value">{{ contratoActual.tipoContratoNombre }}</span></div>
                  <div class="comp-row"><span class="comp-label">Jornada</span><span class="comp-value">{{ contratoActual.tipoJornadaNombre }}</span></div>
                  <div class="comp-row"><span class="comp-label">Sueldo Base</span><span class="comp-value sueldo-value">${{ contratoActual.sueldoBase?.toLocaleString() }}</span></div>
                  <div class="comp-row"><span class="comp-label">Inicio</span><span class="comp-value">{{ contratoActual.fechaInicio || '-' }}</span></div>
                  <div class="comp-row"><span class="comp-label">Fin</span><span class="comp-value">{{ contratoActual.fechaFin || 'Indefinido' }}</span></div>
                  <div class="comp-row"><span class="comp-label">Estado</span>
                    <q-chip dense :color="chipColor(contratoActual.estado)" text-color="white" size="11px" class="estado-chip">{{ contratoActual.estado }}</q-chip>
                  </div>
                </q-card-section>
              </q-card>
            </div>

              <!-- Nuevo Contrato -->
            <div class="col-12 col-md-6">
              <q-card flat bordered class="contract-card contract-card--new">
                <div class="contract-card-header contract-card-header--new">
                  <q-icon name="add_circle" size="16px" style="color: #006051" />
                  <span>NUEVO CONTRATO</span>
                </div>
                <q-card-section class="q-pa-md">
                  <div class="info-row">
                    <span class="info-label">Cargo</span>
                    <span class="info-value">{{ contratoActual.cargoNombre }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">Tipo Contrato</span>
                    <span class="info-value">{{ contratoActual.tipoContratoNombre }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">Jornada</span>
                    <span class="info-value">{{ contratoActual.tipoJornadaNombre }}</span>
                  </div>

                  <q-separator class="q-my-sm" style="border-color: #e8e8e8;" />

                  <div class="row q-col-gutter-sm">
                    <div class="col-12">
                      <q-input
                        v-model="nuevoForm.sueldoBase" label="Nuevo Sueldo Base *"
                        outlined dense type="number" step="0.01" min="0" placeholder="0.00"
                        class="q-mb-sm field-custom"
                        :rules="[v => (v ?? 0) > 0 || 'Debe ser mayor a 0']"
                        lazy-rules
                      >
                        <template v-slot:prepend>
                          <span style="color: #006051; font-weight: 700;">Bs.</span>
                        </template>
                      </q-input>
                    </div>
                    <div class="col-6">
                      <q-input
                        v-model="nuevoForm.fechaInicio" label="Nueva Fecha Inicio *"
                        outlined dense type="date"
                        class="q-mb-sm field-custom"
                        :rules="[v => !!v || 'Fecha de inicio requerida']"
                        lazy-rules
                      />
                    </div>
                    <div class="col-6">
                      <q-input
                        v-model="nuevoForm.fechaFin" label="Nueva Fecha Fin"
                        outlined dense type="date"
                        class="q-mb-sm field-custom"
                        :hint="contratoActual.tipoContratoNombre?.toLowerCase() === 'indefinido' ? 'Contrato indefinido' : ''"
                        :rules="contratoActual.tipoContratoNombre?.toLowerCase() === 'indefinido' ? [] : [v => !v || v > nuevoForm.fechaInicio || 'Debe ser posterior al inicio']"
                        lazy-rules
                      />
                    </div>
                    <div class="col-12">
                      <q-input
                        v-model="nuevoForm.observaciones" label="Observaciones"
                        outlined dense type="textarea" rows="2"
                        class="q-mb-sm field-custom"
                        maxlength="500"
                        :counter="true"
                      />
                    </div>
                  </div>
                </q-card-section>
              </q-card>
            </div>
          </div>
        </template>
      </q-card-section>

      <q-separator style="border-color: #e4edd8" />

      <q-card-actions align="right" class="q-pa-md dialog-actions">
        <q-btn flat label="Cancelar" v-close-popup no-caps style="color: #5a5a5a; font-weight: 700; padding: 8px 20px; border-radius: 9px;" />
        <q-btn
          unelevated label="Confirmar Renovación"
          @click="confirmarRenovacion" no-caps
          :loading="saving" :disable="saving"
          style="background: #006051; color: white; font-weight: 800; padding: 8px 24px; border-radius: 9px; box-shadow: 0 4px 14px rgba(0,96,81,0.3);"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { useQuasar } from 'quasar'
import { getContrato, renovarContrato } from '../../api/contrato/contrato'

const props = defineProps({ modelValue: Boolean, contratoId: Number })
const emit = defineEmits(['update:modelValue', 'saved'])

const $q = useQuasar()
const saving = ref(false)
const loading = ref(false)
const contratoActual = ref(null)

const nuevoForm = reactive({
  sueldoBase: null,
  fechaInicio: '',
  fechaFin: '',
  observaciones: ''
})

const nombreEmpleado = computed(() => {
  if (!contratoActual.value?.empleado) return ''
  return `${contratoActual.value.empleado.nombres || ''} ${contratoActual.value.empleado.apellidos || ''}`.trim()
})

const iniciales = computed(() => {
  const n = nombreEmpleado.value
  if (!n) return 'E'
  return n.split(' ').slice(0, 2).map(p => p[0]).join('').toUpperCase()
})

function chipColor(estado) {
  return { ACTIVO: 'green-7', VENCIDO: 'red-6', FINALIZADO: 'grey-6', SUSPENDIDO: 'orange-7' }[estado] || 'grey-5'
}

watch(() => props.contratoId, async (id) => {
  if (!id) return
  loading.value = true
  try {
    const res = await getContrato(id)
    contratoActual.value = res
    nuevoForm.sueldoBase = res.sueldoBase || null
    const hoy = new Date().toISOString().split('T')[0]
    nuevoForm.fechaInicio = hoy
    nuevoForm.fechaFin = ''
    nuevoForm.observaciones = ''
  } catch {
    contratoActual.value = null
    $q.notify({ type: 'negative', message: 'Error al cargar contrato' })
  } finally {
    loading.value = false
  }
}, { immediate: true })

async function confirmarRenovacion() {
  if (!nuevoForm.fechaInicio) {
    $q.notify({ type: 'warning', message: 'La fecha de inicio es obligatoria' })
    return
  }
  if ((nuevoForm.sueldoBase ?? 0) <= 0) {
    $q.notify({ type: 'warning', message: 'El sueldo base debe ser mayor a 0' })
    return
  }
  if (nuevoForm.fechaFin && nuevoForm.fechaFin <= nuevoForm.fechaInicio) {
    $q.notify({ type: 'warning', message: 'La fecha de fin debe ser posterior a la fecha de inicio' })
    return
  }

  saving.value = true
  try {
    const payload = {
      fechaInicio: nuevoForm.fechaInicio,
      fechaFin: nuevoForm.fechaFin || null,
      sueldoBase: parseFloat(nuevoForm.sueldoBase) || 0,
      observaciones: nuevoForm.observaciones || null
    }
    await renovarContrato(props.contratoId, payload)
    $q.notify({ type: 'positive', message: 'Contrato renovado correctamente' })
    emit('saved')
    emit('update:modelValue', false)
  } catch (error) {
    const msg = error.response?.data?.message || error.response?.data?.detail || 'Error al renovar contrato'
    $q.notify({ type: 'negative', message: msg })
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.dialog-card {
  border-radius: 16px;
  max-width: 1000px;
  margin: 0 auto;
}
.dialog-header {
  background: #f0f7e8;
  border-radius: 16px 16px 0 0;
}
.header-left {
  display: flex; align-items: center; gap: 14px;
}
.header-icon {
  width: 46px; height: 46px; border-radius: 12px;
  background: #e0f2f1; border: 1.5px solid #80cbc4;
  display: flex; align-items: center; justify-content: center;
}
.dialog-body {
  background: #fafafa;
}
.dialog-actions {
  background: #f9fafb;
  border-radius: 0 0 16px 16px;
}
.rounded-banners {
  border-radius: 12px;
}
.contract-card {
  border-radius: 12px;
  height: 100%;
}
.contract-card--old {
  border-color: #e4edd8;
}
.contract-card--new {
  border-color: #006051;
  border-width: 2px;
}
.contract-card-header {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 16px; font-size: 12px; font-weight: 800;
  letter-spacing: 0.5px; border-radius: 11px 11px 0 0;
}
.contract-card--old .contract-card-header {
  background: #f0f7e8; color: #558b2f; border-bottom: 1px solid #e4edd8;
}
.contract-card-header--new {
  background: #e0f2f1; color: #006051; border-bottom: 1px solid #80cbc4;
}
.comp-row {
  padding: 6px 0; font-size: 13px;
  border-bottom: 1px solid #f0f0f0;
  display: flex; align-items: center; gap: 8px;
}
.comp-label {
  color: #6b7280; width: 110px; flex-shrink: 0; font-size: 12px;
}
.comp-value {
  color: #1f2937; font-weight: 600; font-size: 13px;
}
.sueldo-value {
  color: #006051;
}
.estado-chip {
  font-weight: 700; border-radius: 5px; padding: 0 8px;
}
.info-row {
  padding: 5px 0; font-size: 13px;
  display: flex; align-items: center; gap: 8px;
}
.info-label {
  color: #6b7280; width: 110px; flex-shrink: 0; font-size: 12px;
}
.info-value {
  color: #1f2937; font-weight: 600; font-size: 13px;
}
.field-custom :deep(.q-field__control) {
  border-radius: 8px;
}
.field-custom :deep(.q-field__label) {
  font-weight: 600;
  font-size: 12px;
}

</style>

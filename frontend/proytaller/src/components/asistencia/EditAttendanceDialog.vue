<template>
  <q-dialog v-model="visible" persistent max-width="500px">
    <q-card class="modal-card">
      <div class="accent-bar"></div>

      <div class="modal-header">
        <div class="header-left">
          <div class="modal-icon">
            <q-icon name="edit" size="22px" color="#4a8c25" />
          </div>
          <div>
            <div class="modal-eyebrow">{{ esCreacion ? 'NUEVO REGISTRO' : 'EDITAR REGISTRO' }}</div>
            <div class="modal-title">{{ esCreacion ? 'Registrar Asistencia' : 'Editar Asistencia' }}</div>
          </div>
        </div>
        <q-btn icon="close" flat round dense class="close-btn" @click="cerrar" />
      </div>

      <q-card-section class="q-px-lg q-pt-md q-pb-lg form-scroll">
        <div class="text-center q-mb-md">
          <div class="text-weight-bold" style="font-size:1rem; color:#2a5c1a">{{ empleado?.nombreEmpleado || '' }}</div>
          <div style="font-size:0.85rem; color:#666">{{ empleado?.codigoEmpleado || '' }}</div>
          <div style="font-size:0.85rem; color:#666">{{ fechaFormateada }}</div>
        </div>

        <div v-if="esPermiso" class="permiso-banner q-mb-md">
          <q-icon name="event_available" size="18px" />
          <span>Este día tiene un <strong>permiso aprobado</strong>. El registro está bloqueado y no puede editarse.</span>
        </div>

        <q-form @submit.prevent="guardar">
          <div class="row q-col-gutter-md">
            <div class="col-12 q-mb-sm">
              <div class="turno-info">
                <q-icon name="badge" size="16px" color="#4a8c25" />
                <span>Turno: <strong>{{ empleado?.turnoNombre || '--' }}</strong></span>
                <span class="q-ml-md">{{ empleado?.horaEntradaEsperada ? empleado.horaEntradaEsperada.substring(0,5) : '--' }} → {{ empleado?.horaSalidaEsperada ? empleado.horaSalidaEsperada.substring(0,5) : '--' }}</span>
              </div>
            </div>
            <div class="col-12">
              <div class="section-divider">
                <q-icon name="schedule" size="18px" color="#4a8c25" />
                <span>Horarios</span>
              </div>
            </div>
            <div class="col-12 col-md-6">
              <div class="field-group q-mb-md">
                <div class="field-lbl">Hora Entrada</div>
                <q-input v-model="horaEntrada" outlined dense class="field-input" mask="time" :rules="['time']" placeholder="HH:MM" :disable="esPermiso">
                  <template v-slot:append>
                    <q-icon name="access_time" class="cursor-pointer" color="#4a8c25">
                      <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                        <q-time v-model="horaEntrada" format24h color="positive" text-color="white" />
                      </q-popup-proxy>
                    </q-icon>
                  </template>
                </q-input>
              </div>
            </div>
            <div class="col-12 col-md-6">
              <div class="field-group q-mb-md">
                <div class="field-lbl">Hora Salida</div>
                <q-input v-model="horaSalida" outlined dense class="field-input" mask="time" :rules="['time']" placeholder="HH:MM" :disable="esPermiso">
                  <template v-slot:append>
                    <q-icon name="access_time" class="cursor-pointer" color="#4a8c25">
                      <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                        <q-time v-model="horaSalida" format24h color="positive" text-color="white" />
                      </q-popup-proxy>
                    </q-icon>
                  </template>
                </q-input>
              </div>
            </div>

            <div class="col-12">
              <div class="section-divider">
                <q-icon name="info" size="18px" color="#4a8c25" />
                <span>Estado</span>
              </div>
            </div>
            <div class="col-12">
              <div class="field-group q-mb-md">
                <div class="field-lbl">Estado</div>
                <q-select
                  v-model="estado"
                  :options="opcionesEstado"
                  outlined dense
                  class="field-input"
                  emit-value map-options
                  :disable="esPermiso"
                />
              </div>
            </div>
          </div>

          <div class="row justify-end q-gutter-sm q-mt-md">
            <q-btn flat label="Cancelar" class="btn-cancel" @click="cerrar" />
            <q-btn
              :label="esCreacion ? 'Registrar' : 'Guardar'"
              type="submit"
              :loading="guardando"
              :disable="esPermiso"
              icon="save"
              unelevated
              class="btn-save"
            />
          </div>
        </q-form>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  empleado: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'guardar'])

const visible = ref(false)
const guardando = ref(false)

const horaEntrada = ref(null)
const horaSalida = ref(null)
const estado = ref(null)

const opcionesEstado = [
  { label: 'A tiempo', value: 'COMPLETO' },
  { label: 'Tardanza', value: 'TARDANZA' },
  { label: 'Falta', value: 'FALTA' },
  { label: 'Justificado', value: 'JUSTIFICADO' },
  { label: 'Pendiente salida', value: 'PRESENTE' }
]

const fechaFormateada = ref('')

const esPermiso = computed(() => props.empleado?.estado === 'PERMISO')

const esCreacion = computed(() => !props.empleado?.idAsistencia)

function toHHmm(val) {
  if (!val) return null
  return val.substring(0, 5)
}

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.empleado) {
    horaEntrada.value = toHHmm(props.empleado.horaEntrada) || (esCreacion.value ? toHHmm(props.empleado.horaEntradaEsperada) : null)
    horaSalida.value = toHHmm(props.empleado.horaSalida) || (esCreacion.value ? toHHmm(props.empleado.horaSalidaEsperada) : null)
    estado.value = props.empleado.estado || (esCreacion.value ? 'FALTA' : null)
    if (props.empleado.fecha) {
      const [y, m, d] = props.empleado.fecha.split('-')
      fechaFormateada.value = `${d}/${m}/${y}`
    } else {
      fechaFormateada.value = ''
    }
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

function cerrar() {
  visible.value = false
}

function guardar() {
  emit('guardar', {
    idAsistencia: props.empleado?.idAsistencia,
    horaEntrada: horaEntrada.value,
    horaSalida: horaSalida.value,
    estado: estado.value
  })
}
</script>

<style scoped>
.modal-card {
  width: 100%;
  max-width: 500px;
  border-radius: 20px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e4edd8;
  box-shadow: 0 20px 60px rgba(42,92,26,0.15);
  font-family: 'Nunito', sans-serif;
}
.form-scroll {
  max-height: 65vh;
  overflow-y: auto;
}
.permiso-banner {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  color: #1565c0;
  background: #e3f2fd;
  border: 1px solid #90caf9;
  border-radius: 10px;
  padding: 10px 14px;
}
.section-divider {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 800;
  color: #2a5c1a;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  padding-bottom: 4px;
  margin-top: 4px;
  border-bottom: 1.5px solid #c8e0a0;
}
.accent-bar {
  height: 3px;
  background: linear-gradient(90deg, #82bd43, #4a8c25, #64992b);
}
.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  background: #f0f7e8;
  border-bottom: 1px solid #c8e0a0;
  padding: 16px 20px 14px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.modal-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #eaf4d8;
  border: 1.5px solid #82bd43;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.modal-eyebrow {
  font-size: 11px;
  font-weight: 600;
  color: #7aaa4e;
}
.modal-title {
  font-size: 16px;
  font-weight: 900;
  color: #2a5c1a;
}
.close-btn {
  color: #7aaa4e !important;
  background: #f0f7e8 !important;
  border-radius: 8px !important;
}
.close-btn:hover { background: #ddecc5 !important; color: #4a8c25 !important; }
.turno-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #2a5c1a;
  background: #f0f7e8;
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid #c8e0a0;
}
.field-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.field-lbl {
  font-size: 11px;
  font-weight: 800;
  color: #5a8040;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}
.field-input :deep(.q-field__control) {
  border-radius: 10px;
  background: #f7f9f4;
  border: 1px solid #e4edd8;
}
.field-input :deep(.q-field__control:focus-within) {
  border-color: #82bd43;
  box-shadow: 0 0 0 3px rgba(130,189,67,0.15);
}
.btn-cancel {
  background: #fff;
  color: #5a5a5a;
  border: 1.5px solid #d0d0d0;
  border-radius: 9px;
  font-weight: 700;
  font-size: 13px;
}
.btn-cancel:hover { background: #f7f7f7; border-color: #bbb; }
.btn-save {
  border-radius: 9px;
  font-weight: 800;
  font-size: 13px;
  box-shadow: 0 4px 14px rgba(74,140,37,0.3);
  background: #82bd43;
  color: #fff;
}
</style>

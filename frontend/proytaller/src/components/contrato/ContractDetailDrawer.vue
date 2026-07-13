<template>
  <q-drawer
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    side="right"
    overlay
    elevated
    :width="420"
    :breakpoint="500"
    class="detail-drawer"
  >
    <q-scroll-area class="fit">
      <div class="q-pa-md">
        <div class="row items-center justify-between q-mb-md">
          <div class="text-h6 text-weight-bold">Detalle del Contrato</div>
          <q-btn flat round dense icon="close" v-close-popup />
        </div>

        <template v-if="loading">
          <q-skeleton type="rect" height="200px" class="q-mb-md" />
          <q-skeleton type="rect" height="100px" class="q-mb-md" />
        </template>

        <template v-else-if="contrato">
          <!-- Empleado -->
          <q-card flat bordered class="info-card q-mb-md">
            <q-card-section class="q-pa-md">
              <div class="row items-center q-mb-sm">
                <q-avatar size="48px" class="q-mr-md">
                  <img :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(empleadoNombre)}&background=2E7D32&color=fff&size=48`" />
                </q-avatar>
                <div>
                  <div class="text-subtitle1 text-weight-bold">{{ empleadoNombre }}</div>
                  <div class="text-caption text-grey-6">CI: {{ contrato.empleado?.cedula || '-' }}</div>
                </div>
              </div>
              <q-separator class="q-my-sm" />
              <div class="row">
                <div class="col-6 text-caption text-grey-6">Email:</div>
                <div class="col-6 text-body2">{{ contrato.empleado?.email || '-' }}</div>
              </div>
              <div class="row">
                <div class="col-6 text-caption text-grey-6">Teléfono:</div>
                <div class="col-6 text-body2">{{ contrato.empleado?.telefono || '-' }}</div>
              </div>
              <div class="row">
                <div class="col-6 text-caption text-grey-6">Dirección:</div>
                <div class="col-6 text-body2">{{ contrato.empleado?.direccion || '-' }}</div>
              </div>
            </q-card-section>
          </q-card>

          <!-- Información del contrato -->
          <q-card flat bordered class="info-card q-mb-md">
            <q-card-section class="q-pa-md">
              <div class="text-subtitle2 text-weight-bold q-mb-sm">Información del Contrato</div>
              <div class="row info-row"><span class="info-label">N° Contrato</span><span class="info-value">CTR-{{ String(contrato.id).padStart(4, '0') }}</span></div>
              <div class="row info-row"><span class="info-label">Cargo</span><span class="info-value">{{ contrato.cargoNombre || '-' }}</span></div>
              <div class="row info-row"><span class="info-label">Tipo Contrato</span><span class="info-value">{{ contrato.tipoContratoNombre || '-' }}</span></div>
              <div class="row info-row"><span class="info-label">Tipo Jornada</span><span class="info-value">{{ contrato.tipoJornadaNombre || '-' }}</span></div>
              <div class="row info-row"><span class="info-label">Estado</span>
                <q-chip dense :color="chipColor(contrato.estado)" text-color="white" size="12px">{{ contrato.estado }}</q-chip>
              </div>
              <div class="row info-row"><span class="info-label">Sueldo Base</span><span class="info-value">${{ contrato.sueldoBase?.toLocaleString() || '0' }}</span></div>
            </q-card-section>
          </q-card>

          <!-- Horario -->
          <q-card flat bordered class="info-card q-mb-md">
            <q-card-section class="q-pa-md">
              <div class="text-subtitle2 text-weight-bold q-mb-sm">Horario</div>
              <div class="row info-row"><span class="info-label">Horas/Día</span><span class="info-value">{{ contrato.horasDia || 0 }} h</span></div>
              <div class="row info-row"><span class="info-label">Horas/Semana</span><span class="info-value">{{ contrato.horasSemana || 0 }} h</span></div>
              <div class="q-mt-sm">
                <q-badge v-for="d in diasSemana" :key="d" :color="contrato.turnos?.some(t => t.dia === d) ? 'green-7' : 'grey-3'" class="q-mr-xs" :class="{ 'text-grey-6': !contrato.turnos?.some(t => t.dia === d) }">{{ d }}</q-badge>
              </div>
            </q-card-section>
          </q-card>

          <!-- Asistencia -->
          <q-card flat bordered class="info-card q-mb-md">
            <q-card-section class="q-pa-md">
              <div class="text-subtitle2 text-weight-bold q-mb-sm">Control de Asistencia</div>
              <div class="row items-center">
                <q-icon :name="contrato.controlaAsistencia ? 'check_circle' : 'cancel'" :color="contrato.controlaAsistencia ? 'green' : 'grey-4'" size="24px" class="q-mr-sm" />
                <span :class="contrato.controlaAsistencia ? 'text-green' : 'text-grey-6'">{{ contrato.controlaAsistencia ? 'Controla asistencia' : 'No controla asistencia' }}</span>
              </div>
            </q-card-section>
          </q-card>

          <!-- Documentos -->
          <q-card flat bordered class="info-card q-mb-md">
            <q-card-section class="q-pa-md">
              <div class="text-subtitle2 text-weight-bold q-mb-sm">Documentos</div>
              <div class="text-caption text-grey-6">Sin documentos adjuntos</div>
            </q-card-section>
          </q-card>

          <!-- Auditoría -->
          <q-card flat bordered class="info-card">
            <q-card-section class="q-pa-md">
              <div class="text-subtitle2 text-weight-bold q-mb-sm">Auditoría</div>
              <div class="row info-row"><span class="info-label">Creado por</span><span class="info-value">Admin</span></div>
              <div class="row info-row"><span class="info-label">Creado en</span><span class="info-value">{{ contrato.fechaInicio || '-' }}</span></div>
            </q-card-section>
          </q-card>
        </template>

        <template v-else>
          <div class="text-center q-pa-lg text-grey-6">Seleccione un contrato</div>
        </template>
      </div>
    </q-scroll-area>
  </q-drawer>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { getContrato } from '../../api/contrato/contrato'

const props = defineProps({ modelValue: Boolean, contratoId: Number })
const emit = defineEmits(['update:modelValue'])

const contrato = ref(null)
const loading = ref(false)
const diasSemana = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom']

const empleadoNombre = computed(() => {
  if (!contrato.value?.empleado) return '-'
  return `${contrato.value.empleado.nombres || ''} ${contrato.value.empleado.apellidos || ''}`.trim()
})

function chipColor(estado) {
  return { ACTIVO: 'green-7', VENCIDO: 'red-6', FINALIZADO: 'grey-6', SUSPENDIDO: 'orange-7' }[estado] || 'grey-5'
}

watch(() => props.contratoId, async (id) => {
  if (!id) return
  loading.value = true
  try {
    const res = await getContrato(id)
    contrato.value = res.data
  } catch {
    contrato.value = null
  } finally {
    loading.value = false
  }
}, { immediate: true })
</script>



<style scoped>
.detail-drawer {
  border-radius: 16px 0 0 16px;
  background: #f5f7fa;
}
.info-card {
  border-radius: 12px;
  background: white;
}
.info-row {
  padding: 4px 0;
  font-size: 13px;
}
.info-label {
  color: #6b7280;
  width: 120px;
  flex-shrink: 0;
}
.info-value {
  color: #1f2937;
  font-weight: 500;
}
</style>

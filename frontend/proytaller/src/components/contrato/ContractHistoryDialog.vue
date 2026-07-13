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
          <div class="text-h5 text-weight-bold">Historial de Contratos</div>
          <q-btn flat round dense icon="close" v-close-popup />
        </div>
      </q-card-section>

      <q-separator />

      <q-card-section class="dialog-body q-pa-lg">
        <template v-if="loading">
          <div class="q-pa-md">
            <q-skeleton type="rect" height="80px" class="q-mb-md" />
            <q-skeleton type="rect" height="80px" class="q-mb-md" />
            <q-skeleton type="rect" height="80px" />
          </div>
        </template>

        <template v-else-if="historial.length === 0">
          <div class="text-center q-pa-lg text-grey-6">
            <q-icon name="history" size="48px" color="grey-4" />
            <div class="q-mt-sm">No hay historial disponible</div>
          </div>
        </template>

        <template v-else>
          <q-timeline color="primary" class="history-timeline">
            <q-timeline-entry
              v-for="(entry, index) in historial"
              :key="index"
              :title="entry.titulo"
              :subtitle="entry.fecha"
              :icon="entry.icon"
              :color="entry.color"
              side="left"
            >
              <div>
                <q-card flat bordered class="timeline-card q-mb-sm">
                  <q-card-section class="q-pa-md">
                    <div class="row q-col-gutter-md">
                      <div class="col-12 col-md-6">
                        <div class="tl-row"><span class="tl-label">Cargo</span><span class="tl-value">{{ entry.cargo || '-' }}</span></div>
                        <div class="tl-row"><span class="tl-label">Tipo Contrato</span><span class="tl-value">{{ entry.tipoContrato || '-' }}</span></div>
                        <div class="tl-row"><span class="tl-label">Tipo Jornada</span><span class="tl-value">{{ entry.tipoJornada || '-' }}</span></div>
                      </div>
                      <div class="col-12 col-md-6">
                        <div class="tl-row"><span class="tl-label">Sueldo Base</span><span class="tl-value">${{ entry.sueldoBase?.toLocaleString() || '0' }}</span></div>
                        <div class="tl-row"><span class="tl-label">Vigencia</span><span class="tl-value">{{ entry.fechaInicio || '-' }} → {{ entry.fechaFin || 'Actual' }}</span></div>
                        <div class="tl-row"><span class="tl-label">Estado</span>
                          <q-chip dense :color="chipColor(entry.estado)" text-color="white" size="12px">{{ entry.estado }}</q-chip>
                        </div>
                      </div>
                    </div>
                    <q-separator class="q-my-sm" />
                    <div class="text-caption text-grey-6 text-right">Registrado por: {{ entry.usuario || 'Admin' }}</div>
                  </q-card-section>
                </q-card>
              </div>
            </q-timeline-entry>
          </q-timeline>
        </template>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right" class="q-pa-md dialog-actions">
        <q-btn flat label="Cerrar" color="grey-7" v-close-popup no-caps />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useQuasar } from 'quasar'
import { getContrato, listarContratos } from '../../api/contrato/contrato'

const props = defineProps({ modelValue: Boolean, contratoId: Number })
const emit = defineEmits(['update:modelValue'])

const $q = useQuasar()
const loading = ref(false)
const historial = ref([])

function chipColor(estado) {
  return { ACTIVO: 'green-7', VENCIDO: 'red-6', FINALIZADO: 'grey-6', SUSPENDIDO: 'orange-7' }[estado] || 'grey-5'
}

watch(() => props.contratoId, async (id) => {
  if (!id) return
  loading.value = true
  try {
    const res = await getContrato(id)
    const c = res.data
    historial.value = [{
      titulo: `Contrato CTR-${String(id).padStart(4, '0')}`,
      fecha: c.fechaInicio || 'Fecha desconocida',
      icon: 'contract',
      color: 'primary',
      cargo: c.cargoNombre,
      tipoContrato: c.tipoContratoNombre,
      tipoJornada: c.tipoJornadaNombre,
      sueldoBase: c.sueldoBase,
      fechaInicio: c.fechaInicio,
      fechaFin: c.fechaFin,
      estado: c.estado,
      usuario: 'Admin'
    }]

    try {
      const resRel = await listarContratos({ empleadoId: c.empleado?.id, sort: 'fechaInicio,desc' })
      const relacionados = resRel?.content || []
      relacionados.forEach(r => {
        if (r.id !== id) {
          historial.value.push({
            titulo: `Contrato CTR-${String(r.id).padStart(4, '0')}`,
            fecha: r.fechaInicio || '',
            icon: 'description',
            color: 'blue-grey',
            cargo: r.cargoNombre,
            tipoContrato: r.tipoContratoNombre,
            tipoJornada: r.tipoJornadaNombre,
            sueldoBase: r.sueldoBase,
            fechaInicio: r.fechaInicio,
            fechaFin: r.fechaFin,
            estado: r.estado,
            usuario: 'Admin'
          })
        }
      })
    } catch {}

    historial.value.sort((a, b) => new Date(b.fecha) - new Date(a.fecha))
  } catch {
    historial.value = []
    $q.notify({ type: 'negative', message: 'Error al cargar historial' })
  } finally {
    loading.value = false
  }
}, { immediate: true })
</script>

<style scoped>
.dialog-card {
  border-radius: 16px;
  max-width: 900px;
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
.timeline-card {
  border-radius: 10px;
}
.tl-row {
  padding: 3px 0;
  font-size: 13px;
}
.tl-label {
  color: #6b7280;
  width: 110px;
  display: inline-block;
}
.tl-value {
  color: #1f2937;
  font-weight: 500;
}
.history-timeline :deep(.q-timeline__title) {
  font-weight: 600;
  font-size: 15px;
}
.history-timeline :deep(.q-timeline__subtitle) {
  font-size: 12px;
}
</style>

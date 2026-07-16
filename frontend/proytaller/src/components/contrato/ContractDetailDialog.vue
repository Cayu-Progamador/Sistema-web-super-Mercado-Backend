<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card" style="max-height: 85vh; display: flex; flex-direction: column;">

      <!-- Header -->
      <div class="dialog-header">
        <div class="header-left">
          <div class="view-icon">
            <q-icon name="description" size="20px" style="color:#006051" />
          </div>
          <div>
            <div class="dialog-title">Detalle del Contrato</div>
            <div class="dialog-sub">Información completa del contrato laboral</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-container">
        <q-spinner color="#006051" size="40px" />
        <span class="loading-text">Cargando detalles...</span>
      </div>

      <!-- Body -->
      <q-card-section v-else-if="contrato" class="dialog-body">

        <!-- 1. Cabecera - Empleado -->
        <div class="profile-section">
          <div class="profile-avatar">
            <q-avatar size="56px">
              <img :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(empleadoNombre)}&background=006051&color=fff&size=56`" />
            </q-avatar>
          </div>
          <div class="profile-info">
            <div class="profile-name">{{ empleadoNombre }}</div>
            <div class="profile-meta">CI: {{ contrato.empleado?.cedula || '-' }}</div>
          </div>
        </div>

        <div class="info-grid-3">
          <div class="info-item">
            <q-icon name="email" size="16px" color="#006051" />
            <div class="info-content">
              <div class="info-label">Email</div>
              <div class="info-value">{{ contrato.empleado?.email || '-' }}</div>
            </div>
          </div>
          <div class="info-item">
            <q-icon name="phone" size="16px" color="#006051" />
            <div class="info-content">
              <div class="info-label">Teléfono</div>
              <div class="info-value">{{ contrato.empleado?.telefono || '-' }}</div>
            </div>
          </div>
          <div class="info-item full-width">
            <q-icon name="location_on" size="16px" color="#006051" />
            <div class="info-content">
              <div class="info-label">Dirección</div>
              <div class="info-value">{{ contrato.empleado?.direccion || '-' }}</div>
            </div>
          </div>
        </div>

        <!-- 2. Información del Contrato -->
        <div class="section-divider">
          <div class="section-title">
            <q-icon name="badge" size="16px" color="#F57C00" />
            Información del Contrato
          </div>
        </div>

        <div class="info-grid-2">
          <div class="info-item">
            <div class="info-content">
              <div class="info-label">N° Contrato</div>
              <div class="info-value">{{ contrato.nroContrato }}</div>
            </div>
          </div>
          <div class="info-item">
            <div class="info-content">
              <div class="info-label">Cargo</div>
              <div class="info-value">{{ contrato.cargoNombre || '-' }}</div>
            </div>
          </div>
          <div class="info-item">
            <div class="info-content">
              <div class="info-label">Tipo Contrato</div>
              <div class="info-value">{{ contrato.tipoContratoNombre || '-' }}</div>
            </div>
          </div>
          <div class="info-item">
            <div class="info-content">
              <div class="info-label">Tipo Jornada</div>
              <div class="info-value">{{ contrato.tipoJornadaNombre || '-' }}</div>
            </div>
          </div>
          <div class="info-item">
            <div class="info-content">
              <div class="info-label">Estado</div>
              <div class="info-value">
                <q-chip dense :color="chipColor(contrato.estado)" text-color="white" size="12px" class="estado-chip">{{ contrato.estado }}</q-chip>
              </div>
            </div>
          </div>
          <div class="info-item">
            <div class="info-content">
              <div class="info-label">Sueldo Base</div>
              <div class="info-value text-weight-bold" style="color:#006051">${{ formatNumber(contrato.sueldoBase) }}</div>
            </div>
          </div>
          <div class="info-item">
            <div class="info-content">
              <div class="info-label">Fecha Inicio</div>
              <div class="info-value">{{ formatFecha(contrato.fechaInicio) }}</div>
            </div>
          </div>
          <div class="info-item">
            <div class="info-content">
              <div class="info-label">Fecha Fin</div>
              <div class="info-value">{{ formatFecha(contrato.fechaFin) || 'Indefinido' }}</div>
            </div>
          </div>
          <div class="info-item" v-if="contrato.motivoFin">
            <div class="info-content">
              <div class="info-label">Motivo Fin</div>
              <div class="info-value">{{ contrato.motivoFin }}</div>
            </div>
          </div>
          <div class="info-item full-width" v-if="contrato.observaciones">
            <div class="info-content">
              <div class="info-label">Observaciones</div>
              <div class="info-value">{{ contrato.observaciones }}</div>
            </div>
          </div>
        </div>

        <!-- 3. Turno y Horario -->
        <div class="section-divider">
          <div class="section-title">
            <q-icon name="schedule" size="16px" color="#F57C00" />
            Turno y Horario
          </div>
        </div>

        <div class="info-grid-2">
          <div class="info-item">
            <div class="info-content">
              <div class="info-label">Nombre Turno</div>
              <div class="info-value">{{ contrato.turnoNombre || '-' }}</div>
            </div>
          </div>
          <div class="info-item" v-if="contrato.turnoNombre">
            <div class="info-content">
              <div class="info-label">Horario</div>
              <div class="info-value">{{ contrato.horaEntrada || '—' }} - {{ contrato.horaSalida || '—' }}</div>
            </div>
          </div>
        </div>

        <div class="dias-section" v-if="contrato.turnoNombre">
          <div class="dias-label">Días laborables</div>
          <div class="dias-grid">
            <div v-for="d in diasSemana" :key="d.nombre" class="dia-item" :class="{ activo: d.activo }">
              <span class="dia-nombre">{{ d.nombre }}</span>
              <q-icon :name="d.activo ? 'check_circle' : 'cancel'" :color="d.activo ? '#006051' : '#d1d5db'" size="16px" />
            </div>
          </div>
        </div>

        <!-- 4. Control de Asistencia -->
        <div class="section-divider">
          <div class="section-title">
            <q-icon name="fingerprint" size="16px" color="#F57C00" />
            Control de Asistencia
          </div>
        </div>

        <div class="asistencia-card">
          <q-icon :name="contrato.controlaAsistencia ? 'check_circle' : 'cancel'" :color="contrato.controlaAsistencia ? '#006051' : '#d1d5db'" size="28px" />
          <div class="asistencia-info">
            <div class="asistencia-title">{{ contrato.controlaAsistencia ? 'Controla asistencia' : 'No controla asistencia' }}</div>
            <div class="asistencia-detail" v-if="contrato.controlaAsistencia">
              {{ contrato.horasDia || 0 }}h/día · {{ contrato.horasSemana || 0 }}h/semana · Tolerancia: {{ contrato.toleranciaMinutos || 0 }} min
            </div>
          </div>
        </div>

        <!-- 5. Tipo Pago -->
        <div class="section-divider">
          <div class="section-title">
            <q-icon name="payments" size="16px" color="#F57C00" />
            Tipo de Pago
          </div>
        </div>

        <div class="pago-card">
          <q-icon :name="tipoPagoIcon" size="24px" color="#F57C00" />
          <span class="pago-text">{{ contrato.tipoPagoNombre || 'No asignado' }}</span>
        </div>

      </q-card-section>

      <!-- Empty state -->
      <q-card-section v-else class="dialog-body">
        <div class="empty-state">
          <q-icon name="search_off" size="48px" color="#bce9e2" />
          <div class="empty-text">Seleccione un contrato para ver su detalle</div>
        </div>
      </q-card-section>

      <!-- Footer -->
      <div class="dialog-footer">
        <div class="footer-left">
          <div class="footer-icon">
            <q-icon name="verified" size="16px" color="white" />
          </div>
          <span class="footer-text">Datos del contrato</span>
        </div>
        <div class="footer-actions">
          <q-btn class="btn-pdf-footer" unelevated no-caps @click="exportarPDF">
            <q-icon name="picture_as_pdf" size="16px" class="q-mr-xs" />
            PDF
          </q-btn>
          <q-btn class="btn-cerrar" unelevated no-caps @click="cerrar">
            <q-icon name="close" size="16px" class="q-mr-xs" />
            Cerrar
          </q-btn>
        </div>
      </div>

    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { getDetalleContrato, descargarPdfContrato } from '../../api/contrato/contrato'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  contratoId: { type: [Number, String], default: null }
})

const emit = defineEmits(['update:modelValue'])

const abierto = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const contrato = ref(null)
const loading = ref(false)

const empleadoNombre = computed(() => {
  if (!contrato.value?.empleado) return '-'
  return `${contrato.value.empleado.nombres || ''} ${contrato.value.empleado.apellidos || ''}`.trim()
})

const diasSemana = computed(() => {
  const c = contrato.value
  if (!c) return []
  return [
    { nombre: 'Lun', activo: c.lunes },
    { nombre: 'Mar', activo: c.martes },
    { nombre: 'Mié', activo: c.miercoles },
    { nombre: 'Jue', activo: c.jueves },
    { nombre: 'Vie', activo: c.viernes },
    { nombre: 'Sáb', activo: c.sabado },
    { nombre: 'Dom', activo: c.domingo }
  ]
})

const tipoPagoIcon = computed(() => {
  const pago = (contrato.value?.tipoPagoNombre || '').toLowerCase()
  if (pago.includes('quincenal')) return 'calendar_view_week'
  if (pago.includes('mensual')) return 'calendar_month'
  return 'payments'
})

function chipColor(estado) {
  return { ACTIVO: '#006051', VENCIDO: '#C10015', FINALIZADO: '#6b7280', SUSPENDIDO: '#F57C00' }[estado] || '#9ca3af'
}

function formatNumber(val) {
  if (val == null) return '0.00'
  return Number(val).toLocaleString('es-BO', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

watch(() => props.contratoId, async (id) => {
  if (!id) return
  loading.value = true
  try {
    contrato.value = await getDetalleContrato(id)
  } catch {
    contrato.value = null
  } finally {
    loading.value = false
  }
}, { immediate: true })

async function exportarPDF() {
  try {
    const blob = await descargarPdfContrato(props.contratoId)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `contrato_${props.contratoId}.pdf`
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (err) {
    console.error('Error al exportar PDF:', err)
  }
}

function cerrar() {
  abierto.value = false
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

.dialog-card {
  width: 100% !important;
  max-width: 680px !important;
  border-radius: 20px !important;
  overflow: hidden !important;
  background: #ffffff !important;
  border: 1px solid #bce9e2 !important;
  box-shadow: 0 20px 60px rgba(0,96,81,0.15) !important;
  font-family: 'Nunito', sans-serif;
  display: flex;
  flex-direction: column;
  max-height: 85vh;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 60px 40px;
}
.loading-text {
  font-size: 14px;
  font-weight: 700;
  color: #006051;
}

.dialog-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  background: #bce9e2;
  border-bottom: 1px solid #8dd4c9;
  padding: 16px 20px 14px;
  flex-shrink: 0;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.view-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #f0faf7;
  border: 1.5px solid #006051;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.dialog-title {
  font-size: 18px;
  font-weight: 900;
  color: #006051;
  margin-bottom: 2px;
}
.dialog-sub {
  font-size: 12px;
  font-weight: 600;
  color: #4a9e8a;
}
.close-btn {
  color: #006051 !important;
  background: #f0faf7 !important;
  border-radius: 8px !important;
}
.close-btn:hover {
  background: #d5f0e9 !important;
  color: #004d41 !important;
}

.dialog-body {
  padding: 20px !important;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.profile-section {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #f0faf7;
  border: 1px solid #bce9e2;
  border-radius: 14px;
  padding: 16px 20px;
  margin-bottom: 16px;
}
.profile-avatar {
  flex-shrink: 0;
}
.profile-info {
  flex: 1;
}
.profile-name {
  font-size: 16px;
  font-weight: 800;
  color: #006051;
  line-height: 1.3;
}
.profile-meta {
  font-size: 12px;
  font-weight: 600;
  color: #4a9e8a;
  margin-top: 2px;
}

.section-divider {
  display: flex;
  align-items: center;
  margin: 20px 0 12px;
}
.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 800;
  color: #006051;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.info-grid-3 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 4px;
}
.info-grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  background: #f7fcfb;
  border: 1px solid #e0f2ee;
  border-radius: 10px;
  padding: 10px 12px;
}
.info-item.full-width {
  grid-column: 1 / -1;
}
.info-content {
  flex: 1;
  min-width: 0;
}
.info-label {
  font-size: 10px;
  font-weight: 700;
  color: #4a9e8a;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 2px;
}
.info-value {
  font-size: 13px;
  font-weight: 700;
  color: #1f2937;
  word-break: break-word;
}

.estado-chip {
  font-weight: 700;
  min-height: 22px;
}

.dias-section {
  background: #f7fcfb;
  border: 1px solid #e0f2ee;
  border-radius: 10px;
  padding: 12px 16px;
  margin-top: 10px;
}
.dias-label {
  font-size: 10px;
  font-weight: 700;
  color: #4a9e8a;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 8px;
}
.dias-grid {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.dia-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 8px;
  background: #f0faf7;
  border: 1px solid #e0f2ee;
  font-size: 12px;
  font-weight: 600;
  color: #9ca3af;
}
.dia-item.activo {
  background: #e0f2ee;
  border-color: #006051;
  color: #006051;
}
.dia-nombre {
  font-size: 11px;
  font-weight: 700;
}

.asistencia-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #f7fcfb;
  border: 1px solid #e0f2ee;
  border-radius: 12px;
  padding: 14px 16px;
}
.asistencia-info {
  flex: 1;
}
.asistencia-title {
  font-size: 14px;
  font-weight: 700;
  color: #1f2937;
}
.asistencia-detail {
  font-size: 11px;
  font-weight: 600;
  color: #4a9e8a;
  margin-top: 2px;
}

.pago-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fef8f0;
  border: 1px solid #fde0c0;
  border-radius: 12px;
  padding: 14px 16px;
}
.pago-text {
  font-size: 15px;
  font-weight: 800;
  color: #F57C00;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 20px;
}
.empty-text {
  font-size: 14px;
  font-weight: 600;
  color: #9ca3af;
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: #006051;
  border-top: 1px solid #004d41;
  flex-shrink: 0;
}
.footer-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.footer-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #8BC34A;
  display: flex;
  align-items: center;
  justify-content: center;
}
.footer-text {
  font-size: 12px;
  font-weight: 700;
  color: #ffffff;
}
.footer-actions {
  display: flex;
  gap: 8px;
}
.btn-pdf-footer {
  background: #8BC34A !important;
  color: #ffffff !important;
  border-radius: 8px !important;
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  font-weight: 800;
  height: 36px;
  padding: 0 16px;
}
.btn-pdf-footer:hover {
  background: #7cb342 !important;
}
.btn-cerrar {
  background: #ffffff !important;
  color: #006051 !important;
  border-radius: 8px !important;
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  font-weight: 800;
  height: 36px;
  padding: 0 16px;
}
.btn-cerrar:hover {
  background: #bce9e2 !important;
}

@media (max-width: 600px) {
  .info-grid-2,
  .info-grid-3 {
    grid-template-columns: 1fr;
  }
  .dialog-card {
    max-width: 95vw !important;
  }
}
</style>

<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card" style="max-height: 85vh; display: flex; flex-direction: column;">

      <!-- Header -->
      <div class="dialog-header" style="flex-shrink: 0;">
        <div class="header-left">
          <div class="view-icon">
            <q-icon name="visibility" size="22px" style="color:#4a8c25" />
          </div>
          <div>
            <div class="dialog-title">Detalle del Empleado</div>
            <div class="dialog-sub">Información completa del empleado</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>

      <!-- Loading -->
      <div v-if="cargando" class="loading-container" style="flex: 1; display: flex; align-items: center; justify-content: center;">
        <q-spinner color="#82bd43" size="40px" />
        <span class="loading-text">Cargando detalles...</span>
      </div>

      <!-- Body con scroll -->
      <q-card-section v-else class="dialog-body" style="flex: 1; overflow-y: auto; min-height: 0;">

        <!-- Layout principal -->
        <div class="main-layout">

          <!-- Columna izquierda: Perfil sin foto -->
          <div class="profile-card">
            <div class="avatar-container">
              <div class="avatar-large">{{ iniciales }}</div>
              <div class="verified-badge" v-if="empleadoDetalle?.estado">
                <q-icon name="check" size="12px" color="white" />
              </div>
            </div>
            <div class="profile-label">Empleado</div>
            <div class="profile-name">{{ empleadoDetalle?.nombreCompleto || '—' }}</div>
            <div class="profile-dni">CI: {{ empleadoDetalle?.ci || '—' }}</div>
          </div>

          <!-- Columna derecha: Datos personales y laborales -->
          <div class="data-grid">

            <div class="data-item">
              <div class="data-icon">🆔</div>
              <div class="data-content">
                <div class="data-label">ID Empleado</div>
                <div class="data-value">{{ empleadoDetalle?.idEmpleado || '—' }}</div>
              </div>
            </div>

            <div class="data-item">
              <div class="data-icon">✓</div>
              <div class="data-content">
                <div class="data-label">Estado</div>
                <div class="data-value">
                  <span class="estado-badge" :class="empleadoDetalle?.estado ? 'estado-activo' : 'estado-inactivo'">
                    <span class="estado-dot"></span>
                    {{ empleadoDetalle?.estado ? 'Activo' : 'Inactivo' }}
                  </span>
                </div>
              </div>
            </div>

            <div class="data-item">
              <div class="data-icon">📅</div>
              <div class="data-content">
                <div class="data-label">Fecha de Nacimiento</div>
                <div class="data-value">{{ formatFecha(empleadoDetalle?.fechaNacimiento) }}</div>
              </div>
            </div>

            <div class="data-item">
              <div class="data-icon">📅</div>
              <div class="data-content">
                <div class="data-label">Fecha de Contratación</div>
                <div class="data-value">{{ formatFecha(empleadoDetalle?.fechaContratacion) }}</div>
              </div>
            </div>

            <div class="data-item">
              <div class="data-icon">⚥</div>
              <div class="data-content">
                <div class="data-label">Sexo</div>
                <div class="data-value">{{ empleadoDetalle?.sexo || '—' }}</div>
              </div>
            </div>

          </div>

        </div>

        <!-- Contacto -->
        <div class="seccion">
          <div class="section-title">
            <q-icon name="contact_mail" size="14px" style="color:#5a8040" />
            Contacto
          </div>
          <div class="info-grid-2">
            <div class="info-item">
              <div class="info-icon">✉</div>
              <div class="info-content">
                <div class="info-label">Correo Electrónico</div>
                <div class="info-value">{{ empleadoDetalle?.correo || '—' }}</div>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">📞</div>
              <div class="info-content">
                <div class="info-label">Teléfono</div>
                <div class="info-value">{{ empleadoDetalle?.telefono || '—' }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Dirección -->
        <div class="seccion">
          <div class="section-title">
            <q-icon name="location_on" size="14px" style="color:#5a8040" />
            Dirección
          </div>
          <div class="info-grid-2">
            <div class="info-item">
              <div class="info-icon">🏛</div>
              <div class="info-content">
                <div class="info-label">País</div>
                <div class="info-value">{{ empleadoDetalle?.pais || '—' }}</div>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">🏛</div>
              <div class="info-content">
                <div class="info-label">Departamento</div>
                <div class="info-value">{{ empleadoDetalle?.departamento || '—' }}</div>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">🏙</div>
              <div class="info-content">
                <div class="info-label">Ciudad</div>
                <div class="info-value">{{ empleadoDetalle?.ciudad || '—' }}</div>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">📍</div>
              <div class="info-content">
                <div class="info-label">Zona / Barrio</div>
                <div class="info-value">{{ empleadoDetalle?.zona || '—' }}</div>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">🛣</div>
              <div class="info-content">
                <div class="info-label">Calle</div>
                <div class="info-value">{{ empleadoDetalle?.calle || '—' }}</div>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">🔢</div>
              <div class="info-content">
                <div class="info-label">Número</div>
                <div class="info-value">{{ empleadoDetalle?.numero || '—' }}</div>
              </div>
            </div>
            <div class="info-item full-width">
              <div class="info-icon">📝</div>
              <div class="info-content">
                <div class="info-label">Referencia</div>
                <div class="info-value">{{ empleadoDetalle?.referencia || '—' }}</div>
              </div>
            </div>
          </div>
        </div>

      </q-card-section>

      <!-- Footer -->
      <div class="dialog-footer" style="flex-shrink: 0;">
        <div class="footer-left">
          <div class="verified-icon">
            <q-icon name="verified" size="18px" color="white" />
          </div>
          <span class="footer-text">Datos verificados del empleado</span>
        </div>
        <q-btn
          class="btn-pdf"
          unelevated
          no-caps
          @click="exportarPDF"
          icon="picture_as_pdf"
          label="PDF"
        />
        <q-btn
          class="btn-cerrar"
          unelevated
          no-caps
          @click="cerrar"
        >
          <q-icon name="close" size="16px" class="q-mr-sm" />
          Cerrar
        </q-btn>
      </div>

    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { obtenerEmpleado, exportarEmpleadoDetallePDF } from '../../api/empleado/empleado'
import { useQuasar } from 'quasar'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  idEmpleado: { type: [Number, String], default: null }
})

const emit = defineEmits(['update:modelValue'])

const $q = useQuasar()
const abierto = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const cargando = ref(false)
const empleadoDetalle = ref({})

watch(() => props.modelValue, async (val) => {
  if (val && props.idEmpleado) {
    await cargarDetalle()
  }
})

const cargarDetalle = async () => {
  try {
    cargando.value = true
    const respuesta = await obtenerEmpleado(props.idEmpleado)
    empleadoDetalle.value = respuesta
  } catch (error) {
    console.error('Error cargando detalle:', error)
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Error al cargar detalles del empleado'
    })
  } finally {
    cargando.value = false
  }
}

const iniciales = computed(() => {
  const nombre = empleadoDetalle.value?.nombreCompleto || 'E'
  return nombre.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase()
})

const formatFecha = (fecha) => {
  if (!fecha) return '—'
  return new Date(fecha).toLocaleString('es-ES', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

const exportarPDF = async () => {
  try {
    const blob = await exportarEmpleadoDetallePDF(props.idEmpleado)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `ficha_empleado_${props.idEmpleado}.pdf`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    $q.notify({
      type: 'positive',
      message: 'PDF descargado correctamente'
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Error al generar PDF'
    })
  }
}

const cerrar = () => {
  abierto.value = false
  empleadoDetalle.value = {}
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

.dialog-card {
  width: 100% !important;
  max-width: 700px !important;
  border-radius: 20px !important;
  overflow: hidden !important;
  background: #ffffff !important;
  border: 1px solid #e4edd8 !important;
  box-shadow: 0 20px 60px rgba(42,92,26,0.15) !important;
  font-family: 'Nunito', sans-serif;
  display: flex;
  flex-direction: column;
  max-height: 85vh;
}

.loading-container {
  flex-direction: column;
  gap: 16px;
  padding: 40px;
}
.loading-text {
  font-size: 14px;
  font-weight: 700;
  color: #82bd43;
  font-family: 'Nunito', sans-serif;
}

.dialog-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  background: #f0f7e8;
  border-bottom: 1px solid #c8e0a0;
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
  background: #eaf4d8;
  border: 1.5px solid #82bd43;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.dialog-title {
  font-size: 18px;
  font-weight: 900;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
  margin-bottom: 2px;
}
.dialog-sub {
  font-size: 12px;
  font-weight: 600;
  color: #7aaa4e;
  font-family: 'Nunito', sans-serif;
}
.close-btn {
  color: #7aaa4e !important;
  background: #f0f7e8 !important;
  border-radius: 8px !important;
}
.close-btn:hover {
  background: #ddecc5 !important;
  color: #4a8c25 !important;
}

.dialog-body {
  padding: 20px !important;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.main-layout {
  display: grid;
  grid-template-columns: 180px 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.profile-card {
  background: #f7f9f4;
  border: 1px solid #e4edd8;
  border-radius: 14px;
  padding: 20px 15px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.avatar-container {
  position: relative;
  margin-bottom: 12px;
}
.avatar-large {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: #82bd43;
  border: 3px solid #4a8c25;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 900;
  color: #ffffff;
  font-family: 'Nunito', sans-serif;
}
.verified-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #82bd43;
  border: 2px solid #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.profile-label {
  font-size: 11px;
  font-weight: 800;
  color: #82bd43;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 4px;
  font-family: 'Nunito', sans-serif;
}
.profile-name {
  font-size: 14px;
  font-weight: 800;
  color: #2a5c1a;
  margin-bottom: 4px;
  font-family: 'Nunito', sans-serif;
  line-height: 1.3;
}
.profile-dni {
  font-size: 11px;
  font-weight: 600;
  color: #9dbf78;
  font-family: 'Nunito', sans-serif;
}

.data-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.data-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  background: #ffffff;
  border: 1px solid #e4edd8;
  border-radius: 10px;
  padding: 10px 12px;
}
.data-icon {
  font-size: 16px;
  flex-shrink: 0;
  margin-top: 2px;
}
.data-content {
  flex: 1;
  min-width: 0;
}
.data-label {
  font-size: 9px;
  font-weight: 800;
  color: #9dbf78;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 2px;
  font-family: 'Nunito', sans-serif;
}
.data-value {
  font-size: 13px;
  font-weight: 700;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
  word-break: break-word;
}

.estado-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
  font-family: 'Nunito', sans-serif;
}
.estado-activo {
  background: #eaf4d8;
  color: #4a8c25;
  border: 1px solid #c8e0a0;
}
.estado-inactivo {
  background: #fef3e2;
  color: #a05c10;
  border: 1px solid #f5c97a;
}
.estado-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.seccion {
  margin-bottom: 16px;
}
.section-title {
  font-size: 11px;
  font-weight: 800;
  color: #5a8040;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-family: 'Nunito', sans-serif;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 10px;
}

.info-grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.info-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  background: #f7f9f4;
  border: 1px solid #e4edd8;
  border-radius: 10px;
  padding: 12px;
}
.info-item.full-width {
  grid-column: 1 / -1;
}
.info-icon {
  font-size: 16px;
  flex-shrink: 0;
}
.info-content {
  flex: 1;
}
.info-label {
  font-size: 10px;
  font-weight: 800;
  color: #9dbf78;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 3px;
  font-family: 'Nunito', sans-serif;
}
.info-value {
  font-size: 13px;
  font-weight: 700;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
  line-height: 1.4;
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: #2a5c1a;
  border-top: 1px solid #1a4a0a;
  flex-shrink: 0;
}
.footer-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.verified-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #82bd43;
  display: flex;
  align-items: center;
  justify-content: center;
}
.footer-text {
  font-size: 12px;
  font-weight: 700;
  color: #ffffff;
  font-family: 'Nunito', sans-serif;
}
.btn-pdf {
  background: #ffffff !important;
  color: #aa4c0e !important;
  border-radius: 8px !important;
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  font-weight: 800;
  height: 36px;
  padding: 0 16px;
  margin-right: 8px;
}
.btn-pdf:hover {
  background: #fef3e2 !important;
}
.btn-cerrar {
  background: #ffffff !important;
  color: #2a5c1a !important;
  border-radius: 8px !important;
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  font-weight: 800;
  height: 36px;
  padding: 0 16px;
}
.btn-cerrar:hover {
  background: #f0f7e8 !important;
}

@media (max-width: 600px) {
  .main-layout {
    grid-template-columns: 1fr;
  }
  .data-grid {
    grid-template-columns: 1fr;
  }
  .info-grid-2 {
    grid-template-columns: 1fr;
  }
  .dialog-card {
    max-width: 95vw !important;
  }
}
</style>

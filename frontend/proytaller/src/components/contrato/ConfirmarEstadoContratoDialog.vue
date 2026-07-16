<template>
  <q-dialog v-model="abierto" persistent transition-show="fade" transition-hide="fade">
    <q-card class="dialog-card">
      <div class="accent-bar" :class="`accent-bar--${tipo}`"></div>

      <div class="dialog-header" :class="`dialog-header--${tipo}`">
        <div class="header-left">
          <div class="warn-icon" :class="`warn-icon--${tipo}`">
            <q-icon :name="icono" size="22px" :style="{ color: colorIcono }" />
          </div>
          <div>
            <div class="dialog-title">{{ titulo }}</div>
            <div class="dialog-sub">{{ subtitulo }}</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" :class="`close-btn--${tipo}`" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">
        <div class="user-card">
          <div class="user-avatar" :class="`user-avatar--${tipo}`">{{ iniciales }}</div>
          <div>
            <div class="user-name">{{ empleadoNombre }}</div>
            <div class="user-sub">CTR-{{ String(contratoId).padStart(4, '0') }}</div>
          </div>
        </div>

        <div class="warn-box" :class="`warn-box--${tipo}`">
          <q-icon :name="iconoAlerta" size="17px" :style="{ color: colorIcono }" />
          <p v-html="textoAlerta"></p>
        </div>

        <div class="consecuencias" :class="`consecuencias--${tipo}`">
          <div class="cons-title">{{ consTitulo }}</div>
          <ul class="cons-list">
            <li v-for="item in consItems" :key="item">
              <q-icon :name="iconoCons" size="14px" :style="{ color: colorIcono }" /> {{ item }}
            </li>
          </ul>
        </div>

        <div class="confirm-row" :class="`confirm-row--${tipo}`" @click="confirmado = !confirmado">
          <div class="confirm-check" :class="[`confirm-check--${tipo}`, { checked: confirmado }]">
            <q-icon v-if="confirmado" name="check" size="11px" color="white" />
          </div>
          <span class="confirm-txt" :class="`confirm-txt--${tipo}`">{{ textoConfirmacion }}</span>
        </div>
      </q-card-section>

      <div class="dialog-footer">
        <button class="btn-cancel" @click="cerrar">
          <q-icon name="close" size="15px" />Cancelar
        </button>
        <button
          :class="[`btn-accion--${tipo}`, { 'btn-disabled': !confirmado || cargando }]"
          :disabled="!confirmado || cargando"
          @click="confirmar"
        >
          <q-spinner-dots v-if="cargando" color="white" size="1em" />
          <q-icon v-else :name="icono" size="16px" />
          {{ cargando ? textoCargando : textoBoton }}
        </button>
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'
import { activarContrato, desactivarContrato } from '../../api/contrato/contrato'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  contratoId: { type: [Number, String], default: null },
  empleadoNombre: { type: String, default: '' },
  tipo: { type: String, default: 'activar' }
})

const emit = defineEmits(['update:modelValue', 'finalizado'])

const $q = useQuasar()
const abierto = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})
const confirmado = ref(false)
const cargando = ref(false)

const esActivar = computed(() => props.tipo === 'activar')

const icono = computed(() => esActivar.value ? 'play_arrow' : 'pause_circle')
const colorIcono = computed(() => esActivar.value ? '#006051' : '#F57C00')
const titulo = computed(() => esActivar.value ? 'Activar Contrato' : 'Suspender Contrato')
const subtitulo = computed(() => esActivar.value ? 'El contrato volverá a estar vigente' : 'El contrato se pausará temporalmente')
const iconoAlerta = computed(() => esActivar.value ? 'info' : 'warning_amber')
const textoAlerta = computed(() => esActivar.value
  ? 'Al activar este contrato, el empleado <strong>podrá registrar asistencia</strong> y generar planilla nuevamente.'
  : 'Al suspender este contrato, el empleado <strong>no podrá registrar asistencia</strong> ni generar planilla hasta que sea reactivado.'
)
const consTitulo = computed(() => esActivar.value ? '¿Qué ocurrirá?' : '¿Qué ocurrirá?')
const consItems = computed(() => esActivar.value
  ? [
      'El empleado volverá a estar activo en el sistema',
      'Se habilitará el registro de asistencia',
      'El contrato pasará a estado ACTIVO'
    ]
  : [
      'El empleado quedará temporalmente sin acceso',
      'Se pausará la generación de planilla',
      'El contrato pasará a estado SUSPENDIDO'
    ]
)
const iconoCons = computed(() => esActivar.value ? 'check_circle' : 'block')
const textoConfirmacion = computed(() => esActivar.value
  ? 'Entiendo que el contrato se activará'
  : 'Entiendo que el contrato se suspenderá'
)
const textoBoton = computed(() => esActivar.value ? 'Activar Contrato' : 'Suspender Contrato')
const textoCargando = computed(() => esActivar.value ? 'Activando...' : 'Suspendiendo...')

const iniciales = computed(() => {
  if (!props.empleadoNombre) return 'C'
  return props.empleadoNombre.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase()
})

function cerrar() {
  confirmado.value = false
  abierto.value = false
}

async function confirmar() {
  if (!confirmado.value) return
  cargando.value = true
  try {
    if (esActivar.value) {
      await activarContrato(props.contratoId)
    } else {
      await desactivarContrato(props.contratoId)
    }
    $q.notify({ type: 'positive', message: esActivar.value ? 'Contrato activado correctamente' : 'Contrato suspendido correctamente' })
    emit('finalizado', props.contratoId)
    cerrar()
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.message || 'Error al cambiar estado del contrato' })
  } finally {
    cargando.value = false
  }
}
</script>

<style scoped>
.dialog-card {
  width: 100%;
  max-width: 430px;
  border-radius: 20px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e4edd8;
  box-shadow: 0 20px 60px rgba(42,92,26,0.15);
  font-family: 'Nunito', sans-serif;
  animation: dialogPopIn 0.3s ease-out;
}
@keyframes dialogPopIn {
  from { opacity: 0; transform: scale(0.92) translateY(16px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.accent-bar { height: 3px; transition: all 0.3s ease; }
.accent-bar--activar { background: linear-gradient(90deg, #006051, #00897b, #006051); }
.accent-bar--suspender { background: linear-gradient(90deg, #F57C00, #ff9800, #F57C00); }
.dialog-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  border-bottom: 1px solid; padding: 16px 20px 14px;
}
.dialog-header--activar { background: #e8f5e9; border-bottom-color: #a5d6a7; }
.dialog-header--suspender { background: #fff3e0; border-bottom-color: #ffcc80; }
.header-left { display: flex; align-items: center; gap: 12px; }
.warn-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  transition: transform 0.3s ease;
}
.header-left:hover .warn-icon { transform: rotate(-10deg) scale(1.05); }
.warn-icon--activar { background: #e0f2f1; border: 1.5px solid #80cbc4; }
.warn-icon--suspender { background: #ffe0b2; border: 1.5px solid #ffb74d; }
.dialog-title { font-size: 15px; font-weight: 900; margin-bottom: 2px; }
.dialog-header--activar .dialog-title { color: #004d40; }
.dialog-header--suspender .dialog-title { color: #bf360c; }
.dialog-sub { font-size: 11px; font-weight: 600; }
.dialog-header--activar .dialog-sub { color: #00897b; }
.dialog-header--suspender .dialog-sub { color: #ef6c00; }
.close-btn {
  border-radius: 8px; transition: all 0.2s ease;
}
.close-btn--activar { color: #00897b; background: #e0f2f1; }
.close-btn--activar:hover { background: #b2dfdb; color: #004d40; transform: rotate(90deg); }
.close-btn--suspender { color: #ef6c00; background: #ffe0b2; }
.close-btn--suspender:hover { background: #ffcc80; color: #bf360c; transform: rotate(90deg); }
.dialog-body { padding: 18px 20px 4px; }
.user-card {
  display: flex; align-items: center; gap: 12px;
  background: #f7f9f4; border: 1px solid #e4edd8;
  border-radius: 12px; padding: 12px 14px; margin-bottom: 14px;
  transition: all 0.2s ease;
}
.user-card:hover { border-color: #c8e0a0; box-shadow: 0 2px 8px rgba(42,92,26,0.06); }
.user-avatar {
  width: 42px; height: 42px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 900; flex-shrink: 0;
  transition: transform 0.2s ease;
}
.user-avatar--activar { background: #e0f2f1; border: 2px solid #80cbc4; color: #004d40; }
.user-avatar--suspender { background: #ffe0b2; border: 2px solid #ffb74d; color: #bf360c; }
.user-card:hover .user-avatar { transform: scale(1.05); }
.user-name { font-size: 14px; font-weight: 800; color: #2a5c1a; }
.user-sub { font-size: 11px; font-weight: 600; color: #7aaa4e; margin-top: 2px; }
.warn-box {
  display: flex; align-items: flex-start; gap: 10px;
  border-radius: 10px; padding: 11px 13px; margin-bottom: 14px;
  animation: warnSlideIn 0.35s ease-out 0.1s both;
}
@keyframes warnSlideIn {
  from { opacity: 0; transform: translateX(-8px); }
  to { opacity: 1; transform: translateX(0); }
}
.warn-box--activar { background: #e8f5e9; border: 1px solid #a5d6a7; }
.warn-box--suspender { background: #fff3e0; border: 1px solid #ffcc80; }
.warn-box p { font-size: 12.5px; font-weight: 600; line-height: 1.6; margin: 0; }
.warn-box--activar p { color: #00695c; }
.warn-box--suspender p { color: #e65100; }
.warn-box p strong { font-weight: 800; }
.warn-box--activar p strong { color: #004d40; }
.warn-box--suspender p strong { color: #bf360c; }
.consecuencias {
  border-radius: 10px; padding: 12px 14px; margin-bottom: 14px;
}
.consecuencias--activar { background: #e8f5e9; border: 1px solid #a5d6a7; }
.consecuencias--suspender { background: #fff3e0; border: 1px solid #ffcc80; }
.cons-title { font-size: 12px; font-weight: 800; margin-bottom: 8px; }
.consecuencias--activar .cons-title { color: #004d40; }
.consecuencias--suspender .cons-title { color: #bf360c; }
.cons-list { list-style: none; padding: 0; margin: 0; }
.cons-list li {
  display: flex; align-items: center; gap: 8px;
  font-size: 11.5px; font-weight: 600; color: #555;
  padding: 3px 0;
}
.confirm-row {
  display: flex; align-items: center; gap: 9px;
  border-radius: 9px; padding: 10px 12px; margin-bottom: 4px; cursor: pointer;
  transition: all 0.2s ease;
}
.confirm-row--activar { background: #e8f5e9; border: 1px solid #a5d6a7; }
.confirm-row--suspender { background: #fff3e0; border: 1px solid #ffcc80; }
.confirm-row:hover { box-shadow: 0 2px 8px rgba(42,92,26,0.08); }
.confirm-row:active { transform: scale(0.98); }
.confirm-check {
  width: 18px; height: 18px; border-radius: 5px; background: #fff;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  transition: all 0.2s ease;
}
.confirm-check--activar { border: 2px solid #00897b; }
.confirm-check--suspender { border: 2px solid #F57C00; }
.confirm-check--activar.checked { background: #00897b; border-color: #00897b; }
.confirm-check--suspender.checked { background: #F57C00; border-color: #F57C00; }
.confirm-txt { font-size: 12px; font-weight: 700; }
.confirm-txt--activar { color: #00695c; }
.confirm-txt--suspender { color: #e65100; }
.dialog-footer {
  display: flex; gap: 10px; padding: 14px 20px 18px;
  border-top: 1px solid #e4edd8;
}
.btn-cancel {
  flex: 1; padding: 11px; background: #fff; color: #5a5a5a;
  border: 1.5px solid #d0d0d0; border-radius: 9px;
  font-size: 14px; font-weight: 700; cursor: pointer;
  display: flex; align-items: center; justify-content: center; gap: 6px;
  transition: all 0.2s ease;
}
.btn-cancel:hover { background: #f7f7f7; border-color: #bbb; }
.btn-cancel:active { transform: scale(0.97); }
.btn-accion--activar {
  flex: 2; padding: 11px; background: #006051; color: #fff;
  border: none; border-radius: 9px; font-size: 14px; font-weight: 800;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 7px;
  box-shadow: 0 4px 14px rgba(0,96,81,0.3);
  transition: all 0.25s ease;
}
.btn-accion--activar:hover:not(.btn-disabled) { background: #004d40; transform: translateY(-1px); box-shadow: 0 6px 20px rgba(0,96,81,0.4); }
.btn-accion--activar:active:not(.btn-disabled) { transform: translateY(0); }
.btn-accion--suspender {
  flex: 2; padding: 11px; background: #F57C00; color: #fff;
  border: none; border-radius: 9px; font-size: 14px; font-weight: 800;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 7px;
  box-shadow: 0 4px 14px rgba(245,124,0,0.3);
  transition: all 0.25s ease;
}
.btn-accion--suspender:hover:not(.btn-disabled) { background: #e66f00; transform: translateY(-1px); box-shadow: 0 6px 20px rgba(245,124,0,0.4); }
.btn-accion--suspender:active:not(.btn-disabled) { transform: translateY(0); }
.btn-disabled { opacity: 0.5; cursor: not-allowed; transform: none; box-shadow: none; }
</style>

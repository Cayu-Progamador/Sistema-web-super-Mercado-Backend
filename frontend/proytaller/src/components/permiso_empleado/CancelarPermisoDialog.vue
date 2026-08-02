<template>
  <q-dialog v-model="abierto" persistent transition-show="fade" transition-hide="fade">
    <q-card class="dialog-card">
      <div class="accent-bar accent-bar--cancelar"></div>

      <div class="dialog-header dialog-header--cancelar">
        <div class="header-left">
          <div class="warn-icon warn-icon--cancelar">
            <q-icon name="cancel" size="22px" color="#F57C00" />
          </div>
          <div>
            <div class="dialog-title">Cancelar solicitud</div>
            <div class="dialog-sub">Esta acción no se puede deshacer</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn close-btn--cancelar" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">
        <div class="user-card">
          <div class="user-avatar user-avatar--cancelar">{{ iniciales }}</div>
          <div>
            <div class="user-name">{{ solicitud?.nombreTipo }}</div>
            <div class="user-sub">{{ fechasTexto }}</div>
          </div>
        </div>

        <div class="warn-box warn-box--cancelar">
          <q-icon name="warning_amber" size="17px" color="#D32F2F" />
          <p>Al cancelar esta solicitud, se <strong>descartará permanentemente</strong> y no podrá ser recuperada.</p>
        </div>

        <div class="consecuencias consecuencias--cancelar">
          <div class="cons-title">¿Qué ocurrirá?</div>
          <ul class="cons-list">
            <li>
              <q-icon name="block" size="14px" color="#F57C00" /> La solicitud pasará a estado CANCELADO
            </li>
            <li>
              <q-icon name="block" size="14px" color="#F57C00" /> Podrás crear una nueva solicitud inmediatamente
            </li>
          </ul>
        </div>

        <div class="confirm-row confirm-row--cancelar" @click="confirmado = !confirmado">
          <div class="confirm-check confirm-check--cancelar" :class="{ checked: confirmado }">
            <q-icon v-if="confirmado" name="check" size="11px" color="white" />
          </div>
          <span class="confirm-txt confirm-txt--cancelar">Entiendo que la solicitud será cancelada</span>
        </div>
      </q-card-section>

      <div class="dialog-footer">
        <button class="btn-cancel" @click="cerrar">
          <q-icon name="close" size="15px" />Volver
        </button>
        <button
          class="btn-accion--cancelar"
          :class="{ 'btn-disabled': !confirmado || cargando }"
          :disabled="!confirmado || cargando"
          @click="confirmar"
        >
          <q-spinner-dots v-if="cargando" color="white" size="1em" />
          <q-icon v-else name="cancel" size="16px" />
          {{ cargando ? 'Cancelando...' : 'Sí, cancelar solicitud' }}
        </button>
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'
import { cancelarSolicitud } from '../../api/permiso_personal/permiso_personal'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  solicitud: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'cancelado'])

const $q = useQuasar()
const abierto = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})
const confirmado = ref(false)
const cargando = ref(false)

const iniciales = computed(() => {
  if (!props.solicitud?.nombreTipo) return 'C'
  return props.solicitud.nombreTipo.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
})

const fechasTexto = computed(() => {
  if (!props.solicitud) return ''
  const fmt = d => {
    if (!d) return ''
    const [y, m, day] = d.split('-')
    return `${day}/${m}/${y}`
  }
  if (!props.solicitud.fechaFin) return fmt(props.solicitud.fechaInicio)
  return `${fmt(props.solicitud.fechaInicio)} - ${fmt(props.solicitud.fechaFin)}`
})

function cerrar() {
  confirmado.value = false
  abierto.value = false
}

async function confirmar() {
  if (!confirmado.value || !props.solicitud) return
  cargando.value = true
  try {
    await cancelarSolicitud(props.solicitud.id, { comentario: null })
    $q.notify({ type: 'positive', message: 'Solicitud cancelada correctamente', position: 'top' })
    emit('cancelado', props.solicitud.id)
    cerrar()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || e.message || 'Error al cancelar' })
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
  border: 1px solid #ffe0b2;
  box-shadow: 0 20px 60px rgba(245, 124, 0, 0.12);
  font-family: 'Nunito', sans-serif;
  animation: dialogPopIn 0.3s ease-out;
}
@keyframes dialogPopIn {
  from { opacity: 0; transform: scale(0.92) translateY(16px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.accent-bar { height: 3px; }
.accent-bar--cancelar { background: linear-gradient(90deg, #F57C00, #FFB74D, #F57C00); }
.dialog-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  border-bottom: 1px solid; padding: 16px 20px 14px;
}
.dialog-header--cancelar { background: #fff3e0; border-bottom-color: #ffcc80; }
.header-left { display: flex; align-items: center; gap: 12px; }
.warn-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.warn-icon--cancelar { background: #ffe0b2; border: 1.5px solid #ffb74d; }
.dialog-title { font-size: 15px; font-weight: 900; color: #bf360c; margin-bottom: 2px; }
.dialog-sub { font-size: 11px; font-weight: 600; color: #ef6c00; }
.close-btn { border-radius: 8px; }
.close-btn--cancelar { color: #ef6c00; background: #ffe0b2; }
.close-btn--cancelar:hover { background: #ffcc80; color: #bf360c; }
.dialog-body { padding: 18px 20px 4px; }
.user-card {
  display: flex; align-items: center; gap: 12px;
  background: #fff8e1; border: 1px solid #ffe0b2;
  border-radius: 12px; padding: 12px 14px; margin-bottom: 14px;
}
.user-avatar {
  width: 42px; height: 42px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 900; flex-shrink: 0;
}
.user-avatar--cancelar { background: #ffe0b2; border: 2px solid #ffb74d; color: #bf360c; }
.user-name { font-size: 14px; font-weight: 800; color: #e65100; }
.user-sub { font-size: 11px; font-weight: 600; color: #ef6c00; margin-top: 2px; }
.warn-box {
  display: flex; align-items: flex-start; gap: 10px;
  background: #fff3e0; border: 1px solid #ffcc80;
  border-radius: 10px; padding: 11px 13px; margin-bottom: 14px;
}
.warn-box p { font-size: 12.5px; font-weight: 600; line-height: 1.6; margin: 0; color: #e65100; }
.warn-box p strong { font-weight: 800; color: #bf360c; }
.consecuencias {
  background: #fff3e0; border: 1px solid #ffcc80;
  border-radius: 10px; padding: 12px 14px; margin-bottom: 14px;
}
.cons-title { font-size: 12px; font-weight: 800; color: #e65100; margin-bottom: 8px; }
.cons-list { list-style: none; padding: 0; margin: 0; }
.cons-list li {
  display: flex; align-items: center; gap: 8px;
  font-size: 11.5px; font-weight: 600; color: #555;
  padding: 3px 0;
}
.confirm-row {
  display: flex; align-items: center; gap: 9px;
  background: #fff3e0; border: 1px solid #ffcc80;
  border-radius: 9px; padding: 10px 12px; margin-bottom: 4px; cursor: pointer;
}
.confirm-check {
  width: 18px; height: 18px; border-radius: 5px; background: #fff;
  border: 2px solid #F57C00;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.confirm-check.checked { background: #F57C00; border-color: #F57C00; }
.confirm-txt { font-size: 12px; font-weight: 700; color: #e65100; }
.dialog-footer {
  display: flex; gap: 10px; padding: 14px 20px 18px;
  border-top: 1px solid #ffe0b2;
}
.btn-cancel {
  flex: 1; padding: 11px; background: #fff; color: #5a5a5a;
  border: 1.5px solid #d0d0d0; border-radius: 9px;
  font-size: 14px; font-weight: 700; cursor: pointer;
  display: flex; align-items: center; justify-content: center; gap: 6px;
}
.btn-cancel:hover { background: #f7f7f7; border-color: #bbb; }
.btn-accion--cancelar {
  flex: 2; padding: 11px; background: #F57C00; color: #fff;
  border: none; border-radius: 9px; font-size: 14px; font-weight: 800;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 7px;
  box-shadow: 0 4px 14px rgba(245, 124, 0, 0.3);
  transition: all 0.25s ease;
}
.btn-accion--cancelar:hover:not(.btn-disabled) { background: #e66f00; transform: translateY(-1px); box-shadow: 0 6px 20px rgba(245,124,0,0.4); }
.btn-disabled { opacity: 0.5; cursor: not-allowed; transform: none; box-shadow: none; }
</style>

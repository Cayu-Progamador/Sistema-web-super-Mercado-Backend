<template>
  <q-dialog v-model="abierto" persistent transition-show="fade" transition-hide="fade">
    <q-card class="dialog-card">
      <div class="accent-bar"></div>

      <div class="dialog-header">
        <div class="header-left">
          <div class="warn-icon">
            <q-icon name="stop_circle" size="22px" style="color: #C10015" />
          </div>
          <div>
            <div class="dialog-title">Finalizar Contrato</div>
            <div class="dialog-sub">Esta acción no se puede deshacer</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">
        <div class="user-card">
          <div class="user-avatar">{{ iniciales }}</div>
          <div>
            <div class="user-name">{{ empleadoNombre }}</div>
            <div class="user-sub">CTR-{{ String(contratoId).padStart(4, '0') }}</div>
          </div>
        </div>

        <div class="warn-box">
          <q-icon name="warning_amber" size="17px" style="color: #C10015" />
          <p>Al finalizar este contrato, el empleado <strong>quedará sin contrato activo</strong> y no podrá registrar asistencia ni generar planilla.</p>
        </div>

        <div class="consecuencias">
          <div class="cons-title">¿Qué ocurrirá?</div>
          <ul class="cons-list">
            <li><q-icon name="block" size="14px" style="color: #C10015" /> Se desactivará el control de acceso del empleado</li>
            <li><q-icon name="payments_off" size="14px" style="color: #C10015" /> Se detendrá la generación de planilla</li>
            <li><q-icon name="fact_check" size="14px" style="color: #C10015" /> El contrato pasará a estado <strong>FINALIZADO</strong></li>
          </ul>
        </div>

        <div class="confirm-row" @click="confirmado = !confirmado">
          <div class="confirm-check" :class="{ checked: confirmado }">
            <q-icon v-if="confirmado" name="check" size="11px" color="white" />
          </div>
          <span class="confirm-txt">Entiendo que finalizaré el contrato permanentemente</span>
        </div>
      </q-card-section>

      <div class="dialog-footer">
        <button class="btn-cancel" @click="cerrar">
          <q-icon name="close" size="15px" />Cancelar
        </button>
        <button
          class="btn-accion"
          :class="{ 'btn-disabled': !confirmado || cargando }"
          :disabled="!confirmado || cargando"
          @click="confirmar"
        >
          <q-spinner-dots v-if="cargando" color="white" size="1em" />
          <q-icon v-else name="stop_circle" size="16px" />
          {{ cargando ? 'Finalizando...' : 'Finalizar Contrato' }}
        </button>
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'
import { finalizarContrato } from '../../api/contrato/contrato'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  contratoId: { type: [Number, String], default: null },
  empleadoNombre: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue', 'finalizado'])

const $q = useQuasar()
const abierto = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})
const confirmado = ref(false)
const cargando = ref(false)

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
    await finalizarContrato(props.contratoId)
    $q.notify({ type: 'positive', message: 'Contrato finalizado correctamente' })
    emit('finalizado', props.contratoId)
    cerrar()
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.message || 'Error al finalizar contrato' })
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
  border: 1px solid #f0d0d0;
  box-shadow: 0 20px 60px rgba(193,0,21,0.12);
  font-family: 'Nunito', sans-serif;
  animation: dialogPopIn 0.3s ease-out;
}
@keyframes dialogPopIn {
  from { opacity: 0; transform: scale(0.92) translateY(16px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.accent-bar { height: 3px; background: linear-gradient(90deg, #C10015, #e63946, #C10015); }
.dialog-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  border-bottom: 1px solid #f5d0d0; padding: 16px 20px 14px;
  background: #fff5f5;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.warn-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  background: #ffe0e0; border: 1.5px solid #f5a0a0;
  transition: transform 0.3s ease;
}
.header-left:hover .warn-icon { transform: rotate(-10deg) scale(1.05); }
.dialog-title { font-size: 15px; font-weight: 900; color: #8a1a1a; margin-bottom: 2px; }
.dialog-sub { font-size: 11px; font-weight: 600; color: #b06060; }
.close-btn {
  border-radius: 8px; color: #b06060; background: #ffe0e0;
  transition: all 0.2s ease;
}
.close-btn:hover { background: #f5c0c0; color: #C10015; transform: rotate(90deg); }
.dialog-body { padding: 18px 20px 4px; }
.user-card {
  display: flex; align-items: center; gap: 12px;
  background: #fcf7f7; border: 1px solid #f0d0d0;
  border-radius: 12px; padding: 12px 14px; margin-bottom: 14px;
  transition: all 0.2s ease;
}
.user-card:hover { border-color: #e0a0a0; box-shadow: 0 2px 8px rgba(193,0,21,0.06); }
.user-avatar {
  width: 42px; height: 42px; border-radius: 50%;
  background: #ffe0e0; border: 2px solid #f0a0a0;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 900; color: #8a1a1a; flex-shrink: 0;
  transition: transform 0.2s ease;
}
.user-card:hover .user-avatar { transform: scale(1.05); }
.user-name { font-size: 14px; font-weight: 800; color: #2a5c1a; }
.user-sub { font-size: 11px; font-weight: 600; color: #7aaa4e; margin-top: 2px; }
.warn-box {
  display: flex; align-items: flex-start; gap: 10px;
  background: #fff5f5; border: 1px solid #f5d0d0;
  border-radius: 10px; padding: 11px 13px; margin-bottom: 14px;
  animation: warnSlideIn 0.35s ease-out 0.1s both;
}
@keyframes warnSlideIn {
  from { opacity: 0; transform: translateX(-8px); }
  to { opacity: 1; transform: translateX(0); }
}
.warn-box p { font-size: 12.5px; font-weight: 600; line-height: 1.6; margin: 0; color: #a04040; }
.warn-box p strong { color: #8a1a1a; font-weight: 800; }
.consecuencias {
  background: #fcf7f7; border: 1px solid #f0d0d0;
  border-radius: 10px; padding: 12px 14px; margin-bottom: 14px;
}
.cons-title { font-size: 12px; font-weight: 800; color: #8a1a1a; margin-bottom: 8px; }
.cons-list { list-style: none; padding: 0; margin: 0; }
.cons-list li {
  display: flex; align-items: center; gap: 8px;
  font-size: 11.5px; font-weight: 600; color: #555;
  padding: 3px 0;
}
.cons-list li strong { color: #8a1a1a; }
.confirm-row {
  display: flex; align-items: center; gap: 9px;
  background: #fff5f5; border: 1px solid #f5d0d0;
  border-radius: 9px; padding: 10px 12px; margin-bottom: 4px; cursor: pointer;
  transition: all 0.2s ease;
}
.confirm-row:hover { box-shadow: 0 2px 8px rgba(193,0,21,0.08); }
.confirm-row:active { transform: scale(0.98); }
.confirm-check {
  width: 18px; height: 18px; border-radius: 5px; background: #fff;
  border: 2px solid #d97b1a;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  transition: all 0.2s ease;
}
.confirm-check.checked { background: #C10015; border-color: #C10015; }
.confirm-txt { font-size: 12px; font-weight: 700; color: #8a4040; }
.dialog-footer {
  display: flex; gap: 10px; padding: 14px 20px 18px;
  border-top: 1px solid #f0d0d0;
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
.btn-accion {
  flex: 2; padding: 11px; background: #C10015; color: #fff;
  border: none; border-radius: 9px; font-size: 14px; font-weight: 800;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 7px;
  box-shadow: 0 4px 14px rgba(193,0,21,0.3);
  transition: all 0.25s ease;
}
.btn-accion:hover:not(.btn-disabled) { background: #a00010; transform: translateY(-1px); box-shadow: 0 6px 20px rgba(193,0,21,0.4); }
.btn-accion:active:not(.btn-disabled) { transform: translateY(0); }
.btn-disabled { opacity: 0.5; cursor: not-allowed; transform: none; box-shadow: none; }
</style>

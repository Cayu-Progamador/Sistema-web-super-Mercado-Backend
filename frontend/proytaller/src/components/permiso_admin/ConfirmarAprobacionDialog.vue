<template>
  <q-dialog v-model="abierto" persistent transition-show="fade" transition-hide="fade">
    <q-card class="dialog-card">
      <div class="accent-bar accent-bar--aprobar"></div>

      <div class="dialog-header dialog-header--aprobar">
        <div class="header-left">
          <div class="warn-icon warn-icon--aprobar">
            <q-icon name="check_circle" size="22px" color="green-8" />
          </div>
          <div>
            <div class="dialog-title">Aprobar Permiso</div>
            <div class="dialog-sub">La solicitud será aprobada</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn close-btn--aprobar" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">
        <div class="user-card">
          <div class="user-avatar user-avatar--aprobar">{{ iniciales }}</div>
          <div>
            <div class="user-name">{{ solicitud.nombreEmpleado }}</div>
            <div class="user-sub">#{{ solicitud.id }} · {{ solicitud.nombreTipo }}</div>
          </div>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">Inicio</span>
            <span class="info-value">{{ solicitud.fechaInicio }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Fin</span>
            <span class="info-value">{{ solicitud.fechaFin || solicitud.fechaInicio }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Días</span>
            <span class="info-value">{{ dias }} día(s)</span>
          </div>
        </div>

        <div class="warn-box warn-box--aprobar">
          <q-icon name="info" size="17px" color="green-7" />
          <p>Al aprobar este permiso, el empleado <strong>quedará exceptuado</strong> de registrar asistencia durante el período seleccionado.</p>
        </div>

        <div class="consecuencias consecuencias--aprobar">
          <div class="cons-title">¿Qué ocurrirá?</div>
          <ul class="cons-list">
            <li><q-icon name="check_circle" size="14px" color="green-7" /> El permiso quedará en estado <strong>Aprobado</strong></li>
            <li><q-icon name="check_circle" size="14px" color="green-7" /> Se registrará en el historial del empleado</li>
            <li><q-icon name="check_circle" size="14px" color="green-7" /> El empleado será notificado</li>
          </ul>
        </div>

        <div class="confirm-row confirm-row--aprobar" @click="confirmado = !confirmado">
          <div class="confirm-check confirm-check--aprobar" :class="{ checked: confirmado }">
            <q-icon v-if="confirmado" name="check" size="11px" color="white" />
          </div>
          <span class="confirm-txt confirm-txt--aprobar">Confirmo que deseo aprobar esta solicitud</span>
        </div>
      </q-card-section>

      <div class="dialog-footer">
        <button class="btn-cancel" @click="cerrar">
          <q-icon name="close" size="15px" />Cancelar
        </button>
        <button
          class="btn-accion--aprobar"
          :class="{ 'btn-disabled': !confirmado || cargando }"
          :disabled="!confirmado || cargando"
          @click="confirmar"
        >
          <q-spinner-dots v-if="cargando" color="white" size="1em" />
          <q-icon v-else name="check_circle" size="16px" />
          {{ cargando ? 'Aprobando...' : 'Aprobar Permiso' }}
        </button>
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  solicitud: { type: Object, default: null },
})

const emit = defineEmits(['update:modelValue', 'confirmar'])

const abierto = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})
const confirmado = ref(false)
const cargando = ref(false)

const iniciales = computed(() => {
  if (!props.solicitud?.nombreEmpleado) return 'E'
  return props.solicitud.nombreEmpleado.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase()
})

const dias = computed(() => {
  if (!props.solicitud) return 1
  if (!props.solicitud.fechaFin) return 1
  const s = new Date(props.solicitud.fechaInicio + 'T12:00:00')
  const e = new Date(props.solicitud.fechaFin + 'T12:00:00')
  return Math.round((e - s) / (1000 * 60 * 60 * 24)) + 1
})

function cerrar() {
  confirmado.value = false
  abierto.value = false
}

async function confirmar() {
  if (!confirmado.value) return
  cargando.value = true
  emit('confirmar', () => {
    confirmado.value = false
    cargando.value = false
  })
}
</script>

<style scoped>
.dialog-card {
  width: 100%;
  max-width: 430px;
  border-radius: 20px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #c8e6c9;
  box-shadow: 0 20px 60px rgba(46,125,50,0.15);
  font-family: 'Nunito', sans-serif;
  animation: dialogPopIn 0.3s ease-out;
}
@keyframes dialogPopIn {
  from { opacity: 0; transform: scale(0.92) translateY(16px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.accent-bar { height: 3px; }
.accent-bar--aprobar { background: linear-gradient(90deg, #2E7D32, #66BB6A, #2E7D32); }
.dialog-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  border-bottom: 1px solid; padding: 16px 20px 14px;
  background: #E8F5E9; border-bottom-color: #A5D6A7;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.warn-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  transition: transform 0.3s ease;
  background: #E8F5E9; border: 1.5px solid #81C784;
}
.header-left:hover .warn-icon { transform: rotate(-10deg) scale(1.05); }
.dialog-title { font-size: 15px; font-weight: 900; color: #1B5E20; margin-bottom: 2px; }
.dialog-sub { font-size: 11px; font-weight: 600; color: #388E3C; }
.close-btn {
  border-radius: 8px; transition: all 0.2s ease;
  color: #388E3C; background: #C8E6C9;
}
.close-btn:hover { background: #A5D6A7; color: #1B5E20; transform: rotate(90deg); }
.dialog-body { padding: 18px 20px 4px; }
.user-card {
  display: flex; align-items: center; gap: 12px;
  background: #F1F8E9; border: 1px solid #C8E6C9;
  border-radius: 12px; padding: 12px 14px; margin-bottom: 14px;
  transition: all 0.2s ease;
}
.user-card:hover { border-color: #81C784; box-shadow: 0 2px 8px rgba(46,125,50,0.06); }
.user-avatar {
  width: 42px; height: 42px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 900; flex-shrink: 0;
  transition: transform 0.2s ease;
  background: #E8F5E9; border: 2px solid #81C784; color: #1B5E20;
}
.user-card:hover .user-avatar { transform: scale(1.05); }
.user-name { font-size: 14px; font-weight: 800; color: #2E7D32; }
.user-sub { font-size: 11px; font-weight: 600; color: #66BB6A; margin-top: 2px; }
.info-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px;
  margin-bottom: 14px;
}
.info-item {
  background: #F1F8E9; border: 1px solid #C8E6C9;
  border-radius: 10px; padding: 10px 12px; text-align: center;
}
.info-label { display: block; font-size: 10px; font-weight: 700; color: #66BB6A; text-transform: uppercase; margin-bottom: 2px; }
.info-value { display: block; font-size: 13px; font-weight: 800; color: #1B5E20; }
.warn-box {
  display: flex; align-items: flex-start; gap: 10px;
  border-radius: 10px; padding: 11px 13px; margin-bottom: 14px;
  animation: warnSlideIn 0.35s ease-out 0.1s both;
  background: #E8F5E9; border: 1px solid #A5D6A7;
}
@keyframes warnSlideIn {
  from { opacity: 0; transform: translateX(-8px); }
  to { opacity: 1; transform: translateX(0); }
}
.warn-box p { font-size: 12.5px; font-weight: 600; line-height: 1.6; margin: 0; color: #2E7D32; }
.warn-box p strong { font-weight: 800; color: #1B5E20; }
.consecuencias {
  border-radius: 10px; padding: 12px 14px; margin-bottom: 14px;
  background: #E8F5E9; border: 1px solid #A5D6A7;
}
.cons-title { font-size: 12px; font-weight: 800; color: #1B5E20; margin-bottom: 8px; }
.cons-list { list-style: none; padding: 0; margin: 0; }
.cons-list li {
  display: flex; align-items: center; gap: 8px;
  font-size: 11.5px; font-weight: 600; color: #2E7D32;
  padding: 3px 0;
}
.confirm-row {
  display: flex; align-items: center; gap: 9px;
  border-radius: 9px; padding: 10px 12px; margin-bottom: 4px; cursor: pointer;
  transition: all 0.2s ease;
  background: #E8F5E9; border: 1px solid #A5D6A7;
}
.confirm-row:hover { box-shadow: 0 2px 8px rgba(46,125,50,0.08); }
.confirm-row:active { transform: scale(0.98); }
.confirm-check {
  width: 18px; height: 18px; border-radius: 5px; background: #fff;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  transition: all 0.2s ease;
  border: 2px solid #388E3C;
}
.confirm-check.checked { background: #2E7D32; border-color: #2E7D32; }
.confirm-txt { font-size: 12px; font-weight: 700; color: #1B5E20; }
.dialog-footer {
  display: flex; gap: 10px; padding: 14px 20px 18px;
  border-top: 1px solid #C8E6C9;
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
.btn-accion--aprobar {
  flex: 2; padding: 11px; background: #2E7D32; color: #fff;
  border: none; border-radius: 9px; font-size: 14px; font-weight: 800;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 7px;
  box-shadow: 0 4px 14px rgba(46,125,50,0.3);
  transition: all 0.25s ease;
}
.btn-accion--aprobar:hover:not(.btn-disabled) { background: #1B5E20; transform: translateY(-1px); box-shadow: 0 6px 20px rgba(46,125,50,0.4); }
.btn-accion--aprobar:active:not(.btn-disabled) { transform: translateY(0); }
.btn-disabled { opacity: 0.5; cursor: not-allowed; transform: none; box-shadow: none; }
</style>

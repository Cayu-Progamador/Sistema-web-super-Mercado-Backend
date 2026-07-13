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
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">
        <div class="user-card">
          <div class="user-avatar">{{ iniciales }}</div>
          <div>
            <div class="user-name">{{ nombre }}</div>
          </div>
        </div>

        <div class="warn-box" :class="`warn-box--${tipo}`">
          <q-icon :name="iconoAlerta" size="17px" :style="{ color: colorIcono }" />
          <p v-html="textoAlerta"></p>
        </div>

        <div class="confirm-row" :class="`confirm-row--${tipo}`" @click="confirmado = !confirmado">
          <div class="confirm-check" :class="[`confirm-check--${tipo}`, { checked: confirmado }]">
            <q-icon v-if="confirmado" name="check" size="11px" color="white" />
          </div>
          <span class="confirm-txt">{{ textoConfirmacion }}</span>
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

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  id: { type: [Number, String], default: null },
  nombre: { type: String, default: '' },
  tipo: { type: String, default: 'activar' }
})

const emit = defineEmits(['update:modelValue', 'confirmar'])

const abierto = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const esActivar = computed(() => props.tipo === 'activar')
const confirmado = ref(false)
const cargando = ref(false)

const icono = computed(() => esActivar.value ? 'check_circle' : 'block')
const colorIcono = computed(() => esActivar.value ? '#4a8c25' : '#d97b1a')
const titulo = computed(() => esActivar.value ? 'Activar Cargo' : 'Desactivar Cargo')
const subtitulo = computed(() => esActivar.value ? 'Esta acción habilitará el cargo' : 'Esta acción deshabilitará el cargo')
const iconoAlerta = computed(() => esActivar.value ? 'info' : 'warning_amber')
const textoAlerta = computed(() => esActivar.value
  ? 'Al activar este cargo, <strong>estará disponible</strong> para ser asignado a empleados.'
  : 'Al desactivar este cargo, <strong>no podrá ser asignado</strong> a nuevos empleados.'
)
const textoConfirmacion = computed(() => esActivar.value
  ? 'Entiendo que el cargo se activará'
  : 'Entiendo que el cargo se desactivará'
)
const textoBoton = computed(() => esActivar.value ? 'Activar Cargo' : 'Desactivar Cargo')
const textoCargando = computed(() => esActivar.value ? 'Activando...' : 'Desactivando...')

const iniciales = computed(() => {
  if (!props.nombre) return 'C'
  return props.nombre.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase()
})

const cerrar = () => {
  confirmado.value = false
  abierto.value = false
}

const confirmar = async () => {
  if (!confirmado.value) return
  cargando.value = true
  await new Promise(r => setTimeout(r, 400))
  cargando.value = false
  emit('confirmar', props.id)
  cerrar()
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
.accent-bar--activar { background: linear-gradient(90deg, #82bd43, #4a8c25, #64992b); }
.accent-bar--desactivar { background: linear-gradient(90deg, #82bd43, #64992b, #d97b1a); }
.dialog-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  border-bottom: 1px solid; padding: 16px 20px 14px;
}
.dialog-header--activar { background: #f0f7e8; border-bottom-color: #c8e0a0; }
.dialog-header--desactivar { background: #fff8f0; border-bottom-color: #f5dbb8; }
.header-left { display: flex; align-items: center; gap: 12px; }
.warn-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  transition: transform 0.3s ease;
}
.header-left:hover .warn-icon {
  transform: rotate(-10deg) scale(1.05);
}
.warn-icon--activar { background: #eaf4d8; border: 1.5px solid #82bd43; }
.warn-icon--desactivar { background: #fef3e2; border: 1.5px solid #f5c97a; }
.dialog-title { font-size: 15px; font-weight: 900; color: #2a5c1a; margin-bottom: 2px; }
.dialog-sub { font-size: 11px; font-weight: 600; }
.dialog-header--activar .dialog-sub { color: #7aaa4e; }
.dialog-header--desactivar .dialog-sub { color: #9dbf78; }
.close-btn {
  border-radius: 8px;
  color: #7aaa4e;
  background: #f0f7e8;
  transition: all 0.2s ease;
}
.close-btn:hover {
  background: #ddecc5;
  color: #4a8c25;
  transform: rotate(90deg);
}
.dialog-body { padding: 18px 20px 4px; }
.user-card {
  display: flex; align-items: center; gap: 12px;
  background: #f7f9f4; border: 1px solid #e4edd8;
  border-radius: 12px; padding: 12px 14px; margin-bottom: 14px;
  transition: all 0.2s ease;
}
.user-card:hover {
  border-color: #c8e0a0;
  box-shadow: 0 2px 8px rgba(42,92,26,0.06);
}
.user-avatar {
  width: 42px; height: 42px; border-radius: 50%;
  background: #eaf4d8; border: 2px solid #c8e0a0;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 900; color: #2a5c1a; flex-shrink: 0;
  transition: transform 0.2s ease;
}
.user-card:hover .user-avatar {
  transform: scale(1.05);
}
.user-name { font-size: 14px; font-weight: 800; color: #2a5c1a; }
.warn-box {
  display: flex; align-items: flex-start; gap: 10px;
  border-radius: 10px; padding: 11px 13px; margin-bottom: 14px;
  animation: warnSlideIn 0.35s ease-out 0.1s both;
}
@keyframes warnSlideIn {
  from { opacity: 0; transform: translateX(-8px); }
  to { opacity: 1; transform: translateX(0); }
}
.warn-box--activar { background: #f0f7e8; border: 1px solid #c8e0a0; }
.warn-box--desactivar { background: #fff8f0; border: 1px solid #f5c97a; }
.warn-box p { font-size: 12.5px; font-weight: 600; line-height: 1.6; margin: 0; }
.warn-box--activar p { color: #4a8c25; }
.warn-box--desactivar p { color: #a05c10; }
.warn-box p strong { color: #2a5c1a; font-weight: 800; }
.confirm-row {
  display: flex; align-items: center; gap: 9px;
  border-radius: 9px; padding: 10px 12px; margin-bottom: 4px; cursor: pointer;
  transition: all 0.2s ease;
}
.confirm-row--activar { background: #f0f7e8; border: 1px solid #c8e0a0; }
.confirm-row--desactivar { background: #fef3e2; border: 1px solid #f5c97a; }
.confirm-row:hover {
  box-shadow: 0 2px 8px rgba(42,92,26,0.08);
}
.confirm-row:active {
  transform: scale(0.98);
}
.confirm-check {
  width: 18px; height: 18px; border-radius: 5px; background: #fff;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  transition: all 0.2s ease;
}
.confirm-check--activar { border: 2px solid #82bd43; }
.confirm-check--desactivar { border: 2px solid #d97b1a; }
.confirm-check--activar.checked { background: #82bd43; border-color: #82bd43; }
.confirm-check--desactivar.checked { background: #d97b1a; border-color: #d97b1a; }
.confirm-txt { font-size: 12px; font-weight: 700; color: #4a8c25; }
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
  flex: 2; padding: 11px; background: #82bd43; color: #fff;
  border: none; border-radius: 9px; font-size: 14px; font-weight: 800;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 7px;
  box-shadow: 0 4px 14px rgba(74,140,37,0.3);
  transition: all 0.25s ease;
}
.btn-accion--activar:hover:not(.btn-disabled) { background: #4a8c25; transform: translateY(-1px); box-shadow: 0 6px 20px rgba(74,140,37,0.4); }
.btn-accion--activar:active:not(.btn-disabled) { transform: translateY(0); }
.btn-accion--desactivar {
  flex: 2; padding: 11px; background: #d97b1a; color: #fff;
  border: none; border-radius: 9px; font-size: 14px; font-weight: 800;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 7px;
  box-shadow: 0 4px 14px rgba(217,123,26,0.3);
  transition: all 0.25s ease;
}
.btn-accion--desactivar:hover:not(.btn-disabled) { background: #c06a10; transform: translateY(-1px); box-shadow: 0 6px 20px rgba(217,123,26,0.4); }
.btn-accion--desactivar:active:not(.btn-disabled) { transform: translateY(0); }
.btn-disabled { opacity: 0.5; cursor: not-allowed; transform: none; box-shadow: none; }
</style>

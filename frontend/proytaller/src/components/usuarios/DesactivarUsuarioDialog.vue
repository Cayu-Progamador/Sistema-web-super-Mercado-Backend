<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card">

      <div class="accent-bar"></div>

      <div class="dialog-header">
        <div class="header-left">
          <div class="warn-icon">
            <q-icon name="person_off" size="22px" style="color:#d97b1a" />
          </div>
          <div>
            <div class="dialog-title">Desactivar Usuario</div>
            <div class="dialog-sub">Esta acción suspenderá el acceso al sistema</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">

        <div class="user-card">
          <div class="user-avatar">{{ iniciales }}</div>
          <div>
            <div class="user-name">{{ nombre }}</div>
            <div class="user-meta">
              <span class="user-role">{{ rolFormateado }}</span>
            </div>
          </div>
        </div>

        <div class="warn-box">
          <q-icon name="warning_amber" size="17px" style="color:#d97b1a;flex-shrink:0;margin-top:1px" />
          <p>Al desactivar este usuario, <strong>perderá acceso inmediato</strong> al sistema. Podrás reactivarlo en cualquier momento desde la gestión de usuarios.</p>
        </div>

        <div class="consecuencias">
          <div class="cons-title">¿Qué ocurrirá?</div>
          <div class="cons-list">
            <div class="cons-item">
              <span class="cons-dot"></span>No podrá iniciar sesión en el sistema
            </div>
            <div class="cons-item">
              <span class="cons-dot"></span>Sus sesiones activas serán cerradas
            </div>
            <div class="cons-item">
              <span class="cons-dot"></span>Sus datos y registros se conservarán
            </div>
            <div class="cons-item">
              <span class="cons-dot"></span>El rol y permisos quedarán suspendidos
            </div>
          </div>
        </div>

        <div class="confirm-row" @click="confirmado = !confirmado">
          <div class="confirm-check" :class="{ checked: confirmado }">
            <q-icon v-if="confirmado" name="check" size="11px" color="white" />
          </div>
          <span class="confirm-txt">Entiendo que el usuario perderá acceso inmediatamente</span>
        </div>

      </q-card-section>

      <div class="dialog-footer">
        <button class="btn-cancel" @click="cerrar">
          <q-icon name="close" size="15px" />Cancelar
        </button>
        <button
          class="btn-desactivar"
          :class="{ 'btn-disabled': !confirmado || cargando }"
          :disabled="!confirmado || cargando"
          @click="confirmar"
        >
          <q-spinner-dots v-if="cargando" color="white" size="1em" />
          <q-icon v-else name="person_off" size="16px" />
          {{ cargando ? 'Desactivando...' : 'Desactivar Usuario' }}
        </button>
      </div>

    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  id:         { type: [Number, String], default: null },
  nombre:     { type: String, default: '' },
  rol:        { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue', 'confirmar'])

const abierto = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const confirmado = ref(false)
const cargando   = ref(false)

const iniciales = computed(() => {
  if (!props.nombre) return 'U'
  return props.nombre.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase()
})

const rolFormateado = computed(() => {
  if (!props.rol) return '—'
  const s = props.rol.replace('ROLE_', '')
  return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase()
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
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

.dialog-card {
  width: 100% !important;
  max-width: 430px !important;
  border-radius: 20px !important;
  overflow: hidden !important;
  background: #ffffff !important;
  border: 1px solid #e4edd8 !important;
  box-shadow: 0 20px 60px rgba(42,92,26,0.15) !important;
  font-family: 'Nunito', sans-serif;
}

.accent-bar {
  height: 3px;
  background: linear-gradient(90deg, #82bd43, #64992b, #d97b1a);
}

.dialog-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  background: #fff8f0;
  border-bottom: 1px solid #f5dbb8;
  padding: 16px 20px 14px;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.warn-icon {
  width: 44px; height: 44px; border-radius: 12px;
  background: #fef3e2; border: 1.5px solid #f5c97a;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.dialog-title {
  font-size: 15px; font-weight: 900; color: #2a5c1a;
  font-family: 'Nunito', sans-serif; margin-bottom: 2px;
}
.dialog-sub {
  font-size: 11px; font-weight: 600; color: #9dbf78;
  font-family: 'Nunito', sans-serif;
}
.close-btn {
  color: #9dbf78 !important;
  background: #f0f7e8 !important;
  border-radius: 8px !important;
}
.close-btn:hover { background: #ddecc5 !important; color: #4a8c25 !important; }

.dialog-body { padding: 18px 20px 4px !important; }

.user-card {
  display: flex; align-items: center; gap: 12px;
  background: #f7f9f4; border: 1px solid #e4edd8;
  border-radius: 12px; padding: 12px 14px;
  margin-bottom: 14px;
}
.user-avatar {
  width: 42px; height: 42px; border-radius: 50%;
  background: #eaf4d8; border: 2px solid #c8e0a0;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 900; color: #2a5c1a;
  flex-shrink: 0; font-family: 'Nunito', sans-serif;
}
.user-name {
  font-size: 14px; font-weight: 800; color: #2a5c1a;
  margin-bottom: 3px; font-family: 'Nunito', sans-serif;
}
.user-meta { display: flex; align-items: center; gap: 8px; }
.user-role {
  font-size: 11px; font-weight: 700; color: #7aaa4e;
  background: #eaf4d8; border: 1px solid #c8e0a0;
  padding: 2px 8px; border-radius: 20px;
  font-family: 'Nunito', sans-serif;
}
.user-email {
  font-size: 11.5px; font-weight: 600; color: #9dbf78;
  font-family: 'Nunito', sans-serif;
}

.warn-box {
  display: flex; align-items: flex-start; gap: 10px;
  background: #fff8f0; border: 1px solid #f5c97a;
  border-radius: 10px; padding: 11px 13px;
  margin-bottom: 14px;
}
.warn-box p {
  font-size: 12.5px; font-weight: 600; color: #a05c10;
  line-height: 1.6; font-family: 'Nunito', sans-serif;
}
.warn-box p strong { color: #7a3f05; font-weight: 800; }

.consecuencias { margin-bottom: 14px; }
.cons-title {
  font-size: 10px; font-weight: 800; color: #7aaa4e;
  text-transform: uppercase; letter-spacing: 0.1em;
  margin-bottom: 8px; font-family: 'Nunito', sans-serif;
}
.cons-list { display: flex; flex-direction: column; gap: 6px; }
.cons-item {
  display: flex; align-items: center; gap: 8px;
  font-size: 12.5px; font-weight: 600; color: #5a8040;
  font-family: 'Nunito', sans-serif;
}
.cons-dot {
  width: 6px; height: 6px; border-radius: 50%;
  background: #d97b1a; flex-shrink: 0;
}

.confirm-row {
  display: flex; align-items: center; gap: 9px;
  background: #fef3e2; border: 1px solid #f5c97a;
  border-radius: 9px; padding: 10px 12px;
  margin-bottom: 4px; cursor: pointer;
}
.confirm-check {
  width: 18px; height: 18px; border-radius: 5px;
  border: 2px solid #d97b1a; background: #fff;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; transition: all 0.2s;
}
.confirm-check.checked { background: #d97b1a; border-color: #d97b1a; }
.confirm-txt {
  font-size: 12px; font-weight: 700; color: #a05c10;
  font-family: 'Nunito', sans-serif;
}

.dialog-footer {
  display: flex; gap: 10px;
  padding: 14px 20px 18px;
  border-top: 1px solid #e4edd8;
}
.btn-cancel {
  flex: 1; padding: 11px;
  background: #fff; color: #5a5a5a;
  border: 1.5px solid #d0d0d0; border-radius: 9px;
  font-family: 'Nunito', sans-serif;
  font-size: 14px; font-weight: 700;
  cursor: pointer; transition: all 0.2s;
  display: flex; align-items: center; justify-content: center; gap: 6px;
}
.btn-cancel:hover { background: #f7f7f7; border-color: #bbb; }

.btn-desactivar {
  flex: 2; padding: 11px;
  background: #d97b1a; 
  color: #fff;
  border: none; border-radius: 9px;
  font-family: 'Nunito', sans-serif;
  font-size: 14px; font-weight: 800;
  cursor: pointer;
  display: flex; 
  align-items: center; 
  justify-content: center; 
  gap: 7px;
  box-shadow: 0 4px 14px rgba(217,123,26,0.3);
  transition: all 0.2s;
}
.btn-desactivar:hover:not(.btn-disabled) {
  background: #c06a10; transform: translateY(-1px);
}
.btn-disabled {
  opacity: 0.5 !important;
  cursor: not-allowed !important;
  transform: none !important;
  box-shadow: none !important;
}
</style>
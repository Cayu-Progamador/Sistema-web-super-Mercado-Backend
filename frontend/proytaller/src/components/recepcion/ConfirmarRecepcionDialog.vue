<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="anular-card">
      <div class="anular-accent"></div>

      <div class="anular-header">
        <div class="header-left">
          <div class="warn-icon">
            <q-icon name="block" size="22px" color="#d32f2f" />
          </div>
          <div>
            <div class="dialog-title">Anular Recepción</div>
            <div class="dialog-sub">Esta acción invalidará la recepción</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">
        <div class="user-card">
          <div class="user-avatar">{{ iniciales }}</div>
          <div>
            <div class="user-name">{{ numero }}</div>
            <div class="user-detail">{{ proveedor }}</div>
          </div>
        </div>

        <div class="warn-box">
          <q-icon name="warning_amber" size="17px" color="#d32f2f" />
          <p v-html="textoAlerta"></p>
        </div>

        <div class="confirm-row" @click="confirmado = !confirmado">
          <div class="confirm-check" :class="{ checked: confirmado }">
            <q-icon v-if="confirmado" name="check" size="11px" color="white" />
          </div>
          <span class="confirm-txt">Entiendo que la recepción se anulará</span>
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
          <q-icon v-else name="block" size="16px" />
          {{ cargando ? 'Anulando...' : 'Anular Recepción' }}
        </button>
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  recepcion: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'confirmar'])

const abierto = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const confirmado = ref(false)
const cargando = ref(false)

const numero = computed(() => props.recepcion?.numero || '')
const proveedor = computed(() => props.recepcion?.proveedor || 'Proveedor no disponible')

const textoAlerta = 'Al anular esta recepción, <strong>la mercadería no ingresará al inventario</strong> y el estado cambiará a <strong>ANULADA</strong>. Esta acción no se puede deshacer.'

const iniciales = computed(() => {
  const n = proveedor.value
  if (!n) return 'P'
  return n.split(' ').slice(0, 2).map((x) => x[0]).join('').toUpperCase()
})

const cerrar = () => {
  confirmado.value = false
  abierto.value = false
}

const confirmar = async () => {
  if (!confirmado.value) return
  cargando.value = true
  await new Promise((r) => setTimeout(r, 400))
  cargando.value = false
  emit('confirmar', props.recepcion)
  cerrar()
}
</script>

<style scoped>
.anular-card {
  width: 100%;
  max-width: 430px;
  border-radius: 20px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #f2d6d6;
  box-shadow: 0 20px 60px rgba(120, 30, 30, 0.18);
  font-family: 'Nunito', sans-serif;
}
.anular-accent {
  height: 3px;
  background: linear-gradient(90deg, #e57373, #d32f2f, #b71c1c);
}
.anular-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  border-bottom: 1px solid #f0c9c9;
  padding: 16px 20px 14px;
  background: #fdf3f3;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.warn-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #fdeaea;
  border: 1.5px solid #ef9a9a;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.dialog-title {
  font-size: 15px;
  font-weight: 900;
  color: #8c1d1d;
  margin-bottom: 2px;
}
.dialog-sub {
  font-size: 11px;
  font-weight: 600;
  color: #c47575;
}
.close-btn {
  border-radius: 8px !important;
  color: #b34a4a !important;
  background: #fdeaea !important;
}
.close-btn:hover {
  background: #f7d4d4 !important;
  color: #8c1d1d !important;
}
.dialog-body {
  padding: 18px 20px 4px !important;
}
.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fbf6f6;
  border: 1px solid #f0d9d9;
  border-radius: 12px;
  padding: 12px 14px;
  margin-bottom: 14px;
}
.user-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #fdeaea;
  border: 2px solid #efb3b3;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 900;
  color: #8c1d1d;
  flex-shrink: 0;
}
.user-name {
  font-size: 14px;
  font-weight: 800;
  color: #8c1d1d;
}
.user-detail {
  font-size: 12px;
  font-weight: 600;
  color: #a05c5c;
  margin-top: 1px;
}
.warn-box {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  border-radius: 10px;
  padding: 11px 13px;
  margin-bottom: 14px;
  background: #fdf3f3;
  border: 1px solid #f0c9c9;
}
.warn-box p {
  font-size: 12.5px;
  font-weight: 600;
  line-height: 1.6;
  margin: 0;
  color: #a43a3a;
}
.warn-box p strong {
  color: #8c1d1d;
  font-weight: 800;
}
.confirm-row {
  display: flex;
  align-items: center;
  gap: 9px;
  border-radius: 9px;
  padding: 10px 12px;
  margin-bottom: 4px;
  cursor: pointer;
  background: #fdf3f3;
  border: 1px solid #f0c9c9;
}
.confirm-check {
  width: 18px;
  height: 18px;
  border-radius: 5px;
  background: #fff;
  border: 2px solid #d32f2f;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.confirm-check.checked {
  background: #d32f2f;
  border-color: #d32f2f;
}
.confirm-txt {
  font-size: 12px;
  font-weight: 700;
  color: #a43a3a;
}
.dialog-footer {
  display: flex;
  gap: 10px;
  padding: 14px 20px 18px;
  border-top: 1px solid #f0d9d9;
}
.btn-cancel {
  flex: 1;
  padding: 11px;
  background: #fff;
  color: #5a5a5a;
  border: 1.5px solid #d0d0d0;
  border-radius: 9px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}
.btn-cancel:hover {
  background: #f7f7f7;
  border-color: #bbb;
}
.btn-accion {
  flex: 2;
  padding: 11px;
  background: #d32f2f;
  color: #fff;
  border: none;
  border-radius: 9px;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  box-shadow: 0 4px 14px rgba(211, 47, 47, 0.3);
}
.btn-accion:hover:not(.btn-disabled) {
  background: #b71c1c;
  transform: translateY(-1px);
}
.btn-disabled {
  opacity: 0.5 !important;
  cursor: not-allowed !important;
  transform: none !important;
  box-shadow: none !important;
}
</style>
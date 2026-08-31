<template>
  <q-dialog v-model="visible" persistent>
    <q-card class="dialog-card">
      <div class="accent-bar accent-bar--cancelar"></div>

      <div class="dialog-header dialog-header--cancelar">
        <div class="header-left">
          <div class="warn-icon warn-icon--cancelar">
            <q-icon name="cancel" size="22px" color="#d97b1a" />
          </div>
          <div>
            <div class="dialog-title">Cancelar Pedido</div>
            <div class="dialog-sub">Esta acción anulará el pedido</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">
        <div class="user-card">
          <div class="user-avatar">
            <q-icon name="shopping_cart" size="18px" color="#2a5c1a" />
          </div>
          <div>
            <div class="user-name">Pedido #{{ pedido?.numero }}</div>
            <div class="user-sub">{{ pedido?.proveedor }} · {{ pedido?.fechaPedido ? formatFecha(pedido.fechaPedido) : '' }}</div>
          </div>
        </div>

        <div class="warn-box warn-box--cancelar">
          <q-icon name="warning_amber" size="17px" color="#d97b1a" />
          <p v-html="textoAlerta"></p>
        </div>

        <div class="confirm-row confirm-row--cancelar" @click="confirmado = !confirmado">
          <div class="confirm-check confirm-check--cancelar" :class="{ checked: confirmado }">
            <q-icon v-if="confirmado" name="check" size="11px" color="white" />
          </div>
          <span class="confirm-txt">Entiendo que el pedido se cancelará</span>
        </div>
      </q-card-section>

      <div class="dialog-footer">
        <button class="btn-cancel" @click="cerrar">
          <q-icon name="close" size="15px" />No
        </button>
        <button
          class="btn-accion--cancelar"
          :class="{ 'btn-disabled': !confirmado || cargando }"
          :disabled="!confirmado || cargando"
          @click="confirmar"
        >
          <q-spinner-dots v-if="cargando" color="white" size="1em" />
          <q-icon v-else name="cancel" size="16px" />
          {{ cargando ? 'Cancelando...' : 'Sí, cancelar pedido' }}
        </button>
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  pedido: { type: Object, default: null },
  cargando: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'confirmar'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const confirmado = ref(false)

const textoAlerta = 'Al cancelar el pedido, <strong>quedará anulado</strong> y ya no podrá ser recibido ni modificado.'

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

const cerrar = () => {
  confirmado.value = false
  visible.value = false
}

const confirmar = () => {
  if (!confirmado.value) return
  emit('confirmar', props.pedido)
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
}
.accent-bar { height: 3px; }
.accent-bar--cancelar { background: linear-gradient(90deg, #82bd43, #64992b, #d97b1a); }
.dialog-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  border-bottom: 1px solid; padding: 16px 20px 14px;
}
.dialog-header--cancelar { background: #fff8f0; border-bottom-color: #f5dbb8; }
.header-left { display: flex; align-items: center; gap: 12px; }
.warn-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.warn-icon--cancelar { background: #fef3e2; border: 1.5px solid #f5c97a; }
.dialog-title { font-size: 15px; font-weight: 900; color: #2a5c1a; margin-bottom: 2px; }
.dialog-sub { font-size: 11px; font-weight: 600; color: #9dbf78; }
.close-btn { border-radius: 8px !important; color: #d97b1a !important; background: #fef3e2 !important; }
.close-btn:hover { background: #f8e3c0 !important; color: #c06a10 !important; }
.dialog-body { padding: 18px 20px 4px !important; }
.user-card {
  display: flex; align-items: center; gap: 12px;
  background: #f7f9f4; border: 1px solid #e4edd8;
  border-radius: 12px; padding: 12px 14px; margin-bottom: 14px;
}
.user-avatar {
  width: 42px; height: 42px; border-radius: 50%;
  background: #eaf4d8; border: 2px solid #c8e0a0;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 900; color: #2a5c1a; flex-shrink: 0;
}
.user-name { font-size: 14px; font-weight: 800; color: #2a5c1a; }
.user-sub { font-size: 12px; font-weight: 600; color: #7aaa4e; margin-top: 2px; }
.warn-box {
  display: flex; align-items: flex-start; gap: 10px;
  border-radius: 10px; padding: 11px 13px; margin-bottom: 14px;
}
.warn-box--cancelar { background: #fff8f0; border: 1px solid #f5c97a; }
.warn-box p { font-size: 12.5px; font-weight: 600; line-height: 1.6; margin: 0; color: #a05c10; }
.warn-box p strong { color: #2a5c1a; font-weight: 800; }
.confirm-row {
  display: flex; align-items: center; gap: 9px;
  border-radius: 9px; padding: 10px 12px; margin-bottom: 4px; cursor: pointer;
}
.confirm-row--cancelar { background: #fef3e2; border: 1px solid #f5c97a; }
.confirm-check {
  width: 18px; height: 18px; border-radius: 5px; background: #fff;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.confirm-check--cancelar { border: 2px solid #d97b1a; }
.confirm-check--cancelar.checked { background: #d97b1a; border-color: #d97b1a; }
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
}
.btn-cancel:hover { background: #f7f7f7; border-color: #bbb; }
.btn-accion--cancelar {
  flex: 2; padding: 11px; background: #d97b1a; color: #fff;
  border: none; border-radius: 9px; font-size: 14px; font-weight: 800;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 7px;
  box-shadow: 0 4px 14px rgba(217,123,26,0.3);
}
.btn-accion--cancelar:hover:not(.btn-disabled) { background: #c06a10; transform: translateY(-1px); }
.btn-disabled { opacity: 0.5 !important; cursor: not-allowed !important; transform: none !important; box-shadow: none !important; }
</style>
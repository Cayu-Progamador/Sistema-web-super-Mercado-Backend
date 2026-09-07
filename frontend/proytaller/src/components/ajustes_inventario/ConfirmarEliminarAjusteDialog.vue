<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card">
      <div class="accent-bar" />
      <div class="dialog-header">
        <div class="header-left">
          <div class="warn-icon"><q-icon name="delete_forever" size="22px" /></div>
          <div><div class="dialog-title">Eliminar borrador</div><div class="dialog-sub">Esta acción no se puede deshacer</div></div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>
      <q-card-section class="dialog-body">
        <div class="adjustment-card">
          <div class="adjustment-avatar"><q-icon name="tune" size="20px" /></div>
          <div><div class="adjustment-name">{{ ajuste?.codigo || 'Ajuste de inventario' }}</div><div class="adjustment-sub">{{ ajuste?.motivo || 'Borrador' }}</div></div>
        </div>
        <div class="warn-box"><q-icon name="warning_amber" size="17px" /><p>Se eliminará el borrador y sus productos. <strong>El stock no será modificado.</strong></p></div>
        <div class="confirm-row" @click="confirmado = !confirmado">
          <div class="confirm-check" :class="{ checked: confirmado }"><q-icon v-if="confirmado" name="check" size="11px" color="white" /></div>
          <span class="confirm-txt">Entiendo que este borrador será eliminado</span>
        </div>
      </q-card-section>
      <div class="dialog-footer">
        <button class="btn-cancel" @click="cerrar"><q-icon name="close" size="15px" />Cancelar</button>
        <button class="btn-delete" :class="{ 'btn-disabled': !confirmado || loading }" :disabled="!confirmado || loading" @click="$emit('confirmar', ajuste)">
          <q-spinner-dots v-if="loading" color="white" size="1em" /><q-icon v-else name="delete" size="16px" />{{ loading ? 'Eliminando...' : 'Eliminar borrador' }}
        </button>
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({ modelValue: Boolean, ajuste: Object, loading: Boolean })
const emit = defineEmits(['update:modelValue', 'confirmar'])
const confirmado = ref(false)
const abierto = computed({ get: () => props.modelValue, set: (value) => emit('update:modelValue', value) })
watch(() => props.modelValue, (value) => { if (value) confirmado.value = false })
function cerrar() { confirmado.value = false; abierto.value = false }
</script>

<style scoped>
.dialog-card { width: 100%; max-width: 430px; border-radius: 20px; overflow: hidden; background: #fff; border: 1px solid #e4edd8; box-shadow: 0 20px 60px rgba(42,92,26,.15) }
.accent-bar { height: 3px; background: linear-gradient(90deg, #82bd43, #64992b, #d97b1a) }
.dialog-header { display: flex; align-items: flex-start; justify-content: space-between; background: #fff8f0; border-bottom: 1px solid #f5dbb8; padding: 16px 20px 14px }
.header-left { display: flex; align-items: center; gap: 12px }.warn-icon { width: 44px; height: 44px; border-radius: 12px; display: flex; align-items: center; justify-content: center; background: #fef3e2; border: 1.5px solid #f5c97a; color: #d97b1a }.dialog-title { font-size: 15px; font-weight: 900; color: #2a5c1a; margin-bottom: 2px }.dialog-sub { font-size: 11px; font-weight: 600; color: #9dbf78 }.close-btn { border-radius: 8px !important; color: #7aaa4e !important; background: #f0f7e8 !important }.dialog-body { padding: 18px 20px 4px !important }
.adjustment-card { display: flex; align-items: center; gap: 12px; background: #f7f9f4; border: 1px solid #e4edd8; border-radius: 12px; padding: 12px 14px; margin-bottom: 14px }.adjustment-avatar { width: 42px; height: 42px; border-radius: 50%; background: #eaf4d8; border: 2px solid #c8e0a0; display: flex; align-items: center; justify-content: center; color: #2a5c1a }.adjustment-name { font-size: 14px; font-weight: 800; color: #2a5c1a }.adjustment-sub { font-size: 11px; color: #7b857c; margin-top: 2px }.warn-box { display: flex; align-items: flex-start; gap: 10px; border-radius: 10px; padding: 11px 13px; margin-bottom: 14px; background: #fff8f0; border: 1px solid #f5c97a; color: #d97b1a }.warn-box p { font-size: 12.5px; font-weight: 600; line-height: 1.6; margin: 0; color: #a05c10 }.warn-box p strong { color: #2a5c1a }.confirm-row { display: flex; align-items: center; gap: 9px; border-radius: 9px; padding: 10px 12px; margin-bottom: 4px; cursor: pointer; background: #fef3e2; border: 1px solid #f5c97a }.confirm-check { width: 18px; height: 18px; border-radius: 5px; background: #fff; border: 2px solid #d97b1a; display: flex; align-items: center; justify-content: center; flex-shrink: 0 }.confirm-check.checked { background: #d97b1a; border-color: #d97b1a }.confirm-txt { font-size: 12px; font-weight: 700; color: #4a8c25 }.dialog-footer { display: flex; gap: 10px; padding: 14px 20px 18px; border-top: 1px solid #e4edd8 }.btn-cancel, .btn-delete { flex: 1; padding: 11px; border-radius: 9px; font-size: 14px; font-weight: 700; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 6px }.btn-cancel { background: #fff; color: #5a5a5a; border: 1.5px solid #d0d0d0 }.btn-delete { flex: 2; background: #d97b1a; color: #fff; border: none; font-weight: 800; box-shadow: 0 4px 14px rgba(217,123,26,.3) }.btn-disabled { opacity: .5; cursor: not-allowed; box-shadow: none }.btn-delete:hover:not(.btn-disabled) { background: #c06a10 }
</style>

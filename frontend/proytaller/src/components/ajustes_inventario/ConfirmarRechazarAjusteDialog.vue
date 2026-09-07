<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card">
      <div class="accent-bar" />
      <div class="dialog-header">
        <div class="header-left"><div class="warn-icon"><q-icon name="block" size="22px" /></div><div><div class="dialog-title">Rechazar ajuste</div><div class="dialog-sub">El motivo es obligatorio</div></div></div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>
      <q-card-section class="dialog-body">
        <div class="adjustment-card"><div class="adjustment-avatar"><q-icon name="tune" size="20px" /></div><div><div class="adjustment-name">{{ ajuste?.codigo || 'Ajuste de inventario' }}</div><div class="adjustment-sub">{{ ajuste?.tipo || '--' }} · {{ texto(ajuste?.motivo) }}</div></div></div>
        <div class="warn-box"><q-icon name="warning_amber" size="17px" /><p>El ajuste será marcado como <strong>RECHAZADO</strong> y no podrá aplicarse al stock.</p></div>
        <q-input v-model="motivo" outlined type="textarea" autogrow maxlength="500" counter label="Motivo del rechazo *" hint="Explica por qué no se aprueba este ajuste." :error="mostrarError" error-message="El motivo es obligatorio." />
        <div class="confirm-row" @click="confirmado = !confirmado"><div class="confirm-check" :class="{ checked: confirmado }"><q-icon v-if="confirmado" name="check" size="11px" color="white" /></div><span>Confirmo que deseo rechazar este ajuste</span></div>
      </q-card-section>
      <div class="dialog-footer"><q-btn flat no-caps label="Cancelar" icon="close" color="grey-7" @click="cerrar" /><q-btn unelevated no-caps color="orange-8" icon="block" label="Rechazar ajuste" :disable="!puedeConfirmar || loading" :loading="loading" @click="confirmar" /></div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
const props = defineProps({ modelValue: Boolean, ajuste: Object, loading: Boolean })
const emit = defineEmits(['update:modelValue', 'confirmar'])
const motivo = ref('')
const confirmado = ref(false)
const mostrarError = ref(false)
const abierto = computed({ get: () => props.modelValue, set: (value) => emit('update:modelValue', value) })
const puedeConfirmar = computed(() => Boolean(motivo.value.trim() && confirmado.value))
watch(() => props.modelValue, (value) => { if (value) { motivo.value = ''; confirmado.value = false; mostrarError.value = false } })
const texto = (value) => value?.replaceAll('_', ' ') || '--'
function cerrar() { motivo.value = ''; confirmado.value = false; mostrarError.value = false; abierto.value = false }
function confirmar() { if (!puedeConfirmar.value) { mostrarError.value = !motivo.value.trim(); return }; emit('confirmar', { ajuste: props.ajuste, motivo: motivo.value.trim() }) }
</script>

<style scoped>
.dialog-card { width: 100%; max-width: 520px; border-radius: 20px; overflow: hidden; background: #fff; border: 1px solid #e4edd8; box-shadow: 0 20px 60px rgba(42,92,26,.15) }.accent-bar { height: 3px; background: linear-gradient(90deg, #82bd43, #d97b1a, #e6a03b) }.dialog-header { display: flex; align-items: flex-start; justify-content: space-between; background: #fff8f0; border-bottom: 1px solid #f5dbb8; padding: 16px 20px 14px }.header-left { display: flex; align-items: center; gap: 12px }.warn-icon { width: 44px; height: 44px; border-radius: 12px; display: grid; place-items: center; background: #fef3e2; border: 1.5px solid #f5c97a; color: #d97b1a }.dialog-title { font-size: 15px; font-weight: 900; color: #2a5c1a }.dialog-sub { font-size: 11px; color: #9dbf78; font-weight: 600 }.close-btn { color: #7aaa4e !important; background: #f0f7e8 !important }.dialog-body { padding: 18px 20px 10px !important }.adjustment-card { display: flex; align-items: center; gap: 12px; background: #f7f9f4; border: 1px solid #e4edd8; border-radius: 12px; padding: 12px 14px; margin-bottom: 14px }.adjustment-avatar { width: 42px; height: 42px; border-radius: 50%; background: #eaf4d8; border: 2px solid #c8e0a0; display: grid; place-items: center; color: #2a5c1a }.adjustment-name { font-size: 14px; font-weight: 800; color: #2a5c1a }.adjustment-sub { font-size: 11px; color: #7b857c; margin-top: 2px }.warn-box { display: flex; gap: 10px; align-items: flex-start; margin-bottom: 14px; padding: 11px 13px; border: 1px solid #f5c97a; border-radius: 10px; background: #fff8f0; color: #d97b1a }.warn-box p { margin: 0; color: #a05c10; font-size: 12.5px; line-height: 1.6 }.warn-box strong { color: #2a5c1a }.confirm-row { display: flex; align-items: center; gap: 9px; margin-top: 14px; padding: 10px 12px; border: 1px solid #f5c97a; border-radius: 9px; background: #fef3e2; cursor: pointer; color: #4a8c25; font-size: 12px; font-weight: 700 }.confirm-check { width: 18px; height: 18px; border: 2px solid #d97b1a; border-radius: 5px; background: #fff; display: grid; place-items: center; flex-shrink: 0 }.confirm-check.checked { background: #d97b1a }.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 14px 20px 18px; border-top: 1px solid #e4edd8 }
@media (max-width: 600px) { .dialog-card { width: 100vw; max-width: 100vw; border-radius: 0 } .dialog-footer { flex-wrap: wrap }.dialog-footer .q-btn { flex: 1 } }
</style>

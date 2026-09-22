<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card">
      <div class="accent-bar" :class="`accent-bar--${tipo}`"></div>
      <div class="dialog-header" :class="`dialog-header--${tipo}`">
        <div class="header-left">
          <div class="warn-icon" :class="`warn-icon--${tipo}`"><q-icon :name="icono" size="22px" :style="{ color: colorIcono }" /></div>
          <div><div class="dialog-title">{{ titulo }}</div><div class="dialog-sub">{{ subtitulo }}</div></div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>
      <q-card-section class="dialog-body">
        <div class="client-card"><div class="client-avatar"><q-icon name="person" size="20px" /></div><div><div class="client-name">{{ nombre }}</div><div class="client-ci">CI: {{ cliente?.ci || '--' }}</div></div></div>
        <div class="warn-box" :class="`warn-box--${tipo}`"><q-icon :name="iconoAlerta" size="17px" :style="{ color: colorIcono }" /><p v-html="textoAlerta"></p></div>
        <div class="confirm-row" :class="`confirm-row--${tipo}`" @click="confirmado = !confirmado"><div class="confirm-check" :class="[`confirm-check--${tipo}`, { checked: confirmado }]"><q-icon v-if="confirmado" name="check" size="11px" color="white" /></div><span class="confirm-txt">{{ textoConfirmacion }}</span></div>
      </q-card-section>
      <div class="dialog-footer"><button class="btn-cancel" @click="cerrar"><q-icon name="close" size="15px" />Cancelar</button><button :class="[`btn-accion--${tipo}`, { 'btn-disabled': !confirmado || cargando }]" :disabled="!confirmado || cargando" @click="confirmar"><q-spinner-dots v-if="cargando" color="white" size="1em" /><q-icon v-else :name="icono" size="16px" />{{ cargando ? textoCargando : textoBoton }}</button></div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
const props = defineProps({ modelValue: Boolean, cliente: { type: Object, default: null } })
const emit = defineEmits(['update:modelValue', 'confirmar'])
const abierto = computed({ get: () => props.modelValue, set: value => emit('update:modelValue', value) })
const esActivar = computed(() => !props.cliente?.estado), tipo = computed(() => esActivar.value ? 'activar' : 'desactivar')
const confirmado = ref(false), cargando = ref(false)
const nombre = computed(() => [props.cliente?.nombres, props.cliente?.apellidoPaterno, props.cliente?.apellidoMaterno].filter(Boolean).join(' '))
const icono = computed(() => esActivar.value ? 'check_circle' : 'block'), colorIcono = computed(() => esActivar.value ? '#4a8c25' : '#d97b1a')
const titulo = computed(() => esActivar.value ? 'Activar Cliente' : 'Desactivar Cliente'), subtitulo = computed(() => esActivar.value ? 'Esta acción habilitará al cliente' : 'Esta acción deshabilitará al cliente')
const iconoAlerta = computed(() => esActivar.value ? 'info' : 'warning_amber')
const textoAlerta = computed(() => esActivar.value ? 'Al activar este cliente, <strong>podrá seleccionarse en nuevas ventas</strong>.' : 'Al desactivar este cliente, <strong>no podrá seleccionarse en nuevas ventas</strong>.')
const textoConfirmacion = computed(() => esActivar.value ? 'Entiendo que el cliente se activará' : 'Entiendo que el cliente se desactivará')
const textoBoton = computed(() => esActivar.value ? 'Activar Cliente' : 'Desactivar Cliente'), textoCargando = computed(() => esActivar.value ? 'Activando...' : 'Desactivando...')
watch(() => props.modelValue, value => { if (value) confirmado.value = false })
function cerrar() { confirmado.value = false; abierto.value = false }
async function confirmar() { if (!confirmado.value) return; cargando.value = true; await new Promise(resolve => setTimeout(resolve, 300)); cargando.value = false; emit('confirmar', props.cliente); cerrar() }
</script>

<style scoped>
.dialog-card{width:100%;max-width:430px;border-radius:20px;overflow:hidden;background:#fff;border:1px solid #e4edd8;box-shadow:0 20px 60px rgba(42,92,26,.15);font-family:'Nunito',sans-serif}.accent-bar{height:3px}.accent-bar--activar{background:linear-gradient(90deg,#82bd43,#4a8c25,#64992b)}.accent-bar--desactivar{background:linear-gradient(90deg,#82bd43,#64992b,#d97b1a)}.dialog-header{display:flex;align-items:flex-start;justify-content:space-between;border-bottom:1px solid;padding:16px 20px 14px}.dialog-header--activar{background:#f0f7e8;border-bottom-color:#c8e0a0}.dialog-header--desactivar{background:#fff8f0;border-bottom-color:#f5dbb8}.header-left{display:flex;align-items:center;gap:12px}.warn-icon{width:44px;height:44px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0}.warn-icon--activar{background:#eaf4d8;border:1.5px solid #82bd43}.warn-icon--desactivar{background:#fef3e2;border:1.5px solid #f5c97a}.dialog-title{font-size:15px;font-weight:900;color:#2a5c1a;margin-bottom:2px}.dialog-sub{font-size:11px;font-weight:600;color:#7aaa4e}.dialog-header--desactivar .dialog-sub{color:#b77b3c}.close-btn{border-radius:8px!important;color:#7aaa4e!important;background:#f0f7e8!important}.dialog-body{padding:18px 20px 4px!important}.client-card{display:flex;align-items:center;gap:12px;background:#f7f9f4;border:1px solid #e4edd8;border-radius:12px;padding:12px 14px;margin-bottom:14px}.client-avatar{width:42px;height:42px;border-radius:50%;background:#eaf4d8;border:2px solid #c8e0a0;display:flex;align-items:center;justify-content:center;color:#2a5c1a;flex-shrink:0}.client-name{font-size:14px;font-weight:800;color:#2a5c1a}.client-ci{color:#789080;font-size:11px;margin-top:3px}.warn-box{display:flex;align-items:flex-start;gap:10px;border-radius:10px;padding:11px 13px;margin-bottom:14px}.warn-box--activar{background:#f0f7e8;border:1px solid #c8e0a0}.warn-box--desactivar{background:#fff8f0;border:1px solid #f5c97a}.warn-box p{font-size:12.5px;font-weight:600;line-height:1.6;margin:0}.warn-box--activar p{color:#4a8c25}.warn-box--desactivar p{color:#a05c10}.warn-box p strong{color:#2a5c1a;font-weight:800}.confirm-row{display:flex;align-items:center;gap:9px;border-radius:9px;padding:10px 12px;margin-bottom:4px;cursor:pointer}.confirm-row--activar{background:#f0f7e8;border:1px solid #c8e0a0}.confirm-row--desactivar{background:#fef3e2;border:1px solid #f5c97a}.confirm-check{width:18px;height:18px;border-radius:5px;background:#fff;display:flex;align-items:center;justify-content:center;flex-shrink:0}.confirm-check--activar{border:2px solid #82bd43}.confirm-check--desactivar{border:2px solid #d97b1a}.confirm-check--activar.checked{background:#82bd43;border-color:#82bd43}.confirm-check--desactivar.checked{background:#d97b1a;border-color:#d97b1a}.confirm-txt{font-size:12px;font-weight:700;color:#4a8c25}.dialog-footer{display:flex;gap:10px;padding:14px 20px 18px;border-top:1px solid #e4edd8}.btn-cancel,.btn-accion--activar,.btn-accion--desactivar{padding:11px;border-radius:9px;font-size:14px;font-weight:700;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:6px}.btn-cancel{flex:1;background:#fff;color:#5a5a5a;border:1.5px solid #d0d0d0}.btn-accion--activar,.btn-accion--desactivar{flex:2;color:#fff;border:none;font-weight:800}.btn-accion--activar{background:#82bd43;box-shadow:0 4px 14px rgba(74,140,37,.3)}.btn-accion--desactivar{background:#d97b1a;box-shadow:0 4px 14px rgba(217,123,26,.3)}.btn-accion--activar:hover:not(.btn-disabled){background:#4a8c25}.btn-accion--desactivar:hover:not(.btn-disabled){background:#c06a10}.btn-disabled{opacity:.5!important;cursor:not-allowed!important;box-shadow:none!important}@media(max-width:480px){.dialog-footer{flex-direction:column-reverse}.btn-cancel,.btn-accion--activar,.btn-accion--desactivar{flex:auto;width:100%}}
</style>

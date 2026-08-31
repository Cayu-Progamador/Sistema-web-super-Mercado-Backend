<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dv-confirm-card">
      <div class="dv-confirm-accent" :class="{ 'dv-confirm-accent--warning': esAnular }"></div>

      <div class="dv-confirm-header" :class="{ 'dv-confirm-header--warning': esAnular }">
        <div class="row items-center no-wrap q-gutter-sm">
          <div class="dv-confirm-icon" :class="{ 'dv-confirm-icon--warning': esAnular }">
            <q-icon :name="config.icono" size="22px" :color="esAnular ? 'orange-9' : 'green-8'" />
          </div>
          <div>
            <div class="dv-confirm-title">{{ config.titulo }}</div>
            <div class="dv-confirm-sub">{{ config.subtitulo }}</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="dv-confirm-close" @click="cerrar" />
      </div>

      <q-card-section class="dv-confirm-body">
        <div class="dv-confirm-item">
          <div class="dv-confirm-item-icon"><q-icon name="assignment_return" color="green-8" size="20px" /></div>
          <div>
            <div class="dv-confirm-name">{{ devolucion?.numero || 'Devolución' }}</div>
            <div class="dv-confirm-meta">{{ devolucion?.proveedor || 'Proveedor' }} · {{ devolucion?.numeroRecepcion || 'Sin recepción' }}</div>
          </div>
        </div>

        <div class="dv-confirm-alert" :class="{ 'dv-confirm-alert--warning': esAnular }">
          <q-icon :name="esAnular ? 'warning_amber' : 'info'" size="18px" />
          <div>{{ config.mensaje }}</div>
        </div>

        <div class="dv-confirm-check-row" :class="{ 'dv-confirm-check-row--warning': esAnular }" @click="confirmado = !confirmado">
          <div class="dv-confirm-check" :class="[{ checked: confirmado }, { 'dv-confirm-check--warning': esAnular }]">
            <q-icon v-if="confirmado" name="check" size="12px" color="white" />
          </div>
          <span>{{ config.confirmacion }}</span>
        </div>
      </q-card-section>

      <div class="dv-confirm-footer">
        <button class="dv-confirm-cancel" :disabled="cargando" @click="cerrar">
          <q-icon name="close" size="15px" /> Cancelar
        </button>
        <button
          class="dv-confirm-action"
          :class="[{ 'dv-confirm-action--warning': esAnular }, { disabled: !confirmado || cargando }]"
          :disabled="!confirmado || cargando"
          @click="emit('confirmar')"
        >
          <q-spinner-dots v-if="cargando" color="white" size="1em" />
          <q-icon v-else :name="config.icono" size="16px" />
          {{ cargando ? 'Procesando...' : config.boton }}
        </button>
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  accion: { type: String, default: 'confirmar' },
  devolucion: { type: Object, default: null },
  cargando: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'confirmar'])
const confirmado = ref(false)
const abierto = computed({
  get: () => props.modelValue,
  set: (valor) => emit('update:modelValue', valor)
})
const esAnular = computed(() => props.accion === 'anular')

const configuraciones = {
  confirmar: {
    titulo: 'Confirmar devolución', subtitulo: 'La devolución quedará aprobada',
    icono: 'check_circle', boton: 'Confirmar Devolución',
    mensaje: 'Al confirmar, la cantidad será descontada del inventario de la ubicación seleccionada.',
    confirmacion: 'Entiendo que esta acción afectará el inventario'
  },
  enviar: {
    titulo: 'Enviar devolución', subtitulo: 'La mercadería saldrá hacia el proveedor',
    icono: 'local_shipping', boton: 'Enviar al Proveedor',
    mensaje: 'Esta acción registrará que la mercadería ya fue enviada al proveedor.',
    confirmacion: 'Confirmo que la mercadería fue enviada'
  },
  completar: {
    titulo: 'Completar devolución', subtitulo: 'El proveedor recibió la mercadería',
    icono: 'task_alt', boton: 'Completar Devolución',
    mensaje: 'La devolución quedará finalizada y registrada como recibida por el proveedor.',
    confirmacion: 'Confirmo que el proveedor recibió la devolución'
  },
  anular: {
    titulo: 'Anular devolución', subtitulo: 'Esta acción no se puede revertir',
    icono: 'block', boton: 'Anular Devolución',
    mensaje: 'La devolución quedará cancelada y ya no podrá continuar con el proceso.',
    confirmacion: 'Entiendo que la devolución será anulada'
  }
}

const config = computed(() => configuraciones[props.accion] || configuraciones.confirmar)

watch(() => props.modelValue, (valor) => {
  if (valor) confirmado.value = false
})

function cerrar() {
  if (!props.cargando) abierto.value = false
}
</script>

<style scoped>
.dv-confirm-card { width: 100%; max-width: 440px; border-radius: 20px; overflow: hidden; background: #fff; border: 1px solid #e4edd8; box-shadow: 0 20px 60px rgba(42, 92, 26, .15); font-family: 'Nunito', sans-serif; }
.dv-confirm-accent { height: 3px; background: linear-gradient(90deg, #82bd43, #4a8c25, #64992b); }
.dv-confirm-accent--warning { background: linear-gradient(90deg, #82bd43, #d97b1a, #f5a33b); }
.dv-confirm-header { display: flex; justify-content: space-between; align-items: flex-start; padding: 16px 20px 14px; background: #f0f7e8; border-bottom: 1px solid #c8e0a0; }
.dv-confirm-header--warning { background: #fff8f0; border-bottom-color: #f5dbb8; }
.dv-confirm-icon { width: 44px; height: 44px; border-radius: 12px; display: grid; place-items: center; background: #eaf4d8; border: 1.5px solid #82bd43; }
.dv-confirm-icon--warning { background: #fef3e2; border-color: #f5c97a; }
.dv-confirm-title { color: #2a5c1a; font-size: 15px; font-weight: 900; }
.dv-confirm-sub { color: #7aaa4e; font-size: 11px; font-weight: 600; margin-top: 2px; }
.dv-confirm-close { color: #7aaa4e; background: #f0f7e8; border-radius: 8px; }
.dv-confirm-body { padding: 18px 20px 4px; }
.dv-confirm-item { display: flex; align-items: center; gap: 12px; padding: 12px 14px; margin-bottom: 14px; background: #f7f9f4; border: 1px solid #e4edd8; border-radius: 12px; }
.dv-confirm-item-icon { width: 42px; height: 42px; border-radius: 50%; display: grid; place-items: center; background: #eaf4d8; border: 2px solid #c8e0a0; }
.dv-confirm-name { color: #2a5c1a; font-size: 14px; font-weight: 800; }
.dv-confirm-meta { color: #7aaa4e; font-size: 12px; font-weight: 700; margin-top: 2px; }
.dv-confirm-alert { display: flex; align-items: flex-start; gap: 10px; padding: 11px 13px; margin-bottom: 14px; color: #4a8c25; background: #f0f7e8; border: 1px solid #c8e0a0; border-radius: 10px; font-size: 12.5px; font-weight: 600; line-height: 1.5; }
.dv-confirm-alert--warning { color: #a05c10; background: #fff8f0; border-color: #f5c97a; }
.dv-confirm-check-row { display: flex; align-items: center; gap: 9px; padding: 10px 12px; margin-bottom: 4px; color: #4a8c25; background: #f0f7e8; border: 1px solid #c8e0a0; border-radius: 9px; cursor: pointer; font-size: 12px; font-weight: 700; }
.dv-confirm-check-row--warning { color: #a05c10; background: #fef3e2; border-color: #f5c97a; }
.dv-confirm-check { width: 18px; height: 18px; display: grid; place-items: center; flex-shrink: 0; background: #fff; border: 2px solid #82bd43; border-radius: 5px; }
.dv-confirm-check.checked { background: #82bd43; }
.dv-confirm-check--warning { border-color: #d97b1a; }
.dv-confirm-check--warning.checked { background: #d97b1a; }
.dv-confirm-footer { display: flex; gap: 10px; padding: 14px 20px 18px; border-top: 1px solid #e4edd8; }
.dv-confirm-footer button { padding: 11px; border-radius: 9px; font-size: 14px; font-weight: 800; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 7px; }
.dv-confirm-cancel { flex: 1; color: #5a5a5a; background: #fff; border: 1.5px solid #d0d0d0; }
.dv-confirm-action { flex: 2; color: #fff; background: #82bd43; border: 0; box-shadow: 0 4px 14px rgba(74, 140, 37, .3); }
.dv-confirm-action--warning { background: #d97b1a; box-shadow: 0 4px 14px rgba(217, 123, 26, .3); }
.dv-confirm-action.disabled { opacity: .5; cursor: not-allowed; box-shadow: none; }
</style>

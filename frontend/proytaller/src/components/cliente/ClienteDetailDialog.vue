<template>
  <q-dialog v-model="visible">
    <q-card class="detail-dialog" v-if="cliente">
      <q-card-section class="detail-head row items-center no-wrap"><q-avatar icon="person" color="green-1" text-color="green-9" size="48px" /><div class="q-ml-md"><div class="dialog-title">Detalle del cliente</div><div class="dialog-sub">Información registrada</div></div><q-space /><q-btn flat round dense icon="close" v-close-popup /></q-card-section>
      <q-card-section><div class="detail-name">{{ nombre }}</div><q-badge :color="cliente.estado ? 'green-7' : 'grey-6'" :label="cliente.estado ? 'Activo' : 'Inactivo'" />
        <div class="row q-col-gutter-md q-mt-md"><div v-for="item in detalles" :key="item.label" class="col-12 col-sm-6"><div class="detail-label">{{ item.label }}</div><div class="detail-value">{{ item.value }}</div></div></div>
      </q-card-section>
      <q-card-actions align="right"><q-btn flat label="Cerrar" color="green-8" no-caps v-close-popup /></q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ modelValue: Boolean, cliente: Object })
const emit = defineEmits(['update:modelValue'])
const visible = computed({ get: () => props.modelValue, set: v => emit('update:modelValue', v) })
const nombre = computed(() => [props.cliente?.nombres, props.cliente?.apellidoPaterno, props.cliente?.apellidoMaterno].filter(Boolean).join(' '))
const detalles = computed(() => [{ label: 'CI', value: props.cliente?.ci || '--' }, { label: 'Celular', value: props.cliente?.telefono || '--' }, { label: 'Correo', value: props.cliente?.correo || '--' }, { label: 'Dirección', value: props.cliente?.direccion || '--' }, { label: 'Fecha de registro', value: props.cliente?.fechaRegistro ? new Date(props.cliente.fechaRegistro).toLocaleDateString('es-BO') : '--' }])
</script>

<style scoped>
.detail-dialog { width: 620px; max-width: 94vw; border-radius: 20px; }
.detail-head { background: #f4fbf5; border-bottom: 1px solid #e2eee4; padding: 20px 24px; }
.dialog-title { color: #1b5e20; font-weight: 900; font-size: 19px; }.dialog-sub { color: #718078; font-size: 12px; }.detail-name { display: inline-block; margin-right: 12px; color: #1b5e20; font-size: 21px; font-weight: 900; }.detail-label { color: #789080; font-size: 11px; text-transform: uppercase; letter-spacing: .5px; }.detail-value { color: #263238; font-weight: 700; margin-top: 4px; }
</style>

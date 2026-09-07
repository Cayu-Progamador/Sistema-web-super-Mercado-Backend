<template>
  <q-dialog v-model="visible" persistent>
    <q-card class="confirm-dialog">
      <q-card-section class="row items-center no-wrap"><q-avatar :icon="activar ? 'person_add' : 'person_off'" :color="activar ? 'green-1' : 'orange-1'" :text-color="activar ? 'green-9' : 'orange-9'" size="48px" /><div class="q-ml-md"><div class="confirm-title">{{ activar ? 'Activar cliente' : 'Desactivar cliente' }}</div><div class="confirm-sub">Confirma esta acción para continuar.</div></div></q-card-section>
      <q-card-section class="confirm-body">¿Deseas {{ activar ? 'activar' : 'desactivar' }} a <b>{{ nombre }}</b>?</q-card-section>
      <q-card-actions align="right"><q-btn flat label="Cancelar" no-caps v-close-popup /><q-btn unelevated :color="activar ? 'green-8' : 'orange-8'" :label="activar ? 'Activar' : 'Desactivar'" no-caps @click="$emit('confirmar', cliente)" /></q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ modelValue: Boolean, cliente: Object })
const emit = defineEmits(['update:modelValue', 'confirmar'])
const visible = computed({ get: () => props.modelValue, set: v => emit('update:modelValue', v) })
const activar = computed(() => !props.cliente?.estado)
const nombre = computed(() => [props.cliente?.nombres, props.cliente?.apellidoPaterno].filter(Boolean).join(' '))
</script>

<style scoped>
.confirm-dialog { width: 440px; max-width: 94vw; border-radius: 18px; }.confirm-title { color: #1b5e20; font-size: 19px; font-weight: 900; }.confirm-sub { color: #789080; font-size: 12px; }.confirm-body { color: #37474f; line-height: 1.6; padding-top: 4px; }
</style>

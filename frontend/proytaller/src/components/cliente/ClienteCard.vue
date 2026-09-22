<template>
  <q-card flat bordered class="client-card">
    <q-card-section class="client-head row no-wrap items-center">
      <q-avatar size="48px" color="green-1" text-color="green-9" icon="person" />
      <div class="q-ml-md ellipsis">
        <div class="client-name ellipsis">{{ nombreCompleto }}</div>
        <div class="client-ci">CI: {{ cliente.ci || 'Sin registrar' }}</div>
      </div>
      <q-space />
      <q-badge rounded :color="cliente.estado ? 'green-7' : 'grey-6'" :label="cliente.estado ? 'Activo' : 'Inactivo'" />
    </q-card-section>
    <q-separator />
    <q-card-section class="client-info">
      <div class="info-row"><q-icon name="mail" /><span class="ellipsis">{{ cliente.correo || 'Sin correo' }}</span></div>
      <div class="info-row"><q-icon name="event" /><span>Registrado: {{ fecha }}</span></div>
    </q-card-section>
    <q-card-actions align="right" class="client-actions">
      <q-btn flat round dense color="green-8" icon="visibility" @click="$emit('ver', cliente)"><q-tooltip>Ver detalle</q-tooltip></q-btn>
      <q-btn v-permiso="'CLIENTES_EDITAR'" flat round dense color="blue-8" icon="edit" @click="$emit('editar', cliente)"><q-tooltip>Editar cliente</q-tooltip></q-btn>
      <q-btn v-permiso="'CLIENTES_EDITAR'" flat round dense :color="cliente.estado ? 'negative' : 'green-8'" :icon="cliente.estado ? 'person_off' : 'person_add'"
        @click="$emit('estado', cliente)"><q-tooltip>{{ cliente.estado ? 'Desactivar' : 'Activar' }}</q-tooltip></q-btn>
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ cliente: { type: Object, required: true } })
defineEmits(['ver', 'editar', 'estado'])
const nombreCompleto = computed(() => [props.cliente.nombres, props.cliente.apellidoPaterno, props.cliente.apellidoMaterno].filter(Boolean).join(' '))
const fecha = computed(() => props.cliente.fechaRegistro ? new Date(props.cliente.fechaRegistro).toLocaleDateString('es-BO') : '--')
</script>

<style scoped>
.client-card { height: 100%; border-radius: 18px; border-color: #e0e8e2; transition: .2s ease; }
.client-card:hover { transform: translateY(-2px); box-shadow: 0 8px 22px rgba(46, 125, 50, .12); }
.client-head { padding: 18px 18px 14px; }
.client-name { color: #1b5e20; font-weight: 900; font-size: 16px; }
.client-ci, .info-row { color: #738078; font-size: 12px; }
.client-info { min-height: 76px; padding: 14px 18px; }
.info-row { display: flex; align-items: center; gap: 8px; margin: 7px 0; }
.info-row .q-icon { color: #43a047; font-size: 17px; }
.client-actions { padding: 8px 14px; background: #fbfdfb; }
</style>

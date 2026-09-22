<template>
  <q-card flat bordered class="credit-card">
    <q-card-section class="row items-start justify-between q-pb-sm">
      <div>
        <div class="text-overline text-grey-7">{{ credito.codigoVenta }}</div>
        <div class="text-subtitle1 text-weight-bold">{{ credito.cliente }}</div>
        <div class="text-caption text-grey-7">{{ credito.celular || 'Sin celular registrado' }}</div>
      </div>
      <q-badge :color="estadoColor" :label="credito.estado" />
    </q-card-section>
    <q-separator />
    <q-card-section class="row q-col-gutter-sm">
      <div class="col-6"><div class="field-label">Total</div><div class="field-value">{{ moneda(credito.total) }}</div></div>
      <div class="col-6"><div class="field-label">Pago inicial</div><div class="field-value">{{ moneda(credito.pagoInicial) }}</div></div>
      <div class="col-6"><div class="field-label">Saldo pendiente</div><div class="field-value debt">{{ moneda(credito.saldo) }}</div></div>
      <div class="col-6"><div class="field-label">Fecha límite</div><div class="field-value">{{ credito.fechaLimite }}</div></div>
    </q-card-section>
    <q-card-actions align="right" class="q-pt-none">
      <q-btn flat no-caps color="green-8" label="Ver detalle" icon="visibility" @click="$emit('detalle', credito)" />
      <q-btn v-if="credito.saldo > 0" unelevated no-caps color="positive" label="Registrar pago" icon="payments" :disable="bloqueada" @click="$emit('pago', credito)" />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ credito: { type: Object, required: true }, bloqueada: Boolean })
defineEmits(['detalle', 'pago'])
const estadoColor = computed(() => props.credito.estado === 'PAGADO' ? 'positive' : props.credito.estado === 'VENCIDO' ? 'negative' : 'orange')
const moneda = value => `Bs ${Number(value || 0).toFixed(2)}`
</script>

<style scoped>
.credit-card { border-radius: 14px; height: 100%; }
.field-label { color: #7a8694; font-size: 11px; }
.field-value { color: #263442; font-size: 14px; font-weight: 600; margin-top: 3px; }
.debt { color: #c0392b; }
</style>

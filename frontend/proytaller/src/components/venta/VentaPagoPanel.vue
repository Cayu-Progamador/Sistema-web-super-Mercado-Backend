<template>
  <q-card flat bordered class="payment-card">
    <q-card-section class="payment-title"><q-icon name="payments" color="green-8" /> Cobro de la venta</q-card-section>
    <q-card-section class="q-pt-none">
      <q-select v-model="pago.metodo" :options="metodos" outlined dense emit-value map-options label="Método de pago" color="green-8" />
      <div v-if="pago.metodo === 'MIXTO'" class="row q-col-gutter-sm q-mt-sm"><div class="col-6"><q-input v-model.number="pago.efectivo" type="number" min="0" outlined dense label="Efectivo" prefix="Bs " color="green-8" /></div><div class="col-6"><q-input v-model.number="pago.qr" type="number" min="0" outlined dense label="QR" prefix="Bs " color="green-8" /></div></div>
      <div v-if="pago.metodo === 'EFECTIVO'" class="q-mt-sm"><q-input v-model.number="pago.efectivo" type="number" min="0" outlined dense label="Monto recibido" prefix="Bs " color="green-8" /></div>
      <div class="payment-total"><span>Total a pagar</span><b>Bs {{ total.toFixed(2) }}</b></div>
      <div v-if="pago.metodo === 'EFECTIVO'" class="change-row"><span>Vuelto</span><b>Bs {{ Math.max(0, (Number(pago.efectivo) || 0) - total).toFixed(2) }}</b></div>
      <q-btn unelevated color="green-8" icon="check_circle" label="Confirmar venta" no-caps class="full-width q-mt-md" :disable="!valido" @click="$emit('confirmar')" />
      <q-btn flat color="grey-7" icon="save" label="Guardar como borrador" no-caps class="full-width q-mt-xs" @click="$emit('borrador')" />
    </q-card-section>
  </q-card>
</template>

<script setup>
import { reactive, computed } from 'vue'
const props = defineProps({ total: { type: Number, default: 0 } })
defineEmits(['confirmar', 'borrador'])
const pago = reactive({ metodo: 'EFECTIVO', efectivo: 0, qr: 0 })
const metodos = [{ label: 'Efectivo', value: 'EFECTIVO' }, { label: 'QR', value: 'QR' }, { label: 'Pago mixto', value: 'MIXTO' }]
const valido = computed(() => props.total > 0 && (pago.metodo === 'QR' || (pago.metodo === 'EFECTIVO' && Number(pago.efectivo) >= props.total) || (pago.metodo === 'MIXTO' && Number(pago.efectivo) + Number(pago.qr) === props.total)))
</script>

<style scoped>
.payment-card { border-radius: 18px; border-color: #dfeae1; }.payment-title { color: #1b5e20; font-size: 16px; font-weight: 900; display: flex; gap: 8px; align-items: center; }.payment-total, .change-row { display: flex; justify-content: space-between; align-items: center; margin-top: 18px; padding: 12px 14px; border-radius: 11px; background: #f0f8f1; color: #55705c; }.payment-total b { color: #2e7d32; font-size: 20px; }.change-row { margin-top: 8px; background: #fff8f0; color: #a05c10; }.change-row b { font-size: 16px; }
</style>

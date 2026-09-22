<template>
  <q-dialog v-model="visible" persistent>
    <q-card class="payment-dialog">
      <q-card-section class="payment-head row items-center justify-between">
        <div class="row items-center no-wrap">
          <div class="payment-title-icon"><q-icon name="payments" size="24px" /></div>
          <div><div class="text-h6">Registrar pago</div><div class="text-caption text-grey-6">Registra un abono a esta cuenta</div></div>
        </div>
        <q-btn flat round dense icon="close" color="grey-7" v-close-popup />
      </q-card-section>
      <q-card-section v-if="credito">
        <div class="client-balance">
          <div><div class="text-caption text-grey-7">Cliente</div><div class="text-weight-bold">{{ credito.cliente }}</div></div>
          <div class="text-right"><div class="text-caption text-grey-7">Saldo pendiente</div><div class="balance-value">{{ moneda(credito.saldo) }}</div></div>
        </div>
        <q-select v-model="form.metodo" outlined emit-value map-options label="Método de pago" :options="metodos" class="q-mt-lg" />
        <q-input v-if="form.metodo !== 'MIXTO'" v-model.number="form.monto" outlined type="number" min="0.01" :max="credito.saldo" label="Monto recibido" placeholder="Ej.: 50.00" hint="Ingresa el monto que pagará el cliente." class="q-mt-md" :error="!!error" :error-message="error" />
        <div v-else class="row q-col-gutter-sm q-mt-sm">
          <div class="col-6"><q-input v-model.number="form.montoEfectivo" outlined type="number" min="0" label="Efectivo" placeholder="Ej.: 20.00" /></div>
          <div class="col-6"><q-input v-model.number="form.montoQr" outlined type="number" min="0" label="Pago por QR" placeholder="Ej.: 30.00" /></div>
          <div class="col-12 text-caption text-grey-7">Total del abono: <b>{{ moneda(totalMixto) }}</b></div>
          <div v-if="error" class="col-12 text-negative text-caption">{{ error }}</div>
        </div>
        <div v-if="montoSuperaSaldo" class="limit-warning"><q-icon name="warning" /> El monto no puede superar el saldo pendiente de {{ moneda(credito.saldo) }}.</div>
        <q-input v-if="form.metodo === 'QR' || form.metodo === 'MIXTO'" v-model="form.referencia" outlined label="Referencia QR" placeholder="Ej.: QR-000245" hint="Código de referencia de la transferencia." class="q-mt-md" />
      </q-card-section>
      <q-card-actions align="right" class="payment-actions"><q-btn flat label="Cancelar" color="grey-7" v-close-popup /><q-btn unelevated color="positive" icon="check" label="Guardar pago" :disable="montoSuperaSaldo" @click="guardar" /></q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
const props = defineProps({ modelValue: Boolean, credito: Object })
const emit = defineEmits(['update:modelValue', 'guardar'])
const visible = computed({ get: () => props.modelValue, set: value => emit('update:modelValue', value) })
const form = reactive({ monto: null, montoEfectivo: 0, montoQr: 0, metodo: 'EFECTIVO', referencia: '' })
const error = ref('')
const metodos = [{ label: 'Efectivo', value: 'EFECTIVO' }, { label: 'QR', value: 'QR' }, { label: 'Mixto', value: 'MIXTO' }]
const moneda = value => `Bs ${Number(value || 0).toFixed(2)}`
const totalMixto = computed(() => Number(form.montoEfectivo || 0) + Number(form.montoQr || 0))
const montoActual = computed(() => form.metodo === 'MIXTO' ? totalMixto.value : Number(form.monto || 0))
const montoSuperaSaldo = computed(() => Boolean(props.credito && montoActual.value > Number(props.credito.saldo || 0)))
function guardar () {
  error.value = ''
  const monto = form.metodo === 'MIXTO' ? totalMixto.value : Number(form.monto || 0)
  if (!props.credito || monto <= 0 || monto > props.credito.saldo) { error.value = 'El monto debe ser mayor a 0 y no superar el saldo.'; return }
  if (form.metodo === 'MIXTO' && (Number(form.montoEfectivo || 0) <= 0 || Number(form.montoQr || 0) <= 0)) { error.value = 'Ingresa efectivo y QR para un pago mixto.'; return }
  if ((form.metodo === 'QR' || form.metodo === 'MIXTO') && !form.referencia.trim()) { error.value = 'La referencia QR es obligatoria.'; return }
  emit('guardar', { monto, metodo: form.metodo, montoEfectivo: form.metodo === 'MIXTO' ? Number(form.montoEfectivo) : form.metodo === 'EFECTIVO' ? monto : 0, montoQr: form.metodo === 'MIXTO' ? Number(form.montoQr) : form.metodo === 'QR' ? monto : 0, referenciaQr: form.referencia })
  form.monto = null; form.montoEfectivo = 0; form.montoQr = 0; form.metodo = 'EFECTIVO'; form.referencia = ''; visible.value = false
}
</script>

<style scoped>
.payment-dialog { width: 430px; max-width: 95vw; border-radius: 16px; overflow: hidden; }
.payment-head { padding: 18px 20px; background: #f5faf6; border-bottom: 1px solid #e1eee4; }
.payment-title-icon { width: 44px; height: 44px; display: grid; place-items: center; margin-right: 12px; border-radius: 12px; color: #16804b; background: #dff3e6; }
.client-balance { display: flex; justify-content: space-between; align-items: center; padding: 12px 14px; border-radius: 12px; background: #f7f8fa; }
.balance-value { color: #c0392b; font-size: 17px; font-weight: 800; margin-top: 2px; }
.payment-actions { padding: 12px 20px 18px; border-top: 1px solid #edf0f2; }
.limit-warning { display: flex; align-items: center; gap: 6px; margin-top: 10px; padding: 9px 11px; border: 1px solid #f0cf91; border-radius: 9px; color: #a85f00; background: #fff4df; font-size: 12px; font-weight: 700; }
</style>

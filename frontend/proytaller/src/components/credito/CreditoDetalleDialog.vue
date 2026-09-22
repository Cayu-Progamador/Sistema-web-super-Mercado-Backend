<template>
  <q-dialog v-model="visible">
    <q-card class="detail-dialog">
      <q-card-section class="row items-center justify-between bg-primary text-white">
        <div><div class="text-overline">Detalle de crédito</div><div class="text-h6">{{ credito?.codigoVenta }}</div></div>
        <q-btn flat round dense icon="close" v-close-popup />
      </q-card-section>
      <q-card-section v-if="credito" class="dialog-scroll">
        <div class="row q-col-gutter-md q-mb-md">
          <div class="col-12 col-sm-6"><div class="text-caption text-grey-7">Cliente</div><div class="text-weight-bold">{{ credito.cliente }}</div></div>
          <div class="col-12 col-sm-6"><div class="text-caption text-grey-7">Fecha límite</div><div class="text-weight-bold">{{ credito.fechaLimite }}</div></div>
        </div>
        <q-markup-table flat bordered dense class="q-mb-md">
          <thead><tr><th class="text-left">Producto</th><th class="text-right">Cant.</th><th class="text-right">Importe</th></tr></thead>
          <tbody><tr v-for="item in credito.productos" :key="item.codigo"><td>{{ item.producto }}<div class="text-caption text-grey-6">{{ item.codigo }}</div></td><td class="text-right">{{ item.cantidad }}</td><td class="text-right">{{ moneda(item.importe) }}</td></tr></tbody>
        </q-markup-table>
        <div class="amounts">
          <div><span>Total</span><strong>{{ moneda(credito.total) }}</strong></div>
          <div><span>Pago inicial</span><span>{{ moneda(credito.pagoInicial) }}</span></div>
          <div class="balance"><span>Saldo pendiente</span><strong>{{ moneda(credito.saldo) }}</strong></div>
        </div>
        <div class="text-subtitle2 q-mt-lg q-mb-sm">Historial de pagos</div>
        <q-list bordered separator v-if="credito.pagos?.length">
          <q-item v-for="pago in credito.pagos" :key="pago.id"><q-item-section><q-item-label>{{ pago.metodo }}</q-item-label><q-item-label caption>{{ pago.fecha }} {{ pago.referencia ? `· ${pago.referencia}` : '' }}</q-item-label><q-item-label v-if="pago.metodo === 'MIXTO'" caption>Efectivo: {{ moneda(pago.montoEfectivo) }} · QR: {{ moneda(pago.montoQr) }}</q-item-label></q-item-section><q-item-section side class="text-positive text-weight-bold">{{ moneda(pago.monto) }}</q-item-section></q-item>
        </q-list>
        <div v-else class="text-grey-6 text-center q-pa-md">Todavía no tiene pagos registrados.</div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ modelValue: Boolean, credito: Object })
const emit = defineEmits(['update:modelValue'])
const visible = computed({ get: () => props.modelValue, set: value => emit('update:modelValue', value) })
const moneda = value => `Bs ${Number(value || 0).toFixed(2)}`
</script>

<style scoped>
.detail-dialog { width: 720px; max-width: 95vw; border-radius: 14px; }
.dialog-scroll { max-height: 75vh; overflow-y: auto; }
.amounts > div { display: flex; justify-content: space-between; padding: 5px 0; }
.balance { border-top: 1px solid #e5e7eb; margin-top: 5px; padding-top: 10px !important; color: #c0392b; }
.detail-dialog { overflow: hidden; box-shadow: 0 18px 55px rgba(25, 55, 35, .22); }
.detail-dialog > .bg-primary { background: linear-gradient(135deg, #176b43, #2e8b57) !important; padding: 20px 24px; }
.detail-dialog > .bg-primary .q-btn { color: #fff; }
.dialog-scroll > .row { padding: 13px; border: 1px solid #e3ece6; border-radius: 12px; background: #f8fbf9; }
.dialog-scroll > .row .text-weight-bold { color: #27352d; margin-top: 2px; }
.dialog-scroll > .q-markup-table { border-radius: 12px; overflow: hidden; border-color: #e3ece6; }
.dialog-scroll > .q-markup-table thead tr { background: #f1f8f3; color: #176b43; }
.dialog-scroll > .q-markup-table tbody tr:hover { background: #fbfefc; }
.amounts { padding: 14px 16px; border: 1px solid #e3ece6; border-radius: 12px; background: #fbfdfb; }
.amounts > div { color: #627167; }
.amounts strong { color: #26352c; }
.balance { color: #c0392b !important; font-size: 15px; }
.balance strong { color: #c0392b; font-size: 17px; }
.dialog-scroll > .text-subtitle2 { color: #176b43; font-weight: 800; }
.dialog-scroll > .q-list { border-radius: 12px; overflow: hidden; border-color: #e3ece6; }
.dialog-scroll > .q-list .q-item { min-height: 68px; padding: 12px 14px; background: #fbfdfb; }
.dialog-scroll > .q-list .q-item:hover { background: #f1f8f3; }
.dialog-scroll > .q-list .q-item__label:first-child { color: #176b43; font-weight: 800; }
.dialog-scroll > .q-list .q-item__label--caption { color: #718078; margin-top: 4px; }
.dialog-scroll > .q-list .q-item__label--caption + .q-item__label--caption { display: inline-block; width: fit-content; padding: 4px 8px; border-radius: 7px; color: #176b43; background: #e8f6ec; font-size: 11px; font-weight: 700; }
.dialog-scroll > .q-list .q-item__section--side { min-width: 92px; padding-left: 10px; font-size: 15px; }
@media (max-width: 600px) { .detail-dialog > .bg-primary { padding: 17px; } }
</style>

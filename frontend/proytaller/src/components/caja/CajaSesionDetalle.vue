<template>
  <q-dialog v-model="visible">
    <q-card class="detail-dialog">
      <q-card-section class="detail-head row items-center justify-between"><div class="row items-center"><q-avatar icon="fact_check" color="green-1" text-color="green-9" /><div class="q-ml-md"><div class="text-overline">Arqueo de caja</div><div class="text-h6">{{ sesion?.caja }} · {{ sesion?.cajero }}</div></div></div><q-btn flat round dense icon="close" v-close-popup /></q-card-section>
      <q-card-section v-if="sesion"><div class="info-grid"><div><span>Apertura</span><b>{{ sesion.apertura || '--' }}</b></div><div><span>Cierre</span><b>{{ sesion.cierre || 'En curso' }}</b></div></div><div class="summary-box"><div><span>Saldo inicial</span><b>Bs {{ monto(sesion.inicial) }}</b></div><div><span>Ventas en efectivo</span><b>Bs {{ monto(sesion.efectivo) }}</b></div><div><span>Ventas QR</span><b>Bs {{ monto(sesion.qr) }}</b></div><div><span>Ventas a crédito</span><b>Bs {{ monto(sesion.credito) }}</b></div><div><span>Saldo esperado</span><strong>Bs {{ monto(sesion.esperado) }}</strong></div><div><span>Efectivo contado</span><strong>Bs {{ monto(sesion.contado) }}</strong></div><div class="difference"><span>Diferencia</span><strong>Bs {{ monto(sesion.diferencia) }}</strong></div></div><div class="text-caption text-grey-7 q-mt-md">Observación: {{ sesion.observacion || 'Sin observaciones' }}</div></q-card-section>
      <q-card-actions align="right" class="detail-actions"><q-btn flat label="Cerrar" color="grey-7" v-close-popup /><q-btn v-if="sesion?.estado === 'CERRADA'" unelevated color="green-8" icon="print" label="Imprimir arqueo" @click="imprimir" /></q-card-actions>
    </q-card>
  </q-dialog>
</template>
<script setup>
import { computed } from 'vue'
const props = defineProps({ modelValue: Boolean, sesion: Object })
const emit = defineEmits(['update:modelValue'])
const visible = computed({ get: () => props.modelValue, set: value => emit('update:modelValue', value) })
function monto(valor) { return Number(valor ?? 0).toFixed(2) }
function imprimir() { window.print() }
</script>
<style scoped>.detail-dialog{width:520px;max-width:95vw;border-radius:17px;overflow:hidden}.detail-head{padding:18px 22px;background:#f4faf5;border-bottom:1px solid #e0eee2}.info-grid{display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:16px}.info-grid>div{padding:11px 13px;border-radius:10px;background:#f7faf8}.info-grid span,.summary-box span{display:block;color:#718078;font-size:12px}.info-grid b{display:block;color:#26352c;margin-top:4px}.summary-box{padding:13px 15px;border:1px solid #e0ece3;border-radius:12px;background:#fbfdfb}.summary-box>div{display:flex;justify-content:space-between;padding:6px 0;color:#627167}.summary-box b{color:#26352c}.summary-box strong{color:#176b43}.summary-box .difference{margin-top:7px;padding-top:11px;border-top:1px solid #dbe8df;color:#a85f00}.summary-box .difference strong{color:#a85f00}.detail-actions{padding:12px 22px 18px;border-top:1px solid #edf2ee}@media(max-width:500px){.info-grid{grid-template-columns:1fr}}</style>

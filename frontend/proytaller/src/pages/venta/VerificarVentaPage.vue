<template>
  <q-page class="verify-page flex flex-center">
    <q-card flat bordered class="verify-card">
      <q-card-section class="verify-head">
        <q-icon name="verified" size="42px" color="green-8" />
        <div class="verify-title">Verificación de venta</div>
        <div class="verify-sub">Consulta oficial del comprobante</div>
      </q-card-section>
      <q-separator />
      <q-card-section v-if="cargando" class="verify-state"><q-spinner color="green-8" size="42px" /><span>Verificando comprobante...</span></q-card-section>
      <q-card-section v-else-if="error" class="verify-state error"><q-icon name="cancel" size="48px" /><strong>Comprobante no encontrado</strong><span>{{ error }}</span></q-card-section>
      <q-card-section v-else class="verify-data">
        <div class="valid-banner" :class="{ cancelled: venta.estado === 'ANULADA' }"><q-icon :name="venta.valida ? 'check_circle' : 'cancel'" /><strong>{{ venta.valida ? 'Venta válida' : 'Venta anulada' }}</strong></div>
        <div class="data-row"><span>Código</span><strong>{{ venta.codigo }}</strong></div>
        <div class="data-row"><span>Fecha</span><strong>{{ fecha(venta.fechaVenta) }}</strong></div>
        <div class="data-row"><span>Total</span><strong>Bs {{ dinero(venta.total) }}</strong></div>
        <div class="data-row"><span>Estado</span><strong>{{ venta.estado }}</strong></div>
      </q-card-section>
      <q-card-section class="verify-footer">NOMBRE DEL SUPERMERCADO · La Paz, Bolivia</q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { verificarVenta } from '../../api/venta/venta'

const route = useRoute()
const venta = ref(null)
const cargando = ref(true)
const error = ref('')

onMounted(async () => {
  try { venta.value = await verificarVenta(route.params.codigo) } catch (e) { error.value = e?.response?.data?.message || 'El código no corresponde a una venta registrada.' } finally { cargando.value = false }
})
function dinero(valor) { return Number(valor || 0).toFixed(2) }
function fecha(valor) { return valor ? new Date(valor).toLocaleString('es-BO') : '--' }
</script>

<style scoped>
.verify-page{min-height:100vh;background:#f5f7fa;padding:20px}.verify-card{width:460px;max-width:100%;border-radius:20px;border-color:#dfeae1;overflow:hidden;background:#fff}.verify-head{text-align:center;padding:30px 24px 22px}.verify-title{color:#1b5e20;font-size:24px;font-weight:900;margin-top:8px}.verify-sub{color:#789080;font-size:13px;margin-top:4px}.verify-state{min-height:180px;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:10px;color:#60756a;text-align:center}.verify-state.error{color:#b3261e}.verify-data{padding:22px}.valid-banner{display:flex;align-items:center;justify-content:center;gap:8px;color:#1b5e20;background:#edf8ef;border-radius:12px;padding:13px;margin-bottom:18px}.valid-banner.cancelled{color:#b3261e;background:#fff0f0}.data-row{display:flex;justify-content:space-between;gap:14px;padding:11px 0;border-bottom:1px dashed #dce7de;color:#789080;font-size:13px}.data-row strong{color:#37474f;text-align:right}.verify-footer{text-align:center;color:#8a9990;font-size:11px;padding:15px}
</style>

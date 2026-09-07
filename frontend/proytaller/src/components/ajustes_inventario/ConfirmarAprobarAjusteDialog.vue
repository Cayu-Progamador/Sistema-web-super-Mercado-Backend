<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card">
      <div class="accent-bar" />
      <div class="dialog-header">
        <div class="header-left"><div class="approve-icon"><q-icon name="verified" size="22px" /></div><div><div class="dialog-title">Aprobar ajuste</div><div class="dialog-sub">Revisión y autorización del movimiento</div></div></div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>
      <q-card-section class="dialog-body">
        <div class="summary-grid"><div><span>Ajuste</span><b>{{ ajuste?.codigo || '--' }}</b></div><div><span>Tipo</span><b>{{ ajuste?.tipo || '--' }}</b></div><div><span>Motivo</span><b>{{ texto(ajuste?.motivo) }}</b></div><div><span>{{ ajuste?.tipo === 'ENTRADA' ? 'Ganancia estimada' : 'Pérdida estimada' }}</span><b>Bs {{ dinero(ajuste?.tipo === 'ENTRADA' ? gananciaTotal : ajuste?.valorTotal) }}</b></div></div>
        <div class="info-box"><q-icon name="info" size="18px" /><p>Al aprobar, el ajuste quedará listo para ser aplicado. <strong>El stock y Kardex cambiarán únicamente al aplicar el ajuste.</strong></p></div>
        <div class="section-title">Detalle de productos ({{ ajuste?.detalles?.length || 0 }})</div>
        <div v-if="ajuste?.detalles?.length" class="products-list">
          <div v-for="(item, index) in ajuste.detalles" :key="item.id || index" class="product-card">
            <div class="product-head"><span class="product-number">{{ index + 1 }}</span><div><b>{{ item.producto || item.codigoProducto || '--' }}</b><small>{{ item.codigoProducto || 'Sin código' }} · {{ item.codigoLote || 'Sin lote' }}</small></div><q-badge :color="ajuste.tipo === 'ENTRADA' ? 'blue-7' : 'red-6'" :label="`${item.cantidad} ${item.unidad || ''}`" /></div>
            <div class="product-grid"><span><small>Almacén / ubicación</small><b>{{ item.almacen || '--' }} · {{ item.ubicacion || '--' }}</b></span><span><small>Stock anterior</small><b>{{ item.stockAnterior ?? '--' }} {{ item.unidad || '' }}</b></span><span><small>Stock resultante</small><b>{{ item.stockResultante ?? '--' }} {{ item.unidad || '' }}</b></span><span><small>Precio de venta</small><b>Bs {{ dinero(item.precioVenta) }}</b></span><span><small>Costo unitario</small><b>Bs {{ dinero(item.costoUnitario) }}</b></span><span class="loss"><small>{{ ajuste.tipo === 'SALIDA' ? 'Pérdida estimada' : 'Ganancia estimada' }}</small><b>Bs {{ dinero(ajuste.tipo === 'SALIDA' ? item.valorTotal : ganancia(item)) }}</b></span></div>
          </div>
        </div>
        <div v-else class="empty">No hay productos registrados en este ajuste.</div>
        <div v-if="ajuste?.observacion" class="observation"><b>Observación</b><span>{{ ajuste.observacion }}</span></div>
        <div class="confirm-row" @click="confirmado = !confirmado"><div class="confirm-check" :class="{ checked: confirmado }"><q-icon v-if="confirmado" name="check" size="11px" color="white" /></div><span>Confirmo que revisé el ajuste y autorizo su aprobación</span></div>
      </q-card-section>
      <div class="dialog-footer"><q-btn flat no-caps label="Cancelar" icon="close" color="grey-7" @click="cerrar" /><q-btn unelevated no-caps color="green-8" icon="verified" label="Aprobar ajuste" :disable="!confirmado || loading" :loading="loading" @click="$emit('confirmar', ajuste)" /></div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
const props = defineProps({ modelValue: Boolean, ajuste: Object, loading: Boolean })
const emit = defineEmits(['update:modelValue', 'confirmar'])
const confirmado = ref(false)
const abierto = computed({ get: () => props.modelValue, set: (value) => emit('update:modelValue', value) })
watch(() => props.modelValue, (value) => { if (value) confirmado.value = false })
const texto = (value) => value?.replaceAll('_', ' ') || '--'
const dinero = (value) => Number(value || 0).toFixed(2)
const ganancia = (item) => (Number(item.precioVenta || 0) - Number(item.costoUnitario || 0)) * Number(item.cantidad || 0)
const gananciaTotal = computed(() => (props.ajuste?.detalles || []).reduce((total, item) => total + ganancia(item), 0))
function cerrar() { confirmado.value = false; abierto.value = false }
</script>

<style scoped>
.dialog-card { width: min(920px, 96vw); max-height: 92vh; display: flex; flex-direction: column; border-radius: 20px; overflow: hidden; background: #fff; border: 1px solid #e4edd8; box-shadow: 0 20px 60px rgba(42,92,26,.15) }.accent-bar { height: 3px; flex-shrink: 0; background: linear-gradient(90deg, #82bd43, #4a8c25, #2f6f2c) }.dialog-header { display: flex; flex-shrink: 0; align-items: flex-start; justify-content: space-between; background: #f0f7e8; border-bottom: 1px solid #c8e0a0; padding: 16px 20px 14px }.header-left { display: flex; align-items: center; gap: 12px }.approve-icon { width: 44px; height: 44px; border-radius: 12px; display: grid; place-items: center; background: #eaf4d8; border: 1.5px solid #82bd43; color: #4a8c25 }.dialog-title { font-size: 15px; font-weight: 900; color: #2a5c1a }.dialog-sub { font-size: 11px; color: #7aaa4e; font-weight: 600 }.close-btn { color: #7aaa4e !important; background: #fff !important }.dialog-body { flex: 1 1 auto; min-height: 0; overflow-y: auto; padding: 18px 20px 8px !important }.summary-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px }.summary-grid div { display: flex; flex-direction: column; gap: 4px; padding: 10px 12px; border: 1px solid #e4edd8; border-radius: 10px; background: #f7f9f4 }.summary-grid span, .product-grid small { color: #7b857c; font-size: 10px }.summary-grid b, .product-grid b { color: #2a5c1a; font-size: 12px }.info-box { display: flex; gap: 10px; align-items: flex-start; margin: 14px 0; padding: 11px 13px; border: 1px solid #c8e0a0; border-radius: 10px; background: #f0f7e8; color: #4a8c25 }.info-box p { margin: 0; color: #4a8c25; font-size: 12px; line-height: 1.5 }.info-box strong { color: #2a5c1a }.section-title { margin: 14px 0 8px; font-weight: 900; color: #354634 }.products-list { display: grid; gap: 10px }.product-card { border: 1px solid #e1eadf; border-radius: 12px; padding: 12px; background: #fff }.product-head { display: flex; align-items: center; gap: 10px; margin-bottom: 10px }.product-head > div { flex: 1 }.product-head b { display: block; color: #263827; font-size: 13px }.product-head small { color: #7b857c; font-size: 10px }.product-number { display: grid; place-items: center; width: 26px; height: 26px; border-radius: 50%; background: #eaf4d8; color: #4a8c25; font-weight: 900 }.product-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px }.product-grid span { display: flex; flex-direction: column; gap: 3px; padding: 7px; border-radius: 8px; background: #f7f9f4 }.loss { background: #fff8f0 !important }.loss b { color: #d97b1a }.observation { display: flex; flex-direction: column; gap: 8px; margin-top: 12px; padding: 10px 12px; border-radius: 10px; background: #f7f9f4; color: #556158; font-size: 12px }.confirm-row { display: flex; align-items: center; gap: 9px; margin-top: 14px; padding: 10px 12px; border: 1px solid #c8e0a0; border-radius: 9px; background: #f0f7e8; cursor: pointer; color: #4a8c25; font-size: 12px; font-weight: 700 }.confirm-check { width: 18px; height: 18px; border: 2px solid #82bd43; border-radius: 5px; background: #fff; display: grid; place-items: center; flex-shrink: 0 }.confirm-check.checked { background: #4a8c25; border-color: #4a8c25 }.empty { padding: 18px; text-align: center; color: #7b857c }.dialog-footer { display: flex; flex-shrink: 0; justify-content: flex-end; gap: 10px; padding: 14px 20px 18px; border-top: 1px solid #e4edd8 }
@media (max-width: 700px) { .dialog-card { width: 100vw; max-height: 100vh; border-radius: 0 }.summary-grid { grid-template-columns: repeat(2, 1fr) }.product-grid { grid-template-columns: repeat(2, 1fr) }.dialog-footer { flex-wrap: wrap }.dialog-footer .q-btn { flex: 1 } }
</style>

<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card">
      <div class="accent-bar" />
      <div class="dialog-header">
        <div class="header-left">
          <div class="send-icon"><q-icon name="send" size="22px" /></div>
          <div><div class="dialog-title">Enviar a aprobación</div><div class="dialog-sub">Revisión del ajuste de inventario</div></div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">
        <div class="summary-grid">
          <div><span>Ajuste</span><b>{{ ajuste?.codigo || '--' }}</b></div>
          <div><span>Tipo</span><b>{{ ajuste?.tipo || '--' }}</b></div>
          <div><span>Motivo</span><b>{{ texto(ajuste?.motivo) }}</b></div>
          <div><span>Cantidad total</span><b>{{ ajuste?.cantidadTotal ?? 0 }}</b></div>
        </div>

        <div class="warning-box"><q-icon name="info" size="18px" /><p>Al enviar, el ajuste pasará a <strong>PENDIENTE DE APROBACIÓN</strong>. No se modificará el stock hasta que sea aprobado y aplicado.</p></div>

        <div class="section-title">Productos del ajuste ({{ ajuste?.detalles?.length || 0 }})</div>
        <div v-if="ajuste?.detalles?.length" class="products-list">
          <div v-for="(item, index) in ajuste.detalles" :key="item.id || index" class="product-card">
            <div class="product-head"><span class="product-number">{{ index + 1 }}</span><div><b>{{ item.producto || item.codigoProducto || '--' }}</b><small>{{ item.codigoProducto || 'Sin código' }}</small></div><q-badge :color="ajuste.tipo === 'ENTRADA' ? 'blue-7' : 'red-6'" :label="`${item.cantidad} ${item.unidad || ''}`" /></div>
            <div class="product-grid"><span class="quantity"><small>{{ ajuste.tipo === 'ENTRADA' ? 'Cantidad que entra' : 'Cantidad que sale' }}</small><b>{{ item.cantidad ?? 0 }} {{ item.unidad || '' }}</b></span><span><small>Almacén</small><b>{{ item.almacen || '--' }}</b></span><span><small>Ubicación</small><b>{{ item.ubicacion || '--' }}</b></span><span><small>Lote</small><b>{{ item.codigoLote || 'Sin lote' }}</b></span><span><small>Stock anterior</small><b>{{ item.stockAnterior ?? 0 }} {{ item.unidad || '' }}</b></span><span><small>Precio de venta</small><b>Bs {{ dinero(item.precioVenta) }}</b></span><span><small>Costo unitario</small><b>Bs {{ dinero(item.costoUnitario) }}</b></span><span><small>{{ ajuste.tipo === 'ENTRADA' ? 'Ganancia estimada' : 'Pérdida estimada' }}</small><b>Bs {{ dinero(ajuste.tipo === 'ENTRADA' ? ganancia(item) : item.valorTotal) }}</b></span></div>
          </div>
        </div>
        <div v-else class="empty">No hay productos registrados en este ajuste.</div>

        <div v-if="ajuste?.observacion" class="observation"><b>Observación</b><span>{{ ajuste.observacion }}</span></div>
        <div class="confirm-row" @click="confirmado = !confirmado"><div class="confirm-check" :class="{ checked: confirmado }"><q-icon v-if="confirmado" name="check" size="11px" color="white" /></div><span>Confirmo que revisé los productos y deseo enviar este ajuste a aprobación</span></div>
      </q-card-section>

      <div class="dialog-footer"><q-btn flat no-caps label="Cancelar" icon="close" color="grey-7" @click="cerrar" /><q-btn unelevated no-caps color="orange-8" icon="send" label="Enviar a aprobación" :disable="!confirmado || loading" :loading="loading" @click="$emit('confirmar', ajuste)" /></div>
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
function cerrar() { confirmado.value = false; abierto.value = false }
</script>

<style scoped>
.dialog-card { width: min(920px, 96vw); max-height: 92vh; border-radius: 20px; overflow: hidden; background: #fff; border: 1px solid #e4edd8; box-shadow: 0 20px 60px rgba(42,92,26,.15) }.accent-bar { height: 3px; background: linear-gradient(90deg, #82bd43, #d97b1a, #e6a03b) }.dialog-header { display: flex; align-items: flex-start; justify-content: space-between; background: #fff8f0; border-bottom: 1px solid #f5dbb8; padding: 16px 20px 14px }.header-left { display: flex; align-items: center; gap: 12px }.send-icon { width: 44px; height: 44px; border-radius: 12px; display: flex; align-items: center; justify-content: center; background: #fef3e2; border: 1.5px solid #f5c97a; color: #d97b1a }.dialog-title { font-size: 15px; font-weight: 900; color: #2a5c1a }.dialog-sub { font-size: 11px; color: #9dbf78; font-weight: 600 }.close-btn { color: #7aaa4e !important; background: #f0f7e8 !important }.dialog-body { overflow-y: auto; padding: 18px 20px 8px !important }.summary-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px }.summary-grid div { display: flex; flex-direction: column; gap: 4px; padding: 10px 12px; border: 1px solid #e4edd8; border-radius: 10px; background: #f7f9f4 }.summary-grid span, .product-grid small { color: #7b857c; font-size: 10px }.summary-grid b, .product-grid b { color: #2a5c1a; font-size: 12px }.warning-box { display: flex; gap: 10px; align-items: flex-start; margin: 14px 0; padding: 11px 13px; border: 1px solid #f5c97a; border-radius: 10px; background: #fff8f0; color: #d97b1a }.warning-box p { margin: 0; color: #a05c10; font-size: 12px; line-height: 1.5 }.warning-box strong { color: #2a5c1a }.section-title { margin: 14px 0 8px; font-weight: 900; color: #354634 }.products-list { display: grid; gap: 10px }.product-card { border: 1px solid #e1eadf; border-radius: 12px; padding: 12px; background: #fff }.product-head { display: flex; align-items: center; gap: 10px; margin-bottom: 10px }.product-head > div { flex: 1 }.product-head b { display: block; color: #263827; font-size: 13px }.product-head small { color: #7b857c; font-size: 10px }.product-number { display: grid; place-items: center; width: 26px; height: 26px; border-radius: 50%; background: #eaf4d8; color: #4a8c25; font-weight: 900 }.product-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 8px }.product-grid span { display: flex; flex-direction: column; gap: 3px; padding: 7px; border-radius: 8px; background: #f7f9f4 }.observation { display: flex; gap: 8px; flex-direction: column; margin-top: 12px; padding: 10px 12px; border-radius: 10px; background: #f7f9f4; color: #556158; font-size: 12px }.confirm-row { display: flex; align-items: center; gap: 9px; margin-top: 14px; padding: 10px 12px; border: 1px solid #f5c97a; border-radius: 9px; background: #fef3e2; cursor: pointer; color: #4a8c25; font-size: 12px; font-weight: 700 }.confirm-check { width: 18px; height: 18px; border: 2px solid #d97b1a; border-radius: 5px; background: #fff; display: grid; place-items: center; flex-shrink: 0 }.confirm-check.checked { background: #d97b1a }.empty { padding: 18px; text-align: center; color: #7b857c }.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 14px 20px 18px; border-top: 1px solid #e4edd8 }
@media (max-width: 700px) { .dialog-card { width: 100vw; max-height: 100vh; border-radius: 0 }.summary-grid { grid-template-columns: repeat(2, 1fr) }.product-grid { grid-template-columns: repeat(2, 1fr) }.dialog-footer { flex-wrap: wrap }.dialog-footer .q-btn { flex: 1 } }
.product-grid .quantity { background: #eef8e9; border: 1px solid #c8e0a0; }
.product-grid .quantity b { color: #2f6f2c; font-size: 14px; }
.dialog-card { display: flex; flex-direction: column; }
.dialog-header, .dialog-footer, .accent-bar { flex-shrink: 0; }
.dialog-body { flex: 1 1 auto; min-height: 0; overflow-y: auto; }
</style>

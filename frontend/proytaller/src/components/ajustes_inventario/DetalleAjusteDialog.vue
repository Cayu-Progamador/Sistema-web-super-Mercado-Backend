<template>
  <q-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)">
    <q-card class="detail">
      <q-toolbar class="toolbar">
        <q-icon name="tune" color="green-8" size="28px" />
        <q-toolbar-title><div class="title">Detalle del ajuste <span>{{ ajuste?.codigo || '--' }}</span></div><div class="sub">Trazabilidad de la operación de inventario</div></q-toolbar-title>
        <q-badge :color="colorEstado" :label="textoEstado" /><q-btn flat round dense icon="close" class="q-ml-md" v-close-popup />
      </q-toolbar>

      <q-card-section class="body">
        <div class="grid">
          <div><span>Tipo</span><b>{{ ajuste?.tipo || '--' }}</b></div><div><span>Motivo</span><b>{{ texto(ajuste?.motivo) }}</b></div><div><span>Solicitado por</span><b>{{ ajuste?.creadoPor || ajuste?.solicitante || '--' }}</b></div><div><span>Fecha</span><b>{{ fecha(ajuste?.fechaCreacion || ajuste?.fecha) }}</b></div><div><span>Responsable</span><b>{{ ajuste?.responsableLevantamiento || '--' }}</b></div><div><span>Productos</span><b>{{ detalles.length }}</b></div>
        </div>

        <q-banner rounded class="result q-mt-lg"><div><span>Cantidad total</span><b>{{ ajuste?.cantidadTotal ?? ajuste?.cantidad ?? 0 }}</b></div><div><span>{{ esSalida ? 'Pérdida estimada' : 'Ganancia estimada' }}</span><b :class="{ loss: esSalida }">Bs {{ dinero(esSalida ? ajuste?.valorTotal : gananciaTotal) }}</b></div></q-banner>

        <div class="section">Productos del ajuste</div>
        <div v-if="detalles.length" class="products">
          <div v-for="(item, index) in detalles" :key="item.id || index" class="product-card">
            <div class="product-head"><span class="number">{{ index + 1 }}</span><div><b>{{ item.producto || item.codigoProducto || '--' }}</b><small>{{ item.codigoProducto || 'Sin código' }} · {{ item.codigoLote || 'Sin lote' }}</small></div><q-badge :color="esSalida ? 'red-6' : 'blue-7'" :label="`${item.cantidad ?? 0} ${item.unidad || ''}`" /></div>
            <div class="product-grid"><div><span>Almacén</span><b>{{ item.almacen || '--' }}</b></div><div><span>Ubicación</span><b>{{ item.ubicacion || '--' }}</b></div><div><span>Vencimiento</span><b>{{ item.fechaVencimiento || '--' }}</b></div><div><span>Stock anterior</span><b>{{ item.stockAnterior ?? '--' }} {{ item.unidad || '' }}</b></div><div><span>Stock resultante</span><b>{{ item.stockResultante ?? '--' }} {{ item.unidad || '' }}</b></div><div><span>Precio de venta</span><b>Bs {{ dinero(item.precioVenta) }}</b></div><div><span>Costo unitario</span><b>Bs {{ dinero(item.costoUnitario) }}</b></div><div class="money"><span>{{ esSalida ? 'Pérdida estimada' : 'Ganancia estimada' }}</span><b>Bs {{ dinero(esSalida ? item.valorTotal : ganancia(item)) }}</b></div></div>
          </div>
        </div>
        <div v-else class="empty">No hay productos registrados en este ajuste.</div>

        <div class="section">Observación</div><p class="observation">{{ ajuste?.observacion || '--' }}</p>
        <div class="section">Trazabilidad</div>
        <q-timeline color="green-8" layout="dense"><q-timeline-entry title="Creado como borrador" :subtitle="fecha(ajuste?.fechaCreacion || ajuste?.fecha)" icon="add_task" /><q-timeline-entry v-if="['PENDIENTE_APROBACION', 'APROBADO', 'APLICADO'].includes(ajuste?.estado)" title="Enviado a aprobación" icon="send" /><q-timeline-entry v-if="['APROBADO', 'APLICADO'].includes(ajuste?.estado)" title="Aprobado" icon="verified" /><q-timeline-entry v-if="ajuste?.estado === 'APLICADO'" title="Aplicado en existencias y Kardex" icon="check_circle" /><q-timeline-entry v-if="ajuste?.estado === 'RECHAZADO'" title="Rechazado" icon="block" /></q-timeline>
      </q-card-section>

      <q-card-actions align="right" class="actions"><q-btn flat no-caps label="Cerrar" v-close-popup /><q-btn v-if="ajuste?.estado === 'APLICADO'" unelevated no-caps color="green-8" icon="picture_as_pdf" label="Descargar comprobante" @click="$emit('pdf', ajuste)" /></q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ modelValue: Boolean, ajuste: Object })
defineEmits(['update:modelValue', 'pdf'])
const detalles = computed(() => props.ajuste?.detalles || [])
const esSalida = computed(() => props.ajuste?.tipo === 'SALIDA')
const gananciaTotal = computed(() => detalles.value.reduce((total, item) => total + ganancia(item), 0))
const colorEstado = computed(() => ({ BORRADOR: 'grey-7', PENDIENTE_APROBACION: 'amber-8', APROBADO: 'blue-7', APLICADO: 'green-7', RECHAZADO: 'red-7', CANCELADO: 'red-5' }[props.ajuste?.estado] || 'grey-6'))
const textoEstado = computed(() => texto(props.ajuste?.estado))
const texto = (value) => value?.replaceAll('_', ' ') || '--'
const dinero = (value) => Number(value || 0).toFixed(2)
const ganancia = (item) => (Number(item.precioVenta || 0) - Number(item.costoUnitario || 0)) * Number(item.cantidad || 0)
const fecha = (value) => value ? String(value).slice(0, 19).replace('T', ' ') : '--'
</script>

<style scoped>
.detail { width: min(980px, 96vw); max-height: 94vh; display: flex; flex-direction: column; border-radius: 18px; background: #f7faf6 }.toolbar { flex-shrink: 0; padding: 16px 22px; background: linear-gradient(135deg, #f1f9ec, #fff); border-bottom: 1px solid #d8e5d3 }.title { font-size: 19px; font-weight: 900; color: #243323 }.title span { color: #4a8c25 }.sub, .grid span, .product-grid span { font-size: 11px; color: #7b857c }.body { overflow-y: auto; min-height: 0 }.grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px }.grid div { display: flex; flex-direction: column; gap: 4px; padding: 11px; border: 1px solid #e1eadf; background: #fff; border-radius: 12px }.grid b, .product-grid b { font-size: 13px; color: #263827 }.result { display: flex; justify-content: space-between; gap: 16px; background: #eef8e9; color: #34572b }.result div { display: flex; flex-direction: column; gap: 3px }.result span { font-size: 11px }.loss, .money b { color: #c62828 !important }.section { font-size: 14px; font-weight: 900; margin: 20px 0 9px; color: #354634 }.products { display: grid; gap: 10px }.product-card { border: 1px solid #e1eadf; border-radius: 12px; padding: 12px; background: #fff }.product-head { display: flex; align-items: center; gap: 10px; margin-bottom: 10px }.product-head > div { flex: 1 }.product-head b { display: block; color: #263827; font-size: 13px }.product-head small { color: #7b857c; font-size: 10px }.number { display: grid; place-items: center; width: 26px; height: 26px; border-radius: 50%; background: #eaf4d8; color: #4a8c25; font-weight: 900 }.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px }.product-grid div { display: flex; flex-direction: column; gap: 3px; padding: 7px; border-radius: 8px; background: #f7f9f4 }.product-grid .money { background: #fff3e5 }.observation { padding: 12px; margin: 0; border-radius: 10px; background: #fff; border: 1px solid #e1eadf; color: #556158; white-space: pre-wrap }.empty { padding: 18px; text-align: center; color: #7b857c }.actions { flex-shrink: 0; border-top: 1px solid #dfe7dc; background: #fff }
.section { display: flex; align-items: center; gap: 8px; padding: 9px 12px; border-left: 4px solid #6fa63a; border-radius: 8px; background: linear-gradient(90deg, #eef8e9, #fff); color: #2f5f25; }
.products { gap: 12px; }
.product-card { border-color: #d8e5d3; border-radius: 14px; padding: 14px; box-shadow: 0 3px 12px rgba(52,87,43,.06); }
.product-head { margin-bottom: 12px; padding-bottom: 10px; border-bottom: 1px solid #edf2ea; }
.product-head > div { min-width: 0; }
.product-head b { font-weight: 900; }
.number { width: 30px; height: 30px; border-radius: 10px; flex-shrink: 0; }
.product-grid div { min-width: 0; padding: 8px 9px; border: 1px solid #edf2ea; border-radius: 9px; background: #f8fbf7; }
.product-grid div b { overflow-wrap: anywhere; }
.product-grid .money { background: #fff3e5; border-color: #f5c97a; }
@media (max-width: 650px) { .detail { width: 100vw; max-height: 100vh; border-radius: 0 }.grid { grid-template-columns: repeat(2, 1fr) }.product-grid { grid-template-columns: repeat(2, 1fr) }.result { flex-direction: column; gap: 8px } }
</style>

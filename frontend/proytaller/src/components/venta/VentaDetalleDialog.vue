<template>
  <q-dialog v-model="visible">
    <q-card class="receipt-detail">
      <q-card-section class="receipt-head">
        <q-icon name="storefront" size="30px" color="green-8" />
        <div class="receipt-store">NOMBRE DEL SUPERMERCADO</div>
        <div class="receipt-title">COMPROBANTE DE VENTA</div>
        <div class="receipt-address">AV. PRINCIPAL #100, LA PAZ - BOLIVIA</div>
        <div>NIT: 000000000</div>
        <div>Teléfono: 70000000</div>
      </q-card-section>
      <q-separator />
      <q-card-section v-if="venta" class="receipt-body">
        <div class="receipt-status"><span><q-icon name="check_circle" /> Venta confirmada</span><strong>{{ venta.numero }}</strong></div>
        <div class="receipt-info"><div><small>Fecha y hora</small><b>{{ venta.fecha }}</b></div><div><small>Cliente</small><b>{{ venta.cliente }}</b></div><div><small>Método de pago</small><b>{{ venta.metodo }}</b></div><div><small>Productos</small><b>{{ venta.items }}</b></div></div>
        <div class="products-title">Productos vendidos</div>
        <div class="products-table"><div class="product-row product-head"><span>Código / producto</span><span>Unidad</span><span>Cant.</span><span>P. unit.</span><span>Importe</span></div><div v-for="(item, index) in venta.detalles" :key="item.codigoProducto + index" class="product-row"><span><b>{{ item.codigoProducto }}</b><small>{{ item.producto }}</small></span><span>{{ item.unidad }}</span><span>{{ item.cantidad }}</span><span>Bs {{ Number(item.precioUnitario || 0).toFixed(2) }}</span><strong>Bs {{ Number(item.importe || 0).toFixed(2) }}</strong></div></div>
        <div class="receipt-summary"><div><span>Subtotal</span><b>Bs {{ Number(venta.subtotal || 0).toFixed(2) }}</b></div><div><span>Descuento</span><b>Bs {{ Number(venta.descuento || 0).toFixed(2) }}</b></div></div>
        <div class="receipt-total"><span>Total</span><strong>Bs {{ Number(venta.total || 0).toFixed(2) }}</strong></div>
        <div class="payment-details">
          <div class="payment-row"><span>Método de pago</span><b>{{ venta.metodo }}</b></div>
          <template v-if="venta.metodo === 'CREDITO'">
            <div class="payment-row"><span>Pago inicial</span><b>Bs {{ Number(venta.pagoInicial || 0).toFixed(2) }}</b></div>
            <div class="payment-row"><span>Saldo pendiente</span><b>Bs {{ Number(venta.saldoPendiente || 0).toFixed(2) }}</b></div>
            <div class="payment-row"><span>Estado</span><b>{{ venta.estadoCuenta || 'PENDIENTE' }}</b></div>
            <div class="payment-row"><span>Fecha límite</span><b>{{ venta.fechaLimite || '--' }}</b></div>
          </template>
          <template v-else>
            <div v-if="venta.metodo === 'MIXTO' || venta.metodo === 'EFECTIVO'" class="payment-row"><span>Efectivo recibido</span><b>Bs {{ Number(venta.montoEfectivo || venta.totalRecibido || 0).toFixed(2) }}</b></div>
            <div v-if="venta.metodo === 'MIXTO' || venta.metodo === 'QR'" class="payment-row"><span>Pago por QR</span><b>Bs {{ Number(venta.montoQr || 0).toFixed(2) }}</b></div>
            <div v-if="venta.metodo === 'MIXTO'" class="payment-row"><span>Total recibido</span><b>Bs {{ Number(venta.totalRecibido || 0).toFixed(2) }}</b></div>
            <div v-if="venta.referenciaQr" class="payment-row"><span>Referencia QR</span><b>{{ venta.referenciaQr }}</b></div>
            <div class="payment-row"><span>Cambio a entregar</span><b>Bs {{ Number(venta.cambio || 0).toFixed(2) }}</b></div>
          </template>
        </div>
        <div class="receipt-note">Detalle completo de la venta registrada.</div>
      </q-card-section>
      <q-card-actions align="right"><q-btn flat label="Cerrar" color="green-8" no-caps v-close-popup /></q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ modelValue: Boolean, venta: Object })
const emit = defineEmits(['update:modelValue'])
const visible = computed({ get: () => props.modelValue, set: value => emit('update:modelValue', value) })
</script>

<style scoped>
.receipt-detail{width:430px;max-width:94vw;max-height:90vh;border:1px solid #dce9df;border-radius:18px;background:#fff;color:#263238;overflow-y:auto;box-shadow:0 18px 55px rgba(34,75,42,.2)}
.receipt-head{text-align:center;padding:20px 24px 14px;background:linear-gradient(180deg,#f2faf3,#fff);font-size:10px;line-height:1.45;color:#60756a}.receipt-store{margin-top:5px;color:#1b5e20;font-size:18px;font-weight:900;line-height:1.15;letter-spacing:.4px}.receipt-title{color:#52685a;font-size:11px;font-weight:900;letter-spacing:.8px;margin:4px 0;text-transform:uppercase}.receipt-address{margin-top:6px;color:#789080}
.receipt-body{padding:17px 22px}.receipt-status{display:flex;align-items:center;justify-content:space-between;gap:10px;border-bottom:1px dashed #b9cdbd;padding:0 0 12px;color:#1b5e20;font-size:12px}.receipt-status span{display:flex;align-items:center;gap:5px;font-weight:900}.receipt-status strong{color:#37474f;font-size:12px}.receipt-info{display:grid;grid-template-columns:1fr 1fr;gap:12px 18px;padding:15px 0;border-bottom:1px dashed #b9cdbd}.receipt-info div{display:flex;flex-direction:column;gap:4px;min-width:0}.receipt-info small{color:#789080;font-size:10px;text-transform:uppercase;letter-spacing:.4px}.receipt-info b{color:#263238;font-size:12px;overflow-wrap:anywhere}.receipt-total{display:flex;justify-content:space-between;align-items:center;border-top:2px solid #1b5e20;margin-top:14px;padding-top:12px;color:#1b5e20;font-size:17px;font-weight:900}.receipt-total strong{color:#2e7d32;font-size:19px}.receipt-note{text-align:center;color:#789080;font-size:10px;margin-top:16px;padding-top:12px;border-top:1px dotted #c6d5c9}.receipt-detail .q-card__actions{padding:12px 18px;background:#f7fbf7;border-top:1px solid #e1ebe3}.receipt-detail .q-btn{border-radius:9px;font-weight:800}@media(max-width:500px){.receipt-info{grid-template-columns:1fr}}
.products-title{margin-top:15px;color:#1b5e20;font-size:12px;font-weight:900;text-transform:uppercase}.products-table{margin-top:7px;border-top:1px solid #1b5e20;border-bottom:1px solid #1b5e20}.product-row{display:grid;grid-template-columns:minmax(0,1fr) 42px 34px 58px 62px;gap:5px;align-items:center;padding:7px 0;border-bottom:1px dotted #c6d5c9;color:#37474f;font-size:10px}.product-row:last-child{border-bottom:0}.product-row>span:not(:first-child),.product-row>strong{text-align:right}.product-row span:first-child{min-width:0}.product-row b,.product-row small{display:block;overflow-wrap:anywhere}.product-row b{color:#1b5e20;font-size:9px}.product-row small{font-size:10px}.product-head{color:#789080;font-weight:900;text-transform:uppercase;font-size:8px;border-bottom:1px solid #1b5e20}.receipt-summary{margin-top:10px}.receipt-summary>div{display:flex;justify-content:space-between;padding:3px 0;color:#789080;font-size:11px}.receipt-summary b{color:#37474f}
.payment-details{margin-top:12px;padding-top:8px;border-top:1px dashed #b9cdbd}.payment-row{display:flex;justify-content:space-between;gap:12px;padding:4px 0;color:#789080;font-size:11px}.payment-row b{color:#263238;text-align:right;overflow-wrap:anywhere}
</style>

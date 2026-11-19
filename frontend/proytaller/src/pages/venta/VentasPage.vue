<template>
  <q-page class="sales-page">
    <div class="page-wrap">
      <div class="page-heading row items-center q-mb-lg">
        <div><h1 class="title-with-icon"><q-icon name="point_of_sale" color="green-8" size="32px" /> Ventas</h1><p>Registra una nueva venta.</p></div>
        <q-space /><q-btn flat color="orange-8" icon="pause_circle" label="Ventas en espera" no-caps :disable="bloqueada" @click="dialogEspera = true"><q-badge v-if="ventasEspera.length" color="orange-8" floating rounded>{{ ventasEspera.length }}</q-badge></q-btn><q-btn v-if="puedeVerHistorial" flat color="green-8" icon="history" label="Historial de ventas" no-caps to="/historial-ventas" />
      </div>
      <q-banner v-if="bloqueada" rounded class="sales-locked-banner q-mb-lg"><template #avatar><q-icon name="lock" /></template>La caja está cerrada. Abre una caja para habilitar la venta.<template #action><q-btn flat color="green-8" label="Ir a Caja" no-caps @click="irAGestionCaja" /></template></q-banner>
      <div class="row q-col-gutter-lg detail-layout" :class="{ 'sales-locked-layout': bloqueada }">
        <div class="col-12 col-md-5"><div class="search-column-title"><q-icon name="inventory_2" color="green-8" /> Productos</div><VentaProductoSearch :productos="productos" :bloqueada="!sesionCaja?.idSesionCaja" @agregar="agregar" /></div>
        <div class="col-12 col-md-7"><VentaCarrito :items="carrito" :bloqueada="!sesionCaja?.idSesionCaja" @vaciar="carrito = []" @quitar="quitar" @cantidad="cambiarCantidad" @cantidad-directa="cambiarCantidadDirecta" @cantidad-blur="validarCantidadAlSalir" @pagar="abrirPago" @credito="abrirCredito" @espera="ponerEnEspera" /></div>
        <div v-if="bloqueada" class="sales-lock-overlay"><q-icon name="lock" size="30px" /><strong>Caja cerrada</strong><span>Abre una caja para registrar la venta.</span></div>
      </div>
    </div>
  </q-page>

  <q-dialog v-model="dialogCajaCerrada" persistent>
    <q-card class="payment-dialog caja-alert-dialog">
      <q-card-section class="dialog-head row items-center"><q-avatar icon="lock" color="orange-1" text-color="orange-9" /><div class="q-ml-md"><div class="dialog-title alert-title">Caja cerrada</div><div class="dialog-sub">No puedes registrar ventas todavía.</div></div></q-card-section>
      <q-card-section class="caja-alert-content"><q-icon name="point_of_sale" color="orange-8" size="48px" /><p>Debes abrir una caja antes de realizar una venta.</p><small>Ve al módulo Gestión de Caja, abre una caja y vuelve a intentarlo.</small></q-card-section>
      <q-card-actions align="right" class="dialog-actions"><q-btn flat no-caps label="Cerrar" color="grey-7" @click="dialogCajaCerrada = false" /><q-btn unelevated no-caps label="Abrir caja" color="green-8" icon="lock_open" @click="irAGestionCaja" /></q-card-actions>
    </q-card>
  </q-dialog>

  <q-dialog v-model="dialogEspera">
    <q-card class="held-dialog">
      <q-card-section class="dialog-head row items-center"><div><div class="dialog-title"><q-icon name="pause_circle" /> Ventas en espera</div><div class="dialog-sub">Continúa una venta apartada o elimínala.</div></div><q-space /><q-btn flat round dense icon="close" color="grey-7" aria-label="Cerrar" @click="dialogEspera = false" /></q-card-section>
      <q-separator />
      <q-card-section v-if="ventasEspera.length" class="held-list">
        <q-card v-for="venta in ventasEspera" :key="venta.codigo" flat bordered class="held-item">
          <q-card-section class="row items-center q-pb-sm"><div><div class="held-code">{{ venta.codigo }}</div><div class="held-meta">{{ venta.fecha }} · {{ venta.items.length }} producto(s)</div></div><q-space /><strong class="held-total">Bs {{ venta.total.toFixed(2) }}</strong></q-card-section>
          <q-separator />
          <q-card-section class="held-products">{{ venta.items.map((item) => `${item.nombre} x${item.cantidad}`).join(' · ') }}</q-card-section>
          <q-card-actions align="right"><q-btn flat no-caps color="negative" label="Eliminar" icon="delete_outline" @click="eliminarEspera(venta.codigo)" /><q-btn unelevated no-caps color="green-8" label="Continuar venta" icon="play_arrow" @click="continuarVenta(venta.codigo)" /></q-card-actions>
        </q-card>
      </q-card-section>
      <div v-else class="held-empty"><q-icon name="pause_circle_outline" size="52px" color="grey-5" /><div>No hay ventas en espera</div><small>Las ventas apartadas aparecerán aquí.</small></div>
    </q-card>
  </q-dialog>

  <q-dialog v-model="dialogPago" persistent>
    <q-card class="payment-dialog">
      <q-card-section class="dialog-head row items-center"><div><div class="dialog-title"><q-icon name="payments" /> Método de pago</div><div class="dialog-sub">Selecciona cómo se cobrará la venta.</div></div><q-space /><q-btn flat round dense icon="close" color="grey-7" aria-label="Cerrar" @click="dialogPago = false" /></q-card-section>
      <q-separator />
      <q-card-section class="payment-content">
        <div class="payment-total"><span>Total a cobrar</span><strong>Bs {{ totalPago.toFixed(2) }}</strong></div>
        <q-select v-model="pago.metodo" outlined emit-value map-options label="Tipo de pago" :options="metodosPago" class="field" />
        <q-input v-if="pago.metodo === 'EFECTIVO' || pago.metodo === 'MIXTO'" v-model.number="pago.efectivo" outlined type="number" min="0" step="0.01" label="Monto recibido" prefix="Bs" class="field" :error="pago.metodo === 'EFECTIVO' && pago.efectivo < totalPago" error-message="El monto recibido debe cubrir el total." />
        <q-input v-if="pago.metodo === 'QR' || pago.metodo === 'MIXTO'" v-model="pago.referenciaQr" outlined label="Referencia o comprobante QR" placeholder="Ej.: QR-000245" class="field" hint="Identifica el pago realizado." />
        <q-input v-if="pago.metodo === 'MIXTO'" v-model.number="pago.qr" outlined type="number" min="0" step="0.01" label="Monto por QR" prefix="Bs" class="field" :error="sumaMixta !== totalPago" error-message="Efectivo + QR debe ser igual al total." />
        <div v-if="pago.metodo === 'EFECTIVO'" class="payment-result" :class="{ warning: cambio < 0 }"><span>{{ cambio >= 0 ? 'Cambio a entregar' : 'Falta cobrar' }}</span><strong>Bs {{ Math.abs(cambio).toFixed(2) }}</strong></div>
        <div v-if="pago.metodo === 'MIXTO'" class="payment-result" :class="{ warning: sumaMixta < totalPago }"><span>Total recibido</span><strong>Bs {{ sumaMixta.toFixed(2) }}</strong></div>
        <div v-if="pago.metodo === 'MIXTO'" class="payment-result" :class="{ warning: cambioMixto < 0 }"><span>Cambio a entregar</span><strong>Bs {{ Math.max(0, cambioMixto).toFixed(2) }}</strong></div>
      </q-card-section>
      <q-card-actions align="right" class="dialog-actions"><q-btn flat no-caps label="Cancelar" color="grey-7" @click="dialogPago = false" /><q-btn unelevated no-caps label="Confirmar venta" color="green-8" icon="check" :disable="!pagoValido" @click="confirmarPagoBackend" /></q-card-actions>
    </q-card>
  </q-dialog>

  <q-dialog v-model="dialogCredito" persistent>
    <q-card class="payment-dialog credit-dialog">
      <q-card-section class="dialog-head row items-center"><div><div class="dialog-title credit-title"><q-icon name="credit_score" /> Venta a crédito</div><div class="dialog-sub">Registra el cliente y las condiciones de pago.</div></div><q-space /><q-btn flat round dense icon="close" color="grey-7" aria-label="Cerrar" @click="dialogCredito = false" /></q-card-section>
      <q-separator />
      <q-card-section class="payment-content">
        <div class="payment-total"><span>Total de la venta</span><strong>Bs {{ totalCredito.toFixed(2) }}</strong></div>
        <q-select v-model="credito.cliente" outlined emit-value map-options label="Cliente obligado al pago" :options="clientes" class="field" hint="Debe ser un cliente registrado." />
        <div v-if="credito.cliente" class="credit-limit"><span>Límite disponible</span><strong>{{ credito.cliente.limite == null ? 'No configurado' : `Bs ${credito.cliente.limite.toFixed(2)}` }}</strong></div>
        <div v-if="creditoExcedeLimite" class="text-orange-10 bg-orange-1 q-pa-sm q-mb-md rounded-borders text-caption text-weight-bold"><q-icon name="warning" /> El saldo de esta venta supera el límite de crédito del cliente.</div>
        <q-input v-if="credito.metodoInicial !== 'MIXTO'" v-model.number="credito.inicial" outlined type="number" min="0" step="0.01" :label="credito.metodoInicial === 'QR' ? 'Monto pagado por QR' : 'Pago inicial'" prefix="Bs" class="field" :hint="credito.metodoInicial === 'QR' ? 'Registra cuánto pagó mediante QR.' : 'Puede ser Bs 0.'" :disable="credito.metodoInicial === 'NINGUNO'" :error="credito.inicial < 0 || credito.inicial > totalCredito || (credito.metodoInicial === 'QR' && credito.inicial <= 0)" error-message="El monto QR debe ser mayor a cero y no superar el total." />
        <q-select v-model="credito.metodoInicial" outlined emit-value map-options label="Método del pago inicial" :options="metodosIniciales" class="field" />
        <div v-if="credito.metodoInicial === 'MIXTO'" class="mixed-payment-grid"><q-input v-model.number="credito.efectivo" outlined type="number" min="0" step="0.01" label="Inicial en efectivo" prefix="Bs" class="field" /><q-input v-model.number="credito.qr" outlined type="number" min="0" step="0.01" label="Inicial por QR" prefix="Bs" class="field" /></div>
        <q-input v-if="credito.metodoInicial === 'QR' || credito.metodoInicial === 'MIXTO'" v-model="credito.referenciaQr" outlined label="Referencia del pago QR" placeholder="Ej.: QR-000245" class="field" />
        <div class="payment-result"><span>Saldo pendiente</span><strong>Bs {{ saldoCreditoNuevo.toFixed(2) }}</strong></div>
        <q-input v-model="credito.fechaLimite" outlined type="date" label="Fecha límite de pago" class="field" />
        <q-input v-model="credito.observacion" outlined type="textarea" autogrow label="Observación" placeholder="Ej.: Pago quincenal acordado." class="field" />
      </q-card-section>
      <q-card-actions align="right" class="dialog-actions"><q-btn flat no-caps label="Cancelar" color="grey-7" @click="dialogCredito = false" /><q-btn unelevated no-caps label="Guardar venta a crédito" color="orange-8" icon="check" :disable="!creditoValido || creditoExcedeLimite" @click="confirmarCreditoBackend" /></q-card-actions>
    </q-card>
  </q-dialog>
  <q-dialog v-model="dialogComprobante">
    <q-card class="receipt-dialog">
      <q-card-section class="dialog-head row items-center"><div><div class="dialog-title"><q-icon name="receipt_long" /> Comprobante de venta</div><div class="dialog-sub">Venta registrada correctamente.</div></div><q-space /><q-btn flat round dense icon="close" color="grey-7" aria-label="Cerrar" @click="dialogComprobante = false" /></q-card-section>
      <q-separator />
      <q-card-section class="receipt-content">
        <div class="receipt-brand"><div class="receipt-logo"><q-icon name="storefront" /></div><div class="receipt-fiscal"><div class="receipt-store">NOMBRE DEL SUPERMERCADO</div><div class="receipt-business">COMPROBANTE DE VENTA</div><div class="receipt-address">AV. PRINCIPAL #100, LA PAZ - BOLIVIA</div><div>NIT: 000000000</div><div>Teléfono: 70000000</div></div></div>
        <div class="receipt-status"><q-icon name="check_circle" /> <strong class="confirmed-label">Venta confirmada</strong><q-space /><strong>{{ comprobante.codigo }}</strong></div>
        <div class="receipt-info" :class="{ 'receipt-info-six': comprobante.esCredito }"><div><span>Fecha y hora</span><strong>{{ comprobante.fecha }}</strong></div><div><span>Caja</span><strong>{{ comprobante.caja }}</strong></div><div v-if="comprobante.esCredito"><span>CI del cliente</span><strong>{{ comprobante.clienteCi }}</strong></div><div v-if="comprobante.esCredito"><span>Teléfono</span><strong>{{ comprobante.clienteTelefono }}</strong></div><div><span>Vendedor</span><strong>{{ comprobante.vendedor }}</strong></div><div><span>Cliente</span><strong>{{ comprobante.cliente || 'Consumidor final' }}</strong></div></div>
        <div class="receipt-items"><div class="receipt-item receipt-item-head"><span>Código</span><span>Producto</span><span>Unidad</span><span>Cant.</span><span>P. unit.</span><span>Importe</span></div><div v-for="item in comprobante.items" :key="item.id" class="receipt-item"><span>{{ item.codigo }}</span><span class="receipt-product-name">{{ item.nombre }}</span><span>{{ item.presentacion || item.unidad || '--' }}</span><span>{{ formatoCantidad(item.cantidad) }}</span><span>Bs {{ item.precio.toFixed(2) }}</span><strong>Bs {{ (item.precio * item.cantidad).toFixed(2) }}</strong></div></div>
        <div class="receipt-summary"><div><span>Subtotal</span><strong>Bs {{ comprobante.subtotal.toFixed(2) }}</strong></div><div><span>Descuento</span><strong>Bs {{ comprobante.descuento.toFixed(2) }}</strong></div><div class="receipt-total"><span>Total</span><strong>Bs {{ comprobante.total.toFixed(2) }}</strong></div></div>
        <div v-if="!comprobante.esCredito" class="receipt-payment"><div><span>Método de pago</span><strong>{{ comprobante.metodo }}</strong></div><template v-if="comprobante.metodo === 'MIXTO'"><div><span>Efectivo recibido</span><strong>Bs {{ comprobante.efectivo.toFixed(2) }}</strong></div><div><span>Pago por QR</span><strong>Bs {{ comprobante.qr.toFixed(2) }}</strong></div><div><span>Total recibido</span><strong>Bs {{ comprobante.totalRecibido.toFixed(2) }}</strong></div></template><div v-else-if="comprobante.recibido > 0"><span>Monto recibido</span><strong>Bs {{ comprobante.recibido.toFixed(2) }}</strong></div><div v-if="comprobante.qrRef"><span>Referencia QR</span><strong>{{ comprobante.qrRef }}</strong></div><div><span>Cambio a entregar</span><strong>Bs {{ comprobante.cambio.toFixed(2) }}</strong></div></div>
        <div v-if="comprobante.esCredito" class="receipt-credit credit-details"><div><span>Pago inicial</span><strong>Bs {{ comprobante.inicial.toFixed(2) }}</strong></div><div><span>Método inicial</span><strong>{{ comprobante.metodoInicial }}</strong></div><div><span>Saldo pendiente</span><strong>Bs {{ comprobante.saldo.toFixed(2) }}</strong></div><div><span>Fecha límite</span><strong>{{ comprobante.fechaLimite }}</strong></div><div><span>Estado</span><strong>{{ comprobante.saldo > 0 ? (comprobante.inicial > 0 ? 'PARCIAL' : 'PENDIENTE') : 'PAGADO' }}</strong></div><div v-if="comprobante.qrRef"><span>Referencia QR</span><strong>{{ comprobante.qrRef }}</strong></div><div v-if="comprobante.observacion" class="credit-observation"><span>Observación</span><strong>{{ comprobante.observacion }}</strong></div></div>
        <div class="receipt-qr"><div class="qr-preview"><img v-if="comprobante.qrImagen" :src="comprobante.qrImagen" alt="Código QR de verificación" /><q-icon v-else name="qr_code_2" /></div><div><strong>Verificación del comprobante</strong><small>Escanea para consultar la autenticidad de esta venta.</small><span>{{ comprobante.codigo }}</span></div></div>
        <div class="receipt-brand receipt-brand-config"><div class="receipt-logo"><img v-if="empresa.logoUrl" :src="resolverArchivo(empresa.logoUrl)" :alt="empresa.nombreComercial" /><q-icon v-else name="storefront" /></div><div class="receipt-fiscal"><div class="receipt-store">{{ empresa.nombreComercial || 'NOMBRE DEL SUPERMERCADO' }}</div><div class="receipt-business">{{ empresa.razonSocial || 'COMPROBANTE DE VENTA' }}</div><div class="receipt-address">{{ [empresa.direccion, empresa.departamento].filter(Boolean).join(', ') || 'AV. PRINCIPAL #100, LA PAZ - BOLIVIA' }}</div><div v-if="empresa.nit">NIT: {{ empresa.nit }}</div><div v-if="empresa.telefono">Teléfono: {{ empresa.telefono }}</div><div v-if="empresa.correo">{{ empresa.correo }}</div></div></div>
        <div class="receipt-footer">{{ empresa.mensajeComprobante || 'Gracias por su compra. Conserve este comprobante.' }}</div>
      </q-card-section>
      <q-card-actions align="right" class="dialog-actions"><q-btn flat no-caps label="Cerrar" color="grey-7" @click="dialogComprobante = false" /><q-btn flat round dense color="green-8" icon="picture_as_pdf" aria-label="Descargar PDF" @click="descargarPdf"><q-tooltip>Descargar PDF</q-tooltip></q-btn><q-btn flat round dense color="green-8" icon="print" aria-label="Imprimir" @click="imprimirComprobante"><q-tooltip>Imprimir</q-tooltip></q-btn></q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, onActivated, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import QRCode from 'qrcode'
import { storeToRefs } from 'pinia'
import { useQuasar } from 'quasar'
import VentaCarrito from '../../components/venta/VentaCarrito.vue'
import VentaProductoSearch from '../../components/venta/VentaProductoSearch.vue'
import { useVentaStore } from '../../store/venta/ventaStore'
import { crearVenta, descargarComprobante, listarCatalogoVenta } from '../../api/venta/venta'
import { listarClientes } from '../../api/cliente/cliente'
import { obtenerSesionCajaAbierta } from '../../api/caja/caja'
import { useAuthStore } from '../../store/store'
import { obtenerConfiguracionEmpresa } from '../../api/configuracion/configuracionEmpresa'

const $q = useQuasar()
const authStore = useAuthStore()
const puedeVerHistorial = computed(() => authStore.tienePermiso('HISTORIAL_VENTAS_VER'))
const router = useRouter()
const ventaStore = useVentaStore()
const { carrito, ventasEspera } = storeToRefs(ventaStore)
const dialogEspera = ref(false)
const dialogPago = ref(false)
const dialogCajaCerrada = ref(false)
const sesionCaja = ref(null)
const guardandoVenta = ref(false)
const bloqueada = computed(() => !sesionCaja.value?.idSesionCaja)
const totalPago = ref(0)
const dialogCredito = ref(false)
const totalCredito = ref(0)
const dialogComprobante = ref(false)
const comprobante = reactive({ idVenta: null, codigo: '', fecha: '', caja: 'Caja 01', vendedor: '--', items: [], subtotal: 0, descuento: 0, total: 0, metodo: '', cliente: '', clienteCi: '', clienteTelefono: '', metodoInicial: '', observacion: '', esCredito: false, inicial: 0, saldo: 0, fechaLimite: '', cambio: 0, recibido: 0, efectivo: 0, qr: 0, totalRecibido: 0, qrRef: '', qrImagen: '' })
const empresa = reactive({ nombreComercial: '', razonSocial: '', nit: '', telefono: '', correo: '', departamento: '', direccion: '', logoUrl: '', mensajeComprobante: '' })
const resolverArchivo = url => !url || url.startsWith('data:') || url.startsWith('http') ? url : `${import.meta.env.VITE_API_URL || ''}${url}`
const metodosPago = [{ label: 'Efectivo', value: 'EFECTIVO' }, { label: 'QR', value: 'QR' }, { label: 'Mixto', value: 'MIXTO' }]
const clientes = ref([])
const pago = reactive({ metodo: 'EFECTIVO', efectivo: 0, qr: 0, referenciaQr: '', cliente: null, inicial: 0, fechaLimite: '', observacion: '' })
const metodosIniciales = [{ label: 'Sin pago inicial', value: 'NINGUNO' }, { label: 'Efectivo', value: 'EFECTIVO' }, { label: 'QR', value: 'QR' }, { label: 'Mixto', value: 'MIXTO' }]
const credito = reactive({ cliente: null, inicial: 0, efectivo: 0, qr: 0, metodoInicial: 'NINGUNO', referenciaQr: '', fechaLimite: '', observacion: '' })
watch(() => credito.metodoInicial, (metodo) => { if (metodo === 'NINGUNO') { credito.inicial = 0; credito.efectivo = 0; credito.qr = 0; credito.referenciaQr = '' } })
const productos = ref([])
const sumaMixta = computed(() => Number(pago.efectivo || 0) + Number(pago.qr || 0))
const cambio = computed(() => Number(pago.efectivo || 0) - totalPago.value)
const cambioMixto = computed(() => sumaMixta.value - totalPago.value)
const saldoCredito = computed(() => Math.max(0, totalPago.value - Number(pago.inicial || 0)))
const inicialCredito = computed(() => credito.metodoInicial === 'MIXTO' ? Number(credito.efectivo || 0) + Number(credito.qr || 0) : Number(credito.inicial || 0))
const saldoCreditoNuevo = computed(() => Math.max(0, totalCredito.value - inicialCredito.value))
const creditoExcedeLimite = computed(() => credito.cliente?.limite != null && saldoCreditoNuevo.value > Number(credito.cliente.limite))
const creditoValido = computed(() => Boolean(sesionCaja.value?.idSesionCaja && carrito.value.length && credito.cliente && inicialCredito.value >= 0 && inicialCredito.value <= totalCredito.value && credito.fechaLimite && (credito.metodoInicial !== 'QR' || (credito.inicial > 0 && credito.referenciaQr.trim())) && (credito.metodoInicial !== 'MIXTO' || (credito.efectivo > 0 && credito.qr > 0 && credito.referenciaQr.trim()))))
const pagoValido = computed(() => { if (!carrito.value.length || totalPago.value <= 0) return false; if (pago.metodo === 'EFECTIVO') return Number(pago.efectivo || 0) >= totalPago.value; if (pago.metodo === 'QR') return pago.referenciaQr.trim().length > 0; return Number(pago.efectivo || 0) > 0 && Number(pago.qr || 0) > 0 && sumaMixta.value >= totalPago.value && Number(pago.efectivo || 0) >= Math.max(0, totalPago.value - Number(pago.qr || 0)) && pago.referenciaQr.trim().length > 0 })
function agregar(producto) { if (!sesionCaja.value?.idSesionCaja) return dialogCajaCerrada.value = true; const item = carrito.value.find((actual) => actual.id === producto.id); if (item) { if (item.cantidad < producto.stock) item.cantidad++; else $q.notify({ type: 'warning', message: 'La cantidad supera el stock disponible.' }) } else carrito.value.push({ ...producto, cantidad: 1 }) }
function quitar(producto) { if (bloqueada.value) return; carrito.value = carrito.value.filter((item) => item.id !== producto.id) }
function cambiarCantidad(producto, diferencia) { if (bloqueada.value) return; const nuevaCantidad = producto.cantidad + diferencia; if (nuevaCantidad < 1) return quitar(producto); if (nuevaCantidad > producto.stock) return $q.notify({ type: 'warning', message: 'La cantidad supera el stock disponible.' }); producto.cantidad = nuevaCantidad }
function cambiarCantidadDirecta(producto, cantidad) {
  if (bloqueada.value) return
  if (cantidad === '' || cantidad == null) return
  const valor = Number(String(cantidad).replace(',', '.'))
  if (!Number.isFinite(valor)) return
  if (valor <= 0) { producto.cantidad = valor; return }
  if (valor > producto.stock) { producto.cantidad = producto.stock; return $q.notify({ type: 'warning', message: `La cantidad máxima es ${producto.stock}.` }) }
  producto.cantidad = valor
}
function validarCantidadAlSalir(producto) {
  if (bloqueada.value) return
  if (!Number.isFinite(Number(producto.cantidad)) || Number(producto.cantidad) <= 0) {
    producto.cantidad = 1
    return $q.notify({ type: 'warning', message: 'La cantidad debe ser mayor a cero.' })
  }
}
function ponerEnEspera() { if (bloqueada.value || !carrito.value.length) return; const numero = ventasEspera.value.length + 1; ventasEspera.value.push({ codigo: `ESPERA-${String(numero).padStart(3, '0')}`, fecha: new Date().toLocaleString('es-BO'), items: carrito.value.map((item) => ({ ...item })), total: carrito.value.reduce((sum, item) => sum + item.precio * item.cantidad, 0) }); carrito.value = []; $q.notify({ type: 'positive', message: 'Venta puesta en espera.' }) }
function continuarVenta(codigo) { const venta = ventasEspera.value.find((item) => item.codigo === codigo); if (!venta) return; if (carrito.value.length) { $q.notify({ type: 'warning', message: 'Finaliza o aparta la venta actual antes de continuar otra.' }); return } carrito.value = venta.items.map((item) => ({ ...item })); ventasEspera.value = ventasEspera.value.filter((item) => item.codigo !== codigo); dialogEspera.value = false; $q.notify({ type: 'info', message: `${codigo} cargada en el carrito.` }) }
function eliminarEspera(codigo) { ventasEspera.value = ventasEspera.value.filter((item) => item.codigo !== codigo); $q.notify({ type: 'info', message: 'Venta en espera eliminada.' }) }
function abrirPago(total) { if (bloqueada.value) return dialogCajaCerrada.value = true; totalPago.value = Number(total || 0); pago.metodo = 'EFECTIVO'; pago.efectivo = totalPago.value; pago.qr = 0; pago.referenciaQr = ''; pago.cliente = null; pago.inicial = 0; pago.fechaLimite = ''; pago.observacion = ''; dialogPago.value = true }
function abrirCredito(total) { if (bloqueada.value) return dialogCajaCerrada.value = true; totalCredito.value = Number(total || 0); credito.cliente = null; credito.inicial = 0; credito.efectivo = 0; credito.qr = 0; credito.metodoInicial = 'NINGUNO'; credito.referenciaQr = ''; credito.fechaLimite = ''; credito.observacion = ''; dialogCredito.value = true }
function confirmarPago() { prepararComprobante(pago.metodo, '', false, 0, '', pago.metodo === 'MIXTO' ? cambioMixto.value : cambio.value); comprobante.recibido = pago.efectivo; comprobante.efectivo = pago.efectivo; comprobante.qr = pago.qr; comprobante.totalRecibido = sumaMixta.value; comprobante.qrRef = pago.referenciaQr; $q.notify({ type: 'positive', message: 'Pago registrado correctamente.' }); dialogPago.value = false; carrito.value = [] }
function confirmarCredito() { prepararComprobante('CRÉDITO', credito.cliente.nombre, true, inicialCredito.value, credito.fechaLimite, 0); comprobante.metodo = 'CRÉDITO'; comprobante.clienteCi = credito.cliente.ci; comprobante.clienteTelefono = credito.cliente.telefono; comprobante.metodoInicial = credito.metodoInicial; comprobante.observacion = credito.observacion; comprobante.qrRef = credito.referenciaQr; $q.notify({ type: 'positive', message: 'Venta a crédito registrada correctamente.' }); dialogCredito.value = false; carrito.value = [] }
function prepararComprobante(metodo, cliente, esCredito, inicial, fechaLimite, cambioVenta) {
  comprobante.codigo = `V-${Date.now().toString().slice(-8)}`
  comprobante.fecha = new Date().toLocaleString('es-BO')
  comprobante.items = carrito.value.map((item) => ({ ...item }))
  comprobante.subtotal = comprobante.items.reduce((sum, item) => sum + item.precio * item.cantidad, 0)
  comprobante.descuento = 0
  comprobante.total = comprobante.subtotal - comprobante.descuento
  comprobante.metodo = metodo
  comprobante.cliente = cliente
  comprobante.clienteCi = ''
  comprobante.clienteTelefono = ''
  comprobante.metodoInicial = ''
  comprobante.observacion = ''
  comprobante.esCredito = esCredito
  comprobante.inicial = inicial
  comprobante.saldo = Math.max(0, comprobante.total - inicial)
  comprobante.fechaLimite = fechaLimite
  comprobante.cambio = cambioVenta
  comprobante.recibido = 0
  comprobante.efectivo = 0
  comprobante.qr = 0
  comprobante.totalRecibido = 0
  comprobante.qrRef = ''
  comprobante.qrImagen = ''
  QRCode.toDataURL(`${window.location.origin}/verificar-venta/${encodeURIComponent(comprobante.codigo)}`, { width: 180, margin: 1, errorCorrectionLevel: 'M' })
    .then((imagen) => { comprobante.qrImagen = imagen })
  dialogComprobante.value = true
}
function detallePayload() { return carrito.value.map((item) => ({ idExistencia: item.idExistencia, idPresentacion: item.idPresentacion, cantidad: Number(item.cantidad), precioUnitario: Number(item.precio || 0) })) }
function notificarError(error) { $q.notify({ type: 'negative', message: error?.response?.data?.message || error?.response?.data?.error || 'No se pudo registrar la venta.' }) }
function formatoCantidad(valor) { return Number(valor || 0).toLocaleString('es-BO', { minimumFractionDigits: 0, maximumFractionDigits: 2 }) }
async function cargarCatalogo() {
  try {
    productos.value = await listarCatalogoVenta() || []
    console.log('[Ventas] Catálogo recibido del backend:', productos.value)
  } catch (error) {
    console.error('[Ventas] Error cargando catálogo:', error)
    notificarError(error)
  }
}
async function cargarClientes() {
  try {
    const respuesta = await listarClientes({ page: 0, size: 100 })
    clientes.value = (respuesta?.content || []).filter((cliente) => cliente.estado !== false).map((cliente) => {
      const nombre = [cliente.nombres, cliente.apellidoPaterno, cliente.apellidoMaterno].filter(Boolean).join(' ')
      return { label: `${nombre} · CI ${cliente.ci}`, value: { id: cliente.idCliente, nombre, ci: cliente.ci, telefono: cliente.telefono || '--', limite: cliente.limiteCredito == null ? null : Number(cliente.limiteCredito) } }
    })
  } catch (error) { notificarError(error) }
}
async function confirmarPagoBackend() {
  if (guardandoVenta.value) return
  guardandoVenta.value = true
  const recibido = pago.metodo === 'QR' ? totalPago.value : sumaMixta.value
  const payload = { idSesionCaja: sesionCaja.value?.idSesionCaja, detalles: detallePayload(), descuento: 0, pago: { metodo: pago.metodo, montoEfectivo: pago.metodo === 'QR' ? 0 : Number(pago.efectivo || 0), montoQr: pago.metodo === 'EFECTIVO' ? 0 : (pago.metodo === 'QR' ? totalPago.value : Number(pago.qr || 0)), totalRecibido: recibido, cambio: pago.metodo === 'MIXTO' ? cambioMixto.value : (pago.metodo === 'EFECTIVO' ? cambio.value : 0), referenciaQr: pago.referenciaQr || null } }
  try {
    const ventaGuardada = await crearVenta(payload)
    prepararComprobante(pago.metodo, '', false, 0, '', payload.pago.cambio)
    comprobante.idVenta = ventaGuardada?.idVenta
    actualizarCodigoReal(ventaGuardada?.codigo)
    comprobante.vendedor = ventaGuardada?.usuario || '--'
    comprobante.recibido = pago.metodo === 'QR' ? totalPago.value : pago.efectivo; comprobante.efectivo = pago.efectivo; comprobante.qr = payload.pago.montoQr; comprobante.totalRecibido = recibido; comprobante.qrRef = pago.referenciaQr
    $q.notify({ type: 'positive', message: 'Venta registrada correctamente.' }); dialogPago.value = false; ventaStore.limpiarCarrito()
  } catch (error) { notificarError(error) } finally { guardandoVenta.value = false }
}
async function confirmarCreditoBackend() {
  if (guardandoVenta.value) return
  guardandoVenta.value = true
  const payload = { idCliente: credito.cliente.id, idSesionCaja: sesionCaja.value?.idSesionCaja, detalles: detallePayload(), descuento: 0, pago: { metodo: 'CREDITO', montoEfectivo: credito.metodoInicial === 'EFECTIVO' || credito.metodoInicial === 'MIXTO' ? Number(credito.efectivo || credito.inicial || 0) : 0, montoQr: credito.metodoInicial === 'QR' || credito.metodoInicial === 'MIXTO' ? Number(credito.qr || credito.inicial || 0) : 0, totalRecibido: inicialCredito.value, cambio: 0, pagoInicial: inicialCredito.value, fechaLimite: credito.fechaLimite, referenciaQr: credito.referenciaQr || null, observacion: credito.observacion || null } }
  try {
    const ventaGuardada = await crearVenta(payload)
    prepararComprobante('CRÉDITO', credito.cliente.nombre, true, inicialCredito.value, credito.fechaLimite, 0)
    comprobante.idVenta = ventaGuardada?.idVenta
    actualizarCodigoReal(ventaGuardada?.codigo)
    comprobante.vendedor = ventaGuardada?.usuario || '--'
    comprobante.metodo = 'CRÉDITO'; comprobante.clienteCi = credito.cliente.ci; comprobante.clienteTelefono = credito.cliente.telefono; comprobante.metodoInicial = credito.metodoInicial; comprobante.observacion = credito.observacion; comprobante.qrRef = credito.referenciaQr
    $q.notify({ type: 'positive', message: 'Venta a crédito registrada correctamente.' }); dialogCredito.value = false; ventaStore.limpiarCarrito()
  } catch (error) { notificarError(error) } finally { guardandoVenta.value = false }
}
function actualizarCodigoReal(codigo) {
  if (!codigo) return
  comprobante.codigo = codigo
  comprobante.qrImagen = ''
  QRCode.toDataURL(`${window.location.origin}/verificar-venta/${encodeURIComponent(codigo)}`, { width: 180, margin: 1, errorCorrectionLevel: 'M' })
    .then((imagen) => { comprobante.qrImagen = imagen })
}
async function verificarCajaAbierta () { try { sesionCaja.value = await obtenerSesionCajaAbierta(); if (!sesionCaja.value?.idSesionCaja) dialogCajaCerrada.value = true } catch (error) { sesionCaja.value = null; dialogCajaCerrada.value = true } }
function irAGestionCaja () { dialogCajaCerrada.value = false; router.push('/caja') }
onMounted(() => { obtenerConfiguracionEmpresa().then(data => Object.assign(empresa, data || {})).catch(() => {}); cargarCatalogo(); cargarClientes(); verificarCajaAbierta(); window.addEventListener('focus', verificarCajaAbierta) })
onActivated(verificarCajaAbierta)
onBeforeUnmount(() => window.removeEventListener('focus', verificarCajaAbierta))
async function imprimirComprobante() {
  if (!comprobante.idVenta) return $q.notify({ type: 'negative', message: 'No se encontró el identificador de la venta.' })
  const ventana = window.open('', '_blank')
  if (!ventana) return $q.notify({ type: 'warning', message: 'Permite las ventanas emergentes para imprimir el comprobante.' })
  try {
    const archivo = await descargarComprobante(comprobante.idVenta)
    const url = URL.createObjectURL(archivo)
    ventana.location.href = url
    ventana.onload = () => ventana.print()
    window.setTimeout(() => URL.revokeObjectURL(url), 60000)
  } catch (error) {
    ventana.close()
    notificarError(error)
  }
}
async function descargarPdf() {
  if (!comprobante.idVenta) return $q.notify({ type: 'negative', message: 'No se encontró el identificador de la venta.' })
  try {
    const archivo = await descargarComprobante(comprobante.idVenta)
    const url = URL.createObjectURL(archivo)
    const enlace = document.createElement('a')
    enlace.href = url
    enlace.download = `comprobante-${comprobante.codigo}.pdf`
    enlace.click()
    URL.revokeObjectURL(url)
  } catch (error) { notificarError(error) }
}
</script>

<style scoped>
.receipt-content{display:flex;flex-direction:column}.receipt-brand:not(.receipt-brand-config){display:none}.receipt-brand-config{order:-1;display:flex!important}.receipt-brand-config .receipt-logo img{width:34px;height:34px;object-fit:contain;border-radius:50%}
.receipt-brand small{white-space:nowrap;font-size:9px}.receipt-fiscal{text-align:center;color:#60756a;font-size:10px;line-height:1.45}.receipt-fiscal .receipt-address{font-weight:800}.receipt-fiscal .receipt-store{font-size:18px;line-height:1.2}.receipt-fiscal .receipt-business{font-size:11px}.receipt-fiscal>div:not(.receipt-store):not(.receipt-business):not(.receipt-address){white-space:nowrap}
.sales-page{min-height:100vh;background:#f5f7fa;padding:0 24px 24px;margin-top:-14px}.page-wrap{max-width:1440px;margin:auto}.title-with-icon{display:flex;align-items:center;gap:9px;color:#1b5e20;font-size:30px;margin:0;font-weight:900}.page-heading{margin-top:0!important}.page-heading p{color:#789080;margin:0}.detail-layout{align-items:stretch}.search-column-title{display:flex;align-items:center;gap:8px;color:#1b5e20;font-size:17px;font-weight:900;margin:0 0 10px 4px}.payment-dialog{width:520px;max-width:calc(100vw - 28px);border-radius:18px}.dialog-head{padding:18px 20px}.dialog-title{display:flex;align-items:center;gap:8px;color:#1b5e20;font-size:19px;font-weight:900}.dialog-title .q-icon{font-size:24px}.dialog-sub{color:#789080;font-size:12px;margin-top:3px}.payment-content{padding:20px}.payment-total,.payment-result{display:flex;align-items:center;justify-content:space-between;border-radius:12px;padding:13px 15px;margin-bottom:16px;background:#f0f7f1;color:#52705a;font-size:13px}.payment-total strong{color:#1b5e20;font-size:21px}.payment-result{margin:2px 0 0;background:#eef7ef;color:#2e7d32}.payment-result strong{font-size:18px}.payment-result.warning{background:#fff3e0;color:#c45b00}.credit-limit{display:flex;justify-content:space-between;align-items:center;padding:10px 13px;margin:-3px 0 14px;border:1px solid #d9eadb;border-radius:10px;background:#f8fcf8;color:#66806d;font-size:12px}.credit-limit strong{color:#1b5e20;font-size:14px}.mixed-payment-grid{display:grid;grid-template-columns:1fr 1fr;gap:12px}.field{margin-bottom:14px}.dialog-actions{padding:12px 20px 18px}.dialog-actions .q-btn{border-radius:9px;font-weight:800}@media(max-width:700px){.sales-page{padding:0 14px 14px;margin-top:-8px}.title-with-icon{font-size:25px}.page-heading{gap:12px;flex-wrap:wrap}.page-heading .q-btn{width:100%}.payment-content{padding:16px}.mixed-payment-grid{grid-template-columns:1fr}.dialog-actions{padding:10px 16px 16px;flex-wrap:wrap}.dialog-actions .q-btn{flex:1}.payment-dialog{max-height:90vh;overflow:auto}}
.held-dialog{width:680px;max-width:calc(100vw - 28px);border-radius:18px}.held-list{max-height:60vh;overflow:auto;padding:16px 20px}.held-item{border-color:#e1e8e2;border-radius:13px;margin-bottom:12px}.held-code{color:#1b5e20;font-weight:900}.held-meta{color:#789080;font-size:12px;margin-top:3px}.held-total{color:#2e7d32}.held-products{color:#52685a;font-size:13px}.held-empty{min-height:190px;display:flex;flex-direction:column;align-items:center;justify-content:center;color:#60756a;text-align:center}.held-empty div{font-weight:800;margin-top:8px}.held-empty small{color:#8a9990}@media(max-width:700px){.held-dialog{max-height:90vh;overflow:auto}.held-list{padding:12px}}
.receipt-dialog{width:600px;max-width:calc(100vw - 28px);border-radius:18px}.receipt-content{padding:22px 26px;background:#fff}.receipt-brand{display:flex;align-items:center;justify-content:center;gap:11px;text-align:left;padding-bottom:17px;border-bottom:1px dashed #cbd8cd}.receipt-logo{display:flex;align-items:center;justify-content:center;width:42px;height:42px;border-radius:50%;background:#1b5e20;color:#fff;font-size:25px}.receipt-store{color:#1b5e20;font-size:20px;font-weight:900;letter-spacing:.5px}.receipt-business{color:#60756a;font-size:12px}.receipt-brand small{color:#8a9990;font-size:10px}.receipt-status{display:flex;align-items:center;gap:8px;padding:12px 0;border-bottom:1px solid #edf2ee;color:#2e7d32;font-weight:800}.receipt-status strong{font-size:12px;color:#55705c}.receipt-info{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin:15px 0}.receipt-info div,.receipt-credit div{display:flex;flex-direction:column;gap:4px}.receipt-info span,.receipt-credit span{color:#789080;font-size:11px}.receipt-info strong,.receipt-credit strong{color:#37474f;font-size:12px}.receipt-items{border-top:1px solid #dfeae1;border-bottom:1px solid #dfeae1;padding:5px 0}.receipt-item{display:flex;justify-content:space-between;gap:15px;padding:8px 0;color:#52685a;font-size:12px}.receipt-item strong{color:#37474f;text-align:right}.receipt-item-head{color:#789080;font-size:10px;font-weight:800;text-transform:uppercase}.receipt-total,.receipt-change{display:flex;justify-content:space-between;align-items:center;padding:15px 0;font-size:19px;font-weight:900;color:#1b5e20}.receipt-total strong,.receipt-change strong{color:#2e7d32}.receipt-credit{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;padding:12px;border-radius:11px;background:#fff8ed}.receipt-change{padding:12px;border-radius:11px;background:#eef7ef;font-size:14px}.receipt-qr{display:flex;align-items:center;justify-content:center;gap:12px;padding:15px 0;border-top:1px dashed #cbd8cd;margin-top:15px}.qr-preview{display:flex;align-items:center;justify-content:center;width:72px;height:72px;color:#263238}.qr-preview .q-icon{font-size:72px}.receipt-qr div:last-child{display:flex;flex-direction:column;gap:3px}.receipt-qr strong{color:#37474f;font-size:12px}.receipt-qr small{color:#789080;font-size:10px;max-width:220px}.receipt-qr span{color:#1b5e20;font-size:11px;font-weight:800}.receipt-footer{padding-top:12px;text-align:center;color:#789080;font-size:11px;font-style:italic}@media(max-width:600px){.receipt-info,.receipt-credit{grid-template-columns:1fr}.receipt-content{padding:18px}.receipt-item strong{max-width:52%;font-size:11px}.receipt-dialog{max-height:90vh;overflow:auto}.receipt-qr{align-items:flex-start}}
.receipt-dialog{overflow:hidden;box-shadow:0 18px 55px rgba(38,80,43,.2)}.receipt-dialog .dialog-head{background:#f7fbf7;border-bottom:1px solid #e2ece3}.receipt-content{background:linear-gradient(#fff 0 96%,#f7faf7 96%);font-family:Arial,sans-serif}.receipt-brand{flex-direction:column;gap:4px;text-align:center}.receipt-logo{width:48px;height:48px;font-size:28px;margin-bottom:3px}.receipt-store{font-size:23px;letter-spacing:1px}.receipt-business{font-size:12px;text-transform:uppercase;letter-spacing:.7px}.receipt-brand small{font-size:10px}.receipt-status{margin-top:3px;border-bottom:1px dashed #cbd8cd}.receipt-info{padding:12px 0;border-bottom:1px dashed #cbd8cd}.receipt-info div{gap:3px}.receipt-items{margin-top:14px}.receipt-item{display:grid;grid-template-columns:minmax(0,1fr) 48px 78px 82px;align-items:center;gap:8px}.receipt-item span:nth-child(2),.receipt-item span:nth-child(3),.receipt-item strong{text-align:right}.receipt-item-head{grid-template-columns:minmax(0,1fr) 48px 78px 82px}.receipt-total{border-top:2px solid #1b5e20;margin-top:12px}.receipt-credit{border:1px solid #f0d9ae}.receipt-qr{border-top:1px dashed #cbd8cd}.receipt-footer{letter-spacing:.2px}.dialog-actions{background:#f7fbf7}@media print{body *{visibility:hidden!important}.receipt-dialog,.receipt-dialog *{visibility:visible!important}.receipt-dialog{position:absolute!important;left:0!important;top:0!important;width:100%!important;max-width:none!important;box-shadow:none!important}.receipt-dialog .dialog-actions,.receipt-dialog .q-btn{display:none!important}.receipt-content{padding:20px!important}}
.receipt-dialog{max-height:90vh;display:flex;flex-direction:column}.receipt-dialog .receipt-content{overflow-y:auto;min-height:0}.receipt-dialog .dialog-actions{flex-shrink:0}
.receipt-item span:first-child{min-width:0}.receipt-item span:first-child b{display:block;color:#1b5e20;font-size:10px}.receipt-item span:first-child small{display:block;color:#8a9990;font-size:10px}.receipt-summary{margin-top:8px}.receipt-summary>div,.receipt-payment>div{display:flex;justify-content:space-between;padding:5px 0;color:#789080;font-size:12px}.receipt-summary strong,.receipt-payment strong{color:#37474f}.receipt-summary .receipt-total{padding:12px 0}.receipt-payment{border-top:1px dashed #cbd8cd;margin-top:10px;padding-top:8px}.receipt-payment strong{max-width:65%;text-align:right;overflow-wrap:anywhere}
.receipt-change{margin-top:12px;padding:15px 16px;border:2px solid #2e7d32;border-radius:10px;background:#f0f8f1;color:#1b5e20;font-size:13px;letter-spacing:.35px}.receipt-change strong{font-size:22px;color:#1b5e20}
.receipt-status span{font-weight:900;color:#1b5e20}
.receipt-status .confirmed-label{font-size:15px;font-weight:900;color:#1b5e20}
.receipt-info-four{grid-template-columns:repeat(4,1fr)}@media(max-width:900px){.receipt-info-four{grid-template-columns:repeat(2,1fr)}}
.receipt-item,.receipt-item-head{grid-template-columns:78px minmax(0,1fr) 45px 75px 82px}.receipt-item span:nth-child(3),.receipt-item span:nth-child(4),.receipt-item strong{text-align:right}@media(max-width:600px){.receipt-item,.receipt-item-head{grid-template-columns:58px minmax(0,1fr) 38px 65px 72px;gap:5px}.receipt-item{font-size:10px}}
@media print{@page{size:80mm auto;margin:4mm}.receipt-dialog{position:static!important;width:72mm!important;max-width:none!important;max-height:none!important;overflow:visible!important;border-radius:0!important;box-shadow:none!important}.receipt-dialog .receipt-content{width:72mm!important;max-height:none!important;overflow:visible!important;padding:4mm!important}.receipt-dialog .dialog-head,.receipt-dialog .dialog-actions,.receipt-dialog~*{display:none!important}.receipt-brand{padding-bottom:3mm}.receipt-store{font-size:17px}.receipt-info{grid-template-columns:1fr 1fr!important;gap:2mm;margin:3mm 0}.receipt-items{margin-top:3mm}.receipt-item,.receipt-item-head{grid-template-columns:14mm minmax(0,1fr) 9mm 16mm 19mm;gap:1mm;font-size:9px}.receipt-item-head{font-size:8px}.receipt-item span:first-child{overflow-wrap:anywhere}.receipt-total{font-size:15px}.receipt-qr .qr-preview{width:18mm;height:18mm}.receipt-qr .qr-preview .q-icon{font-size:18mm}.receipt-footer{font-size:9px}}
@media print{:deep(.q-dialog__inner),:deep(.q-dialog__inner--minimized){position:static!important;display:block!important;max-height:none!important;height:auto!important;min-height:0!important;overflow:visible!important;padding:0!important}:deep(.q-dialog__backdrop){display:none!important}.receipt-dialog{margin:0!important;transform:none!important}.receipt-dialog .receipt-content{height:auto!important;overflow:visible!important}}
.receipt-credit.credit-details{display:flex;flex-direction:column;gap:0;padding:8px 0;background:#fff;border:0;border-top:1px dashed #cbd8cd;border-bottom:1px dashed #cbd8cd;border-radius:0}.credit-details>div{display:flex;flex-direction:row;justify-content:space-between;align-items:center;gap:16px;padding:7px 0;border-bottom:1px dotted #dce6de}.credit-details>div:last-child{border-bottom:0}.credit-details span{color:#60756a;font-size:11px}.credit-details strong{color:#37474f;font-size:12px;text-align:right}.credit-details .credit-observation{align-items:flex-start}.credit-details .credit-observation strong{max-width:70%;overflow-wrap:anywhere}
.receipt-info-six{grid-template-columns:repeat(3,1fr)}@media(max-width:600px){.receipt-info-six{grid-template-columns:1fr 1fr}}
.receipt-info{gap:5px;margin:8px 0;padding:7px 0}.receipt-info div{gap:1px}.receipt-info span,.receipt-credit span{font-size:10px}.receipt-info strong,.receipt-credit strong{font-size:11px}.credit-details>div{padding:4px 0}.receipt-items{margin-top:8px}.receipt-item{padding:5px 0}.receipt-summary>div,.receipt-payment>div{padding:3px 0}.receipt-total{padding:9px 0}.receipt-qr{padding:9px 0;margin-top:8px}.receipt-footer{padding-top:8px}
.receipt-dialog .dialog-title{font-size:16px}.receipt-dialog .dialog-sub{font-size:10px}.receipt-store{font-size:16px!important}.receipt-business{font-size:10px!important}.receipt-fiscal{font-size:9px}.receipt-status{font-size:11px}.receipt-status .confirmed-label{font-size:12px}.receipt-info strong,.receipt-credit strong,.receipt-payment strong{font-size:10px}.receipt-item{font-size:10px}.receipt-item-head{font-size:8px}.receipt-total{font-size:16px}.receipt-total strong{font-size:16px}.receipt-qr strong{font-size:10px}.receipt-qr small,.receipt-qr span,.receipt-footer{font-size:9px}
.receipt-item{grid-template-columns:68px minmax(0,1fr) 42px 76px 86px!important}.receipt-item>span:nth-child(3),.receipt-item>span:nth-child(4),.receipt-item>strong{width:100%;text-align:right;justify-self:end}.receipt-item-head>span{text-align:right}.receipt-item-head>span:first-child,.receipt-item-head>span:nth-child(2){text-align:left}.receipt-item>span:nth-child(2){min-width:0;overflow-wrap:anywhere}@media(max-width:600px){.receipt-item{grid-template-columns:54px minmax(0,1fr) 34px 61px 70px!important;font-size:9px}}
.qr-preview img{width:72px;height:72px;display:block;image-rendering:auto}
.receipt-product-name{text-align:left!important;min-width:0;overflow-wrap:anywhere}.receipt-item,.receipt-item-head{grid-template-columns:68px minmax(0,1fr) 58px 42px 76px 86px!important}.receipt-item>span:nth-child(3),.receipt-item>span:nth-child(4),.receipt-item>span:nth-child(5),.receipt-item>strong{text-align:right;justify-self:end}.receipt-item-head>span:nth-child(n+3){text-align:right}@media(max-width:600px){.receipt-item,.receipt-item-head{grid-template-columns:52px minmax(0,1fr) 45px 34px 61px 70px!important;gap:4px}.receipt-item{font-size:9px}}
.caja-alert-dialog{border:1px solid #f1d39b;box-shadow:0 18px 50px rgba(166,96,0,.2)}.caja-alert-dialog .dialog-head{background:linear-gradient(135deg,#fff8ed,#fffdf8);padding:22px}.caja-alert-dialog .alert-title{color:#a85f00;font-size:20px}.caja-alert-content{display:flex;flex-direction:column;align-items:center;justify-content:center;text-align:center;padding:28px 30px 22px;background:#fff}.caja-alert-content p{margin:13px 0 5px;color:#664515;font-size:16px;font-weight:800}.caja-alert-content small{max-width:360px;color:#8c806c;font-size:12px;line-height:1.5}.caja-alert-dialog .dialog-actions{padding:12px 22px 20px;background:#fffaf2;border-top:1px solid #f5e6c8}.caja-alert-dialog .dialog-actions .q-btn{border-radius:10px;font-weight:800}
</style>

<style scoped>.sales-locked-banner{background:#fff4df;color:#916000;border:1px solid #f0d59f;font-weight:700}.sales-locked-layout{position:relative}.sales-lock-overlay{position:absolute;inset:0;z-index:5;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:7px;border-radius:18px;background:rgba(245,247,250,.82);color:#916000;text-align:center;pointer-events:all}.sales-lock-overlay strong{font-size:18px}.sales-lock-overlay span{font-size:13px}</style>

<template>
  <q-page class="credit-page q-pa-md">
    <div class="page-heading row items-center justify-between q-mb-lg">
      <div><div class="text-h5 text-weight-bold">Gestión de crédito</div><div class="text-grey-7">Controla saldos pendientes y registra los pagos de tus clientes.</div></div>
    </div>
    <CreditoResumenCards :cards="summaryCards" class="q-mb-lg" />
    <CreditoFiltros :model="filtros" @limpiar="limpiarFiltros" class="q-mb-lg" />
    <div class="row items-center justify-between q-mb-md"><div class="text-subtitle1 text-weight-bold">Cuentas por cobrar <span class="text-grey-6">({{ creditos.length }})</span></div></div>
    <div v-if="cargando" class="text-center q-pa-xl"><q-spinner color="primary" size="42px" /></div>
    <div v-else-if="creditos.length" class="row q-col-gutter-md">
      <div v-for="credito in creditos" :key="credito.id" class="col-12 col-md-6 col-xl-4"><CreditoCard :credito="credito" :bloqueada="!sesionCaja?.idSesionCaja" @detalle="abrirDetalle" @pago="abrirPago" /></div>
    </div>
    <q-card v-else-if="!cargando" flat bordered class="empty-state"><q-icon name="receipt_long" size="48px" color="grey-5" /><div class="text-subtitle1 q-mt-sm">No se encontraron créditos</div></q-card>
    <div v-if="totalPaginas > 1" class="row justify-center q-mt-lg"><q-pagination v-model="pagina" :max="totalPaginas" :max-pages="6" direction-links boundary-links color="primary" @update:model-value="cargarCreditos" /></div>
    <CreditoDetalleDialog v-model="detalleAbierto" :credito="creditoSeleccionado" />
    <RegistrarPagoDialog v-model="pagoAbierto" :credito="creditoSeleccionado" @guardar="registrarPago" />
  </q-page>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useQuasar } from 'quasar'
import CreditoResumenCards from '../../components/credito/CreditoResumenCards.vue'
import CreditoFiltros from '../../components/credito/CreditoFiltros.vue'
import CreditoCard from '../../components/credito/CreditoCard.vue'
import CreditoDetalleDialog from '../../components/credito/CreditoDetalleDialog.vue'
import RegistrarPagoDialog from '../../components/credito/RegistrarPagoDialog.vue'
import { listarCreditos, obtenerResumenCreditos, registrarPagoCredito } from '../../api/venta/venta'
import { obtenerSesionCajaAbierta } from '../../api/caja/caja'

const $q = useQuasar()
const creditos = ref([]); const cargando = ref(false); const pagina = ref(1); const totalPaginas = ref(1)
const resumen = ref({ totalPorCobrar: 0, totalVencido: 0, totalCobrado: 0, cuentasActivas: 0 })
const filtros = reactive({ busqueda: '', estado: '', desde: '', hasta: '' })
const detalleAbierto = ref(false); const pagoAbierto = ref(false); const creditoSeleccionado = ref(null)
const sesionCaja = ref(null)
const moneda = value => `Bs ${Number(value || 0).toFixed(2)}`
const summaryCards = computed(() => [{ label: 'Total por cobrar', value: moneda(resumen.value.totalPorCobrar), icon: 'account_balance_wallet', color: 'green' }, { label: 'Créditos vencidos', value: moneda(resumen.value.totalVencido), icon: 'warning', color: 'red' }, { label: 'Total cobrado', value: moneda(resumen.value.totalCobrado), icon: 'payments', color: 'blue' }, { label: 'Cuentas activas', value: resumen.value.cuentasActivas, icon: 'groups', color: 'orange' }])
function mapCredito(c) { return { ...c, id: c.idCuenta, codigoVenta: c.codigoVenta, saldo: Number(c.saldo || 0), total: Number(c.total || 0), pagoInicial: Number(c.pagoInicial || 0), productos: c.productos || [], pagos: (c.pagos || []).map(p => ({ ...p, referencia: p.referenciaQr, fecha: p.fechaPago ? new Date(p.fechaPago).toLocaleDateString('es-BO') : '--' })) } }
async function cargarCreditos () { cargando.value = true; try { const r = await listarCreditos({ page: pagina.value - 1, size: 6, buscar: filtros.busqueda || undefined, estado: filtros.estado || undefined, desde: filtros.desde || undefined, hasta: filtros.hasta || undefined }); creditos.value = (r?.content || []).map(mapCredito); totalPaginas.value = Math.max(1, r?.totalPages || 1) } catch { $q.notify({ type: 'negative', message: 'No se pudieron cargar los créditos.' }) } finally { cargando.value = false } }
async function cargarResumen () { try { resumen.value = await obtenerResumenCreditos() || resumen.value } catch { /* la lista sigue disponible */ } }
function limpiarFiltros () { Object.assign(filtros, { busqueda: '', estado: '', desde: '', hasta: '' }); pagina.value = 1; cargarCreditos() }
function abrirDetalle (credito) { creditoSeleccionado.value = credito; detalleAbierto.value = true }
function abrirPago (credito) { if (!sesionCaja.value?.idSesionCaja) { $q.notify({ type: 'warning', message: 'Debes abrir una caja antes de registrar pagos.' }); return } creditoSeleccionado.value = credito; pagoAbierto.value = true }
async function registrarPago (pago) { try { const actualizado = await registrarPagoCredito(creditoSeleccionado.value.id, { ...pago, idSesionCaja: sesionCaja.value.idSesionCaja }); creditoSeleccionado.value = mapCredito(actualizado); await Promise.all([cargarCreditos(), cargarResumen()]); $q.notify({ type: 'positive', message: 'Pago registrado correctamente.' }) } catch (error) { $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo registrar el pago.' }) } }
watch(() => [filtros.busqueda, filtros.estado, filtros.desde, filtros.hasta], () => { pagina.value = 1; cargarCreditos() })
onMounted(async () => { cargarCreditos(); cargarResumen(); try { sesionCaja.value = await obtenerSesionCajaAbierta() } catch { sesionCaja.value = null } })
</script>

<style scoped>
.credit-page { background: #f6f8fb; min-height: 100%; }
.page-heading { gap: 16px; }
.empty-state { min-height: 240px; display: grid; place-content: center; text-align: center; border-radius: 14px; }
@media (max-width: 600px) { .page-heading { align-items: flex-start; flex-direction: column; } }
</style>

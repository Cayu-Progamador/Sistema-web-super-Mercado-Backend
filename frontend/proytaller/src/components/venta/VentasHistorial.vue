<template>
  <div><q-card flat bordered class="filters-card q-mb-lg"><q-card-section class="row q-col-gutter-md items-center"><div class="col-12 col-md-4"><q-input v-model="filtro" outlined dense clearable label="Buscar por venta o cliente" color="green-8"><template #prepend><q-icon name="search" color="green-8" /></template></q-input></div><div class="col-12 col-sm-6 col-md-2"><q-input v-model="desde" outlined dense type="date" label="Desde" color="green-8" :max="hasta || hoy" :error="!!errorDesde" :error-message="errorDesde" /></div><div class="col-12 col-sm-6 col-md-2"><q-input v-model="hasta" outlined dense type="date" label="Hasta" color="green-8" :min="desde || undefined" :max="hoy" :error="!!errorHasta" :error-message="errorHasta" /></div><div class="col-12 col-sm-6 col-md-2"><q-select v-model="estado" outlined dense clearable label="Estado" :options="['PAGADA','PENDIENTE_CREDITO','ANULADA']" color="green-8" /></div><div class="col-12 col-sm-6 col-md-2"><q-btn unelevated no-caps icon="cleaning_services" label="Limpiar" color="green-8" class="full-width" @click="limpiarFiltros" /></div></q-card-section></q-card>
    <div class="row q-col-gutter-lg"><div v-for="venta in paginadas" :key="venta.numero" class="col-12 col-md-6 col-xl-4"><q-card flat bordered class="sale-card"><q-card-section class="row items-center"><q-avatar icon="receipt_long" color="green-1" text-color="green-9" /><div class="q-ml-md"><div class="sale-number">{{ venta.numero }}</div><div class="sale-date">{{ venta.fecha }}</div></div><q-space /><q-badge :color="venta.estado === 'PAGADA' ? 'green-7' : venta.estado === 'ANULADA' ? 'negative' : 'orange-8'" :label="venta.estado" /></q-card-section><q-separator /><q-card-section><div class="sale-customer"><q-icon name="person" /> {{ venta.cliente }}</div><div class="sale-summary"><span>{{ venta.items }} productos</span><b>Bs {{ venta.total.toFixed(2) }}</b></div><div class="sale-method"><q-icon name="payments" /> {{ venta.metodo }}</div></q-card-section><q-card-actions align="right"><q-btn flat dense no-caps color="green-8" label="Ver detalle" icon="receipt_long" @click="$emit('ver', venta)" /><q-btn flat dense round color="grey-7" icon="print" @click="$emit('comprobante', venta)"><q-tooltip>Ver comprobante PDF</q-tooltip></q-btn></q-card-actions></q-card></div></div>
    <div v-if="!paginadas.length" class="text-grey-7 text-center q-pa-xl">No se encontraron ventas.</div><div class="pagination-wrap q-mt-xl"><q-pagination v-model="pagina" :max="totalPaginas" direction-links boundary-links color="green-8" /></div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
const props = defineProps({ ventas: { type: Array, default: () => [] } })
defineEmits(['ver', 'comprobante'])
const filtro = ref(''), desde = ref(''), hasta = ref(''), estado = ref(''), pagina = ref(1)
const fechaActual = new Date()
const hoy = `${fechaActual.getFullYear()}-${String(fechaActual.getMonth() + 1).padStart(2, '0')}-${String(fechaActual.getDate()).padStart(2, '0')}`
const errorDesde = computed(() => desde.value && desde.value > hoy ? 'No puede seleccionar una fecha futura.' : desde.value && hasta.value && desde.value > hasta.value ? 'La fecha desde no puede ser posterior a hasta.' : '')
const errorHasta = computed(() => hasta.value && hasta.value > hoy ? 'No puede seleccionar una fecha futura.' : desde.value && hasta.value && desde.value > hasta.value ? 'La fecha hasta debe ser igual o posterior a desde.' : '')
const fechasValidas = computed(() => !errorDesde.value && !errorHasta.value)
const filtradas = computed(() => { if (!fechasValidas.value) return []; const q = normalizar(filtro.value); return props.ventas.filter(v => { const textoVenta = normalizar(v.codigo || v.numero); const textoCliente = normalizar(v.cliente); const coincideTexto = !q || `${textoVenta} ${textoCliente}`.includes(q); const fecha = v.fechaISO || ''; const coincideDesde = !desde.value || fecha >= desde.value; const coincideHasta = !hasta.value || fecha <= hasta.value; const coincideEstado = !estado.value || v.estado === estado.value; return coincideTexto && coincideDesde && coincideHasta && coincideEstado }) })
const porPagina = 6
const totalPaginas = computed(() => Math.max(1, Math.ceil(filtradas.value.length / porPagina)))
const paginadas = computed(() => filtradas.value.slice((pagina.value - 1) * porPagina, pagina.value * porPagina))
watch([filtro, desde, hasta, estado], () => { pagina.value = 1 })
watch(totalPaginas, () => { if (pagina.value > totalPaginas.value) pagina.value = totalPaginas.value })
function limpiarFiltros() { filtro.value = ''; desde.value = ''; hasta.value = ''; estado.value = ''; pagina.value = 1 }
function normalizar(valor) { return String(valor || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim() }
</script>

<style scoped>
.filters-card,.sale-card{border-radius:18px;border-color:#dfeae1}.sale-card{height:100%;transition:.2s}.sale-card:hover{transform:translateY(-2px);box-shadow:0 8px 22px rgba(46,125,50,.11)}.sale-number{color:#1b5e20;font-weight:900}.sale-date,.sale-method{color:#789080;font-size:11px;margin-top:3px}.sale-customer{color:#37474f;font-weight:700}.sale-customer .q-icon,.sale-method .q-icon{color:#43a047;vertical-align:middle;margin-right:5px}.sale-summary{display:flex;justify-content:space-between;align-items:center;border-top:1px solid #edf2ee;margin-top:14px;padding-top:12px;color:#789080;font-size:12px}.sale-summary b{color:#2e7d32;font-size:18px}.pagination-wrap{display:flex;justify-content:center;padding-bottom:20px}
</style>

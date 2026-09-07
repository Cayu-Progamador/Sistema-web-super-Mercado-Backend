<template>
  <q-page class="clients-page">
    <div class="page-wrap">
      <div class="page-heading row items-center q-mb-lg">
        <div><div class="eyebrow">VENTAS · MAESTRO DE CLIENTES</div><h1>Clientes</h1><p>Administra los clientes asociados a tus ventas.</p></div>
        <q-space /><q-btn unelevated color="green-8" icon="person_add" label="Nuevo cliente" no-caps @click="nuevo" />
      </div>
      <div class="summary-grid q-mb-lg"><q-card v-for="item in resumen" :key="item.label" flat bordered class="summary-card"><q-card-section class="row items-center"><q-avatar :icon="item.icon" :color="item.color + '-1'" :text-color="item.color + '-9'" /><div class="q-ml-md"><div class="summary-value">{{ item.value }}</div><div class="summary-label">{{ item.label }}</div></div></q-card-section></q-card></div>
      <ClienteFilters :modelo="filtros" @filtro="aplicarFiltros" @limpiar="limpiar" />
      <div class="section-title row items-center q-mt-xl q-mb-md"><span>Listado de clientes</span><q-space /><span class="result-count">{{ total }} registros</span></div>
      <div v-if="cargando" class="row justify-center q-pa-xl"><q-spinner-dots color="green-8" size="45px" /></div>
      <div v-else-if="!clientes.length" class="empty-state"><q-icon name="people_outline" size="58px" color="green-4" /><div>No se encontraron clientes</div><small>Prueba con otros filtros o registra un nuevo cliente.</small></div>
      <div v-else class="row q-col-gutter-lg"><div v-for="cliente in clientes" :key="cliente.idCliente" class="col-12 col-sm-6 col-lg-4"><ClienteCard :cliente="cliente" @ver="ver" @editar="editar" @estado="abrirEstado" /></div></div>
      <div class="pagination-wrap q-mt-xl"><q-pagination v-model="pagina" :max="maxPaginas" :max-pages="6" direction-links boundary-links color="green-8" @update:model-value="cargar" /></div>
    </div>
    <ClienteFormDialog v-model="dialogForm" :cliente="seleccionado" :guardando="guardando" @guardar="guardar" />
    <ClienteDetailDialog v-model="dialogDetalle" :cliente="seleccionado" />
    <ClienteConfirmarEstadoDialog v-model="dialogEstado" :cliente="seleccionado" @confirmar="cambiarEstado" />
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import ClienteCard from '../../components/cliente/ClienteCard.vue'
import ClienteConfirmarEstadoDialog from '../../components/cliente/ClienteConfirmarEstadoDialog.vue'
import ClienteDetailDialog from '../../components/cliente/ClienteDetailDialog.vue'
import ClienteFilters from '../../components/cliente/ClienteFilters.vue'
import ClienteFormDialog from '../../components/cliente/ClienteFormDialog.vue'
import { activarCliente, actualizarCliente, crearCliente, desactivarCliente, listarClientes } from '../../api/cliente/cliente'

const $q = useQuasar()
const clientes = ref([]), total = ref(0), cargando = ref(false), guardando = ref(false), pagina = ref(1), tamano = 9
const filtros = ref({ busqueda: '', estado: null }), seleccionado = ref(null)
const dialogForm = ref(false), dialogDetalle = ref(false), dialogEstado = ref(false)
const maxPaginas = computed(() => Math.max(1, Math.ceil(total.value / tamano)))
const resumen = computed(() => [{ label: 'Total clientes', value: total.value, icon: 'groups', color: 'green' }, { label: 'Activos', value: clientes.value.filter(x => x.estado).length, icon: 'check_circle', color: 'blue' }, { label: 'Inactivos', value: clientes.value.filter(x => !x.estado).length, icon: 'person_off', color: 'orange' }])

async function cargar() {
  cargando.value = true
  try {
    const r = await listarClientes({ page: pagina.value - 1, size: tamano, busqueda: filtros.value.busqueda || undefined, estado: filtros.value.estado ?? undefined, sortBy: 'nombres', sortDir: 'asc' })
    clientes.value = r?.content || []; total.value = Number(r?.totalElements || 0)
  } catch (e) { clientes.value = []; total.value = 0; $q.notify({ type: 'negative', message: e?.response?.data?.message || 'No se pudieron cargar los clientes.' }) } finally { cargando.value = false }
}
function aplicarFiltros(value) { filtros.value = { ...value }; pagina.value = 1; cargar() }
function limpiar() { filtros.value = { busqueda: '', estado: null }; pagina.value = 1; cargar() }
function nuevo() { seleccionado.value = null; dialogForm.value = true }
function ver(cliente) { seleccionado.value = cliente; dialogDetalle.value = true }
function editar(cliente) { seleccionado.value = { ...cliente }; dialogForm.value = true }
function abrirEstado(cliente) { seleccionado.value = cliente; dialogEstado.value = true }
async function guardar(data) {
  guardando.value = true
  try { const { idCliente, ...payload } = data; if (idCliente) await actualizarCliente(idCliente, payload); else await crearCliente(payload); dialogForm.value = false; $q.notify({ type: 'positive', message: idCliente ? 'Cliente actualizado correctamente.' : 'Cliente registrado correctamente.' }); await cargar() }
  catch (e) { $q.notify({ type: 'negative', message: e?.response?.data?.message || 'No se pudo guardar el cliente.' }) } finally { guardando.value = false }
}
async function cambiarEstado(cliente) {
  try { if (cliente.estado) await desactivarCliente(cliente.idCliente); else await activarCliente(cliente.idCliente); dialogEstado.value = false; $q.notify({ type: 'positive', message: 'Estado del cliente actualizado.' }); await cargar() }
  catch (e) { $q.notify({ type: 'negative', message: e?.response?.data?.message || 'No se pudo cambiar el estado.' }) }
}
onMounted(cargar)
</script>

<style scoped>
.clients-page { min-height: 100vh; background: #f5f7fa; padding: 24px; }.page-wrap { max-width: 1440px; margin: 0 auto; }.eyebrow { color: #43a047; font-size: 11px; font-weight: 900; letter-spacing: 1.2px; }.page-heading h1 { color: #1b5e20; font-size: 30px; margin: 4px 0; font-weight: 900; }.page-heading p { color: #789080; margin: 0; }.summary-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }.summary-card { border-radius: 16px; border-color: #e0e8e2; }.summary-value { color: #263238; font-size: 23px; font-weight: 900; }.summary-label, .result-count { color: #789080; font-size: 12px; }.section-title { color: #1b5e20; font-size: 19px; font-weight: 900; }.empty-state { padding: 70px 20px; text-align: center; color: #60756a; background: #fff; border: 1px dashed #b9d4bd; border-radius: 18px; }.empty-state div { font-size: 18px; font-weight: 800; margin-top: 8px; }.empty-state small { display: block; margin-top: 5px; }.pagination-wrap { display: flex; justify-content: center; padding-bottom: 24px; }
@media (max-width: 700px) { .clients-page { padding: 14px; }.page-heading { align-items: flex-start; gap: 16px; flex-wrap: wrap; }.page-heading .q-btn { width: 100%; }.page-heading h1 { font-size: 25px; }.summary-grid { grid-template-columns: 1fr; gap: 10px; }.summary-card .q-card__section { padding: 12px 16px; }.pagination-wrap :deep(.q-pagination) { transform: scale(.9); } }
</style>

<template>
  <q-page class="pd-page">
    <div class="pd-page-inner">

      <!-- Header -->
      <div class="row items-center justify-between q-mb-lg">
        <div class="col">
          <div class="row items-center no-wrap">
            <div class="pd-header-ic">
              <q-icon name="shopping_cart" size="22px" color="green-8" />
            </div>
            <div class="q-ml-sm pd-header-texto">
              <div class="text-h4 text-bold pd-header-title">Pedidos a Proveedores</div>
              <div class="text-subtitle2 pd-header-subtitle">Gestiona y realiza seguimiento de los pedidos realizados a proveedores.</div>
            </div>
          </div>
        </div>
      </div>

      <!-- KPIs -->
      <PedidosKpis
        :total="resumen.total"
        :borradores="resumen.borradores"
        :enviados="resumen.enviados"
        :parciales="resumen.parciales"
        :completados="resumen.completados"
        :cancelados="resumen.cancelados"
      />

      <!-- Botón nuevo pedido -->
      <div class="row q-my-md">
        <div class="col-12">
          <q-btn v-permiso="'PEDIDOS_CREAR'" class="pd-btn pd-btn-nuevo" label="+ Nuevo Pedido" icon="add" no-caps @click="abrirNuevo" />
        </div>
      </div>

      <!-- Toolbar -->
      <div class="row items-center justify-between q-my-md toolbar-row">
        <div class="row items-center q-gutter-sm">
          <q-btn flat color="green-8" icon="refresh" @click="recargar" />
        </div>
        <div class="row items-center q-gutter-sm">
          <q-input
            v-model="busquedaGlobal"
            outlined
            dense
            placeholder="Buscar por proveedor o número de pedido..."
            class="search-input"
            debounce="400"
            @update:model-value="aplicarBusqueda"
          >
            <template v-slot:prepend>
              <q-icon name="search" color="green-8" />
            </template>
          </q-input>
          <q-btn
            flat
            :icon="filtrosVisibles ? 'expand_less' : 'filter_list'"
            label="Filtros"
            color="green-8"
            @click="filtrosVisibles = !filtrosVisibles"
          >
            <q-badge v-if="filtrosActivos" color="primary" floating>!</q-badge>
          </q-btn>
        </div>
      </div>

      <!-- Collapsible Filters -->
      <q-slide-transition>
        <div v-show="filtrosVisibles">
          <PedidosFiltros :proveedores="proveedores" @filtrar="aplicarFiltros" @limpiar="limpiarFiltros" />
        </div>
      </q-slide-transition>

      <!-- Tabla -->
      <div class="q-mt-md">
        <PedidosTable :rows="pedidos" :loading="cargando" :filtro="busquedaGlobal" @ver="verDetalle"
          @editar="abrirEditar" @pdf="descargarPdf" @cancelar="abrirCancelar" />
      </div>
    </div>

    <!-- Dialog nuevo / editar -->
    <NuevoPedidoDialog v-model="dialogNuevo" :pedido-editar="pedidoEditar" @guardado="recargar" />

    <!-- Dialog detalle -->
    <PedidoDetalleDialog v-model="dialogDetalle" :pedido="pedidoSeleccionado" />

    <!-- Dialog cancelar -->
    <CancelarPedidoDialog v-model="dialogCancelar" :pedido="pedidoCancelar" :cargando="cancelando"
      @confirmar="confirmarCancelar" />
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import PedidosKpis from '../../components/pedido/PedidosKpis.vue'
import PedidosFiltros from '../../components/pedido/PedidosFiltros.vue'
import PedidosTable from '../../components/pedido/PedidosTable.vue'
import NuevoPedidoDialog from '../../components/pedido/NuevoPedidoDialog.vue'
import PedidoDetalleDialog from '../../components/pedido/PedidoDetalleDialog.vue'
import CancelarPedidoDialog from '../../components/pedido/CancelarPedidoDialog.vue'
import {
  listarPedidos,
  obtenerPedido,
  cancelarPedido,
  obtenerKpi
} from '../../api/pedido/pedido'
import { listarProveedores } from '../../api/proveedor/proveedor'

const $q = useQuasar()

const cargando = ref(false)
const pedidos = ref([])
const proveedores = ref([])
const busquedaGlobal = ref('')
const filtrosVisibles = ref(false)
const filtrosActivos = ref(false)

const resumen = reactive({
  total: 0,
  borradores: 0,
  enviados: 0,
  parciales: 0,
  completados: 0,
  cancelados: 0
})

const filtrosAvanzados = ref({})

const dialogNuevo = ref(false)
const dialogDetalle = ref(false)
const dialogCancelar = ref(false)
const pedidoEditar = ref(null)
const pedidoSeleccionado = ref(null)
const pedidoCancelar = ref(null)
const cancelando = ref(false)

onMounted(() => {
  cargarProveedores()
  cargar()
})

async function cargarProveedores() {
  try {
    const res = await listarProveedores({ page: 0, size: 1000, sortBy: 'idProveedor', sortDir: 'asc', estado: true })
    proveedores.value = (res.content || []).map((p) => ({ id: p.id, nombre: p.nombre }))
  } catch (e) {
    proveedores.value = []
  }
}

async function cargar() {
  cargando.value = true
  try {
    const [data, kpi] = await Promise.all([
      listarPedidos({
        ...filtrosAvanzados.value,
        search: busquedaGlobal.value
      }),
      obtenerKpi()
    ])
    pedidos.value = data.content
    resumen.total = kpi.total
    resumen.borradores = kpi.borradores
    resumen.enviados = kpi.enviados
    resumen.parciales = kpi.parcialmenteRecibidos
    resumen.completados = kpi.completados
    resumen.cancelados = kpi.cancelados
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al cargar los pedidos.' })
  } finally {
    cargando.value = false
  }
}

function aplicarBusqueda() {
  cargar()
}

function aplicarFiltros(filtros) {
  filtrosAvanzados.value = filtros
  filtrosActivos.value = Object.values(filtros).some((v) => v !== null && v !== undefined && v !== '')
  cargar()
}

function limpiarFiltros() {
  filtrosAvanzados.value = {}
  filtrosActivos.value = false
  busquedaGlobal.value = ''
  cargar()
}

function recargar() {
  cargar()
}

function abrirNuevo() {
  pedidoEditar.value = null
  dialogNuevo.value = true
}

function abrirEditar(pedido) {
  pedidoEditar.value = pedido
  dialogNuevo.value = true
}

async function verDetalle(pedido) {
  pedidoSeleccionado.value = await obtenerPedido(pedido.idPedido)
  dialogDetalle.value = true
}

async function descargarPdf(pedido) {
  try {
    const { descargarPdfPedido } = await import('../../api/pedido/pedido')
    const blob = await descargarPdfPedido(pedido.idPedido)
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${pedido.numero}.pdf`
    link.click()
    URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: `PDF del pedido ${pedido.numero} descargado correctamente` })
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al descargar el PDF del pedido' })
  }
}

function abrirCancelar(pedido) {
  pedidoCancelar.value = pedido
  dialogCancelar.value = true
}

async function confirmarCancelar(pedido) {
  cancelando.value = true
  try {
    await cancelarPedido(pedido.idPedido)
    $q.notify({ type: 'success', message: `Pedido ${pedido.numero} cancelado correctamente.` })
    dialogCancelar.value = false
    cargar()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al cancelar el pedido.' })
  } finally {
    cancelando.value = false
  }
}
</script>

<style scoped src="../../assets/styles/pedido/pedido.css"></style>

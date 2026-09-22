<template>
  <q-page class="rp-page">
    <div class="rp-page-inner">

    

      <!-- Header -->
      <div class="row items-center justify-between q-mb-lg">
        <div class="col">
          <div class="row items-center no-wrap">
            <div class="rp-header-ic">
              <q-icon name="inventory" size="24px" color="white" />
            </div>
            <div class="q-ml-sm rp-header-texto">
              <div class="text-h4 text-bold rp-header-title">Recepciones de Mercadería</div>
              <div class="text-subtitle2 rp-header-subtitle">
                Registra, verifica y distribuye la mercadería entregada por los proveedores.
              </div>
            </div>
          </div>
        </div>
        
      </div>

      <!-- KPIs -->
      <RecepcionSummaryCards
        :total="resumen.total"
        :revision="resumen.revision"
        :completas="resumen.completas"
        :parciales="resumen.parciales"
        :rechazadas="resumen.rechazadas"
        :anuladas="resumen.anuladas"
      />

      <!-- Toolbar -->
      <div class="row items-center justify-between q-my-md rp-toolbar-row">
        <div class="row items-center q-gutter-sm q-ml-md">
          <q-btn v-permiso="'RECEPCION_CREAR'"
            class="rp-btn rp-btn-primario"
            label="+ Nueva Recepción"
            icon="add"
            no-caps
            @click="abrirNuevaRecepcion"
          />
          <q-btn
            outline
            no-caps
            color="green-8"
            icon="pending_actions"
            label="Pedidos faltantes"
            :class="vistaPendientes === 'faltantes' ? 'rp-btn-pendiente-activo' : ''"
            @click="alternarVistaPendientes('faltantes')"
          >
            <q-badge v-if="faltantes.length" color="orange-8" floating>{{ faltantes.length }}</q-badge>
          </q-btn>
          <q-btn
            outline
            no-caps
            color="orange-8"
            icon="assignment_return"
            label="Productos rechazados"
            :class="vistaPendientes === 'devueltos' ? 'rp-btn-devuelto-activo' : ''"
            @click="alternarVistaPendientes('devueltos')"
          >
            <q-badge v-if="devolucionesEnProceso" color="orange-8" floating>{{ devolucionesEnProceso }}</q-badge>
          </q-btn>
        </div>
        <div class="row items-center q-gutter-sm">
          <q-btn flat color="green-8" icon="refresh" @click="recargar" />
        </div>
        <div class="row items-center q-gutter-sm">
          <q-input
            v-model="busquedaGlobal"
            outlined
            dense
            placeholder="Buscar por nombre del proveedor..."
            class="rp-search-input"
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

      <q-slide-transition>
        <PendientesRecepcionCards
          v-if="vistaPendientes"
          :tipo="vistaPendientes"
          :items="vistaPendientes === 'faltantes' ? faltantes : devoluciones"
          @cerrar="vistaPendientes = null"
          @nueva-recepcion="nuevaRecepcionDesdeFaltantes"
          @recibir-reemplazo="recibirReemplazo"
        />
      </q-slide-transition>

      <!-- Collapsible Filters -->
      <q-slide-transition>
        <div v-show="filtrosVisibles">
          <RecepcionFiltros :proveedores="proveedores" @filtrar="aplicarFiltros" @limpiar="limpiarFiltros" />
        </div>
      </q-slide-transition>

      <!-- Tabla -->
      <div class="q-mt-md">
        <RecepcionTable :rows="recepciones" :loading="cargando" :filtro="busquedaGlobal" @ver="verDetalle" @distribuir="abrirDistribucion" @anular="anularRecepcion" @pdf="descargarPdf" />
      </div>
    </div>

    <!-- Dialog nueva recepción -->
    <NewReceptionStepper
      v-model="dialogNuevo"
      :faltante="faltanteSeleccionado"
      :devolucion="devolucionSeleccionada"
      @guardado="recargar"
    />

    <!-- Dialog detalle -->
    <RecepcionDetalleDialog v-model="dialogDetalle" :recepcion="recepcionSeleccionada" @pdf="descargarPdf" />

    <!-- Dialog anular -->
    <ConfirmarRecepcionDialog v-model="dialogAnular" :recepcion="recepcionAnular" @confirmar="confirmarAnulacion" />

    <!-- Dialog distribuir -->
    <DistribucionRecepcionDialog v-model="dialogDistribuir" :recepcion="recepcionDistribuir" @actualizado="recargar" />

  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import RecepcionSummaryCards from '../../components/recepcion/RecepcionSummaryCards.vue'
import RecepcionFiltros from '../../components/recepcion/RecepcionFiltros.vue'
import RecepcionTable from '../../components/recepcion/RecepcionTable.vue'
import NewReceptionStepper from '../../components/recepcion/NewReceptionStepper.vue'
import RecepcionDetalleDialog from '../../components/recepcion/RecepcionDetalleDialog.vue'
import ConfirmarRecepcionDialog from '../../components/recepcion/ConfirmarRecepcionDialog.vue'
import DistribucionRecepcionDialog from '../../components/recepcion/DistribucionRecepcionDialog.vue'
import PendientesRecepcionCards from '../../components/recepcion/PendientesRecepcionCards.vue'
import { listarRecepciones, obtenerKpi, anularRecepcion as anularRecepcionApi, listarFaltantes, listarDevoluciones } from '../../api/recepcion/recepcion'
import { listarProveedores } from '../../api/proveedor/proveedor'

const $q = useQuasar()

const cargando = ref(false)
const recepciones = ref([])
const proveedores = ref([])
const busquedaGlobal = ref('')
const filtrosVisibles = ref(false)
const filtrosActivos = ref(false)
const filtrosAvanzados = ref({})

const resumen = reactive({
  total: 0,
  revision: 0,
  completas: 0,
  parciales: 0,
  rechazadas: 0,
  anuladas: 0
})

const dialogNuevo = ref(false)
const faltanteSeleccionado = ref(null)
const devolucionSeleccionada = ref(null)
const dialogDetalle = ref(false)
const recepcionSeleccionada = ref(null)
const dialogAnular = ref(false)
const recepcionAnular = ref(null)
const dialogDistribuir = ref(false)
const recepcionDistribuir = ref(null)
const faltantes = ref([])
const devoluciones = ref([])
const devolucionesEnProceso = ref(0)
const vistaPendientes = ref(null)

onMounted(() => {
  cargarProveedores()
  cargar()
})

async function cargarProveedores() {
  try {
    const data = await listarProveedores({ page: 0, size: 100 })
    proveedores.value = (data.content || []).map((p) => ({ id: p.id, nombre: p.nombre }))
  } catch (e) {
    proveedores.value = []
  }
}

async function cargar() {
  cargando.value = true
  try {
    const [data, kpi, falt, dev] = await Promise.all([
      listarRecepciones({
        ...filtrosAvanzados.value,
        search: busquedaGlobal.value,
        page: 0,
        size: 10
      }),
      obtenerKpi(),
      listarFaltantes(),
      listarDevoluciones()
    ])
    recepciones.value = data.content || []
    resumen.total = kpi.total || 0
    resumen.revision = kpi.enRevision || 0
    resumen.completas = kpi.completas || 0
    resumen.parciales = kpi.parciales || 0
    resumen.rechazadas = kpi.rechazadas || 0
    resumen.anuladas = kpi.anuladas || 0
    faltantes.value = falt || []
    devoluciones.value = dev || []
    devolucionesEnProceso.value = (dev || []).filter((d) => d.estado === 'EN_PROCESO').length
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error al cargar las recepciones.' })
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

function verDetalle(recepcion) {
  recepcionSeleccionada.value = recepcion
  dialogDetalle.value = true
}

function alternarVistaPendientes(vista) {
  vistaPendientes.value = vistaPendientes.value === vista ? null : vista
}

function nuevaRecepcionDesdeFaltantes(faltante) {
  vistaPendientes.value = null
  faltanteSeleccionado.value = faltante
  devolucionSeleccionada.value = null
  dialogNuevo.value = true
}

function abrirNuevaRecepcion() {
  faltanteSeleccionado.value = null
  devolucionSeleccionada.value = null
  dialogNuevo.value = true
}

function recibirReemplazo(devolucion) {
  vistaPendientes.value = null
  faltanteSeleccionado.value = null
  devolucionSeleccionada.value = devolucion
  dialogNuevo.value = true
}

async function descargarPdf(recepcion) {
  try {
    const { descargarPdfRecepcion } = await import('../../api/recepcion/recepcion')
    const blob = await descargarPdfRecepcion(recepcion.idRecepcion)
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${recepcion.numero}.pdf`
    link.click()
    URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: `PDF de ${recepcion.numero} descargado correctamente` })
  } catch (e) {
    const msg = e?.response?.data?.message || e?.response?.data?.error || 'Error al descargar el PDF de la recepción.'
    $q.notify({ type: 'negative', message: msg })
  }
}

function abrirDistribucion(recepcion) {
  recepcionDistribuir.value = recepcion
  dialogDistribuir.value = true
}

function anularRecepcion(recepcion) {
  recepcionAnular.value = recepcion
  dialogAnular.value = true
}

async function confirmarAnulacion(recepcion) {
  try {
    await anularRecepcionApi(recepcion.idRecepcion)
    $q.notify({ type: 'success', message: 'Recepción ' + recepcion.numero + ' anulada correctamente.' })
    cargar()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.response?.data?.error || 'Error al anular la recepción.'
    $q.notify({ type: 'negative', message: msg })
  }
}
</script>

<style scoped src="../../assets/styles/recepcion/recepcion.css"></style>

<style scoped>
.rp-btn-pendiente-activo {
  background: #e0f2ee !important;
}

.rp-btn-devuelto-activo {
  background: #fff0df !important;
}
</style>

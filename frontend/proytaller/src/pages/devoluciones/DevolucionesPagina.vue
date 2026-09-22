<template>
  <q-page class="dv-page">
    <div class="dv-page-inner">
      <!-- Header -->
      <div class="row items-center justify-between q-mb-lg">
        <div class="col">
          <div class="row items-center no-wrap">
            <div class="dv-header-ic">
              <q-icon name="assignment_return" size="24px" color="green-8" />
            </div>
            <div class="q-ml-sm dv-header-texto">
              <div class="text-h4 text-bold dv-header-title">Devoluciones al Proveedor</div>
              <div class="text-subtitle2 dv-header-subtitle">
                Registra productos recibidos que deben ser devueltos al proveedor.
              </div>
            </div>
          </div>
        </div>
        
      </div>

      <!-- Tarjetas de resumen -->
      <TarjetasResumenDevoluciones
        :total="resumen.total"
        :borradores="resumen.borradores"
        :solicitadas="resumen.solicitadas"
        :enviadas="resumen.enviadas"
        :recibidas="resumen.recibidas"
        :rechazadas="resumen.rechazadas"
      />

      <!-- Toolbar -->
      <div class="row items-center justify-between q-my-md dv-toolbar-row">
        <div class="q-ml-md">
          <q-btn v-permiso="'DEVOLUCIONES_CREAR'"
            class="dv-btn dv-btn-primario"
            label="+ Nueva Devolución"
            icon="add"
            no-caps
            @click="dialogNuevo = true"
          />
        </div>
        <div class="row items-center q-gutter-sm">
          <q-btn flat color="green-8" icon="refresh" @click="recargar" />
        </div>
        <div class="row items-center q-gutter-sm">
          <q-input
            v-model="busquedaGlobal"
            outlined
            dense
            placeholder="Buscar por N.º devolución, N.º recepción o proveedor..."
            class="dv-search-input"
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

      <!-- Filtros colapsables -->
      <q-slide-transition>
        <div v-show="filtrosVisibles">
          <FiltrosDevoluciones
            :proveedores="proveedores"
            :estados="estados"
            @filtrar="aplicarFiltros"
            @limpiar="limpiarFiltros"
          />
        </div>
      </q-slide-transition>

      <!-- Tabla -->
      <div class="q-mt-md">
        <TablaDevoluciones
          :rows="devoluciones"
          :loading="cargando"
          :filtro="busquedaGlobal"
          @ver="verDetalle"
          @editar="editarDevolucion"
          @confirmar="confirmarDevolucion"
          @enviar="enviarDevolucion"
          @anular="anularDevolucion"
          @registrarRecepcion="registrarRecepcion"
          @pdf="verPdf"
        />
      </div>
    </div>

    <!-- Dialog nueva devolución -->
    <AsistenteNuevaDevolucion v-model="dialogNuevo" @guardado="recargar" />

    <EditarDevolucionDialog
      v-model="dialogEditar"
      :devolucion="devolucionEditar"
      @guardar="guardarEdicionLocal"
    />

    <!-- Dialog detalle -->
    <DialogoDetalleDevolucion v-model="dialogDetalle" :devolucion="devolucionSeleccionada" />

    <ConfirmarAccionDevolucionDialog
      v-model="dialogAccion"
      :accion="accionSeleccionada"
      :devolucion="devolucionAccion"
      :cargando="procesandoAccion"
      @confirmar="ejecutarAccion"
    />
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import TarjetasResumenDevoluciones from '../../components/devoluciones/TarjetasResumenDevoluciones.vue'
import FiltrosDevoluciones from '../../components/devoluciones/FiltrosDevoluciones.vue'
import TablaDevoluciones from '../../components/devoluciones/TablaDevoluciones.vue'
import AsistenteNuevaDevolucion from '../../components/devoluciones/AsistenteNuevaDevolucion.vue'
import EditarDevolucionDialog from '../../components/devoluciones/EditarDevolucionDialog.vue'
import DialogoDetalleDevolucion from '../../components/devoluciones/DialogoDetalleDevolucion.vue'
import ConfirmarAccionDevolucionDialog from '../../components/devoluciones/ConfirmarAccionDevolucionDialog.vue'
import {
  listarDevoluciones,
  listarEstadosDevolucion,
  actualizarDevolucion,
  obtenerDevolucion,
  confirmarDevolucion as confirmarDevolucionApi,
  enviarDevolucion as enviarDevolucionApi,
  completarDevolucion,
  cancelarDevolucion,
  descargarPdfDevolucion
} from '../../api/devoluciones/devoluciones'

const $q = useQuasar()

const cargando = ref(false)
const devoluciones = ref([])
const proveedores = ref([])
const estados = ref([])
const busquedaGlobal = ref('')
const filtrosVisibles = ref(false)
const filtrosActivos = ref(false)
const filtrosAvanzados = ref({})

const resumen = reactive({
  total: 0,
  borradores: 0,
  solicitadas: 0,
  enviadas: 0,
  recibidas: 0,
  rechazadas: 0
})

const dialogNuevo = ref(false)
const dialogEditar = ref(false)
const devolucionEditar = ref(null)
const dialogDetalle = ref(false)
const devolucionSeleccionada = ref(null)
const dialogAccion = ref(false)
const accionSeleccionada = ref('confirmar')
const devolucionAccion = ref(null)
const procesandoAccion = ref(false)

onMounted(() => {
  cargarEstados()
  cargar()
})

async function cargarEstados() {
  try {
    estados.value = await listarEstadosDevolucion() || []
  } catch (e) {
    estados.value = []
    $q.notify({ type: 'negative', message: 'Error al cargar los estados de devolución.' })
  }
}

async function cargar() {
  cargando.value = true
  try {
    const data = await listarDevoluciones()
    const todas = (data || []).map(adaptarDevolucion)
    devoluciones.value = aplicarFiltrosLocales(todas)
    proveedores.value = [...new Set(todas.map((d) => d.proveedor).filter(Boolean))]
      .map((nombre) => ({ id: nombre, nombre }))
    resumen.total = todas.length
    resumen.borradores = todas.filter((d) => d.estado === 'BORRADOR').length
    resumen.solicitadas = todas.filter((d) => d.estado === 'CONFIRMADA').length
    resumen.enviadas = todas.filter((d) => d.estado === 'ENVIADA').length
    resumen.recibidas = todas.filter((d) => d.estado === 'COMPLETADA').length
    resumen.rechazadas = todas.filter((d) => d.estado === 'EN_PROCESO').length
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error al cargar las devoluciones.' })
  } finally {
    cargando.value = false
  }
}

function adaptarDevolucion(data) {
  const detalle = (data.detalles || []).map((item) => ({
    id: item.idDetalleDevolucion,
    idDetalleDevolucion: item.idDetalleDevolucion,
    idDetalleRecepcion: item.idDetalleRecepcion,
    idProducto: item.idProducto,
    producto: item.producto,
    idLote: item.idLote,
    lote: item.lote || 'Sin lote',
    ubicacion: (item.origenes || []).map((o) => `${o.almacen} · ${o.ubicacion}`).join(', '),
    origenes: item.origenes || [],
    cantidadDevolver: Number(item.cantidad || 0),
    motivo: item.motivo,
    observacion: item.observacion
  }))
  return {
    ...data,
    idProveedor: data.proveedor,
    productos: detalle.length,
    cantidadTotal: Number(data.cantidadTotal || 0),
    detalle
  }
}

function aplicarFiltrosLocales(devoluciones) {
  const filtros = filtrosAvanzados.value
  const busqueda = busquedaGlobal.value?.trim().toLowerCase()
  return devoluciones.filter((devolucion) => {
    if (busqueda && ![
      devolucion.numero,
      devolucion.numeroRecepcion,
      devolucion.proveedor
    ].some((valor) => valor?.toLowerCase().includes(busqueda))) return false
    if (filtros.idProveedor && devolucion.idProveedor !== filtros.idProveedor) return false
    if (filtros.estado && devolucion.estado !== filtros.estado) return false
    const fecha = devolucion.fechaDevolucion?.slice(0, 10)
    if (filtros.fechaDesde && fecha < filtros.fechaDesde) return false
    if (filtros.fechaHasta && fecha > filtros.fechaHasta) return false
    return true
  })
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

async function verDetalle(devolucion) {
  devolucionSeleccionada.value = adaptarDevolucion(await obtenerDevolucion(devolucion.idDevolucion))
  dialogDetalle.value = true
}

async function editarDevolucion(devolucion) {
  try {
    devolucionEditar.value = adaptarDevolucion(await obtenerDevolucion(devolucion.idDevolucion))
    dialogEditar.value = true
  } catch (e) {
    $q.notify({ type: 'negative', message: 'No se pudo cargar la devolución para editar.' })
  }
}

async function guardarEdicionLocal(devolucionEditada) {
  try {
    const payload = {
      idRecepcion: devolucionEditada.idRecepcion,
      observacion: devolucionEditada.observacion,
      detalles: devolucionEditada.detalle.map(item => ({
        idDetalleRecepcion: item.idDetalleRecepcion,
        idMotivoDevolucion: item.motivoSeleccionado.idMotivoDevolucion,
        cantidad: Number(item.cantidadDevolver),
        observacion: item.observacion || null,
        origenes: [{ idUbicacion: item.origen, cantidad: Number(item.cantidadDevolver) }]
      }))
    }
    await actualizarDevolucion(devolucionEditada.idDevolucion, payload)
    $q.notify({ type: 'success', message: `Devolución ${devolucionEditada.numero} actualizada.` })
    await cargar()
  } catch (e) {
    $q.notify({ type: 'negative', message: 'No se pudo actualizar la devolución.' })
  }
}

function confirmarDevolucion(devolucion) {
  abrirAccion('confirmar', devolucion)
}

function enviarDevolucion(devolucion) {
  abrirAccion('enviar', devolucion)
}

function anularDevolucion(devolucion) {
  abrirAccion('anular', devolucion)
}

function registrarRecepcion(devolucion) {
  abrirAccion('completar', devolucion)
}

function abrirAccion(accion, devolucion) {
  accionSeleccionada.value = accion
  devolucionAccion.value = devolucion
  dialogAccion.value = true
}

async function ejecutarAccion() {
  const devolucion = devolucionAccion.value
  if (!devolucion) return
  const acciones = {
    confirmar: confirmarDevolucionApi,
    enviar: enviarDevolucionApi,
    anular: cancelarDevolucion,
    completar: completarDevolucion
  }
  const mensajes = {
    confirmar: 'confirmada',
    enviar: 'enviada al proveedor',
    anular: 'anulada',
    completar: 'completada'
  }
  procesandoAccion.value = true
  try {
    await acciones[accionSeleccionada.value](devolucion.idDevolucion)
    $q.notify({ type: 'success', message: `Devolución ${devolucion.numero} ${mensajes[accionSeleccionada.value]}.` })
    dialogAccion.value = false
    await cargar()
  } catch (e) {
    $q.notify({ type: 'negative', message: 'No se pudo realizar la acción sobre la devolución.' })
  } finally {
    procesandoAccion.value = false
  }
}

async function verPdf(devolucion) {
  try {
    const blob = await descargarPdfDevolucion(devolucion.idDevolucion)
    const url = URL.createObjectURL(blob)
    const enlace = document.createElement('a')
    enlace.href = url
    enlace.download = `${devolucion.numero}.pdf`
    enlace.click()
    URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: `PDF de ${devolucion.numero} descargado correctamente.` })
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error al descargar el PDF de la devolución.' })
  }
}
</script>

<style scoped src="../../assets/styles/devoluciones/devoluciones.css"></style>

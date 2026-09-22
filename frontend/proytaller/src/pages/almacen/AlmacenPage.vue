<template>
  <q-page class="alm-page">
    <div class="alm-contenedor">
      <header class="alm-encabezado q-mb-lg">
        <div class="row items-center no-wrap">
          <div class="alm-header-icon"><q-icon name="warehouse" size="27px" /></div>
          <div class="q-ml-md"><h1>Gestión de Almacenes</h1><p>Administra los espacios físicos donde se almacena la mercadería del supermercado.</p></div>
        </div>
        <div class="row q-gutter-sm no-wrap">
          <q-btn v-permiso="'ALMACEN_CREAR'" unelevated no-caps color="green-8" icon="add" label="Nuevo Almacén" @click="abrirNuevo" />
        </div>
      </header>

      <AlmacenIndicadores :total="totalAlmacenes" :activos="almacenesActivos" :inactivos="almacenesInactivos" :ubicaciones="totalUbicaciones" />
      <AlmacenFiltros :tipos="tiposAlmacen" @filtrar="aplicarFiltros" @limpiar="limpiarFiltros" />

      <div class="row items-center justify-between q-mb-md"><div><span class="alm-listado-titulo">Almacenes registrados</span><q-badge color="green-8" class="q-ml-sm">{{ totalElementos }}</q-badge></div><div class="text-caption text-grey-6">Mostrando {{ almacenes.length }} resultados</div></div>

      <div v-if="cargando" class="row q-col-gutter-lg">
        <div v-for="item in porPagina" :key="item" class="col-12 col-sm-6 col-lg-4 q-mb-lg"><q-card flat bordered class="q-pa-lg" style="height: 330px; border-radius: 18px"><q-skeleton type="QAvatar" /><q-skeleton type="text" class="q-mt-md" /><q-skeleton type="text" width="60%" /><q-skeleton height="120px" class="q-mt-lg" /></q-card></div>
      </div>

      <div v-else-if="almacenes.length" class="row q-col-gutter-lg">
        <div v-for="almacen in almacenes" :key="almacen.id" class="col-12 col-sm-6 col-lg-4 q-mb-lg">
          <AlmacenCard :almacen="almacen" @ver="verDetalle" @editar="abrirEditar" @estado="solicitarCambioEstado" @pdf="descargarInventarioPdf" />
        </div>
      </div>

      <q-card v-else flat bordered class="alm-vacio"><q-icon name="search_off" size="58px" color="grey-4" /><h3>No se encontraron almacenes</h3><p>Intenta cambiar los filtros de búsqueda.</p><q-btn outline no-caps color="green-8" icon="restart_alt" label="Limpiar filtros" @click="limpiarFiltros" /></q-card>

      <div v-if="totalPaginas > 1" class="row justify-center q-mt-sm"><q-pagination v-model="pagina" :max="totalPaginas" color="green-8" direction-links boundary-links @update:model-value="cargarAlmacenes" /></div>
    </div>

    <AlmacenFormularioDialog v-model="dialogAlmacen" :almacen="almacenSeleccionado" :tipos="tiposAlmacen" :responsables="responsablesAlmacen" :guardando="guardandoAlmacen" @guardar="guardarAlmacen" />
    <AlmacenDetalleDialog v-model="dialogDetalle" :almacen="almacenSeleccionado" @ver-ubicaciones="verUbicacionesAlmacen" @pdf="descargarInventarioPdf" />
    <AlmacenConfirmacionDialog v-model="dialogConfirmacion" :almacen="almacenSeleccionado" @confirmar="confirmarCambioEstado" />
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'
import AlmacenIndicadores from '../../components/almacen/AlmacenIndicadores.vue'
import AlmacenFiltros from '../../components/almacen/AlmacenFiltros.vue'
import AlmacenCard from '../../components/almacen/AlmacenCard.vue'
import AlmacenFormularioDialog from '../../components/almacen/AlmacenFormularioDialog.vue'
import AlmacenDetalleDialog from '../../components/almacen/AlmacenDetalleDialog.vue'
import AlmacenConfirmacionDialog from '../../components/almacen/AlmacenConfirmacionDialog.vue'
import { activarAlmacen, actualizarAlmacen, crearAlmacen, descargarInventarioAlmacenPdf, desactivarAlmacen, listarAlmacenes, listarResponsablesAlmacen, listarTiposAlmacen } from '../../api/almacen/almacen'

const $q = useQuasar()
const router = useRouter()
const dialogAlmacen = ref(false)
const dialogDetalle = ref(false)
const dialogConfirmacion = ref(false)
const almacenSeleccionado = ref(null)
const filtros = ref({ buscar: '', estado: null, tipo: null })
const pagina = ref(1)
const porPagina = 6
const cargando = ref(false)
const totalElementos = ref(0)
const totalPaginas = ref(0)
const tiposAlmacen = ref([])
const responsablesAlmacen = ref([])
const guardandoAlmacen = ref(false)

/* Datos proporcionados por GET /api/almacenes. */
const almacenesSimulados = ref([
  { id:1,codigo:'ALM-001',nombre:'Almacén Principal',tipo:'PRINCIPAL',estado:'ACTIVO',descripcion:'Almacén general del supermercado.',direccion:'Parte posterior del supermercado',responsable:'Juan Pérez',telefono:'72900001',ubicaciones:8,productos:125,stockTotal:890,capacidadMaxima:1000,unidadCapacidad:'Productos',ocupacion:80,fechaCreacion:'10/08/2026',ubicacionesDetalle:[{codigo:'A-01',nombre:'Alimentos secos'},{codigo:'A-02',nombre:'Arroz y cereales'},{codigo:'B-01',nombre:'Bebidas'},{codigo:'C-01',nombre:'Productos de limpieza'}] },
  { id:2,codigo:'ALM-002',nombre:'Cámara Fría',tipo:'CAMARA_FRIA',estado:'ACTIVO',descripcion:'Área refrigerada para productos perecederos.',direccion:'Sector posterior',responsable:'Carlos Ramos',telefono:'72900002',ubicaciones:4,productos:32,stockTotal:250,capacidadMaxima:500,unidadCapacidad:'Productos',ocupacion:60,fechaCreacion:'12/08/2026' },
  { id:3,codigo:'ALM-003',nombre:'Depósito de Bebidas',tipo:'DEPOSITO',estado:'ACTIVO',descripcion:'Espacio destinado al almacenamiento de bebidas.',direccion:'Sector norte',responsable:'María López',telefono:'72900003',ubicaciones:5,productos:48,stockTotal:420,capacidadMaxima:600,unidadCapacidad:'Productos',ocupacion:72,fechaCreacion:'13/08/2026' },
  { id:4,codigo:'ALM-004',nombre:'Depósito de Limpieza',tipo:'DEPOSITO',estado:'ACTIVO',descripcion:'Área separada para productos de limpieza y cuidado del hogar.',direccion:'Sector este',responsable:'Ana Torres',telefono:'72900004',ubicaciones:3,productos:27,stockTotal:190,capacidadMaxima:350,unidadCapacidad:'Productos',ocupacion:54,fechaCreacion:'14/08/2026' },
  { id:5,codigo:'ALM-005',nombre:'Almacén Secundario',tipo:'SECUNDARIO',estado:'INACTIVO',descripcion:'Espacio auxiliar disponible para futuras operaciones.',direccion:'Bloque lateral',responsable:null,telefono:'',ubicaciones:0,productos:0,stockTotal:0,capacidadMaxima:400,unidadCapacidad:'Productos',ocupacion:0,fechaCreacion:'15/08/2026' },
  { id:6,codigo:'ALM-006',nombre:'Depósito de Cajas',tipo:'DEPOSITO',estado:'ACTIVO',descripcion:'Almacenamiento temporal de productos embalados.',direccion:'Sector de descarga',responsable:'Juan Pérez',telefono:'72900005',ubicaciones:2,productos:18,stockTotal:115,capacidadMaxima:250,unidadCapacidad:'Cajas',ocupacion:46,fechaCreacion:'16/08/2026' },
  { id:7,codigo:'ALM-007',nombre:'Cámara de Congelados',tipo:'CAMARA_FRIA',estado:'ACTIVO',descripcion:'Conservación de alimentos congelados a temperatura controlada.',direccion:'Sector refrigerado',responsable:'Carlos Ramos',telefono:'72900006',ubicaciones:3,productos:21,stockTotal:160,capacidadMaxima:220,unidadCapacidad:'Productos',ocupacion:73,fechaCreacion:'17/08/2026' }
])

const almacenes = ref([])
const totalAlmacenes = computed(() => totalElementos.value)
const almacenesActivos = computed(() => almacenes.value.filter(a => a.estado === 'ACTIVO').length)
const almacenesInactivos = computed(() => almacenes.value.filter(a => a.estado === 'INACTIVO').length)
const totalUbicaciones = computed(() => almacenes.value.reduce((total, a) => total + Number(a.ubicaciones || 0), 0))
const almacenesFiltradosSimulados = computed(() => {
  const texto = filtros.value.buscar?.trim().toLowerCase()
  return almacenes.value.filter(a => {
    const coincideTexto = !texto || a.codigo.toLowerCase().includes(texto) || a.nombre.toLowerCase().includes(texto)
    return coincideTexto && (!filtros.value.estado || a.estado === filtros.value.estado) && (!filtros.value.tipo || a.tipo === filtros.value.tipo)
  })
})
const totalPaginasSimuladas = computed(() => Math.ceil(almacenesFiltradosSimulados.value.length / porPagina))
const almacenesPaginadosSimulados = computed(() => almacenesFiltradosSimulados.value.slice((pagina.value - 1) * porPagina, pagina.value * porPagina))

function adaptarAlmacen(almacen) {
  return {
    ...almacen,
    id: almacen.idAlmacen,
    estado: almacen.activo ? 'ACTIVO' : 'INACTIVO',
    ubicaciones: Number(almacen.totalUbicaciones || 0),
    productos: Number(almacen.totalProductos || 0),
    stockTotal: Number(almacen.stockTotal || 0),
    telefono: almacen.telefonoResponsable || null,
    capacidadMaxima: Number(almacen.capacidadMaxima || 0),
    ocupacion: Number(almacen.ocupacion || 0),
    ubicacionesDetalle: almacen.ubicacionesDetalle || []
  }
}

function obtenerEstadoBackend(estado) {
  if (estado === 'ACTIVO') return true
  if (estado === 'INACTIVO') return false
  return undefined
}

async function cargarTiposAlmacen() {
  try {
    const tipos = await listarTiposAlmacen()
    tiposAlmacen.value = (tipos || []).map(tipo => ({
      label: tipo.nombre === 'SALA_VENTAS' ? 'Sala de ventas' : tipo.nombre === 'ALMACEN' ? 'Almacén' : tipo.nombre,
      value: tipo.nombre,
      descripcion: tipo.descripcion,
      idTipoAlmacen: tipo.idTipoAlmacen
    }))
  } catch {
    tiposAlmacen.value = []
    $q.notify({ type: 'negative', message: 'No se pudieron cargar los tipos de almacén.' })
  }
}

async function cargarResponsablesAlmacen() {
  try {
    const responsables = await listarResponsablesAlmacen()
    responsablesAlmacen.value = (responsables || []).map(responsable => ({
      label: responsable.nombreCompleto,
      value: responsable.idEmpleado,
      telefono: responsable.telefono
    }))
  } catch {
    responsablesAlmacen.value = []
    $q.notify({ type: 'negative', message: 'No se pudieron cargar los responsables.' })
  }
}

async function cargarAlmacenes() {
  cargando.value = true
  try {
    const respuesta = await listarAlmacenes({
      search: filtros.value.buscar?.trim() || undefined,
      tipo: filtros.value.tipo || undefined,
      estado: obtenerEstadoBackend(filtros.value.estado),
      page: pagina.value - 1,
      size: porPagina,
      sortBy: 'nombre',
      sortDir: 'asc'
    })
    almacenes.value = (respuesta?.content || []).map(adaptarAlmacen)
    totalElementos.value = Number(respuesta?.totalElements || 0)
    totalPaginas.value = Number(respuesta?.totalPages || 0)
  } catch (error) {
    almacenes.value = []
    totalElementos.value = 0
    totalPaginas.value = 0
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo cargar el listado de almacenes.' })
  } finally {
    cargando.value = false
  }
}

async function aplicarFiltros(valores) {
  filtros.value = { ...valores }
  pagina.value = 1
  await cargarAlmacenes()
}

async function limpiarFiltros() {
  filtros.value = { buscar: '', estado: null, tipo: null }
  pagina.value = 1
  await cargarAlmacenes()
}
function abrirNuevo() { almacenSeleccionado.value = null; dialogAlmacen.value = true }
function abrirEditar(almacen) { almacenSeleccionado.value = { ...almacen }; dialogAlmacen.value = true }
function verDetalle(almacen) { almacenSeleccionado.value = almacen; dialogDetalle.value = true }
function verUbicacionesAlmacen(almacen) {
  dialogDetalle.value = false
  router.push({ name: 'ubicaciones-almacen', query: { almacenId: almacen.id, almacen: almacen.nombre } })
}

async function guardarAlmacen(datos) {
  if (datos.id) {
    await guardarCambiosAlmacen(datos)
    return
  }

  guardandoAlmacen.value = true
  try {
    await crearAlmacen({
      nombre: datos.nombre,
      idTipoAlmacen: datos.idTipoAlmacen,
      descripcion: datos.descripcion || null,
      direccion: datos.direccion || null,
      idResponsable: datos.idResponsable || null
    })
    dialogAlmacen.value = false
    pagina.value = 1
    $q.notify({ type: 'positive', message: 'Almacén registrado correctamente.' })
    await cargarAlmacenes()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo registrar el almacén.' })
  } finally {
    guardandoAlmacen.value = false
  }
}

async function guardarCambiosAlmacen(datos) {
  guardandoAlmacen.value = true
  try {
    await actualizarAlmacen(datos.id, {
      nombre: datos.nombre,
      idTipoAlmacen: datos.idTipoAlmacen,
      descripcion: datos.descripcion || null,
      direccion: datos.direccion || null,
      idResponsable: datos.idResponsable || null
    })
    dialogAlmacen.value = false
    $q.notify({ type: 'positive', message: 'Almacén actualizado correctamente.' })
    await cargarAlmacenes()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo actualizar el almacén.' })
  } finally {
    guardandoAlmacen.value = false
  }
}

function solicitarCambioEstado(almacen) {
  almacenSeleccionado.value = almacen
  dialogConfirmacion.value = true
}

async function confirmarCambioEstado() {
  if (!almacenSeleccionado.value) return
  const activar = almacenSeleccionado.value.estado === 'INACTIVO'
  try {
    if (activar) await activarAlmacen(almacenSeleccionado.value.id)
    else await desactivarAlmacen(almacenSeleccionado.value.id)
    dialogConfirmacion.value = false
    $q.notify({ type: 'positive', message: `Almacén ${activar ? 'activado' : 'desactivado'} correctamente.` })
    await cargarAlmacenes()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || `No se pudo ${activar ? 'activar' : 'desactivar'} el almacén.` })
  }
}
async function descargarInventarioPdf(almacen) {
  try {
    const blob = await descargarInventarioAlmacenPdf(almacen.id)
    const url = URL.createObjectURL(blob)
    const enlace = document.createElement('a')
    const nombreSeguro = almacen.nombre.replace(/[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ_-]+/g, '_')
    enlace.href = url
    enlace.download = `Inventario_${nombreSeguro}.pdf`
    document.body.appendChild(enlace)
    enlace.click()
    enlace.remove()
    URL.revokeObjectURL(url)
    $q.notify({ type: 'positive', message: `Inventario de ${almacen.nombre} descargado correctamente.` })
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo descargar el inventario en PDF.' })
  }
}

onMounted(() => Promise.all([cargarAlmacenes(), cargarTiposAlmacen(), cargarResponsablesAlmacen()]))
</script>

<style scoped>
.alm-page{min-height:100%;background:#f5f7f4;font-family:'Nunito',sans-serif}.alm-contenedor{max-width:1500px;margin:auto;padding:24px}.alm-encabezado{display:flex;align-items:center;justify-content:space-between;gap:18px}.alm-header-icon{width:56px;height:56px;border-radius:16px;display:grid;place-items:center;color:#fff;background:linear-gradient(135deg,#82bd43,#4a8c25);box-shadow:0 8px 20px rgba(74,140,37,.22)}h1{font-size:27px;line-height:1.2;color:#2a5c1a;margin:0;font-weight:900}.alm-encabezado p{color:#67805f;font-size:13px;margin:4px 0 0}.alm-listado-titulo{font-size:16px;font-weight:900;color:#2a5c1a}.alm-vacio{text-align:center;padding:55px 20px;border-radius:18px;border-style:dashed!important;border-color:#cddbc7!important}.alm-vacio h3{color:#52674d;margin:10px 0 4px}.alm-vacio p{color:#8a9986;margin:0 0 18px}@media(max-width:750px){.alm-contenedor{padding:14px}.alm-encabezado{align-items:flex-start;flex-direction:column}.alm-encabezado>.row:last-child{width:100%}.alm-encabezado>.row:last-child .q-btn{flex:1}h1{font-size:22px}}@media(max-width:430px){.alm-encabezado>.row:last-child{flex-direction:column}.alm-encabezado>.row:last-child .q-btn{width:100%}}
</style>

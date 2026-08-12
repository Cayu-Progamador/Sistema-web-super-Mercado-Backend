<template>
  <q-page class="pv-page">
    <div class="pv-page-inner">
    <!-- Encabezado -->
    <div class="row items-center justify-between q-mb-lg">
      <div class="col">
        <div class="row items-center no-wrap">
          <div class="pv-header-ic">
            <q-icon name="storefront" size="22px" color="green-8" />
          </div>
          <div class="q-ml-sm pv-header-texto">
            <div class="text-h4 text-bold pv-header-title">Gestión de Proveedores</div>
            <div class="text-subtitle2 pv-header-subtitle">Administración de proveedores empresa y proveedores persona natural</div>
          </div>
        </div>
      </div>
    </div>

    <!-- KPI -->
    <ProvidersKpis
      :total="totalProveedores"
      :empresas="conteoEmpresas"
      :personas="conteoPersonas"
      :activos="conteoActivos"
      :inactivos="conteoInactivos"
    />

    <div class="q-mt-md">
      <!-- Tabs de tipo -->
      <ProviderTypeTabs
        v-model="filtroTipo"
        :total="totalProveedores"
        :empresas="conteoEmpresas"
        :personas="conteoPersonas"
      />

      <!-- Barra de herramientas -->
      <div class="pv-toolbar pv-toolbar-grid q-mt-sm">
        <div class="pv-toolbar-actions">
          <q-btn class="pv-btn pv-btn-primario" label="Nuevo Proveedor" icon="add" no-caps @click="nuevoProveedor" />
          <q-btn class="pv-btn pv-btn-naranja" label="Exportar PDF" icon="picture_as_pdf" no-caps @click="exportarPdf" />
          <q-btn class="pv-btn pv-btn-verde" label="Exportar Excel" icon="grid_on" no-caps @click="exportarExcel" />
          <q-btn class="pv-btn pv-btn-icono" icon="refresh" @click="actualizar">
            <q-tooltip>Recargar</q-tooltip>
          </q-btn>
          <q-btn
            class="pv-btn pv-btn-filtro"
            :icon="filtrosVisibles ? 'filter_alt_off' : 'filter_alt'"
            :label="filtrosVisibles ? 'Ocultar filtros' : 'Filtros'"
            no-caps
            @click="filtrosVisibles = !filtrosVisibles"
          />
        </div>
        <q-input
          v-model="busqueda"
          outlined
          dense
          placeholder="Buscar por nombre, NIT o CI..."
          class="pv-search"
          clearable
        >
          <template v-slot:prepend>
            <q-icon name="search" size="18px" />
          </template>
        </q-input>
      </div>

      <!-- Filtros colapsables -->
      <q-slide-transition>
        <div v-show="filtrosVisibles">
          <q-card class="pv-filters q-mt-sm" flat>
            <q-card-section class="q-pa-sm row items-end q-col-gutter-sm">
              <div class="col-6">
                <q-select v-model="filtros.ciudad" :options="ciudades" label="Ciudad / Zona" outlined dense clearable color="primary" emit-value map-options />
              </div>
              <div class="col-6">
                <q-select v-model="filtros.estado" :options="['Activo', 'Inactivo']" label="Estado" outlined dense clearable color="primary" />
              </div>
              <div class="col-12 row justify-end q-gutter-sm q-mt-sm">
                <q-btn class="pv-btn pv-btn-cancelar" label="Cancelar" icon="close" no-caps @click="limpiarFiltros" />
                <q-btn class="pv-btn pv-btn-buscar" label="Buscar" icon="search" no-caps @click="buscar" />
              </div>
            </q-card-section>
          </q-card>
        </div>
      </q-slide-transition>

      <!-- Tabla paginable -->
      <div class="q-mt-sm">
        <ProvidersTable
          :rows="proveedores"
          :loading="cargando"
          :rows-number="totalFiltrado"
          :filtro-tipo="filtroTipo"
          @ver-detalle="verDetalle"
          @editar="editar"
          @historial="historial"
          @exportar="exportarProveedor"
          @cambiar-estado="cambiarEstado"
          @eliminar="eliminar"
          @pagina-cambio="onPaginaCambio"
        />
      </div>
    </div>

    <!-- Drawer detalle -->
    <ProviderDetailDrawer
      v-model="drawerAbierto"
      :proveedor="proveedorSeleccionado"
      @editar="editar"
      @historial="historial"
      @descargar-doc="descargarDoc"
    />

    <!-- Diálogo nuevo proveedor -->
    <NewProviderDialog v-model="dialogAbierto" :proveedor-editar="proveedorEditar" @guardar="guardarNuevo" />

    <!-- Diálogo confirmar estado -->
    <ConfirmarProveedorDialog
      v-model="dialogConfirmarEstado"
      :proveedor="proveedorConfirmar"
      :tipo="tipoConfirmar"
      :cargando="cambiandoEstado"
      @confirmar="confirmarEstado"
      />
    </div>
  </q-page>
</template>

<script setup>
import { reactive, ref, watch, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import ProvidersKpis from '../../components/proveedor/ProvidersKpis.vue'
import ProviderTypeTabs from '../../components/proveedor/ProviderTypeTabs.vue'
import ProvidersTable from '../../components/proveedor/ProvidersTable.vue'
import ProviderDetailDrawer from '../../components/proveedor/ProviderDetailDrawer.vue'
import NewProviderDialog from '../../components/proveedor/NewProviderDialog.vue'
import ConfirmarProveedorDialog from '../../components/proveedor/ConfirmarProveedorDialog.vue'
import { listarProveedores, crearProveedor, actualizarProveedor, activarProveedor, desactivarProveedor, obtenerEstadisticas, listarCiudades, obtenerProveedor, exportarProveedoresPDF, exportarProveedoresExcel, exportarProveedorDetallePDF, exportarProveedorDetalleExcel } from '../../api/proveedor/proveedor'

const $q = useQuasar()
const proveedores = ref([])
const cargando = ref(false)
const notificacion = ref(null)
const totalProveedores = ref(0)
const totalFiltrado = ref(0)
const conteoEmpresas = ref(0)
const conteoPersonas = ref(0)
const conteoActivos = ref(0)
const conteoInactivos = ref(0)
const pagina = ref(0)
const filasPorPagina = ref(10)

const filtroTipo = ref('todos')
const busqueda = ref('')
const filtrosVisibles = ref(false)

const filtros = reactive({
  ciudad: null,
  estado: null
})

const drawerAbierto = ref(false)
const proveedorSeleccionado = ref(null)
const dialogAbierto = ref(false)
const proveedorEditar = ref(null)

const dialogConfirmarEstado = ref(false)
const proveedorConfirmar = ref(null)
const tipoConfirmar = ref('activar')
const cambiandoEstado = ref(false)

const ciudades = ref([])

const cargarCiudades = async () => {
  try {
    const respuesta = await listarCiudades()
    ciudades.value = (respuesta || []).map((c) => ({ label: c.nombre, value: c.id }))
  } catch (error) {
    ciudades.value = []
  }
}

const cargarEstadisticas = async () => {
  try {
    const stats = await obtenerEstadisticas()
    totalProveedores.value = stats.total || 0
    conteoEmpresas.value = stats.empresas || 0
    conteoPersonas.value = stats.personas || 0
    conteoActivos.value = stats.activos || 0
    conteoInactivos.value = stats.inactivos || 0
  } catch (error) {
    // si falla, mantener los contadores en 0
  }
}

const cargarProveedores = async () => {
  cargando.value = true
  try {
    const params = {
      page: pagina.value,
      size: filasPorPagina.value,
      sortBy: 'idProveedor',
      sortDir: 'asc'
    }
    if (busqueda.value?.trim()) params.busqueda = busqueda.value.trim()
    if (filtroTipo.value !== 'todos') params.tipo = filtroTipo.value
    if (filtros.estado) params.estado = filtros.estado === 'Activo'
    if (filtros.ciudad) params.ciudad = filtros.ciudad

    const respuesta = await listarProveedores(params)
    proveedores.value = (respuesta.content || []).map(mapperProveedor)
    totalFiltrado.value = respuesta.totalElements || 0
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Error al cargar proveedores' })
  } finally {
    cargando.value = false
  }
}

function mapperProveedor(p) {
  const esEmpresa = p.tipo === 'EMPRESA'
  return {
    id: p.id,
    tipo: esEmpresa ? 'Empresa' : 'Persona',
    nombre: p.nombre || '',
    color: esEmpresa ? 'blue-8' : 'purple-9',
    categoria: esEmpresa ? 'Empresa' : 'Persona natural',
    documento: { tipo: esEmpresa ? 'NIT' : 'CI', numero: p.documento || '' },
    contacto: { nombre: p.contacto || '', telefono: p.telefono || '', correo: p.correo || '' },
    contactos: (p.contactos || []).map((c) => ({ telefono: c.telefono || '', correo: c.correo || '' })),
    ciudad: p.ciudad || '',
    zona: '',
    estado: p.estado ? 'Activo' : 'Inactivo',
    productos: [],
    condiciones: {},
    comprasMes: 0,
    totalComprado: 'Bs 0',
    historial: []
  }
}

function onPaginaCambio({ page, rowsPerPage }) {
  pagina.value = page - 1
  filasPorPagina.value = rowsPerPage
  cargarProveedores()
}

function mapperDireccion(d) {
  return {
    ciudad: (d && d.ciudad) || '',
    zona: (d && d.zona) || '',
    calle: (d && d.calle) || '',
    numero: (d && d.numero) || '',
    referencia: (d && d.referencia) || ''
  }
}

function mapperDetalleAEditar(d) {
  if (d.tipo === 'EMPRESA' && d.empresa) {
    const e = d.empresa
    return {
      id: d.id,
      tipo: 'Empresa',
      empresa: {
        nit: e.nit || '',
        razonSocial: e.razonSocial || '',
        nombreComercial: e.nombreComercial || '',
        representanteLegal: e.representanteLegal || '',
        contactos: (e.contactos || []).map((c) => ({ telefono: c.telefono || '', correo: c.correo || '' })),
        direccion: mapperDireccion(e.direccion)
      }
    }
  }
  if (d.tipo === 'PERSONA' && d.persona) {
    const p = d.persona
    return {
      id: d.id,
      tipo: 'Persona',
      persona: {
        nombres: p.nombres || '',
        apellidoPaterno: p.apellidoPaterno || '',
        apellidoMaterno: p.apellidoMaterno || '',
        ci: p.ci || '',
        fechaNacimiento: p.fechaNacimiento || '',
        sexo: p.sexo || '',
        telefono: p.telefono || '',
        correo: p.correo || '',
        direccion: mapperDireccion(p.direccion)
      }
    }
  }
  return null
}

function nuevoProveedor() {
  proveedorEditar.value = null
  dialogAbierto.value = true
}

function verDetalle(p) {
  proveedorSeleccionado.value = p
  drawerAbierto.value = true
}

async function editar(p) {
  drawerAbierto.value = false
  try {
    const detalle = await obtenerProveedor(p.id)
    proveedorEditar.value = mapperDetalleAEditar(detalle)
    dialogAbierto.value = true
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Error al cargar el proveedor para editar' })
  }
}

function historial(p) {
  drawerAbierto.value = false
  mostrar('history', 'Historial completo en desarrollo para: ' + p.nombre)
}

function exportarProveedor(p, formato) {
  const id = p && p.id
  if (!id) return
  $q.notify({ type: 'info', message: `Descargando ${formato === 'pdf' ? 'PDF' : 'Excel'}...` })
  const promesa = formato === 'pdf'
    ? exportarProveedorDetallePDF(id)
    : exportarProveedorDetalleExcel(id)
  promesa
    .then((blob) => descargarBlob(blob, `ficha_proveedor_${id}.${formato === 'pdf' ? 'pdf' : 'xlsx'}`))
    .catch((error) => {
      console.error('Error exportar proveedor:', error)
      $q.notify({ type: 'negative', message: error?.message || 'Error al exportar el proveedor.' })
    })
}

function cambiarEstado(p) {
  proveedorConfirmar.value = p
  tipoConfirmar.value = p.estado === 'Activo' ? 'desactivar' : 'activar'
  dialogConfirmarEstado.value = true
}

async function confirmarEstado() {
  const id = proveedorConfirmar.value?.id
  if (!id) return
  cambiandoEstado.value = true
  try {
    if (tipoConfirmar.value === 'activar') {
      await activarProveedor(id)
    } else {
      await desactivarProveedor(id)
    }
    $q.notify({ type: 'success', message: `Proveedor ${proveedorConfirmar.value.nombre} ${tipoConfirmar.value === 'activar' ? 'activado' : 'desactivado'} correctamente.` })
    dialogConfirmarEstado.value = false
    cargarProveedores()
    cargarEstadisticas()
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.message || `Error al ${tipoConfirmar.value === 'activar' ? 'activar' : 'desactivar'} el proveedor.` })
  } finally {
    cambiandoEstado.value = false
  }
}

function eliminar(p) {
  proveedores.value = proveedores.value.filter((x) => x.id !== p.id)
  mostrar('delete', 'Proveedor eliminado: ' + p.nombre)
}

function descargarDoc(tipo) {
  mostrar('download', 'Descarga de documento (' + tipo + ') en desarrollo.')
}

async function guardarNuevo(payload) {
  dialogAbierto.value = false
  cargando.value = true
  try {
    if (payload.id) {
      await actualizarProveedor(payload.id, payload)
      $q.notify({ type: 'positive', message: `Proveedor ${payload.tipo} actualizado correctamente.` })
    } else {
      await crearProveedor(payload)
      $q.notify({ type: 'positive', message: `Proveedor ${payload.tipo} registrado correctamente.` })
    }
    await cargarProveedores()
    await cargarEstadisticas()
  } catch (error) {
    const mensaje = error?.response?.data?.message || 'Error al guardar el proveedor'
    $q.notify({ type: 'negative', message: mensaje })
  } finally {
    cargando.value = false
  }
}

function mostrar(icono, texto) {
  notificacion.value = { color: 'primary', icono, texto }
}

function exportarPdf() {
  $q.notify({ type: 'info', message: 'Descargando PDF...' })
  exportarProveedoresPDF(paramsExportar())
    .then((blob) => descargarBlob(blob, 'reporte_proveedores.pdf'))
    .catch((error) => {
      console.error('Error exportar PDF:', error)
      $q.notify({ type: 'negative', message: error?.message || 'Error al exportar PDF.' })
    })
}

function exportarExcel() {
  $q.notify({ type: 'info', message: 'Descargando Excel...' })
  exportarProveedoresExcel(paramsExportar())
    .then((blob) => descargarBlob(blob, 'reporte_proveedores.xlsx'))
    .catch((error) => {
      console.error('Error exportar Excel:', error)
      $q.notify({ type: 'negative', message: error?.message || 'Error al exportar Excel.' })
    })
}

function paramsExportar() {
  const params = {}
  if (busqueda.value?.trim()) params.busqueda = busqueda.value.trim()
  if (filtroTipo.value !== 'todos') params.tipo = filtroTipo.value
  if (filtros.estado) params.estado = filtros.estado === 'Activo'
  if (filtros.ciudad) params.ciudad = filtros.ciudad
  return params
}

function descargarBlob(blob, filename) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}

function actualizar() {
  cargarProveedores()
  cargarEstadisticas()
  mostrar('refresh', 'Datos actualizados.')
}

function limpiarFiltros() {
  filtros.ciudad = null
  filtros.estado = null
  busqueda.value = ''
  pagina.value = 0
  cargarProveedores()
}

function buscar() {
  pagina.value = 0
  cargarProveedores()
}

watch(busqueda, () => {
  pagina.value = 0
  cargarProveedores()
})

watch(filtroTipo, () => {
  pagina.value = 0
  cargarProveedores()
})

onMounted(() => {
  cargarProveedores()
  cargarEstadisticas()
  cargarCiudades()
})
</script>

<style scoped src="../../assets/styles/proveedor/proveedor.css"></style>

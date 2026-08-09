<template>
  <q-page class="um-page">
    <div class="um-page-inner">
      <!-- Encabezado -->
      <div class="row items-center justify-between q-mb-lg" style="gap: 12px;">
        <div class="row items-center no-wrap">
          <div class="um-header-ic">
            <q-icon name="branding_watermark" size="22px" color="green-8" />
          </div>
          <div class="q-ml-sm">
            <div class="text-h4 text-bold um-header-title">Gestión de Marca</div>
            <div class="text-subtitle2 um-header-subtitle">Administra las marcas que se asignan a los productos</div>
          </div>
        </div>
        <MarcaDashboard :marcas="marcas" />

        <div class="um-header-actions">
          <q-btn no-caps label="Nueva Marca" icon="add" class="cat-btn cat-btn-primario" @click="abrirNuevaMarca()" />
        </div>
      </div>

      <!-- Dashboard -->

      <!-- Barra de filtros -->
      <MarcaToolbar
        :modelo="filtros"
        @buscar="buscar"
        @limpiar="limpiarFiltros"
      />

      <!-- Vista Tabla -->
      <MarcaTable
        :rows="marcas"
        :loading="cargando"
        :pagina="pagina"
        :total-paginas="totalPaginas"
        :total="total"
        @pagina-cambio="onPaginaCambio"
        @ver-detalle="verDetalle"
        @editar="editarMarca"
        @cambiar-estado="cambiarEstado"
      />

      <!-- Detalle -->
      <MarcaDetailDrawer
        v-model="drawerAbierto"
        :marca="marcaSeleccionada"
        @editar="editarMarca"
      />

      <!-- Diálogos -->
      <NewMarcaDialog
        v-model="dialogMarca"
        :marcas="marcas"
        :marca-editar="marcaEditar"
        :guardando="guardando"
        @guardar="guardarMarca"
      />

      <ConfirmarMarcaDialog
        v-model="dialogConfirmarEstado"
        :marca="marcaConfirmar"
        :tipo="tipoConfirmar"
        :cargando="cambiandoEstado"
        @confirmar="confirmarEstado"
      />
    </div>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import MarcaDashboard from '../../components/marca/MarcaDashboard.vue'
import MarcaToolbar from '../../components/marca/MarcaToolbar.vue'
import MarcaTable from '../../components/marca/MarcaTable.vue'
import MarcaDetailDrawer from '../../components/marca/MarcaDetailDrawer.vue'
import NewMarcaDialog from '../../components/marca/NewMarcaDialog.vue'
import ConfirmarMarcaDialog from '../../components/marca/ConfirmarMarcaDialog.vue'
import { listarMarcasPaginado, obtenerDetalleMarca, crearMarca, actualizarMarca, activarMarca, desactivarMarca } from '../../api/marca/marca'

const $q = useQuasar()

const cargando = ref(false)

const filtros = reactive({
  nombre: '',
  estado: 'todos'
})

const marcas = ref([])
const pagina = ref(1)
const totalPaginas = ref(1)
const total = ref(0)
const porPagina = 6

const drawerAbierto = ref(false)
const marcaSeleccionada = ref(null)
const dialogMarca = ref(false)
const marcaEditar = ref(null)
const dialogConfirmarEstado = ref(false)
const marcaConfirmar = ref(null)
const tipoConfirmar = ref('desactivar')
const guardando = ref(false)
const cambiandoEstado = ref(false)

onMounted(() => {
  cargarMarcas()
})

function estadoBackend(estado) {
  if (estado === 'todos' || estado == null) return null
  return estado === 'activo'
}

async function cargarMarcas() {
  cargando.value = true
  try {
    const data = await listarMarcasPaginado({
      busqueda: filtros.nombre.trim() || undefined,
      estado: estadoBackend(filtros.estado),
      page: pagina.value - 1,
      size: porPagina,
      sortBy: 'nombre',
      sortDir: 'asc'
    })
    marcas.value = data.content.map((m) => ({
      id: m.idMarca,
      nombre: m.nombre,
      descripcion: m.descripcion || '',
      estado: m.estado ? 'activo' : 'inactivo',
      nProductos: m.cantidadProductos,
      fechaCreacion: m.fechaCreacion,
      fechaActualizacion: m.fechaActualizacion
    }))
    total.value = data.totalElements
    totalPaginas.value = Math.max(1, data.totalPages)
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al listar las marcas.', position: 'top' })
  } finally {
    cargando.value = false
  }
}

function onPaginaCambio(nuevaPagina) {
  pagina.value = nuevaPagina
  cargarMarcas()
}

function buscar() {
  pagina.value = 1
  cargarMarcas()
}

function limpiarFiltros() {
  filtros.nombre = ''
  filtros.estado = 'todos'
  pagina.value = 1
  cargarMarcas()
}

async function verDetalle(marca) {
  marcaSeleccionada.value = marca
  drawerAbierto.value = true
  try {
    const d = await obtenerDetalleMarca(marca.id)
    marcaSeleccionada.value = {
      id: d.idMarca,
      nombre: d.nombre,
      descripcion: d.descripcion || '',
      estado: d.estado ? 'activo' : 'inactivo',
      nProductos: d.cantidadProductos,
      fechaCreacion: d.fechaCreacion,
      fechaActualizacion: d.fechaActualizacion
    }
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al cargar el detalle.', position: 'top' })
  }
}

function abrirNuevaMarca() {
  marcaEditar.value = null
  dialogMarca.value = true
}

function editarMarca(marca) {
  marcaEditar.value = marca
  dialogMarca.value = true
  drawerAbierto.value = false
}

async function guardarMarca(payload) {
  guardando.value = true
  try {
    if (payload.id) {
      await actualizarMarca(payload.id, { nombre: payload.nombre, descripcion: payload.descripcion })
      $q.notify({ type: 'positive', message: 'Marca actualizada.', position: 'top' })
    } else {
      await crearMarca({ nombre: payload.nombre, descripcion: payload.descripcion })
      $q.notify({ type: 'positive', message: 'Marca creada.', position: 'top' })
    }
    dialogMarca.value = false
    cargarMarcas()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al guardar la marca.', position: 'top' })
  } finally {
    guardando.value = false
  }
}

function cambiarEstado(marca) {
  marcaConfirmar.value = marca
  tipoConfirmar.value = marca.estado === 'activo' ? 'desactivar' : 'activar'
  dialogConfirmarEstado.value = true
}

async function confirmarEstado() {
  const marca = marcaConfirmar.value
  if (!marca) return
  const esActivar = tipoConfirmar.value === 'activar'
  cambiandoEstado.value = true
  try {
    if (esActivar) {
      await activarMarca(marca.id)
    } else {
      await desactivarMarca(marca.id)
    }
    marca.estado = esActivar ? 'activo' : 'inactivo'
    dialogConfirmarEstado.value = false
    $q.notify({
      type: marca.estado === 'activo' ? 'positive' : 'warning',
      message: `Marca ${marca.estado === 'activo' ? 'activada' : 'desactivada'}.`,
      position: 'top'
    })
    cargarMarcas()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al cambiar el estado de la marca.', position: 'top' })
  } finally {
    cambiandoEstado.value = false
  }
}
</script>

<style scoped src="../../assets/styles/categorias/categorias.css"></style>
<style scoped>
.um-page {
  background: #F5F7FA;
  min-height: 100vh;
  padding: 24px;
}

.um-page-inner {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

.um-header-ic {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #E8F5E9;
  border: 1px solid #A5D6A7;
  display: flex;
  align-items: center;
  justify-content: center;
}

.um-header-title {
  color: #1B5E20;
}

.um-header-subtitle {
  color: #5B6675;
}

@media (max-width: 640px) {
  .um-page {
    padding: 16px 12px;
  }
}
</style>
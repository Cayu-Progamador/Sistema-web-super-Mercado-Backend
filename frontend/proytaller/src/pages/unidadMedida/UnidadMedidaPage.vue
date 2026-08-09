<template>
  <q-page class="um-page">
    <div class="um-page-inner">
      <!-- Tarjetas de resumen -->
      
      <!-- Encabezado -->
      <div class="row items-center justify-between q-mb-lg" style="gap: 12px;">
        <div class="row items-center no-wrap">
          <div class="um-header-ic">
            <q-icon name="straighten" size="22px" color="green-8" />
          </div>
          <div class="q-ml-sm">
            <div class="text-h4 text-bold um-header-title">Gestión de Unidad de Medida</div>
            <div class="text-subtitle2 um-header-subtitle">Administra las unidades con las que se miden y venden los productos</div>
          </div>
        </div>
        
        <UnidadMedidaDashboard :unidades="unidades" />
        <div class="um-header-actions">
          <q-btn no-caps label="Nueva Unidad de Medida" icon="add" class="cat-btn cat-btn-primario" @click="abrirNuevaUnidad()" />
        </div>
      </div>

      <!-- Barra de filtros -->
      <UnidadMedidaToolbar
        :modelo="filtros"
        :tipos="tipos"
        @buscar="buscar"
        @limpiar="limpiarFiltros"
      />

      <!-- Vista Tabla -->
      <UnidadMedidaTable
        :rows="unidades"
        :loading="cargando"
        :pagina="pagina"
        :total-paginas="totalPaginas"
        :total="total"
        @pagina-cambio="onPaginaCambio"
        @ver-detalle="verDetalle"
        @editar="editarUnidad"
        @cambiar-estado="cambiarEstado"
      />

      <!-- Detalle -->
      <UnidadMedidaDetailDrawer
        v-model="drawerAbierto"
        :unidad="unidadSeleccionada"
        @editar="editarUnidad"
      />

      <!-- Diálogos -->
      <NewUnidadMedidaDialog
        v-model="dialogUnidad"
        :unidades="unidades"
        :tipos="tipos"
        :unidad-editar="unidadEditar"
        @guardar="guardarUnidad"
      />

      <ConfirmarUnidadMedidaDialog
        v-model="dialogConfirmarEstado"
        :unidad="unidadConfirmar"
        :tipo="tipoConfirmar"
        @confirmar="confirmarEstado"
      />
    </div>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import UnidadMedidaDashboard from '../../components/unidadMedida/UnidadMedidaDashboard.vue'
import UnidadMedidaToolbar from '../../components/unidadMedida/UnidadMedidaToolbar.vue'
import UnidadMedidaTable from '../../components/unidadMedida/UnidadMedidaTable.vue'
import UnidadMedidaDetailDrawer from '../../components/unidadMedida/UnidadMedidaDetailDrawer.vue'
import NewUnidadMedidaDialog from '../../components/unidadMedida/NewUnidadMedidaDialog.vue'
import ConfirmarUnidadMedidaDialog from '../../components/unidadMedida/ConfirmarUnidadMedidaDialog.vue'
import { listarUnidadesMedidaPaginado, obtenerDetalleUnidadMedida, listarTiposUnidadMedida, crearUnidadMedida, actualizarUnidadMedida, activarUnidadMedida, desactivarUnidadMedida } from '../../api/unidadMedida/unidadMedida'

const $q = useQuasar()

const cargando = ref(false)

const filtros = reactive({
  nombre: '',
  tipo: 'todos',
  estado: 'todos'
})

const unidades = ref([])
const pagina = ref(1)
const totalPaginas = ref(1)
const total = ref(0)
const porPagina = 6

const tipos = ref([])
const cargandoTipos = ref(false)

async function cargarTipos() {
  cargandoTipos.value = true
  try {
    const data = await listarTiposUnidadMedida()
    tipos.value = data
  } catch {
    tipos.value = []
  } finally {
    cargandoTipos.value = false
  }
}

function estadoBackend(estado) {
  if (estado === 'todos' || estado == null) return null
  return estado === 'activo'
}

async function cargarUnidades() {
  cargando.value = true
  try {
    const data = await listarUnidadesMedidaPaginado({
      busqueda: filtros.nombre.trim() || undefined,
      estado: estadoBackend(filtros.estado),
      idTipoUnidad: filtros.tipo === 'todos' ? undefined : filtros.tipo,
      page: pagina.value - 1,
      size: porPagina,
      sortBy: 'nombre',
      sortDir: 'asc'
    })
    unidades.value = data.content.map((u) => ({
      id: u.idUnidadMedida,
      nombre: u.nombre,
      abreviatura: u.abreviatura,
      estado: u.estado ? 'activo' : 'inactivo',
      tipoId: u.idTipoUnidad,
      tipoNombre: u.nombreTipoUnidad,
      fechaCreacion: null
    }))
    total.value = data.totalElements
    totalPaginas.value = Math.max(1, data.totalPages)
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al listar las unidades de medida.', position: 'top' })
  } finally {
    cargando.value = false
  }
}

function onPaginaCambio(nuevaPagina) {
  pagina.value = nuevaPagina
  cargarUnidades()
}

function buscar() {
  pagina.value = 1
  cargarUnidades()
}

function limpiarFiltros() {
  filtros.nombre = ''
  filtros.tipo = 'todos'
  filtros.estado = 'todos'
  pagina.value = 1
  cargarUnidades()
}

const drawerAbierto = ref(false)
const unidadSeleccionada = ref(null)
const dialogUnidad = ref(false)
const unidadEditar = ref(null)
const dialogConfirmarEstado = ref(false)
const unidadConfirmar = ref(null)
const tipoConfirmar = ref('desactivar')

onMounted(() => {
  cargarTipos()
  cargarUnidades()
})

async function verDetalle(uni) {
  unidadSeleccionada.value = uni
  drawerAbierto.value = true
  try {
    const d = await obtenerDetalleUnidadMedida(uni.id)
    unidadSeleccionada.value = {
      id: d.idUnidadMedida,
      nombre: d.nombre,
      abreviatura: d.abreviatura,
      estado: d.estado ? 'activo' : 'inactivo',
      tipoId: d.idTipoUnidad,
      tipoNombre: d.nombreTipoUnidad,
      fechaCreacion: d.fechaCreacion
    }
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al cargar el detalle.', position: 'top' })
  }
}

function abrirNuevaUnidad() {
  unidadEditar.value = null
  dialogUnidad.value = true
}

function editarUnidad(uni) {
  unidadEditar.value = uni
  dialogUnidad.value = true
  drawerAbierto.value = false
}

async function guardarUnidad(payload) {
  if (payload.id) {
    try {
      await actualizarUnidadMedida(payload.id, {
        nombre: payload.nombre,
        abreviatura: payload.abreviatura,
        idTipoUnidad: payload.tipoId
      })
      $q.notify({ type: 'positive', message: 'Unidad de medida actualizada.', position: 'top' })
      await cargarUnidades()
    } catch (e) {
      $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al actualizar la unidad de medida.', position: 'top' })
    }
  } else {
    try {
      const creada = await crearUnidadMedida({
        nombre: payload.nombre,
        abreviatura: payload.abreviatura,
        idTipoUnidad: payload.tipoId
      })
      unidades.value.unshift({
        id: creada.idUnidadMedida,
        nombre: creada.nombre,
        abreviatura: creada.abreviatura,
        estado: creada.estado ? 'activo' : 'inactivo',
        tipoId: creada.idTipoUnidad,
        tipoNombre: creada.nombreTipoUnidad,
        fechaCreacion: new Date().toISOString()
      })
      $q.notify({ type: 'positive', message: 'Unidad de medida creada.', position: 'top' })
    } catch (e) {
      $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al crear la unidad de medida.', position: 'top' })
    }
  }
  dialogUnidad.value = false
}

function cambiarEstado(uni) {
  unidadConfirmar.value = uni
  tipoConfirmar.value = uni.estado === 'activo' ? 'desactivar' : 'activar'
  dialogConfirmarEstado.value = true
}

async function confirmarEstado() {
  const uni = unidadConfirmar.value
  if (!uni) return
  const activar = uni.estado === 'inactivo'
  try {
    if (activar) {
      await activarUnidadMedida(uni.id)
    } else {
      await desactivarUnidadMedida(uni.id)
    }
    dialogConfirmarEstado.value = false
    $q.notify({
      type: activar ? 'positive' : 'warning',
      message: `Unidad de medida ${activar ? 'activada' : 'desactivada'}.`,
      position: 'top'
    })
    await cargarUnidades()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al cambiar el estado de la unidad de medida.', position: 'top' })
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
  max-width: none;
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
</style>

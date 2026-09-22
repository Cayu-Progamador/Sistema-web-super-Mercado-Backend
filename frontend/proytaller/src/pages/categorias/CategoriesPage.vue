<template>
  <q-page class="cat-page">
    <div class="cat-page-inner">
      
      <!-- Encabezado -->
      <div class="row items-center justify-between q-mb-lg" style="gap: 12px;">
        <div class="row items-center no-wrap">
          <div class="cat-header-ic">
            <q-icon name="category" size="22px" color="green-8" />
          </div>
          <div class="q-ml-sm">
            <div class="text-h4 text-bold cat-header-title">Gestión de Categorías</div>
            <div class="text-subtitle2 cat-header-subtitle">Administra la clasificación y organización de productos del supermercado</div>
          </div>
        </div>
        <!-- Tarjetas de resumen -->
        <CategoryDashboard />
        
        <div class="cat-header-actions">
          <q-btn v-permiso="'CATEGORIAS_CREAR'" no-caps label="Nueva Subcategoría" icon="playlist_add" class="cat-btn cat-btn-outline" @click="abrirNuevaSubcategoria()" />
          <q-btn v-permiso="'CATEGORIAS_CREAR'" no-caps label="Nueva Categoría" icon="add" class="cat-btn cat-btn-primario" @click="abrirNuevaCategoria()" />
        </div>
      </div>
      
      <!-- Barra de filtros + selector de vista -->
      <CategoriesToolbar
        :modelo="filtros"
        :vista="vistaActiva"
        @buscar="buscar"
        @limpiar="limpiarFiltros"
        @vista-cambio="onVistaCambio"
      />

      <!-- Vista Tabla -->
      <CategoriesTable
        v-if="vistaActiva === 'tabla'"
        :rows="categoriasPagina"
        :loading="cargando"
        :pagina="pagina"
        :total-paginas="totalPaginas"
        :total="total"
        @update:seleccionadas="seleccionadas = $event"
        @pagina-cambio="onPaginaCambio"
        @ver-detalle="verDetalle"
        @editar="editarCategoria"
        @agregar-subcategoria="abrirNuevaSubcategoria"
        @cambiar-estado="cambiarEstado"
        @eliminar="eliminarCategoria"
      />

      <!-- Vista Árbol -->
      <CategoriesTreeView
        v-else
        :categorias="categoriasPagina"
        @ver-detalle="verDetalle"
        @agregar-subcategoria="abrirNuevaSubcategoria"
        @editar-subcategoria="editarSubcategoria"
        @ver-detalle-subcategoria="verDetalleSubcategoria"
      />

      <!-- Detalle -->
      <CategoryDetailDrawer
        v-model="drawerAbierto"
        :categoria="categoriaSeleccionada"
        @editar="editarCategoria"
        @editar-subcategoria="editarSubcategoria"
        @agregar-subcategoria="abrirNuevaSubcategoria"
      />

      <SubcategoryDetailDrawer
        v-model="drawerSubcategoriaAbierto"
        :subcategoria="subcategoriaSeleccionada"
        :categoria="categoriaPadreSeleccionada"
        @editar-subcategoria="editarSubcategoria"
      />

      <!-- Diálogos -->
      <NewCategoryDialog
        v-model="dialogCategoria"
        :categorias="todasLasCategorias"
        :categoria-editar="categoriaEditar"
        @guardar="guardarCategoria"
      />

      <NewSubcategoryDialog
        v-model="dialogSubcategoria"
        :categorias="categoriasActivas"
        :padre-preseleccionado="padrePreseleccionado"
        :subcategoria-editar="subcategoriaEditar"
        @guardar="guardarSubcategoria"
      />

      <ConfirmarCategoriaDialog
        v-model="dialogConfirmarEstado"
        :categoria="categoriaConfirmar"
        :tipo="tipoConfirmar"
        @confirmar="confirmarEstado"
      />
    </div>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import CategoryDashboard from '../../components/categoria/CategoryDashboard.vue'
import CategoriesToolbar from '../../components/categoria/CategoriesToolbar.vue'
import CategoriesTable from '../../components/categoria/CategoriesTable.vue'
import CategoriesTreeView from '../../components/categoria/CategoriesTreeView.vue'
import CategoryDetailDrawer from '../../components/categoria/CategoryDetailDrawer.vue'
import SubcategoryDetailDrawer from '../../components/categoria/SubcategoryDetailDrawer.vue'
import NewCategoryDialog from '../../components/categoria/NewCategoryDialog.vue'
import NewSubcategoryDialog from '../../components/categoria/NewSubcategoryDialog.vue'
import ConfirmarCategoriaDialog from '../../components/categoria/ConfirmarCategoriaDialog.vue'
import { listarCategoriasPaginado, listarCategorias, listarCategoriasActivas, crearCategoria, actualizarCategoria, obtenerDetalleCategoria, activarCategoria, desactivarCategoria } from '../../api/categoria/categoria'
import { listarSubcategorias, crearSubcategoria, actualizarSubcategoria, obtenerDetalleSubcategoria } from '../../api/subcategoria/subcategoria'

const $q = useQuasar()

const vistaActiva = ref('tabla')
const cargando = ref(false)

const filtros = reactive({
  nombre: '',
  estado: 'todos'
})

const categoriasPagina = ref([])
const todasLasCategorias = ref([])
const categoriasActivas = ref([])
const todasLasSubcategorias = ref([])
const pagina = ref(1)
const totalPaginas = ref(1)
const total = ref(0)

const seleccionadas = ref([])
const drawerAbierto = ref(false)
const drawerSubcategoriaAbierto = ref(false)
const categoriaSeleccionada = ref(null)
const subcategoriaSeleccionada = ref(null)
const categoriaPadreSeleccionada = ref(null)
const dialogCategoria = ref(false)
const dialogSubcategoria = ref(false)
const categoriaEditar = ref(null)
const subcategoriaEditar = ref(null)
const padrePreseleccionado = ref(null)
const dialogConfirmarEstado = ref(false)
const categoriaConfirmar = ref(null)
const tipoConfirmar = ref('desactivar')

function estadoBackend() {
  if (filtros.estado === 'activo') return true
  if (filtros.estado === 'inactivo') return false
  return null
}

async function cargarCategorias() {
  cargando.value = true
  try {
    const params = {
      page: pagina.value - 1,
      size: 8
    }
    const nombre = filtros.nombre.trim()
    if (nombre) params.busqueda = nombre
    const estado = estadoBackend()
    if (estado !== null) params.estado = estado

    const res = await listarCategoriasPaginado(params)
    categoriasPagina.value = (res.content || []).map(mapperCategoria)
    total.value = res.totalElements || 0
    totalPaginas.value = Math.max(1, res.totalPages || 1)
    adjuntarSubcategorias(categoriasPagina.value)
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error al cargar las categorías.', position: 'top' })
  } finally {
    cargando.value = false
  }
}

async function cargarTodasParaDialogo() {
  try {
    const lista = await listarCategorias()
    todasLasCategorias.value = (Array.isArray(lista) ? lista : []).map(mapperCategoria)
    await cargarCategoriasActivas()
    await cargarSubcategorias()
    adjuntarSubcategorias(todasLasCategorias.value)
  } catch (e) {
    todasLasCategorias.value = []
  }
}

async function cargarCategoriasActivas() {
  try {
    const lista = await listarCategoriasActivas()
    categoriasActivas.value = (Array.isArray(lista) ? lista : []).map(mapperCategoria)
    adjuntarSubcategorias(categoriasActivas.value)
  } catch (e) {
    categoriasActivas.value = []
  }
}

async function cargarSubcategorias() {
  try {
    const lista = await listarSubcategorias()
    todasLasSubcategorias.value = (Array.isArray(lista) ? lista : []).map(mapperSubcategoria)
    adjuntarSubcategorias(categoriasPagina.value)
    adjuntarSubcategorias(todasLasCategorias.value)
    adjuntarSubcategorias(categoriasActivas.value)
  } catch (e) {
    todasLasSubcategorias.value = []
  }
}

function mapperSubcategoria(s) {
  return {
    id: s.idSubcategoria,
    nombre: s.nombre,
    descripcion: s.descripcion || '',
    estado: s.estado ? 'activo' : 'inactivo',
    idCategoria: s.idCategoria,
    productos: new Array(Number(s.cantidadProductos) || 0)
  }
}

function adjuntarSubcategorias(categorias) {
  const porCategoria = new Map()
  todasLasSubcategorias.value.forEach((s) => {
    if (!porCategoria.has(s.idCategoria)) porCategoria.set(s.idCategoria, [])
    porCategoria.get(s.idCategoria).push(s)
  })
  categorias.forEach((c) => {
    c.subcategorias = porCategoria.get(c.id) || []
  })
}

function mapperCategoria(c) {
  return {
    id: c.idCategoria,
    nombre: c.nombre,
    descripcion: c.descripcion || '',
    estado: c.estado ? 'activo' : 'inactivo',
    icono: iconoCategoria(c.nombre),
    color: colorIcono(c.nombre),
    fechaCreacion: c.fechaCreacion,
    subcategorias: []
  }
}

function onPaginaCambio(p) {
  if (p === pagina.value) return
  pagina.value = p
  cargarCategorias()
}

function buscar() {
  pagina.value = 1
  cargarCategorias()
}

function limpiarFiltros() {
  filtros.nombre = ''
  filtros.estado = 'todos'
  pagina.value = 1
  cargarCategorias()
}

function onVistaCambio(vista) {
  vistaActiva.value = vista
}

async function verDetalle(cat) {
  try {
    const detalle = await obtenerDetalleCategoria(cat.id)
    categoriaSeleccionada.value = mapperDetalleCategoria(detalle)
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error al cargar el detalle de la categoría.', position: 'top' })
    categoriaSeleccionada.value = cat
  }
  drawerAbierto.value = true
}

function mapperDetalleCategoria(d) {
  return {
    id: d.idCategoria,
    nombre: d.nombre,
    descripcion: d.descripcion || '',
    estado: d.estado ? 'activo' : 'inactivo',
    icono: iconoCategoria(d.nombre),
    color: colorIcono(d.nombre),
    fechaCreacion: d.fechaCreacion,
    subcategorias: (d.subcategorias || []).map((s) => ({
      id: s.idSubcategoria,
      nombre: s.nombre,
      descripcion: s.descripcion || '',
      estado: s.estado ? 'activo' : 'inactivo',
      productos: new Array(Number(s.cantidadProductos) || 0)
    }))
  }
}

async function verDetalleSubcategoria(sub, cat) {
  subcategoriaSeleccionada.value = { ...sub, productos: [] }
  categoriaPadreSeleccionada.value = cat
  drawerSubcategoriaAbierto.value = true
  try {
    const detalle = await obtenerDetalleSubcategoria(sub.id)
    subcategoriaSeleccionada.value = mapperDetalleSubcategoria(detalle)
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error al cargar el detalle de la subcategoría.', position: 'top' })
  }
}

function mapperDetalleSubcategoria(d) {
  return {
    id: d.idSubcategoria,
    nombre: d.nombre,
    descripcion: d.descripcion || '',
    estado: d.estado ? 'activo' : 'inactivo',
    fechaCreacion: d.fechaCreacion,
    productos: (d.productos || []).map((p) => p.nombre)
  }
}

function abrirNuevaCategoria() {
  categoriaEditar.value = null
  dialogCategoria.value = true
  cargarTodasParaDialogo()
}

function editarCategoria(cat) {
  categoriaEditar.value = cat
  dialogCategoria.value = true
  drawerAbierto.value = false
}

async function guardarCategoria(payload) {
  try {
    if (payload.id) {
      await actualizarCategoria(payload.id, {
        nombre: payload.nombre,
        descripcion: payload.descripcion || ''
      })
      $q.notify({ type: 'positive', message: 'Categoría actualizada.', position: 'top' })
    } else {
      await crearCategoria({
        nombre: payload.nombre,
        descripcion: payload.descripcion || ''
      })
      $q.notify({ type: 'positive', message: 'Categoría creada.', position: 'top' })
    }
    dialogCategoria.value = false
    await cargarCategorias()
    await cargarTodasParaDialogo()
  } catch (e) {
    const msg = e.response?.data?.message || 'Error al guardar la categoría.'
    $q.notify({ type: 'negative', message: msg, position: 'top' })
  }
}

function abrirNuevaSubcategoria(padre) {
  subcategoriaEditar.value = null
  padrePreseleccionado.value = padre ? padre.id : null
  dialogSubcategoria.value = true
  cargarTodasParaDialogo()
}

function editarSubcategoria(sub, cat) {
  subcategoriaEditar.value = {
    ...sub,
    categoriaId: cat.id
  }
  padrePreseleccionado.value = cat.id
  dialogSubcategoria.value = true
  drawerAbierto.value = false
}

async function guardarSubcategoria(payload) {
  if (payload.id) {
    try {
      await actualizarSubcategoria(payload.id, {
        nombre: payload.nombre,
        descripcion: payload.descripcion || '',
        idCategoria: payload.categoriaId
      })
      $q.notify({ type: 'positive', message: 'Subcategoría actualizada.', position: 'top' })
      dialogSubcategoria.value = false
      await cargarCategorias()
      await cargarTodasParaDialogo()
    } catch (e) {
      const msg = e.response?.data?.message || 'Error al actualizar la subcategoría.'
      $q.notify({ type: 'negative', message: msg, position: 'top' })
    }
    return
  }
  try {
    await crearSubcategoria({
      nombre: payload.nombre,
      descripcion: payload.descripcion || '',
      idCategoria: payload.categoriaId
    })
    $q.notify({ type: 'positive', message: 'Subcategoría creada.', position: 'top' })
    dialogSubcategoria.value = false
    await cargarCategorias()
    await cargarTodasParaDialogo()
  } catch (e) {
    const msg = e.response?.data?.message || 'Error al guardar la subcategoría.'
    $q.notify({ type: 'negative', message: msg, position: 'top' })
  }
}

function cambiarEstado(cat) {
  if (!cat || cat.id == null) return
  categoriaConfirmar.value = cat
  tipoConfirmar.value = cat.estado === 'activo' ? 'desactivar' : 'activar'
  dialogConfirmarEstado.value = true
}

async function confirmarEstado() {
  const cat = categoriaConfirmar.value
  if (!cat || cat.id == null) {
    $q.notify({ type: 'negative', message: 'No se pudo identificar la categoría.', position: 'top' })
    dialogConfirmarEstado.value = false
    return
  }

  const esActivacion = tipoConfirmar.value === 'activar'

  if (esActivacion && cat.estado === 'activo') {
    $q.notify({ type: 'warning', message: 'La categoría ya está activada.', position: 'top' })
    dialogConfirmarEstado.value = false
    return
  }

  if (!esActivacion && cat.estado === 'inactivo') {
    $q.notify({ type: 'warning', message: 'La categoría ya está desactivada.', position: 'top' })
    dialogConfirmarEstado.value = false
    return
  }

  try {
    const res = esActivacion
      ? await activarCategoria(cat.id)
      : await desactivarCategoria(cat.id)
    $q.notify({
      type: 'positive',
      message: esActivacion ? `Categoría "${res.nombre || cat.nombre}" activada.` : `Categoría "${res.nombre || cat.nombre}" desactivada.`,
      position: 'top'
    })
    dialogConfirmarEstado.value = false
    await cargarCategorias()
    await cargarTodasParaDialogo()
  } catch (e) {
    const msg = e.response?.data?.message || 'Error al cambiar el estado de la categoría.'
    $q.notify({ type: 'negative', message: msg, position: 'top' })
  }
}

function eliminarCategoria(cat) {
  $q.dialog({
    title: 'Eliminar categoría',
    message: `¿Seguro que deseas eliminar "${cat.nombre}"?`,
    cancel: true,
    persistent: true
  }).onOk(() => {
    $q.notify({ type: 'info', message: 'La eliminación se conectará próximamente.', position: 'top' })
  })
}

const paleta = ['#2E7D32', '#388E3C', '#00796B', '#F57C00', '#6A1B9A', '#1565C0', '#C62828', '#00838F', '#4E342E', '#5B6675']

function hashNombre(nombre) {
  let h = 0
  const s = (nombre || '').toLowerCase()
  for (let i = 0; i < s.length; i++) h = (h * 31 + s.charCodeAt(i)) >>> 0
  return h
}

function colorIcono(nombre) {
  return paleta[hashNombre(nombre) % paleta.length]
}

function iconoCategoria(nombre) {
  const n = (nombre || '').toLowerCase()
  if (n.includes('beb')) return 'local_cafe'
  if (n.includes('fruta') || n.includes('verd')) return 'eco'
  if (n.includes('limp')) return 'cleaning_services'
  if (n.includes('alim') || n.includes('gran') || n.includes('abar')) return 'kitchen'
  if (n.includes('lácte') || n.includes('lact')) return 'egg_alt'
  return 'category'
}

onMounted(async () => {
  await cargarSubcategorias()
  await cargarCategorias()
  cargarTodasParaDialogo()
})
</script>

<style scoped src="../../assets/styles/categorias/categorias.css"></style>

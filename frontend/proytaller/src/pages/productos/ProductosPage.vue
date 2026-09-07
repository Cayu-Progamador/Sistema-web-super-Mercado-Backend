<template>
  <q-page class="prod-page">
    <div class="prod-page-inner">
      <!-- Encabezado -->
      <div class="row items-center justify-between q-mb-lg">
        <div class="col">
          <div class="row items-center no-wrap">
            <div class="prod-header-ic">
              <q-icon name="inventory_2" size="22px" color="green-8" />
            </div>
            <div class="q-ml-sm prod-header-texto">
              <div class="text-h4 text-bold prod-header-title">Gestión de Productos</div>
              <div class="text-subtitle2 prod-header-subtitle">Catálogo maestro · Productos y presentaciones de venta</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Cards de resumen -->
      <ProductKpiCards class="q-mb-sm" />

      <!-- Barra de herramientas -->
      <ProductsToolbar
        v-model:busqueda="busqueda"
        :filtros-visible="filtrosVisibles"
        @nuevo="nuevoProducto"
        @exportar-excel="exportarExcel"
        @exportar-pdf="exportarPdf"
        @actualizar="actualizar"
        @toggle-filtros="filtrosVisibles = !filtrosVisibles"
      />

      <!-- Filtros colapsables -->
      <q-slide-transition>
        <div v-show="filtrosVisibles" class="q-mt-sm">
          <ProductsFilters
            :categorias="categoriasOpt"
            :subcategorias="subcategoriasOpt"
            :marcas="marcasOpt"
            :unidades="unidadesOpt"
            :modelo="filtros"
            @filtro="onFiltrosChange"
            @buscar="buscar"
            @limpiar="limpiarFiltros"
          />
        </div>
      </q-slide-transition>

      <!-- Tabla -->
      <div class="q-mt-sm">
        <ProductsTable
          :rows="productos"
          :loading="cargando"
          :rows-number="totalFiltrado"
          @ver-detalle="verDetalle"
          @editar="editar"
          @cambiar-estado="cambiarEstado"
          @eliminar="eliminar"
          @pagina-cambio="onPaginaCambio"
        />
      </div>

      <!-- Detalle -->
      <ProductDetailDialog
        v-model="drawerAbierto"
        :producto="productoSeleccionado"
        @editar="editar"
      />

      <!-- Nuevo / Editar -->
      <NewProductDialog
        v-model="dialogAbierto"
        :producto-editar="productoEditar"
        :categorias="categoriasOpt"
        :subcategorias="subcategoriasOpt"
        :marcas="marcasOpt"
        :unidades="unidadesOpt"
        @guardar="guardarNuevo"
        @categoria-cambio="cargarSubcategoriasPorCategoria"
        @categoria-creada="onCategoriaCreada"
        @subcategoria-creada="onSubcategoriaCreada"
        @marca-creada="onMarcaCreada"
      />

      <ConfirmarProductoDialog
        v-model="dialogConfirmarEstado"
        :producto="productoConfirmar"
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
import ProductsToolbar from '../../components/producto/ProductsToolbar.vue'
import ProductKpiCards from '../../components/producto/ProductKpiCards.vue'
import ProductsFilters from '../../components/producto/ProductsFilters.vue'
import ProductsTable from '../../components/producto/ProductsTable.vue'
import ProductDetailDialog from '../../components/producto/ProductDetailDialog.vue'
import NewProductDialog from '../../components/producto/NewProductDialog.vue'
import ConfirmarProductoDialog from '../../components/producto/ConfirmarProductoDialog.vue'
import {
  listarProductos,
  crearProducto,
  actualizarProducto,
  eliminarProducto,
  activarProducto,
  desactivarProducto,
  listarCategorias,
  listarSubcategorias,
  listarMarcas,
  listarUnidades,
  subirImagenProducto,
  exportarProductosPDF,
  exportarProductosExcel
} from '../../api/productos/productos'

const $q = useQuasar()

const productos = ref([])
const cargando = ref(false)
const totalFiltrado = ref(0)
const pagina = ref(0)
const filasPorPagina = ref(10)

const busqueda = ref('')
const filtrosVisibles = ref(false)

let temporizadorBusqueda = null
watch(busqueda, () => {
  clearTimeout(temporizadorBusqueda)
  temporizadorBusqueda = setTimeout(() => {
    pagina.value = 0
    cargarProductos()
  }, 300)
})

const filtros = reactive({
  categoria: null,
  subcategoria: null,
  marca: null,
  estado: null,
  unidadBase: null
})

const categoriasOpt = ref([])
const subcategoriasOpt = ref([])
const marcasOpt = ref([])
const unidadesOpt = ref([])

const drawerAbierto = ref(false)
const productoSeleccionado = ref(null)
const dialogAbierto = ref(false)
const productoEditar = ref(null)
const dialogConfirmarEstado = ref(false)
const productoConfirmar = ref(null)
const tipoConfirmar = ref('desactivar')
const cambiandoEstado = ref(false)

function cargarCatalogos() {
  Promise.all([
    listarCategorias(),
    listarSubcategorias(),
    listarMarcas(),
    listarUnidades()
  ])
    .then(([cats, subs, marcas, unidades]) => {
      categoriasOpt.value = cats.map((c) => ({ label: c.nombre, value: c.idCategoria }))
      subcategoriasOpt.value = subs.map((s) => ({ label: s.nombre, value: s.idSubcategoria }))
      marcasOpt.value = marcas.map((m) => ({ label: m.nombre, value: m.idMarca }))
      unidadesOpt.value = unidades.map((u) => ({
        label: u.nombre,
        value: u.idUnidadMedida,
        tipo: u.nombreTipoUnidad
      }))
    })
    .catch(() => {
      $q.notify({ type: 'negative', message: 'No se pudieron cargar los catálogos.' })
    })
}

function cargarProductos() {
  cargando.value = true
  const params = {
    page: pagina.value,
    size: filasPorPagina.value,
    busqueda: busqueda.value?.trim() || undefined,
    estado: filtros.estado === 'activo' ? true : filtros.estado === 'inactivo' ? false : undefined,
    idCategoria: filtros.categoria || undefined,
    idSubcategoria: filtros.subcategoria || undefined,
    idMarca: filtros.marca || undefined,
    idUnidadBase: filtros.unidadBase || undefined
  }
  listarProductos(params)
    .then((data) => {
      productos.value = (data.content || []).map(mapearProducto)
      totalFiltrado.value = data.totalElements || 0
    })
    .catch(() => {
      $q.notify({ type: 'negative', message: 'No se pudieron cargar los productos.' })
    })
    .finally(() => {
      cargando.value = false
    })
}

function mapearProducto(p) {
  return {
    id: p.idProducto,
    codigo: p.codigoInterno,
    nombre: p.nombre,
    descripcion: p.descripcion,
    categoria: p.nombreCategoria,
    subcategoria: p.nombreSubcategoria,
    marca: p.nombreMarca,
    unidadBase: p.nombreUnidadBase,
    controlaLote: Boolean(p.controlaLote),
    controlaVencimiento: Boolean(p.controlaVencimiento),
    stockMinimo: p.stockMinimo ?? null,
    stockMaximo: p.stockMaximo ?? null,
    tieneStock: Boolean(p.tieneStock),
    tieneMovimientos: Boolean(p.tieneMovimientos),
    nPresentaciones: (p.presentaciones || []).length,
    presentaciones: p.presentaciones || [],
    estado: p.estado ? 'Activo' : 'Inactivo',
    imagen: p.imagen,
    idCategoria: p.idCategoria,
    idSubcategoria: p.idSubcategoria,
    idMarca: p.idMarca,
    idUnidadBase: p.idUnidadBase
  }
}

function onPaginaCambio({ page, rowsPerPage }) {
  pagina.value = page - 1
  filasPorPagina.value = rowsPerPage
  cargarProductos()
}

function nuevoProducto() {
  productoEditar.value = null
  dialogAbierto.value = true
  subcategoriasOpt.value = []
}

function verDetalle(p) {
  productoSeleccionado.value = p
  drawerAbierto.value = true
}

function editar(p) {
  drawerAbierto.value = false
  productoSeleccionado.value = null
  productoEditar.value = p
  dialogAbierto.value = true
  cargarSubcategoriasPorCategoria(p.idCategoria)
}

function cargarSubcategoriasPorCategoria(idCategoria) {
  if (!idCategoria) {
    subcategoriasOpt.value = []
    return
  }
  listarSubcategorias({ idCategoria })
    .then((subs) => {
      subcategoriasOpt.value = subs.map((s) => ({ label: s.nombre, value: s.idSubcategoria }))
    })
    .catch(() => {
      subcategoriasOpt.value = []
      $q.notify({ type: 'negative', message: 'No se pudieron cargar las subcategorías.' })
    })
}

function onCategoriaCreada({ id, nombre }) {
  if (!categoriasOpt.value.some((c) => c.value === id)) {
    categoriasOpt.value.push({ label: nombre, value: id })
  }
}

function onSubcategoriaCreada({ id, nombre }) {
  if (!subcategoriasOpt.value.some((s) => s.value === id)) {
    subcategoriasOpt.value.push({ label: nombre, value: id })
  }
}

function onMarcaCreada({ id, nombre }) {
  if (!marcasOpt.value.some((m) => m.value === id)) {
    marcasOpt.value.push({ label: nombre, value: id })
  }
}

function cambiarEstado(p) {
  productoConfirmar.value = p
  tipoConfirmar.value = p.estado === 'Activo' ? 'desactivar' : 'activar'
  dialogConfirmarEstado.value = true
}

async function confirmarEstado() {
  const producto = productoConfirmar.value
  if (!producto) return
  const esActivar = tipoConfirmar.value === 'activar'
  cambiandoEstado.value = true
  try {
    if (esActivar) {
      await activarProducto(producto.id)
    } else {
      await desactivarProducto(producto.id)
    }
    producto.estado = esActivar ? 'Activo' : 'Inactivo'
    dialogConfirmarEstado.value = false
    $q.notify({
      type: producto.estado === 'Activo' ? 'positive' : 'warning',
      message: `Producto ${producto.estado === 'Activo' ? 'activado' : 'desactivado'}.`,
      position: 'top'
    })
    cargarProductos()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al cambiar el estado del producto.', position: 'top' })
  } finally {
    cambiandoEstado.value = false
  }
}

function eliminar(p) {
  productos.value = productos.value.filter((x) => x.id !== p.id)
  eliminarFn(p)
}

function exportarExcel() {
  $q.notify({ type: 'info', message: 'Descargando Excel...' })
  exportarProductosExcel(paramsExportar())
    .then((blob) => descargarBlob(blob, 'reporte_productos.xlsx'))
    .catch((error) => {
      console.error('Error exportar Excel:', error)
      $q.notify({ type: 'negative', message: error?.message || 'Error al exportar Excel.' })
    })
}

function exportarPdf() {
  $q.notify({ type: 'info', message: 'Descargando PDF...' })
  exportarProductosPDF(paramsExportar())
    .then((blob) => descargarBlob(blob, 'reporte_productos.pdf'))
    .catch((error) => {
      console.error('Error exportar PDF:', error)
      $q.notify({ type: 'negative', message: error?.message || 'Error al exportar PDF.' })
    })
}

function paramsExportar() {
  return {
    busqueda: busqueda.value?.trim() || undefined,
    estado: filtros.estado === 'activo' ? true : filtros.estado === 'inactivo' ? false : undefined,
    idCategoria: filtros.categoria || undefined,
    idSubcategoria: filtros.subcategoria || undefined,
    idMarca: filtros.marca || undefined,
    idUnidadBase: filtros.unidadBase || undefined
  }
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
  cargarProductos()
  $q.notify({ type: 'success', message: 'Datos actualizados.' })
}

function limpiarFiltros() {
  filtros.categoria = null
  filtros.subcategoria = null
  filtros.marca = null
  filtros.estado = null
  filtros.unidadBase = null
  busqueda.value = ''
  pagina.value = 0
  cargarProductos()
}

function buscar() {
  pagina.value = 0
  cargarProductos()
}

function guardarNuevo(payload) {
  const subirImagen = (() => {
    if (!payload.imagen || typeof payload.imagen !== 'object') {
      return Promise.resolve(typeof payload.imagen === 'string' ? payload.imagen : null)
    }
    return subirImagenProducto(payload.imagen)
      .then((res) => res.url || null)
      .catch(() => {
        $q.notify({ type: 'warning', message: 'No se pudo subir la nueva imagen, se conservará la actual.' })
        return typeof payload.imagen === 'string' ? payload.imagen : null
      })
  })()

  subirImagen
    .then((imagenUrl) => {
      const body = {
        nombre: payload.nombre,
        descripcion: payload.descripcion,
        idSubcategoria: payload.subcategoria,
        idMarca: payload.tieneMarca ? (payload.marca || null) : null,
        idUnidadBase: payload.unidadBase,
        controlaLote: !!payload.controlaLote,
        controlaVencimiento: !!payload.controlaVencimiento,
        stockMinimo: Number(payload.stockMinimo),
        stockMaximo: payload.stockMaximo === null || payload.stockMaximo === ''
          ? null
          : Number(payload.stockMaximo),
        imagen: imagenUrl,
        presentaciones: (payload.presentaciones || []).map((p) => ({
          nombrePresentacion: p.nombre,
          idUnidadMedida: p.unidad,
          factorConversion: Number(p.factor) || 1,
          precioVenta: Number(p.precio),
          esPrincipal: !!p.principal,
          usaCodigo: !!p.usaCodigo,
          codigo: p.usaCodigo ? p.codigo : null
        }))
      }

      const peticion = payload.id
        ? actualizarProducto(payload.id, body)
        : crearProducto(body)

      return peticion
    })
    .then(() => {
      $q.notify({
        type: 'positive',
        message: payload.id
          ? `Producto ${payload.nombre} actualizado correctamente.`
          : `Producto ${payload.nombre} registrado correctamente.`
      })
    })
    .catch((error) => {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || 'Error al guardar el producto.'
      })
    })
    .finally(() => {
      dialogAbierto.value = false
      cargarProductos()
    })
}

function onFiltrosChange(nuevosFiltros) {
  Object.assign(filtros, nuevosFiltros)
}

function eliminarFn(p) {
  $q.notify({ type: 'negative', message: `Producto ${p.nombre} eliminado.` })
}

onMounted(() => {
  cargarProductos()
  cargarCatalogos()
})
</script>

<style scoped src="../../assets/styles/productos/productos.css"></style>

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

      <!-- Barra de herramientas -->
      <ProductsToolbar
        v-model:busqueda="busqueda"
        :filtros-visible="filtrosVisibles"
        @nuevo="nuevoProducto"
        @importar="importar"
        @exportar="exportar"
        @actualizar="actualizar"
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
          @duplicar="duplicar"
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
      />
    </div>
  </q-page>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import ProductsToolbar from '../../components/producto/ProductsToolbar.vue'
import ProductsFilters from '../../components/producto/ProductsFilters.vue'
import ProductsTable from '../../components/producto/ProductsTable.vue'
import ProductDetailDialog from '../../components/producto/ProductDetailDialog.vue'
import NewProductDialog from '../../components/producto/NewProductDialog.vue'
import {
  listarProductos,
  crearProducto,
  actualizarProducto,
  eliminarProducto,
  actualizarEstadoProducto,
  listarCategorias,
  listarSubcategorias,
  listarMarcas,
  listarUnidades
} from '../../api/productos/productos'

const $q = useQuasar()

const productos = ref([])
const cargando = ref(false)
const totalFiltrado = ref(0)
const pagina = ref(0)
const filasPorPagina = ref(10)

const busqueda = ref('')
const filtrosVisibles = ref(false)

const filtros = reactive({
  categoria: null,
  subcategoria: null,
  marca: null,
  estado: null,
  unidadBase: null,
  codigoBarra: null,
  presentaciones: null
})

const categoriasOpt = ref([])
const subcategoriasOpt = ref([])
const marcasOpt = ref([])
const unidadesOpt = ref([])

const drawerAbierto = ref(false)
const productoSeleccionado = ref(null)
const dialogAbierto = ref(false)
const productoEditar = ref(null)

const unidadesBase = [
  { label: 'Kilogramo', value: 'kg' },
  { label: 'Gramo', value: 'g' },
  { label: 'Litro', value: 'l' },
  { label: 'Mililitro', value: 'ml' },
  { label: 'Unidad', value: 'uni' },
  { label: 'Docena', value: 'doc' },
  { label: 'Ramo', value: 'ramo' },
  { label: 'Atado', value: 'atado' },
  { label: 'Arroba', value: 'arroba' },
  { label: 'Cuartilla', value: 'cuartilla' },
  { label: 'Quintal', value: 'quintal' }
]

/* Datos de ejemplo para la tabla */
const productosDemo = [
  {
    id: 1, codigo: 'P-0001', nombre: 'Coca-Cola', descripcion: 'Bebida gaseosa carbonatada',
    categoria: 'Bebidas', subcategoria: 'Gaseosas', marca: 'Coca-Cola', unidadBase: 'Unidad',
    nPresentaciones: 3, estado: 'Activo', imagen: null,
    presentaciones: [
      { id: 1, nombre: 'Botella 500 ml', unidad: 'Unidad', factor: 1, precio: 'Bs 6', principal: true },
      { id: 2, nombre: 'Pack x6', unidad: 'Pack', factor: 6, precio: 'Bs 33', principal: false },
      { id: 3, nombre: 'Fardo x12', unidad: 'Fardo', factor: 12, precio: 'Bs 62', principal: false }
    ]
  },
  {
    id: 2, codigo: 'P-0002', nombre: 'Arroz', descripcion: 'Arroz de grano largo',
    categoria: 'Despensa', subcategoria: 'Granos', marca: '', unidadBase: 'Kilogramo',
    nPresentaciones: 3, estado: 'Activo', imagen: null,
    presentaciones: [
      { id: 1, nombre: 'Bolsa 1 kg', unidad: 'Bolsa', factor: 1, precio: 'Bs 9', principal: true },
      { id: 2, nombre: 'Bolsa 5 kg', unidad: 'Bolsa', factor: 5, precio: 'Bs 42', principal: false },
      { id: 3, nombre: 'Quintal', unidad: 'Quintal', factor: 46, precio: 'Bs 400', principal: false }
    ]
  },
  {
    id: 3, codigo: 'P-0003', nombre: 'Leche PIL', descripcion: 'Leche entera envasada',
    categoria: 'Lácteos', subcategoria: 'Leches', marca: 'PIL', unidadBase: 'Litro',
    nPresentaciones: 2, estado: 'Activo', imagen: null,
    presentaciones: [
      { id: 1, nombre: '1 Litro', unidad: 'Litro', factor: 1, precio: 'Bs 9', principal: true },
      { id: 2, nombre: '500 ml', unidad: 'Mililitro', factor: 0.5, precio: 'Bs 5', principal: false }
    ]
  },
  { id: 4, codigo: 'P-0004', nombre: 'Tomate', descripcion: 'Tomate riñón', categoria: 'Verduras', subcategoria: 'Frutas y verduras', marca: '', unidadBase: 'Kilogramo', nPresentaciones: 0, estado: 'Activo', imagen: null, presentaciones: [] },
  { id: 5, codigo: 'P-0005', nombre: 'Carne de Res', descripcion: 'Carne cortada', categoria: 'Carnes', subcategoria: 'Res', marca: '', unidadBase: 'Kilogramo', nPresentaciones: 0, estado: 'Activo', imagen: null, presentaciones: [] },
  { id: 6, codigo: 'P-0006', nombre: 'Papa', descripcion: 'Papa imilla', categoria: 'Verduras', sub: 'Tubérculos', marca: '', unidad: 'Kilogramo', nPresentaciones: 0, estado: 'Activo', imagen: null, presentaciones: [] },
  { id: 7, codigo: 'P-0007', nombre: 'Azúcar', descripcion: 'Azúcar blanca 1 kg', categoria: 'Despensa', subcategoria: 'Dulces', marca: '', unidadBase: 'Kilogramo', nPresentaciones: 1, estado: 'Activo', imagen: null, presentaciones: [{ id: 1, nombre: '1 kg', unidad: 'Bolsa', factor: 1, precio: 'Bs 8', principal: true }] },
  { id: 8, codigo: 'P-0008', nombre: 'Aceite', descripcion: 'Aceite vegetal 1 litro', categoria: 'Despensa', subcategoria: 'Aceites', marca: '', unidadBase: 'Litro', nPresentaciones: 1, estado: 'Inactivo', imagen: null, presentaciones: [{ id: 1, nombre: 'Bolsa', unidad: 'Litro', factor: 1, precio: 'Bs 27', principal: true }] }
]

/* Datos demo para selects */
const demoCategorias = [
  { label: 'Bebidas', value: 1 },
  { label: 'Despensa', value: 2 },
  { label: 'Lácteos', value: 3 },
  { label: 'Verduras', value: 4 },
  { label: 'Carnes', value: 5 }
]

const demoSubcategorias = [
  { label: 'Gaseosas', value: 1 },
  { label: 'Granos', value: 2 },
  { label: 'Leches', value: 3 },
  { label: 'Tubérculos', value: 4 }
]

const demoMarcas = [
  { label: 'Coca-Cola', value: 1 },
  { label: 'PIL', value: 2 },
  { label: 'San Simón', value: 3 }
]

const demoUnidades = [
  { label: 'Kilogramo', value: 'kg' },
  { label: 'Gramo', value: 'g' },
  { label: 'Litro', value: 'l' },
  { label: 'Mililitro', value: 'ml' },
  { label: 'Unidad', value: 'uni' },
  { label: 'Docena', value: 'doc' },
  { label: 'Ramo', value: 'ramo' },
  { label: 'Atado', value: 'atado' }
]

function cargarCatalogos() {
  categoriasOpt.value = demoCategorias
  subcategoriasOpt.value = demoSubcategorias
  marcasOpt.value = demoMarcas
  unidadesOpt.value = demoUnidades
}

function cargarProductos() {
  cargando.value = true
  // Simulación de carga de datos con filtros
  setTimeout(() => {
    let lista = [...productosDemo]
    if (busqueda.value?.trim()) {
      const q = busqueda.value.toLowerCase()
      lista = lista.filter(
        (p) =>
          p.nombre.toLowerCase().includes(q) ||
          p.codigo.toLowerCase().includes(q) ||
          (p.marca || '').toLowerCase().includes(q)
      )
    }
    if (filtros.estado) {
      lista = lista.filter((p) => p.estado.toLowerCase() === filtros.estado)
    }
    if (filtros.presentaciones === 'si') {
      lista = lista.filter((p) => p.nPresentaciones > 0)
    } else if (filtros.presentaciones === 'no') {
      lista = lista.filter((p) => p.nPresentaciones === 0)
    }
    productos.value = lista
    totalFiltrado.value = lista.length
    cargando.value = false
  }, 200)
}

function onPaginaCambio({ page, rowsPerPage }) {
  pagina.value = page - 1
  filasPorPagina.value = rowsPerPage
}

function nuevoProducto() {
  productoEditar.value = null
  dialogAbierto.value = true
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
}

function duplicar(p) {
  productoEditar.value = null
  dialogAbierto.value = true
  $q.notify({ type: 'info', message: 'Duplicando producto: ' + p.nombre })
}

function cambiarEstado(p) {
  p.estado = p.estado === 'Activo' ? 'Inactivo' : 'Activo'
  cambiarEstadoFn(p)
}

function eliminar(p) {
  productos.value = productos.value.filter((x) => x.id !== p.id)
  eliminarFn(p)
}

function importar() {
  $q.notify({ type: 'info', message: 'Importación de productos en desarrollo.' })
}

function exportar() {
  $q.notify({ type: 'info', message: 'Exportación a Excel en desarrollo.' })
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
  filtros.codigoBarra = null
  filtros.presentaciones = null
  busqueda.value = ''
  pagina.value = 0
  cargarProductos()
}

function buscar() {
  pagina.value = 0
  cargarProductos()
}

function guardarNuevo(payload) {
  dialogAbierto.value = false
  // Aquí se conectaría con la API real
  if (payload.id) {
    $q.notify({ type: 'positive', message: `Producto ${payload.nombre} actualizado correctamente.` })
  } else {
    payload.codigoInterno = generarCodigoInterno()
    $q.notify({ type: 'positive', message: `Producto ${payload.nombre} registrado correctamente.` })
  }
  cargarProductos()
}

function generarCodigoInterno() {
  const nums = productosDemo
    .map((p) => parseInt(String(p.codigo || '').replace(/^P-/, ''), 10))
    .filter((n) => !isNaN(n))
  const next = (nums.length ? Math.max(...nums) : 0) + 1
  return `P-${String(next).padStart(4, '0')}`
}

function onFiltrosChange(nuevosFiltros) {
  Object.assign(filtros, nuevosFiltros)
}

function cambiarEstadoFn(p) {
  $q.notify({ type: 'info', message: `Estado de ${p.nombre} actualizado.` })
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
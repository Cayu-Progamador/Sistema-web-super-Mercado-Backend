<template>
  <q-page class="pv-page">
    <!-- Encabezado -->
    <div class="pv-header row items-center no-wrap">
      <div class="pv-header-titulo row items-center no-wrap">
        <div class="pv-header-ic">
          <q-icon name="storefront" size="22px" color="primary" />
        </div>
        <div class="q-ml-sm">
          <div class="pv-title">Gestión de Proveedores</div>
          <div class="pv-subtitle">Administración de proveedores empresa y proveedores persona natural</div>
        </div>
      </div>
      <q-space />
      <q-breadcrumbs class="pv-breadcrumbs" separator="chevron_right" separator-color="grey-5">
        <q-breadcrumbs-el label="Inicio" icon="home" />
        <q-breadcrumbs-el label="Compras" />
        <q-breadcrumbs-el label="Gestión de Proveedores" class="pv-bread-actual" />
      </q-breadcrumbs>
    </div>

    <!-- KPI -->
    <ProvidersKpis
      :total="proveedores.length"
      :empresas="conteoEmpresas"
      :personas="conteoPersonas"
      :activos="conteoActivos"
      :calificacion="calificacionPromedio"
    />

    <div class="q-mt-md">
      <!-- Tabs de tipo -->
      <ProviderTypeTabs
        v-model="filtroTipo"
        :total="proveedores.length"
        :empresas="conteoEmpresas"
        :personas="conteoPersonas"
      />

      <!-- Barra de herramientas -->
      <div class="pv-toolbar row items-center no-wrap q-gutter-sm q-mt-sm">
        <q-btn color="primary" unelevated label="Nuevo Proveedor" icon="add" no-caps @click="nuevoProveedor" />
        <q-btn outline color="primary" label="Exportar PDF" icon="picture_as_pdf" no-caps dense @click="exportarPdf" />
        <q-btn outline color="primary" label="Exportar Excel" icon="grid_on" no-caps dense @click="exportarExcel" />
        <q-btn outline color="grey-8" icon="refresh" dense @click="actualizar" />
        <q-space />
        <q-btn
          outline
          color="grey-8"
          icon="filter_alt"
          :label="filtrosVisibles ? 'Ocultar filtros' : 'Filtros'"
          no-caps
          dense
          @click="filtrosVisibles = !filtrosVisibles"
        />
        <q-input
          v-model="busqueda"
          outlined
          dense
          placeholder="Buscar por nombre, NIT o CI..."
          class="pv-search"
          clearable
          color="primary"
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
              <div class="col-3">
                <q-select v-model="filtros.categoria" :options="categorias" label="Categoría de producto" outlined dense clearable color="primary" emit-value map-options />
              </div>
              <div class="col-3">
                <q-select v-model="filtros.ciudad" :options="ciudades" label="Ciudad / Zona" outlined dense clearable color="primary" emit-value map-options />
              </div>
              <div class="col-3">
                <q-select v-model="filtros.estado" :options="['Activo', 'Inactivo']" label="Estado" outlined dense clearable color="primary" />
              </div>
              <div class="col-3">
                <q-input v-model.number="filtros.califMin" label="Calificación mínima" outlined dense type="number" min="0" max="5" step="0.5" color="primary">
                  <template v-slot:append>
                    <q-icon name="star" color="amber-7" size="16px" />
                  </template>
                </q-input>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </q-slide-transition>

      <!-- Tabla -->
      <div class="q-mt-sm">
        <ProvidersTable
          :rows="filasFiltradas"
          :loading="cargando"
          @ver-detalle="verDetalle"
          @editar="editar"
          @historial="historial"
          @cambiar-estado="cambiarEstado"
          @eliminar="eliminar"
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
    <NewProviderDialog v-model="dialogAbierto" @guardar="guardarNuevo" />

    <!-- Mensajes -->
    <q-banner v-if="notificacion" class="q-mt-md" :color="notificacion.color" text-color="white">
      <template v-slot:avatar>
        <q-icon :name="notificacion.icono" />
      </template>
      {{ notificacion.texto }}
      <template v-slot:action>
        <q-btn flat color="white" label="Cerrar" @click="notificacion = null" />
      </template>
    </q-banner>
  </q-page>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import ProvidersKpis from '../../components/proveedor/ProvidersKpis.vue'
import ProviderTypeTabs from '../../components/proveedor/ProviderTypeTabs.vue'
import ProvidersTable from '../../components/proveedor/ProvidersTable.vue'
import ProviderDetailDrawer from '../../components/proveedor/ProviderDetailDrawer.vue'
import NewProviderDialog from '../../components/proveedor/NewProviderDialog.vue'

const proveedores = ref([
  {
    id: 1,
    tipo: 'Empresa',
    nombre: 'Coca-Cola S.A.',
    color: 'blue-8',
    categoria: 'Gaseosas · Bebidas',
    documento: { tipo: 'NIT', numero: '102-4589012-3', contrato: 'CTR-2025-011' },
    contacto: { nombre: 'María Fernández', telefono: '700-123-456', correo: 'ventas@cocacola.bo' },
    ciudad: 'La Paz',
    zona: '',
    calificacion: 4.8,
    estado: 'Activo',
    productos: ['Gaseosas', 'Bebidas', 'Jugos'],
    condiciones: { formaPago: 'Crédito 30 días', moneda: 'Bolivianos (Bs)', diasEntrega: 'Lunes a Viernes' },
    comprasMes: 4,
    totalComprado: 'Bs 48.250',
    historial: [
      { fecha: '28/07/2026', producto: 'Gaseosas 2L · 500 uni', monto: 'Bs 12.500', orden: 'OC-2026-0148' },
      { fecha: '15/07/2026', producto: 'Bebidas energéticas · 200 uni', monto: 'Bs 9.800', orden: 'OC-2026-0132' },
      { fecha: '02/07/2026', producto: 'Jugos tetra · 300 uni', monto: 'Bs 11.200', orden: 'OC-2026-0117' }
    ]
  },
  {
    id: 2,
    tipo: 'Empresa',
    nombre: 'PIL Andina S.A.',
    color: 'blue-8',
    categoria: 'Lácteos',
    documento: { tipo: 'NIT', numero: '102-7743201-7', contrato: 'CTR-2025-004' },
    contacto: { nombre: 'Carlos Rojas', telefono: '711-456-789', correo: 'comercial@pil.com.bo' },
    ciudad: 'La Paz',
    zona: '',
    calificacion: 4.5,
    estado: 'Activo',
    productos: ['Lácteos', 'Quesos', 'Yogur'],
    condiciones: { formaPago: 'Crédito 60 días', moneda: 'Bolivianos (Bs)', diasEntrega: 'Fijos (Lun y Jue)' },
    comprasMes: 5,
    totalComprado: 'Bs 61.300',
    historial: [
      { fecha: '27/07/2026', producto: 'Leche entera · 400 uni', monto: 'Bs 13.600', orden: 'OC-2026-0147' },
      { fecha: '20/07/2026', producto: 'Yogur frutal · 250 uni', monto: 'Bs 10.400', orden: 'OC-2026-0138' },
      { fecha: '13/07/2026', producto: 'Queso criollo · 120 kg', monto: 'Bs 14.200', orden: 'OC-2026-0129' }
    ]
  },
  {
    id: 3,
    tipo: 'Empresa',
    nombre: 'Nestlé Bolivia S.A.',
    color: 'blue-8',
    categoria: 'Alimentos procesados',
    documento: { tipo: 'NIT', numero: '102-1187450-9', contrato: 'CTR-2025-009' },
    contacto: { nombre: 'Lucía Vaca', telefono: '722-789-123', correo: 'suministros@nestle.com.bo' },
    ciudad: 'Santa Cruz',
    zona: '',
    calificacion: 4.3,
    estado: 'Activo',
    productos: ['Cereales', 'Snacks', 'Enlatados'],
    condiciones: { formaPago: 'Crédito 30 días', moneda: 'Bolivianos (Bs)', diasEntrega: 'A convenir' },
    comprasMes: 3,
    totalComprado: 'Bs 39.100',
    historial: [
      { fecha: '25/07/2026', producto: 'Cereales caja · 180 uni', monto: 'Bs 11.500', orden: 'OC-2026-0145' },
      { fecha: '11/07/2026', producto: 'Snacks surtidos · 300 uni', monto: 'Bs 8.900', orden: 'OC-2026-0125' },
      { fecha: '04/07/2026', producto: 'Conservas · 200 uni', monto: 'Bs 9.700', orden: 'OC-2026-0119' }
    ]
  },
  {
    id: 4,
    tipo: 'Empresa',
    nombre: 'Industrias Arrozera Norte',
    color: 'blue-8',
    categoria: 'Granos · Cereales',
    documento: { tipo: 'NIT', numero: '102-3358901-4', contrato: 'CTR-2026-002' },
    contacto: { nombre: 'Jorge Añez', telefono: '733-222-334', correo: 'ventas@arrozera.com.bo' },
    ciudad: 'Beni',
    zona: '',
    calificacion: 4.1,
    estado: 'Inactivo',
    productos: ['Arroz', 'Azúcar', 'Legumbres'],
    condiciones: { formaPago: 'Crédito 30 días', moneda: 'Bolivianos (Bs)', diasEntrega: 'Lunes a Viernes' },
    comprasMes: 0,
    totalComprado: 'Bs 22.700',
    historial: [
      { fecha: '18/06/2026', producto: 'Arroz grano de oro · 500 sacos', monto: 'Bs 22.700', orden: 'OC-2026-0108' }
    ]
  },
  {
    id: 5,
    tipo: 'Persona',
    nombre: 'Juan Mamani',
    color: 'purple-9',
    categoria: 'Productor agrícola',
    documento: { tipo: 'CI', numero: '6734567-1', contrato: null },
    contacto: { nombre: 'Juan Mamani', telefono: '744-555-667', correo: '' },
    ciudad: 'La Paz',
    zona: 'Valle de Zongo',
    calificacion: 4.6,
    estado: 'Activo',
    productos: ['Papas', 'Verduras'],
    condiciones: { formaPago: 'Contado', frecuenciaEntrega: '2 veces por semana' },
    comprasMes: 8,
    totalComprado: 'Bs 14.900',
    historial: [
      { fecha: '29/07/2026', producto: 'Papa imilla · 400 kg', monto: 'Bs 2.400', orden: 'OC-2026-0150' },
      { fecha: '22/07/2026', producto: 'Verduras surtidas · 180 kg', monto: 'Bs 1.900', orden: 'OC-2026-0141' },
      { fecha: '15/07/2026', producto: 'Papa de temporada · 350 kg', monto: 'Bs 2.100', orden: 'OC-2026-0133' }
    ]
  },
  {
    id: 6,
    tipo: 'Persona',
    nombre: 'Rosa Quispe',
    color: 'purple-9',
    categoria: 'Productora láctea',
    documento: { tipo: 'CI', numero: '4820193-2', contrato: null },
    contacto: { nombre: 'Rosa Quispe', telefono: '755-678-890', correo: '' },
    ciudad: 'La Paz',
    zona: 'Achacachi',
    calificacion: 4.9,
    estado: 'Activo',
    productos: ['Quesos', 'Lácteos'],
    condiciones: { formaPago: 'Contado', frecuenciaEntrega: 'Semanal' },
    comprasMes: 4,
    totalComprado: 'Bs 11.800',
    historial: [
      { fecha: '24/07/2026', producto: 'Queso criollo · 90 kg', monto: 'Bs 3.600', orden: 'OC-2026-0143' },
      { fecha: '17/07/2026', producto: 'Queso fresco · 80 kg', monto: 'Bs 3.200', orden: 'OC-2026-0136' },
      { fecha: '10/07/2026', producto: 'Leche fresca · 150 l', monto: 'Bs 2.100', orden: 'OC-2026-0124' }
    ]
  },
  {
    id: 7,
    tipo: 'Persona',
    nombre: 'Pedro Choque',
    color: 'purple-9',
    categoria: 'Productor de frutas',
    documento: { tipo: 'CI', numero: '5923314-4', contrato: null },
    contacto: { nombre: 'Pedro Choque', telefono: '766-111-223', correo: '' },
    ciudad: 'Cochabamba',
    zona: 'Cliza',
    calificacion: 4.2,
    estado: 'Activo',
    productos: ['Frutas', 'Tomates'],
    condiciones: { formaPago: 'Contado', frecuenciaEntrega: 'Diario' },
    comprasMes: 12,
    totalComprado: 'Bs 18.600',
    historial: [
      { fecha: '30/07/2026', producto: 'Tomate pera · 120 kg', monto: 'Bs 1.500', orden: 'OC-2026-0152' },
      { fecha: '29/07/2026', producto: 'Durazno · 80 kg', monto: 'Bs 1.900', orden: 'OC-2026-0151' },
      { fecha: '23/07/2026', producto: 'Tomate · 100 kg', monto: 'Bs 1.400', orden: 'OC-2026-0142' }
    ]
  },
  {
    id: 8,
    tipo: 'Empresa',
    nombre: 'Embotelladora COBAL',
    color: 'blue-8',
    categoria: 'Bebidas',
    documento: { tipo: 'NIT', numero: '102-6654321-1', contrato: 'CTR-2025-021' },
    contacto: { nombre: 'Ana Flores', telefono: '777-333-445', correo: 'ventas@cobal.com.bo' },
    ciudad: 'Santa Cruz',
    zona: '',
    calificacion: 4.4,
    estado: 'Activo',
    productos: ['Bebidas', 'Gaseosas'],
    condiciones: { formaPago: 'Crédito 45 días', moneda: 'Bolivianos (Bs)', diasEntrega: 'Lunes a Viernes' },
    comprasMes: 6,
    totalComprado: 'Bs 55.800',
    historial: [
      { fecha: '26/07/2026', producto: 'Aguas saborizadas · 350 uni', monto: 'Bs 10.200', orden: 'OC-2026-0146' },
      { fecha: '19/07/2026', producto: 'Gaseosas 600ml · 420 uni', monto: 'Bs 12.600', orden: 'OC-2026-0137' }
    ]
  },
  {
    id: 9,
    tipo: 'Persona',
    nombre: 'Maria Flores',
    color: 'purple-9',
    categoria: 'Productora de huevos',
    documento: { tipo: 'CI', numero: '7123405-7', contrato: null },
    contacto: { nombre: 'Maria Flores', telefono: '788-444-556', correo: '' },
    ciudad: 'Cochabamba',
    zona: 'Quillacollo',
    calificacion: 4.7,
    estado: 'Activo',
    productos: ['Huevos', 'Pan'],
    condiciones: { formaPago: 'Contado', frecuenciaEntrega: '2 veces por semana' },
    comprasMes: 7,
    totalComprado: 'Bs 9.400',
    historial: [
      { fecha: '27/07/2026', producto: 'Huevos · 60 mapas', monto: 'Bs 2.300', orden: 'OC-2026-0149' },
      { fecha: '20/07/2026', producto: 'Huevos · 55 mapas', monto: 'Bs 2.100', orden: 'OC-2026-0139' }
    ]
  },
  {
    id: 10,
    tipo: 'Empresa',
    nombre: 'Molinos del Sur',
    color: 'blue-8',
    categoria: 'Harinas · Pan',
    documento: { tipo: 'NIT', numero: '102-8890123-6', contrato: 'CTR-2026-005' },
    contacto: { nombre: 'Diego Suárez', telefono: '799-555-667', correo: 'comercial@molinosdelsur.bo' },
    ciudad: 'Oruro',
    zona: '',
    calificacion: 3.9,
    estado: 'Inactivo',
    productos: ['Pan', 'Fideos', 'Cereales'],
    condiciones: { formaPago: 'Crédito 30 días', moneda: 'Bolivianos (Bs)', diasEntrega: 'A convenir' },
    comprasMes: 1,
    totalComprado: 'Bs 16.900',
    historial: [
      { fecha: '05/07/2026', producto: 'Harina de trigo · 200 sacos', monto: 'Bs 16.900', orden: 'OC-2026-0120' }
    ]
  }
])

const filtroTipo = ref('todos')
const busqueda = ref('')
const filtrosVisibles = ref(false)
const cargando = ref(false)
const notificacion = ref(null)

const filtros = reactive({
  categoria: null,
  ciudad: null,
  estado: null,
  califMin: null
})

const drawerAbierto = ref(false)
const proveedorSeleccionado = ref(null)
const dialogAbierto = ref(false)

const categorias = [...new Set(proveedores.value.flatMap((p) => p.productos))]
const ciudades = [...new Set(proveedores.value.map((p) => p.ciudad))]

const conteoEmpresas = computed(() => proveedores.value.filter((p) => p.tipo === 'Empresa').length)
const conteoPersonas = computed(() => proveedores.value.filter((p) => p.tipo === 'Persona').length)
const conteoActivos = computed(() => proveedores.value.filter((p) => p.estado === 'Activo').length)
const calificacionPromedio = computed(() => {
  if (!proveedores.value.length) return 0
  return proveedores.value.reduce((acc, p) => acc + p.calificacion, 0) / proveedores.value.length
})

const filasFiltradas = computed(() => {
  const q = busqueda.value.trim().toLowerCase()
  return proveedores.value.filter((p) => {
    if (filtroTipo.value !== 'todos' && p.tipo !== filtroTipo.value) return false
    if (filtros.estado && p.estado !== filtros.estado) return false
    if (filtros.ciudad && p.ciudad !== filtros.ciudad) return false
    if (filtros.califMin && p.calificacion < filtros.califMin) return false
    if (filtros.categoria && !p.productos.includes(filtros.categoria)) return false
    if (q) {
      const texto = `${p.nombre} ${p.documento.numero} ${p.contacto.nombre}`.toLowerCase()
      if (!texto.includes(q)) return false
    }
    return true
  })
})

function nuevoProveedor() {
  dialogAbierto.value = true
}

function verDetalle(p) {
  proveedorSeleccionado.value = p
  drawerAbierto.value = true
}

function editar(p) {
  drawerAbierto.value = false
  mostrar('edit', 'Módulo de edición en desarrollo. Proveedor: ' + p.nombre)
}

function historial(p) {
  drawerAbierto.value = false
  mostrar('history', 'Historial completo en desarrollo para: ' + p.nombre)
}

function cambiarEstado(p) {
  p.estado = p.estado === 'Activo' ? 'Inactivo' : 'Activo'
  mostrar('toggle_on', `${p.nombre} ahora está ${p.estado}`)
}

function eliminar(p) {
  proveedores.value = proveedores.value.filter((x) => x.id !== p.id)
  mostrar('delete', 'Proveedor eliminado: ' + p.nombre)
}

function descargarDoc(tipo) {
  mostrar('download', 'Descarga de documento (' + tipo + ') en desarrollo.')
}

function guardarNuevo(payload) {
  const id = Math.max(0, ...proveedores.value.map((x) => x.id)) + 1
  const empresa = payload.empresa || {}
  const persona = payload.persona || {}
const nombre =
    payload.tipo === 'Empresa'
      ? empresa.nombreComercial || empresa.razonSocial
      : `${persona.nombres} ${persona.apellidoPaterno} ${persona.apellidoMaterno}`.trim()
  const dir = payload.tipo === 'Empresa' ? empresa.direccion : persona.direccion
  proveedores.value.push({
    id,
    tipo: payload.tipo,
    nombre,
    color: payload.tipo === 'Empresa' ? 'blue-8' : 'purple-9',
    categoria: payload.tipo === 'Empresa' ? 'Empresa' : 'Persona natural',
    documento: payload.tipo === 'Empresa'
      ? { tipo: 'NIT', numero: empresa.nit }
      : { tipo: 'CI', numero: persona.ci },
    contacto: {
      nombre: `${persona.nombres} ${persona.apellidoPaterno}`.trim() || '',
      telefono: persona.telefono,
      correo: persona.correo
    },
    ciudad: dir?.ciudad || '',
    zona: dir?.zona || '',
    calificacion: 4.0,
    estado: 'Activo',
    productos: [],
    condiciones: {},
    comprasMes: 0,
    totalComprado: 'Bs 0',
    historial: []
  })
  dialogAbierto.value = false
  mostrar('check_circle', `Proveedor ${nombre} registrado correctamente.`)
}

function mostrar(icono, texto) {
  notificacion.value = { color: 'primary', icono, texto }
}

function exportarPdf() {
  mostrar('picture_as_pdf', 'Exportación a PDF en desarrollo.')
}

function exportarExcel() {
  mostrar('grid_on', 'Exportación a Excel en desarrollo.')
}

function actualizar() {
  cargando.value = true
  setTimeout(() => {
    cargando.value = false
    mostrar('refresh', 'Datos actualizados.')
  }, 600)
}
</script>

<style scoped src="../../assets/styles/proveedor/proveedor.css"></style>

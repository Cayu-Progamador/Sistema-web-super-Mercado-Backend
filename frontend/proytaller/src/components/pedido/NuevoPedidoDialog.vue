<template>
  <q-dialog
    :model-value="visible"
    @update:model-value="val => { visible = val }"
    maximized
    transition-show="slide-up"
    transition-hide="slide-down"
    class="pd-dialog-wrap"
  >
    <q-card class="pd-dialog-card column">
      <div class="pd-accent-bar"></div>
      <q-card-section class="pd-dialog-header q-px-lg q-py-md">
        <div class="row items-center justify-between">
          <div class="row items-center q-gutter-sm">
            <div class="pd-modal-icon">
              <q-icon name="shopping_cart" size="22px" color="#2E7D32" />
            </div>
            <div>
              <div class="pd-modal-title">{{ pedidoEditar ? 'Editar Pedido' : 'Nuevo Pedido' }}</div>
              <div class="pd-modal-eyebrow">Proveedor · {{ nombreProveedor }}</div>
            </div>
          </div>
          <q-btn icon="close" flat round dense class="pd-close-btn" v-close-popup @click="cerrar">
            <q-tooltip>Cerrar</q-tooltip>
          </q-btn>
        </div>
      </q-card-section>

<q-stepper
        v-model="paso"
        ref="stepperRef"
        color="green-7"
        animated
        header-nav
        class="fit col pd-stepper"
      >


        <!-- PASO 1: Datos del pedido -->
<q-step :name="1" title="Datos del pedido" done-icon="check" :done="paso > 1">
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-6">
              <q-select
                v-model="form.idProveedor"
                :options="proveedorOpciones"
                label="Proveedor *"
                outlined
                dense
                emit-value
                map-options
                color="green-7"
                :rules="[val => !!val || 'Seleccione un proveedor']"
                lazy-rules
              />
            </div>
            <div class="col-12 col-md-6">
              <q-input v-model="form.fechaEntregaEstimada" label="Fecha estimada de entrega" type="date" outlined dense color="green-7" />
            </div>
            <div class="col-12">
              <q-input v-model="form.observacion" label="Observación" type="textarea" outlined dense color="green-7" placeholder="Entregar productos en buen estado." autogrow />
            </div>
          </div>
        </q-step>

        <!-- PASO 2: Productos -->
        <q-step :name="2" title="Productos" done-icon="check" :done="paso > 2">
          <div class="row items-center justify-between q-mb-md">
            <div>
              <div class="text-subtitle1 text-bold text-dark">Productos del pedido</div>
              <div class="text-caption text-grey-6">Seleccione los productos y cantidades a solicitar.</div>
            </div>
            <q-btn
              class="pd-btn pd-btn-primario"
              label="Agregar producto"
              icon="add"
              no-caps
              @click="agregarProducto"
            />
          </div>

          <q-separator class="q-mb-md" />

          <template v-if="form.detalles.length > 0">
            <div class="row q-col-gutter-md">
              <div
                v-for="(detalle, index) in form.detalles"
                :key="index"
                class="col-12"
              >
<q-card flat bordered class="pd-prod-card">
                  <q-card-section class="q-pa-md">
                    <div class="row q-col-gutter-sm items-center">
                      <div class="col-12 col-md-6">
                        <q-select
                          v-model="detalle.idProducto"
                          :options="productoOpcionesFiltradas"
                          label="Producto *"
                          outlined
                          dense
                          emit-value
                          map-options
                          color="green-7"
                          use-input
                          input-debounce="300"
                          @filter="filtrarProductos"
                          :rules="[val => !!val || 'Seleccione un producto']"
                          lazy-rules
                          @update:model-value="cambiarProducto(detalle, index)"
                        />
                      </div>
                      <div class="col-12 col-md-6">
                        <q-select
                          v-model="detalle.idUnidadMedida"
                          :options="unidadesDeProducto(detalle)"
                          label="Unidad de compra *"
                          outlined
                          dense
                          emit-value
                          map-options
                          color="green-7"
                          :rules="[val => !!val || 'Seleccione una unidad']"
                          lazy-rules
                          @update:model-value="cambiarUnidad(detalle, index)"
                        />
                      </div>
                      <div class="col-12 col-md-6">
                        <q-input
                          v-model.number="detalle.cantidad"
                          label="Cantidad *"
                          type="number"
                          outlined
                          dense
                          min="1"
                          step="1"
                          color="green-7"
                          :rules="[val => (val ?? 0) >= 1 || 'Debe ser al menos 1']"
                          lazy-rules
                          @keydown="bloquearNegativos"
                        />
                      </div>
                      <div class="col-12 col-md-6">
                        <q-input v-model="detalle.factor" label="Factor" outlined dense readonly color="green-7" />
                      </div>
                      <div class="col-12">
                        <div class="pd-prod-tag text-center">{{ equivalencia(detalle) }}</div>
                      </div>
                      <div class="col-12 text-center q-mt-xs">
                        <q-btn flat round dense color="negative" icon="delete" @click="eliminarProducto(index)">
                          <q-tooltip>Eliminar producto</q-tooltip>
                        </q-btn>
                      </div>
                    </div>
                  </q-card-section>
                </q-card>
              </div>
            </div>

            <div class="q-mt-md">
              <q-table
                :rows="tablaResumenRows"
                :columns="tablaResumenColumnas"
                row-key="index"
                flat
                dense
                hide-bottom
                :pagination="{ rowsPerPage: 0 }"
                class="pd-table"
              />
            </div>
          </template>

          <div v-else class="pd-empty">
            <q-icon name="shopping_basket" class="pd-empty-icon" />
            <div class="pd-empty-title">Sin productos</div>
            <div class="pd-empty-sub">Agregue productos al pedido...</div>
          </div>
        </q-step>

        <!-- PASO 3: Revisión -->
        <q-step :name="3" title="Revisión" done-icon="check" :done="paso > 3">
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-4">
              <q-card flat bordered>
                <q-card-section>
                  <div class="pd-detalle-section-title">
                    <q-icon name="info" color="green-7" size="18px" />
                    Información del pedido
                  </div>
<div class="pd-label">Proveedor</div>
                  <div class="pd-valor">{{ nombreProveedor }}</div>
                  <div class="pd-label q-mt-sm">Entrega estimada</div>
                  <div class="pd-valor">{{ form.fechaEntregaEstimada ? formatFecha(form.fechaEntregaEstimada) : '—' }}</div>
                  <div class="pd-label q-mt-sm">Observación</div>
                  <div class="pd-valor" style="font-weight: 500; color: #344054;">{{ form.observacion || '—' }}</div>
                </q-card-section>
              </q-card>
            </div>

            <div class="col-12 col-md-8">
              <q-card flat bordered>
                <q-card-section>
                  <div class="pd-detalle-section-title">
                    <q-icon name="inventory_2" color="green-7" size="18px" />
                    Productos del pedido
                  </div>
                  <q-table
                    :rows="tablaResumenRows"
                    :columns="tablaResumenColumnas"
                    row-key="index"
                    flat
                    dense
                    hide-bottom
                    :pagination="{ rowsPerPage: 0 }"
                    class="pd-table"
                  />
                </q-card-section>
              </q-card>

              <q-card class="pd-resumen-card q-mt-md" flat>
                <q-card-section class="row items-center justify-between">
                  <div>
                    <div class="pd-label">Total de productos</div>
                    <div class="text-h6 text-bold text-dark">{{ form.detalles.length }}</div>
                  </div>
                  <div>
                    <div class="pd-label">Cantidad total solicitada</div>
                    <div class="text-h6 text-bold text-dark">{{ resumenCantidades }}</div>
                  </div>
                  <q-icon name="fact_check" size="40px" color="green-7" />
                </q-card-section>
              </q-card>
            </div>
          </div>
        </q-step>

        <!-- PASO 4: Confirmación -->
        <q-step :name="4" title="Confirmación" done-icon="check">
          <div class="row justify-center q-py-lg">
            <div class="col-12 col-md-8 text-center">
              <q-icon name="check_circle" class="pd-exito-ic" />
              <div class="pd-exito-title">¡Pedido creado exitosamente!</div>
              <div class="pd-exito-sub">El pedido ha sido registrado en el sistema correctamente.</div>

              <q-card flat bordered class="q-mt-lg">
                <q-card-section class="row justify-center q-col-gutter-md">
                  <div class="col-auto text-left">
                    <div class="pd-label">N.º Pedido</div>
                    <div class="pd-valor text-primary">{{ pedidoCreado?.numero || '—' }}</div>
                  </div>
                  <div class="col-auto text-left">
                    <div class="pd-label">Proveedor</div>
                    <div class="pd-valor">{{ pedidoCreado?.proveedor || nombreProveedor || '—' }}</div>
                  </div>
                  <div class="col-auto text-left">
                    <div class="pd-label">Estado</div>
                    <q-badge :color="colorEstado(pedidoCreado?.estado || 'BORRADOR')" class="pd-badge-estado q-mt-sm">
                      {{ (pedidoCreado?.estado || 'BORRADOR').replace(/_/g, ' ') }}
                    </q-badge>
                  </div>
                </q-card-section>
              </q-card>
            </div>
          </div>
        </q-step>

        <template v-slot:message>
          <q-banner v-if="paso === 2 && !form.detalles.length" class="bg-orange-1 text-orange-9">
            Agregue al menos un producto para continuar.
          </q-banner>
        </template>

        <template v-slot:navigation>
          <q-stepper-navigation class="pd-stepper-nav q-px-lg q-py-md">
            <div class="row items-center justify-between">
              <q-btn
                v-if="paso > 1"
                flat
                color="green-8"
                label="← Volver"
                icon="arrow_back"
                no-caps
                @click="stepperRef?.previous()"
              />

              <div class="row q-gutter-sm">
                <q-btn
                  v-if="paso === 1"
                  flat
                  color="green-8"
                  label="Cancelar"
                  no-caps
                  v-close-popup
                  @click="cerrar"
                />

<q-btn
                  v-if="paso === 2"
                  outline
                  color="green-8"
                  :label="pedidoEditar ? 'Guardar cambios' : 'Guardar como borrador'"
                  icon="draft"
                  no-caps
                  :loading="guardando"
                  :disable="!paso2Valido"
                  @click="guardarBorrador"
                />

<q-btn
                  v-if="paso === 1"
                  unelevated
                  color="green-8"
                  label="Guardar y continuar"
                  icon="arrow_forward"
                  no-caps
                  :disable="!paso1Valido"
                  @click="validarPaso1"
                />

                <q-btn
                  v-if="paso === 2"
                  unelevated
                  color="green-8"
                  label="Guardar y continuar"
                  icon="arrow_forward"
                  no-caps
                  :disable="!paso2Valido"
                  @click="guardarYContinuarPaso2"
                />

<q-btn
                  v-if="paso === 3"
                  unelevated
                  color="green-8"
                  label="Confirmar pedido"
                  icon="check"
                  no-caps
                  :disable="!paso3Valido"
                  @click="abrirConfirmar"
                />

<q-btn
                  v-if="paso === 4"
                  unelevated
                  color="green-8"
                  label="Finalizar"
                  icon="check_circle"
                  no-caps
                  :disable="!paso3Valido"
                  @click="finalizar"
                />
              </div>
            </div>
          </q-stepper-navigation>
        </template>
      </q-stepper>

<!-- Modal confirmación -->
      <q-dialog v-model="confirmarVisible" persistent>
        <q-card class="pd-confirm-card">
          <div class="pd-confirm-accent"></div>

          <div class="pd-confirm-header">
            <div class="pd-confirm-header-left">
              <div class="pd-confirm-warn-icon">
                <q-icon name="inventory_2" size="22px" color="#4a8c25" />
              </div>
              <div>
                <div class="pd-confirm-title">Confirmar Pedido</div>
                <div class="pd-confirm-sub">Esta acción confirmará el pedido al proveedor</div>
              </div>
            </div>
            <q-btn flat round dense icon="close" class="pd-confirm-close" @click="confirmarVisible = false" />
          </div>

          <q-card-section class="pd-confirm-body">
            <div class="pd-confirm-user-card">
              <div class="pd-confirm-avatar">
                <q-icon name="storefront" size="20px" color="#2a5c1a" />
              </div>
              <div>
                <div class="pd-confirm-user-name">{{ nombreProveedor }}</div>
                <div class="pd-confirm-user-sub">{{ form.detalles.length }} producto(s) · {{ resumenCantidades }}</div>
              </div>
            </div>

            <div class="pd-confirm-warn-box">
              <q-icon name="info" size="17px" color="#4a8c25" />
              <p>Al confirmar el pedido, quedará registrado con estado <strong>ENVIADO</strong> y <strong>ya no podrá editarse</strong>.</p>
            </div>

            <div class="pd-confirm-row" @click="confirmado = !confirmado">
              <div class="pd-confirm-check" :class="{ checked: confirmado }">
                <q-icon v-if="confirmado" name="check" size="11px" color="white" />
              </div>
              <span class="pd-confirm-txt">Entiendo que el pedido se confirmará</span>
            </div>
          </q-card-section>

          <div class="pd-confirm-footer">
            <button class="pd-confirm-btn-cancel" @click="confirmarVisible = false">
              <q-icon name="close" size="15px" />Cancelar
            </button>
            <button
              class="pd-confirm-btn-action"
              :class="{ 'pd-confirm-btn-disabled': !confirmado || confirmando }"
              :disabled="!confirmado || confirmando"
              @click="confirmarPedido"
            >
              <q-spinner-dots v-if="confirmando" color="white" size="1em" />
              <q-icon v-else name="check" size="16px" />
              {{ confirmando ? 'Confirmando...' : 'Confirmar pedido' }}
            </button>
          </div>
        </q-card>
      </q-dialog>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { crearPedido, actualizarPedido } from '../../api/pedido/pedido'
import { listarProveedores } from '../../api/proveedor/proveedor'
import { listarProductos } from '../../api/productos/productos'

const estadosColor = {
  BORRADOR: 'grey-6',
  ENVIADO: 'blue-6',
  PARCIALMENTE_RECIBIDO: 'orange-6',
  COMPLETADO: 'positive',
  CANCELADO: 'negative'
}

const colorEstado = (estado) => estadosColor[estado] || 'grey-6'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  pedidoEditar: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'guardado'])

const $q = useQuasar()
const stepperRef = ref(null)
const paso = ref(1)
const guardando = ref(false)
const confirmando = ref(false)
const confirmarVisible = ref(false)
const confirmado = ref(false)
const pedidoCreado = ref(null)

const proveedores = ref([])
const productos = ref([])
const proveedorOpciones = ref([])
const productoOpciones = ref([])
const productoOpcionesFiltradas = ref([])

async function cargarCatalogo() {
  try {
    const [prov, prods] = await Promise.all([
      listarProveedores({ page: 0, size: 1000, sortBy: 'idProveedor', sortDir: 'asc', estado: true }),
      listarProductos({ page: 0, size: 1000, sortBy: 'nombre', sortDir: 'asc', tienePresentaciones: true, estado: true })
    ])
    const proveedoresNormalizados = (prov.content || prov || []).map((p) => ({
      id: p.id ?? p.idProveedor,
      nombre: p.nombre
    }))
    const productosNormalizados = (prods.content || prods || []).map((p) => ({
      id: p.idProducto,
      nombre: p.nombre,
      unidades: (p.presentaciones || []).map((pres) => ({
        idUnidadMedida: pres.idUnidadMedida,
        unidad: pres.nombreUnidadMedida,
        factor: pres.factorConversion
      }))
    }))
    proveedores.value = proveedoresNormalizados
    productos.value = productosNormalizados
    proveedorOpciones.value = proveedoresNormalizados.map((p) => ({ label: p.nombre, value: p.id }))
    productoOpciones.value = productosNormalizados.map((p) => ({ label: p.nombre, value: p.id }))
    productoOpcionesFiltradas.value = productosNormalizados.map((p) => ({ label: p.nombre, value: p.id }))
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al cargar proveedores y productos.' })
  }
}

onMounted(cargarCatalogo)

const form = reactive({
  idProveedor: null,
  fechaEntregaEstimada: null,
  observacion: '',
  detalles: []
})

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      paso.value = 1
      pedidoCreado.value = null
      if (props.pedidoEditar) {
        Object.assign(form, {
          idProveedor: props.pedidoEditar.idProveedor,
          fechaEntregaEstimada: props.pedidoEditar.fechaEntregaEstimada || null,
          observacion: props.pedidoEditar.observacion || '',
          detalles: (props.pedidoEditar.detalles || []).map((d) => ({
            idProducto: d.idProducto,
            idUnidadMedida: d.idUnidadMedida,
            factor: d.factor,
            cantidad: d.cantidad
          }))
        })
      } else {
        Object.assign(form, {
          idProveedor: null,
          fechaEntregaEstimada: null,
          observacion: '',
          detalles: []
        })
      }
    }
  }
)

function unidadesDeProducto(detalle) {
  const prod = productos.value.find((p) => p.id === detalle.idProducto)
  if (!prod) return []
  return prod.unidades.map((u) => ({ label: u.unidad, value: u.idUnidadMedida, factor: u.factor }))
}

function nombreUnidad(detalle) {
  const u = unidadesDeProducto(detalle).find((x) => x.value === detalle.idUnidadMedida)
  return u ? u.label : '—'
}

function filtrarProductos(val, update) {
  update(() => {
    const query = (val || '').toLowerCase()
    productoOpcionesFiltradas.value = productos.value
      .filter((p) => !query || p.nombre.toLowerCase().includes(query))
      .map((p) => ({ label: p.nombre, value: p.id }))
  })
}

function bloquearNegativos(event) {
  if (event.key === '-' || event.key === 'e' || event.key === '+' || event.key === '.') {
    event.preventDefault()
  }
}

function cambiarProducto(detalle, index) {
  detalle.idUnidadMedida = null
  detalle.factor = 0
  const prod = productos.value.find((p) => p.id === detalle.idProducto)
  if (prod && prod.unidades.length === 1) {
    detalle.idUnidadMedida = prod.unidades[0].idUnidadMedida
    detalle.factor = prod.unidades[0].factor
  }
  validarDuplicados(index)
}

function cambiarUnidad(detalle, index) {
  const unidades = unidadesDeProducto(detalle)
  const u = unidades.find((x) => x.value === detalle.idUnidadMedida)
  detalle.factor = u ? u.factor : 0
  validarDuplicados(index)
}

function validarDuplicados(ignoreIndex) {
  const vistos = new Set()
  form.detalles.forEach((d, i) => {
    if (i === ignoreIndex) return
    if (!d.idProducto || !d.idUnidadMedida) return
    const clave = `${d.idProducto}-${d.idUnidadMedida}`
    vistos.add(clave)
  })
  const actual = form.detalles[ignoreIndex]
  if (!actual.idProducto || !actual.idUnidadMedida) return
  if (vistos.has(`${actual.idProducto}-${actual.idUnidadMedida}`)) {
    $q.notify({
      type: 'warning',
      message: 'Este producto con esa unidad ya fue agregado al pedido.',
      position: 'top'
    })
  }
}

function agregarProducto() {
  form.detalles.push({
    idProducto: null,
    idUnidadMedida: null,
    factor: 0,
    cantidad: 1
  })
}

function eliminarProducto(index) {
  form.detalles.splice(index, 1)
}

function equivalencia(detalle) {
  if (!detalle.cantidad || !detalle.factor) return '—'
  return `${detalle.cantidad} ${nombreUnidad(detalle)} = ${detalle.cantidad * detalle.factor} ${unidadBase(detalle)}`
}

function unidadBase(detalle) {
  const prod = productos.value.find((p) => p.id === detalle.idProducto)
  if (!prod) return ''
  return prod.unidades.find((u) => u.factor === 1)?.unidad || 'UN'
}

const tablaResumenColumnas = [
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left' },
  { name: 'unidad', label: 'Unidad', field: 'unidad', align: 'left' },
  { name: 'cantidad', label: 'Cantidad', field: 'cantidad', align: 'center' },
  { name: 'factor', label: 'Factor', field: 'factor', align: 'center' },
  { name: 'equivalencia', label: 'Equivalencia', field: 'equivalencia', align: 'left' }
]

const tablaResumenRows = computed(() => {
  return form.detalles.map((d, i) => ({
    index: i,
    producto: productoOpciones.value.find((p) => p.value === d.idProducto)?.label || '—',
    unidad: nombreUnidad(d) || '—',
    cantidad: d.cantidad || 0,
    factor: d.factor || 0,
    equivalencia: equivalencia(d)
  }))
})

const nombreProveedor = computed(() => {
  const p = proveedores.value.find((x) => x.id === form.idProveedor)
  return p ? p.nombre : '—'
})

const resumenCantidades = computed(() => {
  if (!form.detalles.length) return '—'
  return form.detalles.map((d) => `${d.cantidad} ${nombreUnidad(d)}`).join(', ')
})

const paso1Valido = computed(() => {
  return !!form.idProveedor
})

const paso2Valido = computed(() => {
  if (!form.detalles.length) return false
  for (const d of form.detalles) {
    if (!d.idProducto || !d.idUnidadMedida || !d.cantidad || d.cantidad <= 0) return false
  }
  return !tieneDuplicados()
})

const paso3Valido = computed(() => paso1Valido.value && paso2Valido.value)

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

function validarPaso1() {
  if (!form.idProveedor) {
    $q.notify({ type: 'warning', message: 'Seleccione un proveedor.', position: 'top' })
    return
  }
  stepperRef.value.next()
}

function validarPaso2() {
  if (!form.detalles.length) {
    $q.notify({ type: 'warning', message: 'Agregue al menos un producto al pedido.', position: 'top' })
    return false
  }
  for (const d of form.detalles) {
    if (!d.idProducto || !d.idUnidadMedida) {
      $q.notify({ type: 'warning', message: 'Complete producto y unidad en cada línea.', position: 'top' })
      return false
    }
    if (!d.cantidad || d.cantidad <= 0) {
      $q.notify({ type: 'warning', message: 'La cantidad debe ser mayor a 0.', position: 'top' })
      return false
    }
  }
  if (tieneDuplicados()) {
    $q.notify({ type: 'warning', message: 'Hay productos repetidos con la misma unidad.', position: 'top' })
    return false
  }
  return true
}

function guardarYContinuarPaso2() {
  if (!validarPaso2()) return
  stepperRef.value.next()
}

function tieneDuplicados() {
  const vistos = new Set()
  for (const d of form.detalles) {
    if (!d.idProducto || !d.idUnidadMedida) continue
    const clave = `${d.idProducto}-${d.idUnidadMedida}`
    if (vistos.has(clave)) return true
    vistos.add(clave)
  }
  return false
}

function prepararPayload(estado) {
  return {
    idProveedor: form.idProveedor,
    fechaEntregaEstimada: form.fechaEntregaEstimada || null,
    observacion: form.observacion,
    estado,
    detalles: form.detalles.map((d) => ({
      idProducto: d.idProducto,
      idUnidadMedida: d.idUnidadMedida,
      cantidadSolicitada: d.cantidad
    }))
  }
}

async function guardarBorrador() {
  if (!validarPaso2()) return
  guardando.value = true
  try {
    const creado = props.pedidoEditar
      ? await actualizarPedido(props.pedidoEditar.idPedido, prepararPayload('BORRADOR'))
      : await crearPedido(prepararPayload('BORRADOR'))
    pedidoCreado.value = creado
    $q.notify({ type: 'success', message: props.pedidoEditar ? 'Cambios guardados correctamente.' : 'Pedido guardado como borrador correctamente.' })
    emit('guardado', creado)
    cerrar()
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al guardar el pedido.' })
  } finally {
    guardando.value = false
  }
}

function abrirConfirmar() {
  if (form.detalles.length === 0) return
  confirmado.value = false
  confirmarVisible.value = true
}

async function confirmarPedido() {
  if (!confirmado.value) return
  confirmando.value = true
  try {
    const creado = props.pedidoEditar
      ? await actualizarPedido(props.pedidoEditar.idPedido, prepararPayload('ENVIADO'))
      : await crearPedido(prepararPayload('ENVIADO'))
    pedidoCreado.value = creado
    confirmarVisible.value = false
    paso.value = 4
    $q.notify({ type: 'success', message: 'Pedido confirmado correctamente.' })
  } catch (e) {
    $q.notify({ type: 'negative', message: e.response?.data?.message || 'Error al confirmar el pedido.' })
  } finally {
    confirmando.value = false
  }
}

function finalizar() {
  emit('guardado', pedidoCreado.value)
  cerrar()
}

function cerrar() {
  visible.value = false
  paso.value = 1
}
</script>

<style scoped src="../../assets/styles/pedido/pedido.css"></style>

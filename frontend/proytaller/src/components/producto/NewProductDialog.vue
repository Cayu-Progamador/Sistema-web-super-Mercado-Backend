<template>
  <q-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)"
    transition-show="slide-up" transition-hide="slide-down" class="new-product-dialog" persistent>
    <q-card class="np-card">
      <div class="accent-bar"></div>

      <q-card-section class="dialog-header q-px-lg q-py-md">
        <div class="row items-center justify-between">
          <div class="row items-center q-gutter-sm">
            <div class="modal-icon">
              <q-icon name="inventory_2" size="22px" color="#2E7D32" />
            </div>
            <div>
              <div class="modal-title">{{ esEdicion ? 'Editar Producto' : 'Nuevo Producto' }}</div>
              <div class="modal-eyebrow">Catálogo maestro · Gestión de productos</div>
            </div>
          </div>
          <q-btn icon="close" flat round dense class="close-btn" v-close-popup />
        </div>

        <q-tabs v-model="tab" dense active-color="green-8" indicator-color="green-8" align="left" narrow-indicator
          class="q-mt-md">
          <q-tab name="general" icon="info" label="Información General" />
          <q-tab name="config" icon="tune" label="Configuración" />
          <q-tab name="presentaciones" icon="layers" label="Presentaciones" />
        </q-tabs>
      </q-card-section>

      <q-separator />

      <q-card-section class="np-body q-pa-lg">
        <q-tab-panels v-model="tab" animated class="tab-panels">
          <!-- PESTAÑA 1: INFORMACIÓN GENERAL -->
          <q-tab-panel name="general">
            <div class="section-title">Información general del producto</div>
            <div class="row q-col-gutter-md">
              <div class="col-12">
                <q-input v-model="form.nombre" label="Nombre *" stack-label outlined color="green-8"
                  placeholder="Ej. Coca-Cola 500 ml..." class="q-mb-md field-green" :error="!!errores.nombre"
                  :error-message="errores.nombre" />
              </div>
              <div class="col-12">
                <q-input v-model="form.descripcion" label="Descripción" stack-label outlined color="green-8"
                  type="textarea" rows="2" placeholder="Descripción breve del producto" class="q-mb-md field-green" />
              </div>
              <div class="col-12 col-md-6">
                <div class="np-field-with-btn">
                  <q-select v-model="form.categoria" :options="categoriasFiltradas" label="Categoría *" stack-label
                    outlined color="green-8" emit-value map-options use-input input-debounce="200"
                    class="np-field-el field-green" :loading="creandoCatalogo === 'categoria'"
                    @filter="filtrarCategorias" @update:model-value="categoriaCambio" />
                  <q-btn flat round dense class="np-add-btn" icon="edit" aria-label="Crear categoría"
                    @click="abrirCrear('categoria')">
                    <q-tooltip>Crear categoría nueva</q-tooltip>
                  </q-btn>
                </div>
              </div>
              <div class="col-12 col-md-6">
                <div class="np-field-with-btn">
                  <q-select v-model="form.subcategoria" :options="subcategoriasFiltradas" label="Subcategoría *"
                    stack-label outlined color="green-8" emit-value map-options use-input input-debounce="200"
                    :disable="!form.categoria" class="np-field-el field-green" :error="!!errores.subcategoria"
                    :error-message="errores.subcategoria"
                    :loading="cargandoSubcategorias || creandoCatalogo === 'subcategoria'"
                    @filter="filtrarSubcategorias" />
                  <q-btn flat round dense class="np-add-btn" icon="edit" aria-label="Crear subcategoría"
                    @click="abrirCrear('subcategoria')">
                    <q-tooltip>Crear subcategoría nueva</q-tooltip>
                  </q-btn>
                </div>
              </div>
              <div class="col-12 col-md-6 row items-end q-mb-md">
                <q-toggle v-model="form.tieneMarca" color="green-8" />
                <div class="q-ml-sm" style="font-weight: 700; color: #2E7D32;">¿El producto tiene marca?</div>
              </div>
              <div v-if="form.tieneMarca" class="col-12 col-md-6">
                <div class="np-field-with-btn">
                  <q-select v-model="form.marca" :options="marcasFiltradas" label="Marca" stack-label outlined
                    color="green-8" emit-value map-options clearable use-input input-debounce="200"
                    class="np-field-el field-green" :loading="creandoCatalogo === 'marca'"
                    @filter="filtrarMarcas" />
                  <q-btn flat round dense class="np-add-btn" icon="edit" aria-label="Crear marca"
                    @click="abrirCrear('marca')">
                    <q-tooltip>Crear marca nueva</q-tooltip>
                  </q-btn>
                </div>
              </div>
              <div class="col-12 col-md-4">
                <div class="np-img-zone" :class="{ 'drag-over': isDraggingImagen }" @click="triggerImagenInput"
                  @dragover.prevent="isDraggingImagen = true" @dragleave="isDraggingImagen = false"
                  @drop.prevent="onImagenDrop">
                  <template v-if="!form.imagen">
                    <q-icon name="cloud_upload" size="32px" class="np-img-icon" />
                    <div class="np-img-title">Arrastra tu imagen aquí</div>
                    <div class="np-img-or">o</div>
                    <button class="np-img-btn" @click.stop="triggerImagenInput">Seleccionar archivo</button>
                  </template>
                  <template v-else>
                    <q-img :src="previewImagen" fit="cover" style="height: 120px; width: 100%; border-radius: 8px;" />
                    <div class="np-img-file">{{ typeof form.imagen === 'object' ? form.imagen.name : 'Imagen actual' }}</div>
                    <button class="np-img-remove" @click.stop="quitarImagen">Quitar</button>
                  </template>
                </div>
                <input ref="imagenInput" type="file" accept=".png,.jpg,.jpeg,.webp" style="display: none"
                  @change="onImagenChange" />
                <div v-if="errorImagen" class="np-img-error">
                  <q-icon name="error_outline" size="14px" />{{ errorImagen }}
                </div>
              </div>
            </div>
          </q-tab-panel>

          <!-- PESTAÑA 2: CONFIGURACIÓN -->
          <q-tab-panel name="config">
            <div class="row q-col-gutter-md">
              <div class="col-6 col-md-3">
                <q-toggle v-model="form.controlaLote" color="green-8" label="Controla lote" />
              </div>
              <div class="col-6 col-md-3">
                <q-toggle v-model="form.controlaVencimiento" color="green-8" label="Controla vencimiento" />
              </div>
            </div>
          </q-tab-panel>

          <!-- PESTAÑA 3: PRESENTACIONES -->
          <q-tab-panel name="presentaciones">
            <div class="section-title">Presentaciones de venta</div>

            <div class="row q-col-gutter-md q-mb-md">
              <div class="col-12 col-md-8">
                <div class="config-field-label">
                  ¿Cuál es la unidad base?
                  <q-icon name="help_outline" size="16px" class="config-field-help">
                    <q-tooltip>Es la unidad mínima y única con la que se mide el producto (ej. Kilogramo, Litro,
                      Unidad)</q-tooltip>
                  </q-icon>
                </div>
                <q-select v-model="form.unidadBase" :options="unidades" label="Unidad base *" stack-label outlined
                  color="green-8" emit-value map-options class="q-mb-md field-green" :error="!!errores.unidadBase"
                  :error-message="errores.unidadBase" @update:model-value="unidadBaseCambio" />
              </div>
            </div>

            <div class="row items-center q-gutter-md q-mb-md">
              <q-toggle v-model="form.tienePresentaciones" color="green-8" />
              <span class="text-weight-bold" style="color: #1B5E20;">Este producto tiene múltiples presentaciones</span>
            </div>

            <!-- Estado vacío: solo presentaación por unidad base -->
            <div v-if="!form.tienePresentaciones" class="pres-empty-state">
              <q-icon name="check_circle_outline" size="40px" color="green-6" />
              <div class="pres-empty-title">Se vende solo por su unidad base</div>
              <div class="pres-empty-sub">
                El sistema creará automáticamente una única presentación igual a la unidad base ({{ labelUnidadBase()
                }}).
                Dejará de gestionar presentaciones multiples.
              </div>

              <div class="pres-price-box">
                <div class="pres-price-label">Precio de venta por {{ labelUnidadBase() }}</div>
                <q-input
                  v-model="form.precioUnidadBase"
                  type="number"
                  prefix="Bs "
                  outlined
                  color="green-8"
                  stack-label
                  class="field-green"
                  placeholder="0.00"
                  min="0.01"
                  step="0.01"
                  @keydown="bloquearNoNumerico"
                  @paste="sanearPegadoPrecio"
                  @update:model-value="sanearPrecioUnidadBase"
                  :error="!!errores.precioUnidadBase"
                  :error-message="errores.precioUnidadBase"
                />
              </div>

              <div class="pres-price-box">
                <div class="row items-center q-gutter-sm">
                  <q-toggle v-model="form.usaCodigoBase" color="green-8" dense />
                  <div class="pres-price-label" style="margin-bottom: 0;">Este producto tiene código de barras</div>
                </div>
                <q-input
                  v-if="form.usaCodigoBase"
                  v-model="form.codigoBase"
                  outlined
                  color="green-8"
                  stack-label
                  class="field-green q-mt-sm"
                  label="Código de barras"
                  placeholder="Escanea o escribe el código"
                  :error="!!errores.codigoBase"
                  :error-message="errores.codigoBase"
                />
              </div>
            </div>

            <!-- Tabla editable de presentaciones -->
            <presentations-editable-table v-else :rows="form.presentaciones" :unidades="unidadesDelTipo"
              :errores="errores.presentaciones" :principal-id="principalId" @agregar="agregarPresentacion"
              @eliminar="eliminarPresentacion" @cambio="revisarPresentaciones" @principal-cambio="setPrincipal" />
          </q-tab-panel>
        </q-tab-panels>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right" class="q-pa-md dialog-actions">
        <q-btn flat label="Cancelar" color="green-8" v-close-popup no-caps />
        <q-btn unelevated color="green-8" :label="esEdicion ? 'Guardar cambios' : 'Guardar producto'" no-caps
          :disable="!formValido" @click="guardar" />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <!-- Crear categoría / subcategoría / marca reutilizando formularios de los módulos -->
  <NewCategoryDialog v-model="dialogCategoria" :categorias="categoriasObjetos" @guardar="guardarNuevaCategoria" />
  <NewSubcategoryDialog v-model="dialogSubcategoria" :categorias="categoriasObjetos"
    :padre-preseleccionado="form.categoria" @guardar="guardarNuevaSubcategoria" />
  <NewMarcaDialog v-model="dialogMarca" :marcas="marcasObjetos" @guardar="guardarNuevaMarca" />
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useQuasar } from 'quasar'
import PresentationsEditableTable from './PresentationsEditableTable.vue'
import NewCategoryDialog from '../categoria/NewCategoryDialog.vue'
import NewSubcategoryDialog from '../categoria/NewSubcategoryDialog.vue'
import NewMarcaDialog from '../marca/NewMarcaDialog.vue'
import { crearCategoria } from '../../api/categoria/categoria'
import { crearSubcategoria } from '../../api/subcategoria/subcategoria'
import { crearMarca } from '../../api/marca/marca'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  productoEditar: { type: Object, default: null },
  categorias: { type: Array, default: () => [] },
  subcategorias: { type: Array, default: () => [] },
  marcas: { type: Array, default: () => [] },
  unidades: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:modelValue', 'guardar', 'categoria-cambio', 'categoria-creada', 'subcategoria-creada', 'marca-creada'])
const $q = useQuasar()

const esEdicion = computed(() => !!props.productoEditar)
const tab = ref('general')
let seq = 0

const form = reactive({
  nombre: '',
  descripcion: '',
  categoria: null,
  subcategoria: null,
  tieneMarca: true,
  marca: null,
  imagen: null,
  unidadBase: null,
  controlaLote: false,
  controlaVencimiento: false,
  tienePresentaciones: false,
  presentaciones: [],
  usaCodigoBase: false,
  codigoBase: ''
})

const errores = reactive({
  nombre: '',
  subcategoria: '',
  unidadBase: '',
  precioUnidadBase: '',
  codigoBase: '',
  presentaciones: {}
})

const imagenInput = ref(null)
const isDraggingImagen = ref(false)
const previewImagen = ref('')
const errorImagen = ref('')
const cargandoSubcategorias = ref(false)

const FORMATOS_IMAGEN = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp']
const MAX_IMG_MB = 2

function triggerImagenInput() {
  imagenInput.value?.click()
}

function quitarImagen() {
  form.imagen = null
  previewImagen.value = ''
  errorImagen.value = ''
  if (imagenInput.value) imagenInput.value.value = ''
}

function onImagenDrop(e) {
  isDraggingImagen.value = false
  const file = e.dataTransfer.files?.[0]
  if (file) procesarImagen(file)
}

function onImagenChange(e) {
  const file = e.target.files?.[0]
  if (file) procesarImagen(file)
}

function procesarImagen(file) {
  errorImagen.value = ''
  if (!FORMATOS_IMAGEN.includes(file.type)) {
    errorImagen.value = 'Formato no permitido. Usa JPG, JPEG, PNG o WEBP.'
    return
  }
  if (file.size > MAX_IMG_MB * 1024 * 1024) {
    errorImagen.value = `El archivo supera el tamaño máximo de ${MAX_IMG_MB}MB.`
    return
  }
  const reader = new FileReader()
  reader.onload = (e) => {
    previewImagen.value = e.target.result
    form.imagen = file
  }
  reader.readAsDataURL(file)
}

const principalId = ref(null)

const erroresPresentaciones = computed(() => errores.presentaciones)

function labelUnidadBase() {
  const u = props.unidades.find((x) => x.value === form.unidadBase)
  return u ? u.label : 'la unidad seleccionada'
}

const unidadesDelTipo = computed(() => {
  if (!form.unidadBase) return props.unidades
  const tipo = props.unidades.find((u) => u.value === form.unidadBase)?.tipo
  if (!tipo) return props.unidades
  return props.unidades.filter((u) => u.tipo === tipo)
})

function categoriaCambio(val) {
  form.subcategoria = null
  errores.subcategoria = ''
  emit('categoria-cambio', val)
}

const dialogCategoria = ref(false)
const dialogSubcategoria = ref(false)
const dialogMarca = ref(false)
const creandoCatalogo = ref('')

const categoriasObjetos = computed(() =>
  props.categorias.map((c) => ({ id: c.value, nombre: c.label }))
)

const marcasObjetos = computed(() =>
  props.marcas.map((m) => ({ id: m.value, nombre: m.label }))
)

function abrirCrear(tipo) {
  if (tipo === 'categoria') {
    dialogCategoria.value = true
  } else if (tipo === 'subcategoria') {
    if (!form.categoria) {
      $q.notify({ type: 'warning', message: 'Primero selecciona o crea una categoría.' })
      return
    }
    dialogSubcategoria.value = true
  } else {
    dialogMarca.value = true
  }
}

function guardarNuevaCategoria(payload) {
  crearCategoria({ nombre: payload.nombre, descripcion: payload.descripcion || '' })
    .then((res) => {
      const id = res.idCategoria
      form.categoria = id
      emit('categoria-creada', { id, nombre: payload.nombre })
      categoriaCambio(id)
      dialogCategoria.value = false
      $q.notify({ type: 'positive', message: 'Categoría creada.' })
    })
    .catch((e) => {
      $q.notify({ type: 'negative', message: e.response?.data?.message || 'No se pudo crear la categoría.' })
    })
}

function guardarNuevaSubcategoria(payload) {
  crearSubcategoria({ nombre: payload.nombre, descripcion: payload.descripcion || '', idCategoria: payload.categoriaId })
    .then((res) => {
      const id = res.idSubcategoria
      form.subcategoria = id
      errores.subcategoria = ''
      emit('subcategoria-creada', { id, nombre: payload.nombre })
      dialogSubcategoria.value = false
      $q.notify({ type: 'positive', message: 'Subcategoría creada.' })
    })
    .catch((e) => {
      $q.notify({ type: 'negative', message: e.response?.data?.message || 'No se pudo crear la subcategoría.' })
    })
}

function guardarNuevaMarca(payload) {
  crearMarca({ nombre: payload.nombre, descripcion: payload.descripcion || '' })
    .then((res) => {
      const id = res.idMarca
      form.marca = id
      emit('marca-creada', { id, nombre: payload.nombre })
      dialogMarca.value = false
      $q.notify({ type: 'positive', message: 'Marca creada.' })
    })
    .catch((e) => {
      $q.notify({ type: 'negative', message: e.response?.data?.message || 'No se pudo crear la marca.' })
    })
}

const categoriasFiltradas = ref([])
const subcategoriasFiltradas = ref([])
const marcasFiltradas = ref([])

watch(
  () => props.categorias,
  (val) => { categoriasFiltradas.value = [...val] },
  { immediate: true }
)

watch(
  () => props.subcategorias,
  (val) => { subcategoriasFiltradas.value = [...val] },
  { immediate: true }
)

watch(
  () => props.marcas,
  (val) => { marcasFiltradas.value = [...val] },
  { immediate: true }
)

function filtrarLista(texto, lista) {
  const t = (texto || '').trim().toLowerCase()
  if (!t) return [...lista]
  return lista.filter((o) => String(o.label || '').toLowerCase().includes(t))
}

function filtrarCategorias(val, done) {
  categoriasFiltradas.value = filtrarLista(val, props.categorias)
  done()
}

function filtrarSubcategorias(val, done) {
  subcategoriasFiltradas.value = filtrarLista(val, props.subcategorias)
  done()
}

function filtrarMarcas(val, done) {
  marcasFiltradas.value = filtrarLista(val, props.marcas)
  done()
}

function unidadBaseCambio() {
  form.presentaciones = []
  principalId.value = null
  revisarPresentaciones()
}

function agregarPresentacion() {
  seq += 1
  const tempId = `p-${seq}`
  form.presentaciones.push({
    tempId,
    nombre: '',
    unidad: form.unidadBase,
    factor: 1,
    precio: null,
    usaCodigo: false,
    codigo: '',
    precioError: ''
  })
  if (!principalId.value) principalId.value = tempId
  revisarPresentaciones()
}

function eliminarPresentacion(tempId) {
  form.presentaciones = form.presentaciones.filter((p) => p.tempId !== tempId)
  if (principalId.value === tempId) {
    principalId.value = form.presentaciones.length ? form.presentaciones[0].tempId : null
  }
  revisarPresentaciones()
}

function setPrincipal(tempId) {
  principalId.value = tempId
}

function revisarPresentaciones() {
  if (!form.tienePresentaciones) return
  const errs = {}
  form.presentaciones.forEach((p) => {
    const e = {}
    if (!p.nombre) e.nombre = true
    e.precio = ''
    if (!p.precio || Number(p.precio) <= 0) e.precio = 'Precio obligatorio'
    p.precioError = e.precio
    e.codigo = ''
    if (p.usaCodigo && !p.codigo?.trim()) e.codigo = 'Indica el código de barras'
    errs[p.tempId] = e
  })
  errores.presentaciones = errs
}

const formValido = computed(() => {
  if (errores.nombre || errores.subcategoria || errores.unidadBase || errores.precioUnidadBase) return false
  if (!form.nombre || !form.subcategoria || !form.unidadBase) return false
  if (form.usaCodigoBase && !form.codigoBase?.trim()) return false
  if (!form.tienePresentaciones) return Number(form.precioUnidadBase) > 0
  if (!form.presentaciones.length) return false
  return form.presentaciones.every(
    (p) => p.nombre && Number(p.precio) > 0 && !p.factorError && (!p.usaCodigo || p.codigo?.trim())
  )
})

function resetForm() {
  form.nombre = ''
  form.descripcion = ''
  form.categoria = null
  form.subcategoria = null
  form.tieneMarca = true
  form.marca = null
  form.imagen = null
  previewImagen.value = ''
  errorImagen.value = ''
  isDraggingImagen.value = false
  if (imagenInput.value) imagenInput.value.value = ''
  form.unidadBase = null
  form.controlaLote = false
  form.controlaVencimiento = false
  form.tienePresentaciones = false
  form.precioUnidadBase = null
  form.presentaciones = []
  form.usaCodigoBase = false
  form.codigoBase = ''
  principalId.value = null
  errores.nombre = ''
  errores.subcategoria = ''
  errores.unidadBase = ''
  errores.precioUnidadBase = ''
  errores.codigoBase = ''
  errores.presentaciones = {}
  tab.value = 'general'
}

function precargar(producto) {
  form.nombre = producto.nombre || ''
  form.descripcion = producto.descripcion || ''
  form.categoria = producto.idCategoria ?? null
  form.subcategoria = producto.idSubcategoria ?? null
  form.tieneMarca = !!producto.idMarca
  form.marca = producto.idMarca ?? null
  form.unidadBase = producto.idUnidadBase ?? null
  form.imagen = producto.imagen || null
  previewImagen.value = producto.imagen ? urlImagen(producto.imagen) : ''
  form.presentaciones = []
  form.usaCodigoBase = false
  form.codigoBase = ''
  form.precioUnidadBase = null
  errores.presentaciones = {}
  errores.precioUnidadBase = ''
  errores.codigoBase = ''
  principalId.value = null

  const presentaciones = producto.presentaciones || []
  form.tienePresentaciones = presentaciones.length > 1
  form.presentaciones = presentaciones.map((p, i) => {
    const cod = (p.codigosBarra || [])[0]
    return {
      tempId: `e-${p.idPresentacion ?? i}`,
      nombre: p.nombrePresentacion || '',
      unidad: p.idUnidadMedida ?? form.unidadBase,
      factor: p.factorConversion != null ? Number(p.factorConversion) : 1,
      precio: p.precioVenta != null ? Number(p.precioVenta) : null,
      usaCodigo: !!(cod && cod.codigo),
      codigo: cod ? cod.codigo : '',
      precioError: '',
      factorError: ''
    }
  })
  const principal = presentaciones.find((p) => p.esPrincipal)
  if (principal) {
    const idx = presentaciones.indexOf(principal)
    if (form.presentaciones[idx]) principalId.value = form.presentaciones[idx].tempId
  }

  if (!form.tienePresentaciones) {
    const p = presentaciones[0]
    form.precioUnidadBase = p && p.precioVenta != null ? Number(p.precioVenta) : null
    const cod = p ? (p.codigosBarra || [])[0] : null
    form.usaCodigoBase = !!(cod && cod.codigo)
    form.codigoBase = form.usaCodigoBase ? cod.codigo : ''
  }
  tab.value = 'general'
}

function urlImagen(ruta) {
  if (!ruta) return ''
  if (/^https?:\/\//i.test(ruta)) return ruta
  if (ruta.startsWith('/')) return import.meta.env.VITE_API_URL + ruta
  return import.meta.env.VITE_API_URL + '/uploads/' + ruta
}

function guardar() {
  if (!formValido.value) return
  const payload = {
    ...(esEdicion.value ? { id: props.productoEditar.id } : {}),
    nombre: form.nombre,
    descripcion: form.descripcion,
    categoria: form.categoria,
    subcategoria: form.subcategoria,
    tieneMarca: form.tieneMarca,
    marca: form.tieneMarca ? (form.marca || null) : null,
    imagen: form.imagen,
    unidadBase: form.unidadBase,
    controlaLote: form.controlaLote,
    controlaVencimiento: form.controlaVencimiento,
    presenta: form.tienePresentaciones,
    precioUnidadBase: Number(form.precioUnidadBase) || null,
    presentaciones: form.tienePresentaciones
      ? form.presentaciones.map((p) => ({
        nombre: p.nombre,
        unidad: p.unidad,
        factor: p.factor,
        precio: Number(p.precio),
        usaCodigo: p.usaCodigo,
        codigo: p.usaCodigo ? p.codigo : null,
        principal: p.tempId === principalId.value
      }))
      : [{
        nombre: labelUnidadBase(),
        unidad: form.unidadBase,
        factor: 1,
        precio: Number(form.precioUnidadBase),
        principal: true,
        usaCodigo: form.usaCodigoBase,
        codigo: form.usaCodigoBase ? form.codigoBase : null
      }]
  }
  emit('guardar', payload)
  resetForm()
}

watch(
  () => props.modelValue,
  (val) => {
    if (!val) return
    if (esEdicion.value) {
      precargar(props.productoEditar)
    } else {
      resetForm()
    }
  }
)

watch(
  () => form.precioUnidadBase,
  (val) => {
    if (form.tienePresentaciones) return
    errores.precioUnidadBase = !val || Number(val) <= 0 ? 'El precio debe ser mayor que 0.' : ''
  }
)

function sanearPrecioUnidadBase(val) {
  const n = Number(val)
  if (val === '' || val === null || !isFinite(n) || n <= 0) {
    form.precioUnidadBase = ''
    errores.precioUnidadBase = 'El precio debe ser mayor que 0.'
    return
  }
  form.precioUnidadBase = Math.round(n * 100) / 100
  errores.precioUnidadBase = ''
}

function bloquearNoNumerico(e) {
  const permitidas = ['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'Home', 'End']
  if (permitidas.includes(e.key)) return
  if (e.key === '.') {
    if (e.target.value.includes('.')) e.preventDefault()
    return
  }
  if (!/^\d$/.test(e.key)) e.preventDefault()
}

function sanearPegadoPrecio(e) {
  const texto = (e.clipboardData || window.clipboardData).getData('text')
  if (!texto) return
  const limpio = texto.replace(/[^\d.]/g, '')
  if (limpio !== texto) {
    e.preventDefault()
  }
}

watch(
  () => form.codigoBase,
  (val) => {
    if (form.tienePresentaciones || !form.usaCodigoBase) return
    errores.codigoBase = !val?.trim() ? 'Indica el código de barras.' : ''
  }
)

watch(
  () => form.usaCodigoBase,
  (val) => {
    if (!val) errores.codigoBase = ''
  }
)

watch(
  () => form.tienePresentaciones,
  (val) => {
    if (val) errores.precioUnidadBase = ''
  }
)
</script>

<style scoped>
.np-card {
  width: 50vw;
  max-width: 50vw;
  max-height: 92vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-radius: 14px;
}

.accent-bar {
  height: 4px;
  background: linear-gradient(90deg, #2E7D32, #66BB6A, #A5D6A7);
}

.dialog-header {
  background: #E8F5E9 !important;
}

.modal-icon {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  background: #ffffff;
  border: 1.5px solid #A5D6A7;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-title {
  font-family: 'Nunito', sans-serif;
  font-size: 18px;
  font-weight: 900;
  color: #1B5E20;
}

.modal-eyebrow {
  font-size: 10px;
  font-weight: 700;
  color: #7aaa4e;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  font-family: 'Nunito', sans-serif;
}

.close-btn {
  color: #9dbf78 !important;
  background: #f0f7e8 !important;
  border-radius: 8px !important;
}

.close-btn:hover {
  background: #ddecc5 !important;
  color: #4a8c25 !important;
}

.section-title {
  font-family: 'Nunito', sans-serif;
  font-size: 14px;
  font-weight: 800;
  color: #2E7D32;
  margin-bottom: 14px;
}

.dialog-actions {
  border-top: 1px solid #E0E0E0;
  background: #FAFBF7;
}

.dialog-actions .q-btn:first-child {
  color: #2E7D32;
  font-weight: 700;
  border: 1px solid #A5D6A7;
  border-radius: 8px;
  min-height: 40px;
}

.dialog-actions .q-btn:first-child:hover {
  background: #F1F8E9;
}

.dialog-actions .q-btn:last-child {
  background: #2E7D32;
  color: #ffffff;
  font-weight: 700;
  border-radius: 8px;
  min-height: 40px;
  box-shadow: 0 2px 6px rgba(46, 125, 50, 0.3);
}

.dialog-actions .q-btn:last-child:hover {
  background: #276B2B;
}

.field-green :deep(.q-field__control) {
  background: #ffffff;
  border-radius: 8px;
  border: 1.5px solid #A5D6A7;
}

.field-green :deep(.q-field__control::before) {
  border: none;
}

.field-green :deep(.q-field__control::after) {
  border: none;
}

.field-green :deep(.q-field__control:hover) {
  border-color: #81C784;
}

.field-green :deep(.q-field--focused .q-field__control) {
  border-color: #2E7D32;
}

.field-green :deep(.q-field__native) {
  color: #2E7D32;
}

.field-green :deep(.q-field__label) {
  color: #388E3C;
}

.field-green :deep(.q-field__marginal) {
  color: #2E7D32;
}

.field-green :deep(.q-field__control.q-field--error) {
  border-color: #C62828;
}

.np-body {
  overflow-y: auto;
}

.np-field-with-btn {
  display: flex;
  align-items: flex-start;
  gap: 6px;
}

.np-field-el {
  flex: 1;
}

.np-field-with-btn .q-field--float .q-field__label,
.np-field-with-btn .q-field__label {
  overflow: visible;
}

.np-add-btn {
  margin-top: 4px;
  color: #2E7D32;
  background: #e8f5e9;
  border: 1px solid #c8e6c9;
}

.np-add-btn:hover {
  background: #c8e6c9;
}

.q-toggle, .q-checkbox, .q-radio {
  color: #2E7D32 !important;
}

.q-toggle :deep(.q-toggle__track), .q-checkbox :deep(.q-checkbox__bg), .q-radio :deep(.q-radio__bg) {
  color: #2E7D32 !important;
}

@media (max-width: 1024px) {
  .np-card {
    width: 90vw;
    max-width: 90vw;
  }
}

.config-field-label {
  font-size: 13px;
  font-weight: 700;
  color: #2E7D32;
  margin-bottom: 6px;
  cursor: help;
}

.config-field-help {
  vertical-align: middle;
  margin-left: 4px;
}

.pres-price-box {
  margin-top: 18px;
  padding: 14px;
  background: #F7FDF2;
  border: 1px solid #A5D6A7;
  border-radius: 10px;
  text-align: left;
}

.pres-price-label {
  font-size: 13px;
  font-weight: 700;
  color: #1B5E20;
  margin-bottom: 8px;
}

.np-img-zone {
  border: 2px dashed #A5D6A7;
  border-radius: 12px;
  background: #F7FDF2;
  padding: 18px 14px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.np-img-zone:hover {
  border-color: #81C784;
  background: #F1F8E9;
}

.np-img-zone.drag-over {
  border-color: #2E7D32;
  background: #E8F5E9;
}

.np-img-icon {
  color: #7aaa4e !important;
  margin-bottom: 6px;
}

.np-img-title {
  font-size: 13px;
  font-weight: 700;
  color: #1B5E20;
  margin-bottom: 4px;
}

.np-img-or {
  font-size: 12px;
  font-weight: 600;
  color: #9dbf78;
  margin-bottom: 8px;
}

.np-img-btn {
  background: #2E7D32;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}

.np-img-btn:hover {
  background: #276B2B;
}

.np-img-file {
  font-size: 12px;
  color: #2E7D32;
  font-weight: 600;
  margin-top: 6px;
  word-break: break-all;
}

.np-img-remove {
  margin-top: 8px;
  background: #ffffff;
  color: #C62828;
  border: 1px solid #EF9A9A;
  border-radius: 8px;
  padding: 5px 14px;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}

.np-img-remove:hover {
  background: #FFEBEE;
}

.np-img-error {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #C62828;
  font-size: 12px;
  font-weight: 600;
  margin-top: 6px;
}
</style>
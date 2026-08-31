<template>
  <q-dialog
    :model-value="visible"
    @update:model-value="val => { visible = val }"
    maximized
    transition-show="slide-up"
    transition-hide="slide-down"
    class="dv-dialog-wrap"
  >
    <q-card class="dv-dialog-card column">
      <div class="dv-accent-bar"></div>
      <q-card-section class="dv-dialog-header q-px-lg q-py-md">
        <div class="row items-center justify-between">
          <div class="row items-center q-gutter-sm">
            <div class="dv-modal-icon">
              <q-icon name="assignment_return" size="22px" color="#2E7D32" />
            </div>
            <div>
              <div class="dv-modal-title">Nueva Devolución</div>
              <div class="dv-modal-eyebrow">Devolución de mercadería al proveedor</div>
            </div>
          </div>
          <q-btn icon="close" flat round dense class="dv-close-btn" @click="cerrar">
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
        class="fit col dv-stepper"
      >

        <!-- PASO 1: Recepción -->
        <q-step :name="1" title="Recepción" icon="inventory" :done="paso > 1">
          <PasoSeleccionarRecepcion
            :recepciones="recepcionesDisponibles"
            :seleccionada="recepcionSeleccionada"
            @seleccionar="seleccionarRecepcion"
          />
        </q-step>

        <!-- PASO 2: Productos -->
        <q-step :name="2" title="Productos" icon="inventory_2" :done="paso > 2">
          <PasoProductos
            v-if="paso >= 2"
            :items="items"
            :motivos="motivos"
          />
        </q-step>

        <!-- PASO 3: Revisión -->
        <q-step :name="3" title="Revisión" icon="fact_check" :done="paso > 3">
          <PasoRevision
            v-if="paso >= 3"
            :items="items"
            :seleccionada="recepcionSeleccionada"
            :observacion="observacion"
            @update:observacion="observacion = $event"
          />
        </q-step>

        <template v-slot:navigation>
          <q-stepper-navigation class="dv-stepper-nav q-px-lg q-py-md">
            <div class="row items-center justify-between">
              <div class="row q-gutter-sm">
                <q-btn
                  v-if="paso > 1"
                  flat
                  color="green-8"
                  label="Atrás"
                  icon="arrow_back"
                  no-caps
                  class="dv-btn"
                  @click="stepperRef?.previous()"
                />
                <q-btn
                  v-if="paso === 1"
                  flat
                  color="green-8"
                  label="Cancelar"
                  no-caps
                  class="dv-btn"
                  @click="cerrar"
                />
              </div>

              <div class="row q-gutter-sm" style="margin-left: auto">
                <q-btn
                  v-if="paso === 3"
                  flat
                  color="grey-7"
                  label="Guardar Borrador"
                  icon="save"
                  no-caps
                  class="dv-btn"
                  @click="guardarBorrador"
                />
                <q-btn
                  v-if="paso === 1"
                  class="dv-btn dv-btn-primario"
                  unelevated
                  color="green-8"
                  label="Continuar"
                  icon="arrow_forward"
                  no-caps
                  :disable="!recepcionSeleccionada"
                  @click="stepperRef?.next()"
                />
                <q-btn
                  v-if="paso === 2"
                  class="dv-btn dv-btn-primario"
                  unelevated
                  color="green-8"
                  label="Continuar"
                  icon="arrow_forward"
                  no-caps
                  :disable="!hayProductosValidos"
                  @click="stepperRef?.next()"
                />
                <q-btn
                  v-if="paso === 3"
                  class="dv-btn dv-btn-primario"
                  unelevated
                  color="green-8"
                  label="Confirmar Devolución"
                  icon="check_circle"
                  no-caps
                  :loading="guardando"
                  @click="confirmar"
                />
              </div>
            </div>
          </q-stepper-navigation>
        </template>
      </q-stepper>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useQuasar } from 'quasar'
import {
  listarRecepcionesDisponibles,
  listarMotivosDevolucion,
  crearDevolucion,
  confirmarDevolucion
} from '../../api/devoluciones/devoluciones'
import PasoSeleccionarRecepcion from './pasos/PasoSeleccionarRecepcion.vue'
import PasoProductos from './pasos/PasoProductos.vue'
import PasoRevision from './pasos/PasoRevision.vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'guardado'])

const $q = useQuasar()
const stepperRef = ref(null)
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const paso = ref(1)
const recepcionesDisponibles = ref([])
const recepcionSeleccionada = ref(null)
const items = ref([])
const motivos = ref([])
const observacion = ref('')
const guardando = ref(false)

const itemsSeleccionados = computed(() => items.value.filter((i) => i.seleccionado))

const hayProductosValidos = computed(() =>
  itemsSeleccionados.value.length > 0 &&
  itemsSeleccionados.value.every(
    (i) =>
      Number(i.cantidadDevolver) > 0 &&
      Number(i.cantidadDevolver) <= disponibleOrigen(i) &&
      i.ubicacionOrigen &&
      i.motivo
  )
)

watch(
  () => props.modelValue,
  async (val) => {
    if (val) {
      paso.value = 1
      recepcionSeleccionada.value = null
      items.value = []
      observacion.value = ''
      try {
        const [recepciones, motivosDisponibles] = await Promise.all([
          listarRecepcionesDisponibles(),
          listarMotivosDevolucion()
        ])
        recepcionesDisponibles.value = (recepciones || []).map(mapearRecepcionDisponible)
        motivos.value = motivosDisponibles || []
      } catch (e) {
        recepcionesDisponibles.value = []
        motivos.value = []
      }
    }
  }
)

function mapearRecepcionDisponible(recepcion) {
  return {
    ...recepcion,
    estado: 'DISPONIBLE',
    detalle: (recepcion.detalles || []).map((detalle) => {
      const ubicaciones = (detalle.ubicaciones || []).map((ubicacion) => ({
        idUbicacion: ubicacion.idUbicacion,
        codigo: ubicacion.codigo,
        nombre: `${ubicacion.almacen} · ${ubicacion.codigo} - ${ubicacion.nombre}`,
        ubicacion: ubicacion.nombre,
        tipoAlmacen: ubicacion.tipoAlmacen,
        disponible: Number(ubicacion.disponible || 0)
      }))
      return {
        id: detalle.idDetalleRecepcion,
        idDetalleRecepcion: detalle.idDetalleRecepcion,
        idProducto: detalle.idProducto,
        producto: detalle.producto,
        idLote: detalle.idLote,
        lote: detalle.lote || 'Sin lote',
        recibido: Number(detalle.recibido || 0),
        yaDevuelto: Number(detalle.yaDevuelto || 0),
        disponible: Number(detalle.disponibleParaDevolver || 0),
        ubicacion: ubicaciones[0]?.nombre || 'Sin ubicación',
        ubicaciones
      }
    })
  }
}

function seleccionarRecepcion(idRecepcion) {
  const rec = recepcionesDisponibles.value.find((r) => r.idRecepcion === idRecepcion)
  recepcionSeleccionada.value = rec || null
  items.value = (rec?.detalle || []).map((d) => {
    const ubicaciones = normalizarUbicaciones(d)
    return {
      id: d.id,
      idDetalleRecepcion: d.idDetalleRecepcion,
      producto: d.producto,
      lote: d.lote,
      ubicacion: d.ubicacion,
      ubicaciones,
      ubicacionOrigen: ubicaciones[0]?.value || null,
      recibido: d.recibido,
      yaDevuelto: d.yaDevuelto,
      disponible: d.disponible,
      seleccionado: false,
      cantidadDevolver: null,
      motivo: null
    }
  })
}

function normalizarUbicaciones(detalle) {
  if (Array.isArray(detalle.ubicaciones) && detalle.ubicaciones.length) {
    return detalle.ubicaciones.map((u) => ({
      label: u.nombre || u.codigo || u.ubicacion,
      value: u.idUbicacion ?? u.codigo ?? u.ubicacion,
      tipo: u.tipoAlmacen || u.tipo || 'ALMACEN',
      disponible: Number(u.disponible || 0)
    }))
  }
  return [{
    label: detalle.ubicacion || 'Ubicación sin identificar',
    value: detalle.ubicacion || 'SIN_UBICACION',
    tipo: detalle.tipoAlmacen || 'ALMACEN',
    disponible: Number(detalle.disponible || 0)
  }]
}

function disponibleOrigen(item) {
  const ubicacion = item.ubicaciones?.find((u) => u.value === item.ubicacionOrigen)
  return Number(ubicacion?.disponible ?? item.disponible ?? 0)
}

async function guardarBorrador() {
  await crear('BORRADOR')
}

async function confirmar() {
  await crear('CONFIRMADA')
}

async function crear(estado) {
  if (!hayProductosValidos.value) {
    $q.notify({ type: 'warning', message: 'Complete los productos a devolver y sus motivos.' })
    return
  }
  guardando.value = true
  try {
    const payload = {
      idRecepcion: recepcionSeleccionada.value.idRecepcion,
      observacion: observacion.value,
      detalles: itemsSeleccionados.value.map((i) => ({
        idDetalleRecepcion: i.idDetalleRecepcion,
        idMotivoDevolucion: i.motivo.idMotivoDevolucion,
        cantidad: Number(i.cantidadDevolver),
        observacion: null,
        origenes: [{
          idUbicacion: i.ubicacionOrigen,
          cantidad: Number(i.cantidadDevolver)
        }]
      }))
    }
    let creado = await crearDevolucion(payload)
    if (estado === 'CONFIRMADA') {
      creado = await confirmarDevolucion(creado.idDevolucion)
    }
    $q.notify({
      type: 'success',
      message:
        estado === 'BORRADOR'
          ? `Devolución ${creado.numero} guardada como borrador.`
          : `Devolución ${creado.numero} confirmada.`
    })
    emit('guardado', creado)
    cerrar()
  } catch (e) {
    $q.notify({ type: 'negative', message: 'Error al guardar la devolución.' })
  } finally {
    guardando.value = false
  }
}

function cerrar() {
  visible.value = false
  paso.value = 1
}
</script>

<style scoped src="../../assets/styles/devoluciones/devoluciones.css"></style>

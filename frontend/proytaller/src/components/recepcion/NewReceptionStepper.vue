<template>
  <q-dialog
    :model-value="visible"
    @update:model-value="val => { visible = val }"
    maximized
    transition-show="slide-up"
    transition-hide="slide-down"
    class="rp-dialog-wrap"
  >
    <q-card class="rp-dialog-card column">
      <div class="rp-accent-bar"></div>
      <q-card-section class="rp-dialog-header q-px-lg q-py-md">
        <div class="row items-center justify-between">
          <div class="row items-center q-gutter-sm">
            <div class="rp-modal-icon">
              <q-icon name="inventory" size="22px" color="#2E7D32" />
            </div>
            <div>
              <div class="rp-modal-title">Nueva Recepción</div>
              <div class="rp-modal-eyebrow">Recepción de mercadería</div>
            </div>
          </div>
          <q-btn icon="close" flat round dense class="rp-close-btn" v-close-popup @click="cerrar">
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
        class="fit col rp-stepper"
      >
        <!-- PASO 1: Pedido -->
        <q-step :name="1" title="Pedido" done-icon="check" :done="paso > 1">
          <StepSelectOrder
            :pedidos="pedidosDisponibles"
            :pedido-seleccionado="pedidoSeleccionado"
            @seleccionar="seleccionarPedido"
          />
        </q-step>

        <!-- PASO 2: Recepción -->
        <q-step :name="2" title="Recepción" done-icon="check" :done="paso > 2">
          <StepReception v-if="paso >= 2" :items="items" :motivos="motivos" :pedido-seleccionado="pedidoSeleccionado" />
        </q-step>

        <!-- PASO 3: Lotes -->
        <q-step :name="3" title="Lotes" done-icon="check" :done="paso > 3">
          <StepLots v-if="paso >= 3" :items="itemsConLote" />
        </q-step>

        <!-- PASO 4: Revisión -->
        <q-step :name="4" title="Revisión" done-icon="check" :done="paso > 4">
          <StepReview
            v-if="paso >= 4"
            :items="items"
            :pedido-seleccionado="pedidoSeleccionado"
            :usuario="usuario"
            :observacion="observacion"
            @update:observacion="observacion = $event"
          />
        </q-step>

        <!-- PASO 5: Confirmación -->
        <q-step :name="5" title="Confirmación" done-icon="check" :done="paso > 5">
          <StepConfirm v-if="paso >= 5" :items="items" :cargando="confirmando" @confirmar="confirmarRecepcion" />
        </q-step>

        <!-- PASO 6: Distribución -->
        <q-step :name="6" title="Distribución" done-icon="check">
          <StepDistribution v-if="paso >= 6" :items="distribucion" :ubicaciones="ubicaciones" />
        </q-step>

        <template v-slot:navigation>
          <q-stepper-navigation class="rp-stepper-nav q-px-lg q-py-md">
            <div class="row items-center justify-between">
              <q-btn
                v-if="paso > 1 && paso !== 5"
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
                  v-if="paso === 1"
                  unelevated
                  color="green-8"
                  label="Continuar"
                  icon="arrow_forward"
                  no-caps
                  :disable="!pedidoSeleccionado"
                  @click="irRecepción"
                />

                <q-btn
                  v-if="paso === 2"
                  unelevated
                  color="green-8"
                  label="Continuar"
                  icon="arrow_forward"
                  no-caps
                  @click="irLotes"
                />

                <q-btn
                  v-if="paso === 3"
                  unelevated
                  color="green-8"
                  label="Continuar"
                  icon="arrow_forward"
                  no-caps
                  @click="irRevision"
                />

                <q-btn
                  v-if="paso === 4"
                  unelevated
                  color="green-8"
                  label="Continuar"
                  icon="arrow_forward"
                  no-caps
                  @click="irConfirmacion"
                />

                <q-btn
                  v-if="paso === 6"
                  unelevated
                  color="green-8"
                  label="Finalizar recepción"
                  icon="flag"
                  no-caps
                  :disable="finalizando || hayErroresDistribucion"
                  :loading="finalizando"
                  @click="finalizar"
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
import { crearRecepcion } from '../../api/recepcion/recepcion'
import { listarPedidos, obtenerPedido } from '../../api/pedido/pedido'
import { listarMotivos } from '../../api/devoluciones/motivos'
import { listarUbicaciones } from '../../api/ubicacionAlmacen/ubicacionAlmacen'
import { useAuthStore } from '../../store/store'
import StepSelectOrder from './steps/StepSelectOrder.vue'
import StepReception from './steps/StepReception.vue'
import StepLots from './steps/StepLots.vue'
import StepReview from './steps/StepReview.vue'
import StepConfirm from './steps/StepConfirm.vue'
import StepDistribution from './steps/StepDistribution.vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  faltante: { type: Object, default: null },
  devolucion: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'guardado'])

const $q = useQuasar()
const authStore = useAuthStore()
const stepperRef = ref(null)
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const paso = ref(1)
const pedidosDisponibles = ref([])
const pedidoSeleccionado = ref(null)
const items = ref([])
const distribucion = ref([])
const motivos = ref([])
const ubicaciones = ref([])
const observacion = ref('')
const confirmando = ref(false)
const finalizando = ref(false)

const usuario = computed(() => authStore.fullName || authStore.getUsername || '')

const itemsConLote = computed(() => items.value.filter((i) => i.usaLote))

function hoy() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

watch(
  () => props.modelValue,
  async (val) => {
    if (val) {
      paso.value = 1
      pedidoSeleccionado.value = null
      items.value = []
      distribucion.value = []
      observacion.value = ''
      try {
        const [ped, mot, ub] = await Promise.all([
          props.faltante || props.devolucion
            ? obtenerPedido(props.faltante?.idPedido || props.devolucion.idPedido)
            : listarPedidos({ estado: 'ENVIADO', page: 0, size: 100 }),
          listarMotivos(),
          listarUbicaciones()
        ])
        pedidosDisponibles.value = props.faltante || props.devolucion ? [ped] : ped.content || []
        motivos.value = mot || []
        ubicaciones.value = ub || []
        if (props.faltante) {
          seleccionarPedido(ped, props.faltante)
          paso.value = 2
        }
        if (props.devolucion) {
          seleccionarPedido(ped, null, props.devolucion)
          paso.value = 2
        }
      } catch (e) {
        pedidosDisponibles.value = []
      }
    }
  }
)

function seleccionarPedido(ped, faltante = null, devolucion = null) {
  pedidoSeleccionado.value = ped
  const detalleReemplazo = devolucion?.items?.[0]
  items.value = ped.detalles
    .filter((d) => !faltante || d.idDetalle === faltante.idDetallePedido)
    .filter((d) => !detalleReemplazo || d.idDetalle === detalleReemplazo.idDetallePedido)
    .map((d) => {
      const usaLote = Boolean(d.controlaLote || d.controlaVencimiento)
      return {
        idDetallePedido: d.idDetalle,
        idProducto: d.idProducto,
        producto: d.producto,
        unidad: d.unidad,
        factor: d.factor,
        unidadBase: d.unidadBase || 'UND',
        usaLote,
        precio: 0,
        solicitado: faltante ? Number(faltante.faltante) : Number(detalleReemplazo?.pendienteReemplazo ?? d.cantidad),
        recibido: faltante ? Number(faltante.faltante) : Number(detalleReemplazo?.pendienteReemplazo ?? d.cantidad),
        aceptado: faltante ? Number(faltante.faltante) : Number(detalleReemplazo?.pendienteReemplazo ?? d.cantidad),
        rechazado: 0,
        motivo: null,
        subtotal: 0,
        codigoLote: '',
        fechaFabricacion: hoy(),
        fechaVencimiento: ''
      }
    })
}

function irRecepción() {
  if (!pedidoSeleccionado.value) return
  stepperRef.value.next()
}

function irLotes() {
  stepperRef.value.next()
}

function irRevision() {
  stepperRef.value.next()
}

function irConfirmacion() {
  stepperRef.value.next()
}

async function confirmarRecepcion() {
  confirmando.value = true
  try {
    $q.notify({ type: 'success', message: 'Recepción confirmada. Distribuya la mercadería aceptada.' })
    paso.value = 6
    const almacenUb = ubicaciones.value.find((u) => u.tipoAlmacen === 'ALMACEN')
    const salaUb = ubicaciones.value.find((u) => u.tipoAlmacen === 'SALA_VENTAS')
    distribucion.value = items.value
      .filter((i) => i.aceptado > 0)
      .map((i) => ({
        idDetallePedido: i.idDetallePedido,
        idProducto: i.idProducto,
        producto: i.producto,
        lote: i.codigoLote || '—',
        grupoLote: i.usaLote ? i.codigoLote || '—' : '—',
        usaLote: i.usaLote,
        aceptado: i.aceptado,
        almacen: 0,
        sala: 0,
        idUbicacionAlmacen: almacenUb ? almacenUb.idUbicacion : null,
        idUbicacionSala: salaUb ? salaUb.idUbicacion : null
      }))
  } finally {
    confirmando.value = false
  }
}

const hayErroresDistribucion = computed(() =>
  distribucion.value.some((r) => (r.almacen || 0) + (r.sala || 0) > (r.aceptado || 0))
)

async function finalizar() {
  finalizando.value = true
  try {
    const sinPrecio = items.value.filter((i) => i.aceptado > 0 && !(Number(i.precio) > 0))
    if (sinPrecio.length > 0) {
      $q.notify({ type: 'warning', message: 'Ingrese el precio de compra de todos los productos aceptados.' })
      paso.value = 2
      return
    }
    const payload = {
      idPedido: pedidoSeleccionado.value.idPedido,
      idDevolucionReemplazo: props.devolucion?.idDevolucion || null,
      observacion: observacion.value || null,
      detalles: items.value.map((i) => {
        const dist = distribucion.value.find((d) => d.idDetallePedido === i.idDetallePedido)
        const distribuciones = []
        if (dist) {
          if ((dist.almacen || 0) > 0) distribuciones.push({ idUbicacion: dist.idUbicacionAlmacen, cantidad: Number(dist.almacen) })
          if ((dist.sala || 0) > 0) distribuciones.push({ idUbicacion: dist.idUbicacionSala, cantidad: Number(dist.sala) })
        }
        return {
          idDetallePedido: i.idDetallePedido,
          cantidadRecibida: Number(i.recibido),
          cantidadAceptada: Number(i.aceptado),
          cantidadRechazada: Number(i.rechazado),
          idMotivoDevolucion: i.rechazado > 0 ? i.motivo : null,
          precioUnitario: Number(i.precio),
          distribuciones,
          lote: i.usaLote
            ? {
                codigoLote: i.codigoLote || null,
                fechaFabricacion: i.fechaFabricacion || null,
                fechaVencimiento: i.fechaVencimiento || null
              }
            : null
        }
      })
    }
    const creado = await crearRecepcion(payload)
    $q.notify({ type: 'success', message: `Recepción ${creado.numero} finalizada correctamente.` })
    emit('guardado', creado)
    cerrar()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.response?.data?.error || 'Error al finalizar la recepción.'
    $q.notify({ type: 'negative', message: msg })
  } finally {
    finalizando.value = false
  }
}

function cerrar() {
  visible.value = false
  paso.value = 1
}
</script>

<style scoped src="../../assets/styles/recepcion/recepcion.css"></style>

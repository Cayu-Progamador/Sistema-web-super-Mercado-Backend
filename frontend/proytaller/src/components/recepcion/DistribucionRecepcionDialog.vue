<template>
  <q-dialog v-model="visible" class="rp-dialog-wrap">
    <q-card class="rp-dialog-card column" style="max-width: 1100px">
      <div class="rp-accent-bar"></div>

      <q-card-section class="rp-dialog-header q-px-lg q-py-md">
        <div class="row items-center justify-between">
          <div class="row items-center q-gutter-sm">
            <div class="rp-modal-icon">
              <q-icon name="local_shipping" size="22px" color="#2E7D32" />
            </div>
            <div>
              <div class="rp-modal-title">Distribuir Recepción {{ recepcion?.numero || '' }}</div>
              <div class="rp-modal-eyebrow">Asigne la mercadería a Almacén o Sala de Ventas</div>
            </div>
          </div>
          <q-btn icon="close" flat round dense class="rp-close-btn" v-close-popup>
            <q-tooltip>Cerrar</q-tooltip>
          </q-btn>
        </div>
      </q-card-section>

      <q-card-section class="col q-pa-lg" style="overflow: auto">
        <div class="row q-col-gutter-md q-mb-md">
          <div class="col-12 col-md-3">
            <div class="rp-mini-kpi rp-mini-kpi-ace">
              <div class="rp-mini-label">UNIDADES ACEPTADAS</div>
              <div class="rp-mini-valor">{{ formatNum(totalAceptado) }}</div>
            </div>
          </div>
          <div class="col-12 col-md-3">
            <div class="rp-mini-kpi rp-mini-kpi-rev">
              <div class="rp-mini-label">ALMACÉN</div>
              <div class="rp-mini-valor">{{ formatNum(totalAlmacen) }}</div>
            </div>
          </div>
          <div class="col-12 col-md-3">
            <div class="rp-mini-kpi rp-mini-kpi-par">
              <div class="rp-mini-label">SALA DE VENTAS</div>
              <div class="rp-mini-valor">{{ formatNum(totalSala) }}</div>
            </div>
          </div>
          <div class="col-12 col-md-3">
            <div class="rp-mini-kpi rp-mini-kpi-tot">
              <div class="rp-mini-label">PENDIENTE</div>
              <div class="rp-mini-valor" :style="{ color: totalPendiente > 0 ? '#F57C00' : '#2E7D32' }">
                {{ formatNum(totalPendiente) }}
              </div>
            </div>
          </div>
        </div>

        <q-banner v-if="hayErrores" rounded class="rp-banner-error q-mb-md bg-red-1 text-red-9">
          <template v-slot:avatar>
            <q-icon name="error_outline" color="red-9" size="28px" />
          </template>
          <div class="text-weight-bold">Almacén + Sala de Ventas no puede exceder lo disponible.</div>
        </q-banner>

        <q-table
          :rows="filas"
          :columns="columnas"
          row-key="idDetalleRecepcion"
          flat
          dense
          hide-bottom
          :pagination="{ rowsPerPage: 0 }"
          class="rp-dist-table"
        >
          <template v-slot:body-cell-producto="props">
            <q-td :props="props">
              <div class="text-weight-bold">{{ props.value }}</div>
              <div v-if="props.row.lote" class="rp-factor-hint">
                Lote: {{ props.row.lote }} · Vence: {{ formatFecha(props.row.vencimiento) }}
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-disponible="props">
            <q-td :props="props">
              <span class="text-weight-bold" :style="{ color: props.value > 0 ? '#1B5E20' : '#9E9E9E' }">
                {{ formatNum(props.value) }}
              </span>
            </q-td>
          </template>

          <template v-slot:body-cell-almacen="props">
            <q-td :props="props">
              <div class="row items-center q-gutter-xs no-wrap">
                <q-select
                  :model-value="props.row.idUbicacionAlmacen"
                  :options="opcionesAlmacen"
                  dense
                  outlined
                  emit-value
                  map-options
                  options-dense
                  class="rp-ubicacion-select"
                  style="width: 140px"
                  @update:model-value="props.row.idUbicacionAlmacen = $event"
                />
                <q-input
                  :model-value="props.row.almacen"
                  type="number"
                  dense
                  outlined
                  min="0"
                  class="rp-num-input"
                  :class="{ 'rp-input-error': filaError(props.row) }"
                  style="width: 100px"
                  @update:model-value="setAlmacen(props.row, $event)"
                />
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-sala="props">
            <q-td :props="props">
              <div class="row items-center q-gutter-xs no-wrap">
                <q-select
                  :model-value="props.row.idUbicacionSala"
                  :options="opcionesSala"
                  dense
                  outlined
                  emit-value
                  map-options
                  options-dense
                  class="rp-ubicacion-select"
                  style="width: 140px"
                  @update:model-value="props.row.idUbicacionSala = $event"
                />
                <q-input
                  :model-value="props.row.sala"
                  type="number"
                  dense
                  outlined
                  min="0"
                  class="rp-num-input"
                  :class="{ 'rp-input-error': filaError(props.row) }"
                  style="width: 100px"
                  @update:model-value="setSala(props.row, $event)"
                />
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-validacion="props">
            <q-td :props="props">
              <q-chip
                v-if="filaError(props.row)"
                dense
                size="10px"
                text-color="white"
                style="background: #D32F2F"
              >
                Excede lo disponible
              </q-chip>
              <q-chip
                v-else
                dense
                size="10px"
                text-color="white"
                style="background: #2E7D32"
              >
                OK
              </q-chip>
            </q-td>
          </template>
        </q-table>
      </q-card-section>

      <div class="rp-stepper-nav q-px-lg q-py-md row items-center justify-between">
        <q-btn flat color="green-8" icon="close" label="Cancelar" no-caps v-close-popup />
        <q-btn
          unelevated
          color="green-8"
          icon="local_shipping"
          label="Distribuir"
          no-caps
          :loading="guardando"
          :disable="hayErrores || guardando"
          @click="guardar"
        />
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useQuasar } from 'quasar'
import { distribuirRecepcion } from '../../api/recepcion/recepcion'
import { listarUbicaciones } from '../../api/ubicacionAlmacen/ubicacionAlmacen'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  recepcion: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'actualizado'])

const $q = useQuasar()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const filas = ref([])
const ubicaciones = ref([])
const guardando = ref(false)

const opcionesAlmacen = computed(() =>
  ubicaciones.value
    .filter((u) => u.tipoAlmacen === 'ALMACEN')
    .map((u) => ({ label: `${u.codigo} · ${u.nombre}`, value: u.idUbicacion }))
)

const opcionesSala = computed(() =>
  ubicaciones.value
    .filter((u) => u.tipoAlmacen === 'SALA_VENTAS')
    .map((u) => ({ label: `${u.codigo} · ${u.nombre}`, value: u.idUbicacion }))
)

const columnas = [
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left', style: 'min-width: 200px' },
  { name: 'aceptado', label: 'Aceptado', field: 'aceptado', align: 'center', style: 'width: 90px' },
  { name: 'distribuido', label: 'Distribuido', field: 'distribuido', align: 'center', style: 'width: 100px' },
  { name: 'disponible', label: 'Disponible', field: 'disponible', align: 'center', style: 'width: 90px' },
  { name: 'almacen', label: 'Almacén', align: 'center', style: 'width: 130px' },
  { name: 'sala', label: 'Sala de Ventas', align: 'center', style: 'width: 130px' },
  { name: 'validacion', label: 'Validación', align: 'center', style: 'width: 130px' }
]

watch(
  () => props.modelValue,
  async (val) => {
    if (val) {
      filas.value = []
      guardando.value = false
      try {
        ubicaciones.value = (await listarUbicaciones()) || []
      } catch (e) {
        ubicaciones.value = []
      }
      const detalle = props.recepcion?.detalle || []
      filas.value = detalle
        .filter((d) => Number(d.aceptado || 0) > 0)
        .map((d) => {
          const almacenUb = ubicaciones.value.find((u) => u.tipoAlmacen === 'ALMACEN')
          const salaUb = ubicaciones.value.find((u) => u.tipoAlmacen === 'SALA_VENTAS')
          return {
            idDetalleRecepcion: d.idDetalleRecepcion,
            producto: d.producto,
            lote: d.lote,
            vencimiento: d.vencimiento,
            aceptado: Number(d.aceptado || 0),
            distribuido: Number(d.distribuido || 0),
            disponible: Math.max(0, Number(d.aceptado || 0) - Number(d.distribuido || 0)),
            almacen: 0,
            sala: 0,
            idUbicacionAlmacen: almacenUb ? almacenUb.idUbicacion : null,
            idUbicacionSala: salaUb ? salaUb.idUbicacion : null
          }
        })
    }
  }
)

function setAlmacen(row, val) {
  row.almacen = Number(val) || 0
}

function setSala(row, val) {
  row.sala = Number(val) || 0
}

function disponible(row) {
  return Math.max(0, (row.aceptado || 0) - (row.distribuido || 0))
}

function filaError(row) {
  return (row.almacen || 0) + (row.sala || 0) > disponible(row)
}

const hayErrores = computed(() => filas.value.some((r) => filaError(r)))
const totalAceptado = computed(() => filas.value.reduce((s, r) => s + (r.aceptado || 0), 0))
const totalAlmacen = computed(() => filas.value.reduce((s, r) => s + (r.almacen || 0), 0))
const totalSala = computed(() => filas.value.reduce((s, r) => s + (r.sala || 0), 0))
const totalPendiente = computed(() =>
  filas.value.reduce((s, r) => s + Math.max(0, disponible(r) - (r.almacen || 0) - (r.sala || 0)), 0)
)

function armarPayload() {
  const items = []
  for (const row of filas.value) {
    const distribuciones = []
    if ((row.almacen || 0) > 0) distribuciones.push({ idUbicacion: row.idUbicacionAlmacen, cantidad: Number(row.almacen) })
    if ((row.sala || 0) > 0) distribuciones.push({ idUbicacion: row.idUbicacionSala, cantidad: Number(row.sala) })
    if (distribuciones.length > 0) {
      items.push({ idDetalleRecepcion: row.idDetalleRecepcion, distribuciones })
    }
  }
  return { items }
}

async function guardar() {
  const payload = armarPayload()
  if (payload.items.length === 0) {
    $q.notify({ type: 'warning', message: 'Ingrese al menos una cantidad para distribuir.' })
    return
  }
  guardando.value = true
  try {
    await distribuirRecepcion(props.recepcion.idRecepcion, payload)
    $q.notify({ type: 'success', message: 'Recepción ' + props.recepcion.numero + ' distribuida correctamente.' })
    emit('actualizado')
    visible.value = false
  } catch (e) {
    const msg = e?.response?.data?.message || e?.response?.data?.error || 'Error al distribuir la recepción.'
    $q.notify({ type: 'negative', message: msg })
  } finally {
    guardando.value = false
  }
}

function formatNum(n) {
  return Number(n || 0).toLocaleString('es-BO')
}

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}
</script>

<style scoped src="../../assets/styles/recepcion/recepcion.css"></style>
<template>
  <div>
    <!-- Diagrama visual -->
    <div class="rp-dist-flow q-mb-lg">
      <div class="rp-dist-caja rp-dist-caja-accept">
        <div class="rp-mini-label">UNIDADES ACEPTADAS</div>
        <div class="text-bold" style="font-size: 22px; color: #1B5E20">{{ formatNum(totalAceptado) }}</div>
      </div>

      <div class="rp-dist-connector"></div>

      <div class="rp-dist-cajas">
        <div class="text-center">
          <div class="rp-dist-connector" style="width: 2px; height: 24px; margin: 0 auto"></div>
          <div class="rp-dist-caja rp-dist-caja-almacen">
            <q-icon name="warehouse" size="26px" color="#1976D2" />
            <div class="text-weight-bold text-blue-9 q-mt-xs">Almacén</div>
            <div class="text-bold" style="font-size: 18px; color: #1565C0">{{ formatNum(totalAlmacen) }}</div>
          </div>
        </div>
        <div class="text-center">
          <div class="rp-dist-connector" style="width: 2px; height: 24px; margin: 0 auto"></div>
          <div class="rp-dist-caja rp-dist-caja-sala">
            <q-icon name="storefront" size="26px" color="#F57C00" />
            <div class="text-weight-bold text-orange-9 q-mt-xs">Sala de Ventas</div>
            <div class="text-bold" style="font-size: 18px; color: #E65100">{{ formatNum(totalSala) }}</div>
          </div>
        </div>
      </div>
    </div>

    <div
      class="text-center text-weight-bold rp-dist-pend q-mb-md"
      :style="{ color: totalPendiente > 0 ? '#F57C00' : '#2E7D32' }"
    >
      <span v-if="totalPendiente > 0">{{ formatNum(totalPendiente) }} unidades pendientes de distribución</span>
      <span v-else>Toda la mercadería aceptada ha sido distribuida</span>
    </div>

    <q-banner v-if="hayErrores" rounded class="r-baner-aviso rp-banner-error q-mb-md bg-red-1 text-red-9">
      <template v-slot:avatar>
        <q-icon name="error_outline" color="red-9" size="28px" />
      </template>
      <div class="text-weight-bold">Error en la distribución</div>
      <template v-slot:action>
        <span class="text-weight-bold">Almacén + Sala de Ventas no puede exceder lo aceptado.</span>
      </template>
    </q-banner>

    <q-table
      :rows="items"
      :columns="columnas"
      row-key="idProducto"
      flat
      dense
      hide-bottom
      :pagination="{ rowsPerPage: 0 }"
      class="rp-dist-table"
    >
      <template v-slot:body-cell-producto="props">
        <q-td :props="props">
          <div class="text-weight-bold">{{ props.value }}</div>
          <div v-if="props.row.usaLote" class="rp-factor-hint">
            Lote: {{ props.row.codigoLote || '—' }} · Vence: {{ formatFecha(props.row.fechaVencimiento) }}
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-lote="props">
        <q-td :props="props">
          <span class="text-weight-bold">{{ props.row.grupoLote || '—' }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-aceptado="props">
        <q-td :props="props">
          <span class="rp-subtotal">{{ formatNum(props.value) }}</span>
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
              style="width: 150px"
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
              style="width: 110px"
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
              style="width: 150px"
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
              style="width: 110px"
              @update:model-value="setSala(props.row, $event)"
            />
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-pendiente="props">
        <q-td :props="props">
          <span
            class="text-weight-bold"
            :style="{ color: pendiente(props.row) > 0 ? '#F57C00' : '#2E7D32' }"
          >
            {{ formatNum(pendiente(props.row)) }}
          </span>
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
            Excede lo aceptado
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

    <div v-if="hayErrores" class="text-negative text-center q-mt-sm text-weight-bold" style="font-size: 13px">
      Corrija los errores para poder finalizar la recepción.
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, default: () => [] },
  ubicaciones: { type: Array, default: () => [] }
})

const opcionesAlmacen = computed(() =>
  props.ubicaciones
    .filter((u) => u.tipoAlmacen === 'ALMACEN')
    .map((u) => ({ label: `${u.codigo} · ${u.nombre}`, value: u.idUbicacion }))
)

const opcionesSala = computed(() =>
  props.ubicaciones
    .filter((u) => u.tipoAlmacen === 'SALA_VENTAS')
    .map((u) => ({ label: `${u.codigo} · ${u.nombre}`, value: u.idUbicacion }))
)

const columnas = [
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left', style: 'min-width: 200px' },
  { name: 'lote', label: 'Lote', align: 'center', style: 'width: 90px' },
  { name: 'aceptado', label: 'Aceptado', field: 'aceptado', align: 'center', style: 'width: 100px' },
  { name: 'almacen', label: 'Almacén', align: 'center', style: 'width: 130px' },
  { name: 'sala', label: 'Sala de Ventas', align: 'center', style: 'width: 130px' },
  { name: 'pendiente', label: 'Pendiente', align: 'center', style: 'width: 100px' },
  { name: 'validacion', label: 'Validación', align: 'center', style: 'width: 130px' }
]

function setAlmacen(row, val) {
  row.almacen = Number(val) || 0
}

function setSala(row, val) {
  row.sala = Number(val) || 0
}

function pendiente(row) {
  return Math.max(0, (row.aceptado || 0) - (row.almacen || 0) - (row.sala || 0))
}

function filaError(row) {
  return (row.almacen || 0) + (row.sala || 0) > (row.aceptado || 0)
}

const hayErrores = computed(() => props.items.some((r) => filaError(r)))
const totalAceptado = computed(() => props.items.reduce((s, r) => s + (r.aceptado || 0), 0))
const totalAlmacen = computed(() => props.items.reduce((s, r) => s + (r.almacen || 0), 0))
const totalSala = computed(() => props.items.reduce((s, r) => s + (r.sala || 0), 0))
const totalPendiente = computed(() => props.items.reduce((s, r) => s + pendiente(r), 0))

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

<style scoped src="../../../assets/styles/recepcion/recepcion.css"></style>
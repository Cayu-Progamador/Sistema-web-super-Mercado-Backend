<template>
  <div>
    <div class="row items-center justify-between q-mb-md">
      <div>
        <div class="text-subtitle1 text-bold text-dark">Lotes de productos</div>
        <div class="text-caption text-grey-6">
          Solo los productos que requieren control de lote aparecen aquí.
        </div>
      </div>
      <q-badge outline color="green-7" class="rp-cant-chip">
        {{ items.length }} productos con lote
      </q-badge>
    </div>

    <q-separator class="q-mb-md" />

    <q-banner
      v-if="proximosAVencer.length"
      rounded
      class="r-baner-aviso rp-banner-aviso q-mb-md bg-orange-1 text-orange-9"
    >
      <template v-slot:avatar>
        <q-icon name="warning" color="orange-9" size="28px" />
      </template>
      <div class="text-weight-bold">Producto próximo a vencer</div>
      <template v-slot:action>
        <span class="text-weight-bold">
          {{ proximosAVencer.join(', ') }} vence(n) en menos de 60 días.
        </span>
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
      class="rp-lote-table"
    >
      <template v-slot:body-cell-producto="props">
        <q-td :props="props">
          <div class="text-weight-bold">{{ props.value }}</div>
        </q-td>
      </template>

      <template v-slot:body-cell-codigoLote="props">
        <q-td :props="props">
          <q-input
            :model-value="props.row.codigoLote"
            dense
            outlined
            placeholder="Ej. LOTE-2024-001"
            class="rp-num-input"
            style="width: 180px"
            @update:model-value="props.row.codigoLote = $event"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-fechaFabricacion="props">
        <q-td :props="props">
          <q-input
            :model-value="formatLoteFecha(props.row.fechaFabricacion)"
            dense
            outlined
            readonly
            class="rp-num-input"
            style="width: 130px"
          >
            <template v-slot:append>
              <q-icon name="event" class="cursor-pointer">
                <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                  <q-date
                    :model-value="props.row.fechaFabricacion"
                    mask="YYYY-MM-DD"
                    @update:model-value="props.row.fechaFabricacion = $event"
                  />
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>
        </q-td>
      </template>

      <template v-slot:body-cell-fechaVencimiento="props">
        <q-td :props="props">
          <div class="row items-center no-wrap">
            <q-input
              :model-value="formatLoteFecha(props.row.fechaVencimiento)"
              dense
              outlined
              readonly
              class="rp-num-input"
              :class="{ 'rp-input-error': esProximo(props.row.fechaVencimiento) }"
              style="width: 130px"
            >
              <template v-slot:append>
                <q-icon name="event" class="cursor-pointer">
                  <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                    <q-date
                      :model-value="props.row.fechaVencimiento"
                      mask="YYYY-MM-DD"
                      @update:model-value="props.row.fechaVencimiento = $event"
                    />
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>
            <q-icon
              v-if="esProximo(props.row.fechaVencimiento)"
              name="warning"
              color="orange" size="20px"
              class="q-ml-xs"
            >
              <q-tooltip class="bg-orange-9">Vence en menos de 60 días</q-tooltip>
            </q-icon>
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-cantidad="props">
        <q-td :props="props">
          <span class="text-weight-bold">{{ props.row.aceptado }} {{ props.row.unidadBase }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-estado="props">
        <q-td :props="props">
          <q-chip
            dense
            size="10px"
            text-color="white"
            :style="{ background: esProximo(props.row.fechaVencimiento) ? '#F57C00' : '#2E7D32' }"
          >
            {{ esProximo(props.row.fechaVencimiento) ? 'Ojo vencimiento' : 'OK' }}
          </q-chip>
        </q-td>
      </template>
    </q-table>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, default: () => [] }
})

const columnas = [
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left', style: 'min-width: 180px' },
  { name: 'codigoLote', label: 'Código de lote', align: 'center', style: 'min-width: 200px' },
  { name: 'fechaFabricacion', label: 'Fabricación', align: 'center', style: 'min-width: 150px' },
  { name: 'fechaVencimiento', label: 'Vencimiento', align: 'center', style: 'min-width: 160px' },
  { name: 'cantidad', label: 'Cantidad', align: 'center', style: 'width: 100px' },
  { name: 'estado', label: 'Estado', align: 'center', style: 'width: 120px' }
]

function esProximo(fecha) {
  if (!fecha) return false
  const hoy = new Date()
  hoy.setHours(0, 0, 0, 0)
  const venc = new Date(fecha)
  const dias = Math.round((venc - hoy) / 86400000)
  return dias >= 0 && dias < 60
}

const proximosAVencer = computed(() =>
  props.items.filter((i) => esProximo(i.fechaVencimiento)).map((i) => i.producto)
)

function formatLoteFecha(fecha) {
  if (!fecha) return ''
  const d = new Date(fecha)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}
</script>

<style scoped src="../../../assets/styles/recepcion/recepcion.css"></style>
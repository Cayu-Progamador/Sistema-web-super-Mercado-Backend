<template>
  <div>
    <q-banner rounded class="dv-origin-banner q-mb-md">
      <template v-slot:avatar>
        <q-icon name="warehouse" color="green-8" size="28px" />
      </template>
      <div class="text-weight-bold">Selecciona desde dónde saldrá la mercadería</div>
      <div class="text-caption">La devolución descontará existencias de la ubicación elegida en almacén o sala de ventas.</div>
    </q-banner>

    <q-card flat bordered class="dv-table-card">
      <q-table :rows="items" :columns="columnas" row-key="id" flat :pagination="{ rowsPerPage: 0 }" hide-bottom class="dv-rec-table">
        <template v-slot:body-cell-seleccionar="props">
          <q-td :props="props" class="text-center">
            <q-checkbox :model-value="props.row.seleccionado" color="green-8" @update:model-value="(val) => toggle(props.row, val)" />
          </q-td>
        </template>

        <template v-slot:body-cell-producto="props">
          <q-td :props="props">
            <div class="row items-center no-wrap q-gutter-sm">
              <q-avatar rounded size="38px" class="dv-product-avatar"><q-icon name="inventory_2" size="20px" /></q-avatar>
              <div>
                <div class="text-weight-bold">{{ props.value }}</div>
                <div class="dv-factor-hint">Lote: {{ props.row.lote || 'Sin lote' }}</div>
              </div>
            </div>
          </q-td>
        </template>

        <template v-slot:body-cell-ubicacionOrigen="props">
          <q-td :props="props">
            <q-select
              v-if="props.row.seleccionado"
              v-model="props.row.ubicacionOrigen"
              :options="props.row.ubicaciones"
              option-label="label"
              option-value="value"
              emit-value
              map-options
              outlined
              dense
              color="green-8"
              class="dv-location-select"
              style="min-width: 190px"
            >
              <template v-slot:option="scope">
                <q-item v-bind="scope.itemProps">
                  <q-item-section avatar>
                    <q-icon :name="scope.opt.tipo === 'SALA_VENTAS' ? 'storefront' : 'warehouse'" color="green-8" />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ scope.opt.label }}</q-item-label>
                    <q-item-label caption>{{ formatNum(scope.opt.disponible) }} disponibles</q-item-label>
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
            <span v-else class="text-grey-5">Selecciona el producto</span>
          </q-td>
        </template>

        <template v-slot:body-cell-disponible="props">
          <q-td :props="props" class="text-center">
            <q-chip dense square class="dv-stock-chip">{{ formatNum(disponibleOrigen(props.row)) }}</q-chip>
          </q-td>
        </template>

        <template v-slot:body-cell-cantidadDevolver="props">
          <q-td :props="props" class="text-center">
            <q-input
              v-if="props.row.seleccionado"
              v-model.number="props.row.cantidadDevolver"
              type="number"
              dense
              outlined
              :min="1"
              :max="disponibleOrigen(props.row)"
              :error="tieneError(props.row)"
              :error-message="`Debe ser entre 1 y ${disponibleOrigen(props.row)}`"
              class="dv-num-input"
              style="width: 190px; max-width: 190px"
            />
            <span v-else class="text-grey-4">—</span>
          </q-td>
        </template>

        <template v-slot:body-cell-motivo="props">
          <q-td :props="props">
            <q-select
              v-if="props.row.seleccionado"
              v-model="props.row.motivo"
              :options="motivos"
              option-label="descripcion"
              outlined
              dense
              color="green-8"
              :error="props.row.seleccionado && !props.row.motivo"
              error-message="Seleccione un motivo"
              style="min-width: 220px"
            />
            <span v-else class="text-grey-4">—</span>
          </q-td>
        </template>
      </q-table>
    </q-card>

    <div class="dv-selection-summary q-mt-md">
      <div class="row items-center q-gutter-sm">
        <q-icon name="checklist" color="green-8" size="22px" />
        <div>
          <div class="text-weight-bold">{{ seleccionados.length }} producto(s)</div>
          <div class="text-caption text-grey-7">seleccionados para devolución</div>
        </div>
      </div>
      <div class="text-right">
        <div class="text-caption text-grey-7">Total a devolver</div>
        <div class="dv-selection-total">{{ formatNum(totalADevolver) }} unidades</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, default: () => [] },
  motivos: { type: Array, default: () => [] }
})

const columnas = [
  { name: 'seleccionar', label: '', field: 'seleccionado', align: 'center', style: 'width: 60px' },
  { name: 'producto', label: 'Producto y lote', field: 'producto', align: 'left', style: 'min-width: 220px' },
  { name: 'ubicacionOrigen', label: 'Origen del stock', field: 'ubicacionOrigen', align: 'left', style: 'min-width: 210px' },
  { name: 'disponible', label: 'Disponible', field: 'disponible', align: 'center', style: 'width: 100px' },
  { name: 'cantidadDevolver', label: 'Cantidad', field: 'cantidadDevolver', align: 'center', style: 'width: 210px' },
  { name: 'motivo', label: 'Motivo', field: 'motivo', align: 'left', style: 'min-width: 220px' }
]

const seleccionados = computed(() => props.items.filter((i) => i.seleccionado))
const totalADevolver = computed(() => seleccionados.value.reduce((s, i) => s + (Number(i.cantidadDevolver) || 0), 0))

function toggle(row, val) {
  row.seleccionado = val
  if (val) {
    if (!row.ubicacionOrigen && row.ubicaciones?.length) row.ubicacionOrigen = row.ubicaciones[0].value
    if (!row.cantidadDevolver) row.cantidadDevolver = 1
  } else {
    row.cantidadDevolver = null
    row.motivo = null
  }
}

function disponibleOrigen(row) {
  const ubicacion = row.ubicaciones?.find((u) => u.value === row.ubicacionOrigen)
  return Number(ubicacion?.disponible ?? row.disponible ?? 0)
}

function tieneError(row) {
  if (!row.seleccionado) return false
  if (!row.ubicacionOrigen) return true
  if (!row.cantidadDevolver || row.cantidadDevolver <= 0) return true
  return row.cantidadDevolver > disponibleOrigen(row)
}

function formatNum(n) {
  return Number(n || 0).toLocaleString('es-BO')
}
</script>

<style scoped src="../../../assets/styles/devoluciones/devoluciones.css"></style>

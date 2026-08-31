<template>
  <div>
    <q-banner v-if="seleccionada" rounded class="bg-green-1 text-green-9 dv-banner-inventario q-mb-md">
      <template v-slot:avatar>
        <q-icon name="assignment_return" color="green-8" />
      </template>
      Devolución sobre la recepción <b>{{ seleccionada.numero }}</b> del proveedor
      <b>{{ seleccionada.proveedor }}</b>.
    </q-banner>

    <div class="dv-seccion-title">
      <q-icon name="inventory_2" color="green-7" size="20px" />
      Productos a devolver
    </div>

    <q-banner rounded class="dv-movement-banner q-mb-md">
      <template v-slot:avatar>
        <q-icon name="outbound" color="orange-9" size="26px" />
      </template>
      Al confirmar se registrará una <b>salida por devolución al proveedor</b> desde las ubicaciones indicadas.
    </q-banner>

    <q-card flat bordered class="dv-table-card">
      <q-table
        :rows="itemsSeleccionados"
        :columns="columnas"
        row-key="id"
        flat
        :pagination="{ rowsPerPage: 0 }"
        hide-bottom
        class="dv-rec-table"
      >
        <template v-slot:no-data>
          <div class="text-center q-pa-lg text-grey-6">
            No hay productos seleccionados
          </div>
        </template>

        <template v-slot:body-cell-cantidadDevolver="props">
          <q-td :props="props">
            <span class="text-weight-bold">{{ formatNum(props.value) }}</span>
          </q-td>
        </template>
        <template v-slot:body-cell-motivo="props">
          <q-td :props="props">{{ props.row.motivo?.descripcion || props.row.motivo?.nombre || '—' }}</q-td>
        </template>
      </q-table>
    </q-card>

    <div class="row q-col-gutter-md q-mt-md">
      <div class="col-12">
        <q-input
          :model-value="observacion"
          label="Observación General"
          outlined
          type="textarea"
          autogrow
          color="green-8"
          placeholder="Comentarios adicionales sobre la devolución"
          @update:model-value="$emit('update:observacion', $event)"
        />
      </div>
    </div>

    <div class="dv-total-general q-mt-md">
      <div>
        <div class="dv-mini-label">Total de unidades a devolver</div>
        <div class="dv-mini-valor">{{ formatNum(totalADevolver) }}</div>
      </div>
      <div class="text-right">
        <div class="dv-mini-label">Productos</div>
        <div class="dv-mini-valor">{{ itemsSeleccionados.length }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, default: () => [] },
  seleccionada: { type: Object, default: null },
  observacion: { type: String, default: '' }
})

defineEmits(['update:observacion'])

const columnas = [
  { name: 'producto', label: 'Producto', field: 'producto', align: 'left', style: 'min-width: 220px' },
  { name: 'lote', label: 'Lote', field: 'lote', align: 'center', style: 'width: 120px' },
  { name: 'ubicacionOrigen', label: 'Ubicación origen', field: 'ubicacionOrigen', align: 'left', style: 'min-width: 160px' },
  { name: 'disponible', label: 'Disponible', field: 'disponible', align: 'center', style: 'width: 110px' },
  { name: 'cantidadDevolver', label: 'Cantidad', field: 'cantidadDevolver', align: 'center', style: 'width: 110px' },
  { name: 'motivo', label: 'Motivo', field: 'motivo', align: 'left', style: 'min-width: 220px' }
]

const itemsSeleccionados = computed(() => props.items.filter((i) => i.seleccionado))

const totalADevolver = computed(() =>
  itemsSeleccionados.value.reduce((s, i) => s + (Number(i.cantidadDevolver) || 0), 0)
)

function formatNum(n) {
  return Number(n || 0).toLocaleString('es-BO')
}
</script>

<style scoped src="../../../assets/styles/devoluciones/devoluciones.css"></style>

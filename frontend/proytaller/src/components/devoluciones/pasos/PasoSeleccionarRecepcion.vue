<template>
  <div>
    <div class="row q-col-gutter-md">
      <div class="col-12 col-md-6">
        <q-select
          :model-value="seleccionada?.idRecepcion || null"
          :options="recepciones"
          label="Recepción"
          outlined
          dense
          color="green-8"
          option-label="numero"
          option-value="idRecepcion"
          emit-value
          map-options
          placeholder="Seleccione la recepción"
          class="dv-filter-field"
          @update:model-value="$emit('seleccionar', $event)"
        >
          <template v-slot:no-option>
            <q-item>
              <q-item-section class="text-grey-6">
                No hay recepciones disponibles
              </q-item-section>
            </q-item>
          </template>
        </q-select>
      </div>

      <div class="col-12 col-md-6">
        <q-card v-if="seleccionada" flat bordered class="dv-pedido-sel-card">
          <div class="row q-col-gutter-md">
            <div class="col-6">
              <div class="dv-mini-label">N.º Recepción</div>
              <div class="dv-mini-valor">{{ seleccionada.numero }}</div>
            </div>
            <div class="col-6">
              <div class="dv-mini-label">Proveedor</div>
              <div class="dv-mini-valor">{{ seleccionada.proveedor }}</div>
            </div>
            <div class="col-6">
              <div class="dv-mini-label">Fecha</div>
              <div class="dv-mini-valor">{{ formatFecha(seleccionada.fechaRecepcion) }}</div>
            </div>
            <div class="col-6">
              <div class="dv-mini-label">Estado</div>
              <q-chip
                dense
                size="11px"
                text-color="white"
                class="dv-chip-estado"
                style="background: #2E7D32"
              >
                {{ seleccionada.estado }}
              </q-chip>
            </div>
          </div>
        </q-card>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  recepciones: { type: Array, default: () => [] },
  seleccionada: { type: Object, default: null }
})

defineEmits(['seleccionar'])

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}
</script>

<style scoped src="../../../assets/styles/devoluciones/devoluciones.css"></style>
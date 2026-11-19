<template>
  <div>
    <div class="row q-col-gutter-md">
      <div class="col-12 col-md-6">
        <q-select
          :model-value="seleccionada?.idRecepcion || null"
          :options="opcionesFiltradas"
          label="Recepción de origen *"
          outlined
          dense
          color="green-8"
          use-input
          input-debounce="0"
          option-label="etiqueta"
          option-value="idRecepcion"
          emit-value
          map-options
          hint="Seleccione la recepción donde ingresaron los productos a devolver."
          placeholder="Buscar por código o proveedor"
          class="dv-filter-field"
          @filter="filtrarRecepciones"
          @update:model-value="$emit('seleccionar', $event)"
        >
          <template v-slot:option="scope">
            <q-item v-bind="scope.itemProps">
              <q-item-section>
                <q-item-label>{{ scope.opt.numero }} — {{ scope.opt.proveedor }}</q-item-label>
                <q-item-label caption>{{ formatFecha(scope.opt.fechaRecepcion) }}</q-item-label>
              </q-item-section>
            </q-item>
          </template>
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
            <div class="col-12">
              <div class="dv-mini-label">Productos disponibles para devolución</div>
              <div class="dv-mini-valor">{{ productosDisponibles }} producto(s)</div>
            </div>
          </div>
        </q-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  recepciones: { type: Array, default: () => [] },
  seleccionada: { type: Object, default: null }
})

defineEmits(['seleccionar'])

const opcionesFiltradas = ref([])

watch(
  () => props.recepciones,
  (recepciones) => {
    opcionesFiltradas.value = crearOpciones(recepciones)
  },
  { immediate: true }
)

const productosDisponibles = computed(() =>
  (props.seleccionada?.detalle || []).filter(detalle => Number(detalle.disponible) > 0).length
)

function crearOpciones(recepciones) {
  return (recepciones || []).map(recepcion => ({
    ...recepcion,
    etiqueta: `${recepcion.numero || 'REC'} — ${recepcion.proveedor || 'Sin proveedor'} — ${formatFecha(recepcion.fechaRecepcion)}`
  }))
}

function filtrarRecepciones(valor, update) {
  const texto = (valor || '').toLowerCase().trim()
  update(() => {
    opcionesFiltradas.value = crearOpciones(props.recepciones).filter(recepcion =>
      !texto || recepcion.etiqueta.toLowerCase().includes(texto)
    )
  })
}

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}
</script>

<style scoped src="../../../assets/styles/devoluciones/devoluciones.css"></style>

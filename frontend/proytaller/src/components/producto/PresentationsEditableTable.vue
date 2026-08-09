<template>
  <div ref="rootEl">
    <div v-if="codigoFuente && haySinCodigo" class="row items-center q-gutter-sm q-mb-sm pres-codigo-bar">
      <q-icon name="barcode_reader" size="16px" color="green-8" />
      <span class="pres-codigo-txt">Aplicar el código <b>{{ codigoFuente }}</b> a las presentaciones que aún no tienen código.</span>
      <q-btn dense no-caps color="green-8" size="sm" unelevated icon="check" label="Aplicar" @click="aplicarCodigoATodos" />
    </div>

    <q-table
      v-if="!esMovil"
      :rows="rows"
      :columns="columns"
      row-key="tempId"
      flat
      bordered
      class="pres-table"
      hide-bottom
    >
      <template v-slot:body-cell-nombre="props">
        <q-td :props="props">
<q-input
              v-model="props.row.nombre"
              outlined
              dense
              placeholder="Ej. Botella, Pack x6..."
              :error="errores && errores[props.row.tempId] && errores[props.row.tempId].nombre"
              @input="$emit('cambio')"
            />
        </q-td>
      </template>

      <template v-slot:body-cell-unidad="props">
        <q-td :props="props">
          <q-select
            v-model="props.row.unidad"
            :options="unidades"
            outlined
            dense
            emit-value
            map-options
            placeholder="Unidad"
            menu-anchor="bottom left"
            menu-self="top left"
            @update:model-value="$emit('cambio')"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-factor="props">
        <q-td :props="props">
          <q-input
            v-model="props.row.factor"
            outlined
            dense
            type="number"
            min="0"
            step="0.0001"
            placeholder="1.0"
            @input="$emit('cambio')"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-precio="props">
        <q-td :props="props">
          <q-input
            v-model="props.row.precio"
            outlined
            dense
            type="number"
            min="0"
            step="0.01"
            placeholder="Precio venta Bs"
            :error="!!props.row.precioError"
            :error-message="props.row.precioError"
            @input="$emit('cambio')"
          >
            <template v-slot:prepend>
              <q-icon name="payments" size="16px" />
            </template>
          </q-input>
        </q-td>
      </template>

      <template v-slot:body-cell-codigoBarra="props">
        <q-td :props="props">
          <div class="row items-center no-wrap q-gutter-sm">
            <q-toggle
              v-model="props.row.usaCodigo"
              color="green-8"
              @update:model-value="$emit('cambio')"
            />
            <q-input
              v-if="props.row.usaCodigo"
              v-model="props.row.codigo"
              outlined
              dense
              placeholder="Código de barras"
              style="min-width: 140px"
              @input="$emit('cambio')"
            />
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-principal="props">
        <q-td :props="props" class="text-center">
          <q-radio
            :model-value="principalId"
            :val="props.row.tempId"
            color="green-8"
            @update:model-value="onPrincipalChange(props.row.tempId)"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-acciones="props">
        <q-td :props="props" class="text-center">
          <q-btn
            flat
            round
            dense
            icon="delete_outline"
            color="red-6"
            @click="$emit('eliminar', props.row.tempId)"
          >
            <q-tooltip>Quitar presentación</q-tooltip>
          </q-btn>
        </q-td>
      </template>

      <template v-slot:no-data>
        <div class="pres-no-data">
          <q-icon name="layers_clear" size="34px" color="grey-4" />
          <div>No se han agregado presentaciones. Usa el botón para añadir una.</div>
        </div>
      </template>
    </q-table>

    <!-- ===== Tarjetas para pantallas < 1200px ===== -->
    <div v-else class="column q-gutter-sm">
      <q-card v-for="(row, index) in rows" :key="row.tempId" class="pres-mobile-card">
        <q-card-section class="q-pa-md">
          <div class="row items-center justify-between q-mb-sm">
            <div class="row items-center" style="gap: 8px;">
              <q-avatar size="28px" square style="background: #E8F5E9; border: 1px solid #A5D6A7; border-radius: 7px;">
                <q-icon name="layers" size="16px" color="green-8" />
              </q-avatar>
              <span class="text-subtitle2 text-weight-bold" style="color: #1B5E20;">Presentación {{ index + 1 }}</span>
            </div>
            <q-btn flat round dense icon="delete_outline" color="red-6" @click="$emit('eliminar', row.tempId)">
              <q-tooltip>Quitar presentación</q-tooltip>
            </q-btn>
          </div>

          <q-input
            v-model="row.nombre"
            outlined
            dense
            stack-label
            label="Nombre *"
            placeholder="Ej. Botella, Pack x6..."
            class="q-mb-sm"
            :error="errores && errores[row.tempId] && errores[row.tempId].nombre"
            @input="$emit('cambio')"
          />

          <q-select
            v-model="row.unidad"
            :options="unidades"
            outlined
            dense
            emit-value
            map-options
            stack-label
            label="Se vende en"
            menu-anchor="bottom left"
            menu-self="top left"
            class="q-mb-sm"
            @update:model-value="$emit('cambio')"
          />

          <div class="row q-col-gutter-sm">
            <div class="col-6">
              <q-input
                v-model="row.factor"
                outlined
                dense
                stack-label
                type="number"
                min="0"
                step="0.0001"
                label="Factor conversión"
                placeholder="1.0"
                @input="$emit('cambio')"
              />
            </div>
            <div class="col-6">
              <q-input
                v-model="row.precio"
                outlined
                dense
                stack-label
                type="number"
                min="0"
                step="0.01"
                label="Precio venta"
                placeholder="Bs"
                :error="!!row.precioError"
                :error-message="row.precioError"
                @input="$emit('cambio')"
              >
                <template v-slot:prepend>
                  <q-icon name="payments" size="16px" />
                </template>
              </q-input>
            </div>
          </div>

          <div class="row items-center q-gutter-sm q-mt-sm no-wrap">
            <q-toggle
              v-model="row.usaCodigo"
              color="green-8"
              dense
              @update:model-value="$emit('cambio')"
            />
            <span class="text-caption text-weight-medium" style="color: #2E7D32;">Código de barras</span>
            <q-input
              v-if="row.usaCodigo"
              v-model="row.codigo"
              outlined
              dense
              stack-label
              placeholder="Código de barras"
              class="col"
              @input="$emit('cambio')"
            />
          </div>

          <div class="row items-center q-mt-sm">
            <q-radio
              :model-value="principalId"
              :val="row.tempId"
              color="green-8"
              @update:model-value="onPrincipalChange(row.tempId)"
            />
            <span class="text-body2 text-weight-medium" style="color: #455A64;">Presentación principal</span>
          </div>
        </q-card-section>
      </q-card>

      <div v-if="!rows.length" class="pres-no-data">
        <q-icon name="layers_clear" size="34px" color="grey-4" />
        <div>No se han agregado presentaciones. Usa el botón para añadir una.</div>
      </div>
    </div>

    <div class="row justify-end q-mt-sm">
      <q-btn
        unelevated
        color="green-8"
        dense
        icon="add"
        no-caps
        label="Agregar presentación"
        @click="$emit('agregar')"
        class="pres-add-btn"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  rows: { type: Array, default: () => [] },
  unidades: { type: Array, default: () => [] },
  errores: { type: Object, default: null },
  principalId: { type: [String, Number], default: null }
})

const rootEl = ref(null)
const anchoContenido = ref(window.innerWidth)
let observer = null

onMounted(() => {
  observer = new ResizeObserver((entries) => {
    for (const entry of entries) {
      anchoContenido.value = entry.contentRect.width
    }
  })
  if (rootEl.value) observer.observe(rootEl.value)
})

onBeforeUnmount(() => {
  if (observer) observer.disconnect()
})

const esMovil = computed(() => anchoContenido.value < 1000)

const emit = defineEmits(['agregar', 'eliminar', 'cambio', 'principal-cambio'])

function onPrincipalChange(tempId) {
  emit('principal-cambio', tempId)
}

const codigoFuente = computed(() => {
  const conCodigo = props.rows.filter((r) => r.usaCodigo && r.codigo && r.codigo.trim())
  if (!conCodigo.length) return null
  const principal = conCodigo.find((r) => r.tempId === props.principalId)
  return (principal || conCodigo[0]).codigo.trim()
})

const haySinCodigo = computed(() =>
  props.rows.some((r) => r.usaCodigo && (!r.codigo || !r.codigo.trim()))
)

function aplicarCodigoATodos() {
  const codigo = codigoFuente.value
  if (!codigo) return
  props.rows.forEach((r) => {
    if (r.usaCodigo && (!r.codigo || !r.codigo.trim())) r.codigo = codigo
  })
  emit('cambio')
}

const columns = computed(() => [
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left' },
  { name: 'unidad', label: 'Se vende en', field: 'unidad', align: 'left' },
  { name: 'factor', label: 'Factor conversión', field: 'factor', align: 'left' },
  { name: 'precio', label: 'Precio venta', field: 'precio', align: 'left' },
  { name: 'codigoBarra', label: 'Código de barras', field: 'codigoBarra', align: 'left' },
  { name: 'principal', label: 'Principal', field: 'principal', align: 'center' },
  { name: 'acciones', label: '', field: 'acciones', align: 'center' }
])
</script>

<style scoped>
.pres-table {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #c8e6c9;
}
.pres-table :deep(.q-table thead tr th) {
  background: #E8F5E9 !important;
  color: #2E7D32 !important;
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.pres-table :deep(.q-table tbody td) {
  font-family: 'Nunito', sans-serif;
  color: #2E7D32;
}
.pres-no-data {
  padding: 24px;
  text-align: center;
  color: #2E7D32;
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}
.pres-add-btn {
  border-radius: 8px;
  font-weight: 600;
}

.pres-codigo-bar {
  background: #F1F8E9;
  border: 1px solid #A5D6A7;
  border-radius: 8px;
  padding: 6px 10px;
}

.pres-codigo-txt {
  font-size: 12.5px;
  font-weight: 600;
  color: #2E7D32;
  font-family: 'Nunito', sans-serif;
}

.pres-mobile-card {
  border: 1px solid #c8e6c9;
  border-radius: 12px;
  font-family: 'Nunito', sans-serif;
}

.pres-mobile-card :deep(.q-field__control) {
  background: #F1F8E9;
  border: 1.5px solid #A5D6A7;
  border-radius: 8px;
  color: #2E7D32;
}

.pres-mobile-card :deep(.q-field__control::before) {
  border: none;
}

.pres-mobile-card :deep(.q-field__control::after) {
  border: none;
}

.pres-mobile-card :deep(.q-field__control:hover) {
  border-color: #81C784;
}

.pres-mobile-card :deep(.q-field--focused .q-field__control) {
  border-color: #66BB6A;
}

.pres-mobile-card :deep(.q-field__label) {
  color: #388E3C;
}

.pres-mobile-card :deep(.q-field__native) {
  color: #2E7D32;
}

.pres-mobile-card :deep(.q-field__native::placeholder) {
  color: #81C784;
}

.pres-mobile-card :deep(.q-field__marginal) {
  color: #2E7D32;
}

.pres-mobile-card :deep(.q-select__dropdown-icon) {
  color: #2E7D32;
}
</style>
<template>
  <q-dialog v-model="visible" maximized transition-show="slide-up" transition-hide="slide-down" class="dv-dialog-wrap">
    <q-card class="dv-dialog-card column">
      <div class="dv-accent-bar"></div>
      <q-card-section class="dv-dialog-header q-px-lg q-py-md">
        <div class="row items-center justify-between">
          <div class="row items-center q-gutter-sm">
            <div class="dv-modal-icon"><q-icon name="edit" size="22px" color="#F57C00" /></div>
            <div>
              <div class="dv-modal-title">Editar Devolución</div>
              <div class="dv-modal-eyebrow">Modifique los datos del borrador {{ devolucion?.numero }}</div>
            </div>
          </div>
          <q-btn icon="close" flat round dense class="dv-close-btn" @click="cerrar"><q-tooltip>Cerrar</q-tooltip></q-btn>
        </div>
      </q-card-section>

      <q-card-section class="col scroll q-pa-lg">
        <div class="dv-seccion-title q-mb-md"><q-icon name="inventory" color="green-7" size="20px" /> Recepción de origen</div>
        <div class="row q-col-gutter-md q-mb-xl">
          <div class="col-12 col-md-4"><q-input :model-value="devolucion?.numeroRecepcion" label="Recepción" outlined dense readonly /></div>
          <div class="col-12 col-md-4"><q-input :model-value="devolucion?.proveedor" label="Proveedor" outlined dense readonly /></div>
          <div class="col-12 col-md-4"><q-input :model-value="devolucion?.estado" label="Estado" outlined dense readonly /></div>
        </div>

        <div class="dv-seccion-title q-mb-md"><q-icon name="inventory_2" color="green-7" size="20px" /> Productos a devolver</div>
        <q-table :rows="items" :columns="columnas" row-key="id" flat bordered hide-bottom :pagination="{ rowsPerPage: 0 }" class="dv-rec-table">
          <template #body-cell-producto="props">
            <q-td :props="props"><div class="text-weight-bold">{{ props.row.producto }}</div><div class="text-caption text-grey-6">Lote: {{ props.row.lote }}</div></q-td>
          </template>
          <template #body-cell-origen="props">
            <q-td :props="props">
              <q-select v-model="props.row.origen" :options="props.row.origenes" option-label="label" outlined dense emit-value map-options :disable="props.row.origenes.length <= 1" />
            </q-td>
          </template>
          <template #body-cell-cantidad="props">
            <q-td :props="props"><q-input v-model.number="props.row.cantidadDevolver" type="number" min="1" :max="props.row.disponible" outlined dense /></q-td>
          </template>
          <template #body-cell-motivo="props">
            <q-td :props="props"><q-select v-model="props.row.motivoSeleccionado" :options="motivos" option-label="descripcion" outlined dense /></q-td>
          </template>
        </q-table>

        <q-input v-model="observacion" type="textarea" label="Observación general" outlined color="green-8" class="q-mt-lg" autogrow />
      </q-card-section>

      <div class="dv-stepper-nav q-px-lg q-py-md row items-center justify-between">
        <q-btn flat color="green-8" label="Cancelar" icon="close" no-caps class="dv-btn" @click="cerrar" />
        <q-btn unelevated color="green-8" label="Guardar cambios" icon="save" no-caps class="dv-btn dv-btn-primario" :disable="!formularioValido" @click="guardar" />
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { listarMotivosDevolucion } from '../../api/devoluciones/devoluciones'

const props = defineProps({ modelValue: Boolean, devolucion: { type: Object, default: null } })
const emit = defineEmits(['update:modelValue', 'guardar'])
const items = ref([])
const motivos = ref([])
const observacion = ref('')
const visible = computed({ get: () => props.modelValue, set: valor => emit('update:modelValue', valor) })
const columnas = [
  { name: 'producto', label: 'Producto y lote', field: 'producto', align: 'left' },
  { name: 'origen', label: 'Origen del stock', field: 'origen', align: 'left' },
  { name: 'cantidad', label: 'Cantidad', field: 'cantidadDevolver', align: 'center' },
  { name: 'motivo', label: 'Motivo', field: 'motivo', align: 'left' }
]
const formularioValido = computed(() => items.value.length > 0 && items.value.every(item => Number(item.cantidadDevolver) > 0 && item.origen && item.motivoSeleccionado))

watch(() => props.modelValue, async abierto => {
  if (!abierto || !props.devolucion) return
  motivos.value = await listarMotivosDevolucion().catch(() => [])
  observacion.value = props.devolucion.observacion || ''
  items.value = (props.devolucion.detalle || []).map(item => {
    const origenes = (item.origenes || []).map(origen => ({
      label: [origen.almacen, origen.ubicacion].filter(Boolean).join(' · ') || item.ubicacion,
      value: origen.idUbicacion || origen.ubicacion
    }))
    const opcionesOrigen = origenes.length ? origenes : [{ label: item.ubicacion || 'Origen registrado', value: item.ubicacion || 'ORIGEN' }]
    return {
      ...item,
      disponible: Number(item.disponible || item.cantidadDevolver || 0),
      origenes: opcionesOrigen,
      origen: opcionesOrigen[0].value,
      motivoSeleccionado: motivos.value.find(m => m.nombre === item.motivo || m.descripcion === item.motivo) || null
    }
  })
})

function cerrar() { visible.value = false }
function guardar() {
  emit('guardar', { ...props.devolucion, observacion: observacion.value, detalle: items.value.map(item => ({ ...item, motivo: item.motivoSeleccionado.descripcion })) })
  cerrar()
}
</script>

<style scoped src="../../assets/styles/devoluciones/devoluciones.css"></style>

<template>
  <q-dialog :model-value="modelValue" @update:model-value="emit('update:modelValue', $event)" @hide="alOcultar">
    <q-card style="min-width: 440px; max-width: 90vw; border-radius: 12px;">
      <q-card-section class="row items-center no-wrap" style="background: #E8F5E9; border-radius: 12px 12px 0 0;">
        <q-icon :name="esEdicion ? 'edit' : 'add'" color="green-8" size="24px" />
        <div class="q-ml-sm col">
          <div class="text-subtitle1 text-weight-bold" style="color: #1B5E20;">
            {{ esEdicion ? 'Editar Marca' : 'Nueva Marca' }}
          </div>
          <div class="text-caption" style="color: #66a35f;">Marca que se asigna a los productos del catálogo</div>
        </div>
        <q-btn flat round dense icon="close" size="sm" class="close-btn" @click="cerrar">
          <q-tooltip>Cerrar</q-tooltip>
        </q-btn>
      </q-card-section>

      <q-card-section class="q-pt-md">
        <q-input
          v-model="form.nombre"
          outlined
          dense
          label="Nombre *"
          :rules="[reglaRequerida, reglaLargo, reglaCaracteres, reglaDuplicado]"
          :bottom-slots="true"
          counter
          :maxlength="100"
          class="q-mb-sm cat-campo-dialog"
          @blur="form.nombre = form.nombre.trim()"
        >
          <template v-slot:prepend>
            <q-icon name="branding_watermark" color="green-8" />
          </template>
        </q-input>

        <q-input
          v-model="form.descripcion"
          outlined
          dense
          type="textarea"
          label="Descripción (opcional)"
          :rules="[reglaDescripcionLargo]"
          :bottom-slots="true"
          counter
          :maxlength="255"
          class="q-mb-sm cat-campo-dialog"
          rows="2"
        />
      </q-card-section>

      <q-card-section class="q-pt-none q-pb-md">
        <div class="row justify-end" style="gap: 8px;">
          <q-btn no-caps label="Cancelar" class="cat-btn cat-btn-cancelar" :disable="guardando" @click="cerrar" />
          <q-btn
            no-caps
            :label="guardando ? (esEdicion ? 'Actualizando...' : 'Guardando...') : 'Guardar'"
            :icon="guardando ? null : 'save'"
            class="cat-btn cat-btn-primario"
            :disable="!puedeGuardar || guardando"
            :loading="guardando"
            @click="guardar"
          />
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { reactive, computed, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  marcas: {
    type: Array,
    default: () => []
  },
  marcaEditar: {
    type: Object,
    default: null
  },
  guardando: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'guardar'])

const form = reactive({
  nombre: '',
  descripcion: ''
})

const esEdicion = computed(() => !!props.marcaEditar)

const puedeGuardar = computed(
  () => reglaRequerida(form.nombre) === true
    && reglaLargo(form.nombre) === true
    && reglaCaracteres(form.nombre) === true
    && reglaDuplicado(form.nombre) === true
    && reglaDescripcionLargo(form.descripcion) === true
    && !props.guardando
)

watch(
  () => props.modelValue,
  (val) => {
    if (val) cargarDatos()
  }
)

function cargarDatos() {
  if (props.marcaEditar) {
    form.nombre = props.marcaEditar.nombre || ''
    form.descripcion = props.marcaEditar.descripcion || ''
  } else {
    form.nombre = ''
    form.descripcion = ''
  }
}

function alOcultar() {
  if (!props.modelValue) {
    form.nombre = ''
    form.descripcion = ''
  }
}

const normalizada = (val) => (val || '').trim().toLowerCase()

const reglaRequerida = (val) => (val && val.trim() !== '') || 'El nombre es obligatorio.'

const reglaLargo = (val) => !(val && val.trim().length > 100) || 'El nombre no puede superar 100 caracteres.'

const reglaCaracteres = (val) => !val || /^[a-zA-Z0-9áéíóúüñÁÉÍÓÚÜÑ\s.,&'()-]+$/.test(val) || 'El nombre contiene caracteres no válidos.'

const reglaDuplicado = (val) => {
  const base = normalizada(val)
  if (!base) return true
  const existe = props.marcas.some((m) =>
    normalizada(m.nombre) === base && (!props.marcaEditar || m.id !== props.marcaEditar.id)
  )
  return !existe || 'Ya existe una marca con este nombre.'
}

const reglaDescripcionLargo = (val) => !(val && val.length > 255) || 'La descripción no puede superar 255 caracteres.'

function cerrar() {
  if (props.guardando) return
  emit('update:modelValue', false)
  alOcultar()
}

function guardar() {
  if (!puedeGuardar.value) return
  emit('guardar', {
    id: props.marcaEditar ? props.marcaEditar.id : null,
    nombre: form.nombre.trim(),
    descripcion: form.descripcion.trim()
  })
}
</script>

<style scoped>
.close-btn {
  color: #7aaa4e !important;
  background: #f0f7e8 !important;
  border-radius: 8px !important;
}

.close-btn:hover {
  background: #ddecc5 !important;
  color: #4a8c25 !important;
}

.cat-btn-cancelar {
  background: #ffffff;
  color: #2E7D32;
  border: 0.5px solid #A5D6A7;
  border-radius: 8px;
  min-height: 40px;
  font-weight: 600;
}

.cat-btn-cancelar:hover {
  background: #F1F8E9;
  border-color: #81C784;
  color: #1B5E20;
}

.cat-btn-primario {
  background: #2E7D32 !important;
  color: #ffffff !important;
  font-weight: 700;
  border-radius: 8px;
  min-height: 40px;
  box-shadow: 0 2px 6px rgba(46, 125, 50, 0.3);
}

.cat-btn-primario:hover {
  background: #276B2B !important;
}

.cat-btn-primario .q-icon {
  font-size: 18px;
}

.cat-campo-dialog :deep(.q-field__control) {
  background: #ffffff;
  border-radius: 8px;
  border: 1.5px solid #A5D6A7;
  color: #2E7D32;
}

.cat-campo-dialog :deep(.q-field__control::before) {
  border: none;
}

.cat-campo-dialog :deep(.q-field__control::after) {
  border: none;
}

.cat-campo-dialog :deep(.q-field__control:hover) {
  border-color: #81C784;
}

.cat-campo-dialog :deep(.q-field--focused .q-field__control) {
  border-color: #2E7D32;
}

.cat-campo-dialog :deep(.q-field__native) {
  color: #2E7D32;
}

.cat-campo-dialog :deep(.q-field__native::placeholder) {
  color: #81C784;
}

.cat-campo-dialog :deep(.q-field__label) {
  color: #388E3C;
}

.cat-campo-dialog :deep(.q-field__marginal) {
  color: #2E7D32;
}
</style>
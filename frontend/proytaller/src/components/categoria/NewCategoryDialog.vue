<template>
  <q-dialog :model-value="modelValue" @update:model-value="emit('update:modelValue', $event)">
    <q-card style="min-width: 440px; max-width: 90vw; border-radius: 12px;">
      <q-card-section class="row items-center no-wrap" style="background: #E8F5E9; border-radius: 12px 12px 0 0;">
        <q-icon :name="esEdicion ? 'edit' : 'add'" color="green-8" size="24px" />
        <div class="q-ml-sm col">
          <div class="text-subtitle1 text-weight-bold" style="color: #1B5E20;">
            {{ esEdicion ? 'Editar Categoría' : 'Nueva Categoría' }}
          </div>
          <div class="text-caption" style="color: #66a35f;">Clasificación de mayor nivel del catálogo</div>
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
          :rules="[reglaRequerida, reglaDuplicado]"
          :bottom-slots="true"
          class="q-mb-sm cat-campo-dialog"
        >
          <template v-slot:prepend>
            <q-icon name="category" color="green-8" />
          </template>
        </q-input>

        <q-input
          v-model="form.descripcion"
          outlined
          dense
          type="textarea"
          label="Descripción"
          class="q-mb-sm cat-campo-dialog"
        />
      </q-card-section>

      <q-card-section class="q-pt-none q-pb-md">
        <div class="row justify-end" style="gap: 8px;">
          <q-btn no-caps label="Cancelar" class="cat-btn cat-btn-cancelar" @click="cerrar" />
          <q-btn no-caps label="Guardar" icon="save" class="cat-btn cat-btn-primario" :disable="!puedeGuardar" @click="guardar" />
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
  categorias: {
    type: Array,
    default: () => []
  },
  categoriaEditar: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'guardar'])

const form = reactive({
  nombre: '',
  descripcion: '',
  estado: 'activo'
})

const esEdicion = computed(() => !!props.categoriaEditar)

const puedeGuardar = computed(() => reglaRequerida(form.nombre) === true)

watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      if (props.categoriaEditar) {
        form.nombre = props.categoriaEditar.nombre
        form.descripcion = props.categoriaEditar.descripcion || ''
        form.estado = props.categoriaEditar.estado || 'activo'
      } else {
        form.nombre = ''
        form.descripcion = ''
        form.estado = 'activo'
      }
    }
  }
)

const nominaNormalizada = (val) => (val || '').trim().toLowerCase()

const reglaRequerida = (val) => (val && val.trim() !== '') || 'El nombre es obligatorio.'

const reglaDuplicado = (val) => {
  const base = nominaNormalizada(val)
  if (!base) return true
  const existe = props.categorias.some((c) =>
    nominaNormalizada(c.nombre) === base && (!props.categoriaEditar || c.id !== props.categoriaEditar.id)
  )
  return !existe || 'Ya existe una categoría con este nombre.'
}

function cerrar() {
  emit('update:modelValue', false)
}

function guardar() {
  if (reglaRequerida(form.nombre) !== true || reglaDuplicado(form.nombre) !== true) {
    return
  }
  emit('guardar', {
    id: props.categoriaEditar ? props.categoriaEditar.id : null,
    nombre: form.nombre.trim(),
    descripcion: form.descripcion.trim(),
    estado: form.estado
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

.cat-campo-dialog :deep(.q-select__dropdown-icon) {
  color: #2E7D32;
}
</style>
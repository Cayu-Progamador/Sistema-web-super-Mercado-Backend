<template>
  <q-dialog :model-value="modelValue" @update:model-value="emit('update:modelValue', $event)">
    <q-card style="min-width: 440px; max-width: 90vw; border-radius: 12px;">
      <q-card-section class="row items-center no-wrap" style="background: #E8F5E9; border-radius: 12px 12px 0 0;">
        <q-icon :name="esEdicion ? 'edit' : 'playlist_add'" color="green-8" size="24px" />
        <div class="q-ml-sm col">
          <div class="text-subtitle1 text-weight-bold" style="color: #1B5E20;">
            {{ esEdicion ? 'Editar Subcategoría' : 'Nueva Subcategoría' }}
          </div>
          <div class="text-caption" style="color: #66a35f;">Clasificación secundaria dentro de una categoría</div>
        </div>
        <q-btn flat round dense icon="close" size="sm" class="close-btn" @click="cerrar">
          <q-tooltip>Cerrar</q-tooltip>
        </q-btn>
      </q-card-section>

      <q-card-section class="q-pt-md">
        <q-select
          v-model="form.categoriaId"
          :options="categoriasOpt"
          emit-value
          map-options
          outlined
          dense
          label="Categoría padre *"
          :disable="!!padrePreseleccionado"
          :rules="[reglaCategoriaRequerida]"
          :bottom-slots="true"
          class="q-mb-sm cat-campo-dialog"
        >
          <template v-slot:prepend>
            <q-icon name="category" color="green-8" />
          </template>
        </q-select>

        <q-input
          v-model="form.nombre"
          outlined
          dense
          label="Nombre *"
          maxlength="100"
          :rules="[reglaRequerida, reglaMaxNombre, reglaDuplicado]"
          :bottom-slots="true"
          class="q-mb-sm cat-campo-dialog"
        >
          <template v-slot:prepend>
            <q-icon name="label" color="green-8" />
          </template>
        </q-input>

        <q-input
          v-model="form.descripcion"
          outlined
          dense
          type="textarea"
          label="Descripción"
          maxlength="255"
          :rules="[reglaMaxDescripcion]"
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
  padrePreseleccionado: {
    type: Number,
    default: null
  },
  subcategoriaEditar: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'guardar'])

const form = reactive({
  categoriaId: null,
  nombre: '',
  descripcion: '',
  estado: 'activo'
})

const esEdicion = computed(() => !!props.subcategoriaEditar)

const categoriasOpt = computed(() =>
  props.categorias.map((c) => ({ label: c.nombre, value: c.id }))
)

const puedeGuardar = computed(
  () => reglaCategoriaRequerida(form.categoriaId) === true && reglaRequerida(form.nombre) === true && reglaDuplicado() === true
)

watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      if (props.subcategoriaEditar) {
        form.categoriaId = props.subcategoriaEditar.categoriaId ?? props.padrePreseleccionado
        form.nombre = props.subcategoriaEditar.nombre
        form.descripcion = props.subcategoriaEditar.descripcion || ''
        form.estado = props.subcategoriaEditar.estado || 'activo'
      } else {
        form.categoriaId = props.padrePreseleccionado
          ? props.padrePreseleccionado
          : (categoriasOpt.value.length ? categoriasOpt.value[0].value : null)
        form.nombre = ''
        form.descripcion = ''
        form.estado = 'activo'
      }
    }
  }
)

const nominaNormalizada = (val) => (val || '').trim().toLowerCase()

const reglaCategoriaRequerida = (val) => !!val || 'Selecciona una categoría.'

function subcategoriasDe(categoriaId) {
  const cat = props.categorias.find((c) => c.id === categoriaId)
  return cat ? cat.subcategorias : []
}

const reglaDuplicado = () => {
  const base = nominaNormalizada(form.nombre)
  if (!base) return true
  const existe = props.categorias.some((cat) =>
    (cat.subcategorias || []).some((s) =>
      nominaNormalizada(s.nombre) === base && (!props.subcategoriaEditar || s.id !== props.subcategoriaEditar.id)
    )
  )
  return !existe || 'Ya existe una subcategoría con ese nombre.'
}

const reglaRequerida = (val) => (val && val.trim() !== '') || 'El nombre es obligatorio.'

const reglaMaxNombre = (val) => (val && val.trim().length <= 100) || 'El nombre no puede superar 100 caracteres.'

const reglaMaxDescripcion = (val) => !val || val.length <= 255 || 'La descripción no puede superar 255 caracteres.'

function cerrar() {
  emit('update:modelValue', false)
}

function guardar() {
  if (!form.categoriaId) return
  if (reglaRequerida(form.nombre) !== true) return
  if (reglaMaxNombre(form.nombre) !== true) return
  if (reglaDuplicado() !== true) return
  emit('guardar', {
    id: props.subcategoriaEditar ? props.subcategoriaEditar.id : null,
    categoriaId: form.categoriaId,
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

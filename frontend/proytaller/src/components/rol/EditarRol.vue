<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="modal-card">

      <div class="accent-bar"></div>

      <div class="modal-header">
        <div class="header-left">
          <div class="modal-icon">
            <q-icon :name="esEditar ? 'edit' : 'shield'" size="22px" color="#4a8c25" />
          </div>
          <div>
            <div class="modal-eyebrow">{{ esEditar ? 'EDITAR ROL' : 'NUEVO ROL' }}</div>
            <div class="modal-title">{{ esEditar ? 'Editar Rol' : 'Añadir Rol' }}</div>
          </div>
        </div>
        <q-btn icon="close" flat round dense class="close-btn" v-close-popup />
      </div>

      <q-card-section class="q-px-lg q-pt-md q-pb-sm">

        <div class="field-group q-mb-md">
          <div class="field-lbl">
            <q-icon name="person" size="14px" class="label-icon" />
            Nombre del Rol
          </div>
          <q-input
            v-model="form.nombre"
            outlined
            dense
            placeholder="Ej: ADMINISTRADOR"
            class="field-input"
            :disable="guardando"
            @update:model-value="v => form.nombre = v.toUpperCase()"
          />
        </div>

        <div class="field-group q-mb-md">
          <div class="field-lbl">
            <q-icon name="description" size="14px" class="label-icon" />
            Descripción
          </div>
          <q-input
            v-model="form.descripcion"
            type="textarea"
            outlined
            dense
            placeholder="Describe las funciones y responsabilidades de este rol..."
            class="field-input"
            :disable="guardando"
            rows="3"
          />
        </div>

      </q-card-section>

      <q-card-actions align="right" class="q-px-lg q-pb-md">
        <q-btn flat label="Cancelar" v-close-popup class="btn-cancel" :disable="guardando" />
        <q-btn
          :color="esEditar ? 'orange-8' : 'green-8'"
          :label="esEditar ? 'Actualizar Rol' : 'Crear Rol'"
          :icon="esEditar ? 'save' : 'add'"
          class="btn-save"
          :loading="guardando"
          unelevated
          @click="guardar"
        />
      </q-card-actions>

    </q-card>
  </q-dialog>
</template>

<script setup>
import { reactive, ref, computed, watch } from 'vue'
import { crearRol, actualizarRol } from '../../api/rol/rol'
import { useQuasar } from 'quasar'

const props = defineProps({
  rol: { type: Object, default: null }
})

const abierto = defineModel()
const emit = defineEmits(['guardar'])
const $q = useQuasar()
const guardando = ref(false)

const esEditar = computed(() => props.rol && props.rol.id)

const form = reactive({
  nombre: '',
  descripcion: ''
})

watch(() => props.rol, (val) => {
  if (val && val.id) {
    form.nombre = val.nombre || ''
    form.descripcion = val.descripcion || ''
  } else {
    form.nombre = ''
    form.descripcion = ''
  }
}, { immediate: true })

const guardar = async () => {
  if (!form.nombre.trim()) {
    $q.notify({ type: 'warning', message: 'El nombre del rol es obligatorio' })
    return
  }
  guardando.value = true
  try {
    const payload = {
      nombre: form.nombre.trim().toUpperCase(),
      descripcion: form.descripcion.trim()
    }
    let respuesta
    if (esEditar.value) {
      respuesta = await actualizarRol(props.rol.id, {
        ...payload,
        nombre: 'ROLE_' + payload.nombre
      })
      $q.notify({ type: 'positive', message: 'Rol actualizado correctamente' })
    } else {
      respuesta = await crearRol(payload)
      $q.notify({ type: 'positive', message: 'Rol creado correctamente' })
    }
    emit('guardar', respuesta)
    abierto.value = false
    form.nombre = ''
    form.descripcion = ''
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Error al guardar el rol'
    })
  } finally {
    guardando.value = false
  }
}
</script>

<style scoped>
.modal-card {
  width: 100%;
  max-width: 480px;
  border-radius: 20px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e4edd8;
  box-shadow: 0 20px 60px rgba(42,92,26,0.15);
  font-family: 'Nunito', sans-serif;
}
.accent-bar {
  height: 3px;
  background: linear-gradient(90deg, #82bd43, #4a8c25, #64992b);
}
.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  background: #f0f7e8;
  border-bottom: 1px solid #c8e0a0;
  padding: 16px 20px 14px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.modal-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #eaf4d8;
  border: 1.5px solid #82bd43;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.modal-eyebrow {
  font-size: 11px;
  font-weight: 600;
  color: #7aaa4e;
  font-family: 'Nunito', sans-serif;
}
.modal-title {
  font-size: 16px;
  font-weight: 900;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}
.close-btn {
  color: #7aaa4e !important;
  background: #f0f7e8 !important;
  border-radius: 8px !important;
}
.close-btn:hover { background: #ddecc5 !important; color: #4a8c25 !important; }

.field-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.field-lbl {
  font-size: 11px;
  font-weight: 800;
  color: #5a8040;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-family: 'Nunito', sans-serif;
  display: flex;
  align-items: center;
  gap: 6px;
}
.label-icon {
  color: #82bd43;
}
.field-input :deep(.q-field__control) {
  border-radius: 10px;
  background: #f7f9f4;
  border: 1px solid #e4edd8;
}
.field-input :deep(.q-field__control:focus-within) {
  border-color: #82bd43;
  box-shadow: 0 0 0 3px rgba(130,189,67,0.15);
}

.btn-cancel {
  background: #fff;
  color: #5a5a5a;
  border: 1.5px solid #d0d0d0;
  border-radius: 9px;
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 13px;
}
.btn-cancel:hover { background: #f7f7f7; border-color: #bbb; }
.btn-save {
  border-radius: 9px;
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  box-shadow: 0 4px 14px rgba(74,140,37,0.3);
}
</style>

<template>
  <q-card class="modal-card">
    <div class="accent-bar"></div>

    <div class="modal-header">
      <div class="header-left">
        <div class="modal-icon">
          <q-icon :name="esEditar ? 'edit' : 'work_history'" size="22px" color="#4a8c25" />
        </div>
        <div>
          <div class="modal-eyebrow">{{ esEditar ? 'EDITAR CARGO' : 'NUEVO CARGO' }}</div>
          <div class="modal-title">{{ esEditar ? 'Editar Cargo' : 'Registrar Cargo' }}</div>
          <div class="modal-id" v-if="esEditar && cargo?.id">ID: #{{ cargo.id }}</div>
        </div>
      </div>
      <q-btn icon="close" flat round dense class="close-btn" @click="$emit('cerrar')" />
    </div>

    <q-card-section class="q-px-lg q-pt-md q-pb-lg">
      <q-form @submit.prevent="guardar">
        <div class="row q-col-gutter-md">
          <div class="col-12">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Nombre <span class="required">*</span></div>
              <q-input
                v-model="form.nombre"
                outlined dense
                placeholder="Ej: Supervisor"
                class="field-input"
                :rules="[requiredField]"
              />
            </div>
          </div>
          <div class="col-12">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Descripción</div>
              <q-input
                v-model="form.descripcion"
                outlined dense
                type="textarea"
                rows="3"
                placeholder="Ej: Encargado de supervisar el personal de turno"
                class="field-input"
              />
            </div>
          </div>
        </div>

        <div class="row justify-end q-gutter-sm q-mt-md">
          <q-btn flat label="Cancelar" class="btn-cancel" @click="$emit('cerrar')" />
          <q-btn
            :label="esEditar ? 'Actualizar' : 'Guardar'"
            type="submit"
            :loading="guardando"
            icon="save"
            unelevated
            class="btn-save"
          />
        </div>
      </q-form>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { useQuasar } from 'quasar'
import { crearCargo, actualizarCargo } from '../../api/cargo/cargo'

const props = defineProps({
  cargo: { type: Object, default: null },
  esEditar: { type: Boolean, default: false }
})

const emit = defineEmits(['guardar', 'cerrar'])
const $q = useQuasar()
const guardando = ref(false)

const requiredField = val => (val !== null && val !== undefined && val !== '') || 'Campo obligatorio'

const form = reactive({
  nombre: '',
  descripcion: ''
})

watch(() => props.cargo, (val) => {
  if (val && props.esEditar) {
    form.nombre = val.nombre || ''
    form.descripcion = val.descripcion || ''
  } else {
    form.nombre = ''
    form.descripcion = ''
  }
}, { immediate: true })

const guardar = async () => {
  guardando.value = true
  try {
    if (props.esEditar) {
      await actualizarCargo(props.cargo.id, form)
      $q.notify({ type: 'positive', message: 'Cargo actualizado correctamente' })
    } else {
      await crearCargo(form)
      $q.notify({ type: 'positive', message: 'Cargo registrado correctamente' })
    }
    emit('guardar')
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Error al guardar cargo'
    })
  } finally {
    guardando.value = false
  }
}
</script>

<style scoped>
.modal-card {
  width: 100%;
  max-width: 520px;
  border-radius: 20px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e4edd8;
  box-shadow: 0 20px 60px rgba(42,92,26,0.15);
  font-family: 'Nunito', sans-serif;
  animation: modalSlideIn 0.3s ease-out;
}
@keyframes modalSlideIn {
  from { opacity: 0; transform: translateY(20px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
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
  transition: transform 0.3s ease;
}
.header-left:hover .modal-icon {
  transform: rotate(-8deg) scale(1.05);
}
.modal-eyebrow {
  font-size: 11px;
  font-weight: 600;
  color: #7aaa4e;
}
.modal-title {
  font-size: 16px;
  font-weight: 900;
  color: #2a5c1a;
}
.modal-id {
  font-size: 11px;
  font-weight: 700;
  color: #9dbf78;
  margin-top: 1px;
}
.close-btn {
  color: #7aaa4e;
  background: #f0f7e8;
  border-radius: 8px;
  transition: all 0.2s ease;
}
.close-btn:hover {
  background: #ddecc5;
  color: #4a8c25;
  transform: rotate(90deg);
}
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
}
.required {
  color: #e53935;
  font-size: 14px;
}
.field-input :deep(.q-field__control) {
  border-radius: 10px;
  background: #f7f9f4;
  border: 1px solid #e4edd8;
  transition: all 0.25s ease;
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
  font-weight: 700;
  font-size: 13px;
  transition: all 0.2s ease;
}
.btn-cancel:hover {
  background: #f7f7f7;
  border-color: #bbb;
}
.btn-cancel:active {
  transform: scale(0.97);
}
.btn-save {
  border-radius: 9px;
  font-weight: 800;
  font-size: 13px;
  box-shadow: 0 4px 14px rgba(74,140,37,0.3);
  background: #82bd43;
  color: #fff;
  transition: all 0.25s ease;
}
.btn-save:hover {
  background: #4a8c25;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(74,140,37,0.4);
}
.btn-save:active {
  transform: translateY(0);
}
</style>

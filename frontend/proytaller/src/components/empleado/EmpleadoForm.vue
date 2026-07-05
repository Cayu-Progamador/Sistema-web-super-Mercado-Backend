<template>
  <q-card class="modal-card">
    <div class="accent-bar"></div>

    <div class="modal-header">
      <div class="header-left">
        <div class="modal-icon">
          <q-icon :name="esEditar ? 'edit' : 'person_add'" size="22px" color="#4a8c25" />
        </div>
        <div>
          <div class="modal-eyebrow">{{ esEditar ? 'EDITAR EMPLEADO' : 'NUEVO EMPLEADO' }}</div>
          <div class="modal-title">{{ esEditar ? 'Editar Empleado' : 'Registrar Empleado' }}</div>
        </div>
      </div>
      <q-btn icon="close" flat round dense class="close-btn" @click="$emit('cerrar')" />
    </div>

    <q-card-section class="q-px-lg q-pt-md q-pb-sm">
      <q-form @submit.prevent="guardar">
        <div class="row q-col-gutter-md">
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Nombres</div>
              <q-input v-model="form.nombres" outlined dense placeholder="Nombres" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Apellido Paterno</div>
              <q-input v-model="form.apellidoPaterno" outlined dense placeholder="Apellido paterno" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Apellido Materno</div>
              <q-input v-model="form.apellidoMaterno" outlined dense placeholder="Apellido materno" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">CI</div>
              <q-input v-model="form.ci" outlined dense placeholder="C&eacute;dula de identidad" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Fecha de Nacimiento</div>
              <q-input v-model="form.fechaNacimiento" outlined dense type="date" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Fecha de Contrataci&oacute;n</div>
              <q-input v-model="form.fechaContratacion" outlined dense type="date" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Correo</div>
              <q-input v-model="form.correo" outlined dense placeholder="correo@ejemplo.com" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Tel&eacute;fono</div>
              <q-input v-model="form.telefono" outlined dense placeholder="Tel&eacute;fono" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Sexo</div>
              <q-select
                v-model="form.idSexo"
                :options="sexos"
                option-label="nombre"
                option-value="idSexo"
                emit-value map-options
                outlined dense
                placeholder="Seleccionar sexo"
                class="field-input"
              />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Direcci&oacute;n</div>
              <q-input v-model="form.direccion" outlined dense placeholder="Direcci&oacute;n" class="field-input" />
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
import { reactive, ref, computed, watch } from 'vue'
import { useQuasar } from 'quasar'
import { crearEmpleado, actualizarEmpleado } from '../../api/empleado/empleado'

const props = defineProps({
  empleado: { type: Object, default: null },
  esEditar: { type: Boolean, default: false }
})

const emit = defineEmits(['guardar', 'cerrar'])
const $q = useQuasar()
const guardando = ref(false)

const sexos = ref([
  { idSexo: 1, nombre: 'Masculino' },
  { idSexo: 2, nombre: 'Femenino' }
])

const form = reactive({
  nombres: '',
  apellidoPaterno: '',
  apellidoMaterno: '',
  ci: '',
  fechaNacimiento: '',
  fechaContratacion: '',
  correo: '',
  telefono: '',
  idSexo: null,
  direccion: ''
})

watch(() => props.empleado, (val) => {
  if (val && props.esEditar) {
    form.nombres = val.nombres || ''
    form.apellidoPaterno = val.apellidoPaterno || ''
    form.apellidoMaterno = val.apellidoMaterno || ''
    form.ci = val.ci || ''
    form.fechaNacimiento = val.fechaNacimiento || ''
    form.fechaContratacion = val.fechaContratacion || ''
    form.correo = val.correo || ''
    form.telefono = val.telefono || ''
    form.idSexo = val.idSexo || null
    form.direccion = val.direccion || ''
  } else {
    form.nombres = ''
    form.apellidoPaterno = ''
    form.apellidoMaterno = ''
    form.ci = ''
    form.fechaNacimiento = ''
    form.fechaContratacion = ''
    form.correo = ''
    form.telefono = ''
    form.idSexo = null
    form.direccion = ''
  }
}, { immediate: true })

const guardar = async () => {
  if (!form.nombres.trim()) {
    $q.notify({ type: 'warning', message: 'El nombre es obligatorio' })
    return
  }
  guardando.value = true
  try {
    if (props.esEditar) {
      await actualizarEmpleado(props.empleado.idEmpleado, form)
      $q.notify({ type: 'positive', message: 'Empleado actualizado correctamente' })
    } else {
      await crearEmpleado(form)
      $q.notify({ type: 'positive', message: 'Empleado registrado correctamente' })
    }
    emit('guardar')
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Error al guardar empleado'
    })
  } finally {
    guardando.value = false
  }
}
</script>

<style scoped>
.modal-card {
  width: 100%;
  max-width: 700px;
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
}
.modal-title {
  font-size: 16px;
  font-weight: 900;
  color: #2a5c1a;
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
  font-weight: 700;
  font-size: 13px;
}
.btn-cancel:hover { background: #f7f7f7; border-color: #bbb; }
.btn-save {
  border-radius: 9px;
  font-weight: 800;
  font-size: 13px;
  box-shadow: 0 4px 14px rgba(74,140,37,0.3);
  background: #82bd43;
  color: #fff;
}
</style>

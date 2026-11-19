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

    <q-card-section class="q-px-lg q-pt-md q-pb-lg form-scroll">
      <q-form @submit.prevent="guardar">
        <div class="row q-col-gutter-md">
          <div class="col-12">
            <div class="section-divider">
              <q-icon name="person" size="18px" color="#4a8c25" />
              <span>Datos Personales</span>
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Nombres <span class="required">*</span></div>
              <q-input v-model="form.nombres" outlined dense placeholder="Ej: Juan Carlos" class="field-input"
                :rules="[requiredField, soloLetras]" @update:model-value="sanearNombre('nombres')" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Apellido Paterno</div>
              <q-input v-model="form.apellidoPaterno" outlined dense placeholder="Ej: P&eacute;rez" class="field-input"
                :rules="[soloLetrasOpcional]" @update:model-value="sanearNombre('apellidoPaterno')" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Apellido Materno</div>
              <q-input v-model="form.apellidoMaterno" outlined dense placeholder="Ej: L&oacute;pez" class="field-input"
                :rules="[soloLetrasOpcional]" @update:model-value="sanearNombre('apellidoMaterno')" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">CI <span class="required">*</span></div>
              <q-input v-model="form.ci" outlined dense placeholder="Ej: 1234567" class="field-input" inputmode="numeric"
                maxlength="8" :rules="[requiredField, ciFormat]" @update:model-value="sanearCi" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Fecha de Nacimiento <span class="required">*</span></div>
              <q-input
                v-model="form.fechaNacimiento"
                outlined
                dense
                type="date"
                class="field-input"
                :min="fechaMinimaNacimiento"
                :max="fechaMaximaNacimiento"
                :rules="[requiredField, fechaNacimientoValida]"
              />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Género <span class="required">*</span></div>
              <q-select
                v-model="form.idSexo"
                :options="sexos"
                option-label="nombre"
                option-value="idSexo"
                emit-value map-options
                outlined dense
                placeholder="Seleccionar género"
                class="field-input"
                :rules="[requiredField]"
                :loading="cargandoOpciones"
              />
            </div>
          </div>

          <div class="col-12">
            <div class="section-divider">
              <q-icon name="contact_mail" size="18px" color="#4a8c25" />
              <span>Contacto</span>
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Correo <span class="required">*</span></div>
              <q-input
                v-model="form.correo"
                outlined
                dense
                placeholder="Ej: correo@ejemplo.com"
                class="field-input"
                :rules="[requiredField, emailFormat]"
                @update:model-value="sanearCorreo"
              />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Tel&eacute;fono <span class="required">*</span></div>
              <q-input
                v-model="form.telefono"
                outlined
                dense
                placeholder="Ej: 71234567"
                class="field-input"
                inputmode="numeric"
                maxlength="8"
                :rules="[requiredField, telefonoBoliviano]"
                @update:model-value="sanearTelefono"
              />
            </div>
          </div>

          <div class="col-12">
            <div class="section-divider">
              <q-icon name="location_on" size="18px" color="#4a8c25" />
              <span>Direcci&oacute;n</span>
            </div>
          </div>
          <div class="col-12 col-md-4">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Pa&iacute;s <span class="required">*</span></div>
              <q-select
                v-model="form.pais"
                :options="paises"
                option-label="nombre"
                option-value="nombre"
                emit-value
                map-options
                outlined
                dense
                placeholder="Seleccionar país"
                class="field-input"
                :rules="[requiredField]"
                :loading="cargandoPaises"
                @update:model-value="alCambiarPais"
              />
            </div>
          </div>
          <div class="col-12 col-md-4">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Departamento <span class="required">*</span></div>
              <q-select
                v-model="form.departamento"
                :options="departamentos"
                option-label="nombre"
                option-value="nombre"
                emit-value
                map-options
                outlined
                dense
                placeholder="Seleccionar departamento"
                class="field-input"
                :rules="[requiredField]"
                :disable="!form.pais"
                :loading="cargandoDepartamentos"
                @update:model-value="alCambiarDepartamento"
              />
            </div>
          </div>
          <div class="col-12 col-md-4">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Ciudad <span class="required">*</span></div>
              <q-select
                v-model="form.ciudad"
                :options="ciudades"
                option-label="nombre"
                option-value="nombre"
                emit-value
                map-options
                outlined
                dense
                placeholder="Seleccionar ciudad"
                class="field-input"
                :rules="[requiredField]"
                :disable="!form.departamento"
                :loading="cargandoCiudades"
              />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Zona / Barrio</div>
              <q-input v-model="form.zona" outlined dense placeholder="Ej: Sopocachi, Miraflores" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Calle</div>
              <q-input v-model="form.calle" outlined dense placeholder="Ej: Av. Siempre Viva" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">N&uacute;mero de casa</div>
              <q-input v-model="form.numero" outlined dense placeholder="Ej: 742, #15" class="field-input" />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-group q-mb-md">
              <div class="field-lbl">Referencia</div>
              <q-input v-model="form.referencia" outlined dense placeholder="Ej: Frente a la plaza, Edif. Azul" class="field-input" />
            </div>
          </div>
          
        </div>

        <div class="row justify-end q-gutter-sm q-mt-md">
          <q-btn flat label="Cancelar" class="btn-cancel" @click="$emit('cerrar')" />
          <q-btn
            :label="esEditar ? 'Actualizar' : 'Guardar'"
            type="submit"
            :loading="guardando"
            :disable="!formularioValido || guardando"
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
import { reactive, ref, computed, watch, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import {
  crearEmpleado,
  actualizarEmpleado,
  listarPaises,
  listarDepartamentos,
  listarCiudadesPorDepartamento
} from '../../api/empleado/empleado'
import { listarSexos } from '../../api/sexo/sexo'

const props = defineProps({
  empleado: { type: Object, default: null },
  esEditar: { type: Boolean, default: false }
})

const emit = defineEmits(['guardar', 'cerrar'])
const $q = useQuasar()
const guardando = ref(false)
const cargandoOpciones = ref(false)
const cargandoPaises = ref(false)
const cargandoDepartamentos = ref(false)
const cargandoCiudades = ref(false)

const sexos = ref([])
const paises = ref([])
const departamentos = ref([])
const ciudades = ref([])

const cargarOpciones = async () => {
  cargandoOpciones.value = true
  try {
    const sexosData = await listarSexos()
    sexos.value = sexosData
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cargar opciones' })
  } finally {
    cargandoOpciones.value = false
  }
}

const cargarPaises = async () => {
  cargandoPaises.value = true
  try {
    paises.value = await listarPaises()
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cargar países' })
  } finally {
    cargandoPaises.value = false
  }
}

const cargarDepartamentos = async (idPais) => {
  cargandoDepartamentos.value = true
  try {
    departamentos.value = await listarDepartamentos(idPais)
  } catch {
    departamentos.value = []
    $q.notify({ type: 'negative', message: 'Error al cargar departamentos' })
  } finally {
    cargandoDepartamentos.value = false
  }
}

const cargarCiudades = async (idDepartamento) => {
  cargandoCiudades.value = true
  try {
    ciudades.value = await listarCiudadesPorDepartamento(idDepartamento)
  } catch {
    ciudades.value = []
    $q.notify({ type: 'negative', message: 'Error al cargar ciudades' })
  } finally {
    cargandoCiudades.value = false
  }
}

const form = reactive({
  nombres: '',
  apellidoPaterno: '',
  apellidoMaterno: '',
  ci: '',
  fechaNacimiento: '',
  correo: '',
  telefono: '',
  idSexo: null,
  zona: '',
  calle: '',
  numero: '',
  referencia: '',
  pais: '',
  departamento: '',
  ciudad: ''
})

watch(() => props.empleado, (val) => {
  if (val && props.esEditar) {
    form.nombres = val.nombres || ''
    form.apellidoPaterno = val.apellidoPaterno || ''
    form.apellidoMaterno = val.apellidoMaterno || ''
    form.ci = val.ci || ''
    form.fechaNacimiento = val.fechaNacimiento || ''
    form.correo = val.correo || ''
    form.telefono = val.telefono || ''
    form.idSexo = val.idSexo || null
    form.zona = val.zona || ''
    form.calle = val.calle || ''
    form.numero = val.numero || ''
    form.referencia = val.referencia || ''
    form.pais = val.pais || ''
    form.departamento = val.departamento || ''
    form.ciudad = val.ciudad || ''
  } else {
    form.nombres = ''
    form.apellidoPaterno = ''
    form.apellidoMaterno = ''
    form.ci = ''
    form.fechaNacimiento = ''
    form.correo = ''
    form.telefono = ''
    form.idSexo = null
    form.zona = ''
    form.calle = ''
    form.numero = ''
    form.referencia = ''
    form.pais = ''
    form.departamento = ''
    form.ciudad = ''
  }
}, { immediate: true })

watch(sexos, (nuevosSexos) => {
  if (nuevosSexos.length > 0 && props.esEditar && props.empleado?.idSexo) {
    form.idSexo = props.empleado.idSexo
  }
})

const requiredField = val => (val !== null && val !== undefined && val !== '') || 'Campo obligatorio'

const fechaMinimaNacimiento = computed(() => {
  const fecha = new Date()
  fecha.setFullYear(fecha.getFullYear() - 65)
  return fecha.toISOString().slice(0, 10)
})

const fechaMaximaNacimiento = computed(() => {
  const fecha = new Date()
  fecha.setFullYear(fecha.getFullYear() - 18)
  return fecha.toISOString().slice(0, 10)
})

const fechaNacimientoValida = val => {
  if (!val) return true

  const nacimiento = new Date(val + 'T00:00:00')
  const hoy = new Date()
  hoy.setHours(0, 0, 0, 0)

  if (nacimiento > hoy) return 'La fecha de nacimiento no puede ser futura.'
  if (val < fechaMinimaNacimiento.value) return 'La edad no puede ser mayor a 65 años.'
  if (val > fechaMaximaNacimiento.value) return 'El empleado debe tener al menos 18 años.'
  return true
}

const emailFormat = val =>
  /^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\.[A-Za-z0-9-]+)+$/.test(val || '') ||
  'Ingrese un correo válido, por ejemplo: nombre@correo.com.'

const telefonoBoliviano = val =>
  /^[67]\d{7}$/.test(val || '') ||
  'El celular debe tener 8 dígitos y comenzar con 6 o 7.'

const soloLetras = val => /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$/.test((val || '').trim()) || 'Solo se permiten letras.'

const soloLetrasOpcional = val => !val || /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$/.test(val.trim()) || 'Solo se permiten letras.'

const ciFormat = val => /^\d{4,8}$/.test(val || '') || 'El CI debe tener entre 4 y 8 dígitos.'

const formularioValido = computed(() => {
  return (
    String(form.nombres || '').trim().length > 0 &&
    /^[A-Za-z\u00C0-\u017F ]+$/.test(String(form.nombres).trim()) &&
    /^\d{4,8}$/.test(form.ci || '') &&
    fechaNacimientoValida(form.fechaNacimiento) === true &&
    /^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\.[A-Za-z0-9-]+)+$/.test(form.correo || '') &&
    /^[67]\d{7}$/.test(form.telefono || '') &&
    form.idSexo !== null &&
    String(form.pais || '').trim().length > 0 &&
    String(form.departamento || '').trim().length > 0 &&
    String(form.ciudad || '').trim().length > 0
  )
})

function sanearNombre(campo) {
  form[campo] = String(form[campo] || '').replace(/[^A-Za-zÁÉÍÓÚáéíóúÑñÜü ]/g, '')
}

function sanearCi() {
  form.ci = String(form.ci || '').replace(/\D/g, '').slice(0, 8)
}

function sanearCorreo() {
  form.correo = String(form.correo || '').replace(/\s/g, '').toLowerCase()
}

function sanearTelefono() {
  form.telefono = String(form.telefono || '').replace(/\D/g, '').slice(0, 8)
}

async function alCambiarPais(nombrePais) {
  form.departamento = ''
  form.ciudad = ''
  departamentos.value = []
  ciudades.value = []

  const pais = paises.value.find(item => item.nombre === nombrePais)
  if (pais) await cargarDepartamentos(pais.idPais)
}

async function alCambiarDepartamento(nombreDepartamento) {
  form.ciudad = ''
  ciudades.value = []

  const departamento = departamentos.value.find(item => item.nombre === nombreDepartamento)
  if (departamento) await cargarCiudades(departamento.idDepartamento)
}

async function cargarUbicacionActual() {
  const pais = paises.value.find(item => item.nombre === form.pais)
  if (!pais) return

  await cargarDepartamentos(pais.idPais)
  const departamento = departamentos.value.find(item => item.nombre === form.departamento)
  if (departamento) await cargarCiudades(departamento.idDepartamento)
}

const guardar = async () => {
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
    const mensaje = error.response?.data?.message || ''
    const errorLower = mensaje.toLowerCase()
    if (errorLower.includes('ci') || errorLower.includes('unique') || errorLower.includes('duplicate') || error.response?.status === 409) {
      $q.notify({ type: 'negative', message: 'El CI ingresado ya existe en el sistema' })
    } else {
      $q.notify({ type: 'negative', message: mensaje || 'Error al guardar empleado' })
    }
  } finally {
    guardando.value = false
  }
}

onMounted(async () => {
  await Promise.all([cargarOpciones(), cargarPaises()])
  await cargarUbicacionActual()
})
</script>

<style scoped>
.modal-card {
  width: 100%;
  max-width: 800px;
  border-radius: 20px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e4edd8;
  box-shadow: 0 20px 60px rgba(42,92,26,0.15);
  font-family: 'Nunito', sans-serif;
}
.form-scroll {
  max-height: 65vh;
  overflow-y: auto;
}
.required {
  color: #e53935;
  font-size: 14px;
}
.section-divider {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 800;
  color: #2a5c1a;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  padding-bottom: 4px;
  margin-top: 4px;
  border-bottom: 1.5px solid #c8e0a0;
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

<template>
  <q-card class="usuario-card">

    <!-- Barra superior verde -->
    <div class="accent-bar" />

    <!-- Header -->
    <q-card-section class="card-header-section">
      <div class="header-content">
        <div class="row items-center no-wrap">
          <div class="header-icon-wrap q-mr-md">
            <q-icon name="shield" size="26px" color="primary" />
          </div>
          <div>
            <span class="eyebrow">Sistema de acceso</span>
            <div class="card-title">Registrar Nuevo Usuario</div>
            <div class="text-caption">Complete la información para crear una nueva cuenta de usuario</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" @click="$emit('cerrar')" class="close-btn" />
      </div>
    </q-card-section>

    <q-separator class="custom-separator" />

    <!-- Body -->
    <q-card-section class="card-body">
      <q-form @submit.prevent="guardar">
        <div class="row q-col-gutter-lg">

          <!-- COLUMNA IZQUIERDA -->
          <div class="col-12 col-md-6">

            <div class="section-label q-mb-md">
              <q-icon name="person_outline" size="15px" class="section-icon q-mr-xs" />
              <span>Datos de la cuenta</span>
            </div>

            <!-- Nombre de usuario -->
            <div class="q-mb-md">
              <label class="field-label">Nombre de Usuario <span class="required-star">*</span></label>
              <q-input v-model="formulario.username" placeholder="ej. juan.perez" outlined dense class="custom-input">
                <template #prepend>
                  <q-icon name="person_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon v-if="formulario.username" name="check_circle" size="18px" class="icon-check" />
                </template>
              </q-input>
              <div v-if="formulario.username" class="disponible-msg q-mt-xs">
                <q-icon name="check_circle" size="12px" class="q-mr-xs" />Usuario disponible
              </div>
            </div>

            <!-- Contraseña -->
            <div class="q-mb-sm">
              <label class="field-label">Contraseña <span class="required-star">*</span></label>
              <q-input v-model="formulario.password" placeholder="Mínimo 6 caracteres"
                :type="verContrasena ? 'text' : 'password'" outlined dense class="custom-input">
                <template #prepend>
                  <q-icon name="lock_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon :name="verContrasena ? 'visibility_off' : 'visibility'" class="cursor-pointer input-icon"
                    @click="verContrasena = !verContrasena" />
                </template>
              </q-input>
            </div>

            <!-- Fortaleza contraseña -->
            <div v-if="formulario.password" class="q-mb-md">
              <div class="row items-center justify-between q-mb-xs">
                <span class="text-caption text-grey-6">Fortaleza de la contraseña:</span>
                <span class="text-caption text-weight-bold" :style="{ color: fuerzaColor }">
                  {{ fuerzaLabel }}
                </span>
              </div>
              <q-linear-progress :value="fuerzaValor" :color="fuerzaColorQuasar" rounded size="7px"
                class="strength-bar q-mb-sm" />
              <div class="row q-gutter-xs">
                <q-chip v-for="req in requisitos" :key="req.label" dense size="sm"
                  :icon="req.cumple ? 'check_circle' : 'radio_button_unchecked'"
                  :color="req.cumple ? 'positive' : 'grey-3'" :text-color="req.cumple ? 'white' : 'grey-6'"
                  class="req-chip">{{ req.label }}</q-chip>
              </div>
            </div>

          </div>

          <!-- COLUMNA DERECHA -->
          <div class="col-12 col-md-6">

            <!-- Empleado asociado -->
            <div class="section-label q-mb-md">
              <q-icon name="badge" size="15px" class="section-icon q-mr-xs" />
              <span>Empleado asociado</span>
            </div>
            <div class="q-pa-md">
              <div class="q-gutter-md">
                <label class="field-label">Empleados <span class="required-star">*</span></label>
                <q-select filled v-model="formulario.empleadoId" :options="empleadosOptions"
                  option-label="nombreCompleto" option-value="id" emit-value map-options use-input hide-selected
                  fill-input input-debounce="0" @filter="filterEmpleados">
                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section class="text-grey">
                        No se encontraron empleados
                      </q-item-section>
                    </q-item>
                  </template>
                </q-select>
              </div>
            </div>

            <!-- Roles asignados -->
            <div class="section-label q-mb-md">
              <q-icon name="manage_accounts" size="15px" class="section-icon q-mr-xs" />
              <span>Roles asignados</span>
            </div>

            <!-- role para andir los roles -->
            <div class="q-pa-md">
              <div class="q-gutter-md">
                <label class="field-label">Roles <span class="required-star">*</span></label>
                <q-select v-model="formulario.roles" :options="rolesOptions" option-label="label" option-value="value"
                  emit-value map-options multiple use-chips use-input input-debounce="0" @filter="filterRoles" dense
                  outlined label="Roles">
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps">
                      <q-item-section>
                        <q-item-label>{{ scope.opt.label }}</q-item-label>
                        <q-item-label caption>
                          {{ scope.opt.descripcion }}
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                  </template>
                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section class="text-grey">
                        No se encontraron roles
                      </q-item-section>
                    </q-item>
                  </template>
                </q-select>
              </div>
            </div>
          </div>
        </div>


        <!-- Footer -->
        <div class="row justify-end q-gutter-sm">
          <q-btn label="Cancelar" flat no-caps icon="close" class="btn-cancel" @click="$emit('cerrar')" />
          <q-btn label="Guardar Usuario" type="submit" :loading="cargando" icon="save" unelevated no-caps
            class="btn-save">
            <template #loading>
              <q-spinner-dots color="white" size="1.2em" />
            </template>
          </q-btn>
        </div>


      </q-form>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { registrarUsuario } from '../../api/usuario/usuario'
import { getEmpleadoLista } from '../../api/empleado/empleado'
import { getListRoles } from '../../api/rol/rol'

const emit = defineEmits(['registrado', 'cerrar'])
const $q = useQuasar()
const cargando = ref(false)
const verContrasena = ref(false)
const busquedaEmpleado = ref('')
const empleadoSeleccionado = ref(null)

//para los select empleados
const empleados = ref([])
const empleadosOptions = ref([])

//para los select roles
const roles = ref([])
const rolesOptions = ref([])

const formularioVacio = () => ({
  username: '',
  password: '',
  empleadoId: null,
  roles: []
})

const formulario = ref(formularioVacio())


const requisitos = computed(() => [
  { label: 'Mayúscula', cumple: /[A-Z]/.test(formulario.value.password) },
  { label: 'Minúscula', cumple: /[a-z]/.test(formulario.value.password) },
  { label: 'Número', cumple: /[0-9]/.test(formulario.value.password) },
  { label: 'Especial (!@#)', cumple: /[^A-Za-z0-9]/.test(formulario.value.password) },
])

const fuerzaValor = computed(() => {
  const cumplidos = requisitos.value.filter(r => r.cumple).length
  const longitud = formulario.value.password.length >= 6 ? 1 : 0
  return (cumplidos + longitud) / 5
})

const fuerzaLabel = computed(() => {
  const v = fuerzaValor.value
  if (v <= 0.2) return 'Muy débil'
  if (v <= 0.4) return 'Débil'
  if (v <= 0.6) return 'Regular'
  if (v <= 0.8) return 'Fuerte'
  return 'Muy fuerte'
})

const fuerzaColor = computed(() => {
  const v = fuerzaValor.value
  if (v <= 0.4) return '#e53935'
  if (v <= 0.6) return '#fb8c00'
  return '#43a047'
})

const fuerzaColorQuasar = computed(() => {
  const v = fuerzaValor.value
  if (v <= 0.4) return 'negative'
  if (v <= 0.6) return 'warning'
  return 'positive'
})


const toggleRol = (value) => {
  const idx = formulario.value.roles.indexOf(value)
  if (idx === -1) formulario.value.roles.push(value)
  else formulario.value.roles.splice(idx, 1)
}

//cargar los datos de los roles
const cargarRoles = async () => {
  try {
    const data = await getListRoles()

    roles.value = data.map(rol => ({
      label: rol.nombre.replace('ROLE_', ''), // Mostrar solo el nombre sin el prefijo
      value: rol.nombre,
      descripcion: rol.descripcion
    }))

    rolesOptions.value = [...roles.value]
    console.log(rolesOptions.value)

  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Error al cargar roles'
    })
  }
}

//filtro de roles
const filterRoles = (val, update) => {
  update(() => {
    if (val === '') {
      rolesOptions.value = [...roles.value]
      return
    }

    const needle = val.toLowerCase()
    rolesOptions.value = roles.value.filter(r => r.label.toLowerCase().includes(needle))
  })
}

//cargar los datos del empleado el id y nombre completo para el select
const cargarEmpleados = async () => {
  try {
    const data = await getEmpleadoLista()
    empleados.value = data
    empleadosOptions.value = data

  } catch (error) {
    $q.notify({ type: 'negative', message: 'Error al cargar empleados' })
  }
}

//filtro de empleados
const filterEmpleados = (val, update) => {
  update(() => {
    if (val === '') {
      empleadosOptions.value = empleados.value
      return
    }

    const needle = val.toLowerCase()
    empleadosOptions.value = empleados.value.filter(e => e.nombreCompleto.toLowerCase().includes(needle))
  })
}


onMounted(() => {
  cargarRoles()
  cargarEmpleados()
})

//guardar un nuevo usuario
const guardar = async () => {
  if (!formulario.value.username.trim()) {
    $q.notify({ type: 'warning', message: 'El nombre de usuario es requerido' })
    return
  }

  if (formulario.value.password.length < 6) {
    $q.notify({ type: 'warning', message: 'La contraseña debe tener al menos 6 caracteres' })
    return
  }

  if (!formulario.value.empleadoId) {
    $q.notify({ type: 'warning', message: 'El empleado es requerido' })
    return
  }

  if (formulario.value.roles.length === 0) {
    $q.notify({ type: 'warning', message: 'Debes asignar al menos un rol' })
    return
  }

  try {
    cargando.value = true

    await registrarUsuario(formulario.value)

    $q.notify({
      type: 'positive',
      message: 'Usuario registrado exitosamente'
    })

    formulario.value = formularioVacio()

    emit('registrado')
    emit('cerrar')

  } catch (error) {

    const mensaje =
      error.response?.data?.message ||
      'Error al registrar el usuario'

    $q.notify({
      type: 'negative',
      message: mensaje
    })

  } finally {
    cargando.value = false
  }
}

</script>

<style scoped src="../../../src/assets/styles/user/userForm.css"></style>

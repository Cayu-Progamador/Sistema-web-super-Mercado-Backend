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
              <q-input v-model="formulario.username" placeholder="ej. juan.perez" outlined dense class="custom-input"
                :error="tocado.username && !!errores.username" :error-message="errores.username"
                @blur="tocado.username = true">
                <template #prepend>
                  <q-icon name="person_outline" class="input-icon" />
                </template>
              </q-input>
              <div v-if="formulario.username && !errores.username" class="field-help q-mt-xs">
                El sistema verificará que el usuario no esté registrado al guardar.
              </div>
            </div>

            <!-- Contraseña -->
            <div class="q-mb-sm">
              <label class="field-label">Contraseña <span class="required-star">*</span></label>
              <q-input v-model="formulario.password" placeholder="Mínimo 8 caracteres"
                :type="verContrasena ? 'text' : 'password'" outlined dense class="custom-input"
                :error="tocado.password && !!errores.password" :error-message="errores.password"
                @blur="tocado.password = true">
                <template #prepend>
                  <q-icon name="lock_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon :name="verContrasena ? 'visibility_off' : 'visibility'" class="cursor-pointer input-icon"
                    @click="verContrasena = !verContrasena" />
                </template>
              </q-input>
            </div>

            <!-- Fortaleza: mismo estilo que Cambiar Contraseña -->
            <div v-if="formulario.password" class="q-mb-md">
              <div class="fortaleza-wrap q-mt-sm">
                <div class="fortaleza-label">Fortaleza de la contraseña:</div>
                <div class="fortaleza-bars">
                  <div v-for="i in 4" :key="i" class="fortaleza-bar" :class="getBarClass(i)"></div>
                </div>
                <div class="fortaleza-text" :class="fortalezaClase">{{ fuerzaLabel }}</div>
              </div>

              <div class="requisitos-password q-mt-md">
                <div class="requisitos-titulo">Requisitos de Contraseña</div>
                <div class="req-list">
                  <div v-for="req in requisitos" :key="req.label" class="req-item"
                    :class="{ 'req-ok': req.cumple }">
                    <div class="req-dot" :class="req.cumple ? 'req-dot-ok' : 'req-dot-pending'">
                      <q-icon :name="req.cumple ? 'check' : 'remove'" size="12px" color="white" />
                    </div>
                    <span class="req-texto">{{ req.label }}</span>
                  </div>
                </div>
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
                <q-select outlined dense v-model="formulario.empleadoId" :options="empleadosOptions" class="custom-input"
                  option-label="nombreCompleto" option-value="id" emit-value map-options use-input hide-selected
                  fill-input input-debounce="0" @filter="filterEmpleados" @blur="tocado.empleadoId = true"
                  :error="tocado.empleadoId && !!errores.empleadoId" :error-message="errores.empleadoId">
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
                  outlined label="Roles" class="custom-input" @blur="tocado.roles = true"
                  :error="tocado.roles && !!errores.roles" :error-message="errores.roles">
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
          <q-btn label="Guardar Usuario" type="submit" :loading="cargando" :disable="!formularioValido || cargando" icon="save" unelevated no-caps
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

const tocado = ref({
  username: false,
  password: false,
  empleadoId: false,
  roles: false
})

const errores = computed(() => ({
  username: validarUsername(formulario.value.username),
  password: validarPassword(formulario.value.password),
  empleadoId: formulario.value.empleadoId ? '' : 'Debe seleccionar un empleado.',
  roles: formulario.value.roles.length > 0 ? '' : 'Debe asignar al menos un rol.'
}))

const formularioValido = computed(() =>
  !errores.value.username &&
  !errores.value.password &&
  !errores.value.empleadoId &&
  !errores.value.roles
)

function validarUsername(username) {
  const valor = username?.trim() || ''
  if (!valor) return 'El nombre de usuario es obligatorio.'
  if (valor.length < 3 || valor.length > 50) return 'Debe tener entre 3 y 50 caracteres.'
  if (!/^[A-Za-z0-9._-]+$/.test(valor)) {
    return 'Use solo letras, números, punto, guion o guion bajo.'
  }
  return ''
}

function validarPassword(password) {
  if (!password) return 'La contraseña es obligatoria.'
  if (password.length < 8) return 'Debe tener al menos 8 caracteres.'
  if (!/[A-Z]/.test(password) || !/[a-z]/.test(password) ||
      !/[0-9]/.test(password) || !/[^A-Za-z0-9]/.test(password)) {
    return 'Incluya mayúscula, minúscula, número y carácter especial.'
  }
  return ''
}


const requisitos = computed(() => [
  { label: 'Mínimo 8 caracteres', cumple: formulario.value.password.length >= 8 },
  { label: 'Mayúscula', cumple: /[A-Z]/.test(formulario.value.password) },
  { label: 'Minúscula', cumple: /[a-z]/.test(formulario.value.password) },
  { label: 'Número', cumple: /[0-9]/.test(formulario.value.password) },
  { label: 'Carácter especial (!@#$%)', cumple: /[^A-Za-z0-9]/.test(formulario.value.password) },
])

const fuerzaValor = computed(() => {
  const cumplidos = requisitos.value.filter(r => r.cumple).length
  return cumplidos / requisitos.value.length
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

const fortalezaClase = computed(() => {
  if (fuerzaValor.value <= 0.2) return 'ft-rojo'
  if (fuerzaValor.value <= 0.4) return 'ft-naranja'
  if (fuerzaValor.value <= 0.6) return 'ft-amarillo'
  return 'ft-verde'
})

const getBarClass = (i) => {
  if (fuerzaValor.value === 0) return ''
  if (fuerzaValor.value <= 0.2) return i <= 1 ? 'bar-rojo' : ''
  if (fuerzaValor.value <= 0.4) return i <= 2 ? 'bar-naranja' : ''
  if (fuerzaValor.value <= 0.6) return i <= 3 ? 'bar-amarillo' : ''
  return 'bar-verde'
}


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
  tocado.value = {
    username: true,
    password: true,
    empleadoId: true,
    roles: true
  }

  if (!formularioValido.value) return

  try {
    cargando.value = true

    await registrarUsuario({
      ...formulario.value,
      username: formulario.value.username.trim()
    })

    $q.notify({
      type: 'positive',
      message: 'Usuario registrado exitosamente'
    })

    formulario.value = formularioVacio()
    tocado.value = {
      username: false,
      password: false,
      empleadoId: false,
      roles: false
    }

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

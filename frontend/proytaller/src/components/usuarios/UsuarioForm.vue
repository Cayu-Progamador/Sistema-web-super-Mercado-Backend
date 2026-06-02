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
            <div class="text-h6 card-title">Registrar Nuevo Usuario</div>
            <div class="text-caption text-grey-6">Complete la información para crear una nueva cuenta de usuario</div>
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

          <!-- ── Columna izquierda ── -->
          <div class="col-12 col-md-6">

            <!-- Datos de la cuenta -->
            <div class="section-label q-mb-md">
              <q-icon name="person_outline" color="primary" size="16px" class="q-mr-xs" />
              <span>Datos de la cuenta</span>
            </div>

            <!-- Username -->
            <div class="field-wrapper q-mb-md">
              <label class="field-label">Nombre de Usuario <span class="text-red">*</span></label>
              <q-input
                v-model="formulario.username"
                placeholder="ej. juan.perez"
                outlined dense
                class="custom-input"
                bg-color="input-bg"
              >
                <template #prepend>
                  <q-icon name="person_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon v-if="formulario.username" name="check_circle" color="positive" size="18px" />
                </template>
              </q-input>
              <div v-if="formulario.username" class="text-caption text-positive q-mt-xs">
                <q-icon name="check_circle" size="12px" /> Usuario disponible
              </div>
            </div>

            <!-- Contraseña -->
            <div class="field-wrapper q-mb-sm">
              <label class="field-label">Contraseña <span class="text-red">*</span></label>
              <q-input
                v-model="formulario.password"
                placeholder="Mínimo 6 caracteres"
                :type="verContrasena ? 'text' : 'password'"
                outlined dense
                class="custom-input"
                bg-color="input-bg"
              >
                <template #prepend>
                  <q-icon name="lock_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon
                    :name="verContrasena ? 'visibility_off' : 'visibility'"
                    class="cursor-pointer input-icon"
                    @click="verContrasena = !verContrasena"
                  />
                </template>
              </q-input>
            </div>

            <!-- Barra fortaleza -->
            <div v-if="formulario.password" class="q-mb-md">
              <div class="row items-center justify-between q-mb-xs">
                <span class="text-caption text-grey-6">Fortaleza de la contraseña:</span>
                <span class="text-caption text-weight-bold" :style="{ color: fuerzaColor }">{{ fuerzaLabel }}</span>
              </div>
              <q-linear-progress
                :value="fuerzaValor"
                :color="fuerzaColorQuasar"
                rounded size="7px"
                class="q-mb-sm"
              />
              <div class="row q-gutter-xs">
                <q-chip
                  v-for="req in requisitos" :key="req.label"
                  dense size="sm"
                  :icon="req.cumple ? 'check_circle' : 'radio_button_unchecked'"
                  :color="req.cumple ? 'positive' : 'grey-3'"
                  :text-color="req.cumple ? 'white' : 'grey-6'"
                >
                  {{ req.label }}
                </q-chip>
              </div>
            </div>

            <!-- Configuración -->
            <div class="section-label q-mb-md q-mt-lg">
              <q-icon name="settings" color="primary" size="16px" class="q-mr-xs" />
              <span>Configuración de la cuenta</span>
            </div>

            <div class="config-card q-pa-md q-mb-sm">
              <div class="row items-center justify-between">
                <div>
                  <div class="text-body2 text-weight-medium">Estado de la cuenta</div>
                  <div class="text-caption text-grey-6">
                    {{ formulario.activo ? 'El usuario podrá acceder al sistema' : 'El usuario no podrá acceder' }}
                  </div>
                </div>
                <q-toggle v-model="formulario.activo" color="primary" />
              </div>
              <div class="text-caption q-mt-xs" :class="formulario.activo ? 'text-positive' : 'text-grey-5'">
                <q-icon :name="formulario.activo ? 'check_circle' : 'cancel'" size="13px" class="q-mr-xs" />
                {{ formulario.activo ? 'Cuenta Activa' : 'Cuenta Inactiva' }}
              </div>
            </div>

          </div>

          <!-- ── Columna derecha ── -->
          <div class="col-12 col-md-6">

            <!-- Empleado asociado -->
            <div class="section-label q-mb-md">
              <q-icon name="badge" color="primary" size="16px" class="q-mr-xs" />
              <span>Empleado asociado</span>
            </div>

            <div class="field-wrapper q-mb-lg">
              <label class="field-label">ID del Empleado <span class="text-red">*</span></label>
              <q-input
                v-model.number="formulario.empleadoId"
                placeholder="ej. 1042"
                type="number"
                outlined dense
                class="custom-input"
                bg-color="input-bg"
              >
                <template #prepend>
                  <q-icon name="badge" class="input-icon" />
                </template>
              </q-input>
            </div>

            <!-- Roles -->
            <div class="section-label q-mb-md">
              <q-icon name="manage_accounts" color="primary" size="16px" class="q-mr-xs" />
              <span>Roles asignados</span>
            </div>

            <div class="field-wrapper q-mb-sm">
              <label class="field-label">Roles <span class="text-red">*</span></label>
              <q-select
                v-model="formulario.roles"
                :options="opcionesRoles"
                multiple emit-value map-options
                outlined dense
                class="custom-input"
                bg-color="input-bg"
                placeholder="Selecciona uno o más roles"
              >
                <template #prepend>
                  <q-icon name="manage_accounts" class="input-icon" />
                </template>
                <template #selected-item="scope">
                  <q-chip
                    removable dense
                    @remove="scope.removeAtIndex(scope.index)"
                    :label="scope.opt.label"
                    color="primary"
                    text-color="white"
                    size="sm"
                  />
                </template>
              </q-select>
            </div>

            <!-- Lista visual roles -->
            <div class="roles-list">
              <div
                v-for="opcion in opcionesRoles"
                :key="opcion.value"
                class="rol-item row items-center q-pa-sm cursor-pointer"
                :class="{ 'rol-item-selected': formulario.roles.includes(opcion.value) }"
                @click="toggleRol(opcion.value)"
              >
                <q-checkbox
                  :model-value="formulario.roles.includes(opcion.value)"
                  color="primary" dense class="q-mr-sm"
                  @click.stop
                />
                <div class="col">
                  <div class="text-body2 text-weight-medium">{{ opcion.label }}</div>
                  <div class="text-caption text-grey-6">{{ opcion.descripcion }}</div>
                </div>
              </div>
            </div>

          </div>
        </div>

        <q-separator class="custom-separator q-my-md" />

        <div class="row justify-end footer-actions q-gutter-sm">
          <q-btn label="Cancelar" flat icon="close" @click="$emit('cerrar')" class="btn-cancel" no-caps />
          <q-btn
            label="Guardar Usuario"
            type="submit"
            :loading="cargando"
            icon="save"
            unelevated no-caps
            class="btn-save"
          >
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
import { registrarUsuario, listarRoles } from '../../api/usuario/usuario'

const emit = defineEmits(['registrado', 'cerrar'])
const $q = useQuasar()
const cargando = ref(false)
const verContrasena = ref(false)
const opcionesRoles = ref([])

const formularioVacio = () => ({
  username: '',
  password: '',
  empleadoId: null,
  roles: []
})

const formulario = ref(formularioVacio())

// ── Fortaleza contraseña ─────────────────────────────
const requisitos = computed(() => [
  { label: 'Mayúscula',      cumple: /[A-Z]/.test(formulario.value.password) },
  { label: 'Minúscula',      cumple: /[a-z]/.test(formulario.value.password) },
  { label: 'Número',         cumple: /[0-9]/.test(formulario.value.password) },
  { label: 'Especial (!@#)', cumple: /[^A-Za-z0-9]/.test(formulario.value.password) },
])

const fuerzaValor = computed(() => {
  const cumplidos = requisitos.value.filter(r => r.cumple).length
  const longitud  = formulario.value.password.length >= 6 ? 1 : 0
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

// ── Roles ────────────────────────────────────────────
const descripciones = {
  ROLE_ADMIN:      'Acceso completo al sistema',
  ROLE_CAJERO:     'Procesar ventas y cobros',
  ROLE_VENDEDOR:   'Gestionar clientes y ventas',
  ROLE_SUPERVISOR: 'Monitoreo y reportes',
  ROLE_INVENTARIO: 'Gestión de productos y stock',
}

const toggleRol = (value) => {
  const idx = formulario.value.roles.indexOf(value)
  if (idx === -1) formulario.value.roles.push(value)
  else formulario.value.roles.splice(idx, 1)
}

const cargarRoles = async () => {
  try {
    const data = await listarRoles()
    opcionesRoles.value = data.map(rol => ({
      label: rol.nombre.replace('ROLE_', '').charAt(0).toUpperCase() +
             rol.nombre.replace('ROLE_', '').slice(1).toLowerCase(),
      value: rol.nombre,
      descripcion: descripciones[rol.nombre] ?? 'Sin descripción'
    }))
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cargar los roles' })
  }
}

onMounted(() => cargarRoles())

// ── Guardar ──────────────────────────────────────────
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
    $q.notify({ type: 'warning', message: 'El ID del empleado es requerido' })
    return
  }
  if (formulario.value.roles.length === 0) {
    $q.notify({ type: 'warning', message: 'Debes asignar al menos un rol' })
    return
  }

  try {
    cargando.value = true
    await register(formulario.value)
    $q.notify({ type: 'positive', message: 'Usuario registrado exitosamente' })
    formulario.value = formularioVacio()
    emit('registrado')
    emit('cerrar')
  } catch (error) {
    const mensaje = error.response?.data?.message || 'Error al registrar el usuario'
    $q.notify({ type: 'negative', message: mensaje })
  } finally {
    cargando.value = false
  }
}
</script>

<style scoped src="../../assets/styles/user/userForm.css" />

<style scoped>
.usuario-card {
  width: 820px;
  max-width: 96vw;
}

.card-header-section {
  padding: 20px 24px 14px;
}

.header-content {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.header-icon-wrap {
  width: 46px;
  height: 46px;
  border-radius: 10px;
  background: rgba(56, 142, 60, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.eyebrow {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.8px;
  text-transform: uppercase;
  color: var(--q-primary);
}

.card-title {
  font-weight: 700;
  line-height: 1.3;
}

.card-body {
  padding: 20px 24px 24px;
}

.section-label {
  display: flex;
  align-items: center;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  color: var(--q-primary);
  border-bottom: 1.5px solid #e8f5e9;
  padding-bottom: 6px;
}

.field-label {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  display: block;
  margin-bottom: 6px;
}

.config-card {
  background: #f1f8e9;
  border: 1px solid #c8e6c9;
  border-radius: 8px;
}

.roles-list {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.rol-item {
  border-bottom: 1px solid #f5f5f5;
  transition: background 0.15s;
}

.rol-item:last-child {
  border-bottom: none;
}

.rol-item:hover {
  background: #f9f9f9;
}

.rol-item-selected {
  background: #e8f5e9 !important;
}

.btn-cancel {
  border: 1px solid #ddd;
  border-radius: 8px;
  color: #555;
  padding: 6px 18px;
}

.btn-save {
  background: var(--q-primary);
  color: white;
  border-radius: 8px;
  padding: 6px 22px;
  font-weight: 600;
}
</style>
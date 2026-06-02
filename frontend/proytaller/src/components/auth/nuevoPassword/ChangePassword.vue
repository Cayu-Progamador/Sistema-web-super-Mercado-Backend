<template>
  <q-page class="perfil-page q-pa-md">

    <!-- GRID PRINCIPAL -->
    <div class="perfil-grid">

      <!-- COLUMNA IZQUIERDA -->
      <div class="col-left">
        <q-card class="perfil-card" flat>

          <!-- Título sección -->
          <div class="section-header q-mb-lg">
            <div class="section-icon">
              <q-icon name="lock" size="28px" style="color:#4a8c25" />
            </div>
            <div>
              <div class="section-title">Cambiar Contraseña</div>
              <div class="section-sub">Actualiza tu contraseña para mantener tu cuenta segura.</div>
            </div>
          </div>

          <q-form @submit.prevent="actualizarPassword" class="q-gutter-sm">

            <!-- Contraseña Actual -->
            <div class="field-group">
              <label class="field-lbl">Contraseña Actual</label>
              <q-input v-model="form.actual" outlined dense :type="mostrar.actual ? 'text' : 'password'"
                placeholder="Ingresa tu contraseña actual" class="field-input">
                <template #prepend>
                  <q-icon name="lock_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon :name="mostrar.actual ? 'visibility_off' : 'visibility'" class="eye-icon"
                    @click="mostrar.actual = !mostrar.actual" />
                </template>
              </q-input>
              <span class="field-hint">Ingresa tu contraseña actual para verificar tu identidad.</span>
            </div>

            <!-- Nueva Contraseña -->
            <div class="field-group">
              <label class="field-lbl">Nueva Contraseña</label>
              <q-input v-model="form.nueva" outlined dense :type="mostrar.nueva ? 'text' : 'password'"
                placeholder="Ingresa tu nueva contraseña" class="field-input" @update:model-value="calcularFortaleza">
                <template #prepend>
                  <q-icon name="lock_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon :name="mostrar.nueva ? 'visibility_off' : 'visibility'" class="eye-icon"
                    @click="mostrar.nueva = !mostrar.nueva" />
                </template>
              </q-input>
              <span class="field-hint">Debe contener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y
                símbolos.</span>

              <!-- Fortaleza -->
              <div class="fortaleza-wrap q-mt-sm">
                <div class="fortaleza-label">Fortaleza de la contraseña:</div>
                <div class="fortaleza-bars">
                  <div v-for="i in 4" :key="i" class="fortaleza-bar" :class="getBarClass(i)"></div>
                </div>
                <div class="fortaleza-text" :class="fortalezaColor">{{ fortalezaTexto }}</div>
              </div>
            </div>

            <!-- Confirmar Nueva Contraseña -->
            <div class="field-group">
              <label class="field-lbl">Confirmar Nueva Contraseña</label>
              <q-input v-model="form.confirmar" outlined dense :type="mostrar.confirmar ? 'text' : 'password'"
                placeholder="Repite tu nueva contraseña" class="field-input"
                :class="{ 'input-error': form.confirmar && form.nueva !== form.confirmar }">
                <template #prepend>
                  <q-icon name="lock_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon :name="mostrar.confirmar ? 'visibility_off' : 'visibility'" class="eye-icon"
                    @click="mostrar.confirmar = !mostrar.confirmar" />
                </template>
              </q-input>
              <span v-if="form.confirmar && form.nueva !== form.confirmar" class="field-error">
                Las contraseñas no coinciden.
              </span>
              <span v-else class="field-hint">Vuelve a ingresar la nueva contraseña.</span>
            </div>

            <!-- Consejo -->
            <div class="consejo-box q-mt-sm">
              <q-icon name="info" size="18px" style="color:#4a8c25;flex-shrink:0" />
              <span>
                <strong style="color:#2a5c1a">Consejo:</strong>
                <span style="color:#5a8040"> Usa una contraseña única que no hayas utilizado antes.</span>
              </span>
            </div>

            <!-- Botones -->
            <div class="row justify-end q-gutter-sm q-mt-md">
              <q-btn label="Cancelar" flat class="btn-cancel" @click="resetForm" />
              <q-btn type="submit" label="Actualizar Contraseña" icon="lock" class="btn-save" unelevated
                :loading="cargando">
                <template #loading>
                  <q-spinner-dots color="white" size="1em" />
                </template>
              </q-btn>
            </div>

          </q-form>
        </q-card>
      </div>

      <!-- COLUMNA DERECHA -->
      <div class="col-right">

        <!-- Requisitos -->
        <q-card class="perfil-card q-mb-md" flat>
          <div class="card-title-row">
            <q-icon name="checklist" class="card-title-icon" />
            Requisitos de Contraseña
          </div>

          <div class="req-list">
            <div v-for="req in requisitos" :key="req.texto" class="req-item" :class="{ 'req-ok': req.cumplido }">
              <div class="req-dot" :class="req.cumplido ? 'req-dot-ok' : 'req-dot-pending'">
                <q-icon :name="req.cumplido ? 'check' : 'remove'" size="12px" color="white" />
              </div>
              <span class="req-texto">{{ req.texto }}</span>
            </div>
          </div>
        </q-card>

        <!-- Tips de seguridad -->
        <q-card class="perfil-card" flat>
          <div class="card-title-row">
            <q-icon name="security" class="card-title-icon" />
            Tips de Seguridad
          </div>

          <div class="tips-list">
            <div class="tip-item">
              <div class="tip-icon ri-green">
                <q-icon name="shield" size="18px" style="color:#4a8c25" />
              </div>
              <div>
                <div class="tip-title">No compartas tu contraseña</div>
                <div class="tip-sub">Nunca compartas tu contraseña con otras personas.</div>
              </div>
            </div>
            <div class="tip-item">
              <div class="tip-icon ri-orange">
                <q-icon name="vpn_key" size="18px" style="color:#d97b1a" />
              </div>
              <div>
                <div class="tip-title">Usa contraseñas únicas</div>
                <div class="tip-sub">No uses la misma contraseña en diferentes sitios.</div>
              </div>
            </div>
            <div class="tip-item">
              <div class="tip-icon ri-teal">
                <q-icon name="refresh" size="18px" style="color:#0f6e56" />
              </div>
              <div>
                <div class="tip-title">Cámbiala regularmente</div>
                <div class="tip-sub">Se recomienda cambiar tu contraseña periódicamente.</div>
              </div>
            </div>
            <div class="tip-item" style="border-bottom:none">
              <div class="tip-icon ri-blue">
                <q-icon name="lock" size="18px" style="color:#185fa5" />
              </div>
              <div>
                <div class="tip-title">Cierra sesión</div>
                <div class="tip-sub">Cierra sesión en dispositivos compartidos o públicos.</div>
              </div>
            </div>
          </div>
        </q-card>

      </div>
    </div>

    <!-- Footer -->
    <div class="page-footer">© 2024 Mercat - Todos los derechos reservados</div>

  </q-page>
</template>

<script setup>
import { cambiarContrasena } from '../../../api/usuario/usuario'
import { ref, reactive, computed } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../../store/store'

const router = useRouter()
const store = useAuthStore()

const $q = useQuasar()
const cargando = ref(false)

const form = reactive({ actual: '', nueva: '', confirmar: '' })
const mostrar = reactive({ actual: false, nueva: false, confirmar: false })
const fortaleza = ref(0)

const calcularFortaleza = (val) => {
  let score = 0
  if (val.length >= 8) score++
  if (/[A-Z]/.test(val)) score++
  if (/[a-z]/.test(val)) score++
  if (/[0-9]/.test(val)) score++
  if (/[!@#$%^&*]/.test(val)) score++
  fortaleza.value = score
}

const fortalezaTexto = computed(() => {
  if (fortaleza.value <= 1) return 'Muy débil'
  if (fortaleza.value === 2) return 'Débil'
  if (fortaleza.value === 3) return 'Regular'
  if (fortaleza.value === 4) return 'Fuerte'
  return 'Muy fuerte'
})

const fortalezaColor = computed(() => {
  if (fortaleza.value <= 1) return 'ft-rojo'
  if (fortaleza.value === 2) return 'ft-naranja'
  if (fortaleza.value === 3) return 'ft-amarillo'
  return 'ft-verde'
})

const getBarClass = (i) => {
  if (fortaleza.value === 0) return ''
  if (fortaleza.value <= 1) return i <= 1 ? 'bar-rojo' : ''
  if (fortaleza.value === 2) return i <= 2 ? 'bar-naranja' : ''
  if (fortaleza.value === 3) return i <= 3 ? 'bar-amarillo' : ''
  return 'bar-verde'
}

const requisitos = computed(() => [
  { texto: 'Mínimo 8 caracteres', cumplido: form.nueva.length >= 8 },
  { texto: 'Al menos una mayúscula (A-Z)', cumplido: /[A-Z]/.test(form.nueva) },
  { texto: 'Al menos una minúscula (a-z)', cumplido: /[a-z]/.test(form.nueva) },
  { texto: 'Al menos un número (0-9)', cumplido: /[0-9]/.test(form.nueva) },
  { texto: 'Al menos un carácter especial (!@#$%)', cumplido: /[!@#$%^&*]/.test(form.nueva) }
])

const resetForm = () => {
  form.actual = ''; form.nueva = ''; form.confirmar = ''
  fortaleza.value = 0
}


const actualizarPassword = async () => {
  
  if (form.actual === form.nueva) {
    $q.notify({
      type: 'negative',
      message: 'La nueva contraseña debe ser diferente a la actual'
    })
    return
  }

  if (form.nueva !== form.confirmar) {
    $q.notify({
      type: 'negative',
      message: 'Las contraseñas no coinciden'
    })
    return
  }

  cargando.value = true

  try {

    const payload = {
      passwordActual: form.actual,
      passwordNueva: form.nueva,
      confirmarPassword: form.confirmar
    }

    const response = await cambiarContrasena(payload)

    $q.notify({
      type: 'positive',
      message: 'Contraseña actualizada correctamente. Iniciar Sesion nuevamente'
    })

    resetForm()

    setTimeout(() => {
      store.logout()
      router.push('/login')
    }, 3000)

  } catch (error) {

    $q.notify({
      type: 'negative',
      message:
        error.response.message ||
        'Error al cambiar contraseña'
    })

  } finally {

    cargando.value = false

  }
}
</script>

<style scoped src="../../../assets/styles/forgotPassword/changePassword.css"></style>
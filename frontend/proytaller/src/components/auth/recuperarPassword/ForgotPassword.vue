<template>
  <q-dialog v-model="abierto" persistent>
    <div class="recover-wrap">

      <!-- PASO 1 -->
      <div class="step-card" v-if="paso === 1">
        <div class="card-header">
          <div class="h-icon"><q-icon name="lock_open" size="20px" style="color:#4a8c25" /></div>
          <div>
            <div class="h-eyebrow">Seguridad de cuenta</div>
            <div class="h-title">Recuperar contraseña</div>
          </div>
          <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
        </div>
        <div class="card-body">
          <div class="steps-indicator">
            <div class="sdot">
              <div class="dot dot-active">1</div><span class="slbl slbl-active">Correo</span>
            </div>
            <div class="sline"></div>
            <div class="sdot">
              <div class="dot dot-pending">2</div><span class="slbl">Código</span>
            </div>
            <div class="sline"></div>
            <div class="sdot">
              <div class="dot dot-pending">3</div><span class="slbl slbl-sm">Nueva<br>clave</span>
            </div>
            <div class="sline"></div>
            <div class="sdot">
              <div class="dot dot-pending"><q-icon name="check" size="10px" /></div><span class="slbl">Listo</span>
            </div>
          </div>
          <div class="info-box">
            <q-icon name="info_outline" size="15px" style="color:#4a8c25;flex-shrink:0;margin-top:1px" />
            <p>Ingresa el <strong>correo electrónico</strong> asociado a tu cuenta y te enviaremos un código de
              verificación.</p>
          </div>
          <div class="field-group">
            <label class="field-lbl">Correo electrónico</label>
            <q-input v-model="form.correo" outlined dense placeholder="ejemplo@correo.com" class="field-input">
              <template #prepend><q-icon name="mail_outline" class="input-icon" /></template>
            </q-input>
            <span class="inp-hint">Usa el correo con el que te registraste en el sistema.</span>
          </div>
          <button class="btn-primary" :disabled="cargando" @click="enviarCodigo">
            <q-spinner-dots v-if="cargando" color="white" size="1em" />
            <q-icon v-else name="send" size="16px" />
            {{ cargando ? 'Enviando...' : 'Enviar código de verificación' }}
          </button>
          <button class="btn-outline" @click="cerrar">
            <q-icon name="arrow_back" size="15px" />Volver al inicio de sesión
          </button>
        </div>
      </div>

      <!-- PASO 2 -->
      <div class="step-card" v-if="paso === 2">
        <div class="card-header">
          <div class="h-icon"><q-icon name="lock_open" size="20px" style="color:#4a8c25" /></div>
          <div>
            <div class="h-eyebrow">Seguridad de cuenta</div>
            <div class="h-title">Recuperar contraseña</div>
          </div>
          <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
        </div>
        <div class="card-body">
          <div class="steps-indicator">
            <div class="sdot">
              <div class="dot dot-done"><q-icon name="check" size="10px" color="white" /></div><span
                class="slbl slbl-done">Correo</span>
            </div>
            <div class="sline sline-done"></div>
            <div class="sdot">
              <div class="dot dot-active">2</div><span class="slbl slbl-active">Código</span>
            </div>
            <div class="sline"></div>
            <div class="sdot">
              <div class="dot dot-pending">3</div><span class="slbl slbl-sm">Nueva<br>clave</span>
            </div>
            <div class="sline"></div>
            <div class="sdot">
              <div class="dot dot-pending"><q-icon name="check" size="10px" /></div><span class="slbl">Listo</span>
            </div>
          </div>
          <div class="info-box">
            <q-icon name="mark_email_read" size="15px" style="color:#4a8c25;flex-shrink:0;margin-top:1px" />
            <p>Hemos enviado un código de verificación a <strong>{{ form.correo }}</strong>. Revisa tu bandeja de
              entrada.</p>
          </div>
          <div class="field-group">
            <label class="field-lbl">Código de verificación</label>
            <div class="otp-row">
              <input v-for="(_, i) in otp" :key="i" v-model="otp[i]" class="otp-inp" :class="{ 'otp-first': i === 0 }"
                type="text" maxlength="1" @input="otpNext($event, i)" @keydown.backspace="otpPrev($event, i)" />
            </div>
            <p class="otp-hint">El código expira en <strong>{{ timerTexto }}</strong></p>
            <p class="resend-txt">¿No recibiste el código? <span @click="reenviarCodigo">Reenviar código</span></p>

          </div>
          <button class="btn-primary" :disabled="cargando" @click="verificarCodigo">
            <q-spinner-dots v-if="cargando" color="white" size="1em" />
            <q-icon v-else name="verified_user" size="16px" />
            {{ cargando ? 'Verificando...' : 'Verificar código' }}
          </button>
          <button class="btn-outline" @click="paso = 1">
            <q-icon name="arrow_back" size="15px" />Volver al paso anterior
          </button>
        </div>
      </div>

      <!-- PASO 3 -->
      <div class="step-card" v-if="paso === 3">
        <div class="card-header">
          <div class="h-icon"><q-icon name="lock_open" size="20px" style="color:#4a8c25" /></div>
          <div>
            <div class="h-eyebrow">Seguridad de cuenta</div>
            <div class="h-title">Recuperar contraseña</div>
          </div>
          <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
        </div>
        <div class="card-body">
          <div class="steps-indicator">
            <div class="sdot">
              <div class="dot dot-done"><q-icon name="check" size="10px" color="white" /></div><span
                class="slbl slbl-done">Correo</span>
            </div>
            <div class="sline sline-done"></div>
            <div class="sdot">
              <div class="dot dot-done"><q-icon name="check" size="10px" color="white" /></div><span
                class="slbl slbl-done">Código</span>
            </div>
            <div class="sline sline-done"></div>
            <div class="sdot">
              <div class="dot dot-active">3</div><span class="slbl slbl-active slbl-sm">Nueva<br>clave</span>
            </div>
            <div class="sline"></div>
            <div class="sdot">
              <div class="dot dot-pending"><q-icon name="check" size="10px" /></div><span class="slbl">Listo</span>
            </div>
          </div>
          <div class="info-box">
            <q-icon name="info_outline" size="15px" style="color:#4a8c25;flex-shrink:0;margin-top:1px" />
            <p>Ingresa tu nueva contraseña para completar el proceso.</p>
          </div>
          <div class="field-group">
            <label class="field-lbl">Nueva contraseña</label>
            <q-input v-model="form.nuevaPassword" outlined dense :type="verNueva ? 'text' : 'password'"
              placeholder="Mínimo 8 caracteres" class="field-input" @update:model-value="calcularFortaleza">
              <template #prepend><q-icon name="lock_outline" class="input-icon" /></template>
              <template #append>
                <q-icon :name="verNueva ? 'visibility_off' : 'visibility'" class="eye-icon"
                  @click="verNueva = !verNueva" />
              </template>
            </q-input>
            <div class="str-row">
              <span class="str-min">Mínimo 8 caracteres</span>
              <span class="str-val" :style="{ color: fortalezaColor }">{{ fortalezaTexto }}</span>
            </div>
            <div class="str-bars">
              <div v-for="i in 4" :key="i" class="sbar" :class="{ 'sbar-on': i <= fortaleza }"></div>
            </div>
          </div>
          <div class="field-group">
            <label class="field-lbl">Confirmar contraseña</label>
            <q-input v-model="form.confirmarPassword" outlined dense :type="verConfirmar ? 'text' : 'password'"
              placeholder="Repite la contraseña" class="field-input">
              <template #prepend><q-icon name="lock_outline" class="input-icon" /></template>
              <template #append>
                <q-icon :name="verConfirmar ? 'visibility_off' : 'visibility'" class="eye-icon"
                  @click="verConfirmar = !verConfirmar" />
              </template>
            </q-input>
          </div>
          <button class="btn-primary" :disabled="cargando" @click="actualizarPassword">
            <q-spinner-dots v-if="cargando" color="white" size="1em" />
            <q-icon v-else name="lock" size="16px" />
            {{ cargando ? 'Actualizando...' : 'Actualizar contraseña' }}
          </button>
          <button class="btn-outline" @click="paso = 2">
            <q-icon name="arrow_back" size="15px" />Volver al paso anterior
          </button>
        </div>
      </div>

      <!-- PASO 4 -->
      <div class="step-card" v-if="paso === 4">
        <div class="card-header">
          <div class="h-icon"><q-icon name="lock_open" size="20px" style="color:#4a8c25" /></div>
          <div>
            <div class="h-eyebrow">Seguridad de cuenta</div>
            <div class="h-title">Recuperar contraseña</div>
          </div>
        </div>
        <div class="card-body">
          <div class="steps-indicator">
            <div class="sdot">
              <div class="dot dot-done"><q-icon name="check" size="10px" color="white" /></div><span
                class="slbl slbl-done">Correo</span>
            </div>
            <div class="sline sline-done"></div>
            <div class="sdot">
              <div class="dot dot-done"><q-icon name="check" size="10px" color="white" /></div><span
                class="slbl slbl-done">Código</span>
            </div>
            <div class="sline sline-done"></div>
            <div class="sdot">
              <div class="dot dot-done"><q-icon name="check" size="10px" color="white" /></div><span
                class="slbl slbl-done slbl-sm">Nueva<br>clave</span>
            </div>
            <div class="sline sline-done"></div>
            <div class="sdot">
              <div class="dot dot-done"><q-icon name="check" size="10px" color="white" /></div><span
                class="slbl slbl-done">Listo</span>
            </div>
          </div>
          <div class="success-body">
            <div class="success-circle">
              <q-icon name="check_circle_outline" size="34px" style="color:#4a8c25" />
            </div>
            <div class="success-title">¡Contraseña actualizada!</div>
            <div class="success-sub">
              Tu contraseña ha sido actualizada correctamente.<br />
              Ahora puedes iniciar sesión con tu nueva contraseña.
            </div>
            <button class="btn-primary btn-center" @click="cerrar">
              <q-icon name="login" size="16px" />Ir al inicio de sesión
            </button>
          </div>
        </div>
      </div>

    </div>
  </q-dialog>
</template>

<script setup>
import { forgotPassword, resendCode, resetPassword, veriFyPin } from '../../../api/login/login'
//import '../../../assets/styles/forgotPassword/forgotPassword.css'
import { ref, reactive, computed, onUnmounted } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()

const abierto = ref(false)
const paso = ref(1)
const cargando = ref(false)
const verNueva = ref(false)
const verConfirmar = ref(false)
const fortaleza = ref(0)
const timerSegundos = ref(585)
let timerInterval = null

const form = reactive({
  correo: '',
  codigo: '',
  nuevaPassword: '',
  confirmarPassword: ''
})

const otp = ref(['', '', '', '', '', ''])

/* ── Expuesto al padre ── */
const openForgotPassword = () => {
  paso.value = 1
  form.correo = ''
  form.codigo = ''
  form.nuevaPassword = ''
  form.confirmarPassword = ''
  otp.value = ['', '', '', '', '', '']
  fortaleza.value = 0
  abierto.value = true
}

defineExpose({ openForgotPassword })

/* ── Cerrar ── */
const cerrar = () => {
  abierto.value = false
  clearInterval(timerInterval)
}

/* ── Timer ── */
const timerTexto = computed(() => {
  const m = Math.floor(timerSegundos.value / 60).toString().padStart(2, '0')
  const s = (timerSegundos.value % 60).toString().padStart(2, '0')
  return `${m}:${s} minutos`
})

const iniciarTimer = () => {
  timerSegundos.value = 585
  clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    if (timerSegundos.value > 0) timerSegundos.value--
    else clearInterval(timerInterval)
  }, 1000)
}

onUnmounted(() => clearInterval(timerInterval))

/* ── OTP ── */
const otpNext = (e, i) => {
  if (e.target.value && i < 5) {
    const inputs = document.querySelectorAll('.otp-inp')
    inputs[i + 1]?.focus()
  }
}
const otpPrev = (e, i) => {
  if (!otp.value[i] && i > 0) {
    const inputs = document.querySelectorAll('.otp-inp')
    inputs[i - 1]?.focus()
  }
}

/* ── Fortaleza ── */
const calcularFortaleza = (val) => {
  let score = 0
  if (val.length >= 8) score++
  if (/[A-Z]/.test(val)) score++
  if (/[0-9]/.test(val)) score++
  if (/[!@#$%^&*]/.test(val)) score++
  fortaleza.value = score
}

const fortalezaTexto = computed(() => {
  if (fortaleza.value <= 1) return 'Muy débil'
  if (fortaleza.value === 2) return 'Débil'
  if (fortaleza.value === 3) return 'Fuerte'
  return 'Muy fuerte'
})

const fortalezaColor = computed(() => {
  if (fortaleza.value <= 1) return '#ef4444'
  if (fortaleza.value === 2) return '#d97b1a'
  return '#4a8c25'
})

/* ── Acciones enviar al correo codigo */
const enviarCodigo = async () => {
  if (!form.correo) {
    $q.notify({
      type: 'warning',
      message: 'Ingresa tu correo electrónico'
    })
    return
  }
  cargando.value = true
  try {
    await forgotPassword({
      email: form.correo
    })

    iniciarTimer()
    paso.value = 2

    $q.notify({
      type: 'positive',
      message: `Código enviado a ${form.correo}`
    })

  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'No se pudo enviar el código. Verifica el correo.'
    })
    console.log(error)

  } finally {
    cargando.value = false
  }

}
//metodo para reenviar el codigo de recuperacion al correo 
const reenviarCodigo = async () => {
  cargando.value = true
  try {
    await resendCode({
      email: form.correo
    })
    iniciarTimer()
    $q.notify({ type: 'positive', message: 'Código reenviado exitosamente' })
  } catch (error) {
    $q.notify({ type: 'negative', message: 'No se pudo reenviar el código' })
    console.log(error)

  } finally {
    cargando.value = false
  }
}


//metodod para verificar el codigo de recuperacion enviado en el correo del usuario
const verificarCodigo = async () => {
  const codigo = otp.value.join('')
  if (codigo.length < 6) {
    $q.notify({ type: 'warning', message: 'Ingresa el código completo de 6 dígitos' })
    return
  }
  cargando.value = true
  try {
    // await verificarCodigoRecuperacion({ correo: form.correo, codigo })
    await veriFyPin({
      email: form.correo,
      pin: codigo
    })

    form.codigo = codigo

    clearInterval(timerInterval)
    paso.value = 3

    $q.notify({ type: 'positive', message: 'Código verificado correctamente' })
  } catch {
    $q.notify({ type: 'negative', message: 'Código incorrecto o expirado' })
  } finally {
    cargando.value = false
  }
}

//metodo para actualizar la contraseña
const actualizarPassword = async () => {
  if (form.nuevaPassword !== form.confirmarPassword) {
    $q.notify({ type: 'negative', message: 'Las contraseñas no coinciden' })
    return
  }
  if (form.nuevaPassword.length < 8) {
    $q.notify({ type: 'warning', message: 'La contraseña debe tener mínimo 8 caracteres' })
    return
  }
  cargando.value = true
  try {
    // await cambiarPassword({ correo: form.correo, nuevaPassword: form.nuevaPassword })
    await resetPassword({
      email: form.correo,
      pin: form.codigo,
      newPassword: form.nuevaPassword

    })

    paso.value = 4
    
    $q.notify({ type: 'positive', message: '¡Contraseña actualizada exitosamente!' })
  } catch {
    $q.notify({ type: 'negative', message: 'Error al actualizar la contraseña' })
  
  } finally {
    cargando.value = false
  }
}
</script>

<style scoped src="../../../assets/styles/forgotPassword/forgotPassword.css"></style>
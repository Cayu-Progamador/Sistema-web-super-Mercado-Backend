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
  nuevaPassword: '',
  confirmarPassword: ''
})

const otp = ref(['', '', '', '', '', ''])

/* ── Expuesto al padre ── */
const openForgotPassword = () => {
  paso.value = 1
  form.correo = ''
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

/* ── Acciones — conecta tu API aquí ── */
const enviarCodigo = async () => {
  if (!form.correo) {
    $q.notify({ type: 'warning', message: 'Ingresa tu correo electrónico' })
    return
  }
  cargando.value = true
  try {
    // await solicitarCodigoRecuperacion({ correo: form.correo })
    await new Promise(r => setTimeout(r, 1000)) // simulación
    iniciarTimer()
    paso.value = 2
    $q.notify({ type: 'positive', message: `Código enviado a ${form.correo}` })
  } catch {
    $q.notify({ type: 'negative', message: 'No se pudo enviar el código. Verifica el correo.' })
  } finally {
    cargando.value = false
  }
}

const reenviarCodigo = async () => {
  cargando.value = true
  try {
    // await solicitarCodigoRecuperacion({ correo: form.correo })
    await new Promise(r => setTimeout(r, 800))
    iniciarTimer()
    $q.notify({ type: 'positive', message: 'Código reenviado exitosamente' })
  } finally {
    cargando.value = false
  }
}

const verificarCodigo = async () => {
  const codigo = otp.value.join('')
  if (codigo.length < 6) {
    $q.notify({ type: 'warning', message: 'Ingresa el código completo de 6 dígitos' })
    return
  }
  cargando.value = true
  try {
    // await verificarCodigoRecuperacion({ correo: form.correo, codigo })
    await new Promise(r => setTimeout(r, 1000))
    clearInterval(timerInterval)
    paso.value = 3
    $q.notify({ type: 'positive', message: 'Código verificado correctamente' })
  } catch {
    $q.notify({ type: 'negative', message: 'Código incorrecto o expirado' })
  } finally {
    cargando.value = false
  }
}

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
    await new Promise(r => setTimeout(r, 1000))
    paso.value = 4
    $q.notify({ type: 'positive', message: '¡Contraseña actualizada exitosamente!' })
  } catch {
    $q.notify({ type: 'negative', message: 'Error al actualizar la contraseña' })
  } finally {
    cargando.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

.recover-wrap {
  width: 100%;
  max-width: 460px;
  font-family: 'Nunito', sans-serif;
}

/* ── Card ── */
.step-card {
  background: #ffffff;
  border: 1.5px solid #e4edd8;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(74, 140, 37, 0.12);
}

/* ── Header ── */
.card-header {
  background: #f7f9f4;
  border-bottom: 1px solid #e4edd8;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.h-icon {
  width: 42px;
  height: 42px;
  border-radius: 11px;
  background: #eaf4d8;
  border: 1.5px solid #c8e0a0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.h-eyebrow {
  font-size: 9px;
  font-weight: 700;
  color: #9dbf78;
  text-transform: uppercase;
  letter-spacing: 0.13em;
  margin-bottom: 2px;
  font-family: 'Nunito', sans-serif;
}

.h-title {
  font-size: 15px;
  font-weight: 900;
  color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}

.close-btn {
  margin-left: auto;
  color: #9dbf78 !important;
  background: #f0f7e8 !important;
  border-radius: 8px !important;
}

.close-btn:hover {
  background: #ddecc5 !important;
  color: #4a8c25 !important;
}

/* ── Body ── */
.card-body {
  padding: 18px 20px 22px;
}

/* ── Steps ── */
.steps-indicator {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.sdot {
  display: flex;
  align-items: center;
  gap: 5px;
}

.dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 900;
  flex-shrink: 0;
  font-family: 'Nunito', sans-serif;
}

.dot-active {
  background: #4a8c25;
  color: #fff;
}

.dot-pending {
  background: #e4edd8;
  color: #9dbf78;
}

.dot-done {
  background: #4a8c25;
  color: #fff;
}

.slbl {
  font-size: 10px;
  font-weight: 700;
  color: #9dbf78;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  line-height: 1.2;
  font-family: 'Nunito', sans-serif;
}

.slbl-active {
  color: #2a5c1a;
}

.slbl-done {
  color: #7aaa4e;
}

.slbl-sm {
  font-size: 9px;
}

.sline {
  flex: 1;
  height: 1.5px;
  background: #e4edd8;
  margin: 0 5px;
}

.sline-done {
  background: #c8e0a0;
}

/* ── Info box ── */
.info-box {
  display: flex;
  align-items: flex-start;
  gap: 9px;
  background: #f0f7e8;
  border: 1px solid #c8e0a0;
  border-radius: 9px;
  padding: 10px 12px;
  margin-bottom: 14px;
}

.info-box p {
  font-size: 12px;
  font-weight: 600;
  color: #5a8040;
  line-height: 1.5;
  font-family: 'Nunito', sans-serif;
}

.info-box p strong {
  color: #2a5c1a;
  font-weight: 800;
}

/* ── Fields ── */
.field-group {
  margin-bottom: 4px;
}

.field-lbl {
  display: block;
  font-size: 10px;
  font-weight: 800;
  color: #7aaa4e;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 5px;
  font-family: 'Nunito', sans-serif;
}

.inp-hint {
  display: block;
  font-size: 12px;
  font-weight: 500;
  color: #b1e760;
  margin-bottom: 14px;
  font-family: 'Nunito', sans-serif;
}

/* Quasar input overrides */
.field-input :deep(.q-field__control) {
  background: #fbfdf8 !important;
  border: 1.5px solid #ddecc5 !important;
  border-radius: 9px !important;
  box-shadow: none !important;
}

.field-input :deep(.q-field__control::before),
.field-input :deep(.q-field__control::after) {
  display: none !important;
}

.field-input :deep(.q-field--focused .q-field__control) {
  border-color: #4a8c25 !important;
  box-shadow: 0 0 0 3px rgba(74, 140, 37, 0.1) !important;
}

.field-input :deep(.q-field__native) {
  color: #2a5c1a !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 13px !important;
  font-weight: 700 !important;
}

.field-input :deep(input::placeholder) {
  color: #95ce39 !important;
  font-weight: 700 !important;
}

.field-input :deep(.q-field__label) {
  display: none !important;
}

.field-input :deep(.q-focus-helper) {
  display: none !important;
}

.input-icon {
  color: #bde483 !important;
  font-size: 18px !important;
}

.eye-icon {
  color: #9dbf78 !important;
  font-size: 18px !important;
  cursor: pointer;
}

.eye-icon:hover {
  color: #4a8c25 !important;
}

/* ── OTP ── */
.otp-row {
  display: flex;
  gap: 7px;
  margin-bottom: 5px;
}

.otp-inp {
  width: 42px;
  height: 48px;
  border: 1.5px solid #ddecc5;
  border-radius: 9px;
  background: #fbfdf8;
  text-align: center;
  font-family: 'Nunito', sans-serif;
  font-size: 18px;
  font-weight: 900;
  color: #2a5c1a;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.otp-inp:focus {
  border-color: #4a8c25;
  box-shadow: 0 0 0 3px rgba(74, 140, 37, 0.12);
}

.otp-first {
  border-color: #4a8c25;
  box-shadow: 0 0 0 3px rgba(74, 140, 37, 0.12);
}

.otp-hint {
  font-size: 11px;
  font-weight: 600;
  color: #9dbf78;
  margin-bottom: 6px;
  font-family: 'Nunito', sans-serif;
}

.otp-hint strong {
  color: #2a5c1a;
}

.resend-txt {
  font-size: 12px;
  font-weight: 600;
  color: #9dbf78;
  margin-bottom: 14px;
  font-family: 'Nunito', sans-serif;
}

.resend-txt span {
  color: #d97b1a;
  font-weight: 800;
  cursor: pointer;
}

.resend-txt span:hover {
  text-decoration: underline;
}

/* ── Fortaleza ── */
.str-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 4px 0;
}

.str-min {
  font-size: 10.7px;
  font-weight: 600;
  color: #bbe977;
  font-family: 'Nunito', sans-serif;
}

.str-val {
  font-size: 10.5px;
  font-weight: 800;
  font-family: 'Nunito', sans-serif;
}

.str-bars {
  display: flex;
  gap: 4px;
  margin-bottom: 14px;
}

.sbar {
  flex: 1;
  height: 3px;
  border-radius: 3px;
  background: #e4edd8;
  transition: background 0.3s;
}

.sbar-on {
  background: #4a8c25;
}

/* ── Botones ── */
.btn-primary {
  width: 100%;
  padding: 12px 16px;
  background: #4a8c25;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-family: 'Nunito', sans-serif;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 10px;
  transition: background 0.2s, transform 0.15s;
}

.btn-primary:hover:not(:disabled) {
  background: #3d7a1e;
  transform: translateY(-1px);
}

.btn-primary:active {
  transform: translateY(0);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-center {
  max-width: 260px;
  margin: 0 auto;
}

.btn-outline {
  width: 100%;
  padding: 11px 16px;
  background: #fff;
  color: #4a4a4a;
  border: 1.5px solid #d0d0d0;
  border-radius: 8px;
  font-family: 'Nunito', sans-serif;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.btn-outline:hover {
  background: #f7f7f7;
  border-color: #bbb;
}

/* ── Éxito ── */
.success-body {
  text-align: center;
  padding: 10px 0 6px;
}

.success-circle {
  width: 76px;
  height: 76px;
  border-radius: 50%;
  background: #eaf4d8;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 14px;
}

.success-title {
  font-size: 18px;
  font-weight: 900;
  color: #2a5c1a;
  margin-bottom: 6px;
  font-family: 'Nunito', sans-serif;
}

.success-sub {
  font-size: 12.8px;
  font-weight: 600;
  color: #89c24d;
  line-height: 1.6;
  margin-bottom: 18px;
  font-family: 'Nunito', sans-serif;
}
</style>
<template>
  <q-dialog v-model="dialog" persistent maximized transition-show="jump-down" transition-hide="jump-up">
    <div class="full-width full-height flex flex-center login-bg">

      <q-card class="login-card" flat>
        <div class="row no-wrap full-height">

          <!-- PANEL IZQUIERDO -->
          <div class="left-panel column justify-between">

            <!-- Decoración top -->
            <div class="deco-top">
              <div class="deco-circle deco-circle-1"></div>
              <div class="deco-circle deco-circle-2"></div>
            </div>

            <!-- Contenido central -->
            <div class="left-content column justify-center q-px-xl q-py-lg">

              <!-- Logo -->
              <div class="left-logo-wrap q-mb-lg">
                <div class="left-logo-icon">
                  <svg width="36" height="36" viewBox="0 0 80 80" fill="none">
                    <path d="M18 22 Q18 18 22 18 L58 18" stroke="#c8e0a0" stroke-width="5" stroke-linecap="round" fill="none"/>
                    <path d="M22 18 L28 50 Q29 54 33 54 L58 54 Q62 54 63 50 L68 28 L24 28" stroke="#9fe1cb" stroke-width="4.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
                    <path d="M36 44 L42 34 L48 44" stroke="#d97b1a" stroke-width="5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
                    <path d="M33 38 L39 28 L45 38" stroke="#f5a030" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
                    <circle cx="36" cy="60" r="5" fill="#9fe1cb"/>
                    <circle cx="56" cy="60" r="4" fill="#5DCAA5"/>
                    <path d="M50 18 Q50 10 54 8 Q52 14 58 12 Q56 18 50 18Z" fill="#c8e0a0"/>
                    <path d="M56 16 Q60 8 65 8 Q62 14 66 13 Q63 19 56 16Z" fill="#eaf4d8"/>
                  </svg>
                </div>
                <span class="left-brand">Mercat</span>
              </div>

              <div class="left-title">{{ lorem.split(' ').slice(0,2).join(' ') || 'Bienvenido' }}</div>
              <div class="left-title-main">Sistema de<br/>Gestión</div>
              <div class="orange-bar"></div>
              <div class="left-desc">{{ lorem }}</div>

              <!-- Tags -->
            </div>

            <!-- Decoración bottom -->
            <div class="deco-bottom">
              <div class="deco-circle deco-circle-3"></div>
            </div>

          </div>

          <!-- PANEL DERECHO -->
          <div class="right-panel column flex-center">

            <!-- Header -->
            <div class="text-center q-mb-xl">
              <div class="right-eyebrow">Bienvenido de vuelta</div>
              <div class="right-title">Inicio de Sesión</div>
              <div class="right-sub">Ingresa tus credenciales para continuar</div>
            </div>

            <!-- FORMULARIO -->
            <q-form @submit="submitLogin" class="q-gutter-md login-form">
              <div class="form__container">

                <div class="field-wrap">
                  <label class="field-label">Usuario</label>
                  <q-input
                    v-model="form.username"
                    outlined
                    dense
                    placeholder="Ingresa tu usuario"
                    :rules="[val => !!val || 'Ingrese su usuario']"
                    class="custom-input"
                  >
                    <template v-slot:prepend>
                      <q-icon name="person" class="icons" />
                    </template>
                  </q-input>
                </div>

                <div class="field-wrap">
                  <label class="field-label">Contraseña</label>
                  <q-input
                    v-model="form.password"
                    outlined
                    dense
                    placeholder="Ingresa tu contraseña"
                    :type="isPwd ? 'password' : 'text'"
                    :rules="[val => !!val || 'Ingrese su contraseña']"
                    class="custom-input"
                  >
                    <template v-slot:prepend>
                      <q-icon name="lock" class="icons" />
                    </template>
                    <template v-if="form.password" v-slot:append>
                      <q-icon
                        :name="isPwd ? 'visibility_off' : 'visibility'"
                        class="cursor-pointer eye"
                        @click="isPwd = !isPwd"
                      />
                    </template>
                  </q-input>
                </div>

                <div class="forgot-row">
                  <span class="forgot-link">¿Olvidaste tu contraseña?</span>
                </div>

                <q-btn
                  type="submit"
                  label="Ingresar al sistema"
                  class="btn-login full-width"
                  unelevated
                  :loading="isLoading"
                >
                  <template v-slot:loading>
                    <q-spinner-ios color="white" size="1.5em" />
                  </template>
                </q-btn>

              </div>

              <p v-if="errorMsg" class="text-negative text-center q-mt-sm">
                {{ errorMsg }}
              </p>

            </q-form>

           

          </div>
        </div>
      </q-card>
    </div>
  </q-dialog>
</template>

<script setup>
import { ref, reactive } from "vue"
import { useRouter } from "vue-router"
import { login } from '../../api/login/login'
import { useAuthStore } from "../../store/store"

const router = useRouter()
const authStore = useAuthStore()

const dialog = ref(true)
const isLoading = ref(false)
const errorMsg = ref("")
const isPwd = ref(true)

const lorem = 'Administra tu supermercado de forma simple, rápida y segura desde un solo lugar.'

const form = reactive({
  username: "",
  password: ""
})

const submitLogin = async () => {
  errorMsg.value = ""
  isLoading.value = true
  try {
    const respuesta = await login({
      username: form.username,
      password: form.password
    })
    authStore.login(respuesta.token, {
      username: respuesta.username,
      nombreCompleto: respuesta.nombreCompleto
    })
    router.push("/")
  } catch (error) {
    errorMsg.value = error.respuesta?.data?.message || "Usuario o contraseña incorrectos"
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

/* ── Fondo ── */
.login-bg {
  background: #f0f5eb !important;
}

/* ── Card ── */
.login-card {
  width: 900px;
  max-width: 94vw;
  height: 560px;
  border-radius: 28px !important;
  overflow: hidden;
  box-shadow: 0 24px 80px rgba(42, 92, 26, 0.18), 0 4px 16px rgba(0,0,0,0.06) !important;
  font-family: 'Nunito', sans-serif;
  border: none !important;
}

/* ════════════════════════════
   PANEL IZQUIERDO
════════════════════════════ */
.left-panel {
  width: 42%;
  background: #2a5c1a;
  position: relative;
  overflow: hidden;
  padding: 0;
}

/* Curva derecha */
.left-panel::after {
  content: "";
  position: absolute;
  top: 0;
  right: -70px;
  width: 140px;
  height: 100%;
  background: #2a5c1a;
  border-radius: 50%;
  z-index: 1;
}

/* Decoración círculos */
.deco-top { position: absolute; top: 0; left: 0; z-index: 0; }
.deco-bottom { position: absolute; bottom: 0; left: 0; z-index: 0; }

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.07;
  background: #ffffff;
}
.deco-circle-1 { width: 260px; height: 260px; top: -80px; left: -80px; }
.deco-circle-2 { width: 140px; height: 140px; top: 60px; right: -20px; opacity: 0.04; }
.deco-circle-3 { width: 200px; height: 200px; bottom: -80px; left: -60px; }

/* Contenido izquierdo */
.left-content {
  position: relative;
  z-index: 2;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 48px 44px 48px 40px;
}

/* Logo en panel izquierdo */
.left-logo-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.left-logo-icon {
  width: 52px;
  height: 52px;
  background: rgba(255,255,255,0.1);
  border-radius: 14px;
  border: 1px solid rgba(255,255,255,0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}

.left-brand {
  font-family: 'Nunito', sans-serif;
  font-size: 22px;
  font-weight: 900;
  color: #ffffff;
  letter-spacing: -0.5px;
}

/* Títulos */
.left-title {
  font-family: 'Nunito', sans-serif;
  font-size: 12px;
  font-weight: 700;
  color: rgba(255,255,255,0.5);
  text-transform: uppercase;
  letter-spacing: 0.14em;
  margin-bottom: 6px;
}

.left-title-main {
  font-family: 'Nunito', sans-serif;
  font-size: 28px;
  font-weight: 900;
  color: #ffffff;
  line-height: 1.2;
  letter-spacing: -0.5px;
}

.orange-bar {
  width: 40px;
  height: 3.5px;
  background: #d97b1a;
  border-radius: 2px;
  margin: 14px 0;
}

.left-desc {
  font-family: 'Nunito', sans-serif;
  font-size: 13.5px;
  color: rgba(255,255,255,0.65);
  line-height: 1.7;
  font-weight: 500;
}

/* Tags */
.left-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.left-tag {
  font-family: 'Nunito', sans-serif;
  font-size: 11px;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 20px;
  background: rgba(255,255,255,0.1);
  color: rgba(255,255,255,0.8);
  border: 1px solid rgba(255,255,255,0.15);
  letter-spacing: 0.04em;
}

.left-tag-orange {
  background: rgba(217,123,26,0.2);
  color: #f5b96e;
  border-color: rgba(217,123,26,0.3);
}

/* ════════════════════════════
   PANEL DERECHO
════════════════════════════ */
.right-panel {
  width: 58%;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 48px 56px;
  position: relative;
  z-index: 2;
}

/* Línea de acento top */
.right-panel::before {
  content: "";
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 3px;
  background: linear-gradient(90deg, #4a8c25, #7aaa4e, #d97b1a);
}

/* Eyebrow */
.right-eyebrow {
  font-family: 'Nunito', sans-serif;
  font-size: 11px;
  font-weight: 700;
  color: #9dbf78;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  margin-bottom: 6px;
}

/* Título derecho */
.right-title {
  font-family: 'Nunito', sans-serif;
  font-size: 26px;
  font-weight: 900;
  color: #2a5c1a;
  letter-spacing: -0.3px;
  line-height: 1.2;
}

/* Subtítulo */
.right-sub {
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  font-weight: 500;
  color: #bdd49a;
  margin-top: 4px;
}

/* Formulario */
.login-form {
  width: 100%;
  max-width: 320px;
}

.form__container {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

/* Field wrap con label externo */
.field-wrap {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-bottom: 6px;
}

.field-label {
  font-family: 'Nunito', sans-serif;
  font-size: 10px;
  font-weight: 700;
  color: #87bb56;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

/* Olvidaste contraseña */
.forgot-row {
  text-align: right;
  margin-bottom: 8px;
  margin-top: -2px;
}

.forgot-link {
  font-family: 'Nunito', sans-serif;
  font-size: 12px;
  font-weight: 700;
  color: #d97b1a;
  cursor: pointer;
  transition: color 0.2s;
}
.forgot-link:hover { color: #b8631a; }

/* ── Inputs ── */
.custom-input :deep(.q-field__control) {
  background: #fbfdf8 !important;
  border: 1px solid #ddecc5 !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  font-family: 'Nunito', sans-serif !important;
  height: 40px !important;
}

.custom-input :deep(.q-field__control::before),
.custom-input :deep(.q-field__control::after) {
  display: none !important;
}

.custom-input :deep(.q-field--focused .q-field__control) {
  background: #ffffff !important;
  border: 1.5px solid #4a8c25 !important;
  box-shadow: 0 0 0 3px rgba(74, 140, 37, 0.1) !important;
}

.custom-input :deep(.q-focus-helper),
.custom-input :deep(.q-ripple) {
  display: none !important;
}

.custom-input :deep(.q-field__native) {
  color: #2a5c1a !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 14px !important;
  font-weight: 600 !important;
}

.custom-input :deep(input::placeholder) {
  color: #c8e0a0 !important;
  font-weight: 500 !important;
}

.custom-input :deep(.q-field__label) {
  display: none !important;
}

/* ── Íconos ── */
.icons {
  color: #bad68f !important;
  font-size: 20px;
  transition: color 0.1s;
}

.custom-input :deep(.q-field--focused) .icons {
  color: #4a8c25 !important;
}

.eye {
  color: #bdd49a;
  transition: color 0.2s;
}
.eye:hover { color: #4a8c25; }

/* ── Botón ── */
.btn-login {
  height: 40px !important;
  border-radius: 12px !important;
  background: #4a8c25 !important;
  color: #ffffff !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 15px !important;
  font-weight: 800 !important;
  letter-spacing: 0.03em !important;
  transition: all 0.2s !important;
}

.btn-login:hover {
  background: #3d7a1e !important;
  transform: translateY(-1px) !important;
}

.btn-login:active {
  transform: translateY(0) !important;
}

/* ── Error ── */
.text-negative {
  font-family: 'Nunito', sans-serif !important;
  font-weight: 700 !important;
  font-size: 13px !important;
  color: #b91c1c !important;
}


/* ── Transiciones ── */
.login-card,
.left-panel,
.right-panel {
  transition: all 0.3s ease;
}
</style>
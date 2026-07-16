<template>
  <q-dialog v-model="dialog" persistent maximized transition-show="jump-down" transition-hide="jump-up">
    <div class="full-width full-height flex flex-center login-bg">

      <q-card class="login-card" flat>
        <div class="row no-wrap full-height">

          <div class="left-panel column justify-between">

            <div class="deco-top">
              <div class="deco-circle deco-circle-1"></div>
              <div class="deco-circle deco-circle-2"></div>
            </div>

            <div class="left-content column justify-center q-px-xl q-py-lg">

              <div class="left-logo-wrap q-mb-lg">
                <div class="left-logo-icon">
                  <svg width="36" height="36" viewBox="0 0 80 80" fill="none">
                    <path d="M18 22 Q18 18 22 18 L58 18" stroke="#c8e0a0" stroke-width="5" stroke-linecap="round"
                      fill="none" />
                    <path d="M22 18 L28 50 Q29 54 33 54 L58 54 Q62 54 63 50 L68 28 L24 28" stroke="#9fe1cb"
                      stroke-width="4.5" stroke-linecap="round" stroke-linejoin="round" fill="none" />
                    <path d="M36 44 L42 34 L48 44" stroke="#d97b1a" stroke-width="5" stroke-linecap="round"
                      stroke-linejoin="round" fill="none" />
                    <path d="M33 38 L39 28 L45 38" stroke="#f5a030" stroke-width="3.5" stroke-linecap="round"
                      stroke-linejoin="round" fill="none" />
                    <circle cx="36" cy="60" r="5" fill="#9fe1cb" />
                    <circle cx="56" cy="60" r="4" fill="#5DCAA5" />
                    <path d="M50 18 Q50 10 54 8 Q52 14 58 12 Q56 18 50 18Z" fill="#c8e0a0" />
                    <path d="M56 16 Q60 8 65 8 Q62 14 66 13 Q63 19 56 16Z" fill="#eaf4d8" />
                  </svg>
                </div>
                <span class="left-brand">Mercat</span>
              </div>

              <div class="left-title">{{ lorem.split(' ').slice(0, 2).join(' ') || 'Bienvenido' }}</div>
              <div class="left-title-main">Sistema de<br />Gestión</div>
              <div class="orange-bar"></div>
              <div class="left-desc">{{ lorem }}</div>

              </div>

            <div class="deco-bottom">
              <div class="deco-circle deco-circle-3"></div>
            </div>

          </div>

          <div class="right-panel column flex-center">

            <div class="text-center q-mb-xl">
              <div class="right-eyebrow">Bienvenido de vuelta</div>
              <div class="right-title">Inicio de Sesión</div>
              <div class="right-sub">Ingresa tus credenciales para continuar</div>
            </div>

            <q-form @submit="submitLogin" class="q-gutter-md login-form">
              <div class="form__container">

                <div class="field-wrap">
                  <label class="field-label">Usuario</label>
                  <q-input v-model="form.username" outlined dense placeholder="Ingresa tu usuario"
                    :rules="[val => !!val || 'Ingrese su usuario']" class="custom-input">
                    <template v-slot:prepend>
                      <q-icon name="person" class="icons" />
                    </template>
                  </q-input>
                </div>

                <div class="field-wrap">
                  <label class="field-label">Contraseña</label>
                  <q-input v-model="form.password" outlined dense placeholder="Ingresa tu contraseña"
                    :type="isPwd ? 'password' : 'text'" :rules="[val => !!val || 'Ingrese su contraseña']"
                    class="custom-input">
                    <template v-slot:prepend>
                      <q-icon name="lock" class="icons" />
                    </template>
                    <template v-if="form.password" v-slot:append>
                      <q-icon :name="isPwd ? 'visibility_off' : 'visibility'" class="cursor-pointer eye"
                        @click="isPwd = !isPwd" />
                    </template>
                  </q-input>
                </div>

                <div class="forgot-row">
                  <a class="forgot-link" @click="forgotRef?.openForgotPassword()">
                    ¿Olvidaste tu contraseña?
                  </a>
                </div>

                <q-btn 
                  type="submit" 
                  :label="bloqueado ? `Intente en ${segundosRestantes}s` : 'Ingresar al sistema'" 
                  class="btn-login full-width" 
                  unelevated
                  :loading="isLoading"
                  :disable="bloqueado"
                  :color="bloqueado ? 'grey-6' : 'primary'"
                >
                  <template v-slot:loading>
                    <q-spinner-ios color="white" size="1.5em" />
                  </template>
                </q-btn>

              </div>

              <p v-if="errorMsg" class="text-negative text-center q-mt-sm text-weight-bold">
                {{ errorMsg }}
              </p>

            </q-form>

          </div>
        </div>
      </q-card>
      <ForgotPassword ref="forgotRef" />
    </div>
  </q-dialog>
</template>

<script setup>
import { ref, reactive, onUnmounted } from "vue"
import { useRouter } from "vue-router"
import { login } from '../../api/login/login'
import { useAuthStore } from "../../store/store"
import ForgotPassword from '../../components/auth/recuperarPassword/ForgotPassword.vue'

const router = useRouter()
const authStore = useAuthStore()
const dialog = ref(true)
const isLoading = ref(false)
const errorMsg = ref("")
const isPwd = ref(true)
const forgotRef = ref(null)

// --- VARIABLES PARA EL BLOQUEO ---
const bloqueado = ref(false)
const segundosRestantes = ref(0)
let temporizador = null

const lorem = 'Administra tu supermercado de forma simple, rápida y segura desde un solo lugar.'

const form = reactive({
  username: "",
  password: ""
})

// --- FUNCIÓN DEL TEMPORIZADOR ---
const iniciarContador = (segundos) => {
  bloqueado.value = true
  segundosRestantes.value = segundos

  // Limpiar cualquier temporizador previo
  if (temporizador) clearInterval(temporizador)

  temporizador = setInterval(() => {
    segundosRestantes.value--
    
    if (segundosRestantes.value <= 0) {
      clearInterval(temporizador)
      bloqueado.value = false
      errorMsg.value = "" // Limpiar el error para que intente de nuevo
    }
  }, 1000)
}

// Limpiar el intervalo si el usuario cambia de página mientras está bloqueado
onUnmounted(() => {
  if (temporizador) clearInterval(temporizador)
})

const submitLogin = async () => {
  // Evitar que se envíe si está bloqueado
  if (bloqueado.value) return 

  errorMsg.value = ""
  isLoading.value = true
  
  try {
    const respuesta = await login({
      username: form.username,
      password: form.password
    })
    
    authStore.login(respuesta.token, {
      username: respuesta.username,
      nombreCompleto: respuesta.nombreCompleto,
      cargo: respuesta.cargo
    })
    router.push("/")
    
  } catch (error) {
    // 1. Extraer el mensaje correctamente (Axios lo guarda en error.response.data.message)
    const mensajeError = error.response?.data?.message || "Usuario o contraseña incorrectos"
    
    errorMsg.value = mensajeError

    // 2. Buscar si el mensaje contiene el tiempo de bloqueo (agregamos la 'i' para ignorar mayúsculas)
    const match = String(mensajeError).match(/(?:en|por)\s+(\d+)\s+segundos/i)
    
    if (match) {
      // Extraer el número y arrancar el contador
      const segundos = parseInt(match[1], 10)
      iniciarContador(segundos)
    }

  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped src="../../assets/styles/login/login.css">
</style>
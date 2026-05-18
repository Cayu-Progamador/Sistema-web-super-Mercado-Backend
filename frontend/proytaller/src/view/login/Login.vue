<template>
  <q-dialog v-model="dialog" persistent maximized transition-show="jump-down" transition-hide="jump-up">
    <div class="full-width full-height flex flex-center bg-grey-2">

      <q-card class="bg-white card" flat bordered>
        <div class="row no-wrap">

          <!-- PANEL IZQUIERDO -->
          <div class="column justify-center text-white q-pa-xl left-panel">
            <div class="text-h5 text-bold">Bienvenido</div>
            <div class="q-mt-md text-desc">
              {{ lorem }}
            </div>
          </div>

          <!-- PANEL DE LOGIN -->
          <div class="column flex-center q-pa-xl right-panel">

            <div class="text-center q-mb-md">
              <div class="text-h5 text-primary text-bold">Inicio de Sesión</div>
              <div class="text-grey-7 text-subtitle2">Acceso al Sistema</div>
            </div>

            <!-- Imagen -->
            <div class="q-mb-lg img">
              <q-img :src="url" spinner-color="primary" spinner-size="42px" />
            </div>

            <!-- FORMULARIO -->
            <q-form @submit="submitLogin" class="q-gutter-md form">

              <div class="form__container">

                <!-- Usuario -->
                <q-input v-model="form.username" outlined  dense label="Usuario"
                  :rules="[val => !!val || 'Ingrese su usuario']" class="custom-input">
                  <template v-slot:prepend>
                    <q-icon name="person"  class="icons"/>
                  </template>
                </q-input>

                <!-- Contraseña -->
                <q-input v-model="form.password" outlined  dense label="Contraseña" :type="isPwd ? 'password' : 'text'"
                  :rules="[val => !!val || 'Ingrese su contraseña']" class="custom-input">

                  <template v-slot:prepend>
                    <q-icon name="lock"  class="icons"/>
                  </template>

                  <template v-if="form.password" v-slot:append>
                    <q-icon :name="isPwd ? 'visibility_off' : 'visibility'" class="cursor-pointer eye"
                      @click="isPwd = !isPwd" />
                  </template>

                </q-input>

                <!-- BOTÓN -->
                <q-btn type="submit" label="Iniciar Sesión" class="btn-login full-width q-mt-md" rounded unelevated
                  :loading="isLoading">

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
import { ref, reactive } from "vue";

const dialog = ref(true);
const isLoading = ref(false);
const errorMsg = ref("");
const isPwd = ref(true);

const lorem = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit...'
const url = ref("https://www.freeiconspng.com/thumbs/login-icon/user-login-icon-29.png")

const form = reactive({
  username: "",
  password: ""
});

// SOLO VISUAL
const submitLogin = () => {
  isLoading.value = true;

  setTimeout(() => {
    isLoading.value = false;
    errorMsg.value = "Solo es una demo visual";
  }, 1500);
};
</script>

<style scoped>  
/* FONDO GENERAL */
.bg-grey-2 {
  background: #f3f6f4 !important;
}

/* CARD PRINCIPAL */
.card {
  width: 850px;
  max-width: 92vw;
  height: 520px;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

/* PANEL IZQUIERDO (VERDE) */
.left-panel {
  width: 42%;
  background: linear-gradient(135deg, #1f7a4f, #0f5f3c);
  position: relative;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* CURVA ESTILO DISEÑO IMAGEN */
.left-panel::after {
  content: "";
  position: absolute;
  top: 0;
  right: -80px;
  width: 160px;
  height: 100%;
  background: #1f7a4f;
  border-radius: 100px;
  z-index: 1;
}

/* TEXTO IZQUIERDO */
.text-desc {
  opacity: 0.9;
  font-size: 14px;
  line-height: 1.6;
  margin-top: 10px;
}

/* PANEL DERECHO */
.right-panel {
  width: 58%;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 50px 40px;
  position: relative;
}

/* TITULO LOGIN */
.right-panel .text-h5 {
  color: #1f7a4f !important;
}

/* IMAGEN */
.img {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #1f7a4f;
  margin: 0 auto 20px auto;
}

/* FORM */
.form {
  width: 100%;
  max-width: 320px;
  margin: 0 auto;
}



/* ICONOS */
.icons {
  color: #1f7a4f;
  font-size: 22px;
}

/* OJO PASSWORD */
.eye {
  color: #1f7a4f;
}

/* BOTÓN LOGIN */
.btn-login {
  height: 45px;
  border-radius: 30px !important;
  background: linear-gradient(135deg, #1f7a4f, #0f5f3c) !important;
  color: white;
  font-weight: bold;
  transition: 0.3s;
}



/* TEXTO ERROR */
.text-negative {
  font-weight: bold;
  text-align: center;
}

/* EFECTO SUAVE */
.card,
.left-panel,
.right-panel {
  transition: all 0.3s ease;
}
.custom-input :deep(.q-field__control) {
  background: #ffffff !important;   /* fondo blanco real */
  border: 1px solid #e6e6e6 !important;
  border-radius: 17px !important;
  box-shadow: none !important;
}

/* quita TODAS las capas internas de Quasar */
.custom-input :deep(.q-field__control::before),
.custom-input :deep(.q-field__control::after) {
  background: transparent !important;
  border: none !important;
  display: none !important;
}

/* cuando está activo (click) */
.custom-input :deep(.q-field--focused .q-field__control) {
  background: #ffffff !important;
  border: 1px solid #1f7a4f !important;
  box-shadow: none !important;
}

/* elimina overlay azul / efectos de focus */
.custom-input :deep(.q-focus-helper),
.custom-input :deep(.q-ripple) {
  display: none !important;
}

/* texto limpio */
.custom-input :deep(.q-field__native) {
  color: #333 !important;
}

</style>
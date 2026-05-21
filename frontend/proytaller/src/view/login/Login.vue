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
import { ref, reactive } from "vue"
import "../../assets/styles/login/login.css"
import { useRouter } from "vue-router"
import { login } from '../../api/login/login'
import { useAuthStore } from "../../store/store"
//router
const router = useRouter()
//store
const authStore = useAuthStore()


const dialog = ref(true);
const isLoading = ref(false);
const errorMsg = ref("");
const isPwd = ref(true);

const lorem = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit...'
const url = ref("https://www.freeiconspng.com/thumbs/login-icon/user-login-icon-29.png")

//formulario
const form = reactive({
  username: "",
  password: ""
});

// login 
const submitLogin = async () => {
  errorMsg.value = "";
  isLoading.value = true;
  try {
    //peticion al backend
    const respuesta = await login({
      username: form.username,
      password: form.password
    });
    console.log(respuesta)
    //guardar token en pinia

    authStore.login(
      respuesta.token, 
    {
      username: respuesta.username,
      nombreCompleto: respuesta.nombreCompleto
    });

    //redireccionar al home
    router.push("/");
  } catch (error) {
    errorMsg.value = error.respuesta?.data?.message ||"Usuario o contraseña incorrectos";
  
  } finally {
    isLoading.value = false;
  }
};
</script>


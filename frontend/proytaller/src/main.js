import { createApp } from 'vue'
import App from './App.vue'
import '../src/assets/styles/style.css'

import { createPinia  } from 'pinia'

import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
//quasar 
import { Quasar } from 'quasar'
import quasarSettings  from '../quasar.settings'
import 'quasar/dist/quasar.css'
import '@quasar/extras/material-icons/material-icons.css'
//reset time
import { initSessionManager } from './util/sessionManager'

//router
import router from './router'
import { useAuthStore } from './store/store'

const app = createApp(App)

//configuración de vue router
app.use(router)

//configuración de quasar
app.use(Quasar, quasarSettings)


//configuración de pinia
app.use(pinia)

const aplicarPermiso = (el, binding) => {
  const authStore = useAuthStore()
  const codigos = Array.isArray(binding.value) ? binding.value : [binding.value]
  el.style.display = codigos.some(codigo => authStore.tienePermiso(codigo)) ? '' : 'none'
}

app.directive('permiso', {
  mounted: aplicarPermiso,
  updated: aplicarPermiso
})

//inicializamos rl grstor de session por inactividad
initSessionManager(router)
app.mount('#app')

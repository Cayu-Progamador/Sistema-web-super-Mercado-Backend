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

const app = createApp(App)

//configuración de vue router
app.use(router)

//configuración de quasar
app.use(Quasar, quasarSettings)

//configuración de pinia
app.use(pinia)

//inicializamos rl grstor de session por inactividad
initSessionManager(router)
app.mount('#app')


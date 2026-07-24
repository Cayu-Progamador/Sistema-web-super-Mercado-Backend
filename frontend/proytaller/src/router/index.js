import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/store'
import { tieneAccesoAsistencia } from '../api/asistencia/asistencia'

import Layout from '../layouts/index.vue'
import Login from '../pages/login/Login.vue'
import Home from "../layouts/modules/MainContent.vue";
import ChangePassword from '../components/auth/nuevoPassword/ChangePassword.vue'
import UsuariosPage from '../pages/usuario/UsuarioPage.vue'
import Perfil from '../pages/perfil/Perfil.vue'
import Roles from '../pages/rol/Roles.vue'
import Empleado from '../pages/empleado/EmpleadoPage.vue'
import Cargos from '../pages/cargo/CargoPage.vue'
import Contrato from '../pages/contrato/ContratoPage.vue'
import MisAsistencia from '../pages/asistencia/MisAsistencia.vue'
import AsistenciaAdmin from '../pages/asistencia/AsistenciaAdmin.vue'
const routes = [
  {
    path: '/login',
    name: 'login',
    component: Login
  },

  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        name: 'home',
        component: Home
      },
      
      {
      path: 'change',
      name : 'change',
      component: ChangePassword
      },
      
      {
        path: 'usuarios', 
        name: 'usuarios',
        component: UsuariosPage
      },
      
      {
        path: 'perfil', 
        name: 'perfil',
        component: Perfil
      },
      {
        path: 'roles', 
        name: 'roles',
        component: Roles
      },
      {
        path: 'empleado', 
        name: 'empleado',
        component: Empleado
      },
      {
        path: 'cargos',
        name: 'cargos',
        component: Cargos
      },
      {
        path: 'contratos',
        name: 'contratos',
        component: Contrato
      },
      {
        path: 'asistencia',
        name: 'asistencia',
        component: MisAsistencia
      },
      {
        path: 'asistencia/admin',
        name: 'asistencia-admin',
        component: AsistenciaAdmin
      }

    ]
  },

  {
    path: '/:pathMatch(.*)*', redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const store = useAuthStore();

  const token = store.token || JSON.parse(localStorage.getItem('auth'))?.token

  if (token && to.name === 'login') {
    return next({ name: 'home' })
  }

  if (!token && to.name !== 'login') {
    return next({ name: 'login' })
  }

  if (token && (to.name === 'asistencia' || to.name === 'asistencia-admin')) {
    try {
      const res = await tieneAccesoAsistencia()
      if (to.name === 'asistencia-admin' && !res.tieneAcceso) {
        return next({ name: 'home' })
      }
      if (to.name === 'asistencia' && !res.tieneAcceso) {
        return next({ name: 'home' })
      }
    } catch {
      return next({ name: 'home' })
    }
  }

  next()
})


export default router

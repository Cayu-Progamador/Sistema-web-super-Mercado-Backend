import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/store'

import Layout from '../layouts/index.vue'
import Login from '../pages/login/Login.vue'
import Home from "../layouts/modules/MainContent.vue";
import ChangePassword from '../components/auth/nuevoPassword/ChangePassword.vue'
import UsuariosPage from '../pages/usuario/UsuarioPage.vue'
import Perfil from '../pages/perfil/Perfil.vue'
import Roles from '../pages/rol/Roles.vue'
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

router.beforeEach((to, from, next) => {
  const store = useAuthStore();

// Leer token de Pinia o de localStorage directamente
  const token = store.token || JSON.parse(localStorage.getItem('auth'))?.token

  if (token && to.name === 'login') {
    // Usuario logueado → redirige al home
    return next({ name: 'home' })
  }

  if (!token && to.name !== 'login') {
    // Usuario no logueado → redirige al login
    return next({ name: 'login' })
  }

  next()
})


export default router

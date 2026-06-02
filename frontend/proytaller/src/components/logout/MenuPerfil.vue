<template>
    <q-menu :offset="[0, 12]" class="custom-user-menu-dropdown">
      <div class="user-info-container">

        <!-- Info Usuario -->
        <q-item class="info-section q-py-md q-px-md">
          <q-item-section>
            <div class="row items-center no-wrap q-mb-xs">
              <q-icon name="person" size="1.5rem" class="user-icon q-mr-sm" />
              <q-item-label class="user-name text-weight-bold">
                {{ userInfo.nombreEmpleado }}
              </q-item-label>
            </div>

            <q-item-label class="user-username">
              Usuario:
              <span class="text-role">{{ userInfo.username }}</span>
            </q-item-label>

            <q-item-label class="user-username">
              Rol:
              <span
                v-for="rol in userInfo.roles"
                :key="rol"
                class="text-role"
              >
                {{ rol.replace('ROLE_', '') }}
              </span>
            </q-item-label>
          </q-item-section>
        </q-item>

        <q-separator class="menu-separator" />

        <!-- Opciones -->
        <q-list style="min-width: 260px;">

          <q-item clickable v-ripple class="menu-item-perfil" @click="goPerfil()">
            <q-item-section avatar>
              <q-icon name="person" size="1.2rem" class="menu-icon-perfil" />
            </q-item-section>
            <q-item-section>
              <q-item-label class="menu-label-perfil">Perfil</q-item-label>
            </q-item-section>
          </q-item>

          <q-item clickable v-ripple class="menu-item-password" @click="cambiarPassword()">
            <q-item-section avatar>
              <q-icon name="lock" size="1.2rem" class="menu-icon-password" />
            </q-item-section>
            <q-item-section>
              <q-item-label class="menu-label-password">Cambiar Contraseña</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator class="menu-separator" />

          <q-item clickable v-ripple class="menu-item-logout" @click="logout()">
            <q-item-section avatar>
              <q-icon name="logout" size="1.2rem" class="menu-icon-logout" />
            </q-item-section>
            <q-item-section>
              <q-item-label class="menu-label-logout">Salir</q-item-label>
            </q-item-section>
          </q-item>

        </q-list>
      </div>
    </q-menu>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../store/store'
import { onMounted, ref } from 'vue'
import { getUserList } from '../../api/usuario/usuario'

const userInfo = ref({
  username: '',
  nombreEmpleado: '',
  apellidoEmpleado: '',
  roles: []
})

const router = useRouter()
const store = useAuthStore()

async function fetchUserInfo() {
  
  try {
    const response = await getUserList()
    userInfo.value = response
  } catch (error) {
    console.error('No se puede obtener la info del usuario')
  }
}

onMounted(() => {
    fetchUserInfo()
})

const logout = () => {
  store.logout()
  router.push('/login')
}

const goPerfil = () => {
  router.push('/perfil')
}

const cambiarPassword = () => {
  router.push('/change')
}
</script>

<style  src="../../assets/styles/perfil/menuPerfil.css"></style>

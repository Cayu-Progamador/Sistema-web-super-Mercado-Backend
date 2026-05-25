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
    router.push('/login')
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

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800&display=swap');

/* ── Dropdown container ── */
.custom-user-menu-dropdown {
  border-radius: 14px !important;
  border: 1px solid #e4edd8 !important;
  background: #ffffff !important;
  box-shadow: 0 8px 32px rgba(74, 140, 37, 0.12) !important;
  overflow: hidden !important;
  font-family: 'Nunito', sans-serif !important;
}

/* ── Sección info usuario ── */
.info-section {
  background: #f7f9f4 !important;
}

/* ── Ícono de usuario ── */
.user-icon {
  color: #4a8c25 !important;
}

/* ── Nombre del usuario ── */
.user-name {
  font-family: 'Nunito', sans-serif !important;
  font-size: 15px !important;
  font-weight: 800 !important;
  color: #2a5c1a !important;
}

/* ── Labels secundarios ── */
.user-username {
  font-family: 'Nunito', sans-serif !important;
  font-size: 12px !important;
  font-weight: 600 !important;
  color: #9dbf78 !important;
  margin-top: 2px !important;
}

/* ── Valor del rol / username ── */
.text-role {
  font-family: 'Nunito', sans-serif !important;
  font-weight: 700 !important;
  color: #4a8c25 !important;
  background: #eaf4d8 !important;
  border-radius: 5px !important;
  padding: 1px 6px !important;
  font-size: 11.5px !important;
  margin-left: 3px !important;
}

/* ── Separadores ── */
.menu-separator {
  background: #e4edd8 !important;
}

/* ── Item: Perfil ── */
.menu-item-perfil {
  border-radius: 8px !important;
  margin: 3px 6px !important;
  transition: background 0.15s !important;
}
.menu-item-perfil:hover {
  background: #f0f7e8 !important;
}

.menu-icon-perfil {
  color: #0f6e56 !important;
}

.menu-label-perfil {
  font-family: 'Nunito', sans-serif !important;
  font-size: 13.5px !important;
  font-weight: 600 !important;
  color: #2a5c1a !important;
}

/* ── Item: Cambiar Contraseña ── */
.menu-item-password {
  border-radius: 8px !important;
  margin: 3px 6px !important;
  transition: background 0.15s !important;
}
.menu-item-password:hover {
  background: #f0f7e8 !important;
}

.menu-icon-password {
  color: #d97b1a !important;
}

.menu-label-password {
  font-family: 'Nunito', sans-serif !important;
  font-size: 13.5px !important;
  font-weight: 600 !important;
  color: #2a5c1a !important;
}

/* ── Item: Salir ── */
.menu-item-logout {
  border-radius: 8px !important;
  margin: 3px 6px !important;
  transition: background 0.15s !important;
}
.menu-item-logout:hover {
  background: #fff4f4 !important;
}

.menu-icon-logout {
  color: #b91c1c !important;
}

.menu-label-logout {
  font-family: 'Nunito', sans-serif !important;
  font-size: 13.5px !important;
  font-weight: 600 !important;
  color: #b91c1c !important;
}
</style>
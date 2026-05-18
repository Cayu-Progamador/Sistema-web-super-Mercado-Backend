<template>
  <q-btn flat class="avatar-button" aria-label="Menú de Usuario" no-ripple>
    <!-- Avatar -->
    <q-avatar size="60px" class="avatar-style">
      <img src="https://cdn.quasar.dev/img/boy-avatar.png" alt="Avatar Usuario">
    </q-avatar>

    <!-- Menú -->
    <q-menu :offset="[0, 12]" class="custom-user-menu-dropdown">
      <div class="user-info-container">

        <!-- Info Usuario -->
        <q-item class="info-section q-py-md q-px-md">
          <q-item-section>
            <div class="row items-center no-wrap q-mb-xs">
              <q-icon name="person" size="1.5rem" class="user-icon q-mr-sm" />
              <q-item-label class="user-name text-weight-bold">
                {{ userInfo.nombreCompleto }}
              </q-item-label>
            </div>
            <q-item-label class="user-role">
              {{ userInfo.username }}
            </q-item-label>
          </q-item-section>
        </q-item>

        <q-separator color="#4F5C6A" />

        <!-- Opciones -->
        <q-list style="min-width: 260px;">

          <q-item clickable v-ripple class="menu-item-perfil" @click="goPerfil()">
            <q-item-section avatar>
              <q-icon name="person" size="1.2rem" class="menu-icon-perfil user-icon " />
            </q-item-section>
            <q-item-section>
              <q-item-label class="menu-label-perfil">Perfil</q-item-label>
            </q-item-section>
          </q-item>

          <q-item clickable v-ripple class="menu-item-password" @click="cambiarPassword()">
            <q-item-section avatar>
              <q-icon name="lock" size="1.2rem" class="menu-icon-password user-icon" />
            </q-item-section>
            <q-item-section>
              <q-item-label class="menu-label-password">Cambiar Contraseña</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator color="#4F5C6A" />

          <q-item clickable v-ripple class="menu-item" @click="logout()">
            <q-item-section avatar>
              <q-icon name="logout" size="1.2rem" class="menu-icon logout" />
            </q-item-section>
            <q-item-section>
              <q-item-label class="menu-label logout">Salir</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </div>
    </q-menu>
  </q-btn>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/store'
import { onMounted, ref } from 'vue'
import { getUserList } from '../api/usuario/usuario'

const userInfo = ref({ nombreCompleto: '', username: '' })
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
  console.log("me diste un click",router.push('/change'))
}

</script>

<style>
/* --- Avatar --- */
.avatar-style {
  border: 2px solid #00A99D;
  cursor: pointer;
  transition: transform 0.2s;
}

.avatar-style:hover {
  transform: scale(1.05);
}

/* --- Menú --- */
.custom-user-menu-dropdown {
  border-radius: 12px;
  background-color: #e4e4e4;
  box-shadow: 0 4px 15px rgba(235, 229, 229, 0.3);
  padding: 0;
}

/* --- Info Usuario --- */
.user-info-container {
  background-color: #2F3E4F;
  color: #FFFFFF;
}

.user-icon {
  color: #00A99D;
}

.user-name {
  font-size: 0.9rem;
  color: #FFFFFF;
}

.user-role {
  font-size: 0.8rem;
  color: #f6f8fa;
  padding-left: 36px;
}

/* --- Opciones --- */
.menu-item {
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:hover {
  background-color: #384A5C;
}

.menu-icon {
  color: #D9534F;
}

.menu-label {
  color: #D9534F;
  font-weight: 600;
}

.logout {
  color: #FF5C5C !important;
}

.q-separator {
  margin: 4px 0;
}
</style>

<template>
  <div class="q-pa-md q-gutter-sm avatar-button">
    <q-avatar size="50px" class="overlapping avatar-style">
      <img v-if="fotoUrl" :src="fotoUrl" />
      <span v-else>{{ iniciales }}</span>
    </q-avatar>
    <MenuPerfil/>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../../store/store'
import MenuPerfil from '../logout/MenuPerfil.vue'
import '../../assets/styles/perfil/avatar.css'

const authStore = useAuthStore()
const fotoUrl = computed(() => authStore.userInfo?.fotoUrl || '')
const iniciales = computed(() => {
    const nombre = authStore.userInfo?.nombreCompleto || ''
    return nombre
        .split(' ')
        .slice(0, 2)
        .map(n => n[0])
        .join('')
        .toUpperCase() || 'U'
})
</script>

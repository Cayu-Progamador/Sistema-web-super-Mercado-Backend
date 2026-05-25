<template>
  <div class="q-pa-md q-gutter-sm avatar-button">
    <q-avatar
      v-for="n in 1"
      :key="n"
      size="50px"
      class="overlapping avatar-style"
    >
      <img :src="`https://cdn.quasar.dev/img/avatar${1 }.jpg`" />
    </q-avatar>
    <MenuPerfil/>
  </div>
    


</template>

<script setup>
import MenuPerfil from '../logout/MenuPerfil.vue'
import '../../assets/styles/perfil/avatar.css'
</script>


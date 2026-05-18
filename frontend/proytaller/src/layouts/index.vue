<template>
    <q-layout view="lHh Lpr lff" class="shadow-2 rounded-borders">
        <q-header 
            class="header"
        >
            <MyHeader 
               @toggle-drawer="toggleLeftDrawer" 
            />
        </q-header>

        <q-drawer 
            v-model="leftDrawerOpen" 
            show-if-above 
            bordered
            class="drawer" 
            :mini="miniState"
            @mouseenter="miniState = false" 
            @mouseleave="miniState = true"
            :width="drawerWidth" 
            :breakpoint="500"
        >
            <MySidebar />
        </q-drawer>

        <q-page-container class="full-height">
            <router-view />
        </q-page-container>
        <!-- <MainContent>
            </MainContent> -->

    </q-layout>
</template>

<script setup>
import { ref } from 'vue'
//import MainContent from "./modules/MainContent.vue";   // Was Main
import MyHeader from "./modules/Header.vue";    // Was Header
import MySidebar from "./modules/Aside.vue";    // Was Aside

const leftDrawerOpen = ref(false)
const drawerWidth = ref(200)

const miniState = ref(false)

function toggleLeftDrawer() {
    leftDrawerOpen.value = !leftDrawerOpen.value
}

</script>
<style>
.full-height {
  min-height: 100vh; /* asegura que ocupe toda la altura de la ventana */
}
.drawer {
    background-color: #d6d9ff;
}
.header {
    background-color: #3c78ff;
}
</style>
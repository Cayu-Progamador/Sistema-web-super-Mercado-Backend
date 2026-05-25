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
            <MySidebar :is-mini="miniState" />
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
import '../assets/styles/layout/layouts.css'
const leftDrawerOpen = ref(false)
const drawerWidth = ref(200)

const miniState = ref(false)

function toggleLeftDrawer() {
    leftDrawerOpen.value = !leftDrawerOpen.value
}

</script>

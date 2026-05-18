<template>
  <q-scroll-area class="fit" :horizontal-thumb-style="{ opacity: 0 }">
    <q-list padding>
      <q-item clickable v-ripple to="/">
        <q-item-section avatar>
          <q-icon name="dashboard" />
        </q-item-section>
        <q-item-section>Dashboard</q-item-section>
      </q-item>
    </q-list>
  </q-scroll-area>

</template>

<script setup>

</script>

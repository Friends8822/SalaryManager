<template>
  <div class="app-shell">
    <router-view />
    <ToastMessage />
  </div>
</template>

<script setup>
import ToastMessage from './components/ToastMessage.vue'
</script>
